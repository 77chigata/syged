package com.example.sndi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sndi.model.TypeDocument;
import com.example.sndi.repository.TypedocumentRepository;

@Service
public class TypedocumentService {
    
@Autowired
    private TypedocumentRepository typeDocumentRepository;

    public List<TypeDocument> getAllTypeDocuments() {
        return typeDocumentRepository.findAll();
    }

    public Optional<TypeDocument> getTypeDocumentById(Long id) {
        return typeDocumentRepository.findById(id);
    }

    public TypeDocument createTypeDocument(TypeDocument typeDocument) {
        return typeDocumentRepository.save(typeDocument);
    }

    public Optional<TypeDocument> updateTypeDocument(Long id, TypeDocument updated) {
        return typeDocumentRepository.findById(id).map(typeDocument -> {
            typeDocument.setExtensionFichier(updated.getExtensionFichier());
            typeDocument.setLibelle(updated.getLibelle());
            return typeDocumentRepository.save(typeDocument);
        });
    }

    public boolean deleteTypeDocument(Long id) {
        if (typeDocumentRepository.existsById(id)) {
            typeDocumentRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
