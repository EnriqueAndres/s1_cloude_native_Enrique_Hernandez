package com.Duoc.semanaUno.service;

import org.springframework.stereotype.Service;

import com.Duoc.semanaUno.repository.CompraRepository;
import com.Duoc.semanaUno.repository.EntradaRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    //comprar entradas de un evento y generar archivo físico del resumen
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

        Compra compraGuardada = compraRepository.save(compra);

        // Generar archivo físico en la carpeta local "resumenes/"
        generarArchivoResumen(compraGuardada);

        return compraGuardada;
    }

    // Construye el contenido de texto del resumen y lo guarda en disco
    public Path generarArchivoResumen(Compra compra) {
        String contenido = construirTextoResumen(compra);
        try {
            Path carpeta = Paths.get("resumenes");
            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }
            Path archivo = carpeta.resolve("resumen_compra_" + compra.getId() + ".txt");
            Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
            return archivo;
        } catch (IOException e) {
            throw new RuntimeException("Error al generar el archivo físico del resumen: " + e.getMessage(), e);
        }
    }

    // Devuelve los bytes del archivo de resumen para descargarlo desde Postman
    public byte[] obtenerBytesResumen(Long compraId) {
        Compra compra = compraRepository.findById(compraId)
            .orElseThrow(() -> new RuntimeException("No se encontró la compra con el ID: " + compraId));
        Path archivo = generarArchivoResumen(compra);
        try {
            return Files.readAllBytes(archivo);
        } catch (IOException e) {
            return construirTextoResumen(compra).getBytes(StandardCharsets.UTF_8);
        }
    }

    private String construirTextoResumen(Compra compra) {
        return "========================================\n"
             + "           RESUMEN DE COMPRA            \n"
             + "========================================\n"
             + "N° de Resumen (ID) : " + compra.getId() + "\n"
             + "ID del Evento      : " + compra.getEventoId() + "\n"
             + "Evento             : " + compra.getEvento() + "\n"
             + "Fecha del Evento   : " + compra.getFechaEvento() + "\n"
             + "Cantidad Entradas  : " + compra.getCantidadEntradas() + "\n"
             + "Precio Unitario    : $" + compra.getPrecioUnitario() + "\n"
             + "Precio Total       : $" + compra.getPrecioTotal() + "\n"
             + "========================================\n";
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
