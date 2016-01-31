package com.github.t1.configee;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class Configuration {
    private String stringConfig;
    private boolean booleanConfig;
    private int intConfig;
    private BigDecimal bigDecimalConfig;
    private ComplexConfiguration complexConfig;
}
