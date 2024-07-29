package me.folgue.jum.utils;

import java.util.ArrayList;
import java.util.List;

public class CliUtils {

    private final static long ANIM_DELAY = 150;

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
}
