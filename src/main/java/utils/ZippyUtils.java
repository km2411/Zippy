package utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ZippyUtils {

    public static final int BUFFER_SIZE = 1024;
    public static final String PART_POSTFIX = ".part.";
    public static final String DELIM = "/";

    public static Map<String, List<String>> getAllFilesInDir(String sourceDir) {
        Map<String, List<String>> pathToFileList = Maps.newHashMap();
        File directory = new File(sourceDir);
        walk(sourceDir, directory, pathToFileList);
        return pathToFileList;
    }

    private static void walk(String sourceDir, File directory, Map<String, List<String>> pathToFileList) {
        List<String> filesOnPath = Lists.newArrayList();
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                walk(sourceDir, file, pathToFileList);
            } else {
                filesOnPath.add(file.getName());
            }
        }
        Path base = Paths.get(sourceDir);
        Path curDir = Paths.get(directory.getAbsolutePath());
        pathToFileList.put(base.relativize(curDir).toString(), filesOnPath);
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

    private static String getPrefix(String file) {
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
}
