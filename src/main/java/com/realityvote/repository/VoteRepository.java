package com.realityvote.repository;

import com.realityvote.model.Vote;
import com.realityvote.model.Program;
import com.realityvote.model.Contestant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    long countByContestant(Contestant c);
    long countByProgram(Program p);

    @Query("select v.contestant.id, count(v) from Vote v where v.program.id = :programId group by v.contestant.id")
    List<Object[]> tallyByProgram(Long programId);
}
