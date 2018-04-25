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
     * Test the algorithms in {@link AbstractLeaderBoardAssistant}.
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

        verifyLeaderBoardAlgorithm(competition, results);

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

        verifyLeaderBoardAlgorithm(competition, results);

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
