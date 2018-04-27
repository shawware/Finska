/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.util.Map;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Defines the persistence API for loading entities.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public interface IEntityLoader
{
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
     * Retrieves the competition players.
     * 
     * @return The players as map from ID to player.
     * 
     * @throws PersistenceException error loading players
     */
    Map<Integer, Player> getPlayers()
        throws PersistenceException;
}
