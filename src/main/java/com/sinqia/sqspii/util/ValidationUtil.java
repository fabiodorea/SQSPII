package com.sinqia.sqspii.util;

public class ValidationUtil {

    public static boolean isValidCPF(String cpf) {

        if (cpf == null)
            return false;

        cpf = StringUtil.extractDigits(cpf);

        if (cpf.trim().length() != 11)
            return false;

        if (cpf.equals("11111111111")
                || cpf.equals("22222222222")
                || cpf.equals("33333333333")
                || cpf.equals("44444444444")
                || cpf.equals("55555555555")
                || cpf.equals("66666666666")
                || cpf.equals("77777777777")
                || cpf.equals("88888888888")
                || cpf.equals("99999999999")
                || cpf.equals("00000000000")) {
            return false;
        }

        return validateDvCPF(cpf);

    }

    public static boolean isValidCNPJ(String cnpj) {

        if (cnpj == null)
            return false;

        cnpj = StringUtil.extractDigits(cnpj);

        if (cnpj.length() != 14)
            return false;

        return validateDvCNPJ(cnpj);
    }

    private static boolean validateDvCPF(String cpf) {

        int sum1, sum2, dv1, dv2, aux;
        sum1 = 0;
        sum2 = 0;

        for (aux = 0; aux < 9; aux++) {
            sum1 += (10 - aux) * (Integer.parseInt(cpf.substring(aux, aux + 1)));
            sum2 += (11 - aux) * (Integer.parseInt(cpf.substring(aux, aux + 1)));
        }

        dv1 = 11 - (sum1 % 11);
        sum2 += 2 * (Integer.parseInt(cpf.substring(9, 10)));
        dv2 = 11 - (sum2 % 11);

        if ((sum1 % 11) < 2)
            dv1 = 0;
        if ((sum2 % 11) < 2)
            dv2 = 0;

        if ((Integer.parseInt(cpf.substring(9, 10)) != dv1) ||
                (Integer.parseInt(cpf.substring(10, 11)) != dv2)) {
            return false;
        }
        return true;
    }

    private static boolean validateDvCNPJ(String cnpj) {

        int aux, dv1 = 0, dv2 = 0;
        String strMult1 = "543298765432";
        String strMult2 = "6543298765432";

        for (aux = 0; aux <= 11; aux++) {
            dv1 += (Integer.parseInt(cnpj.substring(aux, aux + 1))
                    * Integer.parseInt(strMult1.substring(aux, aux + 1)));
        }

        for (aux = 0; aux <= 12; aux++) {
            dv2 += (Integer.parseInt(cnpj.substring(aux, aux + 1))
                    * Integer.parseInt(strMult2.substring(aux, aux + 1)));
        }
        dv1 = (dv1 * 10) % 11;
        dv2 = (dv2 * 10) % 11;

        if (dv1 == 10)
            dv1 = 0;
        if (dv2 == 10)
            dv2 = 0;

        if (dv1 != Integer.parseInt(cnpj.substring(12, 13)))
            return false;
        if (dv2 != Integer.parseInt(cnpj.substring(13, 14)))
            return false;

        return true;
    }

    public static boolean isValidMobileNumber(String mobileNumber) {

        mobileNumber = StringUtil.extractDigits(mobileNumber);

        if (mobileNumber.length() != 11 && mobileNumber.length() != 10) {

            return false;
        }

        if (!mobileNumber.substring(0, 2).matches(
                "(([1,4,6,8,9][1-9])|(2[1,2,4,7,8])|(3[1-8])|(4[1-9])|(5[1-5])|(7[1,3,4,5,7,9]))")) {

            return false;
        }

        return true;
    }
    
}
