package com.example.sndi.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Partage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codePartage;

    private LocalDate datePartage;

    private String message;

    @ManyToOne
    @JoinColumn(name = "IdDestinataire")
    private User destinataire;

    @ManyToOne
    @JoinColumn(name = "IdEmeteur")
    private User emetteur;

    @Column(name = "document_partage", columnDefinition = "TEXT")
    private String documentPartage;

    @Transient
    @ManyToOne
    @JoinColumn(name = "IdDocument")
    private Document document;

}
