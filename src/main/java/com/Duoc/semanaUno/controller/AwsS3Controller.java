package com.Duoc.semanaUno.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.Duoc.semanaUno.service.EfsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/s3")
@RequiredArgsConstructor
public class AwsS3Controller {

    @Autowired
    private AwsS3Service awsS3Service;

    @Autowired
    private EfsService efsService;

    @GetMapping("/{bucket}/objects")
    public ResponseEntity<List<S3ObjectDto>> listObjects(@PathVariable String bucket) {
        List<S3ObjectDto> dtoList = awsS3Service.listObjects(bucket);
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<Void> subirResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam("file") MultipartFile file) {
        try {
            String key = numeroResumen + "/" + file.getOriginalFilename();
            efsService.saveToEfs(key, file);
            awsS3Service.upload(bucket, key, file);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<Void> modificarResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String fileName) {
        try {
            String nombreArchivo = (fileName != null && !fileName.isBlank())
                    ? fileName
                    : file.getOriginalFilename();
            String key = numeroResumen + "/" + nombreArchivo;
            efsService.saveToEfs(key, file);
            awsS3Service.upload(bucket, key, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<byte[]> descargarResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam(required = false) String fileName) {
        String nombreArchivo = (fileName != null && !fileName.isBlank())
                ? fileName
                : "resumen_compra_" + numeroResumen + ".txt";
        String key = numeroResumen + "/" + nombreArchivo;
        byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }

    @DeleteMapping("/{bucket}/resumen/{numeroResumen}")
    public ResponseEntity<Void> borrarResumenCompra(
            @PathVariable String bucket,
            @PathVariable Long numeroResumen,
            @RequestParam(required = false) String fileName) {
        String nombreArchivo = (fileName != null && !fileName.isBlank())
                ? fileName
                : "resumen_compra_" + numeroResumen + ".txt";
        String key = numeroResumen + "/" + nombreArchivo;
        awsS3Service.deleteObject(bucket, key);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{bucket}/object")
    public ResponseEntity<byte[]> downloadObject(@PathVariable String bucket, @RequestParam String key) {
        byte[] fileBytes = awsS3Service.downloadAsBytes(bucket, key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileBytes);
    }

    @PostMapping({"/{bucket}/object", "/{bucket}/objects"})
    public ResponseEntity<Void> uploadObject(
            @PathVariable String bucket,
            @RequestParam String key,
            @RequestParam("file") MultipartFile file) {
        try {
            efsService.saveToEfs(key, file);
            awsS3Service.upload(bucket, key, file);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping({"/{bucket}/object", "/{bucket}/objects"})
    public ResponseEntity<Void> updateObject(
            @PathVariable String bucket,
            @RequestParam String key,
            @RequestParam("file") MultipartFile file) {
        try {
            efsService.saveToEfs(key, file);
            awsS3Service.upload(bucket, key, file);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{bucket}/move")
    public ResponseEntity<Void> moveObject(
            @PathVariable String bucket,
            @RequestParam String sourceKey,
            @RequestParam String destKey) {
        awsS3Service.moveObject(bucket, sourceKey, destKey);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping({"/{bucket}/object", "/{bucket}/objects"})
    public ResponseEntity<Void> deleteObject(@PathVariable String bucket, @RequestParam String key) {
        awsS3Service.deleteObject(bucket, key);
        return ResponseEntity.noContent().build();
    }
}
