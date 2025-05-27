package com.example.sndi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sndi.model.Projet;
import com.example.sndi.repository.ProjetRepository;

@Service
public class ProjetService {

    @Autowired 
    private ProjetRepository projetRepository;

    public List<Projet>findAll(){
        return projetRepository.findAll();

    }
    public Optional<Projet>findById(Long Id){
    return projetRepository.findById(Id);
    }
    
    public Projet save(Projet projet){
        return projetRepository.save(projet);
    }
}
