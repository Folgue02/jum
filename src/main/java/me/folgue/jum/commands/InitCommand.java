package me.folgue.jum.commands;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;
import me.folgue.jum.App;
import me.folgue.jum.config.PackageConfiguration;
import picocli.CommandLine;

@CommandLine.Command(name = "init", description = "Initializes the package configuration for a jar.", version = App.VERSION)
public class InitCommand implements Callable<Integer> {

    @CommandLine.Parameters(description = "Name of the jar file to use.", paramLabel = "jar")
    private File jarFile;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    private boolean help;

    @CommandLine.Option(names = {"-V", "--package-version"}, description = "Specifies the version of the package.", defaultValue = "1.0")
    private String packageVersion;

    @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true)
    private boolean version;

    @CommandLine.Option(names = {"-o", "--output"}, description = "YAML output file.")
    private Optional<File> outputYmlFile;

    @CommandLine.Option(names = {"-j", "--jdk-version"}, description = "Version of the JDK to be used with this tool.")
    private Optional<String> jdkVersion;

    @CommandLine.Option(names = {"-a", "--author"}, description = "Specifies the name of the author.")
    private Optional<String> packageAuthor;

    @CommandLine.Option(names = {"-n", "--name"}, description = "Specifies the name of the package.")
    private Optional<String> packageName;

    @CommandLine.Option(names = {"-d", "--description"}, description = "Specifies the description of the package.")
    private Optional<String> packageDescription;

    @Override
    public Integer call() throws Exception {
        String packageAuthorString = this.packageAuthor.orElse(System.getProperty("user.name", "anon"));
        String packageVersionString = this.packageVersion;
        String jdkVersionString = this.jdkVersion.orElse(System.getProperty("java.specification.version", "22"));
        String packageNameString = this.packageName.orElseGet(() -> {
            if (this.jarFile.getName().contains(".")) {
                return this.jarFile.getName().split("\\.")[0];
            }
            return this.jarFile.getName();
        });
        String packageDescriptionString = this.packageDescription.orElse("A Java utility.");
        Path outputYmlFileFinal = this.outputYmlFile.orElse(new File(packageNameString + ".yml")).toPath();

        var pkgConfig = new PackageConfiguration(packageNameString, packageDescriptionString, jdkVersionString, packageAuthorString, packageVersionString);

        if (!this.jarFile.isFile()) {
            System.err.println(Ansi.colorize("The JAR file specified doesn't seem to be a file/exist.", Attribute.RED_TEXT(), Attribute.BOLD()));
            return 1;
        }

        try {
            var mapper = new TomlMapper();

            mapper.writeValue(outputYmlFileFinal.toFile(), pkgConfig);
        } catch (IOException e) {
            System.err.println(Ansi.colorize("Couldn't write into the output file.", Attribute.RED_TEXT(), Attribute.BOLD()));
            return 1;
        }

        System.out.println(Ansi.colorize("Package configuration generated.", Attribute.GREEN_TEXT()));
        return 0;
    }
}
