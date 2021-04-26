package com.sinqia.sqspii.response;

import java.util.UUID;

import lombok.Data;

@Data
public class DynamicQrCodeResponse {

    private UUID documentIdentifier;
    private String textualContent;
    private String generatedImage;

}
