package me.folgue.jum.commands;

import me.folgue.jum.App;
import picocli.CommandLine;

@CommandLine.Command(name = "jum", description = "Java Utility Manager", version = App.VERSION, subcommands = {InstallCommand.class, InitCommand.class})
public class MainCommand {

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    private boolean help;

    @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true)
    private boolean version;

}
