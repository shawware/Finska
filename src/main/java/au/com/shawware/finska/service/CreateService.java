/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.time.LocalDate;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Provides service for creating competition data.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class CreateService
{
    private static final Logger LOG = LoggerFactory.getLogger(CreateService.class);

    /** The source for competition data. */
    private final IEntityRepository mRepository;
    /** The observer to be notified when this services updates the repository. */
    private final IChangeObserver mObserver;

    /**
     * Constructs a new service.
     * 
     * @param repository the competition data source
     * @param observer observes changes to the repository made by this service
     */
    /*package*/ CreateService(IEntityRepository repository, IChangeObserver observer)
    {
        mRepository = repository;
        mObserver   = observer;
    }

    /**
     * Creates a new round with the given features in the specified competition.
     * 
     * @param competitionID the competition ID
     * @param roundDate the round date
     * @param playerIds the players participating in the round
     * 
     * @return The new round.
     * 
     * @throws PersistenceException storage error
     * @throws IllegalArgumentException empty argument
     */
    @SuppressWarnings("boxing")
    public FinskaRound createRound(int competitionID, LocalDate roundDate, int[] playerIds)
        throws PersistenceException
    {
        if (roundDate == null)
        {
            throw new IllegalArgumentException("Empty round date"); //$NON-NLS-1$
        }
        if ((playerIds == null) || (playerIds.length == 0))
        {
            throw new IllegalArgumentException("Empty player IDs"); //$NON-NLS-1$
        }
        // TODO: add players to comp
        FinskaCompetition competition = mRepository.getCompetition(competitionID);
        Map<Integer, Player> players = mRepository.getPlayers();

        FinskaRound round = new FinskaRound(competition.numberOfRounds() + 1, roundDate);
        for (int playerId : playerIds)
        {
            // TODO: handle duplicates
            if (!players.containsKey(playerId))
            {
                throw new PersistenceException("Cannot find player with ID: " + playerId); //$NON-NLS-1$
            }
            round.addPlayer(players.get(playerId));
        }

        round = mRepository.createRound(competition, round);
        LOG.info("Created new round " + round.getKey() + " in competition " + competition.getKey()); //$NON-NLS-1$ //$NON-NLS-2$

        mObserver.repositoryUpdated();

        return round;
    }
}
