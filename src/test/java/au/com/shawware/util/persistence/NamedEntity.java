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
 * A simple named entity to test persistence with.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class NamedEntity extends AbstractEntity<String>
{
    /**
     * Constructs a new entity.
     * 
     * @param id the entity's ID
     * @param name the entity's name
     */
    public NamedEntity(@JsonProperty("id") int id,
                       @JsonProperty("key") String name)
    {
        super(id, name);
    }

    /**
     * Constructs a new, unidentified entity.
     * 
     * @param name the entity's name
     */
    public NamedEntity(String name)
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
