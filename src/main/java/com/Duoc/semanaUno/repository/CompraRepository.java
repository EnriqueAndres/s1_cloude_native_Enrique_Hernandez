package com.Duoc.semanaUno.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Duoc.semanaUno.model.Compra;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

}
