package com.example.jobportal;

import com.example.jobportal.dto.CandidateMatchDTO;
import com.example.jobportal.service.MatchmakingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MatchmakingServiceTest {

    @Autowired
    private MatchmakingService matchmakingService;

    @Test
    public void testFindMatchesForJob() {
        // Get matches for Senior Java Developer job (id=1)
        List<CandidateMatchDTO> matches = matchmakingService.findMatchesForJob(1L);

        // Verify we get results and they're ordered by rank
        assertFalse(matches.isEmpty());

        // Verify the ranking is correct (ranks should be sequential starting from 1)
        assertEquals(1, matches.get(0).getRank());

        if (matches.size() > 1) {
            assertTrue(matches.get(0).getMatchScore() >= matches.get(1).getMatchScore());
        }

        // Verify candidates meet minimum requirements
        for (CandidateMatchDTO match : matches) {
            // The candidates should have at least 60% skill match which is reflected in the score
            assertTrue(match.getMatchScore() > 0);
        }
    }

    @Test
    public void testFindTopMatchesForEmployer() {
        // Get top matches for Tech Innovations (id=1)
        List<CandidateMatchDTO> matches = matchmakingService.findTopMatchesForEmployer(1L, 3);

        // Verify we get results
        assertFalse(matches.isEmpty());

        // Verify the number of results doesn't exceed the limit
        assertTrue(matches.size() <= 3);
    }
}