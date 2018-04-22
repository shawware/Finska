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
import java.util.Set;
import java.util.stream.Collectors;

import au.com.shawware.compadmin.entity.Competition;
import au.com.shawware.compadmin.entity.Entrant;
import au.com.shawware.compadmin.entity.Match;
import au.com.shawware.compadmin.entity.Round;

/**
 * Abstracts code that other assistants may wish to re-use by sub-classing.
 * 
 * @param <CompetitionType> the competition type
 * @param <RoundType> the round type
 * @param <MatchType> the match type
 * @param <EntrantType> the entrant type
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class AbstractLeaderBoardAssistant<
        CompetitionType extends Competition<RoundType, MatchType>,
        RoundType extends Round<MatchType>,
        MatchType extends Match,
        EntrantType extends Entrant> implements ILeaderBoardAssistant
{
    /** The competition being analysed. */
    protected final CompetitionType mCompetition;
    /** The competition entrants. */
    private final Map<Integer, EntrantType> mEntrants;
    /** The result item specification to use when comparing for equality / ranking. */
    private final ResultSpec mComparisonSpec;

    /**
     * Constructs a new assistant using the given competition and result specification.
     * 
     * @param competition the competition to analyse
     * @param entrants the competition entrants
     * @param comparisonSpec the comparison result specification
     * 
     * @throws IllegalArgumentException empty competition or comparison specification
     */
    public AbstractLeaderBoardAssistant(CompetitionType competition, Map<Integer, EntrantType> entrants, ResultSpec comparisonSpec)
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
    public List<EntrantResult> compileOverallResults()
    {
        return compileOverallResults(mCompetition.getRoundIds().size());
    }

    @Override
    @SuppressWarnings("boxing")
    public List<EntrantResult> compileOverallResults(int rounds)
    {
        Set<Integer> roundIDs = mCompetition.getRoundIds();
        if ((rounds <= 0) || (rounds > roundIDs.size()))
        {
            throw new IllegalArgumentException("Invalid number of rounds: " + rounds); //$NON-NLS-1$
        }

        ResultSpec spec = createResultSpecification();

        Map<Integer, EntrantResult> results = new HashMap<>();
        for (Integer entrantID : mEntrants.keySet())
        {
            results.put(entrantID, new EntrantResult(entrantID, spec));
        }

        // Process each round until we reach the limit;
        int i = 1;
        for (Integer roundID : roundIDs)
        {
            if (i > rounds)
            {
                break;
            }
            RoundType round = mCompetition.getRound(roundID);
            processRound(results, round);
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
     * Create the result specification for the overall result compilation.
     * 
     * @return The result specification.
     */
    protected abstract ResultSpec createResultSpecification();

    /**
     * Update an entrant's result after the overall compilation is complete.
     * 
     * @param result the result to update
     */
    protected abstract void postCompile(EntrantResult result);

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
            if (mComparisonSpec.isInteger(name))
            {
                rc = Integer.compare(result2.getResultItemValueAsInt(name), result1.getResultItemValueAsInt(name));
            }
            else
            {
                rc = Double.compare(result2.getResultItemValueAsDouble(name), result1.getResultItemValueAsDouble(name));
            }
            if (rc != 0)
            {
                break;
            }
        }
        return rc;
    }
}
