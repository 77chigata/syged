package com.example.sndi.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
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

import com.example.sndi.model.Document;
import com.example.sndi.model.Projet;
import com.example.sndi.service.DocumentService;
import com.example.sndi.service.ProjetService;

import jakarta.annotation.security.PermitAll;

@RestController
@RequestMapping("/document")
public class DocumentContoller {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private ProjetService projetService;

    @GetMapping("/all")
    public List<Document> findAllDocuments() throws Exception {
        return documentService.findAll();
    }

    @PostMapping("/save")
    @PermitAll
    public ResponseEntity<Object> process_document_request(@RequestBody Map<String, Object> request) {
     try {
            // 1. Récupération des champs généraux
            String nomProjet = (String) request.get("nomProjet");
            Map<String, Object> documentWrapper = (Map<String, Object>) request.get("document");

            // 2. Création du projet associé
            Projet projet = new Projet();
            projet.setNomProjet(nomProjet);
            Projet savedProjet = projetService.save(projet);

            // 3. Liste finale des documents à sauvegarder
            List<Document> documents = new ArrayList<>();

            // 4. Parcours dynamique des fichiers du documentWrapper
            for (Map.Entry<String, Object> entry : documentWrapper.entrySet()) {
                String documentType = entry.getKey(); // ex: "cahier_charge"
                Map<String, Object> docMap = (Map<String, Object>) entry.getValue();

                // Vérifie que le document est rempli
                String nomFichier = (String) docMap.get("nomFichier");
                String contenuBase64 = (String) docMap.get("contenuFichier");

                if (nomFichier != null && contenuBase64 != null) {
                    byte[] contenuBytes = Base64.getDecoder().decode(contenuBase64);

                    Document doc = new Document();
                    doc.setNomDocument(nomFichier);
                    doc.setContenuFichier(contenuBytes);
                    doc.setDateDepot(LocalDate.now());
                    doc.setProjet(savedProjet); // Lien avec le projet

                    documentService.save(doc);
                    documents.add(doc);
                }
            }

            return ResponseEntity.ok(documentService.process_document_request(documents));

        } catch (Exception e) {
            System.err.println("Erreur : " + e);
            return ResponseEntity.badRequest().body("Erreur de traitement : " + e.getMessage());
        }
    }

    @PutMapping("updated/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document details) {
        // on va cherche le document par son ID
        return documentService.findById(id).map(document -> {
            // on va mettre à jour le nom avec la nouvelle valeur reçue

            // on sauvegarde les modifications grâce au service
            Document updated = null;
            try {
                updated = documentService.save(document);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

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
