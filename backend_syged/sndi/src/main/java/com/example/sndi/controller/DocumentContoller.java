package com.example.sndi.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/document")
public class DocumentContoller {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/all")
    public List<Document> findAllDocuments() {
        return documentService.findAll();
    }

    @PostMapping("/save")
    @PermitAll
    public ResponseEntity<Object> process_document_request(@RequestBody Map<String, Object> request) {
        try {
            // Récupérer les objets principaux
            Object documentObj = request.get("document");
            Object utilisateurObj = request.get("utilisateur");
            String nomProjet = (String) request.get("nomProjet");
            String message = (String) request.get("message");
            String selectedType = (String) request.get("selectedType");

            // Validation des données requises
            if (documentObj == null || !(documentObj instanceof Map)) {
                return ResponseEntity.badRequest().body("Format de document invalide");
            }

            if (utilisateurObj == null) {
                return ResponseEntity.badRequest().body("Utilisateur requis");
            }

            // Récupérer la map des documents
            Map<String, Object> documentMap = (Map<String, Object>) documentObj;

            List<Document> documents = new ArrayList<>();

            // Traiter chaque type de document
            for (String typeDocument : documentMap.keySet()) {
                Object docTypeObj = documentMap.get(typeDocument);

                if (docTypeObj instanceof Map) {
                    Map<String, Object> docTypeMap = (Map<String, Object>) docTypeObj;

                    String nomFichier = (String) docTypeMap.get("nomFichier");
                    Object contenuFichier = docTypeMap.get("contenuFichier");

                    // Créer un document seulement si des données existent
                    if (nomFichier != null || contenuFichier != null) {
                        Document doc = new Document();

                        // Définir les propriétés du document
                        doc.setNomDocument(nomFichier);

                        doc.setContenuFichier((String) contenuFichier);
                        doc.setDateDepot(LocalDate.now()); // Date actuelle

                        // Récupérer et définir l'utilisateur
                        Map<String, Object> utilisateurMap = (Map<String, Object>) utilisateurObj;
                        Utilisateur user = new Utilisateur();
                        user.setIdUtilisateur(Long.valueOf(utilisateurMap.get("idUtilisateur").toString()));
                        doc.setUtilisateur(user);

                        // Créer et définir le projet
                        if (nomProjet != null && !nomProjet.trim().isEmpty()) {
                            Projet projet = new Projet();
                            projet.setNomProjet(nomProjet);
                            doc.setProjet(projet);
                        }
                        documents.add(doc);
                    }
                }
            }

            // Vérifier qu'au moins un document a été traité
            if (documents.isEmpty()) {
                return ResponseEntity.badRequest().body("Aucun document valide à traiter");
            }

            // Traitement via service
            return ResponseEntity.ok(documentService.process_document_request(documents));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors du traitement : " + e.getMessage());
        }
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
