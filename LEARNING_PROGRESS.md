## Learning Progress

### Local Development Setup

The project currently uses LocalStack to simulate AWS services locally.

Tools configured:

* Docker
* LocalStack
* AWS CLI
* IntelliJ IDEA

LocalStack is running on:

`http://localhost:4566`

### S3 Concepts Learned

#### Bucket and Object Key

Example:

`S3 URI: s3://cloud-data/support/customer-101/logs/error.log`

* Bucket: `cloud-data`
* Object Key: `support/customer-101/logs/error.log`

S3 does not have traditional folders. Folder-like structures are created using object key prefixes.

#### Prefix-Based Navigation

Cloud Explorer will provide a filesystem-like navigation experience using:

* `Prefix`
* `Delimiter`
* `Contents`
* `CommonPrefixes`

Example:

```text
📄 hello.txt
📁 reports
📁 support
```

The application will use the S3 `ListObjectsV2` API with:

```text
Prefix = current path
Delimiter = /
```

* `Contents` represents objects/files at the current navigation level.
* `CommonPrefixes` represents virtual folders.

#### Pagination

S3 object listings are paginated.

Important concepts:

* `MaxKeys`
* `IsTruncated`
* `NextContinuationToken`

Cloud Explorer will paginate results within the currently opened prefix rather than loading an entire bucket.

Example:

```text
Current Prefix
      ↓
ListObjectsV2
      ↓
Limited Results
      ↓
Continuation Token
      ↓
Load Next Page
```

### Current Progress

* [x] Docker configured
* [x] LocalStack configured
* [x] AWS CLI installed
* [x] Connected AWS CLI to LocalStack
* [x] Created local S3 bucket
* [x] Uploaded test objects
* [x] Learned S3 object keys and prefixes
* [x] Tested prefix-based navigation
* [x] Tested pagination
* [x] Spring Boot application setup
* [x] AWS SDK integration
* [x] Basic Explorer backend API
* [x] Add Copy Object API
* [x] Use AWS SDK `CopyObjectRequest`
* [x] Copy objects within the same bucket
* [x] Validate source and destination keys
* [x] Handle missing source objects
* [x] Verify copied object using metadata API
* [x] Add Delete Object API
* [x] Use AWS SDK `DeleteObjectRequest`
* [x] Delete objects from a bucket
* [x] Verify deletion using metadata API
* [x] Understand S3 delete idempotency
* [ ] Frontend implementation
* [ ] Authentication and authorization
* [ ] Deployment


### Spring Boot Fundamentals

#### Spring Boot Application Setup

Created the Cloud Explorer backend as a Spring Boot application.

Current backend stack:

* Java 25
* Spring Boot 4.1.1
* Maven
* Spring Web MVC
* Spring Boot Actuator

The backend application entry point is:
ExplorerApplication.java


### AWS SDK and S3 Integration

#### AWS SDK for Java v2

Added the AWS SDK for Java v2 to the Spring Boot backend.

Learned about Maven dependency management using the AWS SDK BOM.

The BOM manages compatible versions for AWS SDK modules.

Current AWS dependency: "software.amazon.awssdk:s3"



### Spring Boot S3 Integration

#### S3Client Configuration

Configured the AWS SDK `S3Client` as a Spring Bean.

LocalStack configuration includes:

* Custom endpoint configuration
* Static credentials for local development
* Configured AWS region
* Path-style S3 access

Path-style access was required because virtual-hosted-style addressing attempted to resolve bucket-specific hostnames such as:

`cloud-data.localhost`

Instead, LocalStack is accessed using:

`localhost:4566/bucket-name`

#### Bucket Listing

Implemented an endpoint for listing available buckets:

`GET /api/buckets`

#### Object Listing and Prefix Navigation

Implemented:

`GET /api/buckets/{bucket}/objects`

Supported query parameters:

* `prefix`
* `maxKeys`
* `continuationToken`

Object navigation uses the S3 `ListObjectsV2` API with:

* `Prefix`
* `Delimiter`
* `Contents`
* `CommonPrefixes`

API responses separate:

* Folders
* Files

File metadata currently includes:

* Name
* Object key
* Size
* Last modified timestamp

#### Pagination

Implemented S3 pagination using:

* `maxKeys`
* `continuationToken`
* `isTruncated`
* `nextContinuationToken`

Pagination was tested across multiple pages using LocalStack.

#### Validation and Error Handling

Added request validation for:

* `maxKeys` must be greater than `0`

Added global exception handling using:

* `@RestControllerAdvice`
* `ConstraintViolationException`

API errors now return a consistent structure containing:

* Timestamp
* HTTP status
* Error message