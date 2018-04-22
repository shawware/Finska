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
import au.com.shawware.compadmin.entity.Competition;
import au.com.shawware.compadmin.entity.Round;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;

/**
 * Loads players and competitions from a store and makes them available.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class EntityLoader implements IEntityLoader
{
    /* The singleton instances. */
    private static Map<String, EntityLoader> sLoaders = new HashMap<>();

    /** The competition store. */
    private final IEntityStore<FinskaCompetition> mCompetitionStore;
    /** The round store. */
    private final IEntityStore<FinskaRound> mRoundStore;
    /** The match store. */
    private final IEntityStore<FinskaMatch> mMatchStore;
    /** The player store. */
    private final IEntityStore<Player> mPlayerStore;

    /**
     * Constructs a new loader.
     * 
     * @param factory the persistence factory to use for obtaining stores
     */
    private EntityLoader(PersistenceFactory factory)
    {
        mCompetitionStore = factory.getStore(FinskaCompetition.class);
        mRoundStore       = factory.getStore(FinskaRound.class);
        mMatchStore       = factory.getStore(FinskaMatch.class);
        mPlayerStore      = factory.getStore(Player.class);
    }

    /**
     * Gets a singleton instance of the loader.
     * @param factory the factory to obtain persistence stores from
     * 
     * @return The loader.
     */
    public static synchronized final EntityLoader getLoader(PersistenceFactory factory)
    {
        if (!sLoaders.containsKey(factory.getRoot()))
        {
            sLoaders.put(factory.getRoot(), new EntityLoader(factory));
        }
        return sLoaders.get(factory.getRoot());
    }

    @Override
    public Map<Integer, Player> getPlayers()
        throws PersistenceException
    {
        return mPlayerStore.getAll();
    }

    @Override
    @SuppressWarnings("boxing")
    public FinskaCompetition getCompetition(int id)
        throws PersistenceException
    {
        Map<Integer, FinskaCompetition> competitions = getCompetitions();
        if (!competitions.containsKey(id))
        {
            throw new PersistenceException("Competition does not exist: " + id); //$NON-NLS-1$
        }
        return competitions.get(id);
    }

    /**
     * Loads all the competitions and any dependent entities.
     * 
     * @return The competitions, rounds and matches.
     * 
     * @throws PersistenceException error loading data
     */
    /*package*/ Map<Integer, FinskaCompetition> getCompetitions()
        throws PersistenceException
    {
        Map<Integer, Player> players = getPlayers();
        Map<Integer, FinskaMatch> matches = mMatchStore.getAll();
        Map<Integer, FinskaRound> rounds = mRoundStore.getAll();
        Map<Integer, FinskaCompetition> competitions = mCompetitionStore.getAll();

        loadDependentEntities(competitions, rounds, Competition::getRoundIds, Competition::addRound);
        loadDependentEntities(rounds, matches, Round::getMatchIds, Round::addMatch);
        loadDependentEntities(rounds, players, FinskaRound::getPlayersIds, FinskaRound::addPlayer);
        loadDependentEntities(matches, players, FinskaMatch::getWinnerIds, FinskaMatch::addWinner);

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