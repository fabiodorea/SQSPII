package com.sinqia.sqspii.enums;

import java.util.Arrays;
import java.util.List;

public enum EnumPayerType {

    F,
    J,
    N;

    public String getKey() {
        return String.valueOf(this.ordinal());
    }

    public String getName() {
        String ret = null;
        switch (this) {
        case F:
            ret = "Física";
            break;
        case J:
            ret = "Jurídica";
            break;
        case N:
            ret = "Não Informado";
            break;
        }
        return ret;
    }

    public static List<EnumPayerType> getList() {
        return Arrays.asList(EnumPayerType.values());
    }

}
