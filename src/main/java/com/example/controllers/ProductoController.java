package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {


    private final ProductoService productoService;

    // El metodo responde a una request del tipo 
    // http://localhost:8080/productos?page=0&size=3
    // Si no se especifica page y size entonces que devuelva los productos ordenados
    // por el nombre, por ejemplo 
    @GetMapping
    public ResponseEntity<List<Producto>> findAll(
           @RequestParam(name = "page", required = false) Integer page,
           @RequestParam(name = "size", required = false) Integer size) {

        ResponseEntity<List<Producto>> responseEntity = null;   
        Sort sortByName = Sort.by("name");
        List<Producto> productos = new ArrayList<>();

        // Comprobamos si han enviado page y size
        if(page != null && size != null) {
            // Queremos devolver los productos paginados
            Pageable pageable = PageRequest.of(page, size, sortByName);
            Page<Producto> pageProductos =  productoService.findAll(pageable);   
            productos = pageProductos.getContent();
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
        } else {
           // Solo ordenamiento

           productos = productoService.findAll(sortByName); 
           responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);

        }

        return responseEntity;
    }

    // Metodo que persiste un producto y valida que el producto este bien formado
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveProduct(@Valid @RequestBody Producto producto, 
                     BindingResult validationResults)  {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // Comprobar si el producto tiene errores
        if (validationResults.hasErrors()) {
    
            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errores.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", errores);
            responseAsMap.put("Producto Mal Formado", producto);

            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // No hay errores en el producto, pues a persistir el producto
        
        try {
            Producto productoPersistido = productoService.save(producto);
            String successMessage = "El producto se ha persistido exitosamente ";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Producto Persistido", productoPersistido );
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            String error = "Error al intentar persistir el producto y la causa mas probable es: "
                          + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Producto que se ha intentado persistir", producto);
            responseEntity = new ResponseEntity<Map<String,Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return responseEntity;

    }
} 
