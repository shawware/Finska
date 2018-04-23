/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;

import au.com.shawware.compadmin.entity.TestCompetition;
import au.com.shawware.compadmin.entity.TestEntrant;
import au.com.shawware.util.test.AbstractUnitTest;

/**
 * Common code for scoring unit tests. Primarily establish a simple
 * scoring system that can be re-used in other tests.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "static-method", "boxing" })
public class AbstractScoringUnitTest extends AbstractUnitTest
{
    /** The full set of result items. */
    protected static ResultSpec sSpec;
    /** The result items to use is comparisons. */
    protected static ResultSpec sComparisonSpec;
    /** A sample competition. */
    protected static TestCompetition sCompetition;
    /** A sample set of entrants. */
    protected static Map<Integer, TestEntrant> sEntrants;

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

        sCompetition = new TestCompetition(1, "Test", LocalDate.of(2018, 3, 9));

        sEntrants = new HashMap<>();
        for (int i = 1; i <= 10; i++)
        {
            TestEntrant e = new TestEntrant(i, "E" + String.format("%02d", i));
            sEntrants.put(e.getId(), e);
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
