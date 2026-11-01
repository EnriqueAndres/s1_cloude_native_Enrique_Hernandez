package com.Duoc.semanaUno.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class S3ObjectDto {

    private String key;
    private Long size;
    private String lastModified;
}
