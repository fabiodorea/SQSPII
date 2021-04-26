package com.sinqia.sqspii.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DynamicQrCodeUpdateRequest extends DynamicQrCodeRequest implements Serializable {

    private static final long serialVersionUID = 4550772831862020690L;
    
    private boolean deleteDocument;
    
}
