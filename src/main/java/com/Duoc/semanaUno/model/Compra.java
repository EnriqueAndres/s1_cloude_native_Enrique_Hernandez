package com.Duoc.semanaUno.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long eventoId;

    @Column(nullable = false)
    private String evento;

    @Column(nullable = false)
    private LocalDate fechaEvento;

    @Column(nullable = false)
    private int cantidadEntradas;

    @Column(nullable = false)
    private int precioUnitario;

    @Column(nullable = false)
    private int precioTotal;
}
