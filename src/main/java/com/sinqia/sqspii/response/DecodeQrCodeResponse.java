package com.sinqia.sqspii.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DecodeQrCodeResponse {

    private String qrCodeType;
    private String key;
    private String merchantName;
    private String city;
    private String transactionIdentifier;
}
