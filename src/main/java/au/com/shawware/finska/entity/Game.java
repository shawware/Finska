/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.util.StringUtil;

/**
 * Models a single game of Finska.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Game extends AbstractEntity
{
    /** The game number. */
    private final int mNumber;
    /** The ID of the winning player. */
    private Player mWinner;
    /** The winning player. */
    private int mWinnerId;
    // TODO: fast win?

    /**
     * 
     */
    /**
     * Constructs a new game.
     *
     * @param id the game's ID
     * @param number the game's number (within a match)
     */
    public Game(@JsonProperty("id") int id,
                @JsonProperty("number") int number)
    {
        super(id);
        mNumber = number;
        mWinnerId = DEFAULT_ID;
    }

    /**
     * Constructs a new game.
     *
     * @param number the game's number
     */
    public Game(int number)
    {
        this(DEFAULT_ID, number);
    }

    /**
     * @return The game's number.
     */
    public int getNumber()
    {
        return mNumber;
    }

    /**
     * @return The game's winning player.
     */
    @JsonIgnore
    public Player getWinner()
    {
        return mWinner;
    }

    /**
     * Sets this game's winner.
     * 
     * @param winner the winning player
     */
    @JsonIgnore
    public void setWinner(Player winner)
    {
        mWinnerId = winner.getId();
        mWinner = winner;
    }

    /**
     * @return This game's winning player's ID.
     */
    public int getWinnerId()
    {
        return mWinnerId;
    }

    /**
     * Sets this game's winning player ID.
     * 
     * @param winnerId the winning player's ID
     */
    public void setWinnerId(int winnerId)
    {
        mWinnerId = winnerId;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mNumber, mWinnerId);
    }
}
