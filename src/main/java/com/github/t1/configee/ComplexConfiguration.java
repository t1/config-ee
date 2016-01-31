package com.github.t1.configee;

import java.util.regex.*;

import org.joda.convert.TypedStringConverter;

public class ComplexConfiguration {
    public static class Converter implements TypedStringConverter<ComplexConfiguration> {
        @Override
        public String convertToString(ComplexConfiguration object) {
            return "{one=" + object.getOne() + ",two=" + object.getTwo() + "}";
        }

        @Override
        public ComplexConfiguration convertFromString(Class<? extends ComplexConfiguration> cls,
                String str) {
            Pattern pattern = Pattern.compile("\\{one=(.*),two=(.*)\\}");
            Matcher matcher = pattern.matcher(str);
            if (!matcher.matches())
                throw new IllegalStateException(
                        ""
                );
            return new ComplexConfiguration(matcher.group(1), matcher.group(2));
        }

        @Override
        public Class<?> getEffectiveType() {
            return ComplexConfiguration.class;
        }
    }

    private String one, two;

    @SuppressWarnings("unused")
    private ComplexConfiguration() {
    }

    public ComplexConfiguration(String one, String two) {
        this.one = one;
        this.two = two;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((one == null) ? 0 : one.hashCode());
        result = prime * result + ((two == null) ? 0 : two.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ComplexConfiguration other = (ComplexConfiguration) obj;
        if (one == null) {
            if (other.one != null)
                return false;
        } else if (!one.equals(other.one))
            return false;
        if (two == null) {
            if (other.two != null)
                return false;
        } else if (!two.equals(other.two))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ComplexConfiguration [one=" + one + ", two=" + two + "]";
    }

}
