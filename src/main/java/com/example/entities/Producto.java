package com.example.entities;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "productos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Producto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "El nombre no puede ser nulo")
    @NotEmpty(message = "El nombre del producto no puede estar vacio")
    @Size(min = 4, max = 25 , message = "El nombre no puede tener menos de 4 caracteres ni mas de 25")
    private String name;

    @NotBlank(message = "La descripcion del producto es requerida")
    private String description;

    @Min(value = 0, message = "El stock no puede ser menor que cero")
    private int stock;

    @Min(value = 0, message = "El precio no puede ser negativo")
    private double price;

    private String imagen;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    // https://stackoverflow.com/questions/67353793/what-does-jsonignorepropertieshibernatelazyinitializer-handler-do
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Presentacion presentacion;

}
