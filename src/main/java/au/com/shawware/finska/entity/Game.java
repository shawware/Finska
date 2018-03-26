/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.compadmin.entity.AbstractEntity;
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
    /** The winning player. */
    private Set<Integer> mWinnerIds;
    /** The ID of the winning player. */
    private Map<Integer, Player> mWinners;
    /** Was the game won in 5 tosses. */
    private boolean mHasFastWinner;

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
        mNumber         = number;
        mWinnerIds      = new HashSet<>();
        mWinners        = new HashMap<>();
        mHasFastWinner  = false;
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
     * Adds a winner to this match.
     * 
     * @param player the winning player to add
     */
    @SuppressWarnings("boxing")
    public void addWinner(Player player)
    {
        // TODO; error checks
        mWinnerIds.add(player.getId());
        mWinners.put(player.getId(), player);
    }

    /**
     * Retrieve the given player from this game.
     * 
     * @param id the player's ID
     * 
     * @return The corresponding player.
     * 
     * @throws IllegalArgumentException player cannot be found
     */
    @SuppressWarnings("boxing")
    @JsonIgnore
    public Player getWinner(int id)
        throws IllegalArgumentException
    {
        if (!mWinners.containsKey(id))
        {
            throw new IllegalArgumentException("Player " + id + " is not present in this game");
        }
        return mWinners.get(id);
    }

    /**
     * @return Whether this game has a winner yet.
     */
    @JsonIgnore
    public boolean hasWinner()
    {
        return (mWinnerIds.size() > 0);
    }

    /**
     * @return This game's winning players' IDs.
     */
    public Set<Integer> getWinnerIds()
    {
        return mWinnerIds;
    }

    /**
     * Sets this game's winning players' IDs.
     * 
     * @param winnerIds the winning players' IDs
     */
    public void setWinnerIds(Set<Integer> winnerIds)
    {
        mWinners.clear();
        mWinnerIds.clear();
        mWinnerIds.addAll(winnerIds);
    }

    /**
     * @return Whether this game's winner won in 5 tosses.
     */
    public boolean getHasFastWinner()
    {
        return mHasFastWinner;
    }

    /**
     * Specifies whether this game was won in 5 tosses.
     * 
     * @param fastWin the new setting
     */
    public void setHasFastWinner(boolean fastWin)
    {
        mHasFastWinner = fastWin;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mNumber, mWinnerIds, mHasFastWinner);
    }
}
