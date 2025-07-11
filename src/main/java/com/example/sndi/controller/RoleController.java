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

import com.example.sndi.model.Role;

import com.example.sndi.repository.RoleRepository;
import com.example.sndi.service.RoleService;

import jakarta.persistence.SqlResultSetMapping;

@RestController
@RequestMapping("/role")
public class RoleController {
    
    @Autowired 
    private RoleRepository roleRepository;
    @Autowired
    private RoleService roleService;

    
    @GetMapping("")
    private List<Role> findAll(){
        return roleService.getAllRoles();
    }
    @PostMapping("/save")
    private ResponseEntity<Role> save(@RequestBody Role request){
                  // on sauvegarde le département via le service
        Role savRole= roleService.createRole(request);

        return ResponseEntity.ok(savRole);
    }
    
    @PutMapping("updated/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Long id, @RequestBody Role details) {
        // on va cherche le département par son ID
        return roleRepository.findById(id).map(Role -> {
            // on va mettre à jour le nom avec la nouvelle valeur reçue
            Role.setLibelleRole(details.getLibelleRole());
    
            // on sauvegarde les modifications grâce au service
            Role updated = roleService.createRole(Role);
    
            // on retourne une réponse HTTP 200 OK avec le département modifié
            return ResponseEntity.ok(updated);
        }).orElseGet(() -> {
            // si aucun département trouvé, on retourne 404 Not Found
            return ResponseEntity.notFound().build();
        });


    }
    
    @DeleteMapping("delete/{id}")   
public ResponseEntity<Object> deleteRole(@PathVariable Long id) {
    // on vérifie si le département existe
    return roleRepository.findById(id).map(Role -> {
        // si oui on supprime
        roleRepository.delete(Role);

        // on retourne une réponse vide avec le code HTTP 204 No Content
        return ResponseEntity.noContent().build();
    }).orElseGet(() -> {
        // si non on retourne 404 Not Found
        return ResponseEntity.notFound().build();
    });
}
}
