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
@SuppressWarnings("nls")
public enum ResultItem
{
    /** The total number of rounds played. */
    ROUNDS("Rounds"),
    /** The total number of matches played. */
    MATCHES("Matches"),
    /** The total number of wins. */
    WINS("Wins"),
    /** The total name of "fast" wins, ie. 5 tosses. */
    FAST_WINS("Fast Wins"),
    /** The total number of times the first two matches in a round were won. */
    WIN_BOTH("Win Both"),
    /** The total number of times all matches in a round were won. */
    WIN_ALL("Win All"),
    /** The total number of points. */
    POINTS("Points"),
    /** The average number of points per round. */
    POINTS_PER_ROUND("Pts/Round"),
    /** The running total number of points. */
    RUNNING_TOTAL("Total");

    /** The items used to order and rank results. */
    private static final ResultSpec sComparisonSpec;
    static {
        sComparisonSpec = new ResultSpec();
        sComparisonSpec.addItem(ResultItem.POINTS.toString());
    }

    /** The text to display for this item. */
    private final String mText;

    /**
     * Constructs a new item.
     * 
     * @param text the display text
     */
    private ResultItem(String text)
    {
        mText = text;
    }

    /**
     * @return The items used to order and rank results.
     */
    public static ResultSpec getComparisonSpecification()
    {
        return sComparisonSpec;
    }

    @Override
    public String toString()
    {
        return mText;
    }
}
