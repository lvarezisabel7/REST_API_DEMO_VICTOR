package com.example.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.entities.Presentacion;

@Repository
public interface PresentacionDao  extends JpaRepository<Presentacion, Integer> {

}
