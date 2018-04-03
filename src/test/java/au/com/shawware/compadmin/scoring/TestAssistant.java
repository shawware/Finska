/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.List;

import au.com.shawware.util.StringUtil;

/**
 * A test assistant that accepts a set of pre-calculated results and
 * comparison items.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestAssistant extends AbstractLeaderBoardAssistant
{
    /** The result items to use. */
    private final List<EntrantResult> mResults;

    /**
     * Constructs a new assistant from the given fixtures.
     * 
     * @param results the pre-calculated results
     * @param comparisonItems the comparison items to use to compare results
     */
    public TestAssistant(List<EntrantResult> results, List<String> comparisonItems)
    {
        super(comparisonItems);
        mResults = results;
    }

    @Override
    public List<EntrantResult> compileOverallResults()
    {
        return mResults;
    }

    @Override
    public List<List<EntrantResult>> compileRoundResults()
    {
        return null;
    }

    @Override
    public String toString()
    {
        return StringUtil.toString(mResults);
    }
}
