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

import com.example.sndi.model.Model;
import com.example.sndi.repository.ModelRepository;
import com.example.sndi.repository.ProjetRepository;

@RestController
@RequestMapping("/model")
public class ModelContoller {
    
     @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ProjetRepository projetRepository;

    @GetMapping
    public List<Model> getAllModels() {
        return modelRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model> getModelById(@PathVariable Long id) {
        Optional<Model> model = modelRepository.findById(id);
        return model.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Model> createModel(@RequestBody Model model) {
        // Vérifie si le projet associé existe
       
        Model savedModel = modelRepository.save(model);
        return ResponseEntity.ok(savedModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Model> updateModel(@PathVariable Long id, @RequestBody Model modelDetails) {
        Optional<Model> optionalModel = modelRepository.findById(id);
        if (!optionalModel.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Model model = optionalModel.get();
        model.setLibelleModel(modelDetails.getLibelleModel());
        model.setNombreFichier(modelDetails.getNombreFichier());
        model.setChamps(modelDetails.getChamps());
      

        Model updatedModel = modelRepository.save(model);
        return ResponseEntity.ok(updatedModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        if (!modelRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        modelRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
