package com.test.impl;

import com.google.common.collect.SortedSetMultimap;
import com.test.enums.ZipFormatType;
import com.test.interfaces.FileCompressorDecompressor;
import com.test.services.ChunkedUnzip;
import com.test.services.ChunkedZip;
import com.test.utils.ZippyUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements {@link FileCompressorDecompressor} using {@link java.util.zip} compression algorithm
 */
public class FileCompressorDecompressorZipImpl implements FileCompressorDecompressor {

    private final ExecutorService executor;
    private static final Logger LOGGER = Logger.getLogger(FileCompressorDecompressorZipImpl.class.getName());

    /**
     * @param nThreads - thread pool size for executor service
     */
    public FileCompressorDecompressorZipImpl(int nThreads) {
        this.executor = Executors.newFixedThreadPool(nThreads);
    }

    /**
     * This method calls {@link ChunkedZip} for the provided arguments
     * {@inheritDoc}
     */
    @Override
    public void compress(String sourceDir, String destinationDir, Integer maxFileSize) {
        long start = System.currentTimeMillis();
        Map<String, List<String>> pathToFileList = ZippyUtils.getAllFilesInDir(sourceDir);
        for (Map.Entry<String, List<String>> entry : pathToFileList.entrySet()) {
            String relativePath = entry.getKey();
            for (String file : entry.getValue()) {
                ZippyUtils.createOutDirsIfMissing(destinationDir + relativePath);
                executor.execute(new ChunkedZip(file, sourceDir + relativePath,
                                    destinationDir + relativePath, maxFileSize));
            }
        }
        terminate(start);
    }

    /**
     * This method calls {@link ChunkedUnzip} for the provided arguments
     * {@inheritDoc}
     */
    @Override
    public void decompress(String sourceDir, String destinationDir) {
        long start = System.currentTimeMillis();
        Map<String, SortedSetMultimap<String, String>> pathToFileAndPartFiles = ZippyUtils.getAllZippedFilesWithParts(sourceDir,
                                                                            ZipFormatType.ZIP.getExtension());
        for (Map.Entry<String, SortedSetMultimap<String, String>> entry : pathToFileAndPartFiles.entrySet()) {
            String relativePath = entry.getKey();
            for (String file : entry.getValue().keySet()) {
                ZippyUtils.createOutDirsIfMissing(destinationDir + relativePath);
                executor.execute(new ChunkedUnzip(file, sourceDir + relativePath,
                                    destinationDir + relativePath, entry.getValue().get(file)));
            }
        }
        terminate(start);
    }

    private void terminate(long start) {
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            executor.shutdownNow();
            LOGGER.log(Level.SEVERE, e.toString());
        } finally {
            executor.shutdownNow();
            long finish = System.currentTimeMillis();
            LOGGER.info("Elapsed Time: " + ((float) (finish - start)) / 1000 + " seconds");
        }
    }

}
