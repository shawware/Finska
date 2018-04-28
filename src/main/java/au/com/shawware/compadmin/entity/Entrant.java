/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.util.StringUtil;
import au.com.shawware.util.persistence.AbstractEntity;

/**
 * Models a competition entrant.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class Entrant extends AbstractEntity<String>
{
    /**
     * Constructs a new, identified entrant.
     * 
     * @param id the entrant's ID
     * @param name the entrant's name
     */
    public Entrant(@JsonProperty("id") int id,
                   @JsonProperty("key") String name)
    {
        super(id, name);
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

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), getKey());
    }
}
