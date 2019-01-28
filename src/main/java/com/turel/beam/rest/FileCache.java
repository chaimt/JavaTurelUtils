package com.turel.beam.rest;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class FileCache {
    private Map<String, FileInfo> fileCache = new HashMap<>();

    public boolean isUpdated(String deployJar, Long updateTime) {
        final FileInfo fileInfo = fileCache.get(deployJar);
        if (fileInfo == null) {
            return true;
        }
        return fileInfo.updatedTime < updateTime;
    }

    public void cacheFile(String deployJar, Long updateTime){
        FileInfo fileInfo = fileCache.get(deployJar);
        if (fileInfo==null){
            fileInfo =  FileInfo.builder().jar(deployJar).updatedTime(updateTime).build();
        }
        fileInfo.updatedTime = updateTime;
        fileCache.put(deployJar,fileInfo);
    }

    @Data
    @Builder
    static class FileInfo {
        Long updatedTime;
        String jar;
    }


}
