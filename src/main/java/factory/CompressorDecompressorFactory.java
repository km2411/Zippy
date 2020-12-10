package factory;

import enums.ZipFormatType;
import impl.FileCompressorDecompressorZipImpl;
import interfaces.FileCompressorDecompressor;

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
