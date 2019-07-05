package com.upload.upload;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.azure.datalake.store.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AzureDataLakeStoreService {

    private static Logger log = LoggerFactory.getLogger(AzureDataLakeStoreService.class);

    private ADLStoreClient client;


    public void init(String authTokenEndpoint, String clientId, String clientKey, String accountFQDN) throws ADLException {
        if (client == null) {
            // Create client object using client creds
            if (authTokenEndpoint == null || clientId == null || clientKey == null || accountFQDN == null)
                log.error("Credantial to connect to Azure Data Lake Store missing please check azureConfig.properties");

            AccessTokenProvider provider = new ClientCredsTokenProvider(authTokenEndpoint, clientId, clientKey);
            client = ADLStoreClient.createClient(accountFQDN, provider);
        }
    }

    public boolean createFileFromStream(InputStream stream, String filename) {
        try (OutputStream out = client.createFile(filename, IfExists.OVERWRITE)) {
            client.setPermission(filename, "777");
            IOUtils.copyLarge(stream, out);
            stream.close();
        } catch (IOException e) {
            log.error("Error while uploading to azure", e);
            return false;
        }
        return true;
    }

}

