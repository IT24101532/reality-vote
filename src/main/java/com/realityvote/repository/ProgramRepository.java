package com.realityvote.repository;

import com.realityvote.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProgramRepository extends JpaRepository<Program, Long> {

    // ✅ Custom query method to fetch only active programs
    List<Program> findByActiveTrue();
}
