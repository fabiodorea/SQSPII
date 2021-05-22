package com.sinqia.sqspii.data.multitenancy.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@Table(name = "spi_qrcode_dinamico_log")
public class AuditDynamicQrCode implements Serializable {
    private static final long serialVersionUID = -665981554740189649L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_din_log")
    private Long id;
    
    @Column(name = "tex_log_ant", nullable = false)
    private String oldValue;
    
    @Column(name = "dat_cri_qrc", nullable = false)
    private LocalDateTime created;

}
