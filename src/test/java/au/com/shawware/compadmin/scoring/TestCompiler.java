/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.Map;

import au.com.shawware.compadmin.entity.TestCompetition;
import au.com.shawware.compadmin.entity.TestEntrant;
import au.com.shawware.compadmin.entity.TestMatch;
import au.com.shawware.compadmin.entity.TestRound;

/**
 * A test compiler that accepts a set of pre-calculated results and
 * comparison items.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestCompiler extends AbstractResultsCompiler<TestCompetition, TestRound, TestMatch, TestEntrant>
{
    /**
     * Constructs a new compiler for the given competition.
     * 
     * @param competition the test competition
     */
    public TestCompiler(TestCompetition competition)
    {
        this(competition, AbstractScoringUnitTest.sComparisonSpec);
    }

    /**
     * Constructs a new compiler for the given items.
     * Primarily used to test the constructor error checking logic.
     * 
     * @param competition the test competition
     * @param comparisonSpec the comparison specification
     */
    public TestCompiler(TestCompetition competition, ResultSpec comparisonSpec)
    {
        super(competition, comparisonSpec);
    }

    @Override
    protected ResultSpec createResultSpecification(boolean includeRunningTotal)
    {
        ResultSpec result;
        if (includeRunningTotal)
        {
            result = new ResultSpec();
            ResultSpec original = AbstractScoringUnitTest.sSpec;
            for (String name : original.getItemNames())
            {
                result.addItem(name, original.isInteger(name));
            }
            result.addItem(TestResultItems.TOTAL);
        }
        else
        {
            result = AbstractScoringUnitTest.sSpec;
        }
        return result;
    }

    @Override
    @SuppressWarnings("boxing")
    protected void processRound(Map<Integer, EntrantResult> results, TestRound round)
    {
        round.getMatches().forEach(match ->
        {
            EntrantResult result1 = results.get(match.getTeam1());
            EntrantResult result2 = results.get(match.getTeam2());

            int goalDifference = match.getScore1() - match.getScore2();

            result1.incrementResultItem(TestResultItems.MATCHES, 1);
            result2.incrementResultItem(TestResultItems.MATCHES, 1);

            result1.incrementResultItem(TestResultItems.FOR, match.getScore1());
            result1.incrementResultItem(TestResultItems.AGAINST, match.getScore2());
            result1.incrementResultItem(TestResultItems.GOAL_DIFF, goalDifference);
 
            result2.incrementResultItem(TestResultItems.FOR, match.getScore2());
            result2.incrementResultItem(TestResultItems.AGAINST, match.getScore1());
            result2.incrementResultItem(TestResultItems.GOAL_DIFF, -goalDifference);

            if (goalDifference > 0)
            {
                result1.incrementResultItem(TestResultItems.POINTS, 3);
                result1.incrementResultItem(TestResultItems.WINS, 1);
                result2.incrementResultItem(TestResultItems.LOSSES, 1);
            }
            else if (goalDifference < 0)
            {
                result1.incrementResultItem(TestResultItems.LOSSES, 1);
                result2.incrementResultItem(TestResultItems.WINS, 1);
                result2.incrementResultItem(TestResultItems.POINTS, 3);
            }
            else
            {
                result1.incrementResultItem(TestResultItems.POINTS, 1);
                result1.incrementResultItem(TestResultItems.DRAWS, 1);
                result2.incrementResultItem(TestResultItems.DRAWS, 1);
                result2.incrementResultItem(TestResultItems.POINTS, 1);
            }
        });
    }

    @Override
    protected void postCompile(EntrantResult result)
    {
        double against = result.getResultItemValueAsInt(TestResultItems.AGAINST);
        if (against > 0.0)
        {
            result.setResultItem(TestResultItems.GOAL_PERC,
                    result.getResultItemValueAsInt(TestResultItems.FOR) / against);
        }
    }

    @Override
    protected String getPointsItemName()
    {
        return TestResultItems.POINTS;
    }

    @Override
    protected String getRunningTotalItemName()
    {
        return TestResultItems.TOTAL;
    }
}
