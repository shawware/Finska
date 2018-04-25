/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import au.com.shawware.util.StringUtil;

/**
 * Maintains an ordered set of items and their types to include in a result.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class ResultSpec
{
    /** The specification itself. */
    private final Map<String, Boolean> mSpec;

    /**
     * Constructs a new, empty specification.
     */
    public ResultSpec()
    {
        mSpec = new LinkedHashMap<>();
    }

    /**
     * Adds a new integer item to this specification.
     * 
     * @param name the item name
     * 
     * @throws IllegalArgumentException empty item name or item already present
     */
    public void addItem(String name)
        throws IllegalArgumentException
    {
        addItem(name, true);
    }

    /**
     * Adds a new item to this specification.
     * 
     * @param name the item name
     * @param isInteger whether the item is integral or floating point
     * 
     * @throws IllegalArgumentException empty item name or item already present
     */
    public void addItem(String name, boolean isInteger)
        throws IllegalArgumentException
    {
        verifyUnknown(name);
        mSpec.put(name, Boolean.valueOf(isInteger));
    }

    /**
     * Whether this specification has the given item.
     * 
     * @param name the item name
     *
     * @return Whether this specification has the given item.
     */
    public boolean hasItem(String name)
    {
        verifyNotEmpty(name);
        return mSpec.containsKey(name);
    }

    /**
     * @return The set of items names in the order they were added.
     */
    public Set<String> getItemNames()
    {
        return mSpec.keySet();
    }

    /**
     * Whether the given item is integral.
     * 
     * @param name the item name
     * 
     * @return Whether the given item is integral.
     * 
     * @throws IllegalArgumentException empty item name or item name is not present
     */
    public boolean isInteger(String name)
        throws IllegalArgumentException
    {
        verifyKnown(name);
        return mSpec.get(name).booleanValue();
    }

    /**
     * Whether the given item is floating point.
     * 
     * @param name the item name
     * 
     * @return Whether the given item is floating point.
     * 
     * @throws IllegalArgumentException empty item name or item name is not present
     */
    public boolean isFloatingPoint(String name)
        throws IllegalArgumentException
    {
        return !isInteger(name);
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
        if (!mSpec.containsKey(name))
        {
            throw new IllegalArgumentException("Unknown item name: " + name); //$NON-NLS-1$
        }
    }

    /**
     * Verifies that the given item is not yet present in this specification.
     * 
     * @param name the item name
     * 
     * @throws IllegalArgumentException empty item name or item name already present
     */
    private void verifyUnknown(String name)
        throws IllegalArgumentException
    {
        verifyNotEmpty(name);
        if (mSpec.containsKey(name))
        {
            throw new IllegalArgumentException("Item already present: " + name); //$NON-NLS-1$
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
            throw new IllegalArgumentException("Empty item name"); //$NON-NLS-1$
        }
    }

    @Override
    public String toString()
    {
        return StringUtil.toString(mSpec);
    }
}
