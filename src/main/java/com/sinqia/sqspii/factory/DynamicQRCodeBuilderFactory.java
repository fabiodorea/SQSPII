package com.sinqia.sqspii.factory;

import com.sinqia.sqspii.context.TenantContext;
import com.sinqia.sqspii.domain.DynamicQrCodeData;
import com.sinqia.sqspii.domain.QrCodeField;
import com.sinqia.sqspii.entity.DynamicQrCode;

import com.sinqia.sqspii.entity.Parameter;
import com.sinqia.sqspii.enums.EnumBoolean;
import com.sinqia.sqspii.repository.ParameterRepository;
import com.sinqia.sqspii.request.DynamicQrCodeRequest;
import com.sinqia.sqspii.response.DecodeDynamicQrCodeResponse;
import com.sinqia.sqspii.util.ValidationUtil;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@Component
public class DynamicQRCodeBuilderFactory {

    private static NumberFormat format = NumberFormat.getNumberInstance(Locale.US);

    @Autowired
    private ParameterRepository parameterRepository;

    static {
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        format.setMinimumIntegerDigits(1);
        format.setGroupingUsed(false);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String buildQRCodeString(DynamicQrCode data) {
        StringBuilder code = new StringBuilder();
        DynamicQrCodeData qrCodeData = new DynamicQrCodeData();

        String financialValue = format.format(data.getFinalValue());
        qrCodeData.getTransactionAmount().setValue(format.format(data.getFinalValue()));
        qrCodeData.getTransactionAmount().setSize(financialValue.length());

        qrCodeData.getMerchantName().setValue(data.getReceiverName());
        qrCodeData.getMerchantName().setSize(data.getReceiverName().length());

        qrCodeData.getMerchantCity().setValue(data.getCity());
        qrCodeData.getMerchantCity().setSize(data.getCity().length());

        return code
                .append(qrCodeData.getPayloadFormatIndicator().toString())
                .append(qrCodeData.getPointOfInitiationMethod().toString())
                .append(merchantAccountInfoCalculate(qrCodeData, data))
                .append(qrCodeData.getMerchantCategoryCode().toString())
                .append(qrCodeData.getTransactionCurrency().toString())
                .append(qrCodeData.getTransactionAmount().getSize() > 0 ? qrCodeData.getTransactionAmount().toString() : null)
                .append(qrCodeData.getCountryCode().toString())
                .append(qrCodeData.getMerchantName().toString())
                .append(qrCodeData.getMerchantCity().toString())
                .append(additionalDataFieldCalculate(qrCodeData, data))
                .append(qrCodeData.getCrc16().toString())
                .toString();
    }

    private String merchantAccountInfoCalculate(DynamicQrCodeData qrCodeData, DynamicQrCode data) {
        QrCodeField merchantInfo = qrCodeData.getMerchantAccountInformation();
        QrCodeField gui = qrCodeData.getGui();
        QrCodeField key = qrCodeData.getKey();
        QrCodeField url = qrCodeData.getUrl();

        key.setValue(data.getKey());
        key.setSize(key.getValue().length());

        Parameter parameter = parameterRepository.findAll().stream().findFirst().orElseThrow(() ->
                new RuntimeException("Nenhuma parametrização encontrada para o usuário: " + TenantContext.getCurrentTenant()));

        url.setValue(parameter.getHostName() + "/" + TenantContext.getCurrentTenant() + "/" + data.getPayloadIdentifier());
        url.setSize(url.getValue().length());

        merchantInfo.setValue(gui.toString() + key.toString() + url.toString());
        merchantInfo.setSize(gui.toString().length() + key.toString().length() + url.toString().length());

        return merchantInfo.toString();
    }

    private String additionalDataFieldCalculate(DynamicQrCodeData qrCodeData, DynamicQrCode data) {
        QrCodeField additionalDataField = qrCodeData.getAdditionalDataField();
        QrCodeField referenceLabel = qrCodeData.getReferenceLabel();

        if (!Strings.isEmpty(data.getTransactionIdentifier())) {
            referenceLabel.setValue(data.getTransactionIdentifier());
            referenceLabel.setSize(data.getTransactionIdentifier().length());

            additionalDataField.setValue(referenceLabel.toString());
            additionalDataField.setSize(referenceLabel.toString().length());
            return additionalDataField.toString();
        }
        return "";
    }

    public DecodeDynamicQrCodeResponse buildDecodeDynamicQrCodeResponse(DynamicQrCode dynamicQrCode) {
        LocalDateTime tempDateTime = LocalDateTime.from(dynamicQrCode.getCreated());

        Parameter parameter = parameterRepository.findAll().stream().findFirst().orElseThrow(() ->
                new RuntimeException("Nenhuma parametrização encontrada para o usuário: " + TenantContext.getCurrentTenant()));

        return DecodeDynamicQrCodeResponse.builder()
                .qrCodeType("dynamic")
                .payloadUrl(parameter.getHostName() + "/" + TenantContext.getCurrentTenant() + "/" + dynamicQrCode.getPayloadIdentifier())
                .key(dynamicQrCode.getKey())
                .merchantName(dynamicQrCode.getReceiverName())
                .city(dynamicQrCode.getCity())
                .transactionIdentifier(dynamicQrCode.getTransactionIdentifier())
                .review(dynamicQrCode.getPayloadRevision())
                .calendar(DynamicQrCodeRequest.Calendar.builder()
                        .createdAt(dynamicQrCode.getCreated())
                        .presentedAt(dynamicQrCode.getPresentation())
                        .expiracy(tempDateTime.until(dynamicQrCode.getExpirationDate(), ChronoUnit.SECONDS))
                        .due(dynamicQrCode.getDueDate().toLocalDate())
                        .receivableAfterMaturity(dynamicQrCode.getReceivableAfterDue().equals(EnumBoolean.S))
                        .build())
                .debtor(DynamicQrCodeRequest.Debtor.builder()
                        .cpf(ValidationUtil.isValidCPF(dynamicQrCode.getPayerCpfCnpj()) ? dynamicQrCode.getPayerCpfCnpj() : null)
                        .cnpj(ValidationUtil.isValidCNPJ(dynamicQrCode.getPayerCpfCnpj()) ? dynamicQrCode.getPayerCpfCnpj() : null)
                        .name(dynamicQrCode.getPayerName())
                        .build())
                .value(DynamicQrCodeRequest.Value.builder()
                .original(dynamicQrCode.getOriginalValue())
                        .finale(dynamicQrCode.getFinalValue())
                        .interest(dynamicQrCode.getInterestValue())
                        .penalty(dynamicQrCode.getPenaltyValue())
                        .discount(dynamicQrCode.getDiscountValue())
                        .allowsChange(dynamicQrCode.getAllowsChange().equals(EnumBoolean.S))
                        .build())
                .additionalInformations(AdditionalInformationFactory.getInstance().buildAdditionalInformationResponseList(dynamicQrCode.getAdditionalInformation()))
                .build();
    }

    public String getQrCodePayloadUrl(String message){
        DynamicQrCodeData data = new DynamicQrCodeData();

        data.getPayloadFormatIndicator().setSize(getFieldSize(message, 0, 0));
        data.getPayloadFormatIndicator().setValue(getFieldValue(message, 0, data.getPayloadFormatIndicator().getSize(), 0));
        data.getPayloadFormatIndicator().setEndPosition(data.getPayloadFormatIndicator().getFullSize());

        data.getPointOfInitiationMethod().setSize(getFieldSize(message, data.getPayloadFormatIndicator().getEndPosition(), 0));
        data.getPointOfInitiationMethod().setValue(getFieldValue(message, data.getPayloadFormatIndicator().getEndPosition(), data.getPointOfInitiationMethod().getSize(), 0));
        data.getPointOfInitiationMethod().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize());

        data.getMerchantAccountInformation().setSize(getFieldSize(message, data.getPointOfInitiationMethod().getEndPosition(), 1));
        data.getMerchantAccountInformation().setValue(getFieldValue(message, data.getPointOfInitiationMethod().getEndPosition(), data.getMerchantAccountInformation().getSize(), 1));
        data.getMerchantAccountInformation().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize() + data.getMerchantAccountInformation().getFullSize());
        data.getMerchantAccountInformation().setInitPosition(data.getPointOfInitiationMethod().getEndPosition() + data.getMerchantAccountInformation().getHeaderSize());

        data.getGui().setSize(getFieldSize(message, data.getMerchantAccountInformation().getInitPosition(), 0));
        data.getGui().setValue(getFieldValue(message, data.getMerchantAccountInformation().getInitPosition(), data.getGui().getSize(), 0));
        data.getGui().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize() + data.getMerchantAccountInformation().getHeaderSize() + data.getGui().getFullSize());

        data.getKey().setSize(getFieldSize(message, data.getGui().getEndPosition(), 0));
        data.getKey().setValue(getFieldValue(message, data.getGui().getEndPosition(), data.getKey().getSize(), 0));
        data.getKey().setEndPosition(data.getPayloadFormatIndicator().getFullSize() + data.getPointOfInitiationMethod().getFullSize() + data.getMerchantAccountInformation().getHeaderSize() + data.getGui().getFullSize() + data.getKey().getFullSize());

        data.getUrl().setSize(getFieldSize(message, data.getKey().getEndPosition(), 0));
        data.getUrl().setValue(getFieldValue(message, data.getKey().getEndPosition(), data.getUrl().getSize(), 0));
        data.getUrl().setEndPosition(data.getPayloadFormatIndicator().getFullSize()
                + data.getPointOfInitiationMethod().getFullSize() + data.getMerchantAccountInformation().getHeaderSize() + data.getGui().getFullSize()
                + data.getKey().getFullSize() + data.getUrl().getFullSize());

        return data.getUrl().getValue();
    }

    private static int getFieldSize(String message, int initialPosition, int extraOffSet) {
        return Integer.parseInt(message.substring(initialPosition + 2, initialPosition + 4 + extraOffSet));
    }

    public static String getFieldValue(String message, int initialPosition, int size, int extraOffSet) {
        return message.substring(initialPosition + 4 + extraOffSet, initialPosition + 4 + size + extraOffSet);
    }

}
