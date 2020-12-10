package models;

import enums.ZipFormatType;
import utils.ZippyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ChunkedZip implements Runnable {

    private String filename;
    private String sourceDir;
    private String destDir;

    private long maxFileSize;

    private FileInputStream fis;
    private ZipOutputStream currZipOutStream;
    private int currPartIndex;

    private static final String FILE_EXTENSION = ZipFormatType.ZIP.getExtension();

    public ChunkedZip(String filename, String sourceDir, String destDir, Integer maxFileSize) {
        this.filename = filename;
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        this.maxFileSize = maxFileSize * 1024 * 1024;
    }

    @Override
    public void run() {
        try {
            createZip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createZip() throws IOException {
        String filepath = ZippyUtils.getPathWithDelimiter(sourceDir) + filename;
        fis = new FileInputStream(filepath);
        long filesize = Files.size(Paths.get(filepath));

        int length;
        long remaining = filesize, accumulator = 0;
        byte[] inBuffer = new byte[ZippyUtils.BUFFER_SIZE];

        while ((length = fis.read(inBuffer)) > 0) {
            if (currZipOutStream == null || (accumulator + ZippyUtils.BUFFER_SIZE >= maxFileSize)) {
                updateCurrentZipOutStream();
                remaining -= accumulator;
                System.out.println("Compressing " + filename + ".. " + ((float) (filesize - remaining) * 100 / filesize) + " % done..");
                accumulator = 0;
            }
            currZipOutStream.write(inBuffer, 0, length);
            accumulator += length;
        }

        fis.close();
        currZipOutStream.closeEntry();
        currZipOutStream.close();
        System.out.println("Compression complete for " + filename + " 100 % done..");
    }

    private void updateCurrentZipOutStream() throws IOException {
        if (currZipOutStream != null) {
            currZipOutStream.closeEntry();
            currZipOutStream.close();
        }
        File targetFile = new File(ZippyUtils.getPathWithDelimiter(destDir), getFilePartName());
        FileOutputStream fos = new FileOutputStream(targetFile);
        currZipOutStream = new ZipOutputStream(fos);
        currZipOutStream.putNextEntry(new ZipEntry(filename));
    }

    private String getFilePartName() {
        currPartIndex += 1;
        StringBuilder nameBuilder = new StringBuilder(filename).append(ZippyUtils.PART_POSTFIX)
                .append(currPartIndex).append(FILE_EXTENSION);
        return nameBuilder.toString();
    }
}
