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
     * Test the basic leader board generation algorithm for a simple case.
     */
    @Test
    public void testSimpleCase()
    {
        final Number[][] results = new Number[][]
        {
            { 1, 3, 0, 1, -2.0, 1 },
            { 2, 3, 3, 3,  3.0, 9 },
            { 3, 3, 1, 2,  0.0, 4 },
            { 4, 3, 1, 1, -1.0, 3 },
        };
        final int[][] ranks = new int[][]
        {
            { 2, 1 },
            { 3, 2 },
            { 4, 3 },
            { 1, 4 },
        };

        verifyCase(results, ranks);
    }

    /**
     * Test the leader board generation algorithm for a complex case.
     * That is, one where sorting and ranking involves more than just
     * the points and we have equal ranks.
     */
    @Test
    public void testComplexCase()
    {
        final Number[][] results = new Number[][]
        {
            { 1, 9, 5, 20,  5.2, 16 },
            { 2, 9, 1,  3, -2.0, 10 },
            { 3, 9, 8, 30, 20.0, 24 },
            { 4, 9, 2,  3, -1.0, 10 },
            { 5, 9, 4, 19,  5.2, 16 },
            { 6, 9, 3, 18,  5.2, 16 },
            { 7, 9, 7, 25, 10.5, 21 },
            { 8, 9, 6, 25, 10.5, 21 },
            { 9, 9, 0,  5, -5.0,  0 },
        };
        final int[][] ranks = new int[][]
        {
            { 3, 1 },
            { 7, 2 },
            { 8, 2 },
            { 1, 4 },
            { 5, 5 },
            { 6, 6 },
            { 4, 7 },
            { 2, 8 },
            { 9, 9 },
        };

        verifyCase(results, ranks);
    }

    /**
     * Test the algorithms in {@link AbstractLeaderBoardAssistant}.
     */
    @Test
    public void testLeaderBoardAlgorithm()
    {
        final int[][] games = new int[][]
        {
            { 1, 1, 2, 2, 1 },
            { 1, 3, 4, 1, 0 },
            { 1, 5, 6, 3, 2 },
            { 2, 1, 2, 2, 1 },
            { 2, 3, 4, 1, 0 },
            { 2, 5, 6, 3, 2 },
        };

        TestCompetition competition = generateCompetition(games, 0, 3);
        final Number[][] results = new Number[][]
        {
            { 1, 5, 1, 1, 0, 0, 3, 2,  1,     1.5, 3 },
            { 2, 1, 1, 1, 0, 0, 2, 1,  1,     2.0, 3 },
            { 3, 3, 1, 1, 0, 0, 1, 0,  1,     0.0, 3 },
            { 4, 6, 1, 0, 0, 1, 2, 3, -1, 2.0/3.0, 0 },
            { 5, 2, 1, 0, 0, 1, 1, 2, -1,     0.5, 0 },
            { 6, 4, 1, 0, 0, 1, 0, 1, -1,     0.0, 0 },
        };

        verifyLeaderBoardAlgorithm(competition, results);
    }

    /**
     * Verify the leaderboard generated from the given competition.
     * 
     * @param competition the competition being tested
     * @param expectedResults the expected results
     */
    private void verifyLeaderBoardAlgorithm(TestCompetition competition, Number[][] expectedResults)
    {
        ILeaderBoardAssistant assistant = new TestAssistant(competition);
        List<EntrantResult> actualResults = LeaderBoardGenerator.generateLeaderBoard(assistant);

        Assert.assertNotNull(actualResults);
        Assert.assertNotNull(expectedResults);
        Assert.assertEquals(expectedResults.length, actualResults.size());

        for (int i = 0; i < expectedResults.length; i++)
        {
            String id = "Index: " + i;
            EntrantResult actualResult = actualResults.get(i);
            Assert.assertEquals(id, expectedResults[i][ 0].intValue(),    actualResult.getRank());
            Assert.assertEquals(id, expectedResults[i][ 1].intValue(),    actualResult.getEntrantID());
            Assert.assertEquals(id, expectedResults[i][ 2].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.MATCHES));
            Assert.assertEquals(id, expectedResults[i][ 3].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.WINS));
            Assert.assertEquals(id, expectedResults[i][ 4].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.DRAWS));
            Assert.assertEquals(id, expectedResults[i][ 5].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.LOSSES));
            Assert.assertEquals(id, expectedResults[i][ 6].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.FOR));
            Assert.assertEquals(id, expectedResults[i][ 7].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.AGAINST));
            Assert.assertEquals(id, expectedResults[i][ 8].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.GOAL_DIFF));
            Assert.assertEquals(id, expectedResults[i][ 9].doubleValue(), actualResult.getResultItemValueAsDouble(TestResultItems.GOAL_PERC), 0.0001);
            Assert.assertEquals(id, expectedResults[i][10].intValue(),    actualResult.getResultItemValueAsInt(TestResultItems.POINTS));
        }
    }

    /**
     * Verify a single case, ie. generate a leader board from the given
     * inputs and verify that they match the expected outputs.
     * 
     * @param input the input results
     * @param output the expected ranks
     */
    private void verifyCase(Number[][] input, int[][] output)
    {
        Assert.assertNotNull(input);
        Assert.assertNotNull(output);
        Assert.assertEquals(input.length, output.length);

        ILeaderBoardAssistant assistant = new TestAssistant(sCompetition);
        List<EntrantResult> results = assistant.compileOverallResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(input.length, results.size());

        List<EntrantResult> leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);

        verifyRanking(leaderBoard, output);
    }

    /**
     * Verify the given ranked results are as expected.
     * 
     * @param rankedResults the ranked results
     * @param expectedRanks the expected ranks
     */
    private void verifyRanking(List<EntrantResult> rankedResults, int[][] expectedRanks)
    {
        Assert.assertNotNull(rankedResults);
        Assert.assertEquals(expectedRanks.length, rankedResults.size());
        for (int i = 0; i < expectedRanks.length; i++)
        {
            EntrantResult result = rankedResults.get(i);
            Assert.assertNotNull(result);
            Assert.assertEquals(expectedRanks[i][0], result.getEntrantID());
            Assert.assertEquals(expectedRanks[i][1], result.getRank());
        }
    }

    /**
     * Test the error handling in the constructor of the abstract assistant.
     */
    @Test
    public void testErrorHandling()
    {
        Map<Integer, TestEntrant> emptyEntrants = new HashMap<>();

        verifyExceptionThrown(() -> new TestAssistant(null, null, null), IllegalArgumentException.class, "Empty competition");
        verifyExceptionThrown(() -> new TestAssistant(sCompetition, null, null), IllegalArgumentException.class, "Empty entrants");
        verifyExceptionThrown(() -> new TestAssistant(sCompetition, emptyEntrants, null), IllegalArgumentException.class, "Empty entrants");
        verifyExceptionThrown(() -> new TestAssistant(sCompetition, sEntrants, null), IllegalArgumentException.class, "Empty comparison item specification");
    }
}
