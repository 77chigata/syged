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
public class Partage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codePartage;

    private String message;

    @ManyToOne
    @JoinColumn(name = "IdDestinataire")
    private Utilisateur Destinataire;

    @ManyToOne
    @JoinColumn(name = "IdEmeteur")
    private Utilisateur Emetteur;

    @ManyToOne
    @JoinColumn(name = "IdDocument")
    private Document document;
    
}
