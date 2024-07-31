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
}
