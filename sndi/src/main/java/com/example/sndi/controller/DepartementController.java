package com.example.sndi.controller;

import java.util.List;
import java.util.Optional;

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
import com.example.sndi.repository.DepartementRepository;
import com.example.sndi.service.DepartementService;

@RestController
@RequestMapping("/departement")
public class DepartementController {

    @Autowired
    private  DepartementRepository departementRepository;

    @Autowired
    private DepartementService departementService;



    @GetMapping("")
    private List<Departement> findAll(){
        return departementService.findAll();
    }
    @PostMapping("/save")
    private ResponseEntity<Departement> save(@RequestBody Departement request){
                  // on sauvegarde le département via le service
        Departement savDepartement= departementService.save(request);

        return ResponseEntity.ok(savDepartement);
    }
    
    @PutMapping("updated/{id}")
    public ResponseEntity<Departement> updateDepartement(@PathVariable Long id, @RequestBody Departement details) {
        // on va cherche le département par son ID
        return departementRepository.findById(id).map(departement -> {
            // on va mettre à jour le nom avec la nouvelle valeur reçue
            departement.setNomDepartement(details.getNomDepartement());
    
            // on sauvegarde les modifications grâce au service
            Departement updated = departementService.save(departement);
    
            // on retourne une réponse HTTP 200 OK avec le département modifié
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> {
            // si aucun département trouvé, on retourne 404 Not Found
            return ResponseEntity.notFound().build();
        });


    }
    
    @DeleteMapping("delete/{id}")
public ResponseEntity<Object> deleteDepartement(@PathVariable Long id) {
    // on vérifie si le département existe
    return departementRepository.findById(id).map(departement -> {
        // si oui on supprime
        departementRepository.delete(departement);

        // on retourne une réponse vide avec le code HTTP 204 No Content
        return ResponseEntity.noContent().build();
    }).orElseGet(() -> {
        // si non on retourne 404 Not Found
        return ResponseEntity.notFound().build();
    });
}



}
