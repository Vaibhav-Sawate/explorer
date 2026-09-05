package com.explorer.backend.dto;

import java.util.List;
//{"folders":[{"name":"reports","path":"reports/"}],
// "files:[{"name": "hello.txt","key":"hello.txt","size":26}]}

public record ObjectListResponse (
        String bucket,
        String prefix,
        List<FolderResponse> folders,
        List<FileResponse> files,
        boolean isTruncated,          //tells frontend is there are more results available
        String nextContinuationToken     //contains token to req the next page
){
}
