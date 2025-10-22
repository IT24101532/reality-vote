// ContestantRepository.java
package com.realityvote.repository;

import com.realityvote.model.Contestant;
import com.realityvote.model.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ContestantRepository extends JpaRepository<Contestant, Long> {

    // FIXED: use programs instead of program
    @Query("SELECT c FROM Contestant c JOIN c.programs p WHERE p = :program")
    List<Contestant> findByProgram(Program program);
}
