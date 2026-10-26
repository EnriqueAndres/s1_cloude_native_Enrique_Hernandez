package com.Duoc.semanaUno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Duoc.semanaUno.model.Entrada;

@Repository 
public interface EntradaRepository extends JpaRepository<Entrada, Long>{

}
