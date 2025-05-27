package com.example.sndi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sndi.model.Departement;
import com.example.sndi.model.Document;
import com.example.sndi.repository.DepartementRepository;
import com.example.sndi.repository.DocumentRepository;
import com.example.sndi.service.DepartementService;
import com.example.sndi.service.DocumentService;

@RestController
@RequestMapping("/document")
public class DocumentContoller {
    
   

    @Autowired
    private DocumentService documentService;
 
    

    @PostMapping("/save")
    private ResponseEntity<Document> save(@RequestBody Document request){
                  // on sauvegarde le document via le service
        Document savDocument= documentService.save(request);

        return ResponseEntity.ok(savDocument);
    }
    
    @PutMapping("updated/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document details) {
        // on va cherche le document par son ID
        return documentService.findById(id).map(document -> {
            // on va mettre à jour le nom avec la nouvelle valeur reçue
            document.setNomDocument(details.getNomDocument());
    
            // on sauvegarde les modifications grâce au service
            Document updated = documentService.save(document);
    
            // on retourne une réponse HTTP 200 OK avec le département modifié
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> {
            // si aucun document trouvé, on retourne 404 Not Found
            return ResponseEntity.notFound().build();
        });


    }
    

    

    @DeleteMapping("delete/{id}")
public ResponseEntity<Object> deleteDocument(@PathVariable Long id) {
    // on vérifie si le document existe
    return documentService.findById(id).map(document -> {
        // si oui on supprime
        documentService.deleteById(id);

        // on retourne une réponse vide avec le code HTTP 204 No Content
        return ResponseEntity.noContent().build();
    }).orElseGet(() -> {
        // si non on retourne 404 Not Found
        return ResponseEntity.notFound().build();
    });
}



    
}
