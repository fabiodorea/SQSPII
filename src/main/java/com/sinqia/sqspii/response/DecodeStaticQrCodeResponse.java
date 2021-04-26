package com.sinqia.sqspii.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DecodeStaticQrCodeResponse extends DecodeQrCodeResponse {

    private Double value;
    private String additionalInformation;
}
