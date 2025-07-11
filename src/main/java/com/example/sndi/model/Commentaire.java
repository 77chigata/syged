package com.example.sndi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Commentaire {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCommantaire;

    private String detailCommentaire;

    @ManyToOne
    @JoinColumn(name = "idDocument")
    private Document document;


    @ManyToOne
    @JoinColumn(name = "idUtilisateur")
    private Utilisateur utilisateur;
}
