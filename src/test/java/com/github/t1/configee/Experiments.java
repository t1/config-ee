package com.github.t1.configee;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.file.*;

import org.junit.*;
import org.yaml.snakeyaml.Yaml;

public class Experiments {
    private static final Path PATH = Paths.get("src/test/resources/config.yaml");

    Yaml yaml = new Yaml();

    Configuration config = new Configuration();

    @Before
    public void setup() {
        config.setStringConfig("configured string");
        config.setBooleanConfig(true);
        config.setIntConfig(123);
        config.setBigDecimalConfig(new BigDecimal("123.456"));
        config.setComplexConfig(new ComplexConfiguration("a", "b"));
    }

    @Test
    public void dump() {
        String dump = yaml.dump(config);

        assertThat(PATH).hasContent(dump);
    }

    @Test
    public void load() throws Exception {
        Configuration config = yaml.loadAs(Files.newInputStream(PATH), Configuration.class);

        assertThat(config).isEqualTo(this.config);
    }
}
