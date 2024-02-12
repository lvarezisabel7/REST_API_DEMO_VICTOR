package com.example.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.dao.PresentacionDao;
import com.example.entities.Presentacion;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresentacionServiceImpl implements PresentacionService{

    private final PresentacionDao presentacionDao;

    @Override
    public List<Presentacion> findAll() {
        return presentacionDao.findAll();
    }

    @Override
    public Presentacion findById(int id) {
        return presentacionDao.findById(id).get();
    }

    @Override
    public void save(Presentacion presentacion) {
        presentacionDao.save(presentacion);
    }

    @Override
    public void delete(Presentacion presentacion) {
        presentacionDao.delete(presentacion);
    }

}
