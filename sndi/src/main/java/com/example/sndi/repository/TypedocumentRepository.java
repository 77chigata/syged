package com.example.sndi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sndi.model.TypeDocument;

import java.util.Optional;


public interface TypedocumentRepository extends JpaRepository <TypeDocument, Long> {

    Optional<TypeDocument> findByLibelle(int libelle);

    
}
