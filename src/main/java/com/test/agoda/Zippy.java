package com.test.agoda;

import com.google.common.collect.Maps;
import com.test.agoda.enums.ZipFormatType;
import com.test.agoda.factory.CompressorDecompressorFactory;
import com.test.agoda.interfaces.FileCompressorDecompressor;
import com.test.agoda.utils.ZippyUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static java.lang.System.exit;

public class Zippy {
    private static final CompressorDecompressorFactory factory = new CompressorDecompressorFactory();

    public static void main(String[] args) {
        Map<String, Integer> config = loadConfig();
        int len = args.length;
        if (len == 0) {
            System.out.println("No input argument provided");
            printUsage();
        }
        String mode = args[0];
        String sourceDir, destDir;
        FileCompressorDecompressor FCD = factory.initFCD(ZipFormatType.ZIP, config.get("ThreadCount"));

        switch (mode.toLowerCase()) {
            case "-c":
                if (len < 4 || len > 4) {
                    System.out.println("Incorrect arguments provided");
                    printUsage();
                }
                sourceDir = ZippyUtils.getPathWithDelimiter(args[1]);
                destDir = ZippyUtils.getPathWithDelimiter(args[2]);
                Integer maxSize = Integer.valueOf(args[3]);
                if (maxSize == null || maxSize == 0) {
                    maxSize = config.get("DefaultFileSize");
                    System.out.println("Using default maxFileSize = " + maxSize);
                }
                FCD.zip(sourceDir, destDir, maxSize);
                break;
            case "-d":
                if (len < 3 || len > 3) {
                    System.out.println("Incorrect arguments provided");
                    printUsage();
                }
                sourceDir = ZippyUtils.getPathWithDelimiter(args[1]);
                destDir = ZippyUtils.getPathWithDelimiter(args[2]);
                FCD.unzip(sourceDir, destDir);
                break;
            default:
                printUsage();
        }
    }

    private static Map<String, Integer> loadConfig() {
        Map<String, Integer> configs = Maps.newHashMap();
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            configs.put("ThreadCount", Integer.parseInt(String.valueOf(properties.get("ThreadCount"))));
            configs.put("DefaultFileSize", Integer.parseInt(String.valueOf(properties.get("DefaultFileSize"))));
            return configs;
        } catch (IOException e) {
            System.out.println("Error in loading config.properties");
            e.printStackTrace();
            exit(1);
        }
        return configs;
    }

    private static void printUsage() {
        System.out.println("Usage: ");
        System.out.println("Compress : -c input_dir output_dir max_file_size[MB]");
        System.out.println("Decompress : -d input_dir output_dir");
        exit(1);
    }
}
