package com.explorer.backend.controller;

import com.explorer.backend.dto.*;
import com.explorer.backend.service.S3Service;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("/buckets")
    public List<String> listBuckets() {
        return s3Service.listBuckets();
    }

    @GetMapping("/buckets/{bucket}/objects")
    public ObjectListResponse listObjects(
            @PathVariable String bucket,
            @RequestParam(defaultValue = "")
            String prefix,

            @RequestParam(required = false)
            @Min(value =1, message="maxKeys must be greater than 0")
            Integer maxKeys,

            @RequestParam(required = false)
            String continuationToken
    ) {
        return s3Service.listObjects(bucket, prefix,  maxKeys, continuationToken);
    }

    @GetMapping("/buckets/{bucket}/object")
    public ObjectMetadataResponse getObjectMetadata(
            @PathVariable String bucket,

            @RequestParam
            @NotBlank (message = "key must not be blank")
            String key
    ) {
        return s3Service.getObjectMetadata(bucket, key);
    }

    @GetMapping("/buckets/{bucket}/object/download")
    public ResponseEntity<InputStreamResource> downloadObject(
            @PathVariable String bucket,
            @RequestParam
            @NotBlank(message ="key must not be blank")
            String key
    ) {
        ResponseInputStream<GetObjectResponse> objectStream = s3Service.downloadObject(bucket, key);
        GetObjectResponse response = objectStream.response();
        String fileName = key.substring(key.lastIndexOf("/") + 1);

        return ResponseEntity.ok()
                .contentType(
                        response.contentType() !=null
                                ? MediaType.parseMediaType(response.contentType())
                                : MediaType.APPLICATION_OCTET_STREAM
                )
                .contentLength(response.contentLength())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                       // "attachment; filename=\""+ fileName+"\""
                        //For filenames with space
                        ContentDisposition.attachment()
                                .filename(fileName, StandardCharsets.UTF_8)
                                .build()
                                .toString()
                )
                .body(new InputStreamResource(objectStream));
    }

    @GetMapping(
            value = "/buckets/{bucket}/object/preview",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> previewObject(
            @PathVariable String bucket,
            @RequestParam
            @NotBlank(message="key must not blank")
            String key
    ) {
        String content = s3Service.previewObject(bucket, key);
        return ResponseEntity.ok(content);
    }

    @GetMapping("/buckets/{bucket}/objects/search")
    public ObjectSearchResponse searchObjects(
            @PathVariable String bucket,
            @RequestParam
            @NotBlank(message = "query must not be blank")
            String query
    ) {
        List<FileResponse> files = s3Service.searchObjects(bucket, query);
        return new ObjectSearchResponse(
                bucket,
                query,
                files
        );
    }

    @PostMapping("buckets/{bucket}/object/copy")
    public CopyObjectResponse copyObject(
            @PathVariable String bucket,
            @RequestParam @NotBlank String sourceKey,
            @RequestParam @NotBlank String destinationKey
    ) {
        s3Service.copyObject(bucket, sourceKey, destinationKey);

        return new CopyObjectResponse(
                bucket, sourceKey, destinationKey);
    }

    @DeleteMapping("buckets/{bucket}/object")
    public DeleteObjectResponse deleteObject(
            @PathVariable String bucket,
            @RequestParam @NotBlank String key
    ) {
        s3Service.deleteObject(bucket, key);
        return new DeleteObjectResponse(bucket, key);
    }


}
