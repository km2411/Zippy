package com.test.factory;

import com.test.enums.ZipFormatType;
import com.test.impl.FileCompressorDecompressorZipImpl;
import com.test.interfaces.FileCompressorDecompressor;

/**
 * Factory that provides required compression-decompression algorithm's implementation
 */
public class CompressorDecompressorFactory {

    public FileCompressorDecompressor initFCD(ZipFormatType type, Integer threadCount) {
        switch (type) {
            case ZIP:
                return new FileCompressorDecompressorZipImpl(threadCount);
            default:
                throw new IllegalArgumentException("Un-supported format");
        }
    }
}
