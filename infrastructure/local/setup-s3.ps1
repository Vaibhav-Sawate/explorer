$ErrorActionPreference = "Stop"

$ENDPOINT_URL = "http://localhost:4566"
$BUCKET = "cloud-data"

# Project root: infrastructure/local -> explorer
$PROJECT_ROOT = Resolve-Path "$PSScriptRoot\..\.."
$TEST_FILES = Join-Path $PROJECT_ROOT "test-files"

Write-Host ""
Write-Host "====================================="
Write-Host " Setting up LocalStack S3 test data"
Write-Host "====================================="
Write-Host ""

# Check whether the bucket already exists
$bucketExists = aws s3 ls "s3://$BUCKET" `
    --endpoint-url=$ENDPOINT_URL 2>$null

if ($LASTEXITCODE -ne 0) {
    Write-Host "Creating bucket: $BUCKET"

    aws s3 mb "s3://$BUCKET" `
        --endpoint-url=$ENDPOINT_URL
}
else {
    Write-Host "Bucket already exists: $BUCKET"
}

Write-Host ""
Write-Host "Uploading test files..."
Write-Host ""

# Root files
aws s3 cp `
    (Join-Path $TEST_FILES "hello.txt") `
    "s3://$BUCKET/hello.txt" `
    --endpoint-url=$ENDPOINT_URL

# info.log
if (Test-Path (Join-Path $TEST_FILES "info.log")) {
    aws s3 cp `
        (Join-Path $TEST_FILES "info.log") `
        "s3://$BUCKET/info.log" `
        --endpoint-url=$ENDPOINT_URL
}

# File with spaces
if (Test-Path (Join-Path $TEST_FILES "my new file.txt")) {
    aws s3 cp `
        (Join-Path $TEST_FILES "my new file.txt") `
        "s3://$BUCKET/my new file.txt" `
        --endpoint-url=$ENDPOINT_URL
}

# Reports
if (Test-Path (Join-Path $TEST_FILES "report.pdf")) {
    aws s3 cp `
        (Join-Path $TEST_FILES "report.pdf") `
        "s3://$BUCKET/reports/2026/january/report.pdf" `
        --endpoint-url=$ENDPOINT_URL
}

# Customer 101
aws s3 cp `
    (Join-Path $TEST_FILES "error-101.log") `
    "s3://$BUCKET/support/customer-101/logs/error.log" `
    --endpoint-url=$ENDPOINT_URL

# Customer 202
aws s3 cp `
    (Join-Path $TEST_FILES "error-202.log") `
    "s3://$BUCKET/support/customer-202/logs/error.log" `
    --endpoint-url=$ENDPOINT_URL

# Customer 202 additional error
aws s3 cp `
    (Join-Path $TEST_FILES "error-202-extra.log") `
    "s3://$BUCKET/support/customer-202/logs/error-202.log" `
    --endpoint-url=$ENDPOINT_URL

# Large file for preview limit testing
$LARGE_FILE = Join-Path $PROJECT_ROOT "large-file.txt"

if (Test-Path $LARGE_FILE) {
    aws s3 cp `
        $LARGE_FILE `
        "s3://$BUCKET/large-file.txt" `
        --endpoint-url=$ENDPOINT_URL
}

Write-Host ""
Write-Host "====================================="
Write-Host " S3 setup complete!"
Write-Host "====================================="
Write-Host ""

Write-Host "Current S3 objects:"
aws s3 ls "s3://$BUCKET/" `
    --recursive `
    --endpoint-url=$ENDPOINT_URL