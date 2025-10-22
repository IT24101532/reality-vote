package com.realityvote.repository;

import com.realityvote.model.Vote;
import com.realityvote.model.Program;
import com.realityvote.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByViewerAndProgram(UserAccount viewer, Program program);

    long countByContestantId(Long contestantId);

    @Query("""
        SELECT v.viewer.email, v.viewer.username, v.contestant.name, v.program.name 
        FROM Vote v
        ORDER BY v.votedAt DESC
    """)
    List<Object[]> getVotingReport();
}
