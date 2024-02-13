package com.example.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {


    private final ProductoService productoService;

    // Metodo que devuelve todos los productos en formato JSON
    @GetMapping
    public ResponseEntity<List<Producto>> findAll() {

        var productos = productoService.findAll();

        ResponseEntity<List<Producto>> responseEntity; 
        responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.CREATED);

        return responseEntity;
    }

}
