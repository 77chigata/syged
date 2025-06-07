package com.example.sndi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sndi.model.Departement;
import com.example.sndi.model.Document;
import com.example.sndi.model.Projet;
import com.example.sndi.repository.DepartementRepository;
import com.example.sndi.repository.DocumentRepository;

@Service
public class DocumentService {
   
    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ProjetService projetService;


    public List<Document>findAll(){
        return documentRepository.findAll();
    }

    public Optional<Document>finById(Long Id){
        return documentRepository.findById(Id);
    }

    @Transactional
    public Document save( Document document){
        getThis().beforeSave(document);
        Projet projet= projetService.findByNomProjet(document.getProjet().getNomProjet());
        document.getProjet().setIdProjet(projet.getIdProjet());
        return documentRepository.save(document);
    }

    @Transactional
    public Object process_document_request( Object payload){
        System.out.println("Document request: " + payload);
        return payload; // Placeholder for actual processing logic
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }
    
    public void deleteById(Long id) {
        documentRepository.deleteById(id);
    }

    @Transactional
    public Projet beforeSave(Document document ){
        return projetService.save(document.getProjet());
    }
        
    protected DocumentService getThis(){
        return this;
    }
}
