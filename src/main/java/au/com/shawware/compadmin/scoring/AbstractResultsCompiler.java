/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import au.com.shawware.compadmin.entity.Competition;
import au.com.shawware.compadmin.entity.Entrant;
import au.com.shawware.compadmin.entity.Match;
import au.com.shawware.compadmin.entity.Round;
import au.com.shawware.util.MutableInteger;
import au.com.shawware.util.StringUtil;

/**
 * Abstracts code that other results compilers may wish to re-use by sub-classing.
 * 
 * @param <CompetitionType> the competition type
 * @param <RoundType> the round type
 * @param <MatchType> the match type
 * @param <EntrantType> the entrant type
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class AbstractResultsCompiler<
        CompetitionType extends Competition<EntrantType, RoundType, MatchType>,
        RoundType extends Round<MatchType>,
        MatchType extends Match,
        EntrantType extends Entrant> implements IResultsCompiler
{
    /** The competition being analysed. */
    private final CompetitionType mCompetition;
    /** The competition entrants. */
    private final Map<Integer, EntrantType> mEntrants;
    /** The result item specification to use when comparing for equality / ranking. */
    private final ResultSpec mComparisonSpec;

    /**
     * Constructs a new compiler using the given competition and result specification.
     * 
     * @param competition the competition to analyse
     * @param entrants the competition entrants
     * @param comparisonSpec the comparison result specification
     * 
     * @throws IllegalArgumentException empty competition or comparison specification
     */
    public AbstractResultsCompiler(CompetitionType competition, Map<Integer, EntrantType> entrants, ResultSpec comparisonSpec)
        throws IllegalArgumentException
    {
        if (competition == null)
        {
            throw new IllegalArgumentException("Empty competition"); //$NON-NLS-1$
        }
        if ((entrants == null) || (entrants.size() == 0))
        {
            throw new IllegalArgumentException("Empty entrants"); //$NON-NLS-1$
        }
        if ((comparisonSpec == null) || (comparisonSpec.getItemNames().size() == 0))
        {
            throw new IllegalArgumentException("Empty comparison item specification"); //$NON-NLS-1$
        }
        mCompetition    = competition;
        mEntrants       = entrants;
        mComparisonSpec = comparisonSpec;
    }

    @Override
    public final List<EntrantResult> compileCurrentResults()
    {
        return compileResultsFor(mCompetition.numberOfRounds());
    }

    @Override
    public final List<EntrantResult> compilePreviousResults()
    {
        return compileResultsFor(mCompetition.numberOfRounds() - 1);
    }

    /**
     * Compile the results for the given number of rounds. If there
     * are no such rounds, return an empty result.
     * 
     * @param rounds the number of rounds
     *
     * @return The corresponding results.
     */
    private List<EntrantResult> compileResultsFor(int rounds)
    {
        List<EntrantResult> results;
        if (rounds <= 0)
        {
            results = new ArrayList<>();
        }
        else
        {
            results = compileResults(rounds);
        }
        return results;
    }

    @Override
    @SuppressWarnings("boxing")
    public final List<EntrantResult> compileResults(int rounds)
    {
        Set<Integer> roundIDs = mCompetition.getRoundIds();
        if ((rounds <= 0) || (rounds > roundIDs.size()))
        {
            throw new IllegalArgumentException("Invalid number of rounds: " + rounds); //$NON-NLS-1$
        }

        ResultSpec spec = createResultSpecification(false);

        Map<Integer, EntrantResult> results = new HashMap<>();
        for (Integer entrantID : mEntrants.keySet())
        {
            results.put(entrantID, new EntrantResult(entrantID, spec));
        }

        // Process each round until we reach the limit;
        int i = 1;
        Iterator<RoundType> it = mCompetition.getRounds().iterator();
        while (it.hasNext())
        {
            if (i > rounds)
            {
                break;
            }
            processRound(results, it.next());
            i++;
        }

        for (Integer playerID : mEntrants.keySet())
        {
            EntrantResult result = results.get(playerID);
            postCompile(result);
        }

        return results.values().stream().collect(Collectors.toList());
    }

    /**
     * Update an entrant's result after the overall compilation is complete.
     * 
     * @param result the result to update
     */
    protected void postCompile(EntrantResult result)
    {
        // Default is to do nothing. Sub-classes can over-ride.
    }

    @Override
    @SuppressWarnings("boxing")
    public final List<List<EntrantResult>> compileRoundResults()
    {
        ResultSpec spec = createResultSpecification(true);
        String pointsItemName = getPointsItemName();
        String runningTotalItemName = getRunningTotalItemName();

        List<List<EntrantResult>> results = new ArrayList<>(mCompetition.numberOfRounds());

        Map<Integer, MutableInteger> runningTotals = new HashMap<>(mEntrants.size());
        for (Integer entrantID : mEntrants.keySet())
        {
            runningTotals.put(entrantID, new MutableInteger(0));
        }

        mCompetition.getRounds().forEach(round -> {
            Map<Integer, EntrantResult> roundResults = new HashMap<>(mEntrants.size());
            for (Integer entrantID : mEntrants.keySet())
            {
                roundResults.put(entrantID, new EntrantResult(entrantID, spec));
            }

            processRound(roundResults, round);

            List<EntrantResult> roundResult = new ArrayList<>(mEntrants.size());
            for (Integer entrantID : mEntrants.keySet())
            {
                EntrantResult entrantResult = roundResults.get(entrantID);
                MutableInteger runningTotal = runningTotals.get(entrantID);
                runningTotal.incrementBy(entrantResult.getResultItemValueAsInt(pointsItemName));
                entrantResult.incrementResultItem(runningTotalItemName, runningTotal.getValue());
                postCompile(entrantResult);
                roundResult.add(entrantResult);
            }
            results.add(roundResult);
        });

        return results;
    }

    /**
     * Gets the name of the result item that represents points per round.
     * 
     * @return The name of the points item.
     */
    protected abstract String getPointsItemName();

    /**
     * Gets the name of the result item that represents the running total of points.
     * 
     * @return The name of the running total item.
     */
    protected abstract String getRunningTotalItemName();

    /**
     * Create the result specification for the overall result compilation.
     *
     * @param includeRunningTotal whether to include a running total in the result specification
     * 
     * @return The result specification.
     */
    protected abstract ResultSpec createResultSpecification(boolean includeRunningTotal);

    /**
     * Process the given round. That is, evaluate each match and update
     * the results for the entrants accordingly.
     * 
     * @param results the players' results so far
     * @param round the round to process
     */
    protected abstract void processRound(Map<Integer, EntrantResult> results, RoundType round);

    @Override
    public final int compare(EntrantResult result1, EntrantResult result2)
        throws IllegalArgumentException
    {
        int rc = 0;
        for (String name : mComparisonSpec.getItemNames())
        {
            rc = result1.compare(result2, name);
            if (rc != 0)
            {
                break;
            }
        }
        return rc;
    }

    @Override
    public String toString()
    {
        return StringUtil.toString(mCompetition, mEntrants, mComparisonSpec);
    }
}
