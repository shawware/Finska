/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a leader board based using the given results compiler.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class LeaderBoardGenerator
{
    /**
     * Generates a leader board using the given compiler for all rounds
     * of a competition.
     * 
     * @param compiler the results compiler to use
     * 
     * @return The sorted, ranked leader board.
     */
    public static List<EntrantResult> generateLeaderBoard(IResultsCompiler compiler)
    {
        List<EntrantResult> currentResults = compiler.compileCurrentResults();

        if (currentResults.size() > 0)
        {
            List<EntrantResult> previousResults = compiler.compilePreviousResults();
            postProcessResults(currentResults, previousResults, compiler);
        }
        return currentResults;
    }

    /**
     * Generates a leader board using the given compiler for the given
     * number of rounds of a competition.
     * 
     * @param compiler the results compiler to use
     * @param rounds the number of rounds
     * 
     * @return The sorted, ranked leader board.
     *
     * @throws IllegalArgumentException invalid number of rounds 
     */
    public static List<EntrantResult> generateLeaderBoard(IResultsCompiler compiler, int rounds)
    {
        List<EntrantResult> currentResults = compiler.compileResults(rounds);

        if (currentResults.size() > 0)
        {
            List<EntrantResult> previousResults = (rounds > 1) ?
                    compiler.compileResults(rounds - 1) : new ArrayList<>();
            postProcessResults(currentResults, previousResults, compiler);
        }
        return currentResults;
    }
    
    /**
     * Compile the history of the entrants' rank over all the rounds.
     * 
     * @param compiler the results compiler to use
     * @param rounds how many rounds to generate the history for
     * 
     * @return The sorted list of the entrants' rank history.
     */
    public static List<EntrantHistory> generateRankHistory(IResultsCompiler compiler, int rounds)
    {
        return generateHistory(compiler, rounds, true, null);
    }
    
    /**
     * Compile the history of the entrants' results over all the rounds.
     * 
     * @param compiler the results compiler to use
     * @param rounds how many rounds to generate the history for
     * @param spec the result item specification
     * @param resultItem the particular result item to retrieve
     * 
     * @return The sorted list of the entrants' result history.
     */
    public static List<EntrantHistory> generateResultHistory(IResultsCompiler compiler, int rounds, String resultItem)
    {
        return generateHistory(compiler, rounds, false, resultItem);
    }

    /**
     * Compile the history of the entrants' results over all the rounds.
     * 
     * @param compiler the results compiler to use
     * @param rounds how many rounds to generate the history for
     * @param rank whether to generate rank or score results
     * @param resultItem the particular result item to retrieve
     * 
     * @return The sorted list of the entrants' history.
     */
    @SuppressWarnings("boxing")
    private static List<EntrantHistory> generateHistory(IResultsCompiler compiler, int rounds, boolean rank, String resultItem)
    {
        List<EntrantHistory> history = new ArrayList<>();
        if (rounds <= 0)
        {
            return history;
        }
        Map<Integer, Number[]> historyData = new HashMap<>();
        List<EntrantResult> results = null;
        for (int i = 1; i <= rounds; i++)
        {
            results = generateLeaderBoard(compiler, i);
            for (EntrantResult result : results)
            {
                int entrantID = result.getEntrantID();
                Number[] row;
                if (historyData.containsKey(entrantID))
                {
                    row = historyData.get(entrantID);
                }
                else
                {
                    row = new Number[rounds];
                    historyData.put(entrantID, row);
                }
                Number item;
                if (rank)
                {
                    item = result.getRank();
                }
                else
                {
                    if (result.getResultSpecification().isInteger(resultItem))
                    {
                        item = result.getResultItemValueAsInt(resultItem);
                    }
                    else
                    {
                        item = result.getResultItemValueAsDouble(resultItem);
                    }
                }
                row[i - 1] = item;
            }
        }
        /*
         * The results are always ordered by rank. The final value for results
         * will be as at the last round and we use this ordering.
         */
        if (results != null) // This should always be the case.
        {
            results.forEach(result -> {
                history.add(new EntrantHistory(result.getEntrantID(), historyData.get(result.getEntrantID())));
            });
        }
        return history;
    }

    /**
     * Post-processes the compiled results. This includes sorting, ranking
     * and comparison with the results up to the previous round.
     * 
     * @param currentResults the current results
     * @param previousResults the previous results (can be empty)
     * @param comparator the result comparator
     */
    private static void postProcessResults(List<EntrantResult> currentResults, List<EntrantResult> previousResults, Comparator<EntrantResult> comparator)
    {
        if (currentResults.size() > 0)
        {
            currentResults.sort(comparator);

            rankResults(currentResults, comparator);

            /*
             * If two (or more) teams have the same rank, we sort them by entrant ID
             * in ascending order. This is purely so the sort order is deterministic.
             * This will help with testing amongst other things. 
             */
            currentResults.sort((EntrantResult result1, EntrantResult result2) -> {
                int rc = Integer.compare(result1.getRank(), result2.getRank());
                if (rc == 0)
                {
                    rc = Integer.compare(result1.getEntrantID(), result2.getEntrantID());
                }
                return rc;
            });

            if (previousResults.size() > 0)
            {
                previousResults.sort(comparator);
                rankResults(previousResults, comparator);
                addPreviousRank(currentResults, previousResults);
            }
        }
    }

    /**
     * Assign rankings to the given results. Assumes they've been sorted appropriately.
     * 
     * @param results the results to rank
     * @param comparator the comparator to determine whether results have the same rank
     */
    private static void rankResults(List<EntrantResult> results, Comparator<EntrantResult> comparator)
    {
        if (results.size() == 0)
        {
            return;
        }
        EntrantResult previousResult = results.get(0);
        previousResult.setRank(1);
        for (int i = 1; i < results.size(); i++)
        {
            EntrantResult result = results.get(i);
            int rank = (comparator.compare(previousResult, result) == 0) ?
                    previousResult.getRank() : // Same rank as previous
                    i + 1;                     // New ranking, noting a list is 0-indexed.
            result.setRank(rank);
            previousResult = result;
        }
    }

    /**
     * Adds the previous rank to the current results.
     * 
     * @param currentResults the current results (sorted and ranked)
     * @param previousResults the previous results (sorted and ranked)
     */
    @SuppressWarnings("boxing")
    private static void addPreviousRank(List<EntrantResult> currentResults, List<EntrantResult> previousResults)
    {
        Map<Integer, EntrantResult> previousResultsMap = new HashMap<>();
        previousResults.forEach(previousResult -> previousResultsMap.put(previousResult.getEntrantID(), previousResult));
        currentResults.forEach(currentResult -> {
            EntrantResult previousResult = previousResultsMap.get(currentResult.getEntrantID());
            currentResult.setPreviousRank(previousResult.getRank());
        });
    }
}
