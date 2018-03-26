/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.ILeaderBoardAssistant;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Game;
import au.com.shawware.finska.entity.Match;
import au.com.shawware.finska.entity.Player;

/**
 * Exercises and verifies the {@link CompetitionAnalyser} algorithm.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "static-method" })
public class AnalyserUnitTests
{
    /**
     * Test the {@link CompetitionAnalyser#compileResults()} algorithm.
     */
    @Test
    public void testAlgorithm()
    {
        Competition competition = createCompetition();

        // Test the Seertech Scoring System
        ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1);
        int[][] expectedResults = new int[][]
        {
            { 1, 1, 5, 4, 1, 1, 19 },
            { 2, 2, 5, 3, 0, 0, 14 },
            { 3, 3, 4, 2, 1, 0, 11 },
        };
        verifyAlgorithm(competition, scoringSystem, expectedResults);

        // Test the a basic scoring Scoring System - no bonuses of any kind.
        scoringSystem = new ScoringSystem(5, 0, 0, 0);
        expectedResults = new int[][]
        {
            { 1, 1, 5, 4, 0, 0, 20 },
            { 2, 2, 5, 3, 0, 0, 15 },
            { 3, 3, 4, 2, 0, 0, 10 },
        };
        verifyAlgorithm(competition, scoringSystem, expectedResults);

        // Test the a scoring Scoring System that counts wins and games.
        scoringSystem = new ScoringSystem(2, 1, 0, 0);
        expectedResults = new int[][]
        {
            { 1, 1, 5, 4, 0, 0, 13 },
            { 2, 2, 5, 3, 0, 0, 11 },
            { 3, 3, 4, 2, 0, 0,  8 },
        };

        // Test the a scoring Scoring System that counts wins and fast wins.
        scoringSystem = new ScoringSystem(2, 0, 5, 0);
        expectedResults = new int[][]
        {
            { 1, 1, 5, 4, 1, 0, 15 },
            { 3, 2, 4, 2, 1, 0,  7 },
            { 2, 3, 5, 3, 0, 0,  6 },
        };

        // Test the a scoring Scoring System that counts wins and win alls.
        scoringSystem = new ScoringSystem(4, 0, 0, 1);
        expectedResults = new int[][]
        {
            { 1, 1, 5, 4, 1, 0, 17 },
            { 2, 2, 5, 3, 0, 0, 12 },
            { 3, 3, 4, 2, 1, 0,  8 },
        };
    }

    /**
     * Verify the {@link CompetitionAnalyser#compileResults()} algorithm
     * for the given competition and scoring system.
     * 
     * @param competition the competition
     * @param scoringSystem the scoring system
     * @param expectedResults the expected results
     */
    private void verifyAlgorithm(Competition competition, ScoringSystem scoringSystem, int[][] expectedResults)
    {
        ILeaderBoardAssistant assistant = new CompetitionAnalyser(competition, scoringSystem);
        List<EntrantResult> leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);

        Assert.assertNotNull(leaderBoard);
        Assert.assertEquals(expectedResults.length, leaderBoard.size());

        for (int i = 0; i < expectedResults.length; i++)
        {
            EntrantResult result = leaderBoard.get(i);
            Assert.assertEquals(expectedResults[i][0], result.getRank());
            Assert.assertEquals(expectedResults[i][1], result.getEntrantID());
            Assert.assertEquals(expectedResults[i][2], result.getResultItemValue(ResultItem.GAMES.toString()));
            Assert.assertEquals(expectedResults[i][3], result.getResultItemValue(ResultItem.WINS.toString()));
            if (scoringSystem.scoreFastWins())
            {
                Assert.assertEquals(expectedResults[i][4], result.getResultItemValue(ResultItem.FAST_WINS.toString()));
            }
            if (scoringSystem.scoreWinAll())
            {
                Assert.assertEquals(expectedResults[i][5], result.getResultItemValue(ResultItem.WIN_ALL.toString()));
            }
            Assert.assertEquals(expectedResults[i][6], result.getResultItemValue(ResultItem.POINTS.toString()));
        }
    }

    /**
     * Create a competition with players, matches and games.
     * 
     * @return The generated competition.
     */
    private Competition createCompetition()
    {
        Player p1 = new Player(1, "David");
        Player p2 = new Player(2, "Mike");
        Player p3 = new Player(3, "Tom");

        // A match of three players, two games with two different winners, one a fast win.
        Game g1 = new Game(1, 1);
        Game g2 = new Game(2, 2);
        g1.addWinner(p1);
        g1.setHasFastWinner(true);
        g2.addWinner(p2);

        Match m1 = new Match(1, 1, LocalDate.of(2018, 3, 10));
        m1.addPlayer(p1);
        m1.addPlayer(p2);
        m1.addPlayer(p3);
        m1.addGame(g1);
        m1.addGame(g2);

        // A match of three players, two games with two different winners, no fast wins.
        Game g3 = new Game(3, 3);
        Game g4 = new Game(4, 4);
        g3.addWinner(p2);
        g4.addWinner(p3);

        Match m2 = new Match(2, 2, LocalDate.of(2018, 3, 11));
        m2.addPlayer(p1);
        m2.addPlayer(p2);
        m2.addPlayer(p3);
        m2.addGame(g3);
        m2.addGame(g4);

        // A match of two players, one game - ensure no "win all" scored.
        Game g5 = new Game(5, 5);
        g5.addWinner(p2);

        Match m3 = new Match(3, 3, LocalDate.of(2018, 3, 12));
        m3.addPlayer(p1);
        m3.addPlayer(p2);
        m3.addGame(g5);

        // A match of three players, two games with the same winner, no fast wins.
        Game g6 = new Game(6, 6);
        Game g7 = new Game(7, 7);
        g6.addWinner(p1);
        g7.addWinner(p1);

        Match m4 = new Match(4, 4, LocalDate.of(2018, 3, 13));
        m4.addPlayer(p1);
        m4.addPlayer(p2);
        m4.addPlayer(p3);
        m4.addGame(g6);
        m4.addGame(g7);

        // A match of three players, two games with different winners, one fast win.
        Game g8 = new Game(8, 8);
        Game g9 = new Game(9, 9);
        g8.addWinner(p1);
        g9.addWinner(p3);
        g9.setHasFastWinner(true);

        Match m5 = new Match(5, 5, LocalDate.of(2018, 3, 14));
        m5.addPlayer(p1);
        m5.addPlayer(p2);
        m5.addPlayer(p3);
        m5.addGame(g8);
        m5.addGame(g9);

        Competition competition = new Competition("C1", LocalDate.of(2018, 3, 9));

        competition.addMatch(m1);
        competition.addMatch(m2);
        competition.addMatch(m3);
        competition.addMatch(m4);
        competition.addMatch(m5);

        return competition;
    }
}
