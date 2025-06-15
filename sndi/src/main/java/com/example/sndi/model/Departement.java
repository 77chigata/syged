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
public class Departement {
// les attribures dans la classe
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDepartement;

    private String nomDepartement;
}
