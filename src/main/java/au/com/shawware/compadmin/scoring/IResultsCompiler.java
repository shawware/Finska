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
 * Specifies the results compilation API. This can be used directly
 * or to generate a leader board.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public interface IResultsCompiler extends Comparator<EntrantResult>
{
    /**
     * Compile the current (overall) results for all entrants.
     * This represents all rounds of the current competition.
     * 
     * @return The set of results for all entrants.
     */
    List<EntrantResult> compileCurrentResults();

    /**
     * Compile the previous results for all entrants.
     * This represents all but one rounds of the current competition.
     * If this is no rounds, then an empty result is returned.
     * 
     * @return The set of results for all entrants.
     */
    List<EntrantResult> compilePreviousResults();

    /**
     * Compile the results for all entrants for the first N rounds.
     *
     * @param rounds the number of rounds
     *
     * @return The set of results for all entrants.
     *
     * @throws IllegalArgumentException invalid number of rounds 
     */
    List<EntrantResult> compileResults(int rounds);

    /**
     * Compile the results for all entrants for each round.
     * 
     * @return The set of results for all entrants by round.
     */
    List<List<EntrantResult>> compileRoundResults();
}
