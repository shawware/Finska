/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.util.StringUtil;

/**
 * Models a Finska player.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Player extends AbstractEntity
{
    /** The player's name. */
    private final String mName;

    /**
     * Constructs a new, identified player.
     * 
     * @param id the player's ID
     * @param name the player's name
     */
    public Player(@JsonProperty("id") int id,
                  @JsonProperty("name") String name)
    {
        super(id);
        mName = name;
    }

    /**
     * Constructs a new, unidentified player.
     * 
     * @param name the player's name
     */
    public Player(String name)
    {
        this(DEFAULT_ID, name);
    }

    /**
     * @return The player's name.
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
