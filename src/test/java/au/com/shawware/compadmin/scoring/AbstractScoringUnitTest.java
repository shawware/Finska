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
    protected static ResultSpec sSpec;
    /** The result items to use is comparisons. */
    protected static ResultSpec sComparisonSpec;
    /** A sample set of players. */
    protected static Map<Integer, Player> sPlayers;

    /**
     * Set up test fixtures.
     */
    @BeforeClass
    public static void setUp()
    {
        sSpec = new ResultSpec();
        sSpec.addItem("Matches");
        sSpec.addItem("Wins");
        sSpec.addItem("For");
        sSpec.addItem("M%", false);
        sSpec.addItem("Points");

        sComparisonSpec = new ResultSpec();
        sComparisonSpec.addItem("Points");
        sComparisonSpec.addItem("M%", false);
        sComparisonSpec.addItem("For");

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
    protected final List<EntrantResult> convertFixture(Number[][] fixture)
    {
        List<EntrantResult> results = new ArrayList<>(fixture.length);
        for (Number[] entrantData : fixture)
        {
            EntrantResult result = new EntrantResult(entrantData[0].intValue(), sSpec);
            result.incrementResultItem("Matches", entrantData[1].intValue());
            result.incrementResultItem("Wins",    entrantData[2].intValue());
            result.incrementResultItem("For",     entrantData[3].intValue());
            result.setResultItem      ("M%",      entrantData[4].doubleValue());
            result.incrementResultItem("Points",  entrantData[5].intValue());
            results.add(result);
        }
        return results;
    }
}
