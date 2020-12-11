package com.test.agoda.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ZippyUtils {

    public static final int BUFFER_SIZE = 1024;
    public static final String PART_POSTFIX = ".part.";
    public static final String DELIM = "/";

    private ZippyUtils() {
        throw new IllegalStateException("Can't instantiate a utility class");
    }

    public static Map<String, List<String>> getAllFilesInDir(String sourceDir) {
        Map<String, List<String>> pathToFileList = Maps.newHashMap();
        walk(sourceDir, Paths.get(sourceDir), pathToFileList);
        return pathToFileList;
    }

    private static void walk(String sourceDir, Path currDirectory, Map<String, List<String>> pathToFileList) {
        List<String> filesOnPath = Lists.newArrayList();
        List<Path> files = getFilesInDir(currDirectory, true);
        for (Path file : files) {
            if (Files.isDirectory(file)) {
                walk(sourceDir, file, pathToFileList);
            } else {
                filesOnPath.add(currDirectory.relativize(file).toString());
            }
        }
        Path base = Paths.get(sourceDir);
        pathToFileList.put(base.relativize(currDirectory).toString(), filesOnPath);
    }

    public static List<Path> getFilesInDir(Path currDir, boolean considerSubDirs) {
        // walk includes current path as well in the output
        try (Stream<Path> files = Files.walk(currDir)) {
            return files.filter(f -> !f.equals(currDir) && (!Files.isDirectory(f) || considerSubDirs))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    public static Map<String, SortedSetMultimap<String, String>> getAllZippedFilesWithParts(String sourceDir, String extension) {
        // check files with extension
        Map<String, SortedSetMultimap<String, String>> allZippedWithParts = Maps.newHashMap();
        Map<String, List<String>> allFilesInDir = getAllFilesInDir(sourceDir);
        for (Map.Entry<String, List<String>> filesAtPath : allFilesInDir.entrySet()) {
            allZippedWithParts.put(filesAtPath.getKey(), getZippedPartFiles(filesAtPath.getValue(), extension));
        }
        return allZippedWithParts;
    }

    private static SortedSetMultimap<String, String> getZippedPartFiles(List<String> filenames, String extension) {
        SortedSetMultimap<String, String> fileToParts = TreeMultimap.create();
        for (String file : filenames) {
            if (file.endsWith(extension)) {
                // need to keep a sorted order here
                fileToParts.put(getPrefix(file), file);
            }
        }
        return fileToParts;
    }

    public static String getPrefix(String file) {
        String[] parts = file.split("\\.");
        int len = parts.length - 3;
        String key = parts[0];
        for (int i = 1; i < len; i++) {
            key += "." + parts[i];
        }
        return key;
    }

    public static void createOutDirsIfMissing(String destDir) {
        File directory = new File(destDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static String getPathWithDelimiter(String path) {
        if (path.endsWith(DELIM)) {
            return path;
        }
        return path + DELIM;
    }
}
