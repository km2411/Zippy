package com.test.interfaces;

/**
 * This interface provides methods to compress/ de-compress files
 */
public interface FileCompressorDecompressor {

    /**
     * @param sourceDir      - source path for files to be compressed
     * @param destinationDir - destination path for compressed files
     * @param maxFileSize    - maximum file size limit for a compressed file
     */
    void compress(String sourceDir, String destinationDir, Integer maxFileSize);

    /**
     * @param sourceDir      - source path for files to be de-compressed
     * @param destinationDir - destination path for unzipped files.
     */
    void decompress(String sourceDir, String destinationDir);
}
