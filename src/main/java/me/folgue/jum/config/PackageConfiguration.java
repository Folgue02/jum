package me.folgue.jum.config;

import java.io.IOException;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageConfiguration {

    private String name;
    private String description;
    private String jdkVersion;
    private String author;
    private String version;

    public static PackageConfiguration fromString(String configStr) throws IOException {
        var opts = new LoaderOptions();
        Yaml yaml = new Yaml(new Constructor(PackageConfiguration.class, new LoaderOptions()));
        PackageConfiguration pkgConfig = yaml.load(configStr);

        Objects.requireNonNull(pkgConfig, "Invalid package configuration");
        return pkgConfig;
    }
}
