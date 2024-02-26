package com.example.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.security.entities.OurUser;


public interface OurUserRepository extends JpaRepository<OurUser, Integer> {
    Optional<OurUser> findByEmail(String email);
}
