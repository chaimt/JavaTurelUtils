package com.turel.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.List;

/**
 * Created by Chaim on 23/02/2017.
 */
@RequiredArgsConstructor
@Getter
public class AppCredential {
    public static final HttpTransport TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    final private String p12File;
    final private String serviceAccountId;
    private InputStream resourceAsStream;

    public GoogleCredential getCredential(List<String> scopes) throws GeneralSecurityException, IOException {
        resourceAsStream = AppCredential.class.getClassLoader().getResourceAsStream(p12File);
        if (resourceAsStream==null)
            throw new RuntimeException("P12File missing: " + p12File);
        PrivateKey privateKey = SecurityUtils.loadPrivateKeyFromKeyStore(
                SecurityUtils.getPkcs12KeyStore(), resourceAsStream,
                "notasecret",
                "privatekey", "notasecret");

        GoogleCredential credential = new GoogleCredential.Builder().setTransport(TRANSPORT)
                .setJsonFactory(JSON_FACTORY)
                .setServiceAccountId(serviceAccountId)
                .setServiceAccountScopes(scopes)
                .setServiceAccountPrivateKey(privateKey)
                .build();
        return credential;
    }



}
