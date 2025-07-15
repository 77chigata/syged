package com.example.sndi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.sndi.model.Partage;
import com.example.sndi.model.Projet;
import com.example.sndi.service.PartageService;

@RestController
@RequestMapping("/partage")
public class PartageController {

    @Autowired
    private PartageService partageService;

    @PostMapping("/save")
    private ResponseEntity<Partage> savePartage(@RequestBody Partage request) throws Exception {
        Partage savePartage = partageService.save(request);
        return ResponseEntity.ok(savePartage);
    }

}
