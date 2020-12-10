import enums.ZipFormatType;
import factory.CompressorDecompressorFactory;
import interfaces.FileCompressorDecompressor;

public class Zippy {
    static final CompressorDecompressorFactory factory = new CompressorDecompressorFactory();

    public static void main(String[] args) {
        // utility to display usage and parse-format args

        long maxFileSizeMB = 10; //Long.valueOf(args[0]);

        FileCompressorDecompressor FCD = factory.initFCD(ZipFormatType.ZIP, maxFileSizeMB * 1024 * 1024);

        String sourceDir = "/home/km2411/IdeaProjects/zippy/TestFiles/";
        String destDir = "/home/km2411/IdeaProjects/zippy/TestOut/";

        FCD.zip(sourceDir, destDir);
        FCD.unzip(destDir, sourceDir);
    }
}
