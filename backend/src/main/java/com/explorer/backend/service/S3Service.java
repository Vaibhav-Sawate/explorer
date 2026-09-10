package com.explorer.backend.service;

import com.explorer.backend.dto.FileResponse;
import com.explorer.backend.dto.FolderResponse;
import com.explorer.backend.dto.ObjectListResponse;
import com.explorer.backend.dto.ObjectMetadataResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class S3Service {

    private final S3Client s3Client;
    private static final long MAX_PREVIEW_SIZE =1024*1024;
    private static final Set<String> PREVIEWABLE_EXTENSIONS= Set.of(
            "txt",
            "log",
            "json",
            "csv",
            "xml"
    );

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

    public ObjectMetadataResponse getObjectMetadata(
            String bucket,
            String key
    ) {
        HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        HeadObjectResponse response = s3Client.headObject(request);

        return new ObjectMetadataResponse(
                key,
                response.contentLength(),
                response.lastModified(),
                response.contentType(),
                response.eTag(),
                response.metadata()
        );
    }

    public ResponseInputStream<GetObjectResponse> downloadObject(
            String bucket,
            String key
    ) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        return s3Client.getObject(request);
    }

    private String getFileExtension(String key) {
        int lastDotIndex = key.lastIndexOf('.');
        if(lastDotIndex == -1 || lastDotIndex == key.length() - 1) {
            return "";
        }
        return key.substring(lastDotIndex + 1);
    }

    //If size id too large -> no preview
    public String previewObject(String bucket, String key){

        String extension = getFileExtension(key);
        if(!PREVIEWABLE_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "File type is not supported for preview"
            );
        }
        HeadObjectRequest headRequest = HeadObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        HeadObjectResponse headResponse = s3Client.headObject(headRequest);

        if(headResponse.contentLength() > MAX_PREVIEW_SIZE) {
            throw new IllegalArgumentException(
                    "File is too large to preview"
            );
        }

        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        try(ResponseInputStream<GetObjectResponse> objectStream = s3Client.getObject(getRequest)) {
            return new String(
                    objectStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException exception) {
            throw new RuntimeException("Failed to preview object", exception);
        }
    }

    public List<FileResponse> searchObjects(String bucket, String query){
        List<FileResponse> files = new ArrayList<>();
        ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();
        ListObjectsV2Response response;
        String normalizedQuery = query.toLowerCase(Locale.ROOT);

        do {
            response = s3Client.listObjectsV2(request);
            files.addAll(
                    response.contents().stream()
                            .filter(object -> object.key()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(normalizedQuery)
                            )
                            .map(object -> new FileResponse(
                                    object.key().substring(
                                            object.key().lastIndexOf('/')+1
                                    ),
                                    object.key(),
                                    object.size(),
                                    object.lastModified()
                            ))
                            .toList()
            );

            request = ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .continuationToken(response.nextContinuationToken())
                    .build();
        } while (response.isTruncated());

        return files;
    }

    public void copyObject(String bucket, String sourceKey, String destinationKey){
        if(sourceKey.equals(destinationKey)) {
            throw new IllegalArgumentException("Sorce and destination keys must be different");
        }

        CopyObjectRequest request = CopyObjectRequest.builder()
                .copySource(bucket + "/"+ sourceKey)
                .destinationBucket(bucket)
                .key(destinationKey)
                .build();

        s3Client.copyObject(request);
    }

    public void deleteObject( String bucket, String key){
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

}
