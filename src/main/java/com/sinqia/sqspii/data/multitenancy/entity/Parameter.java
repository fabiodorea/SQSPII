package com.sinqia.sqspii.data.multitenancy.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@DynamicUpdate
@Table(name = "spi_parametro_geral")
public class Parameter implements Serializable {
    private static final long serialVersionUID = -665981554740189649L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_par_ger")
    private Long id;

    @Column(name = "id_ent_par ")
    private Long idEntPar;
    
    @Column(name = "nom_hst_url_pyl_qrc", length = 100, nullable = true)
    private String hostName;
    

}
