package com.sinqia.sqspii.data.multitenancy.entity;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "spi_usuario_dados_acesso")
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

    @Column(name = "cod_tip_bd")
    private String dataBaseTypeCode;

    /**
     * ‘O’ = Oracle
     * ‘Q’ = SQL Server
     * ‘D’ = DB2
     * @return o driver da conexao
     * @throws Exception
     */
    public String getDriverClassName() throws Exception {
        if (dataBaseTypeCode.equalsIgnoreCase("Q")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else {
            throw new Exception("Tipo de banco de dados nao implementado: " + dataBaseTypeCode);
        }
    }

    public String getUrl() throws Exception {
        if (dataBaseTypeCode.equalsIgnoreCase("Q")) {
            if (server.indexOf(",") > 0) {
                String servidor = server.split(",")[0];
                String porta = server.split(",")[1];
                return "jdbc:sqlserver://" + servidor + ":" + porta + ";databaseName=" + databaseName + ";";
            } else {
                return "jdbc:sqlserver://" + server + ";databaseName=" + databaseName + ";";
            }
        } else {
            throw new Exception("Tipo de banco de dados nao implementado: " + dataBaseTypeCode);
        }
    }

}