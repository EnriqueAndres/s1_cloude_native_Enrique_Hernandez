package com.Duoc.semanaUno.service;

import org.springframework.stereotype.Service;

import com.Duoc.semanaUno.repository.CompraRepository;
import com.Duoc.semanaUno.repository.EntradaRepository;
import java.util.*;
import com.Duoc.semanaUno.model.*;

@Service 
public class EntradaServicio {

    private final EntradaRepository entradaRepository;
    private final CompraRepository compraRepository;

    public EntradaServicio(EntradaRepository entradaRepository, CompraRepository compraRepository){
        this.entradaRepository = entradaRepository;
        this.compraRepository = compraRepository;
    }

    //get listar todos los eventos disponibles
    public List<Entrada> listarEntradas(){
        return entradaRepository.findAll();
    }

    //get buscar id entrada
    public Optional <Entrada> buscarIdEntrada(Long id){
        return entradaRepository.findById(id);
    }

    //guardar post entrada
    public Entrada guardarEntrada(Entrada entrada){
        return entradaRepository.save(entrada);
    }

    //comprar entradas de un evento
    public Compra comprarEntradas(CompraRequest request){
        Entrada entrada = entradaRepository.findById(request.getEventoId())
            .orElseThrow(() -> new RuntimeException("No se encontró el evento con el ID: " + request.getEventoId()));

        int precioUnitario = entrada.getPrecioEntrada();
        int precioTotal = precioUnitario * request.getCantidad();

        Compra compra = new Compra();
        compra.setEventoId(entrada.getId());
        compra.setEvento(entrada.getNombre());
        compra.setFechaEvento(entrada.getFecha());
        compra.setCantidadEntradas(request.getCantidad());
        compra.setPrecioUnitario(precioUnitario);
        compra.setPrecioTotal(precioTotal);

        return compraRepository.save(compra);
    }

    //listar todas las compras realizadas
    public List<Compra> listarCompras(){
        return compraRepository.findAll();
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
