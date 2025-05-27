package com.example.sndi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sndi.model.Document;

public interface DocumentRepository  extends JpaRepository<Document,Long>{

     List<Document> findByUtilisateurIdUtilisateur(Long userId);



    
}
