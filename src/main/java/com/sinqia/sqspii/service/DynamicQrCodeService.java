package com.sinqia.sqspii.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinqia.sqspii.context.TenantContext;
import com.sinqia.sqspii.entity.AdditionalInformation;
import com.sinqia.sqspii.entity.AuditDynamicQrCode;
import com.sinqia.sqspii.entity.DynamicQrCode;
import com.sinqia.sqspii.entity.Situation;
import com.sinqia.sqspii.enums.EnumBoolean;
import com.sinqia.sqspii.enums.EnumPayerType;
import com.sinqia.sqspii.exception.BusinessException;
import com.sinqia.sqspii.exception.InvalidChargeSituationException;
import com.sinqia.sqspii.exception.UnableToGenerateQRCodeImageException;
import com.sinqia.sqspii.factory.DynamicQRCodeBuilderFactory;
import com.sinqia.sqspii.repository.AdditionalInformationRepository;
import com.sinqia.sqspii.repository.AuditDynamicQrCodeRepository;
import com.sinqia.sqspii.repository.DynamicQrCodeRepository;
import com.sinqia.sqspii.repository.SituationRepository;
import com.sinqia.sqspii.request.DecodeQrCodeRequest;
import com.sinqia.sqspii.request.DynamicQrCodeRequest;
import com.sinqia.sqspii.request.DynamicQrCodeUpdateRequest;
import com.sinqia.sqspii.response.DecodeQrCodeResponse;
import com.sinqia.sqspii.response.DynamicQrCodeResponse;
import com.sinqia.sqspii.util.ImageUtil;
import com.sinqia.sqspii.util.ValidationUtil;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class DynamicQrCodeService {

    @Autowired
    private DynamicQrCodeRepository dynamicQrCodeRepository;

    @Autowired
    private AdditionalInformationRepository additionalInformationRepository;

    @Autowired
    private DynamicQRCodeBuilderFactory dynamicQRCodeBuilderFactory;

    @Autowired
    private SituationRepository situationRepository;

    @Autowired
    private AuditDynamicQrCodeRepository auditDynamicRepository;

    @Autowired
    private SignQrCodeService signQrCodeService;

    @Autowired
    private DecodeQrCodeService decodeQrCodeService;

    @Autowired
    ObjectMapper mapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public DynamicQrCodeResponse generate(DynamicQrCodeRequest request) {

        validateRequest(request);

        Situation situation = situationRepository.findByDescription(Situation.ACTIVE)
                .orElseThrow(() -> new BusinessException("Situação ativa não encontrada!"));

        UUID documentIdentifier = UUID.randomUUID();

        DynamicQrCode dynamic = new DynamicQrCode();
        dynamic.setKey(request.getKey());
        dynamic.setCity(request.getCity());
        dynamic.setTransactionIdentifier(request.getTransactionIdentifier());
        if (request.getCalendar() != null) {
            dynamic.setExpirationDate(LocalDateTime.now().plusSeconds(request.getCalendar().getExpiracy()));
            dynamic.setDueDate(request.getCalendar().getDue().atStartOfDay());
            dynamic.setReceivableAfterDue(getBooleanValue(request.getCalendar().isReceivableAfterMaturity()));
        }
        if (request.getDebtor() != null) {
            dynamic.setPayerCpfCnpj(getPayerCpfCnpj(request));
            dynamic.setPayerType(getPayerType(request));
            dynamic.setPayerName(request.getDebtor().getName());
        }
        dynamic.setPayerRequest(request.getPayerRequest());

        dynamic.setReceiverName(request.getReceiverName());

        if (request.getValue() != null) {
            dynamic.setOriginalValue(request.getValue().getOriginal());
            dynamic.setFinalValue(request.getValue().getFinale());
            dynamic.setInterestValue(request.getValue().getInterest());
            dynamic.setPenaltyValue(request.getValue().getPenalty());
            dynamic.setDiscountValue(request.getValue().getDiscount());
            dynamic.setAllowsChange(getBooleanValue(request.getValue().isAllowsChange()));
        }
        dynamic.setPayloadIdentifier(documentIdentifier);
        dynamic.setPayloadVersion("1.0");
        dynamic.setPayloadRevision(1L);
        dynamic.setCreated(LocalDateTime.now());
        dynamic.setPresentation(LocalDateTime.now());
        dynamic.setSituation(situation);
        log.info(dynamic.toString());
        dynamicQrCodeRepository.save(dynamic);

        if (!CollectionUtils.isEmpty(request.getAdditionalInformations())) {
            for (DynamicQrCodeRequest.AdditionalInformationRequest itemInfo : request.getAdditionalInformations()) {
                AdditionalInformation newInfo = new AdditionalInformation();
                newInfo.setName(itemInfo.getName());
                newInfo.setValue(itemInfo.getValue());
                newInfo.setDynamicQrCode(dynamic);
                additionalInformationRepository.save(newInfo);
            }

        }
        String qrCodeLine = dynamicQRCodeBuilderFactory.buildQRCodeString(dynamic);

        try {
            DynamicQrCodeResponse response = new DynamicQrCodeResponse();
            String base64Image = ImageUtil.generateQRCodeImage(qrCodeLine);
            response.setGeneratedImage(base64Image);
            response.setTextualContent(qrCodeLine);
            response.setDocumentIdentifier(documentIdentifier);
            return response;
        } catch (IOException e) {
            throw new UnableToGenerateQRCodeImageException();
        }
    }

    private void validateRequest(DynamicQrCodeRequest request) {
        if (request.getDebtor() != null && !StringUtils.isEmpty(request.getDebtor().getName()) &&
                (StringUtils.isEmpty(request.getDebtor().getCnpj())
                        && StringUtils.isEmpty(request.getDebtor().getCpf()))) {
            throw new BusinessException("Obrigatório informar o cpf ou cnpj ao informar o nome do devedor");
        }

        if (request.getDebtor() != null &&
                (!StringUtils.isEmpty(request.getDebtor().getCnpj())
                        && !StringUtils.isEmpty(request.getDebtor().getCpf()))) {
            throw new BusinessException("Informar o cpf ou cnpj do devedor, não os dois");
        }

        if (request.getDebtor() != null && !StringUtils.isEmpty(request.getDebtor().getCnpj())
                && !ValidationUtil.isValidCNPJ(request.getDebtor().getCnpj())) {
            throw new BusinessException("Cnpj inválido");
        }

        if (request.getDebtor() != null && !StringUtils.isEmpty(request.getDebtor().getCpf())
                && !ValidationUtil.isValidCPF(request.getDebtor().getCpf())) {
            throw new BusinessException("Cpf inválido");
        }
    }

    private EnumBoolean getBooleanValue(boolean value) {
        if (value)
            return EnumBoolean.S;
        else
            return EnumBoolean.N;
    }

    private EnumPayerType getPayerType(DynamicQrCodeRequest request) {
        if (request.getDebtor().getCnpj() != null && !StringUtils.isEmpty(request.getDebtor().getCnpj()))
            return EnumPayerType.J;
        else if (request.getDebtor().getCpf() != null && !StringUtils.isEmpty(request.getDebtor().getCpf()))
            return EnumPayerType.F;
        else
            return EnumPayerType.N;
    }

    private String getPayerCpfCnpj(DynamicQrCodeRequest request) {
        if (request.getDebtor().getCnpj() != null && request.getDebtor().getCnpj().trim().length() > 0)
            return request.getDebtor().getCnpj();
        else
            return request.getDebtor().getCpf();

    }

    public DynamicQrCode findByIdentifier(String documentId) {
        UUID uuid = UUID.fromString(documentId);
        DynamicQrCode dynamic = dynamicQrCodeRepository.findByPayloadIdentifier(uuid)
                .orElseThrow(() -> new BusinessException("Documento não encontrado!"));
        return dynamic;
    }

    @Transactional
    public String createPayloadByIdentifier(String documentId, String tenant) throws Exception {
        UUID uuid = UUID.fromString(documentId);
        DynamicQrCode dynamic = dynamicQrCodeRepository.findByPayloadIdentifier(uuid)
                .orElseThrow(() -> new BusinessException("Documento não encontrado!"));

        if(!dynamic.getSituation().getDescription().equalsIgnoreCase(Situation.ACTIVE))
            throw new InvalidChargeSituationException("A cobrança não está ativa: " + dynamic.getSituation().getDescription());

        return signQrCodeService.encode(mapper.writeValueAsString(dynamic));
    }

    public DecodeQrCodeResponse decodeQrCode(DecodeQrCodeRequest request) throws Exception {
        return decodeQrCodeService.decode(request);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DynamicQrCodeResponse update(String documentId, DynamicQrCodeUpdateRequest request) {
        UUID uuid = UUID.fromString(documentId);

        Situation situationRemoved = situationRepository.findByDescription(Situation.REMOVED_RECEIVER)
                .orElseThrow(() -> new BusinessException("Situação ativa não encontrada!"));
        
        Situation situationActive = situationRepository.findByDescription(Situation.ACTIVE)
                .orElseThrow(() -> new BusinessException("Situação ativa não encontrada!"));
        
        DynamicQrCode dynamic = dynamicQrCodeRepository.findByPayloadIdentifier(uuid)
                .orElseThrow(() -> new BusinessException("Documento não encontrado!"));

        if(dynamic.getSituation().getId() != situationActive.getId()) {
            throw new BusinessException("Não foi possivel alterar documento com situação " + dynamic.getSituation().getDescription());
        }
        
        validateRequest(request);

        ObjectMapper mapper = new ObjectMapper();
        AuditDynamicQrCode audit = new AuditDynamicQrCode();
        audit.setCreated(LocalDateTime.now());
        String json;
        try {
            json = mapper.writeValueAsString(dynamic);
            audit.setOldValue(json);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
            throw new BusinessException("Não foi possivel converter o codigo anterior!", e1);
        }
        auditDynamicRepository.save(audit);

        dynamic.setKey(request.getKey());
        dynamic.setCity(request.getCity());
        dynamic.setTransactionIdentifier(request.getTransactionIdentifier());
        
        if (request.getCalendar() != null) {
            dynamic.setExpirationDate(LocalDateTime.now().plusSeconds(request.getCalendar().getExpiracy()));
            dynamic.setDueDate(request.getCalendar().getDue().atStartOfDay());
            dynamic.setReceivableAfterDue(getBooleanValue(request.getCalendar().isReceivableAfterMaturity()));
        }
        
        if (request.getDebtor() != null) {
            dynamic.setPayerCpfCnpj(getPayerCpfCnpj(request));
            dynamic.setPayerType(getPayerType(request));
            dynamic.setPayerName(request.getDebtor().getName());
        }
        
        dynamic.setPayerRequest(request.getPayerRequest());
        dynamic.setReceiverName(request.getReceiverName());

        if (request.getValue() != null) {
            dynamic.setOriginalValue(request.getValue().getOriginal());
            dynamic.setFinalValue(request.getValue().getFinale());
            dynamic.setInterestValue(request.getValue().getInterest());
            dynamic.setPenaltyValue(request.getValue().getPenalty());
            dynamic.setDiscountValue(request.getValue().getDiscount());
            dynamic.setAllowsChange(getBooleanValue(request.getValue().isAllowsChange()));
        }
        dynamic.setPayloadVersion("1." + (dynamic.getPayloadRevision() + 1));
        dynamic.setPayloadRevision(dynamic.getPayloadRevision() + 1);
        dynamic.setCreated(LocalDateTime.now());
        dynamic.setPresentation(LocalDateTime.now());

        if(request.isDeleteDocument()) {
            dynamic.setSituation(situationRemoved);
        }
        
        log.info(dynamic.toString());
        dynamicQrCodeRepository.save(dynamic);

        // o nome do recebedor vai na alteração tb? é alteravel?
        
        additionalInformationRepository.deleteByDynamic(dynamic.getId());
        if (!CollectionUtils.isEmpty(request.getAdditionalInformations())) {
            for (DynamicQrCodeRequest.AdditionalInformationRequest itemInfo : request.getAdditionalInformations()) {
                AdditionalInformation newInfo = new AdditionalInformation();
                newInfo.setName(itemInfo.getName());
                newInfo.setValue(itemInfo.getValue());
                newInfo.setDynamicQrCode(dynamic);
                additionalInformationRepository.save(newInfo);
            }

        }

        String qrCodeline = dynamicQRCodeBuilderFactory.buildQRCodeString(dynamic);
        try {
            DynamicQrCodeResponse response = new DynamicQrCodeResponse();
            String base64Image = ImageUtil.generateQRCodeImage(qrCodeline);
            response.setGeneratedImage(base64Image);
            response.setTextualContent(qrCodeline);
            response.setDocumentIdentifier(dynamic.getPayloadIdentifier());
            return response;
        } catch (IOException e) {
            throw new UnableToGenerateQRCodeImageException();
        }
    }
}
