package com.explorer.backend.service;

import com.explorer.backend.dto.FileResponse;
import com.explorer.backend.dto.FolderResponse;
import com.explorer.backend.dto.ObjectListResponse;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;

    public  S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public List<String> listBuckets() {
        return s3Client.listBuckets()   //give me all the buckets
                .buckets()              //extract from the response
                .stream()               //process each item in the collection.
                .map(bucket -> bucket.name())    //LAMBDA is used here to transforms each Bucket object into its name.
                .toList();              //convert Stream back into a List:
    }

    public ObjectListResponse listObjects(String bucket, String prefix, Integer maxKeys, String continuationToken) {
        //Used Builder here coz here the vars maxKeys and tokena re optional
        ListObjectsV2Request.Builder requestBuilder = ListObjectsV2Request.builder()
                .bucket(bucket)
                .prefix(prefix)
                .delimiter("/");


        if(maxKeys !=null) {
            requestBuilder.maxKeys(maxKeys);
        }

        if(continuationToken != null && !continuationToken.isBlank()) {
            requestBuilder.continuationToken(continuationToken);
        }

        ListObjectsV2Request request = requestBuilder.build();

        ListObjectsV2Response response = s3Client.listObjectsV2(request);

        List<FolderResponse> folders = response.commonPrefixes()
                .stream()
                .map(commonPrefix -> new FolderResponse(
                        getFolderName(commonPrefix.prefix()),
                        commonPrefix.prefix()
                ))
                .toList();

        List<FileResponse> files = response.contents()
                .stream()
                .map(s3Object -> new FileResponse(
                        getFileName(s3Object.key()),
                        s3Object.key(),
                        s3Object.size(),
                        s3Object.lastModified()
                ))
                .toList();

        return new ObjectListResponse(bucket, prefix, folders, files, response.isTruncated(), response.nextContinuationToken()
        );
    }

    private String getFolderName(String path) {
        String normalizedPath = path.endsWith("/")
                ? path.substring(0, path.length() - 1)
                : path;

        int lastSlashIndex = normalizedPath.lastIndexOf("/");

        return lastSlashIndex >=0
                ? normalizedPath.substring(lastSlashIndex+1)
                : normalizedPath;
    }

    private String getFileName(String key) {
        int lastSlashIndex = key.lastIndexOf("/");

        return lastSlashIndex >=0
                ? key.substring(lastSlashIndex+1)
                : key;
    }
}
