package com.sinqia.sqspii.controller;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinqia.sqspii.entity.DynamicQrCode;
import com.sinqia.sqspii.enums.SuccessCode;
import com.sinqia.sqspii.request.DecodeQrCodeRequest;
import com.sinqia.sqspii.request.DynamicQrCodeRequest;
import com.sinqia.sqspii.request.DynamicQrCodeUpdateRequest;
import com.sinqia.sqspii.request.StaticQrCodeRequest;
import com.sinqia.sqspii.response.DecodeQrCodeResponse;
import com.sinqia.sqspii.response.DefaultResponse;
import com.sinqia.sqspii.response.DynamicQrCodeResponse;
import com.sinqia.sqspii.response.ErrorResponse;
import com.sinqia.sqspii.response.StaticQrCodeResponse;
import com.sinqia.sqspii.response.SuccessResponse;
import com.sinqia.sqspii.service.DynamicQrCodeService;
import com.sinqia.sqspii.service.StaticQrCodeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/v1")
@ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(responseCode = "500", description = "INTERNAL_SERVER_ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public class QrCodeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(QrCodeController.class);

    @Autowired
    private StaticQrCodeService staticQrCodeService;

    @Autowired
    private DynamicQrCodeService dynamicQrCodeService;

    //@RolesAllowed("pix") //TODO bloquear endpoints com base nas roles do keycloak
    @PostMapping(path = "/pix/static", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Gerar QrCode estático", description = "Endpoint responsável pela geração de um novo qrcode estático a partir das informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(schema = @Schema(implementation = DynamicQrCodeResponse.class)))
    public ResponseEntity<DefaultResponse> staticQrCode(@Valid @RequestBody StaticQrCodeRequest request) {

        StaticQrCodeResponse response = staticQrCodeService.generate(request);
        return ResponseEntity.ok(SuccessResponse.builder()
                .code(SuccessCode.QR_CODE_GENERATED.getCode())
                .message(SuccessCode.QR_CODE_GENERATED.getMessage())
                .body(response)
                .build());
    }

    @PostMapping(path = "/pix/dynamic", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Gerar QrCode dinâmico", description = "Endpoint responsável pela geração de um novo qrcode dinâmico a partir das informações fornecidas.")
    @ApiResponse(responseCode = "201", description = "CREATED", content = @Content(schema = @Schema(implementation = DynamicQrCodeResponse.class)))
    public ResponseEntity<DefaultResponse> dynamicQrCode(@Valid @RequestBody DynamicQrCodeRequest request) {
        DynamicQrCodeResponse response = dynamicQrCodeService.generate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder()
                .code(SuccessCode.QR_CODE_GENERATED.getCode())
                .message(SuccessCode.QR_CODE_GENERATED.getMessage())
                .body(response)
                .build());
    }

    @GetMapping(path = "/public/pix/{tenant}/{document-id}", produces = "application/json")
    @Operation(summary = "Consulta QrCode dinâmico", description = "Endpoint responsável pela consulta de um qrcode dinâmico a partir do tenant e do codigo.")
    public ResponseEntity<DefaultResponse> getPayload(@PathVariable("document-id") String documentId, @PathVariable("tenant") String tenant) throws Exception {
        String code = dynamicQrCodeService.createPayloadByIdentifier(documentId, tenant);
        return ResponseEntity.ok(SuccessResponse.builder()
                .code(SuccessCode.PAYLOAD_GENERATED.getCode())
                .message(SuccessCode.PAYLOAD_GENERATED.getMessage())
                .body(code)
                .build());
    }

    @PostMapping(path = "/qr-code/decode", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Decodificar QrCode", description = "Endpoint responsável por decodificar um qrcode.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = DecodeQrCodeRequest.class)))
    public ResponseEntity<DefaultResponse> decodeQrCode(@RequestBody DecodeQrCodeRequest request) throws Exception {
        DecodeQrCodeResponse response = dynamicQrCodeService.decodeQrCode(request);
        return ResponseEntity.ok(SuccessResponse.builder()
                .code(SuccessCode.QR_CODE_DECODE.getCode())
                .message(SuccessCode.QR_CODE_DECODE.getMessage())
                .body(response)
                .build());
    }

    @PostMapping(path = "/qr-code/update/{documentId}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Alterar QrCode dinâmico", description = "Endpoint responsável pela alteração de um qrcode dinâmico existente.")
    @ApiResponse(responseCode = "201", description = "UPDATED", content = @Content(schema = @Schema(implementation = DynamicQrCodeResponse.class)))
    public ResponseEntity<DefaultResponse> updateDynamicQrCode(
            @PathVariable String documentId,
            @Valid @RequestBody DynamicQrCodeUpdateRequest request) {
        DynamicQrCodeResponse code = dynamicQrCodeService.update(documentId, request);
        return ResponseEntity.ok(SuccessResponse.builder()
                .code(SuccessCode.PAYLOAD_UPDATED.getCode())
                .message(SuccessCode.PAYLOAD_UPDATED.getMessage())
                .body(code)
                .build());
    }

    @GetMapping(path = "/qr-code/entity/{documentId}", consumes = "application/json", produces = "application/json")
    @Operation(summary = "Consulta QrCode dinâmico", description = "Endpoint responsável por consultar um qrcode dinâmico existente.")
    public ResponseEntity<DefaultResponse> getEntity(@PathVariable String documentId) {
        DynamicQrCode code = dynamicQrCodeService.findByIdentifier(documentId);
        return ResponseEntity.ok(SuccessResponse.builder()
                .code(SuccessCode.PAYLOAD_GENERATED.getCode())
                .message(SuccessCode.PAYLOAD_GENERATED.getMessage())
                .body(code)
                .build());
    }

}
