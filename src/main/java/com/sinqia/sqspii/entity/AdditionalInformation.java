package com.sinqia.sqspii.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "spi_informacao_adicional_qrc")
public class AdditionalInformation implements Serializable {
    private static final long serialVersionUID = -665981554740189649L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num_seq_inf_adi_qrc")
    private Long id;
    
    @Column(name = "nom_inf_adi", length = 50, nullable = false)
    private String name;
    
    @Column(name = "vr_inf_adi", length = 200, nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "num_seq_qrc_dnm")
    @JsonIgnore
    @ToString.Exclude
    private DynamicQrCode dynamicQrCode;

}
