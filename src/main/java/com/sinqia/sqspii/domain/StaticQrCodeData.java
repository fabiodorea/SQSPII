package com.sinqia.sqspii.domain;

import lombok.Data;

@Data
public class StaticQrCodeData {

    QrCodeField payloadFormatIndicator = new QrCodeField("00", "Payload Format Indicator", 2, "01", null, null, false, null, null);
    QrCodeField pointOfInitiationMethod = new QrCodeField("01", "Point of Initiation Method", 2, "11", null, null, false, null, null); // “11” (QR reutilizável) ou “12” (QR utilizável apenas uma vez)
    QrCodeField merchantAccountInformation = new QrCodeField("26", "Merchant Account Information", 0, "", null, null, true, null, null);
    QrCodeField gui = new QrCodeField("00", "GUI", 14, "br.gov.bcb.pix", 0, 0, false, null, null);
    QrCodeField key = new QrCodeField("01", "Chave", 0, "", 1, 77, true, null, null);
    QrCodeField additionalInfo = new QrCodeField("02", "Aditional Information", 0, "", 1, 72, true, null, null);
    QrCodeField merchantCategoryCode = new QrCodeField("52", "Merchant Category Code", 4, "0000", null, null, false, null, null);
    QrCodeField transactionCurrency = new QrCodeField("53", "Transaction Currency", 3, "986", null, null, false, null, null);
    QrCodeField transactionAmount = new QrCodeField("54", "Transaction Amout", 0, "", 1, 13, true, null, null);
    QrCodeField countryCode = new QrCodeField("58", "Country Code", 2, "BR", null, null, false, null, null);
    QrCodeField merchantName = new QrCodeField("59", "Merchant Name", 0, "", null, null, true, null, null);
    QrCodeField merchantCity = new QrCodeField("60", "Merchant City", 0, "", null, null, true, null, null);
    QrCodeField additionalDataField = new QrCodeField("62", "Aditional Data Field", 0, "", 1, 99, true, null, null);
    QrCodeField txId = new QrCodeField("05", "txId", 0, "", 1, 25, true, null, null);
    QrCodeField crc16 = new QrCodeField("63", "CRC16", 4, "DFE3", null, null, true, null, null);
}
