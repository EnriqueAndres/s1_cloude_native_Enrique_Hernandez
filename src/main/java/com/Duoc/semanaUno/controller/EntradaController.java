package com.Duoc.semanaUno.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Duoc.semanaUno.service.EntradaServicio;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.Duoc.semanaUno.model.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;



@RestController 
@RequestMapping("/api/entradas")
public class EntradaController {

    private final EntradaServicio entradaServicio;

    public EntradaController(EntradaServicio entradaServicio){
        this.entradaServicio = entradaServicio;
    }

    //GET listar todos los eventos disponibles
    @GetMapping("")
    public ResponseEntity<List<Entrada>> listarEntradas(){
        return ResponseEntity.ok(entradaServicio.listarEntradas());
    }

    //GET por ID
    @GetMapping("/{id}")
    public ResponseEntity<Entrada> getIdEntrada(@PathVariable Long id){
        return entradaServicio.buscarIdEntrada(id)
            .map(entrada -> ResponseEntity.ok(entrada)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    //POST crear evento
    @PostMapping("")
    public ResponseEntity<Entrada> saveNuevaEntrada(@RequestBody @Valid Entrada entrada){
        Entrada nuevaEntrada = entradaServicio.guardarEntrada(entrada);
        return new ResponseEntity<>(nuevaEntrada, HttpStatus.CREATED);
    }

    //POST comprar entradas
    @PostMapping("/comprar")
    public ResponseEntity<Compra> comprarEntradas(@RequestBody @Valid CompraRequest request){
        try {
            Compra resumenCompra = entradaServicio.comprarEntradas(request);
            return new ResponseEntity<>(resumenCompra, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //GET listar todas las compras realizadas
    @GetMapping("/compras")
    public ResponseEntity<List<Compra>> listarCompras(){
        return ResponseEntity.ok(entradaServicio.listarCompras());
    }

    // PUT
    @PutMapping("/{id}")
    public ResponseEntity<Entrada> actualizarEntrada(@PathVariable Long id, @RequestBody Entrada entradaDetalles) {
        try {
            Entrada entradaActualizada = entradaServicio.actualizarEntrada(id, entradaDetalles);

            return ResponseEntity.ok(entradaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEntrada(@PathVariable Long id) {
        try {
            entradaServicio.eliminarEntrada(id);
            return ResponseEntity.noContent().build(); 
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    } 
    
}
