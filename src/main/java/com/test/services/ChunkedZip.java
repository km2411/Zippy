package com.test.services;

import com.test.enums.ZipFormatType;
import com.test.utils.ZippyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private static final Logger LOGGER = Logger.getLogger(ChunkedZip.class.getName());

    /**
     * This class compresses a file to create .zip files
     *
     * @param filename    - name of the file to be compressed
     * @param sourceDir   - source path for files to be compressed
     * @param destDir     - destination path for compressed (.zip) files
     * @param maxFileSize - maximum file size limit for a compressed file
     */
    public ChunkedZip(String filename, String sourceDir, String destDir, Integer maxFileSize) {
        this.filename = filename;
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        this.maxFileSize = (long) maxFileSize * 1024 * 1024;
    }

    @Override
    public void run() {
        try {
            createZip();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }

    private void createZip() throws IOException {
        String filepath = ZippyUtils.getPathWithDelimiter(sourceDir) + filename;
        fis = new FileInputStream(filepath);
        long filesize = Files.size(Paths.get(filepath));

        int length;
        long remaining = filesize;
        long accumulator = 0;
        byte[] inBuffer = new byte[ZippyUtils.BUFFER_SIZE];
        updateCurrentZipOutStream();

        while ((length = fis.read(inBuffer)) > 0) {
            if (accumulator >= (maxFileSize - new Random().nextInt(ZippyUtils.BUFFER_SIZE))) {
                updateCurrentZipOutStream();
                remaining -= accumulator;
                accumulator = 0;
                LOGGER.info("Compressing " + filename + ".. " + ((float) (filesize - remaining) * 100 / filesize) + " % done..");
            }
            currZipOutStream.write(inBuffer, 0, length);
            accumulator += length;
        }

        closeZipOutStream();
        fis.close();
        LOGGER.info("Compression complete for " + filename + " 100 % done..");
    }

    private void updateCurrentZipOutStream() throws IOException {
        if (currZipOutStream != null) {
            closeZipOutStream();
        }
        File targetFile = new File(ZippyUtils.getPathWithDelimiter(destDir), getFilePartName());
        FileOutputStream fos = new FileOutputStream(targetFile);
        currZipOutStream = new ZipOutputStream(fos);
        currZipOutStream.putNextEntry(new ZipEntry(filename));
    }

    private void closeZipOutStream() throws IOException {
        currZipOutStream.flush();
        currZipOutStream.closeEntry();
        currZipOutStream.close();
    }

    private String getFilePartName() {
        currPartIndex += 1;
        StringBuilder nameBuilder = new StringBuilder(filename).append(ZippyUtils.PART_POSTFIX)
                .append(currPartIndex).append(FILE_EXTENSION);
        return nameBuilder.toString();
    }
}
