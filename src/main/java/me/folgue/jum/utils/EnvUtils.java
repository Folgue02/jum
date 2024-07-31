package me.folgue.jum.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;

public class EnvUtils {

    public static boolean isInPath(Path path) {
        var pathContents = System.getenv().getOrDefault("PATH", "");
        return Arrays.stream(pathContents.split(File.pathSeparator))
                .anyMatch(p -> Path.of(p).equals(path));
    }
}
