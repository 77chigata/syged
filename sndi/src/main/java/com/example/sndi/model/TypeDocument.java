package com.example.sndi.model;

import java.util.List;



import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TypeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTypedoc;

    private String extensionFichier;
    private int libelle;

    @OneToMany(mappedBy = "typeDocument")
    private List<Document> documents;
}
