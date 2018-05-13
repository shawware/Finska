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

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Provides services for maintaining matches.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class MatchService extends AbstractService
{
    private static final Logger LOG = LoggerFactory.getLogger(MatchService.class);

    /**
     * Constructs a new service.
     * 
     * @param repository the competition data source
     * @param observer observes changes to the repository made by this service
     */
    /*package*/ MatchService(IEntityRepository repository, IChangeObserver observer)
    {
        super(repository, observer);
    }

    /**
     * Creates a new match with the given features in the specified competition / round.
     * 
     * @param competitionID the competition ID
     * @param roundNumber the round number (within the competition)
     * @param winnerIds the IDs of the winning players
     * @param fastWin whether the winning players had a fast win
     * 
     * @return The new match.
     * 
     * @throws PersistenceException storage error
     * @throws IllegalArgumentException empty argument
     */
    @SuppressWarnings({ "nls", "boxing" })
    public FinskaMatch createMatch(int competitionID, int roundNumber, int[] winnerIds, boolean fastWin)
        throws PersistenceException
    {
        if ((winnerIds == null) || (winnerIds.length == 0))
        {
            throw new IllegalArgumentException("Empty winner IDs");
        }
        // TODO: add players to comp
        FinskaCompetition competition = mRepository.getCompetition(competitionID);
        FinskaRound round = competition.getRound(roundNumber);
        Map<Integer, Player> players = mRepository.getPlayers();

        FinskaMatch match = new FinskaMatch(round.numberOfMatches() + 1, round.getRoundDate());
        for (int winnerId : winnerIds)
        {
            // TODO: handle duplicates
            if (!players.containsKey(winnerId))
            {
                throw new PersistenceException("Cannot find player with ID: " + winnerId);
            }
            if (!round.hasPlayer(winnerId))
            {
                throw new PersistenceException("Cannot find player eith ID " + winnerId +  " in round " + roundNumber);
            }
            match.addWinner(players.get(winnerId));
        }
        match.setFastWinner(fastWin);

        match = mRepository.createMatch(competition, round, match);
        LOG.info("Created new match " + match.getKey() + " in round " + roundNumber + " in competition " + competition.getKey());

        mObserver.repositoryUpdated();

        return match;
    }
}
