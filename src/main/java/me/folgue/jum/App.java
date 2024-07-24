package me.folgue.jum;

import me.folgue.jum.commands.MainCommand;
import picocli.CommandLine;

public class App {

    public final static String VERSION = "0.1";

    public static void main(String[] args) {
        new CommandLine(new MainCommand()).execute(args);
    }
}
