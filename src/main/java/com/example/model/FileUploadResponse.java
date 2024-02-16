package com.example.model;

import lombok.Builder;

@Builder
public record FileUploadResponse(String fileName, String downloadURI, long size) {}
