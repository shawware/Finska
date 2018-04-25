/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;

import au.com.shawware.compadmin.entity.TestCompetition;
import au.com.shawware.compadmin.entity.TestEntrant;
import au.com.shawware.compadmin.entity.TestMatch;
import au.com.shawware.compadmin.entity.TestRound;
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
    /** The result items to use is comparisons. */
    protected static ResultSpec sComparisonSpec;
    /** A sample competition. */
    protected static TestCompetition sCompetition;
    /** A sample set of entrants. */
    protected static Map<Integer, TestEntrant> sEntrants;
    /** The full set of result items. */
    protected static ResultSpec sSpec;

    /**
     * Set up test fixtures.
     */
    @BeforeClass
    public static void setUp()
    {
        sSpec = new ResultSpec();
        sSpec.addItem(TestResultItems.MATCHES);
        sSpec.addItem(TestResultItems.WINS);
        sSpec.addItem(TestResultItems.DRAWS);
        sSpec.addItem(TestResultItems.LOSSES);
        sSpec.addItem(TestResultItems.FOR);
        sSpec.addItem(TestResultItems.AGAINST);
        sSpec.addItem(TestResultItems.GOAL_DIFF);
        sSpec.addItem(TestResultItems.GOAL_PERC, false);
        sSpec.addItem(TestResultItems.POINTS);

        sComparisonSpec = new ResultSpec();
        sComparisonSpec.addItem(TestResultItems.POINTS);
        sComparisonSpec.addItem(TestResultItems.GOAL_DIFF);
        sComparisonSpec.addItem(TestResultItems.FOR);

        sCompetition = new TestCompetition(1, "Test", LocalDate.of(2018, 3, 9));

        sEntrants = new HashMap<>();
        for (int i = 1; i <= 6; i++)
        {
            TestEntrant e = new TestEntrant(i, "E" + String.format("%02d", i));
            sEntrants.put(e.getId(), e);
        }
    }

    /**
     * Build a competition from the specified slice of the given match data.
     * 
     * @param matches the match data
     * @param start the start of the slice (inclusive)
     * @param end the end of the slice (exclusive)
     *
     * @return The corresponding competition.
     */
    protected final TestCompetition generateCompetition(int[][] matches, int start, int end)
    {
        TestCompetition competition = new TestCompetition(1, "Test", LocalDate.of(2018, 3, 9));
        TestRound round = null;
        for (int i = start; i < end; i++)
        {
            if ((round == null) || (matches[i][0] != matches[i-1][0]))
            {
                round = new TestRound(matches[i][0], matches[i][0], competition.getStartDate());
                competition.addRound(round);
            }
            TestMatch match = new TestMatch(i + 1, i + 1, competition.getStartDate(), matches[i][1], matches[i][2], matches[i][3], matches[i][4]);
            round.addMatch(match);
        }
        return competition;
    }
}
