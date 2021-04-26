package com.sinqia.sqspii.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DecodeQrCodeRequest {

    @Schema(description = "Texto que representa o QrCode")
    @NotEmpty(message = "Campo 'qrCodeString' é obritagório")
    private String qrCodeString;
}
