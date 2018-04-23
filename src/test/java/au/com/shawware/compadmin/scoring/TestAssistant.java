/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.List;
import java.util.Map;

import au.com.shawware.compadmin.entity.TestCompetition;
import au.com.shawware.compadmin.entity.TestEntrant;
import au.com.shawware.compadmin.entity.TestMatch;
import au.com.shawware.compadmin.entity.TestRound;
import au.com.shawware.util.StringUtil;

/**
 * A test assistant that accepts a set of pre-calculated results and
 * comparison items.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestAssistant extends AbstractLeaderBoardAssistant<TestCompetition, TestRound, TestMatch, TestEntrant>
{
    /** The result items to use. */
    private final List<EntrantResult> mResults;

    /**
     * Constructs a new assistant from the given fixtures.
     * 
     * @param results the pre-calculated results
     * @param competition the test competition
     * @param entrants the test competition's entrants
     * @param comparisonSpec the comparison specification
     */
    public TestAssistant(List<EntrantResult> results, TestCompetition competition,
                        Map<Integer, TestEntrant> entrants, ResultSpec comparisonSpec)
    {
        super(competition, entrants, comparisonSpec);
        mResults = results;
    }

    @Override
    public List<EntrantResult> compileOverallResults()
    {
        return mResults;
    }

    @Override
    protected ResultSpec createResultSpecification(boolean includeRunningTotal)
    {
        return null;
    }

    @Override
    protected void processRound(Map<Integer, EntrantResult> results,
            TestRound round)
    {
        // Do nothing
    }

    @Override
    protected String getPointsItemName()
    {
        return null;
    }

    @Override
    protected String getRunningTotalItemName()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return StringUtil.toString(mResults);
    }
}
