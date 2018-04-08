/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

/**
 * Abstracts code that other assistants may wish to re-use by sub-classing.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class AbstractLeaderBoardAssistant implements ILeaderBoardAssistant
{
    /** The result item specification to use when comparing for equality / ranking. */
    private final ResultSpec mComparisonSpec;

    /**
     * Constructs a new assistant using the given result items.
     * The items will be used in the order they are given.
     * 
     * @param comparisonSpec the comparison result items
     * 
     * @throws IllegalArgumentException empty result items or item
     */
    public AbstractLeaderBoardAssistant(ResultSpec comparisonSpec)
        throws IllegalArgumentException
    {
        if ((comparisonSpec == null) || (comparisonSpec.getItemNames().size() == 0))
        {
            throw new IllegalArgumentException("Empty comparison item specification"); //$NON-NLS-1$
        }
        mComparisonSpec = comparisonSpec;
    }

    @Override
    public int compare(EntrantResult result1, EntrantResult result2)
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
