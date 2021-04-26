package com.sinqia.sqspii.factory;

import com.sinqia.sqspii.domain.QrCodeField;
import com.sinqia.sqspii.domain.StaticQrCodeData;
import com.sinqia.sqspii.exception.CharacterLimitExceededException;
import com.sinqia.sqspii.request.StaticQrCodeRequest;

import org.apache.logging.log4j.util.Strings;

import java.text.NumberFormat;
import java.util.Locale;

public class StaticQRCodeBuilderFactory {

    private static NumberFormat format = NumberFormat.getNumberInstance(Locale.US);

    static {
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        format.setMinimumIntegerDigits(1);
        format.setGroupingUsed(false);
    }

    private static final StaticQRCodeBuilderFactory INSTANCE = new StaticQRCodeBuilderFactory();

    public static StaticQRCodeBuilderFactory getInstance() {
        return INSTANCE;
    }

    public String buildQRCodeString(StaticQrCodeRequest request) {
        StringBuilder code = new StringBuilder();
        StaticQrCodeData qrCodeData = new StaticQrCodeData();

        if (request.getFinancialValue() != null) {
            String financialValue = format.format(request.getFinancialValue());
            qrCodeData.getTransactionAmount().setValue(format.format(request.getFinancialValue()));
            qrCodeData.getTransactionAmount().setSize(financialValue.length());
        }

        qrCodeData.getMerchantCity().setValue(request.getCity());
        qrCodeData.getMerchantCity().setSize(request.getCity().length());

        qrCodeData.getMerchantName().setValue(request.getMerchantName());
        qrCodeData.getMerchantName().setSize(request.getMerchantName().length());

        return code
                .append(qrCodeData.getPayloadFormatIndicator().toString())
                .append(qrCodeData.getPointOfInitiationMethod().toString())
                .append(merchantAccountInfoCalculate(qrCodeData, request))
                .append(qrCodeData.getMerchantCategoryCode().toString())
                .append(qrCodeData.getTransactionCurrency().toString())
                .append(qrCodeData.getTransactionAmount().getSize() > 0 ? qrCodeData.getTransactionAmount().toString() : null)
                .append(qrCodeData.getCountryCode().toString())
                .append(qrCodeData.getMerchantName().toString())
                .append(qrCodeData.getMerchantCity().toString())
                .append(additionalDataFieldCalculate(qrCodeData, request))
                .append(qrCodeData.getCrc16().toString())
                .toString();
    }

    private String additionalDataFieldCalculate(StaticQrCodeData qrCodeData, StaticQrCodeRequest request) {
        QrCodeField additionalDataField = qrCodeData.getAdditionalDataField();
        QrCodeField txId = qrCodeData.getTxId();

        if (!Strings.isEmpty(request.getTransactionIdentifier())) {
            txId.setValue(request.getTransactionIdentifier());
            txId.setSize(request.getTransactionIdentifier().length());

            additionalDataField.setValue(txId.toString());
            additionalDataField.setSize(txId.toString().length());
            return additionalDataField.toString();
        }
        return "";
    }

    private String merchantAccountInfoCalculate(StaticQrCodeData qrCodeData, StaticQrCodeRequest request) {
        QrCodeField merchantInfo = qrCodeData.getMerchantAccountInformation();
        QrCodeField gui = qrCodeData.getGui();
        QrCodeField key = qrCodeData.getKey();
        QrCodeField additionalInfo = qrCodeData.getAdditionalInfo();

        key.setValue(request.getKey());
        key.setSize(request.getKey().length());

        merchantInfo.setValue(gui.toString() + key.toString());
        merchantInfo.setSize(key.toString().length() + gui.toString().length());

        if (!Strings.isEmpty(request.getAdicionalInformation())) {
            int validAdditionalInfoLength = 99 - (key.getSize() + gui.getSize() + 8);

            if (request.getAdicionalInformation().length() > validAdditionalInfoLength)
                throw new CharacterLimitExceededException("O campo 'informação adicional' ultrapassou o limite permitido.");

            additionalInfo.setValue(request.getAdicionalInformation());
            additionalInfo.setSize(request.getAdicionalInformation().length());

            merchantInfo.setValue(gui.toString() + key.toString() + additionalInfo.toString());
            merchantInfo.setSize(key.toString().length() + gui.toString().length() + additionalInfo.toString().length());
        }

        return merchantInfo.toString();
    }

    public StaticQrCodeData buildDecodeQrCodeResponse(String message) {

        StaticQrCodeData data = new StaticQrCodeData();

        data.getPayloadFormatIndicator().setSize(getFieldSize(message, 0));
        data.getPayloadFormatIndicator().setValue(getFieldValue(message, 0, data.getPayloadFormatIndicator().getSize()));
        data.getPayloadFormatIndicator().setEndPosition(data.getPayloadFormatIndicator().getFullSize());

        data.getPointOfInitiationMethod().setSize(getFieldSize(message, data.getPayloadFormatIndicator().getEndPosition()));
        data.getPointOfInitiationMethod().setValue(getFieldValue(message, data.getPayloadFormatIndicator().getEndPosition(), data.getPointOfInitiationMethod().getSize()));
        data.getPointOfInitiationMethod().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize());

        data.getMerchantAccountInformation().setSize(getFieldSize(message, data.getPointOfInitiationMethod().getEndPosition()));
        data.getMerchantAccountInformation().setValue(getFieldValue(message, data.getPointOfInitiationMethod().getEndPosition(), data.getMerchantAccountInformation().getSize()));
        data.getMerchantAccountInformation().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize() + data.getMerchantAccountInformation().getFullSize());
        data.getMerchantAccountInformation().setInitPosition(data.getPointOfInitiationMethod().getEndPosition() + 4);

        data.getGui().setSize(getFieldSize(message, data.getMerchantAccountInformation().getInitPosition()));
        data.getGui().setValue(getFieldValue(message, data.getMerchantAccountInformation().getInitPosition(), data.getGui().getSize()));
        data.getGui().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize() + 4 + data.getGui().getFullSize());

        data.getKey().setSize(getFieldSize(message, data.getGui().getEndPosition()));
        data.getKey().setValue(getFieldValue(message, data.getGui().getEndPosition(), data.getKey().getSize()));
        data.getKey().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize() + 4 + data.getGui().getFullSize() + data.getKey().getFullSize());

        if (data.getMerchantAccountInformation().getEndPosition() > data.getKey().getEndPosition()) {
            data.getAdditionalInfo().setSize(getFieldSize(message, data.getKey().getEndPosition()));
            data.getAdditionalInfo().setValue(getFieldValue(message, data.getKey().getEndPosition(), data.getAdditionalInfo().getSize()));
        }

        data.getMerchantCategoryCode().setSize(getFieldSize(message, data.getMerchantAccountInformation().getEndPosition()));
        data.getMerchantCategoryCode().setValue(getFieldValue(message, data.getMerchantAccountInformation().getEndPosition(), data.getMerchantCategoryCode().getSize()));
        data.getMerchantCategoryCode().setEndPosition(data.getPayloadFormatIndicator().getFullSize()
                + data.getPointOfInitiationMethod().getFullSize() +
                data.getMerchantAccountInformation().getFullSize()
                + data.getMerchantCategoryCode().getFullSize());

        data.getTransactionCurrency().setSize(getFieldSize(message, data.getMerchantCategoryCode().getEndPosition()));
        data.getTransactionCurrency().setValue(getFieldValue(message, data.getMerchantCategoryCode().getEndPosition(), data.getTransactionCurrency().getSize()));
        data.getTransactionCurrency().setEndPosition(data.getPayloadFormatIndicator().getFullSize()
                + data.getPointOfInitiationMethod().getFullSize()
                + data.getMerchantAccountInformation().getFullSize()
                + data.getMerchantCategoryCode().getFullSize()
                + data.getTransactionCurrency().getFullSize());

        data.getTransactionAmount().setSize(getFieldSize(message, data.getTransactionCurrency().getEndPosition()));
        data.getTransactionAmount().setValue(getFieldValue(message, data.getTransactionCurrency().getEndPosition(), data.getTransactionAmount().getSize()));
        data.getTransactionAmount().setEndPosition(data.getPayloadFormatIndicator().getFullSize()
                + data.getPointOfInitiationMethod().getFullSize()
                + data.getMerchantAccountInformation().getFullSize()
                + data.getMerchantCategoryCode().getFullSize()
                + data.getTransactionCurrency().getFullSize() + data.getTransactionAmount().getFullSize());

        data.getCountryCode().setSize(getFieldSize(message, data.getTransactionAmount().getEndPosition()));
        data.getCountryCode().setValue(getFieldValue(message, data.getTransactionAmount().getEndPosition(), data.getCountryCode().getSize()));
        data.getCountryCode().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize()
                + data.getMerchantAccountInformation().getFullSize() + data.getMerchantCategoryCode().getFullSize()
                + data.getTransactionCurrency().getFullSize() + data.getTransactionAmount().getFullSize() + data.getCountryCode().getFullSize());

        data.getMerchantName().setSize(getFieldSize(message, data.getCountryCode().getEndPosition()));
        data.getMerchantName().setValue(getFieldValue(message, data.getCountryCode().getEndPosition(), data.getMerchantName().getSize()));
        data.getMerchantName().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize()
                + data.getMerchantAccountInformation().getFullSize() + data.getMerchantCategoryCode().getFullSize()
                + data.getTransactionCurrency().getFullSize() + data.getTransactionAmount().getFullSize() + data.getCountryCode().getFullSize() + data.getMerchantName().getFullSize());

        data.getMerchantCity().setSize(getFieldSize(message, data.getMerchantName().getEndPosition()));
        data.getMerchantCity().setValue(getFieldValue(message, data.getMerchantName().getEndPosition(), data.getMerchantCity().getSize()));
        data.getMerchantCity().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize()
                + data.getMerchantAccountInformation().getFullSize() + data.getMerchantCategoryCode().getFullSize()
                + data.getTransactionCurrency().getFullSize() + data.getTransactionAmount().getFullSize() + data.getCountryCode().getFullSize() + data.getMerchantName().getFullSize()
                + data.getMerchantCity().getFullSize());

        boolean hasAdditionalDataField = hasAdditionalField(message, data.getMerchantCity().getEndPosition());

        if (hasAdditionalDataField) {
            data.getAdditionalDataField().setSize(getFieldSize(message, data.getMerchantCity().getEndPosition()));
            data.getAdditionalDataField().setValue(getFieldValue(message, data.getMerchantCity().getEndPosition(), data.getAdditionalDataField().getSize()));
            data.getAdditionalDataField().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize()
                    + data.getMerchantAccountInformation().getFullSize() + data.getMerchantCategoryCode().getFullSize()
                    + data.getTransactionCurrency().getFullSize() + data.getTransactionAmount().getFullSize() + data.getCountryCode().getFullSize() + data.getMerchantName().getFullSize()
                    + data.getMerchantCity().getFullSize() + data.getAdditionalDataField().getFullSize());
            data.getAdditionalDataField().setInitPosition(data.getMerchantCity().getEndPosition() + 4);

            data.getTxId().setSize(getFieldSize(message, data.getAdditionalDataField().getInitPosition()));
            data.getTxId().setValue(getFieldValue(message, data.getAdditionalDataField().getInitPosition(), data.getTxId().getSize()));
            data.getTxId().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize()
                    + data.getMerchantAccountInformation().getFullSize() + data.getMerchantCategoryCode().getFullSize()
                    + data.getTransactionCurrency().getFullSize() + data.getTransactionAmount().getFullSize() + data.getCountryCode().getFullSize() + data.getMerchantName().getFullSize()
                    + data.getMerchantCity().getFullSize() + data.getAdditionalDataField().getFullSize() + data.getTxId().getFullSize());
        }

        data.getCrc16().setSize(getFieldSize(message, data.getAdditionalDataField().getEndPosition()));
        data.getCrc16().setValue(getFieldValue(message, data.getAdditionalDataField().getEndPosition(), data.getCrc16().getSize()));

        return data;
    }

    private static boolean hasAdditionalField(String message, int position) {
        String id = message.substring(position, position + 2);
        if ("62".equalsIgnoreCase(id))
            return true;
        return false;
    }

    private static int getFieldSize(String message, int initialPosition) {
        return Integer.parseInt(message.substring(initialPosition + 2, initialPosition + 4));
    }

    public static String getFieldValue(String message, int initialPosition, int size) {
        return message.substring(initialPosition + 4, initialPosition + 4 + size);
    }

}
