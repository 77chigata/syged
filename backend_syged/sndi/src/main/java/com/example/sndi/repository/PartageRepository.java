package com.example.sndi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sndi.model.Partage;
import com.example.sndi.model.User;



public interface PartageRepository extends JpaRepository <Partage,Long> {
    
    List<Partage> findByDestinataireId(Long idDestinataire);
    List<Partage> findByEmetteurId(Long idEmetteur);
}
