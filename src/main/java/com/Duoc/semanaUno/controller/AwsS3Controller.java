package com.Duoc.semanaUno.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Duoc.semanaUno.dto.S3ObjectDto;
import com.Duoc.semanaUno.service.AwsS3Service;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    // Listar todos los objetos del bucket
    @GetMapping("/{bucket}/objects")
    public List<S3ObjectDto> listObjects(@PathVariable String bucket) {
        return awsS3Service.listObjects(bucket);
    }

    // =========================================================================
    // ENDPOINTS PARA RESUMEN DE COMPRA EN CARPETA <numeroResumen>/
    // =========================================================================

    // 1. Subir archivo de resumen de compra a la carpeta <numeroResumen>/ en S3
    @PostMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<String> subirResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam("file") MultipartFile file) {
        String key = numeroResumen + "/" + file.getOriginalFilename();
        awsS3Service.upload(bucket, key, file);
        return new ResponseEntity<>("Resumen subido exitosamente en S3: " + key, HttpStatus.CREATED);
    }

    // 2. Modificar (reemplazar) el archivo del resumen de compra en S3
    @PutMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<String> modificarResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String fileName) {
        String nombreArchivo = (fileName != null && !fileName.isBlank())
                ? fileName
                : file.getOriginalFilename();
        String key = numeroResumen + "/" + nombreArchivo;
        awsS3Service.upload(bucket, key, file);
        return ResponseEntity.ok("Resumen modificado exitosamente en S3: " + key);
    }

    // 3. Descargar el archivo del resumen de compra desde S3
    @GetMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<byte[]> descargarResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam(required = false) String fileName) {
        String nombreArchivo = (fileName != null && !fileName.isBlank())
                ? fileName
                : "resumen_compra_" + numeroResumen + ".txt";
        String key = numeroResumen + "/" + nombreArchivo;
        byte[] bytes = awsS3Service.downloadAsBytes(bucket, key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    // 4. Borrar el archivo del resumen de compra de S3
    @DeleteMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<String> borrarResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam(required = false) String fileName) {
        String nombreArchivo = (fileName != null && !fileName.isBlank())
                ? fileName
                : "resumen_compra_" + numeroResumen + ".txt";
        String key = numeroResumen + "/" + nombreArchivo;
        awsS3Service.deleteObject(bucket, key);
        return ResponseEntity.ok("Resumen eliminado exitosamente de S3: " + key);
    }

    // =========================================================================
    // ENDPOINTS GENÉRICOS POR KEY
    // =========================================================================

    @GetMapping("/{bucket}/object")
    public ResponseEntity<byte[]> download(@PathVariable String bucket, @RequestParam String key) {
        byte[] bytes = awsS3Service.downloadAsBytes(bucket, key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @PostMapping({"/{bucket}/object", "/{bucket}/objects"})
    public ResponseEntity<String> upload(
            @PathVariable String bucket,
            @RequestParam String key,
            @RequestParam("file") MultipartFile file) {
        awsS3Service.upload(bucket, key, file);
        return new ResponseEntity<>("Archivo subido correctamente: " + key, HttpStatus.CREATED);
    }

    @PutMapping({"/{bucket}/object", "/{bucket}/objects"})
    public ResponseEntity<String> update(
            @PathVariable String bucket,
            @RequestParam String key,
            @RequestParam("file") MultipartFile file) {
        awsS3Service.upload(bucket, key, file);
        return ResponseEntity.ok("Archivo modificado correctamente: " + key);
    }

    @PostMapping("/{bucket}/move")
    public ResponseEntity<String> move(
            @PathVariable String bucket,
            @RequestParam String sourceKey,
            @RequestParam String destKey) {
        awsS3Service.moveObject(bucket, sourceKey, destKey);
        return ResponseEntity.ok("Objeto movido de " + sourceKey + " a " + destKey);
    }

    @DeleteMapping({"/{bucket}/object", "/{bucket}/objects"})
    public ResponseEntity<Void> delete(@PathVariable String bucket, @RequestParam String key) {
        awsS3Service.deleteObject(bucket, key);
        return ResponseEntity.noContent().build();
    }
}
