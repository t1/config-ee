package com.github.t1.configee;

import static com.github.t1.exap.reflection.ReflectionProcessingEnvironment.*;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static javax.tools.StandardLocation.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.github.t1.exap.Round;

@RunWith(MockitoJUnitRunner.class)
public class ConfigAnnotationProcessorTest {
    private static final String PACKAGE = ConfigAnnotationProcessorTest.class.getPackage().getName();

    @Mock
    Round round;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    ConfigAnnotationProcessor processor = new ConfigAnnotationProcessor();

    @After
    public void cleanup() {
        ENV.clearCreatedResource();
    }

    private void given(Class<?>... types) {
        when(round.fieldsAnnotatedWith(Config.class)).thenReturn(
                asList(types).stream().map(ENV::type).flatMap(t -> t.getAllFields().stream())
                        .filter(f -> f.isAnnotated(Config.class)).collect(toList()));
    }

    private List<String> createdResource() {
        String createdResource = ENV.getCreatedResource(SOURCE_OUTPUT, PACKAGE, "Configuration");
        assertThat(createdResource)
                .contains("package " + PACKAGE + ";")
                .contains("import lombok.Data;")
                .contains("@Data")
                .contains("public class Configuration {");
        return asList(createdResource.split("\n"));
    }

    @Test
    public void shouldGenerateEmptyConfig() {
        class Annotated {
            @SuppressWarnings("unused")
            String nonConfig;
        }
        given(Annotated.class);

        processor.process(round);

        assertThat(ENV.getCreatedResources()).isEmpty();
    }

    @Test
    public void shouldGenerateStringConfig() {
        class Annotated {
            @Config
            String stringConfig;
        }
        given(Annotated.class);

        processor.process(round);

        assertThat(createdResource()).contains("    private String stringConfig;");
    }

    @Test
    public void shouldGenerateBooleanConfig() {
        class Annotated {
            @Config
            boolean boolConfig;
        }
        given(Annotated.class);

        processor.process(round);

        assertThat(createdResource()).contains("    private boolean boolConfig;");
    }

    @Test
    public void shouldGenerateBigDecimalField() {
        class Annotated {
            @Config
            BigDecimal n;
        }
        given(Annotated.class);

        processor.process(round);

        assertThat(createdResource()).contains("    private BigDecimal n;");
    }

    @Test
    public void shouldGeneratePojoField() {
        class Annotated {
            @Config
            private ComplexConfiguration complexConfig;
        }
        given(Annotated.class);

        processor.process(round);

        assertThat(createdResource()).contains("    private ComplexConfiguration complexConfig;");
    }

    @Test
    public void shouldGenerateTwoConfigFields() {
        class Annotated {
            @Config
            String c1, c2;
        }
        given(Annotated.class);

        processor.process(round);

        assertThat(createdResource())
                .contains("    private String c1;")
                .contains("    private String c2;")
        ;
    }

    @Test
    public void shouldGenerateFullConfiguration() {
        class Annotated {
            @Config
            String c1, c2;
        }
        given(Annotated.class);

        processor.process(round);

        assertThat(createdResource())
                .contains("    private String c1;")
                .contains("    private String c2;")
        ;
    }
}
