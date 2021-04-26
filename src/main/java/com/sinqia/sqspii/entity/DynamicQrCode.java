package com.sinqia.sqspii.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.sinqia.sqspii.enums.EnumBoolean;
import com.sinqia.sqspii.enums.EnumPayerType;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "spi_qrcode_dinamico")
public class DynamicQrCode implements Serializable {

    private static final long serialVersionUID = 6471172231353601799L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_seq_qrc_dnm")
    private Long id;

    @Column(name = "num_ves_pyl", nullable = false, length = 10)
    private String payloadVersion;

    @Column(name = "id_pyl", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID payloadIdentifier;

    @Column(name = "num_rvi_pyl", nullable = false)
    private Long payloadRevision;

    @Column(name = "rec_nome", length = 200)
    private String receiverName;

    @Column(name = "dat_cri_qrc", nullable = false)
    private LocalDateTime created;

    @Column(name = "dat_apr_qrc", nullable = false)
    private LocalDateTime presentation;

    @Column(name = "dat_vct_pag", nullable = true)
    private LocalDateTime dueDate;

    @Column(name = "dat_exa_qrc", nullable = true)
    private LocalDateTime expirationDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "flg_rec_apo_vct", nullable = false, length = 1)
    private EnumBoolean receivableAfterDue;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "id_tip_pes_pag", nullable = true, length = 1)
    private EnumPayerType payerType;

    @Column(name = "num_cpf_cnpj_pag", nullable = true)
    private String payerCpfCnpj;

    @Column(name = "nom_usu_pag", nullable = true)
    private String payerName;

    @Column(name = "tex_sol_pag", length = 140, nullable = true)
    private String payerRequest;

    @Column(name = "vr_oal_doc", nullable = false)
    private BigDecimal originalValue;

    @Column(name = "vr_fia_doc", nullable = false)
    private BigDecimal finalValue;

    @Column(name = "vr_jur_doc", nullable = true)
    private BigDecimal interestValue;

    @Column(name = "vr_mta_doc", nullable = true)
    private BigDecimal penaltyValue;

    @Column(name = "vr_dco_doc", nullable = true)
    private BigDecimal discountValue;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "flg_pem_alt_vlr", nullable = false, length = 1)
    private EnumBoolean allowsChange;

    @Column(name = "dsc_chv_end", length = 77, nullable = false)
    private String key;

    @Column(name = "nom_cid", length = 15, nullable = false)
    private String city;

    @Column(name = "id_trn", length = 35, nullable = false)
    private String transactionIdentifier;

    @OneToMany(mappedBy = "dynamicQrCode", fetch = FetchType.LAZY)
    private List<AdditionalInformation> additionalInformation;

    @ManyToOne
    @JoinColumn(name = "id_sit_cob", nullable = false)
    private Situation situation;
}
