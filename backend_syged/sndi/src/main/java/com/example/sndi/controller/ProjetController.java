package com.example.sndi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sndi.model.Projet;
import com.example.sndi.service.ProjetService;

@RestController
@RequestMapping("/projet")
public class ProjetController {
    
    @Autowired
    private ProjetService projetService;

    @PostMapping("/save")
    private ResponseEntity<Projet> save(@RequestBody Projet request){
        
        Projet savProjet= projetService.save(request);
        return ResponseEntity.ok(savProjet);
    }
}
