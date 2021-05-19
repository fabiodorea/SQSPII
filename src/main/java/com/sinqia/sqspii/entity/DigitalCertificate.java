package com.sinqia.sqspii.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "spi_certificado_digital")
public class DigitalCertificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cer_dig")
    private Long id;

    @Column(name = "dsc_cer")
    private String descriptionCertificate;

    @Column(name = "nom_hst_ste_qrc")
    private String host;

    @Column(name = "id_ent_par")
    private Long idEntPar;

    @Column(name = "id_sit_cer")
    private Long idSitCer;

    @Column(name = "id_fna_cer_dig")
    private Long idFnaCerDig;
}
