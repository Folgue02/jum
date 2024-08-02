package me.folgue.jum;

import me.folgue.jum.commands.MainCommand;
import picocli.CommandLine;

public class App {

    public final static String VERSION = "0.2";

    public static void main(String[] args) throws Exception {
        new CommandLine(new MainCommand()).execute(args);
    }
}
