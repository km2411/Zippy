package com.test.enums;

/**
 * Enum to denote different compression types
 */
public enum ZipFormatType {

    ZIP(".zip");

    private final String extension;

    ZipFormatType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
