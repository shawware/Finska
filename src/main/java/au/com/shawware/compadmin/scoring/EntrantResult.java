/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.DoubleAdder;

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
    /** The result item specification. */
    private final ResultSpec mSpec;
    /** The ordered sets of result items. */
    private final Map<String, Number> mItems;
    /** The current ranking (if assigned) for this entrant. */
    private int mRank;

    /**
     * Creates a new, empty result for a competition entrant.
     * 
     * @param entrantID the entrant's ID
     * @param spec the result item specification
     * 
     * @throws IllegalArgumentException empty list of names or empty name
     */
    public EntrantResult(int entrantID, ResultSpec spec)
        throws IllegalArgumentException
    {
        if ((spec == null) || (spec.getItemNames().size() == 0))
        {
            throw new IllegalArgumentException("Empty result specification"); //$NON-NLS-1$
        }

        mEntrantID = entrantID;
        mRank      = 0;
        mSpec      = spec;
        mItems     = new TreeMap<>();
        for (String name : mSpec.getItemNames())
        {
            if (mSpec.isInteger(name))
            {
                mItems.put(name, new AtomicInteger(0));
            }
            else
            {
                mItems.put(name, new DoubleAdder());
            }
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
     * @return This entrant's result specification.
     */
    public ResultSpec getResultSpecification()
    {
        return mSpec;
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
     * Compare this result with the given result for the given item.
     * 
     * @param that the other result
     * @param name the item name
     *
     * @return An integer as per {@link Comparator#compare(Object, Object)}.
     */
    public int compare(EntrantResult that, String name)
    {
        if (that == null)
        {
            throw new IllegalArgumentException("Null result"); //$NON-NLS-1$
        }
        if (this.mSpec.isInteger(name) != that.mSpec.isInteger(name))
        {
            throw new IllegalArgumentException("Mismtached types for: " + name); //$NON-NLS-1$
        }
        int rc;
        if (mSpec.isInteger(name))
        {
            rc = Integer.compare(that.getResultItemValueAsInt(name), this.getResultItemValueAsInt(name));
        }
        else
        {
            rc = Double.compare(that.getResultItemValueAsDouble(name), this.getResultItemValueAsDouble(name));
        }
        return rc;
    }

    /**
     * Returns the integer value of the given result item.
     * 
     * @param name the result item's name
     * 
     * @return The result item's value.
     * 
     * @throws IllegalArgumentException unknown result item
     */
    public int getResultItemValueAsInt(String name)
        throws IllegalArgumentException
    {
        verifyInteger(name);
        return getResultItemValue(name).intValue();
    }

    /**
     * Returns the floating point value of the given result item.
     * 
     * @param name the result item's name
     * 
     * @return The result item's value.
     * 
     * @throws IllegalArgumentException unknown result item
     */
    public double getResultItemValueAsDouble(String name)
        throws IllegalArgumentException
    {
        verifyFloatingPoint(name);
        return getResultItemValue(name).doubleValue();
    }

    /**
     * Returns the internal value of the given result item.
     * 
     * @param name the result item's name
     * 
     * @return The result item's value.
     * 
     * @throws IllegalArgumentException unknown result item
     */
    private Number getResultItemValue(String name)
    {
        return mItems.get(name);
    }

    /**
     * Increments the given result item by the given amount.
     * The amount can be negative if desired.
     * 
     * @param name the result item name
     * @param increment the amount to increment by
     * 
     * @throws IllegalArgumentException unknown result item name or non-integer result item
     */
    public void incrementResultItem(String name, int increment)
        throws IllegalArgumentException
    {
        verifyInteger(name);
        AtomicInteger value = (AtomicInteger)mItems.get(name);
        value.addAndGet(increment);
    }

    /**
     * Increments the given result item by the given amount.
     * The amount can be negative if desired.
     * 
     * @param name the result item name
     * @param newValue the new value for the result item
     * 
     * @throws IllegalArgumentException unknown result item name or non-integer result item
     */
    public void setResultItem(String name, double newValue)
        throws IllegalArgumentException
    {
        verifyFloatingPoint(name);
        DoubleAdder value = (DoubleAdder)mItems.get(name);
        value.reset();
        value.add(newValue);
    }

    /**
     * Verifies that the given item is an integer result item in this specification.
     * 
     * @param name the item name
     * 
     * @throws IllegalArgumentException empty item name, item name not present or item not integer
     */
    private void verifyInteger(String name)
        throws IllegalArgumentException
    {
        verifyKnown(name);
        if (!mSpec.isInteger(name))
        {
            throw new IllegalArgumentException("Non integer result item: " + name); //$NON-NLS-1$
        }
    }

    /**
     * Verifies that the given item is a floating point result item in this specification.
     * 
     * @param name the item name
     * 
     * @throws IllegalArgumentException empty item name, item name not present or item not floating point
     */
    private void verifyFloatingPoint(String name)
        throws IllegalArgumentException
    {
        verifyKnown(name);
        if (!mSpec.isFloatingPoint(name))
        {
            throw new IllegalArgumentException("Non floating point result item: " + name); //$NON-NLS-1$
        }
    }

    /**
     * Verifies that the given item is already present in this specification.
     * 
     * @param name the item name
     * 
     * @throws IllegalArgumentException empty item name or item name not present
     */
    private void verifyKnown(String name)
        throws IllegalArgumentException
    {
        verifyNotEmpty(name);
        if (!mItems.containsKey(name))
        {
            throw new IllegalArgumentException("Unknown result item: " + name); //$NON-NLS-1$
        }
    }

    /**
     * Verifies that the given item name is not empty.
     * 
     * @param name the item name
     * 
     * @throws IllegalArgumentException empty item name
     */
    @SuppressWarnings("static-method")
    private void verifyNotEmpty(String name)
        throws IllegalArgumentException
    {
        if (StringUtil.isEmpty(name))
        {
            throw new IllegalArgumentException("Empty result item name"); //$NON-NLS-1$
        }
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(mRank, mEntrantID, mItems);
    }
}
