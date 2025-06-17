package com.example.sndi.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

import com.example.sndi.model.Departement;
import com.example.sndi.model.Document;
import com.example.sndi.model.Projet;
import com.example.sndi.model.Utilisateur;
import com.example.sndi.repository.DepartementRepository;
import com.example.sndi.repository.DocumentRepository;
import com.example.sndi.service.DepartementService;
import com.example.sndi.service.DocumentService;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/document")
public class DocumentContoller {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/save")
    @PermitAll
    public ResponseEntity<Object> process_document_request(@RequestBody Map<String, Object> request) {
        List<Map<String, Object>> champs = (List<Map<String, Object>>) request.get("document");

        List<Document> documents = new ArrayList<>();

        for (Map<String, Object> champ : champs) {
            Document doc = new Document();

            doc.setNomDocument((String) champ.get("nomDocument"));
            doc.setContenuFichier((Byte[]) champ.get("contenuFichier"));
            doc.setDateDepot(LocalDate.parse((String) champ.get("dateDepot")));

            Map<String, Object> utilisateurMap = (Map<String, Object>) champ.get("utilisateur");
            Utilisateur user = new Utilisateur();
            user.setIdUtilisateur(Long.valueOf(utilisateurMap.get("idUtilisateur").toString()));
            doc.setUtilisateur(user);

            Map<String, Object> projetMap = (Map<String, Object>) champ.get("projet");
            Projet projet = new Projet();
            projet.setNomProjet((String) (projetMap.get("nomProjet").toString()));
            doc.setProjet(projet);

            documents.add(doc);
        }

        // Traitement via service
        return ResponseEntity.ok(documentService.process_document_request(documents));
    }

    @PutMapping("updated/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document details) {
        // on va cherche le document par son ID
        return documentService.findById(id).map(document -> {
            // on va mettre à jour le nom avec la nouvelle valeur reçue

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
