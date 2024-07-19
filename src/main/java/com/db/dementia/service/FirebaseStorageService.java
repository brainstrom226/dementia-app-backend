package com.db.dementia.service;

import com.db.dementia.dto.UploadResponse;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@DependsOn("firebaseApp")
public class FirebaseStorageService {

    private final Storage storage;
    private final FirebaseService firebaseService;
    private static final String BUCKET_NAME = "dementia-app-f7333.appspot.com";
    @Autowired

    public FirebaseStorageService(final FirebaseService firebaseService) throws IOException {
        this.firebaseService = firebaseService;
        final ClassPathResource resource =
                new ClassPathResource("private-key.json", this.getClass().getClassLoader());
        final Credentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
        storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public UploadResponse uploadFile(@NonNull final MultipartFile file, @NonNull final String user)
            throws IOException, ExecutionException, InterruptedException {
        final String objectName = Objects.requireNonNull(file.getOriginalFilename());
        final String fileName =  String.format(
                FilenameUtils.getBaseName(objectName)+"-%s.%s",
                UUID.randomUUID(),
                FilenameUtils.getExtension(objectName));
        System.out.println("File Name : " + fileName);

        final BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        final Blob blob = storage.create(blobInfo, file.getBytes());

        final URL downloadURL = blob.signUrl(1, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());
        System.out.println("Download URl : " + downloadURL);

        return new UploadResponse(user,
                firebaseService.saveUploadResponse(String.format(DatabaseContextPath.GALLERY_USER_NODE,user),fileName));
    }

    public byte[] downloadFile(String fileName) {
        Blob blob = storage.get(BlobId.of(BUCKET_NAME, fileName));
        return blob.getContent();
    }

    public void deleteAllFiles() {
        Bucket bucket = storage.get(BUCKET_NAME);
        bucket.list().iterateAll().forEach(blob -> storage.delete(blob.getBlobId()));
    }
}
