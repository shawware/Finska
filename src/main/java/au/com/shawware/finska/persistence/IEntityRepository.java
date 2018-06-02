/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.util.Map;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Defines the persistence API for Finska entity CRUD.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public interface IEntityRepository
{
    /**
     * Retrieves all players.
     * 
     * @return The players.
     * 
     * @throws PersistenceException error loading players
     */
    Map<Integer, Player> getPlayers()
        throws PersistenceException;

    /**
     * Retrieve the player data for the specified player.
     * 
     * @param id the player's ID
     *
     * @return The player.
     *
     * @throws PersistenceException error loading player
     */
    public Player getPlayer(int id)
        throws PersistenceException;

    /**
     * Retrieves all competitions.
     * 
     * @return The competitions.
     * 
     * @throws PersistenceException error loading competitions
     */
    Map<Integer, FinskaCompetition> getCompetitions()
        throws PersistenceException;

    /**
     * Retrieves the specified competition.
     * 
     * @param id the competition ID
     * 
     * @return The competition.
     * 
     * @throws PersistenceException error loading competition
     */
    FinskaCompetition getCompetition(int id)
        throws PersistenceException;

    /**
     * Creates a new competition.
     * 
     * @param competition the competition
     * 
     * @return The created competition.
     * 
     * @throws PersistenceException error during creation
     */
    FinskaCompetition createCompetition(FinskaCompetition competition)
        throws PersistenceException;

    /**
     * Updates the given competition.
     * 
     * @param competition the competition to update
     * 
     * @throws PersistenceException error during update
     */
    void updateCompetition(FinskaCompetition competition)
        throws PersistenceException;

    /**
     * Creates a new round in the given competition.
     * 
     * @param competition the competition
     * @param round the new round
     * 
     * @return The created round.
     * 
     * @throws PersistenceException error during creation
     */
    FinskaRound createRound(FinskaCompetition competition, FinskaRound round)
        throws PersistenceException;

    /**
     * Updates the given round.
     * 
     * @param round the round to update
     * 
     * @throws PersistenceException error during update
     */
    void updateRound(FinskaRound round)
        throws PersistenceException;

    /**
     * Creates a new match in the given round of the given competition.
     * 
     * @param competition the competition
     * @param round the round the match belongs to
     * @param match the new match
     * 
     * @return The created match.
     * 
     * @throws PersistenceException error during creation
     */
    FinskaMatch createMatch(FinskaCompetition competition, FinskaRound round, FinskaMatch match)
        throws PersistenceException;

    /**
     * Updates an existing match in the given round of the given competition.
     * 
     * @param competition the competition
     * @param round the round the match belongs to
     * @param match the match to update
     * 
     * @throws PersistenceException error during update
     */
    void updateMatch(FinskaCompetition competition, FinskaRound round, FinskaMatch match)
        throws PersistenceException;

    /**
     * Creates a new player.
     * 
     * @param player the new player
     * 
     * @return The created player.
     * 
     * @throws PersistenceException error during creation
     */
    Player createPlayer(Player player)
        throws PersistenceException;

    /**
     * Updates an existing player.
     * 
     * @param player the player to update
     * 
     * @throws PersistenceException error during update
     */
    void updatePlayer(Player player)
        throws PersistenceException;
}
