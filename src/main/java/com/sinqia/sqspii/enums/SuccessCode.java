package com.sinqia.sqspii.enums;

public enum SuccessCode {

    QR_CODE_GENERATED("1", "QRCode gerado com sucesso"),
    PAYLOAD_GENERATED("2", "Payload gerado com sucesso"),
    PAYLOAD_UPDATED("3", "Payload alterado com sucesso"),
    QR_CODE_DECODE("4", "QrCode String decodificada com sucesso");

    private String code;
    private String message;

    SuccessCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
