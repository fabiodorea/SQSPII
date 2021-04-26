package com.sinqia.sqspii.enums;

public enum ErrorCode {

    BAD_REQUEST("Um ou mais parâmetros está em formato incorreto."),
    UNEXPECTED_ERROR("Um erro inesperado ocorreu, por favor, verifique os logs."),
    SERVICE_UNAVAILABLE("O serviço está indisponível no momento, tente novamente mais tarde."),
    GATEWAY_TIMEOUT("Gateway timed out."),

    //Spring errors
    INVALID_PARAMETER("Um ou mais parâmetros são inválidos"),
    INVALID_PATH_VARIABLE_TYPE("O tipo de dado da variavel do path é inválido"),
    VALIDATION_FAILED_FOR_PARAMETER("Um ou mais parâmetros são inválidos"),
    MISSING_PARAMETER("Parametro obrigatório não informado"),
    METHOD_NOT_SUPPORTED("Não é possível usar esta ação neste endpoint"),
    AUTHORIZED_USER_NOT_FOUND_EXCEPTION("Não foi possível obter informações do usuário autorizado."),
    AUTHORIZED_USER_WITHOUT_PERMISSION_EXCEPTION("Usuário sem permissão para execução."),

    UNABLE_TO_GENERATE_QRCODE_IMAGE("Falha ao criar um qrcode válido."),
    CHARACTER_LIMIT_EXCEEDED("O campo ultrapassou o limite de caracteres."),
    INVALID_FIELD_VALUE("O campo não possui um valor válido."),
    INVALID_QRCODE_STRING("A string fornecida para decodificação é inválida."),
    UNABLE_TO_DECODE_QR_CODE("Não foi possível decodificar o qrcode.");

    private String error;

    ErrorCode(String error) {
        this.error = error;
    }

    public String getMessage() {
        return this.error;
    }

}

