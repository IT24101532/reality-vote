package com.realityvote.repository;

import com.realityvote.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProgramRepository extends JpaRepository<Program, Long> { }
