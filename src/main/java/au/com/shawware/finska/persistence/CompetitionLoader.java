/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.util.HashMap;
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
 * Loads players and competitions from a store and makes them available.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class CompetitionLoader
{
    /* The singleton instances. */
    private static Map<String, CompetitionLoader> sLoaders = new HashMap<>();

    /** The competition store. */
    private final IEntityStore<Competition> mCompetitionStore;
    /** The match store. */
    private final IEntityStore<Match> mMatchStore;
    /** The game store. */
    private final IEntityStore<Game> mGameStore;
    /** The player store. */
    private final IEntityStore<Player> mPlayerStore;

    /**
     * Constructs a new loader.
     * 
     * @param factory the persistence factory to use for obtaining stores
     */
    private CompetitionLoader(PersistenceFactory factory)
    {
        mCompetitionStore = factory.getStore(Competition.class);
        mMatchStore       = factory.getStore(Match.class);
        mGameStore        = factory.getStore(Game.class);
        mPlayerStore      = factory.getStore(Player.class);
    }

    /**
     * Gets a singleton instance of the loader.
     * @param factory the factory to obtain persistence stores from
     * 
     * @return The loader.
     */
    public static synchronized final CompetitionLoader getLoader(PersistenceFactory factory)
    {
        if (!sLoaders.containsKey(factory.getRoot()))
        {
            sLoaders.put(factory.getRoot(), new CompetitionLoader(factory));
        }
        return sLoaders.get(factory.getRoot());
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
    public Map<Integer, Competition> getCompetitions()
        throws PersistenceException
    {
        Map<Integer, Player> players = getPlayers();
        Map<Integer, Game> games = mGameStore.getAll();
        Map<Integer, Match> matches = mMatchStore.getAll();
        Map<Integer, Competition> competitions = mCompetitionStore.getAll();

        loadDependentEntities(competitions, matches, Competition::getMatchIds, Competition::addMatch);
        loadDependentEntities(matches, games, Match::getGameIds, Match::addGame);
        loadDependentEntities(matches, players, Match::getPlayersIds, Match::addPlayer);
        loadDependentEntities(games, players, Game::getWinnerIds, Game::addWinner);

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
                    throw new PersistenceException("Entity " + container.getId() + " refers to non-existent entity; " + id);  //$NON-NLS-1$//$NON-NLS-2$
                }
            }
        }
    }
}
