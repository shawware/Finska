/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.compadmin.entity.Entrant;

/**
 * Models a Finska player.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Player extends Entrant
{
    /**
     * Constructs a new, identified player.
     * 
     * @param id the player's ID
     * @param name the player's name
     */
    public Player(@JsonProperty("id") int id,
                  @JsonProperty("name") String name)
    {
        super(id, name);
    }

    /**
     * Constructs a new, unidentified player.
     * 
     * @param name the player's name
     */
    public Player(String name)
    {
        super(name);
    }
}
