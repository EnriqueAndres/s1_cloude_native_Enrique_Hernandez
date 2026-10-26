package com.Duoc.semanaUno.service;

import org.springframework.stereotype.Service;

import com.Duoc.semanaUno.repository.EntradaRepository;
import java.util.*;
import com.Duoc.semanaUno.model.*;

@Service 
public class EntradaServicio {

    private final EntradaRepository entradaRepository;

    public EntradaServicio(EntradaRepository entradaRepository){
        this.entradaRepository = entradaRepository;
    }

    //get buscar id entrada
    public Optional <Entrada> buscarIdEntrada(Long id){
        return entradaRepository.findById(id);
    }

    //guardar post entrada
    public Entrada guardarEntrada(Entrada entrada){
        return entradaRepository.save(entrada);
    }

    //actualizar usuario put
    public Entrada actualizarEntrada(Long id, Entrada entradaDetalles){
        
        Entrada entrada = entradaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró la entrada con el ID: " + id));

        entrada.setNombre(entradaDetalles.getNombre());
        entrada.setFecha(entradaDetalles.getFecha());
        entrada.setPrecioEntrada(entradaDetalles.getPrecioEntrada());

        return entradaRepository.save(entrada);
    }

    // borrar entrada
    public void eliminarEntrada(Long id){
        Entrada entrada = entradaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se encontró la entrada con el ID: " + id));
        
        entradaRepository.delete(entrada);
    }    

}
