package com.sinqia.sqspii.response;

import com.sinqia.sqspii.request.DynamicQrCodeRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DecodeDynamicQrCodeResponse extends DecodeQrCodeResponse{

    private String payloadUrl;
    private Long review;
    private DynamicQrCodeRequest.Calendar calendar;
    private DynamicQrCodeRequest.Debtor debtor;
    private DynamicQrCodeRequest.Value value;
    private String payerRequest;
    private List<DynamicQrCodeRequest.AdditionalInformationRequest> additionalInformations;
}
