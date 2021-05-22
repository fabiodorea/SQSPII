package com.sinqia.sqspii.factory;


import com.sinqia.sqspii.data.multitenancy.entity.AdditionalInformation;
import com.sinqia.sqspii.request.DynamicQrCodeRequest;

import java.util.ArrayList;
import java.util.List;

public class AdditionalInformationFactory {

    private static final AdditionalInformationFactory INSTANCE = new AdditionalInformationFactory();

    public static AdditionalInformationFactory getInstance(){
       return INSTANCE;
    }

    private AdditionalInformationFactory() {
    }

    public DynamicQrCodeRequest.AdditionalInformationRequest buildAdditionalInformationResponse(AdditionalInformation additionalInformation) {
        return DynamicQrCodeRequest.AdditionalInformationRequest.builder()
                .name(additionalInformation.getName())
                .value(additionalInformation.getValue())
                .build();
    }

    public List<DynamicQrCodeRequest.AdditionalInformationRequest> buildAdditionalInformationResponseList(List<AdditionalInformation> additionalInformation) {
        List<DynamicQrCodeRequest.AdditionalInformationRequest> list = new ArrayList<>();

        for (AdditionalInformation information : additionalInformation) {
            list.add(buildAdditionalInformationResponse(information));
        }

        return list;
    }
}
