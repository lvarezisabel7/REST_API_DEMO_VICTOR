package com.example.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.entities.Producto;
import com.example.helpers.FileDownloadUtil;
import com.example.helpers.FileUploadUtil;
import com.example.model.FileUploadResponse;
import com.example.services.ProductoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final FileUploadUtil fileUploadUtil;
    private final FileDownloadUtil fileDownloadUtil;

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
        if (page != null && size != null) {
            // Queremos devolver los productos paginados
            Pageable pageable = PageRequest.of(page, size, sortByName);
            Page<Producto> pageProductos = productoService.findAll(pageable);
            productos = pageProductos.getContent();
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
        } else {
            // Solo ordenamiento

            productos = productoService.findAll(sortByName);
            responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);

        }

        return responseEntity;
    }

    /**
     * Persiste un producto en la base de datos
     * @throws IOException
     * 
     * */
    // Guardar (Persistir), un producto, con su presentacion en la base de datos
    // Para probarlo con POSTMAN: Body -> form-data -> producto -> CONTENT TYPE ->
    // application/json
    // no se puede dejar el content type en Auto, porque de lo contrario asume
    // application/octet-stream
    // y genera una exception MediaTypeNotSupported
    @PostMapping(consumes = "multipart/form-data")
    @Transactional
    public ResponseEntity<Map<String, Object>> saveProduct(
            @Valid @RequestPart(name = "producto", required = true) Producto producto,
            BindingResult validationResults,
            @RequestPart(name = "file", required = false) MultipartFile file) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // Comprobar si el producto tiene errores
        if (validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errores.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", errores);
            responseAsMap.put("Producto Mal Formado", producto);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // Comprobamos si hay imagen para el producto
        if (file != null) {

            try {
                String fileName = file.getOriginalFilename();
                String fileCode = fileUploadUtil.saveFile(fileName, file);
                producto.setImagen(fileCode + "-" + fileName) ;

                // Hay que devolver informacion respecto al archivo que se ha guardado
                // , para lo cual en una capa model vamos a crear un Record con la info del
                // archivo que vamos a devolver.

            FileUploadResponse fileUploadResponse = FileUploadResponse
                       .builder()
                       .fileName(fileCode + "-" + fileName)
                       .downloadURI("/productos/downloadFile/" 
                                 + fileCode + "-" + fileName)
                       .size(file.getSize())
                       .build();
            
            responseAsMap.put("info de la imagen: ", fileUploadResponse);           

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // No hay errores en el producto, pues a persistir el producto

        try {
            Producto productoPersistido = productoService.save(producto);
            String successMessage = "El producto se ha persistido exitosamente ";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Producto Persistido", productoPersistido);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);
        } catch (DataAccessException e) {
            String error = "Error al intentar persistir el producto y la causa mas probable es: "
                    + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Producto que se ha intentado persistir", producto);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    // Metodo que actualiza un producto cuyo id recibe como parametro
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@Valid @RequestBody Producto producto,
            BindingResult validationResults,
            @PathVariable(name = "id", required = true) Integer idProducto) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // Comprobar si el producto tiene errores
        if (validationResults.hasErrors()) {

            List<String> errores = new ArrayList<>();

            List<ObjectError> objectErrors = validationResults.getAllErrors();

            objectErrors.forEach(objectError -> errores.add(objectError.getDefaultMessage()));

            responseAsMap.put("errores", errores);
            responseAsMap.put("Producto Mal Formado", producto);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);

            return responseEntity;

        }

        // No hay errores en el producto, pues actualizar el producto

        try {
            producto.setId(idProducto);
            Producto productoActualizado = productoService.save(producto);
            String successMessage = "El producto se ha actualizado exitosamente ";
            responseAsMap.put("Success Message", successMessage);
            responseAsMap.put("Producto Actualizado", productoActualizado);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String error = "Error al intentar actualizar el producto y la causa mas probable es: "
                    + e.getMostSpecificCause();
            responseAsMap.put("error", error);
            responseAsMap.put("Producto que se ha intentado actualizar", producto);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;

    }

    // Metodo que recupera un producto por el ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findProductById(
            @PathVariable(name = "id", required = true) Integer idProduct) throws IOException {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            Producto producto = productoService.findById(idProduct);

            if (producto != null) {
                String successMessage = "Producto con id " + idProduct + ", encontrado";
                responseAsMap.put("successMessage", successMessage);
                responseAsMap.put("producto", producto);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            } else {
                String errorMessage = "Producto con id " + idProduct + ", no encontrado";
                responseAsMap.put("error message", errorMessage);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);
            }

        } catch (DataAccessException e) {
            String errorGrave = "Se ha producido un error grave al buscar el producto con id " + idProduct +
                    ", y la causa mas probable es: " + e.getMostSpecificCause();
            responseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }

    // Eliminar un producto por el ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProductById(
            @PathVariable(name = "id", required = true) Integer idProduct) {
        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        try {
            productoService.delete(productoService.findById(idProduct));
            String successMessage = "Producto con id " + idProduct + ", eliminado exitosamente";
            responseAsMap.put("successMessage", successMessage);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
        } catch (DataAccessException e) {
            String errorGrave = "Error grave al intentar eliminar el poducto con id " + idProduct
                    + ", y la causa mas probable es: " + e.getMostSpecificCause();
            responseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    /**
     *  Implementa filedownnload end point API 
     **/    
    @GetMapping("/downloadFile/{fileCode}")
    public ResponseEntity<?> downloadFile(@PathVariable(name = "fileCode") String fileCode) {

        Resource resource = null;

        try {
            resource = fileDownloadUtil.getFileAsResource(fileCode);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }

        if (resource == null) {
            return new ResponseEntity<>("File not found ", HttpStatus.NOT_FOUND);
        }

        String contentType = "application/octet-stream";
        String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";

        return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(contentType))
        .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
        .body(resource);

    }  
}
