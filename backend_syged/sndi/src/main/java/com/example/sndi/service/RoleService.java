package com.example.sndi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.sndi.model.Role;
import com.example.sndi.repository.RoleRepository;


@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Récupérer tous les rôles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Récupérer un rôle par son ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // Créer un nouveau rôle
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    // Mettre à jour un rôle existant
    public Optional<Role> updateRole(Long id, Role updatedRole) {
        return roleRepository.findById(id).map(role -> {
            role.setLibelleRole(updatedRole.getLibelleRole());
            return roleRepository.save(role);
        });
    }

    // Supprimer un rôle par ID
    public boolean deleteRole(Long id) {
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
}
