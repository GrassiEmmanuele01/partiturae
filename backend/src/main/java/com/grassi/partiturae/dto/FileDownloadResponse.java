package com.grassi.partiturae.dto;

public record FileDownloadResponse(String filename, byte[] content) {
}