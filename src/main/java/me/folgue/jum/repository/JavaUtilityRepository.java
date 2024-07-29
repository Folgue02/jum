package me.folgue.jum.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import me.folgue.jum.config.PackageConfiguration;

@Data
@AllArgsConstructor
public class JavaUtilityRepository {

    private final static String PKG_PATH = "pkgs";
    private final static String CONFIG_PATH = "config";
    private final static String BIN_PATH = "bin";
    private final static String JDK_PATH = "jdk";

    private final Path repoPath;

    /**
     * @return The contents of the script template read from the resources of
     * the application.
     */
    private static String getScriptTemplate() {
        try {
            return new String(JavaUtilityRepository.class.getClassLoader().getResourceAsStream("run_script_template.pl").readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    /**
     * Generates the scripts contents based on the configuration given.
     *
     * @param scriptInfo Configuration that'll be used to generate the script.
     * @return The contents of the script generated.
     */
    private static String generateScript(ScriptGeneratorConfig scriptInfo) {
        String stringTemplate = getScriptTemplate();
        return stringTemplate.formatted(
                scriptInfo.generatorVersion(),
                scriptInfo.jdkVersion()
        );
    }

    /**
     * Creates the directory structure for the utility repository.
     *
     * @throws IOException
     */
    public void initialize() throws IOException {
        this.repoPath.toFile().mkdirs();
        this.repoPath.resolve(PKG_PATH).toFile().mkdirs();
        this.repoPath.resolve(BIN_PATH).toFile().mkdirs();
        this.repoPath.resolve(CONFIG_PATH).toFile().mkdirs();
        this.repoPath.resolve(JDK_PATH).toFile().mkdirs();
    }

    public Path getPkgPath() {
        return this.repoPath.resolve(PKG_PATH);
    }

    public Path getConfigPath() {
        return this.repoPath.resolve(CONFIG_PATH);
    }

    public Path getBinPath() {
        return this.repoPath.resolve(BIN_PATH);
    }

    public Path getScriptPath(String packageName) {
        return this.getBinPath().resolve(packageName);
    }

    public Path getJarPath(String packageName) {
        return this.getBinPath().resolve(packageName + ".jar");
    }

    public Path getJdkPath() {
        return this.repoPath.resolve(JDK_PATH);
    }

    public Path getPathToJdk(String version) {
        return this.getJdkPath().resolve(version);
    }

    public boolean isJdkInstalled(String version) {
        return this.getPathToJdk(version).toFile().isDirectory();
    }

    /**
     * Generates a script based on the package configuration given, and saves it
     * to the right place.
     *
     * @param packageConfig Configuration of the package related to the script
     * to be saved.
     * @throws IOException If there's any error while writing to the location of
     * the script.
     */
    public void saveScript(PackageConfiguration packageConfig) throws IOException {
        this.saveScript(packageConfig.getName(), generateScript(new ScriptGeneratorConfig("1.0", packageConfig.getJdkVersion(), JDK_PATH)));
    }

    /**
     * Writes the passed contents to the path of the script that gets generated
     * based on the package name.
     *
     * @param packageName Name of the package.
     * @param contents Contents of the script.
     * @throws IOException If there's any error while writing to the location of
     * the script.
     */
    private void saveScript(String packageName, String contents) throws IOException {
        Path scriptPath = this.getScriptPath(packageName);
        Files.writeString(scriptPath, contents);

        // +x on unix
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
            Set<PosixFilePermission> scriptPerms = PosixFilePermissions.fromString("rwxr-xr-x");
            Files.setPosixFilePermissions(scriptPath, scriptPerms);
        }
    }

    public void saveJar(PackageConfiguration packageConfig, Path originalJarPath) throws IOException {
        Path jarPath = this.getJarPath(packageConfig.getName());

        Files.copy(originalJarPath, jarPath);
    }
}
