package com.example.sndi.dto;

import com.example.sndi.model.Departement;
import com.example.sndi.model.TypeUtilisateur;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {

  
    private String name;

    private String username;

    private String password;
    private String contact;
    private Departement departement;
    private TypeUtilisateur typeUtilisateur;

}
