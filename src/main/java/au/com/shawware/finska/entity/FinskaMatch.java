/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.compadmin.entity.Match;
import au.com.shawware.util.StringUtil;

/**
 * Models a single match of Finska.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class FinskaMatch extends Match
{
    /** The winning players' IDs. */
    private Set<Integer> mWinnerIds;
    /** The winning players. */
    private Map<Integer, Player> mWinners;
    /** Any of the winning players that won in 5 tosses.  */
    private Set<Integer> mFastWinnerIds;

    /**
     * Constructs a new match.
     *
     * @param id the match's ID
     * @param number the match's number (within a match)
     * @param matchDate the match's date
     */
    public FinskaMatch(@JsonProperty("id") int id,
                       @JsonProperty("key") int number,
                       @JsonProperty("matchDate") LocalDate matchDate)
    {
        super(id, number, matchDate);
        mWinnerIds     = new HashSet<>();
        mWinners       = new HashMap<>();
        mFastWinnerIds = new HashSet<>();
    }

    /**
     * Constructs a new match.
     *
     * @param number the match's number
     * @param matchDate the match's date
     */
    public FinskaMatch(int number, LocalDate matchDate)
    {
        this(DEFAULT_ID, number, matchDate);
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
            throw new IllegalArgumentException("Null player"); //$NON-NLS-1$
        }
        if (mWinnerIds.contains(player.getId()))
        {
            throw new IllegalArgumentException("Player " + player.getId() + " has already been recorded"); //$NON-NLS-1$ //$NON-NLS-2$
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
            throw new IllegalArgumentException("Null player"); //$NON-NLS-1$
        }
        mWinnerIds.add(player.getId());
        mWinners.put(player.getId(), player);
    }

    /**
     * Whether the given player ID is that of a winner of this match.
     * 
     * @param id the player's ID
     * 
     * @return Whether the given player is a winner.
     */
    @JsonIgnore
    @SuppressWarnings("boxing")
    public boolean isWinner(int id)
    {
        // TODO: how do we validate the ID belongs to a valid player?
        return mWinnerIds.contains(id);
    }

    /**
     * Retrieve the given player from this match.
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
            throw new IllegalArgumentException("Player " + id + " is not present in this match"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return mWinners.get(id);
    }

    /**
     * @return Whether this match has a winner yet.
     */
    @JsonIgnore
    public boolean hasWinner()
    {
        return (mWinnerIds.size() > 0);
    }

    /**
     * @return This match's winning players' IDs.
     */
    public Set<Integer> getWinnerIds()
    {
        return mWinnerIds;
    }

    /**
     * Sets this match's winning players' IDs.
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
     * Whether this match has a fast winner(s).
     *
     * @return Whether this match has a fast winner(s).
     */
    @JsonIgnore
    public boolean hasFastWinner()
    {
        return (mFastWinnerIds.size() > 0);
    }

    /**
     * Whether the given player ID won the match in 5 tosses.
     * 
     * @param winnerId the player ID to test
     * 
     * @return Whether they won fast.
     */
    @JsonIgnore
    @SuppressWarnings("boxing")
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
     * Specifies whether this match was won in 5 tosses.
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
        return StringUtil.toString(getId(), getKey(), getMatchDate(), mWinnerIds, mFastWinnerIds);
    }
}
