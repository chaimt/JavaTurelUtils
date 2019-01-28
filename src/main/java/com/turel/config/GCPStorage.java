package com.turel.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GCPStorage {
    public static final List<String> STORAGE_SCOPES = Arrays.asList(StorageScopes.DEVSTORAGE_FULL_CONTROL);
    private static final Logger LOG = LoggerFactory.getLogger(GCPStorage.class);
    private AppCredential appCredential;
    private Storage storage = null;

    public GCPStorage(AppCredential appCredential) {
        this.appCredential = appCredential;

    }

    public Storage getStorage() {
        if (storage == null) {
            try {
                GoogleCredential credential = appCredential.getCredential(STORAGE_SCOPES);
                storage = new Storage(AppCredential.TRANSPORT, AppCredential.JSON_FACTORY, credential);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return storage;
    }

    @Bean
    public com.google.cloud.storage.Storage getCloudStorage() {
        return StorageOptions.newBuilder().setCredentials(getCredentials()).build().getService();
    }

    @Bean
    public GoogleCredentials getCredentials() {
        GoogleCredentials credentials;
        try {
            File file = new File(getClass().getClassLoader().getResource("BQ-migration-21ff44f07194.json").getFile());
            credentials = ServiceAccountCredentials.fromStream(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return credentials;
    }


    public void downloadFile(String bucket, String object, File outputFile) {
        try {
            final File parentFile = outputFile.getParentFile();
            if (!parentFile.exists()) {
                outputFile.getParentFile().mkdirs();
            }
            FileOutputStream out = new FileOutputStream(outputFile);
            final Storage.Objects.Get get = getStorage().objects().get(bucket, object);
            get.executeMediaAndDownloadTo(out);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void metaData(String bucket, String object) {
        try {
            final Storage.Objects.Get get = storage.objects().get(bucket, object);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
