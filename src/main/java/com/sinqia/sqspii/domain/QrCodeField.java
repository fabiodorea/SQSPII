package com.sinqia.sqspii.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeField {

    private String id;
    private String name;
    private int size;
    private String value;
    private Integer minSize;
    private Integer maxSize;
    private boolean calculated;
    private Integer initPosition;
    private Integer endPosition;

    @Override
    public String toString() {
        return id + getSize(size) + value;
    }

    private String getSize(int value) {
        return String.format("%02d", value);
    }

    public int getFullSize(){
        return id.length() + getSize(size).length() + value.length();
    }

    public int getHeaderSize(){
        return id.length() + getSize(size).length();
    }

}
