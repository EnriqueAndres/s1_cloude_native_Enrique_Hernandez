package com.Duoc.semanaUno.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping ("/microservicio")
public class MicroservicioController {

    @PostMapping
    public ResponseEntity<String> create(@RequestBody Map<String, String> body) {

        System.out.println("Se recibió llamada a endpoint POST");
        return ResponseEntity.ok("Integración OK - POST, recibido body: " + body);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> read(@PathVariable("id") String id) {

        System.out.println("Se recibió llamada a endpoint GET");
        return ResponseEntity.ok("Integración OK - GET, recibido path variable: " + id);
    }

    @PutMapping
    public ResponseEntity<String> update(@RequestParam("status") String status) {

        System.out.println("Se recibió llamada a endpoint PUT");
        return ResponseEntity.ok("Integración OK - PUT, recibido query param: " + status);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String authHeader) {

        System.out.println("Se recibió llamada a endpoint DELETE");
        return ResponseEntity.ok("Integración OK - DELETE, recibido header: " + authHeader);
    }
}
