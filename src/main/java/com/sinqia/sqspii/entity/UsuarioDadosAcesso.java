package com.sinqia.sqspii.entity;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "DataSourceConfig")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioDadosAcesso implements Serializable {

    private static final long serialVersionUID = -7958904874010833870L;

    @Id
    @Column(name = "cod_usu")
    private Long id;

    @Column(name = "cod_log_usu")
    private String userCode;

    @Column(name = "nom_usu")
    private String userName;

    @Column(name = "cod_con")
    private String connCode;

    @Column(name = "dsc_con")
    private String connDescription;

    @Column(name = "cod_drv")
    private String driverCode;

    @Column(name = "dsc_ser_con")
    private String server;

    @Column(name = "dsc_bd_con")
    private String databaseName;

    @Column(name = "dsc_usu_con")
    private String dbUserName;

    @Column(name = "dsc_sen_con")
    private String dbPassword;

    @Column(name = "dsc_par_con")
    private String descParCon;

    @Column(name = "flg_tip_con")
    private String flagTipoConexao;

    @Column(name = "id_ent_par")
    private String idEntPar;

    @Column(name = "cod_grp")
    private String codigoGrupo;

    @Column(name = "dsc_tok_grp")
    private String descricaoTokenGrupo;

//    @Column(name = "dsc_ser_con")
//    private String driverClassName;

//    @Column(name = "dsc_ser_con")
//    private boolean initialize;

    public String getDriverClassName() {
        return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

    public String getUrl() {
        return "jdbc:sqlserver://"+ server +":1433;databaseName=" + databaseName + ";integratedSecurity=true";
    }

    public boolean isInitialize() {
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataSourceConfig{");
        sb.append("id=").append(id);
        sb.append(", name='").append(userCode).append('\'');
        sb.append(", url='").append(getUrl()).append('\'');
        sb.append(", username='").append(dbUserName).append('\'');
        sb.append(", password='").append(dbPassword).append('\'');
        sb.append(", driverClassName='").append(getDriverClassName()).append('\'');
        sb.append(", initialize=").append(isInitialize());
        sb.append('}');
        return sb.toString();
    }
}