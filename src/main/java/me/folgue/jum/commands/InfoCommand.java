package me.folgue.jum.commands;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.concurrent.Callable;
import me.folgue.jum.App;
import me.folgue.jum.config.PackageConfiguration;
import me.folgue.jum.repository.JavaUtilityRepository;
import picocli.CommandLine;

@CommandLine.Command(name = "info", description = "Displays information about the specified package.", version = App.VERSION)
public class InfoCommand implements Callable<Integer> {

    private JavaUtilityRepository uRepo;

    @CommandLine.Option(usageHelp = true, names = {"-h", "--help"})
    private boolean help;

    @CommandLine.Option(versionHelp = true, names = {"-v", "--version"})
    private boolean version;

    @CommandLine.Parameters(paramLabel = "package name")
    private String packageName;

    @Override
    public Integer call() {
        if (!this.initializeRepo()) {
            return 1;
        }

        if (!this.uRepo.doesPackageExist(packageName)) {
            System.err.println(Ansi.colorize("The specified package doesn't exist in the repository.", Attribute.RED_TEXT()));
            return 1;
        }

        var pkgConfigOpt = this.readPackageConfiguration();

        if (pkgConfigOpt.isPresent()) {
            System.out.printf("====> Package configuration of '%s'\n", this.packageName);
            System.out.println(pkgConfigOpt.get());
            return 0;
        } else {
            return 1;
        }
    }

    public boolean initializeRepo() {
        this.uRepo = JavaUtilityRepository.defaultRepo();
        try {
            this.uRepo.initialize();
            return true;
        } catch (IOException e) {
            System.err.println(Ansi.colorize("Couldn't initialize repository.", Attribute.GREEN_TEXT()));
            return false;
        }
    }

    public Optional<PackageConfiguration> readPackageConfiguration() {
        try {
            String pkgConfigStr = Files.readString(this.uRepo.getPkgConfigPath(this.packageName));
            return Optional.of(PackageConfiguration.fromTOMLString(pkgConfigStr));
        } catch (IOException e) {
            System.err.println(Ansi.colorize("Couldn't read/parse the package configuration due to the following error: " + e));
            return Optional.empty();
        }

    }
}
