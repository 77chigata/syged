package com.example.sndi.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.web.bind.annotation.SessionAttributes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@SessionAttributes

@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    private String nomDocument;

    private String clesymetrique;

    @Lob
    @Column
    private byte[] contenuFichier;

    private LocalDate dateDepot;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    private User utilisateur;

    @ManyToOne
    @JoinColumn(name = "id_type_document")
    private TypeDocument typeDocument;

    @ManyToOne
    @JoinColumn(name = "id_projet")
    private Projet projet;

}
