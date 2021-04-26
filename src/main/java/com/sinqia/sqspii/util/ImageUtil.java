package com.sinqia.sqspii.util;

import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;

import com.sinqia.sqspii.exception.UnableToGenerateQRCodeImageException;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil {

    private ImageUtil() {
    }

    public static String generateQRCodeImage(String barcodeText) throws IOException {
        try (ByteArrayOutputStream stream = QRCode.from(barcodeText).withSize(250, 250).stream();
             ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

            BufferedImage img = ImageIO.read(bis);
            ImageIO.write(img, "jpg", bos);
            byte[] imageBytes = bos.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            byte[] imageString = encoder.encode(imageBytes);

            return new String(imageString);
        } catch (IOException e) {
            throw new UnableToGenerateQRCodeImageException();
        }
    }
}
