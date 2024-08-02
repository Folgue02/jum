package me.folgue.jum.utils;

import java.util.ArrayList;
import java.util.List;

public final class CliUtils {

    private final static long ANIM_DELAY = 150;

    private CliUtils() {
    }

    public interface Task<T> {

        T run() throws Exception;
    }

    public static <T> T runWithProgress(Task<T> task, String message) throws Exception {
        return runWithProgress(task, message, true);
    }

    public static <T> T runWithProgress(Task<T> t, String message, boolean newLine) throws Exception {
        Exception[] thrownException = {null};
        List<T> result = new ArrayList<>();

        var process = Thread.ofVirtual().start(() -> {
            try {
                result.add(t.run());
            } catch (Exception e) {
                thrownException[0] = e;
            }
        });

        int animIndex = 0;
        char[] anim = {'|', '/', '-', '\\'};
        while (process.isAlive()) {
            if (animIndex == anim.length - 1) {
                animIndex = 0;
            } else {
                animIndex++;
            }
            String animMessage = "%c %s".formatted(anim[animIndex], message);

            System.out.printf("\r%s", animMessage);

            Thread.sleep(ANIM_DELAY);
        }

        if (thrownException[0] != null) {
            throw thrownException[0];
        }
        if (newLine) {
            System.out.println();
        }

        return result.get(0);
    }

    public String stringIntoLines(String text, int lineLength) {
        var sb = new StringBuilder();
        int currLineLength = 0;

        for (char c : text.toCharArray()) {
            if (currLineLength >= lineLength) {
                sb.append('\n');
                sb.append(c);
                currLineLength = 1;
            } else if (c == '\n') {
                sb.append(c);
                currLineLength = 0;
            } else {
                currLineLength++;
                sb.append(c);
            }

        }
        return sb.toString();
    }
}
