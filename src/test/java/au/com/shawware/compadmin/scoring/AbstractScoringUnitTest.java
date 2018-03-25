/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;

import au.com.shawware.finska.entity.Player;

/**
 * Common code for scoring unit tests. Primarily establish a simple
 * scoring system that can be re-used in other tests.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "static-method", "boxing" })
public class AbstractScoringUnitTest
{
    /** The full set of result items. */
    protected static List<String> sResultItems;
    /** The result items to use is comparisons. */
    protected static List<String> sComparisonItems;
    /** A sample set of players. */
    protected static Map<Integer, Player> sPlayers;

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

        sPlayers = new HashMap<>();
        for (int i = 1; i <= 10; i++)
        {
            Player p = new Player(i, "P" + String.format("%02d", i));
            sPlayers.put(p.getId(), p);
        }
    }

    /**
     * Converts a test fixture to a set of results.
     * 
     * @param fixture the test fixture to convert
     *
     * @return The corresponding results.
     */
    protected final List<EntrantResult> convertFixture(int[][] fixture)
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
