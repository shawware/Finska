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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.compadmin.entity.Round;
import au.com.shawware.util.StringUtil;

/**
 * Models a single Finska round (of one or more games).
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class FinskaRound extends Round<FinskaMatch>
{
    /** The IDs of the players who participated. */
    private final Set<Integer> mPlayersIds;
    /** The players who participated. */
    private final Map<Integer, Player> mPlayers;

    /**
     * Constructs a new Finska round.
     * 
     * @param id the round's ID
     * @param number the round's number
     * @param roundDate the date the round was held
     */
    public FinskaRound(@JsonProperty("id") int id,
                       @JsonProperty("key") int number,
                       @JsonProperty("roundDate") LocalDate roundDate)
    {
        super(id, number, roundDate);
        mPlayersIds = new HashSet<>();
        mPlayers    = new HashMap<>();
     }

    /**
     * Constructs a new round.
     * 
     * @param number the round's number
     * @param roundDate the date the round was held
     */
    public FinskaRound(int number, LocalDate roundDate)
    {
        this(DEFAULT_ID, number, roundDate);
    }

    /**
     * @return The IDs of the players who participated.
     */
    public Set<Integer> getPlayersIds()
    {
        return mPlayersIds;
    }

    /**
     * Sets the IDs of the players who participated.
     * 
     * @param playerIds the player IDs
     */
    public void setPlayerIds(Set<Integer> playerIds)
    {
        mPlayers.clear();
        mPlayersIds.clear();
        mPlayersIds.addAll(playerIds);
    }

    /**
     * Adds a player to this round.
     * 
     * @param player the player to add
     */
    @SuppressWarnings("boxing")
    public void addPlayer(Player player)
    {
        if (player == null)
        {
            throw new IllegalArgumentException("Null player"); //$NON-NLS-1$
        }
        mPlayersIds.add(player.getId());
        mPlayers.put(player.getId(), player);
    }

    /**
     * Retrieve the given player from this competition.
     * 
     * @param id the player's ID
     * 
     * @return The corresponding player.
     * 
     * @throws IllegalArgumentException player cannot be found
     */
    @JsonIgnore
    @SuppressWarnings("boxing")
    public Player getPlayer(int id)
        throws IllegalArgumentException
    {
        if (!mPlayers.containsKey(id))
        {
            throw new IllegalArgumentException("Player " + id + " is not present in this round"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return mPlayers.get(id);
    }

    /**
     * @return The players involved in this round.
     */
    @JsonIgnore
    public List<Player> getPlayers()
    {
        return mPlayers.values().stream().collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), getKey(), getRoundDate(), getMatchIds(), mPlayersIds);
    }
}
