/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.ILeaderBoardAssistant;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.test.AbstractUnitTest;

/**
 * Exercises and verifies the {@link CompetitionAnalyser} algorithm.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "static-method", "boxing" })
public class AnalyserUnitTest extends AbstractUnitTest
{
    /**
     * Test the {@link CompetitionAnalyser#compileOverallResults()} algorithm.
     */
    @Test
    public void testAlgorithm()
    {
        Map<Integer, Player> players = new HashMap<>();
        FinskaCompetition competition = createCompetition(players);

        // Test the a basic scoring Scoring System - no bonuses of any kind.
        ScoringSystem scoringSystem = new ScoringSystem(5, 0, 0, 0, 0);
        Number[][] expectedResults = new Number[][]
        {
            { 1, 1, 6, 12, 7, 0, 0, 0, 35, 35.0/6.0 },
            { 2, 2, 6, 12, 4, 0, 0, 0, 20, 20.0/6.0 },
            { 3, 3, 5, 11, 2, 0, 0, 0, 10, 2.0      },
            { 3, 5, 2,  5, 2, 0, 0, 0, 10, 5.0      },
            { 5, 4, 3,  7, 1, 0, 0, 0,  5, 5.0/3.0  },
        };
        verifyAlgorithm(competition, players, scoringSystem, expectedResults);

        // Test the a scoring Scoring System that counts wins and games.
        scoringSystem = new ScoringSystem(2, 1, 0, 0, 0);
        expectedResults = new Number[][]
        {
            { 1, 1, 6, 12, 7, 0, 0, 0, 20, 20.0/6.0 },
            { 2, 2, 6, 12, 4, 0, 0, 0, 14, 14.0/6.0 },
            { 3, 3, 5, 11, 2, 0, 0, 0,  9, 1.8      },
            { 4, 5, 2,  5, 2, 0, 0, 0,  6, 3.0      },
            { 5, 4, 3,  7, 1, 0, 0, 0,  5, 5.0/3.0  },
        };
        verifyAlgorithm(competition, players, scoringSystem, expectedResults);

        // Test the a scoring Scoring System that counts wins and fast wins.
        scoringSystem = new ScoringSystem(2, 0, 5, 0, 0);
        expectedResults = new Number[][]
        {
            { 1, 1, 6, 12, 7, 1, 0, 0, 19, 19.0/6.0 },
            { 2, 3, 5, 11, 2, 1, 0, 0,  9, 1.8      },
            { 3, 2, 6, 12, 4, 0, 0, 0,  8, 8.0/6.0  },
            { 4, 5, 2,  5, 2, 0, 0, 0,  4, 2.0      },
            { 5, 4, 3,  7, 1, 0, 0, 0,  2, 2.0/3.0  },
        };
        verifyAlgorithm(competition, players, scoringSystem, expectedResults);

        // Test the a scoring Scoring System that counts wins and win both.
        scoringSystem = new ScoringSystem(4, 0, 0, 1, 0);
        expectedResults = new Number[][]
        {
            { 1, 1, 6, 12, 7, 0, 2, 0, 30, 5.0      },
            { 2, 2, 6, 12, 4, 0, 0, 0, 16, 16.0/6.0 },
            { 3, 5, 2,  5, 2, 0, 1, 0,  9, 4.5      },
            { 4, 3, 5, 11, 2, 0, 0, 0,  8, 1.6      },
            { 5, 4, 3,  7, 1, 0, 0, 0,  4, 4.0/3.0  },
        };
        verifyAlgorithm(competition, players, scoringSystem, expectedResults);

        // Test the a scoring Scoring System that counts wins and win alls.
        scoringSystem = new ScoringSystem(4, 0, 0, 0, 1);
        expectedResults = new Number[][]
        {
            { 1, 1, 6, 12, 7, 0, 0, 1, 29, 29.0/6.0 },
            { 2, 2, 6, 12, 4, 0, 0, 0, 16, 16.0/6.0 },
            { 3, 5, 2,  5, 2, 0, 0, 1,  9, 4.5      },
            { 4, 3, 5, 11, 2, 0, 0, 0,  8, 1.6      },
            { 5, 4, 3,  7, 1, 0, 0, 0,  4, 4.0/3.0  },
        };
        verifyAlgorithm(competition, players, scoringSystem, expectedResults);

        // Test the Seertech Scoring System
        scoringSystem = new ScoringSystem(3, 1, 1, 1, 0);
        expectedResults = new Number[][]
        {
            { 1, 1, 6, 12, 7, 1, 2, 0, 30, 5.0 },
            { 2, 2, 6, 12, 4, 0, 0, 0, 18, 3.0 },
            { 3, 3, 5, 11, 2, 1, 0, 0, 12, 2.4 },
            { 4, 5, 2,  5, 2, 0, 1, 0,  9, 4.5 },
            { 5, 4, 3,  7, 1, 0, 0, 0,  6, 2.0 },
        };
        verifyAlgorithm(competition, players, scoringSystem, expectedResults);
    }

    /**
     * Verify the {@link CompetitionAnalyser#compileOverallResults()} algorithm
     * for the given competition and scoring system.
     * 
     * @param competition the competition
     * @param players the players in the competition
     * @param scoringSystem the scoring system
     * @param expectedResults the expected results
     */
    private void verifyAlgorithm(FinskaCompetition competition, Map<Integer, Player> players, ScoringSystem scoringSystem, Number[][] expectedResults)
    {
        ILeaderBoardAssistant assistant = new CompetitionAnalyser(players, competition, scoringSystem);
        List<EntrantResult> leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);

        Assert.assertNotNull(leaderBoard);
        Assert.assertEquals(expectedResults.length, leaderBoard.size());

        for (int i = 0; i < expectedResults.length; i++)
        {
            String id = "Index: " + i;
            EntrantResult result = leaderBoard.get(i);
            Assert.assertEquals(id, expectedResults[i][0].intValue(), result.getRank());
            Assert.assertEquals(id, expectedResults[i][1].intValue(), result.getEntrantID());
            Assert.assertEquals(id, expectedResults[i][2].intValue(), result.getResultItemValueAsInt(ResultItem.ROUNDS.toString()));
            Assert.assertEquals(id, expectedResults[i][3].intValue(), result.getResultItemValueAsInt(ResultItem.MATCHES.toString()));
            Assert.assertEquals(id, expectedResults[i][4].intValue(), result.getResultItemValueAsInt(ResultItem.WINS.toString()));
            verifyOptionalResultItem(id, result, ResultItem.FAST_WINS, scoringSystem.scoreFastWins(), expectedResults[i][5].intValue());
            verifyOptionalResultItem(id, result, ResultItem.WIN_BOTH, scoringSystem.scoreWinBoth(), expectedResults[i][6].intValue());
            verifyOptionalResultItem(id, result, ResultItem.WIN_ALL, scoringSystem.scoreWinAll(), expectedResults[i][7].intValue());
            Assert.assertEquals(id, expectedResults[i][8].intValue(), result.getResultItemValueAsInt(ResultItem.POINTS.toString()));
            Assert.assertEquals(id, expectedResults[i][9].doubleValue(), result.getResultItemValueAsDouble(ResultItem.POINTS_PER_ROUND.toString()), 0.0001);
        }
    }

    /**
     * Verify the given result either contains the given item at the right value
     * or does not contain the item at all.
     * 
     * @param id result ID for error messages
     * @param result the result under test
     * @param item the result item that may or may no be present
     * @param present whether the item should be present
     * @param expectedResult the expected value of the item
     */
    private void verifyOptionalResultItem(String id, EntrantResult result, ResultItem item, boolean present, int expectedResult)
    {
        if (present)
        {
            Assert.assertEquals(id, expectedResult, result.getResultItemValueAsInt(item.toString()));
        }
        else
        {
            verifyExceptionThrown(() -> result.getResultItemValueAsInt(item.toString()), IllegalArgumentException.class, "Unknown result item: " + item);
        }
    }

    /**
     * Create a competition with players, matches and games.
     * 
     * @param players holder for the players that are created for the competition
     * 
     * @return The generated competition.
     */
    private FinskaCompetition createCompetition(Map<Integer, Player> players)
    {
        Player p1 = new Player(1, "David");
        Player p2 = new Player(2, "Mike");
        Player p3 = new Player(3, "Tom");
        Player p4 = new Player(4, "Dick");
        Player p5 = new Player(5, "Harry");

        players.put(p1.getId(), p1);
        players.put(p2.getId(), p2);
        players.put(p3.getId(), p3);
        players.put(p4.getId(), p4);
        players.put(p5.getId(), p5);

        // A match of three players, two games with two different winners, one a fast win.
        FinskaRound r1 = new FinskaRound(1, 1, LocalDate.of(2018, 3, 10));
        FinskaMatch m1 = new FinskaMatch(1, 1, r1.getRoundDate());
        FinskaMatch m2 = new FinskaMatch(2, 2, r1.getRoundDate());
        m1.addWinner(p1, true);
        m2.addWinner(p2);

        r1.addPlayer(p1);
        r1.addPlayer(p2);
        r1.addPlayer(p3);
        r1.addMatch(m1);
        r1.addMatch(m2);

        // A match of four players in teams of two, two games with two different winners, no fast wins.
        FinskaRound r2 = new FinskaRound(2, 2, LocalDate.of(2018, 3, 11));
        FinskaMatch m3 = new FinskaMatch(3, 3, r2.getRoundDate());
        FinskaMatch m4 = new FinskaMatch(4, 4, r2.getRoundDate());
        m3.addWinner(p1);
        m3.addWinner(p2);
        m4.addWinner(p3);
        m4.addWinner(p4);

        r2.addPlayer(p1);
        r2.addPlayer(p2);
        r2.addPlayer(p3);
        r2.addPlayer(p4);
        r2.addMatch(m3);
        r2.addMatch(m4);

        // A match of two players, one game - ensure no "win both" or "win all" scored.
        FinskaRound r3 = new FinskaRound(3, 3, LocalDate.of(2018, 3, 12));
        FinskaMatch m5 = new FinskaMatch(5, 5, r3.getRoundDate());
        m5.addWinner(p2);

        r3.addPlayer(p1);
        r3.addPlayer(p2);
        r3.addMatch(m5);

        // A match of five players, two games with the same winner, no fast wins.
        FinskaRound r4 = new FinskaRound(4, 4, LocalDate.of(2018, 3, 13));
        FinskaMatch m6 = new FinskaMatch(6, 6, r4.getRoundDate());
        FinskaMatch m7 = new FinskaMatch(7, 7, r4.getRoundDate());
        m6.addWinner(p1);
        m6.addWinner(p5);
        m7.addWinner(p1);
        m7.addWinner(p5);

        r4.addPlayer(p1);
        r4.addPlayer(p2);
        r4.addPlayer(p3);
        r4.addPlayer(p4);
        r4.addPlayer(p5);
        r4.addMatch(m6);
        r4.addMatch(m7);

        // A match of three players, two games with different winners, one fast win.
        FinskaRound r5 = new FinskaRound(5, 5, LocalDate.of(2018, 3, 14));
        FinskaMatch m8 = new FinskaMatch(8, 8, r5.getRoundDate());
        FinskaMatch m9 = new FinskaMatch(9, 9, r5.getRoundDate());
        m8.addWinner(p1);
        m9.addWinner(p3, true);

        r5.addPlayer(p1);
        r5.addPlayer(p2);
        r5.addPlayer(p3);
        r5.addMatch(m8);
        r5.addMatch(m9);

        // A match of five players, first two games with same winner.
        FinskaRound r6 = new FinskaRound(6, 6, LocalDate.of(2018, 3, 15));
        FinskaMatch m10 = new FinskaMatch(10, 10, r6.getRoundDate());
        FinskaMatch m11 = new FinskaMatch(11, 11, r6.getRoundDate());
        FinskaMatch m12 = new FinskaMatch(12, 12, r6.getRoundDate());
        m10.addWinner(p1);
        m11.addWinner(p1);
        m12.addWinner(p2);

        r6.addPlayer(p1);
        r6.addPlayer(p2);
        r6.addPlayer(p3);
        r6.addPlayer(p4);
        r6.addPlayer(p5);
        r6.addMatch(m10);
        r6.addMatch(m11);
        r6.addMatch(m12);

        FinskaCompetition competition = new FinskaCompetition("C1", LocalDate.of(2018, 3, 9));

        competition.addRound(r1);
        competition.addRound(r2);
        competition.addRound(r3);
        competition.addRound(r4);
        competition.addRound(r5);
        competition.addRound(r6);

        return competition;
    }
}
