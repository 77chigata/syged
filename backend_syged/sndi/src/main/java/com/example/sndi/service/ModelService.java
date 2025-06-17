package com.example.sndi.service;

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

import com.example.sndi.model.Model;
import com.example.sndi.repository.ModelRepository;

public class ModelService {
    
    @Autowired
    private ModelRepository modelRepository;

    // 🔹 Créer un modèle
    @PostMapping
    public Model createModel(@RequestBody Model model) {
        return modelRepository.save(model);
    }

    // 🔹 Lister tous les modèles
    @GetMapping
    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    // 🔹 Obtenir un modèle par ID
    @GetMapping("/{id}")
    public ResponseEntity<Model> getModelById(@PathVariable Long id) {
        Optional<Model> model = modelRepository.findById(id);
        return model.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

   

    // 🔹 Supprimer un modèle
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        if (modelRepository.existsById(id)) {
            modelRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
