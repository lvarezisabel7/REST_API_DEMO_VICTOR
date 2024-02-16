package com.example.helpers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.entities.Presentacion;
import com.example.entities.Producto;
import com.example.services.PresentacionService;
import com.example.services.ProductoService;

@Configuration
public class LoadSampleData {

    @Bean
    public CommandLineRunner saveSampleData(ProductoService productoService,
            PresentacionService presentacionService) {

        return datos -> {

            presentacionService.save(Presentacion.builder()
                    .name("unidad")
                    .build());

            presentacionService.save(Presentacion.builder()
                    .name("docena")
                    .build());

            productoService.save(Producto.builder()
                    .name("rezma de papel")
                    .description("Desc 1")
                    .stock(10)
                    .price(3.75)
                    .presentacion(presentacionService.findById(1))
                    .build());

            productoService.save(Producto.builder()
                    .name("cartas")
                    .description("Desc 2")
                    .stock(1)
                    .price(10)
                    .presentacion(presentacionService.findById(2))
                    .build());

            productoService.save(Producto.builder()
                    .name("guitarra de juguete")
                    .description("Desc 3")
                    .stock(5)
                    .price(4.5)
                    .presentacion(presentacionService.findById(1))
                    .build());

            productoService.save(Producto.builder()
                    .name("teclado para computadora")
                    .description("Desc 4")
                    .stock(15)
                    .price(5)
                    .presentacion(presentacionService.findById(1))
                    .build());

            productoService.save(Producto.builder()
                    .name("teclado para laptop")
                    .description("Desc 5")
                    .stock(40)
                    .price(5)
                    .presentacion(presentacionService.findById(1))
                    .build());

            productoService.save(Producto.builder()
                    .name("bocinas bluetooth")
                    .description("Desc 6")
                    .stock(15)
                    .price(5)
                    .presentacion(presentacionService.findById(1))
                    .build());

            productoService.save(Producto.builder()
                    .name("lapices 2b")
                    .description("Desc 7")
                    .stock(4)
                    .price(1.50)
                    .presentacion(presentacionService.findById(2))
                    .build());

            productoService.save(Producto.builder()
                    .name("plumas color azul")
                    .description("Desc 8")
                    .stock(2)
                    .price(10)
                    .presentacion(presentacionService.findById(1))
                    .build());

            productoService.save(Producto.builder()
                    .name("monitor dell 15p")
                    .description("Desc 9")
                    .stock(40)
                    .price(5)
                    .presentacion(presentacionService.findById(1))
                    .build());

            productoService.save(Producto.builder()
                    .name("cargador samsung")
                    .description("Desc 10")
                    .stock(10)
                    .price(10)
                    .presentacion(presentacionService.findById(2))
                    .build());

            productoService.save(Producto.builder()
                    .name("mouse básico")
                    .description("Desc 11")
                    .stock(5)
                    .price(5)
                    .presentacion(presentacionService.findById(2))
                    .build());

        };

    }
}
