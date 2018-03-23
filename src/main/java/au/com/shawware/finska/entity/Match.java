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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import au.com.shawware.util.StringUtil;

/**
 * Models a single round match (of one or more games).
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Match extends AbstractEntity
{
    /** The match's round number. */
    private final int mRound;
    /** The date the match was held. */
    private final LocalDate mMatchDate;
    /** The IDs of the players who participated. */
    private final Set<Integer> mPlayersIds;
    /** The players who participated. */
    private final Map<Integer, Player> mPlayers;
    /** The IDs of the games that make up this match. */
    private final Set<Integer> mGameIds;
    /** The games that make up this match. */
    private final Map<Integer, Game> mGames;

    /**
     * Constructs a new match.
     * 
     * @param id the match's ID
     * @param round the match's round number
     * @param matchDate the date the match was held
     */
    public Match(@JsonProperty("id") int id,
                 @JsonProperty("round") int round,
                 @JsonProperty("matchDate") LocalDate matchDate)
     {
        super(id);
        mRound      = round;
        mMatchDate  = matchDate;
        mPlayersIds = new HashSet<>();
        mPlayers    = new HashMap<>();
        mGameIds    = new HashSet<>();
        mGames      = new HashMap<>();
     }

    /**
     * Constructs a new match.
     * 
     * @param round the match's round number
     * @param matchDate the date the match was held
     */
    public Match(int round, LocalDate matchDate)
    {
        this(DEFAULT_ID, round, matchDate);
    }

    /**
     * @return This match's round number.
     */
    public int getRound()
    {
        return mRound;
    }

    /**
     * @return The date this match was played.
     */
    @JsonDeserialize(using = LocalDateDeserializer.class) 
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getMatchDate()
    {
        return mMatchDate;
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
     * Adds a player to this match.
     * 
     * @param player the player to add
     */
    @SuppressWarnings("boxing")
    public void addPlayer(Player player)
    {
        // TODO; error checks
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
    @SuppressWarnings("boxing")
    @JsonIgnore
    public Player getPlayer(int id)
        throws IllegalArgumentException
    {
        if (!mPlayers.containsKey(id))
        {
            throw new IllegalArgumentException("Player " + id + " is not present in this match");
        }
        return mPlayers.get(id);
    }

    /**
     * @return The IDs of the games that make up this match.
     */
    public Set<Integer> getGameIds()
    {
        return mGameIds;
    }

    /**
     * Sets the IDs of the games in this match.
     * 
     * @param gameIds the game IDs
     */
    public void setGameIds(Set<Integer> gameIds)
    {
        mGames.clear();
        mGameIds.clear();
        mGameIds.addAll(gameIds);
    }

    /**
     * Adds a game to this match.
     * 
     * @param game the game to add
     */
    @SuppressWarnings("boxing")
    public void addGame(Game game)
    {
        // TODO; error checks
        mGameIds.add(game.getId());
        mGames.put(game.getId(), game);
    }

    /**
     * Retrieve the given game from this competition.
     * 
     * @param id the game's ID
     * 
     * @return The corresponding game.
     * 
     * @throws IllegalArgumentException game cannot be found
     */
    @SuppressWarnings("boxing")
    @JsonIgnore
    public Game getGame(int id)
        throws IllegalArgumentException
    {
        if (!mGames.containsKey(id))
        {
            throw new IllegalArgumentException("Game " + id + " is not present in this match");
        }
        return mGames.get(id);
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mRound, mMatchDate, mPlayersIds, mGameIds);
    }
}
