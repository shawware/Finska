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
import au.com.shawware.compadmin.entity.TestEntrant;

/**
 * Exercise and verify the leaderboard generator business logic.
 * Note: this is limited to the sorting and ranking logic.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "boxing", "static-method", "nls" })
public class LeaderBoardUnitTest extends AbstractScoringUnitTest
{
    /**
     * Test the algorithms in {@link AbstractResultsCompiler}.
     */
    @Test
    public void testLeaderBoardAlgorithm()
    {
        // Test fixture with all rounds and matches.
        final int[][] games = new int[][]
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

        TestCompetition competition;
        Number[][] results;

        // Test Round 1 only.
        competition = generateCompetition(games, 0, 3);
        results = new Number[][]
        {
            { 1, 5, 1, 1, 0, 0, 3, 2,  1,     1.5, 3 },
            { 2, 1, 1, 1, 0, 0, 2, 1,  1,     2.0, 3 },
            { 3, 3, 1, 1, 0, 0, 1, 0,  1,     0.0, 3 },
            { 4, 6, 1, 0, 0, 1, 2, 3, -1, 2.0/3.0, 0 },
            { 5, 2, 1, 0, 0, 1, 1, 2, -1,     0.5, 0 },
            { 6, 4, 1, 0, 0, 1, 0, 1, -1,     0.0, 0 },
        };
        verifyLeaderBoardAlgorithm(competition, 1, results, null, null);

        Number[][] round1Results = results; // Preserve for later.

        // Test Round 2 only.
        competition = generateCompetition(games, 3, 6);
        results = new Number[][]
        {
            { 1, 2, 1, 1, 0, 0, 3, 2,  1,     1.5, 3 },
            { 2, 4, 1, 1, 0, 0, 2, 1,  1,     2.0, 3 },
            { 3, 6, 1, 1, 0, 0, 1, 0,  1,     0.0, 3 },
            { 4, 5, 1, 0, 0, 1, 2, 3, -1, 2.0/3.0, 0 },
            { 5, 1, 1, 0, 0, 1, 1, 2, -1,     0.5, 0 },
            { 6, 3, 1, 0, 0, 1, 0, 1, -1,     0.0, 0 },
        };
        verifyLeaderBoardAlgorithm(competition, 1, results, null, null);

        // Test Round 3 only.
        competition = generateCompetition(games, 6, 9);
        results = new Number[][]
        {
            { 1, 2, 1, 1, 0, 0, 3, 1,  2,     3.0, 3 },
            { 2, 1, 1, 0, 1, 0, 1, 1,  0,     1.0, 1 },
            { 2, 4, 1, 0, 1, 0, 1, 1,  0,     1.0, 1 },
            { 2, 5, 1, 0, 1, 0, 1, 1,  0,     1.0, 1 },
            { 2, 6, 1, 0, 1, 0, 1, 1,  0,     1.0, 1 },
            { 6, 3, 1, 0, 0, 1, 1, 3, -2, 1.0/3.0, 0 },
        };
        verifyLeaderBoardAlgorithm(competition, 1, results, null, null);

        // Test Rounds 1 and 2 together.
        competition = generateCompetition(games, 0, 6);
        results = new Number[][]
        {
            { 1, 5, 2, 1, 0, 1, 5, 5,  0,     1.0, 3 },
            { 2, 2, 2, 1, 0, 1, 4, 4,  0,     1.0, 3 },
            { 3, 1, 2, 1, 0, 1, 3, 3,  0,     1.0, 3 },
            { 3, 6, 2, 1, 0, 1, 3, 3,  0,     1.0, 3 },
            { 5, 4, 2, 1, 0, 1, 2, 2,  0,     1.0, 3 },
            { 6, 3, 2, 1, 0, 1, 1, 1,  0,     1.0, 3 },
        };
        verifyLeaderBoardAlgorithm(competition, 2, results, round1Results, null);

        Number[][] round2Results = results; // Preserve for later.

        // Test Rounds 1, 2 and 3 together.
        competition = generateCompetition(games, 0, 9);
        results = new Number[][]
        {
            { 1, 2, 3, 2, 0, 1, 7, 5,  2,     1.4, 6 },
            { 2, 5, 3, 1, 1, 1, 6, 6,  0,     1.0, 4 },
            { 3, 1, 3, 1, 1, 1, 4, 4,  0,     1.0, 4 },
            { 3, 6, 3, 1, 1, 1, 4, 4,  0,     1.0, 4 },
            { 5, 4, 3, 1, 1, 1, 3, 3,  0,     1.0, 4 },
            { 6, 3, 3, 1, 0, 2, 2, 4, -2,     0.5, 3 },
        };
        verifyLeaderBoardAlgorithm(competition, 3, results, round2Results, round1Results);
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

        actualResults = compiler.compileOverallResults();
        verifyResults(actualResults, expectedResults, false);

        actualResults = compiler.compileOverallResults(rounds);
        verifyResults(actualResults, expectedResults, false);

        actualResults = LeaderBoardGenerator.generateLeaderBoard(compiler);
        verifyResults(actualResults, expectedResults, true);

       if (rounds > 1)
       {
           actualResults = compiler.compileOverallResults(rounds - 1);
           verifyResults(actualResults, expectedResults2, false);
       }
       if (rounds > 2)
       {
           actualResults = compiler.compileOverallResults(rounds - 2);
           verifyResults(actualResults, expectedResults3, false);
       }
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
     */
    private void verifyResults(List<EntrantResult> actualResults, Number[][] expectedResults, boolean ranked)
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
        }
    }

    /**
     * Test the error handling in the constructor of the abstract compiler.
     */
    @Test
    public void testErrorHandling()
    {
        Map<Integer, TestEntrant> emptyEntrants = new HashMap<>();

        verifyExceptionThrown(() -> new TestCompiler(null, null, null), IllegalArgumentException.class, "Empty competition");
        verifyExceptionThrown(() -> new TestCompiler(sCompetition, null, null), IllegalArgumentException.class, "Empty entrants");
        verifyExceptionThrown(() -> new TestCompiler(sCompetition, emptyEntrants, null), IllegalArgumentException.class, "Empty entrants");
        verifyExceptionThrown(() -> new TestCompiler(sCompetition, sEntrants, null), IllegalArgumentException.class, "Empty comparison item specification");
    }
}
