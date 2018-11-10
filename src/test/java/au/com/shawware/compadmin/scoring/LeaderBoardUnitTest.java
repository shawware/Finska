/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.compadmin.entity.TestCompetition;

/**
 * Exercise and verify the leaderboard generator business logic.
 * Note: this is limited to the sorting and ranking logic.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "boxing", "static-method", "nls" })
public class LeaderBoardUnitTest extends AbstractScoringUnitTest
{
    /*
     * Test fixture with all rounds and matches.
     * Format: round# and match args.
     */
    private static final int[][] MATCHES = new int[][]
    {
        { 1, 1, 2, 2, 1 },
        { 1, 3, 4, 1, 0 },
        { 1, 5, 6, 3, 2 },
        { 2, 1, 4, 1, 2 },
        { 2, 2, 5, 3, 2 },
        { 2, 3, 6, 0, 1 },
        { 3, 1, 6, 1, 1 },
        { 3, 2, 3, 3, 1 },
        { 3, 4, 5, 1, 1 },
        { 4, 4, 5, 1, 1 },
    };

    /**
     * Test the algorithms in {@link AbstractResultsCompiler} and
     * {@link LeaderBoardGenerator}.
     */
    @Test
    public void testLeaderBoardAlgorithm()
    {

        TestCompetition competition;

        /*
         * Result format: rank, entrant ID, results for items.
         * If (#rounds == 1), there is one additional column
         * with the running total. If (#rounds > 1), there are
         * two additional columns with the previous rank and
         * the rank delta.
         */
        // Test Round 1 only.
        competition = generateCompetition(MATCHES, 0, 3);
        Number[][] round1Results = new Number[][]
        {
            { 1, 5, 1, 1, 0, 0, 3, 2,  1,     1.5, 3, 3 },
            { 2, 1, 1, 1, 0, 0, 2, 1,  1,     2.0, 3, 3 },
            { 3, 3, 1, 1, 0, 0, 1, 0,  1,     0.0, 3, 3 },
            { 4, 6, 1, 0, 0, 1, 2, 3, -1, 2.0/3.0, 0, 0 },
            { 5, 2, 1, 0, 0, 1, 1, 2, -1,     0.5, 0, 0 },
            { 6, 4, 1, 0, 0, 1, 0, 1, -1,     0.0, 0, 0 },
        };
        verifyLeaderBoardAlgorithm(competition, 1, round1Results, null, null);

        // Test Round 2 only.
        competition = generateCompetition(MATCHES, 3, 6);
        Number[][] round2Results = new Number[][]
        {
            { 1, 2, 1, 1, 0, 0, 3, 2,  1,     1.5, 3, 3 },
            { 2, 4, 1, 1, 0, 0, 2, 1,  1,     2.0, 3, 3 },
            { 3, 6, 1, 1, 0, 0, 1, 0,  1,     0.0, 3, 3 },
            { 4, 5, 1, 0, 0, 1, 2, 3, -1, 2.0/3.0, 0, 3 },
            { 5, 1, 1, 0, 0, 1, 1, 2, -1,     0.5, 0, 3 },
            { 6, 3, 1, 0, 0, 1, 0, 1, -1,     0.0, 0, 3 },
        };
        verifyLeaderBoardAlgorithm(competition, 1, round2Results, null, null);

        // Test Round 3 only.
        competition = generateCompetition(MATCHES, 6, 9);
        Number[][] round3Results = new Number[][]
        {
            { 1, 2, 1, 1, 0, 0, 3, 1,  2,     3.0, 3, 6 },
            { 2, 1, 1, 0, 1, 0, 1, 1,  0,     1.0, 1, 4 },
            { 2, 4, 1, 0, 1, 0, 1, 1,  0,     1.0, 1, 4 },
            { 2, 5, 1, 0, 1, 0, 1, 1,  0,     1.0, 1, 4 },
            { 2, 6, 1, 0, 1, 0, 1, 1,  0,     1.0, 1, 4 },
            { 6, 3, 1, 0, 0, 1, 1, 3, -2, 1.0/3.0, 0, 3 },
        };
        verifyLeaderBoardAlgorithm(competition, 1, round3Results, null, null);

        // Test Rounds 1 and 2 together.
        competition = generateCompetition(MATCHES, 0, 6);
        Number[][] rounds1and2Results = new Number[][]
        {
            { 1, 5, 2, 1, 0, 1, 5, 5,  0,     1.0, 3, 1,  0 },
            { 2, 2, 2, 1, 0, 1, 4, 4,  0,     1.0, 3, 5,  3 },
            { 3, 1, 2, 1, 0, 1, 3, 3,  0,     1.0, 3, 2, -1 },
            { 3, 6, 2, 1, 0, 1, 3, 3,  0,     1.0, 3, 4,  1 },
            { 5, 4, 2, 1, 0, 1, 2, 2,  0,     1.0, 3, 6,  1 },
            { 6, 3, 2, 1, 0, 1, 1, 1,  0,     1.0, 3, 3, -3 },
        };
        verifyLeaderBoardAlgorithm(competition, 2, rounds1and2Results, round1Results, null);

        // Test Rounds 1, 2 and 3 together.
        competition = generateCompetition(MATCHES, 0, 9);
        Number[][] rounds1to3results = new Number[][]
        {
            { 1, 2, 3, 2, 0, 1, 7, 5,  2,     1.4, 6, 2,  1 },
            { 2, 5, 3, 1, 1, 1, 6, 6,  0,     1.0, 4, 1, -1 },
            { 3, 1, 3, 1, 1, 1, 4, 4,  0,     1.0, 4, 3,  0 },
            { 3, 6, 3, 1, 1, 1, 4, 4,  0,     1.0, 4, 3,  0 },
            { 5, 4, 3, 1, 1, 1, 3, 3,  0,     1.0, 4, 5,  0 },
            { 6, 3, 3, 1, 0, 2, 2, 4, -2,     0.5, 3, 6,  0 },
        };
        verifyLeaderBoardAlgorithm(competition, 3, rounds1to3results, rounds1and2Results, round1Results);

        verifyRoundResults(competition, round1Results, round2Results, round3Results);
    }

    @Test
    public void verifyHistoryAlgorithm()
    {
        TestCompetition competition = generateCompetition(MATCHES, 0, 9);
        IResultsCompiler compiler = new TestCompiler(competition);
        Map<Integer, int[]> actualHistory;

        // Entrant ID is index + 1
        int[][] expectedRankHistory =
        {
                { 2, 3, 3 },
                { 5, 2, 1 },
                { 3, 6, 6 },
                { 6, 5, 5 },
                { 1, 1, 2 },
                { 4, 3, 3 },
        };
        actualHistory = LeaderBoardGenerator.generateHistory(compiler, 3, true, null);
        verifyHistory(expectedRankHistory, actualHistory);

        actualHistory = LeaderBoardGenerator.generateHistory(compiler, 3, false, TestResultItems.POINTS);
        int[][] expectedScoreHistory =
        {
                { 3, 3, 4 },
                { 0, 3, 6 },
                { 3, 3, 3 },
                { 0, 3, 4 },
                { 3, 3, 4 },
                { 0, 3, 4 },
        };
        verifyHistory(expectedScoreHistory, actualHistory);
    }
    
    /**
     * Verifies the actual history matches the expected.
     * 
     * @param expectedHistory the expected history
     * @param actualHistory the actual history
     */
    private void verifyHistory(int[][] expectedHistory, Map<Integer, int[]> actualHistory)
    {
        Assert.assertNotNull(actualHistory);
        Assert.assertEquals(expectedHistory.length, actualHistory.size());
        for (int i = 0; i < expectedHistory.length; i++)
        {
            Assert.assertTrue(actualHistory.containsKey(i + 1));
            Assert.assertArrayEquals(expectedHistory[i], actualHistory.get(i + 1));
        }
    }

    /**
     * Verify the overall results generated from the given competition.
     * 
     * @param competition the competition being tested
     * @param rounds the number of rounds of expected results
     * @param expectedResults the full set of expected results
     * @param expectedResults2 the set of results for one fewer round
     * @param expectedResults3 the set of results for two fewer rounds
     */
    private void verifyLeaderBoardAlgorithm(TestCompetition competition, int rounds,
        Number[][] expectedResults, Number[][] expectedResults2, Number[][] expectedResults3)
    {
        List<EntrantResult> actualResults;

        IResultsCompiler compiler = new TestCompiler(competition);

        actualResults = compiler.compileCurrentResults();
        verifyResults(actualResults, expectedResults, false, false, false);

        actualResults = compiler.compileResults(rounds);
        verifyResults(actualResults, expectedResults, false, false, false);

        actualResults = LeaderBoardGenerator.generateLeaderBoard(compiler, rounds);
        verifyResults(actualResults, expectedResults, true, (rounds > 1), false);

        actualResults = LeaderBoardGenerator.generateLeaderBoard(compiler);
        verifyResults(actualResults, expectedResults, true, (rounds > 1), false);

        if (rounds == 1)
        {
            actualResults = compiler.compilePreviousResults();
            Assert.assertNotNull(actualResults);
            Assert.assertEquals(0, actualResults.size());
        }
        else if (rounds == 2)
        {
            actualResults = compiler.compilePreviousResults();
            verifyResults(actualResults, expectedResults2, false, false, false);
            actualResults = compiler.compileResults(rounds - 1);
            verifyResults(actualResults, expectedResults2, false, false, false);
            actualResults = LeaderBoardGenerator.generateLeaderBoard(compiler, rounds - 1);
            verifyResults(actualResults, expectedResults2, true, false, false);
        }
        else // (rounds == 3)
        {
            actualResults = compiler.compilePreviousResults();
            verifyResults(actualResults, expectedResults2, false, false, false);
            actualResults = compiler.compileResults(rounds - 2);
            verifyResults(actualResults, expectedResults3, false, false, false);
            actualResults = LeaderBoardGenerator.generateLeaderBoard(compiler, rounds - 1);
            verifyResults(actualResults, expectedResults2, true, true, false);
            actualResults = LeaderBoardGenerator.generateLeaderBoard(compiler, rounds - 2);
            verifyResults(actualResults, expectedResults3, true, false, false);
        }
    }

    /**
     * Verifies the round results for the given competition.
     * This is match results per round plus a running total.
     * 
     * @param competition the competition being tested
     * @param round1Results the expected round one results (including the running total)
     * @param round2Results the expected round two results (including the running total)
     * @param round3Results the expected round three results (including the running total)
     */
    private void verifyRoundResults(TestCompetition competition,
        Number[][] round1Results, Number[][] round2Results, Number[][] round3Results)
    {
        IResultsCompiler compiler = new TestCompiler(competition);
        List<List<EntrantResult>> actualResults = compiler.compileRoundResults();

        Assert.assertNotNull(actualResults);
        Assert.assertEquals(3, actualResults.size());

        verifyResults(actualResults.get(0), round1Results, false, false, true);
        verifyResults(actualResults.get(1), round2Results, false, false, true);
        verifyResults(actualResults.get(2), round3Results, false, false, true);
    }

    /**
     * Verify the actual and expected results are equivalent.
     * The expected results have been ranked. The actual results may or
     * may not be ranked, depending on whether they have only been compiled
     * or whether they have been ranked. If the former, they will be ordered
     * by entrant ID. If the latter, by rank (obviously).
     * 
     * @param actualResults the actual results
     * @param expectedResults the expected results
     * @param ranked whether the actual results have been ranked
     * @param hasPreviousRank whether the actual results have a previous rank
     * @param hasTotal whether the results contain a running total
     */
    private void verifyResults(List<EntrantResult> actualResults, Number[][] expectedResults,
                               boolean ranked, boolean hasPreviousRank, boolean hasTotal)
    {
        Assert.assertNotNull(actualResults);
        Assert.assertNotNull(expectedResults);
        Assert.assertEquals(expectedResults.length, actualResults.size());

        Map<Integer, Integer> map = new HashMap<>();
        if (!ranked)
        {
            // Build a map of where to find the expected result for each actual result.
            for (EntrantResult actualResult : actualResults)
            {
                boolean found = false;
                for (int i = 0; i < expectedResults.length; i++)
                {
                    if (actualResult.getEntrantID() == expectedResults[i][1].intValue())
                    {
                        map.put(actualResult.getEntrantID(), i);
                        found = true;
                    }
                }
                Assert.assertTrue(found);
            }
        }

        for (int i = 0; i < actualResults.size(); i++)
        {
            String id = "Index: " + i;
            EntrantResult actualResult = actualResults.get(i);

            int index, expectedRank;
            if (ranked)
            {
                index = i;
                expectedRank = expectedResults[i][0].intValue();
            }
            else
            {
                index = map.get(actualResult.getEntrantID());
                expectedRank = 0;
            }
            Number[] expectedResult = expectedResults[index];

            Assert.assertEquals(id, expectedRank,                     actualResult.getRank());
            Assert.assertEquals(id, expectedResult[ 1].intValue(),    actualResult.getEntrantID());
            Assert.assertEquals(id, expectedResult[ 2].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.MATCHES));
            Assert.assertEquals(id, expectedResult[ 3].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.WINS));
            Assert.assertEquals(id, expectedResult[ 4].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.DRAWS));
            Assert.assertEquals(id, expectedResult[ 5].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.LOSSES));
            Assert.assertEquals(id, expectedResult[ 6].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.FOR));
            Assert.assertEquals(id, expectedResult[ 7].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.AGAINST));
            Assert.assertEquals(id, expectedResult[ 8].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.GOAL_DIFF));
            Assert.assertEquals(id, expectedResult[ 9].doubleValue(), actualResult.getResultItemValueAsDouble(TestResultItems.GOAL_PERC), 0.0001);
            Assert.assertEquals(id, expectedResult[10].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.POINTS));
            if (hasTotal)
            {
                Assert.assertEquals(id, expectedResult[11].intValue(), actualResult.getResultItemValueAsInt(TestResultItems.TOTAL));
                Assert.assertEquals(0, actualResult.getPreviousRank());
            }
            if (hasPreviousRank)
            {
                Assert.assertEquals(id, expectedResult[11].intValue(), actualResult.getPreviousRank());
                Assert.assertEquals(id, expectedResult[12].intValue(), actualResult.getRankDelta());
            }
        }
    }

    /**
     * Test the error handling in the methods of the abstract compiler.
     */
    @Test
    public void testErrorHandling()
    {
        verifyExceptionThrown(() -> new TestCompiler(null, null), IllegalArgumentException.class, "Empty competition");
        verifyExceptionThrown(() -> new TestCompiler(sCompetition, null), IllegalArgumentException.class, "Empty comparison item specification");

        IResultsCompiler compiler = new TestCompiler(sCompetition);
        List<EntrantResult> results = compiler.compileCurrentResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());

        results = compiler.compilePreviousResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(0, results.size());

        verifyExceptionThrown(() -> compiler.compileResults(-1), IllegalArgumentException.class, "Invalid number of rounds: -1");
        verifyExceptionThrown(() -> compiler.compileResults(0),  IllegalArgumentException.class, "Invalid number of rounds: 0");
        verifyExceptionThrown(() -> compiler.compileResults(1),  IllegalArgumentException.class, "Invalid number of rounds: 1");

        verifyExceptionThrown(() -> LeaderBoardGenerator.generateLeaderBoard(compiler, -1), IllegalArgumentException.class, "Invalid number of rounds: -1");
        verifyExceptionThrown(() -> LeaderBoardGenerator.generateLeaderBoard(compiler,  0), IllegalArgumentException.class, "Invalid number of rounds: 0");
        verifyExceptionThrown(() -> LeaderBoardGenerator.generateLeaderBoard(compiler,  1), IllegalArgumentException.class, "Invalid number of rounds: 1");
    }
}
