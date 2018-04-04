/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.List;

import au.com.shawware.util.StringUtil;

/**
 * Abstracts code that other assistants may wish to re-use by sub-classing.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class AbstractLeaderBoardAssistant implements ILeaderBoardAssistant
{
    /** The result items to use when comparing for equality / ranking. */
    private final List<String> mComparisonItems;

    /**
     * Constructs a new assistant using the given result items.
     * The items will be used in the order they are given.
     * 
     * @param comparisonItems the comparison result items
     * 
     * @throws IllegalArgumentException empty result items or item
     */
    public AbstractLeaderBoardAssistant(List<String> comparisonItems)
        throws IllegalArgumentException
    {
        if ((comparisonItems == null) || (comparisonItems.size() == 0) ||
            comparisonItems.stream().filter(StringUtil::isEmpty).findAny().isPresent())
        {
            throw new IllegalArgumentException("Empty comparison items"); //$NON-NLS-1$
        }
        mComparisonItems = comparisonItems;
    }

    @Override
    public int compare(EntrantResult result1, EntrantResult result2)
        throws IllegalArgumentException
    {
        int rc = 0;
        for (String name : mComparisonItems)
        {
            rc = result2.getResultItemValue(name) - result1.getResultItemValue(name);
            if (rc != 0)
            {
                break;
            }
        }
        return rc;
    }
}
