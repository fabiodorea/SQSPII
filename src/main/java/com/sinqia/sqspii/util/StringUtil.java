package com.sinqia.sqspii.util;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class StringUtil {

    public static String extractDigits(String in) {
        if (in == null)
            return null;

        if (isBlank(in))
            return "";

        char caracteres[] = in.toCharArray();
        StringBuilder ret = new StringBuilder();
        for ( int i=0; i < in.length(); i++ ){
            if (caracteres[i] >= '0' && caracteres[i] <= '9')
                ret.append(caracteres[i]);
        }
        return ret.toString();
    }
    
}
