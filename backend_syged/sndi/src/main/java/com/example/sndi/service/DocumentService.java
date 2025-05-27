package com.example.sndi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sndi.model.Departement;
import com.example.sndi.model.Document;
import com.example.sndi.repository.DepartementRepository;
import com.example.sndi.repository.DocumentRepository;

@Service
public class DocumentService {
   
    @Autowired
    private DocumentRepository documentRepository;
    public List<Document>findAll(){
        return documentRepository.findAll();
    }

    public Optional<Document>finById(Long Id){
        return documentRepository.findById(Id);
    }

    public Document save( Document document){
        return documentRepository.save(document);
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }
    
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }


    
}
