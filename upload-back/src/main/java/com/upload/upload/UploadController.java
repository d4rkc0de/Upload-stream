package com.upload.upload;

import com.microsoft.azure.datalake.store.ADLException;
import org.apache.commons.fileupload.util.Streams;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class UploadController {

    @Autowired
    Environment env;

    @Autowired
    AzureDataLakeStoreService azureDataLakeStoreService;
    private static Logger log = LoggerFactory.getLogger(UploadController.class);

    @PostMapping("/upload")
    public ResponseEntity handleUpload(HttpServletRequest request) throws Exception {
        log.info("UploadController started !");
        try {
            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iterStream = upload.getItemIterator(request);
            while (iterStream.hasNext()) {
                FileItemStream item = iterStream.next();
                String name = item.getName();
                InputStream stream = item.openStream();
                if (!item.isFormField()) {
                    log.info("file is being streamed");
                    copyStreamToAzure(stream, getUploadPath() + "/" + name);
                    //convertInputStreamToFileNio(stream, name);
                } else {
                    String formFieldValue = Streams.asString(stream);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            log.error("failed!", e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Fichier téléchargé avec succès.", HttpStatus.OK);
    }

    String getUploadPath() {
        String uploadArbo = env.getProperty("uploadTest");
        return uploadArbo;
    }

    boolean copyStreamToAzure(InputStream stream, String fileName) {
        String authTokenEndpoint = env.getProperty("authTokenEndpoint");
        String clientId = env.getProperty("clientId");
        String clientKey = env.getProperty("clientKey");
        String accountFQDN = env.getProperty("accountFQDN");
        try {
            azureDataLakeStoreService.init(authTokenEndpoint, clientId, clientKey, accountFQDN);
        } catch (ADLException e) {
            System.out.println(e.getMessage());
        }
        boolean isFileCreated = azureDataLakeStoreService.createFileFromStream(stream, fileName);
        return isFileCreated;
    }

    private void convertInputStreamToFileNio(InputStream is, String fileName) throws IOException {
        String outputFile = "C:\\Pwc\\workspace\\Upload\\" + fileName;
        Files.copy(is, Paths.get(outputFile));
        File file = new File(outputFile);
    }

}
