package com.sinqia.sqspii.handler;

import com.sinqia.sqspii.enums.ErrorCode;
import com.sinqia.sqspii.exception.CharacterLimitExceededException;
import com.sinqia.sqspii.exception.InvalidQrCodeStringToDecodeException;
import com.sinqia.sqspii.exception.UnableToGenerateQRCodeImageException;
import com.sinqia.sqspii.factory.ErrorResponseFactory;
import com.sinqia.sqspii.response.ErrorResponse;
import com.sinqia.sqspii.response.SinqiaError;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class SQSPIIExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponseFactory.build(ErrorCode.METHOD_NOT_SUPPORTED));
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseFactory.build(ErrorCode.INVALID_PATH_VARIABLE_TYPE));
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntity
                .badRequest()
                .body(ErrorResponseFactory.build(ErrorCode.INVALID_PARAMETER));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponseFactory.build(ErrorCode.VALIDATION_FAILED_FOR_PARAMETER);

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorResponse.getBody().add(SinqiaError.builder()
            .code(ErrorCode.INVALID_FIELD_VALUE.toString())
            .field(error.getField())
            .message(error.getDefaultMessage())
            .solution("Preencha o campo corretamente.")
            .build());
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errorResponse.getBody().add(SinqiaError.builder()
                    .field(error.getObjectName())
                    .message(error.getDefaultMessage())
                    .solution(error.getCode())
                    .build());
        }

        return ResponseEntity
                .badRequest()
                .body(errorResponse);
    }

    @Override
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponseFactory.build(ErrorCode.MISSING_PARAMETER);
        errorResponse.getBody().add(SinqiaError.builder()
                .field(ex.getParameterName())
                .message(ex.getMessage())
                .solution("Informe o parâmetro obrigatório.")
                .build());

        return ResponseEntity
                .badRequest()
                .body(errorResponse);

    }

    // RuntimeException
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity handleUnexpectedException(RuntimeException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponseFactory.build(
                ErrorCode.UNEXPECTED_ERROR, e.getMessage())
        );
    }

    @ExceptionHandler(UnableToGenerateQRCodeImageException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleInvalidOccOrderException(UnableToGenerateQRCodeImageException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponseFactory.build(
                ErrorCode.UNABLE_TO_GENERATE_QRCODE_IMAGE, e.getMessage())
        );
    }

    @ExceptionHandler(InvalidQrCodeStringToDecodeException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleInvalidQrCodeStringToDecodeException(InvalidQrCodeStringToDecodeException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponseFactory.build(
                ErrorCode.INVALID_QRCODE_STRING, e.getMessage())
        );
    }

    @ExceptionHandler(CharacterLimitExceededException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleCharacterLimitExceededException(CharacterLimitExceededException e, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponseFactory.build(
                ErrorCode.CHARACTER_LIMIT_EXCEEDED, e.getMessage())
        );
    }
}
