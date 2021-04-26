package com.sinqia.sqspii.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DynamicQrCodeRequest implements Serializable {

    private static final long serialVersionUID = 2045868037506177952L;

    @Schema(description = "Chave de endereçamento da conta transacional")
    @NotEmpty(message = "Campo 'key' é obritagório")
    @Valid
    private String key;

    @NotEmpty(message = "Campo 'city' é obritagório")
    @Schema(description = "Cidade de realização da ordem de pagamento instantâneo")
    private String city;
    
    @NotEmpty(message = "Campo 'transactionIdentifier' é obritagório")
    @Size(max = 35, message =  "Campo 'transactionIdentifier' não pode exceder o tamanho máximo de 35 caracteres.")
    @Schema(description = "Identificador da transação utilizado para conciliação do recebedor")
    private String transactionIdentifier;

    @NotEmpty(message = "Campo 'receiverName' é obritagório")
    @Size(max = 25, message =  "Campo 'receiverName' não pode exceder o tamanho máximo de 25 caracteres.")
    @Schema(description = "Nome do recebedor da ordem de pagamento instantâneo")
    private String receiverName;
    
    private Calendar calendar;
    
    private Debtor debtor;

    private Value value;
    
    private String payerRequest;
    
    private List<AdditionalInformationRequest> additionalInformations;

    @Data
    @Builder
    public static class Calendar {
        private LocalDateTime createdAt;
        private LocalDateTime presentedAt;
        private Long expiracy;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate due;
        private boolean receivableAfterMaturity;
    }
    
    @Data
    @Builder
    public static class Debtor {
        private String cpf;
        private String cnpj;
        private String name;
    }

    @Data
    @Builder
    public static class Receiver {
        private String cpf;
        private String cnpj;
        private String name;
    }
    
    @Data
    @Builder
    public static class Value {
        private BigDecimal original;
        private BigDecimal finale;
        private BigDecimal interest;
        private BigDecimal penalty;
        private BigDecimal discount;
        private boolean allowsChange;
    }
    
    @Data
    @Builder
    public static class AdditionalInformationRequest {
        private String name;
        private String value;
    }
    
}
