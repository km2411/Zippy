package com.test.services;

import com.test.utils.ZippyUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ChunkedUnzip implements Runnable {

    private String filename;
    private String sourceDir;
    private String destDir;
    private SortedSet<String> partFiles;

    private FileOutputStream fos;
    private static final Logger LOGGER = Logger.getLogger(ChunkedUnzip.class.getName());

    /**
     * This class de-compresses .zip files to retrieve original file
     *
     * @param filename  - name of the original file, before compression
     * @param sourceDir - source path for .zip files to be de-compressed
     * @param destDir   - destination path for unzipped files.
     * @param partFiles - sorted set of .zip files created after compression
     */
    public ChunkedUnzip(String filename, String sourceDir, String destDir, SortedSet<String> partFiles) {
        this.filename = filename;
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        this.partFiles = partFiles;
    }

    @Override
    public void run() {
        try {
            unzipAll();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }

    private void unzipAll() throws IOException {
        try {
            fos = new FileOutputStream(ZippyUtils.getPathWithDelimiter(destDir) + filename);
            for (String file : partFiles) {
                unzipSingleFile(file);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.toString());
        } finally {
            fos.flush();
            fos.close();
            LOGGER.info("De-compression complete for " + filename + " 100 % done..");
        }
    }

    private void unzipSingleFile(String partFile) {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceDir + ZippyUtils.DELIMITER + partFile))) {
            ZipEntry zipEntry = zis.getNextEntry();
            LOGGER.info("De-compressing " + filename + "..Part " + (partFiles.headSet(partFile).size() + 1) + " / " + partFiles.size());
            while (zipEntry != null) {
                int length;
                byte buffer[] = new byte[ZippyUtils.BUFFER_SIZE];
                while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString());
        }
    }
}