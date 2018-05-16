/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import au.com.shawware.compadmin.scoring.ResultSpec;

/**
 * Enumerate the Finska result items.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public enum ResultItem
{
    /** The total number of rounds played. */
    ROUNDS,
    /** The total number of matches played. */
    MATCHES,
    /** The total number of wins. */
    WINS,
    /** The total name of "fast" wins, ie. 5 tosses. */
    FAST_WINS,
    /** The total number of times the first two matches in a round were won. */
    WIN_BOTH,
    /** The total number of times all matches in a round were won. */
    WIN_ALL,
    /** The total number of points. */
    POINTS,
    /** The average number of points per round. */
    POINTS_PER_ROUND,
    /** The running total number of points. */
    RUNNING_TOTAL;

    /** The items used to order and rank results. */
    private static final ResultSpec sComparisonSpec;
    static {
        sComparisonSpec = new ResultSpec();
        sComparisonSpec.addItem(ResultItem.POINTS.toString());
    }

    /**
     * @return The items used to order and rank results.
     */
    public static ResultSpec getComparisonSpecification()
    {
        return sComparisonSpec;
    }
}
