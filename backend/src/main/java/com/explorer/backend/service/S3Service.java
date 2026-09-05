package com.explorer.backend.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

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
}
