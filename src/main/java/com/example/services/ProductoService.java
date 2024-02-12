package com.example.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.entities.Producto;

public interface ProductoService {

    public Page<Producto> findAll(Pageable pageable);
    public List<Producto> findAll(Sort sort);
    public Producto findById(int id);
    public Producto save(Producto producto);
    public void delete(Producto producto);

}
