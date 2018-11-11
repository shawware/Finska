/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import au.com.shawware.util.StringUtil;

/**
 * Records a single entrant's history, be it for rank or a specific result.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class EntrantHistory
{
    /** Identifies the competition entrant to whom these results belong. */
    private final int mEntrantID;
    /** The entrant's history. */
    private final Number[] mHistory;
    
    /**
     * Constructs a new history record.
     * 
     * @param entrantID the entrant's ID
     * @param history the entrant's history
     */
    public EntrantHistory(int entrantID, Number[] history)
    {
        mEntrantID = entrantID;
        mHistory = history;
    }

    /**
     * @return The entrant's ID.
     */
    public int getEntrantID()
    {
        return mEntrantID;
    }

    /**
     * @return The entrant's history.
     */
    public Number[] getHistory()
    {
        return mHistory;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(mEntrantID, mHistory);
    }
}
