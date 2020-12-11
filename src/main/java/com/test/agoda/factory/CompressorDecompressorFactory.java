package com.test.agoda.factory;

import com.test.agoda.enums.ZipFormatType;
import com.test.agoda.impl.FileCompressorDecompressorZipImpl;
import com.test.agoda.interfaces.FileCompressorDecompressor;

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
