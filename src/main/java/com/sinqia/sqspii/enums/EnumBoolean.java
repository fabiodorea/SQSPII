package com.sinqia.sqspii.enums;

import java.util.Arrays;
import java.util.List;

public enum EnumBoolean {

    N,
    S;

    public String getKey() {
        return String.valueOf(this.ordinal());
    }

    public String getName() {
        String ret = null;
        switch (this) {
        case N:
            ret = "Não";
            break;
        case S:
            ret = "Sim";
            break;
        }
        return ret;
    }

    public static List<EnumBoolean> getList() {
        return Arrays.asList(EnumBoolean.values());
    }

}
