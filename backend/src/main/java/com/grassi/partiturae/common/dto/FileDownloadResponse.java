package com.grassi.partiturae.common.dto;

public record FileDownloadResponse(String filename, byte[] content) {
}