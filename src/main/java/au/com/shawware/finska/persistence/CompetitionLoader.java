/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import au.com.shawware.compadmin.entity.AbstractEntity;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Game;
import au.com.shawware.finska.entity.Match;
import au.com.shawware.finska.entity.Player;

/**
 * Loads the competitions from store and makes them available.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class CompetitionLoader
{
    /* The singleton instance. */
    private static CompetitionLoader sLoader;

    private final IEntityStore<Competition> mCompetitionStore;
    private final IEntityStore<Match> mMatchStore;
    private final IEntityStore<Game> mGameStore;
    private final IEntityStore<Player> mPlayerStore;

    /**
     * Constructs a new loader.
     * 
     * @param root the root directory to use for storage
     */
    private CompetitionLoader(String root)
    {
        PersistenceFactory factory = PersistenceFactory.getFactory(root);

        mCompetitionStore = factory.getStore(Competition.class);
        mMatchStore       = factory.getStore(Match.class);
        mGameStore        = factory.getStore(Game.class);
        mPlayerStore      = factory.getStore(Player.class);
    }

    /**
     * Gets a singleton instance of the loader.
     * 
     * @param root the root directory to use
     * 
     * @return The loader.
     */
    public static synchronized final CompetitionLoader getLoader(String root)
    {
        if (sLoader == null)
        {
            sLoader = new CompetitionLoader(root);
        }
        return sLoader;
    }

    /**
     * Loads all the players.
     * 
     * @return The players.
     * 
     * @throws PersistenceException error loading players
     */
    public Map<Integer, Player> getPlayers()
        throws PersistenceException
    {
        return mPlayerStore.getAll();
    }

    /**
     * Loads all the competitions and any dependent entities.
     * 
     * @return The competitions, matches and games.
     * 
     * @throws PersistenceException error loading data
     */
    @SuppressWarnings("boxing")
    public Map<Integer, Competition> getCompetitions()
        throws PersistenceException
    {
        Map<Integer, Player> players = getPlayers();
        Map<Integer, Game> games = mGameStore.getAll();
        Map<Integer, Match> matches = mMatchStore.getAll();
        Map<Integer, Competition> competitions = mCompetitionStore.getAll();

        for (Game game : games.values())
        {
            int id = game.getWinnerId();
            if (id != AbstractEntity.DEFAULT_ID)
            {
                if (players.containsKey(id))
                {
                    game.setWinner(players.get(id));
                }
                else
                {
                    throw new PersistenceException("Game " + game.getId() + " refers to non-existent player; " + id);
                }
            }
        }

        loadDependentEntities(competitions, matches, Competition::getMatchIds, Competition::addMatch);
        loadDependentEntities(matches, games, Match::getGameIds, Match::addGame);
        loadDependentEntities(matches, players, Match::getPlayersIds, Match::addPlayer);

        return competitions;
    }

    /**
     * Load the dependent entities into their appropriate containers.
     * 
     * @param containers the container class that holds the dependent entities
     * @param dependents the dependent entities to be added to a container
     * @param getIdsFor the method for obtaining the IDs of the dependent entities
     * @param addTo the method for adding a dependent entity into a container
     * 
     * @throws PersistenceException missing entity
     */
    @SuppressWarnings("static-method")
    private <Container extends AbstractEntity, Dependent extends AbstractEntity>
                void loadDependentEntities(Map<Integer, Container> containers, Map<Integer, Dependent> dependents,
                                           Function<Container, Set<Integer>> getIdsFor, BiConsumer<Container, Dependent> addTo)
        throws PersistenceException
    {
        for (Container container : containers.values())
        {
            for (Integer id : getIdsFor.apply(container))
            {
                if (dependents.containsKey(id))
                {
                    addTo.accept(container, dependents.get(id));
                }
                else
                {
                    throw new PersistenceException("Entity " + container.getId() + " refers to non-existent entity; " + id);
                }
            }
        }
    }
}
