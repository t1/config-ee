package com.github.t1.configee;

import static com.github.t1.configee.ConfigExtensionTest.ComplexConfiguration.*;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.regex.*;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.joda.convert.TypedStringConverter;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.*;

@RunWith(Arquillian.class)
public class ConfigExtensionTest {
    @Deployment
    public static JavaArchive createArquillianDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(ConfigValue.class, ConfigCdiExtension.class)
                .addAsServiceProvider(Extension.class, ConfigCdiExtension.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Value
    @Builder(builderMethodName = "complex")
    public static class ComplexConfiguration {
        String one, two;
    }

    @Data
    public static class Configured {
        private ConfigValue<String> stringConfig;
        private ConfigValue<Boolean> booleanConfig;
        private ConfigValue<Integer> intConfig;
        private ConfigValue<BigDecimal> bigDecimalConfig;
        private ConfigValue<ComplexConfiguration> complexConfig;
    }

    @Inject
    Configured configured;

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
        ConfigCdiExtension.CONVERT.register(ComplexConfiguration.class,
                new TypedStringConverter<ComplexConfiguration>() {
                    @Override
                    public String convertToString(ComplexConfiguration object) {
                        return "{one=" + object.getOne() + ",two=" + object.getTwo() + "}";
                    }

                    @Override
                    public ComplexConfiguration convertFromString(Class<? extends ComplexConfiguration> cls,
                            String str) {
                        Pattern pattern = Pattern.compile("\\{one=(.*),two=(.*)\\}");
                        Matcher matcher = pattern.matcher(str);
                        matcher.matches();
                        return complex()
                                .one(matcher.group(1))
                                .two(matcher.group(2))
                                .build();
                    }

                    @Override
                    public Class<?> getEffectiveType() {
                        return ComplexConfiguration.class;
                    }
                });
        assertThat(configured.complexConfig).isNotNull();
        assertThat(configured.complexConfig.get()).isEqualTo(complex().one("a").two("b").build());
    }
}
