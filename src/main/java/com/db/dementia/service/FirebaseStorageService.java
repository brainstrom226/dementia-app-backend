package com.db.dementia.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@DependsOn("firebaseApp")
public class FirebaseStorageService {

    private final Storage storage;

    public FirebaseStorageService() throws IOException {
        final ClassPathResource resource =
                new ClassPathResource("private-key.json", this.getClass().getClassLoader());
        final Credentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
        storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

    public String uploadFile(@NonNull final MultipartFile file) throws IOException {
        final String bucketName = "dementia-app-f7333.appspot.com";
        final String objectName = Objects.requireNonNull(file.getOriginalFilename());
        final String fileName =  String.format(
                FilenameUtils.getBaseName(objectName)+"-%s.%s",
                UUID.randomUUID(),
                FilenameUtils.getExtension(objectName));
        System.out.println("File Name : " + fileName);

        final BlobId blobId = BlobId.of(bucketName, fileName);
        final BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(file.getContentType()).build();

        final Blob blob = storage.create(blobInfo, file.getBytes());

        final URL downloadURL = blob.signUrl(1, TimeUnit.MINUTES, Storage.SignUrlOption.withV4Signature());

        return downloadURL.toString();
    }

    public byte[] downloadFile(String fileName) {
        final String bucketName = "dementia-app-f7333.appspot.com";
        Blob blob = storage.get(BlobId.of(bucketName, fileName));
        return blob.getContent();
    }
}
