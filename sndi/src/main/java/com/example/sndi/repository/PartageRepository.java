package com.example.sndi.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.sndi.model.Document;
import com.example.sndi.model.Partage;


public interface PartageRepository extends JpaRepository<Partage, Long> 
{
     
}
