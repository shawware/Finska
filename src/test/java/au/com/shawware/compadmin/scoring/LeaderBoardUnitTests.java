/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Exercise and verify the leaderboard business logic.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "static-method" })
public class LeaderBoardUnitTests
{
    /** The full set of result items. */
    private static List<String> sResultItems;
    /** The result items to use is comparisons. */
    private static List<String> sComparisonItems;

    /**
     * Set up test fixtures.
     */
    @BeforeClass
    public static void setUp()
    {
        sResultItems = new ArrayList<>();
        sResultItems.add("Games");
        sResultItems.add("Wins");
        sResultItems.add("For");
        sResultItems.add("GD");
        sResultItems.add("Points");

        sComparisonItems = new ArrayList<>();
        sComparisonItems.add("Points");
        sComparisonItems.add("GD");
        sComparisonItems.add("For");
    }

    /**
     * Test the basic leader board generation algorithm for a simple case.
     */
    @Test
    public void testSimpleCase()
    {
        final int[][] results = new int[][]
        {
            { 1, 3, 0, 1, -2, 1 },
            { 2, 3, 3, 3,  3, 9 },
            { 3, 3, 1, 2,  0, 4 },
            { 4, 3, 1, 1, -1, 3 },
        };
        final int[][] ranks = new int[][]
        {
            { 2, 1 },
            { 3, 2 },
            { 4, 3 },
            { 1, 4 },
        };

        verifyCase(results,  ranks);
    }

    /**
     * Test the leader board generation algorithm for a complex case.
     * That is, one where sorting and ranking involves more than just
     * the points and we have equal ranks.
     */
    @Test
    public void testComplexCase()
    {
        final int[][] results = new int[][]
        {
            { 1, 9, 5, 20,  5, 16 },
            { 2, 9, 1,  3, -2, 10 },
            { 3, 9, 8, 30, 20, 24 },
            { 4, 9, 2,  3, -1, 10 },
            { 5, 9, 4, 19,  5, 16 },
            { 6, 9, 3, 18,  5, 16 },
            { 7, 9, 7, 25, 10, 21 },
            { 8, 9, 6, 25, 10, 21 },
            { 9, 9, 0,  5, -5,  0 },
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

        verifyCase(results,  ranks);
    }

    /**
     * Verify a single case, ie. generate a leader board from the given
     * inputs and verify that they match the expected outputs.
     * 
     * @param input the input results
     * @param output the expected ranks
     */
    private void verifyCase(int[][] input, int[][] output)
    {
        Assert.assertNotNull(input);
        Assert.assertNotNull(output);
        Assert.assertEquals(input.length, output.length);

        TestAssistant assistant = new TestAssistant(convertFixture(input), sComparisonItems);
        List<EntrantResult> results = assistant.compileResults();
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
     * Converts a test fixture to a set of results.
     * 
     * @param fixture the test fixture to convert
     *
     * @return The corresponding results.
     */
    private List<EntrantResult> convertFixture(int[][] fixture)
    {
        List<EntrantResult> results = new ArrayList<>(fixture.length);
        for (int[] entrantData : fixture)
        {
            EntrantResult result = new EntrantResult(entrantData[0], sResultItems);
            result.updateResultItem("Games",  entrantData[1]);
            result.updateResultItem("Wins",   entrantData[2]);
            result.updateResultItem("For",    entrantData[3]);
            result.updateResultItem("GD",     entrantData[4]);
            result.updateResultItem("Points", entrantData[5]);
            results.add(result);
        }
        return results;
    }
}
