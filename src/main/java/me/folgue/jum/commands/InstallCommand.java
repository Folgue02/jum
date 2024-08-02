package me.folgue.jum.commands;

import static com.diogonunes.jcolor.Ansi.colorize;
import com.diogonunes.jcolor.Attribute;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import me.folgue.jum.App;
import me.folgue.jum.config.PackageConfiguration;
import me.folgue.jum.repository.JavaUtilityRepository;
import me.folgue.jum.utils.EnvUtils;
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
        if (!this.initalizeRepo()) {
            return 1;
        }

        if (!this.packageFile.isFile()) {
            System.err.println(colorize("The specified file doesn't exist.", Attribute.RED_TEXT()));
            return 1;
        }

        PackageConfiguration pkgConfig;
        Optional<PackageConfiguration> pkgConfigOpt = this.readPackageConfig(this.packageFile.toPath());

        if (pkgConfigOpt.isPresent()) {
            pkgConfig = pkgConfigOpt.get();
        } else {
            return 1;
        }

        if (this.jarPackagePath == null) {
            this.jarPackagePath = Path.of(Objects.requireNonNullElse(this.packageFile.getParent(), "."), pkgConfig.getName() + ".jar").toString();
        }

        System.out.printf("Package to be installed: \n%s\n", pkgConfig);

        if (!this.savePackageConfig(pkgConfig) || !this.saveScript(pkgConfig) || !this.saveJar(pkgConfig)) {
            return 1;
        }

        System.out.println(colorize("%s v%s installed succesfully.".formatted(pkgConfig.getName(), pkgConfig.getVersion()), Attribute.GREEN_TEXT()));

        this.postInstallationHook();
        return 0;
    }

    private Optional<PackageConfiguration> readPackageConfig(Path packageFilePath) {
        try {
            String configStr = Files.readString(packageFilePath);
            PackageConfiguration pkgConfig = PackageConfiguration.fromTOMLString(configStr);
            Objects.requireNonNull(pkgConfig);
            return Optional.of(pkgConfig);
        } catch (IOException e) {
            System.err.println(colorize("Couldn't read the specified file due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return Optional.empty();
        } catch (NullPointerException e) {
            System.err.println(colorize("Invalid package configuration.", Attribute.RED_TEXT()));
            return Optional.empty();
        }
    }

    private boolean initalizeRepo() {
        this.uRepo = JavaUtilityRepository.defaultRepo();
        try {
            this.uRepo.initialize();
        } catch (IOException e) {
            System.err.println(colorize("Couldn't initialize the repository due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return false;
        }
        return true;
    }

    private boolean savePackageConfig(PackageConfiguration pkgConfig) {
        try {
            this.uRepo.savePackageConfig(pkgConfig);
        } catch (IOException e) {
            System.err.println(colorize("Couldn't save the package configuration in the repository due to the following repository: " + e, Attribute.RED_TEXT()));
            return false;
        }
        return true;
    }

    private boolean saveScript(PackageConfiguration pkgConfig) {
        try {
            this.uRepo.saveScript(pkgConfig);
        } catch (IOException e) {
            System.err.println(colorize("Couldn't create the package's script due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return false;
        }
        return true;
    }

    private boolean saveJar(PackageConfiguration pkgConfig) {
        try {
            this.uRepo.saveJar(pkgConfig, Path.of(this.jarPackagePath));
        } catch (IOException e) {
            System.err.println(colorize("Couldn't copy the package's JAR due to the following error: " + e.getMessage(), Attribute.RED_TEXT(), Attribute.BOLD()));
            return false;
        }
        return true;
    }

    public void postInstallationHook() {
        if (!EnvUtils.isInPath(this.uRepo.getBinPath())) {
            System.out.println(colorize("WARNING: The binaries directory of the JUM repository is not present in the PATH variable, for JUM to work properly, add it to the PATH variable (i.e. export PATH=\"" + this.uRepo.getBinPath() + "\").", Attribute.YELLOW_TEXT()));
        }

    }
}
