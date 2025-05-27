package com.example.sndi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sndi.model.Departement;
import com.example.sndi.repository.DepartementRepository;

@Service
public class DepartementService {
    @Autowired
    private DepartementRepository departementRepository;
    public List<Departement>findAll(){
        return departementRepository.findAll();
    }

    public Optional<Departement>finById(Long Id){
        return departementRepository.findById(Id);
    }

    public Departement save( Departement departement){
        return departementRepository.save(departement);
    }

    public Optional<Departement> findById(Long id) {
        return departementRepository.findById(id);
    }
    
    public void deleteById(Long id) {
        departementRepository.deleteById(id);
    }
    

    
}
