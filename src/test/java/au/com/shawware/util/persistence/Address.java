/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.util.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.util.StringUtil;

/**
 * A very simple address for test purposes.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("boxing")
public class Address extends AbstractEntity<Integer>
{
    /** The address's street. */
    private final String mStreet;

    /**
     * Constructs a new entity.
     * 
     * @param id the entity's ID
     * @param number the address's number
     * @param street the address's street
     */
    public Address(@JsonProperty("id") int id,
                   @JsonProperty("key") int number,
                   @JsonProperty("street") String street)
    {
        super(id, number);
        if (StringUtil.isEmpty(street))
        {
            throw new IllegalArgumentException("Empty street"); //$NON-NLS-1$
        }
        mStreet = street;
    }

    /**
     * Constructs a new, unidentified entity.
     * 
     * @param number the address's number
     * @param street the address's street
     */
    public Address(int number, String street)
    {
        this(DEFAULT_ID, number, street);
    }

    /**
     * @return This address's street.
     */
    public String getStreet()
    {
        return mStreet;
    }

    @Override
    public String toString()
    {
        return StringUtil.toString(getId(), getKey(), mStreet);
    }
}
