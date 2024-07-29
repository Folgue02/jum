package me.folgue.jum.commands;

import static com.diogonunes.jcolor.Ansi.colorize;
import com.diogonunes.jcolor.Attribute;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Callable;
import me.folgue.jum.App;
import me.folgue.jum.config.PackageConfiguration;
import me.folgue.jum.repository.JavaUtilityRepository;
import picocli.CommandLine;

@CommandLine.Command(name = "install", description = "Installs a utility", version = App.VERSION)
public class InstallCommand implements Callable<Integer> {

    private JavaUtilityRepository uRepo;

    @CommandLine.Parameters(index = "0", description = "Path to the package file")
    private File packageFile;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true)
    private boolean help;

    @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true)
    private boolean version;

    @CommandLine.Option(names = {"-j", "--jar"}, required = false)
    private String jarPackagePath = null;

    @Override
    public Integer call() {
        this.uRepo = new JavaUtilityRepository(Path.of(System.getProperty("user.home"), ".jumrepo"));
        try {
            this.uRepo.initialize();
        } catch (IOException e) {
            System.err.println(colorize("Couldn't initialize the repository due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return 255;
        }

        if (!this.packageFile.isFile()) {
            System.out.println(colorize("The specified file doesn't exist.", Attribute.RED_TEXT()));
            return 1;
        }

        PackageConfiguration pkgConfig;
        try {
            String configStr = Files.readString(this.packageFile.toPath());
            pkgConfig = PackageConfiguration.fromString(configStr);
            Objects.requireNonNull(pkgConfig);
        } catch (Exception e) {
            System.err.println(colorize("Couldn't read the specified file due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return 1;
        }

        if (this.jarPackagePath == null) {
            this.jarPackagePath = Path.of(Objects.requireNonNullElse(this.packageFile.getParent(), "."), pkgConfig.getName() + ".jar").toString();
        }

        System.out.printf("Using '%s' as package configuration.\n", pkgConfig);
        try {
            this.uRepo.saveScript(pkgConfig);
        } catch (IOException e) {
            System.err.println(colorize("Couldn't create the package's script due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return 1;
        }

        try {
            this.uRepo.saveJar(pkgConfig, Path.of(this.jarPackagePath));
        } catch (IOException e) {
            System.err.println(colorize("Couldn't copy the package's JAR due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return 1;
        }
        return 0;
    }

    public void checkJdkInstallation(String version) {
        if (this.uRepo.isJdkInstalled(version)) {
            return;
        }

    }
}
