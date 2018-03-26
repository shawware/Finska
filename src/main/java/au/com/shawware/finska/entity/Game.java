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
    /** The winning players' IDs. */
    private Set<Integer> mWinnerIds;
    /** The winning players. */
    private Map<Integer, Player> mWinners;
    /** Any of the winning players that won in 5 tosses.  */
    private Set<Integer> mFastWinnerIds;

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
        mFastWinnerIds  = new HashSet<>();
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
     * @param fastWinner whether the player one in 5 tosses
     */
    @SuppressWarnings("boxing")
    public void addWinner(Player player, boolean fastWinner)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Null player");
        }
        if (mWinnerIds.contains(player.getId()))
        {
            throw new IllegalArgumentException("Player " + player.getId() + " has already been recorded");
        }
        addWinner(player);
        mFastWinnerIds.add(player.getId());
    }

    /**
     * Adds a winner to this match.
     * 
     * @param player the winning player to add
     */
    @SuppressWarnings("boxing")
    public void addWinner(Player player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Null player");
        }
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
     * Whether the given player ID won the game in 5 tosses.
     * 
     * @param winnerId the player ID to test
     * 
     * @return Whether they won fast.
     */
    @JsonIgnore
    public boolean isFastWinner(int winnerId)
    {
        return mFastWinnerIds.contains(winnerId);
    }

    /**
     * @return The IDs of any fast winners.
     */
    public Set<Integer> getFastWinnerIds()
    {
        return mFastWinnerIds;
    }

    /**
     * Specifies whether this game was won in 5 tosses.
     * 
     * @param fastWinnerIds the new setting
     */
    public void setFastWinnerIds(Set<Integer> fastWinnerIds)
    {
        // TODO: check IDs in players
        mFastWinnerIds.clear();
        mFastWinnerIds.addAll(fastWinnerIds);
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mNumber, mWinnerIds, mFastWinnerIds);
    }
}
