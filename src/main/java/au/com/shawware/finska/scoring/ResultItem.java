/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumerate the Finska result items.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public enum ResultItem
{
    /** The total number of games played. */
    GAMES,
    /** The total number of wins. */
    WINS,
    /** The total name of "fast" wins, ie. 5 tosses. */
    FAST_WINS,
    /** The total number of times all games in a match were won. */
    WIN_ALL,
    /** The total number of points. */
    POINTS;

    /** The items used to order and rank results. */
    private static final List<String> sComparisonItems;
    static {
        sComparisonItems = new ArrayList<String>();
        sComparisonItems.add(ResultItem.POINTS.toString());
    }

    /**
     * @return The items used to order and rank results.
     */
    public static List<String> getComparisonItems()
    {
        return sComparisonItems;
    }
}
