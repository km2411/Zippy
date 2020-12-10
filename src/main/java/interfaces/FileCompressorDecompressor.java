package interfaces;

public interface FileCompressorDecompressor {

    void zip(String sourceDir, String destinationDir, Integer maxFileSizeMB);

    void unzip(String sourceDir, String destinationDir);
}
