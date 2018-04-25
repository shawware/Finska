/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.Comparator;
import java.util.List;

/**
 * Generates a leader board based using the given assistant.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class LeaderBoardGenerator
{
    /**
     * Generates a leaderboard using the given assistant.
     * 
     * @param assistant the assistant to use
     * 
     * @return The sorted, ranked leader board.
     */
    public static List<EntrantResult> generateLeaderBoard(ILeaderBoardAssistant assistant)
    {
        List<EntrantResult> results = assistant.compileOverallResults();

        results.sort(assistant);

        rankResults(results, assistant);

        /*
         * If two (or more) teams have the same rank, we sort them by entrant ID
         * in ascending order. This is purely so the sort order is deterministic.
         * This will help with testing amongst other things. 
         */
        results.sort((EntrantResult result1, EntrantResult result2) -> {
            int rc = Integer.compare(result1.getRank(), result2.getRank());
            if (rc == 0)
            {
                rc = Integer.compare(result1.getEntrantID(), result2.getEntrantID());
            }
            return rc;
        });

        return results;
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
}
