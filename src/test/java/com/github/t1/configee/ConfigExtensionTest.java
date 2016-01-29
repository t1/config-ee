package com.github.t1.configee;

import static org.assertj.core.api.Assertions.*;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import java.math.BigDecimal;

@RunWith(Arquillian.class)
public class ConfigExtensionTest {
    @Deployment
    public static JavaArchive createArquillianDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(ConfigValue.class, ConfigCdiExtension.class)
                .addAsServiceProvider(Extension.class, ConfigCdiExtension.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    public static class Configured {
        private ConfigValue<String> stringConfig;
        private ConfigValue<Boolean> booleanConfig;
        private ConfigValue<Integer> intConfig;
        private ConfigValue<BigDecimal> bigDecimalConfig;
        private ConfigValue<ComplexConfiguration> complexConfig;
    }

    @Inject
    Configured configured;

    @Inject
    Configured configured2;

    public static class ConfiguredX {
        private ConfigValue<String> booleanConfig;
    }

    @Inject
    ConfiguredX configuredX;

    @Test
    public void shouldConfigureString() {
        assertThat(configured.stringConfig).isNotNull();
        assertThat(configured.stringConfig.get()).isEqualTo("configured string");
    }

    @Test
    public void shouldConfigureBoolean() {
        assertThat(configured.booleanConfig).isNotNull();
        assertThat(configured.booleanConfig.get()).isTrue();
    }

    @Test
    public void shouldConfigureInt() {
        assertThat(configured.intConfig).isNotNull();
        assertThat(configured.intConfig.get()).isEqualTo(123);
    }

    @Test
    public void shouldConfigureBigDecimal() {
        assertThat(configured.bigDecimalConfig).isNotNull();
        assertThat(configured.bigDecimalConfig.get()).isEqualTo("123.456");
    }

    @Test
    public void shouldConfigureComplex() {
        Converter.CONVERT.register(ComplexConfiguration.class, new ComplexConfiguration.Converter());
        assertThat(configured.complexConfig).isNotNull();
        assertThat(configured.complexConfig.get()).isEqualTo(new ComplexConfiguration("a", "b"));
    }

    @Test
    public void shouldConfigure2() {
        assertThat(configured2.stringConfig).isNotNull();
        assertThat(configured2.stringConfig.get()).isEqualTo("configured string");
    }

    @Test
    public void shouldConfigureX() {
        Throwable thrown = catchThrowable(() -> configuredX.booleanConfig.get());

        assertThat(thrown).isInstanceOf(ClassCastException.class)
                .hasMessage("Cannot cast java.lang.Boolean to java.lang.String");
    }

}
