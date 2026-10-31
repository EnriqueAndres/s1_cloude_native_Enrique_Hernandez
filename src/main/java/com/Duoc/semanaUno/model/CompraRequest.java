package com.Duoc.semanaUno.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CompraRequest {

    @NotNull
    private Long eventoId;

    @NotNull
    @Positive
    private int cantidad;
}
