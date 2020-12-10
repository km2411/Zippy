package factory;

import enums.ZipFormatType;
import impl.FileCompressorDecompressorZipImpl;
import interfaces.FileCompressorDecompressor;

public class CompressorDecompressorFactory {

    public FileCompressorDecompressor initFCD(ZipFormatType type, long maxFileSize) {
        switch (type) {
            case ZIP:
                return new FileCompressorDecompressorZipImpl(maxFileSize);
            default:
                throw new IllegalArgumentException("Un-supported format");
        }
    }
}
