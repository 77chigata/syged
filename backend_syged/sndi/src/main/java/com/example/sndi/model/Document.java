package com.example.sndi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@SessionAttributes

@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    private String nomDocument;
    private String cheminFichier;
    private LocalDate dateDepot;
    
    private byte[] data;
    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;


    @ManyToOne
    @JoinColumn(name = "id_type_document")
    private TypeDocument typeDocument;

    @ManyToOne
    @JoinColumn(name = "id_projet")
    private Projet projet;

    public void setNomDocument(String nomDocument2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setNomDocument'");
    }

}
