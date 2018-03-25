/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.compadmin.entity.AbstractEntity;
import au.com.shawware.util.StringUtil;

/**
 * Models a competition entrant.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Entrant extends AbstractEntity
{
    /** The entrant's name. */
    private final String mName;

    /**
     * Constructs a new, identified entrant.
     * 
     * @param id the entrant's ID
     * @param name the entrant's name
     */
    public Entrant(@JsonProperty("id") int id,
                   @JsonProperty("name") String name)
    {
        super(id);
        mName = name;
    }

    /**
     * Constructs a new, unidentified entrant.
     * 
     * @param name the entrant's name
     */
    public Entrant(String name)
    {
        this(DEFAULT_ID, name);
    }

    /**
     * @return The entrant's name.
     */
    public String getName()
    {
        return mName;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mName);
    }
}
