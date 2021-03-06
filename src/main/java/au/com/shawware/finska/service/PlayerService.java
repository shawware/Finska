/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.util.StringUtil;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Provides services for maintaining players.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class PlayerService extends AbstractService
{
    private static final Logger LOG = LoggerFactory.getLogger(PlayerService.class);

    /**
     * Constructs a new service.
     * 
     * @param repository the competition data source
     * @param observer observes changes to the repository made by this service
     */
    /*package*/ PlayerService(IEntityRepository repository, IChangeObserver observer)
    {
        super(repository, observer);
    }

    /**
     * Retrieves all the players (not just those in a specific competition).
     * 
     * @return The players.
     * 
     * @throws PersistenceException storage error
     */
    public Map<Integer, Player> getPlayers()
        throws PersistenceException
    {
        return mRepository.getPlayers();
    }

    /**
     * Retrieve the player data for the specified player.
     * 
     * @param id the player's ID
     *
     * @return The player.
     * 
     * @throws PersistenceException storage error
     */
    public Player getPlayer(int id)
        throws PersistenceException
    {
        return mRepository.getPlayer(id);
    }

    /**
     * Creates a new player with the given attributes
     * 
     * @param name the player's name
     * 
     * @return The new player.
     * 
     * @throws PersistenceException storage error
     * @throws IllegalArgumentException empty or invalid argument
     */
    @SuppressWarnings({ "nls" })
    public Player createPlayer(String name)
        throws PersistenceException, IllegalArgumentException
    {
        verifyParameters(name);

        Player player = new Player(name);

        player = mRepository.createPlayer(player);
        LOG.info("Created new player " + player.getId());

        mObserver.repositoryUpdated();

        return player;
    }

    /**
     * Updates the given player.
     * 
     * @param id the player's ID
     * @param name the player's name
     * 
     * @return The updated player.
     * 
     * @throws PersistenceException storage error
     * @throws IllegalArgumentException empty or invalid argument
     */
    @SuppressWarnings({ "nls" })
    public Player updatePlayer(int id, String name)
        throws PersistenceException, IllegalArgumentException
    {
        verifyParameters(name);

        Player player = mRepository.getPlayer(id);

        player.setKey(name);

        mRepository.updatePlayer(player);
        LOG.info("Updated player " + player.getId());

        mObserver.repositoryUpdated();

        return player;
    }

    /**
     * Verify the given parameters meet the minimum standard.
     * 
     * @param name the player's name
     * 
     * @throws IllegalArgumentException invalid parameter
     */
    @SuppressWarnings({ "nls", "static-method" })
    private void verifyParameters(String name)
        throws IllegalArgumentException
    {
        if (StringUtil.isEmpty(name))
        {
            throw new IllegalArgumentException("Empty player name");
        }
    }
}
