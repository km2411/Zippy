package interfaces;

public interface FileCompressorDecompressor {

    void zip(String sourceDir, String destinationDir);

    void unzip(String sourceDir, String destinationDir);
}
