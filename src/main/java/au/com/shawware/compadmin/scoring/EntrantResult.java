/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.com.shawware.util.MutableInteger;
import au.com.shawware.util.StringUtil;

/**
 * Records a single entrant's results.
 * 
 * This is done in a generic fashion to support multiple competition
 * types and their respective scoring systems.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class EntrantResult
{
    /** Identifies the competition entrant to whom these results belong. */
    private final int mEntrantID;
    /** The result item names. */
    private final List<String> mItemNames;
    /** The ordered sets of result items. */
    private final Map<String, MutableInteger> mItems;
    /** The current ranking (if assigned) for this entrant. */
    private int mRank;

    /**
     * Creates a new, empty result for a competition entrant.
     * 
     * @param entrantID the entrant's ID
     * @param names the names of the result items
     * 
     * @throws IllegalArgumentException empty list of names or empty name
     */
    public EntrantResult(int entrantID, List<String> names)
        throws IllegalArgumentException
    {
        if ((names == null) || (names.size() == 0))
        {
            throw new IllegalArgumentException("Empty result item names"); //$NON-NLS-1$
        }

        mEntrantID = entrantID;
        mRank      = 0;
        mItemNames = names;
        mItems     = new TreeMap<>();
        for (String name : names)
        {
            if (StringUtil.isEmpty(name))
            {
                throw new IllegalArgumentException("Empty result item"); //$NON-NLS-1$
            }
            mItems.put(name, new MutableInteger(0));
        }
    }

    /**
     * @return This entrant's ID.
     */
    public int getEntrantID()
    {
        return mEntrantID;
    }

    /**
     * @return This entrant's current rank.
     */
    public int getRank()
    {
        return mRank;
    }

    /**
     * @return This entrant's result item names.
     */
    public List<String> getItemNames()
    {
        return mItemNames;
    }

    /**
     * Sets this entrant's rank.
     * 
     * @param rank the new rank
     */
    public void setRank(int rank)
    {
        mRank = rank;
    }

    /**
     * Returns the value of the given result item.
     * 
     * @param name the result item's name
     * 
     * @return The result item's value.
     * 
     * @throws IllegalArgumentException unknown result item
     */
    public int getResultItemValue(String name)
        throws IllegalArgumentException
    {
        if (!mItems.containsKey(name))
        {
            throw new IllegalArgumentException("Unknown result item: " + name); //$NON-NLS-1$
        }
        return mItems.get(name).getValue();
    }

    /**
     * Increments the given result item by the given amount.
     * The amount can be negative if desired.
     * 
     * @param name the result item name
     * @param increment the amount to increment by
     * 
     * @throws IllegalArgumentException unknown result item name
     */
    public void updateResultItem(String name, int increment)
        throws IllegalArgumentException
    {
        if (!mItems.containsKey(name))
        {
            throw new IllegalArgumentException("Unknown result item: " + name); //$NON-NLS-1$
        }
        mItems.get(name).incrementBy(increment);
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(mRank, mEntrantID, mItems);
    }
}
