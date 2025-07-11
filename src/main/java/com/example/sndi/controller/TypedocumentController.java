package com.example.sndi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sndi.model.TypeDocument;
import com.example.sndi.service.TypedocumentService;

@RestController
@RequestMapping("/typedocument")
public class TypedocumentController {
    
 @Autowired
    private TypedocumentService typeDocumentService;

    @GetMapping
    public List<TypeDocument> getAllTypeDocuments() {
        return typeDocumentService.getAllTypeDocuments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TypeDocument> getTypeDocumentById(@PathVariable Long id) {
        return typeDocumentService.getTypeDocumentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TypeDocument> createTypeDocument(@RequestBody TypeDocument typeDocument) {
        return ResponseEntity.ok(typeDocumentService.createTypeDocument(typeDocument));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeDocument> updateTypeDocument(@PathVariable Long id,
                                                           @RequestBody TypeDocument typeDocument) {
        return typeDocumentService.updateTypeDocument(id, typeDocument)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTypeDocument(@PathVariable Long id) {
        boolean deleted = typeDocumentService.deleteTypeDocument(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
    
}
