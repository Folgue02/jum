package me.folgue.jum.config;

import com.fasterxml.jackson.dataformat.toml.TomlMapper;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageConfiguration {

    private String name;
    private String description;
    private String jdkVersion;
    private String author;
    private String version;

    public static PackageConfiguration fromTOMLString(String configStr) throws IOException {
        var mapper = new TomlMapper();
        return mapper.readValue(configStr, PackageConfiguration.class);
    }

    public String toTOML() {
        var mapper = new TomlMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            // ??? How does this fail ???
            throw new Error(e);
        }
    }

    @Override
    public String toString() {
        return """
               Name:          %s
               Description:   %s
               JDK version:   %s
               Version:       %s
               """.formatted(
                this.name,
                this.description,
                this.jdkVersion,
                this.version
        );
    }
}
