package com.sinqia.sqspii.service;

import com.sinqia.sqspii.exception.UnableToGenerateQRCodeImageException;
import com.sinqia.sqspii.factory.StaticQRCodeBuilderFactory;
import com.sinqia.sqspii.request.StaticQrCodeRequest;
import com.sinqia.sqspii.response.StaticQrCodeResponse;
import com.sinqia.sqspii.util.ImageUtil;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StaticQrCodeService extends ServiceBase {

    public StaticQrCodeResponse generate(StaticQrCodeRequest request) {

        String code = StaticQRCodeBuilderFactory.getInstance().buildQRCodeString(request);
        StaticQrCodeResponse response = new StaticQrCodeResponse();

        try {
            String base64Image = ImageUtil.generateQRCodeImage(code);
            response.setGeneratedImage(base64Image);
            response.setTextualContent(code);
            return  response;
        } catch (IOException e) {
            throw new UnableToGenerateQRCodeImageException();
        }
    }
}
