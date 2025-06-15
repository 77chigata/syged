package com.example.sndi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTypeUtilisateur;

    private String libelleTypeUtilisateur;
}
