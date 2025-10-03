package com.realityvote.repository;

import com.realityvote.model.Contestant;
import com.realityvote.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface ContestantRepository extends JpaRepository<Contestant, Long> {
    List<Contestant> findByProgram(Program program);
    Optional<Contestant> findByEmail(String email); // <— NEW
}

