/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.util.Collections;
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
     * @throws IllegalArgumentException empty or invalid argument
     */
    @SuppressWarnings({ "nls" })
    public FinskaMatch createMatch(int competitionID, int roundNumber, int[] winnerIds, boolean fastWin)
        throws PersistenceException
    {
        verifyParameters(winnerIds);

        // TODO: add players to comp
        FinskaCompetition competition = mRepository.getCompetition(competitionID);
        FinskaRound round = competition.getRound(roundNumber);
        Map<Integer, Player> players = mRepository.getPlayers();

        FinskaMatch match = new FinskaMatch(round.numberOfMatches() + 1, round.getRoundDate());

        updateMatch(players, round, match, winnerIds, fastWin);

        match = mRepository.createMatch(competition, round, match);
        LOG.info("Created new match " + match.getKey() + " in round " + roundNumber + " in competition " + competition.getKey());

        mObserver.repositoryUpdated();

        return match;
    }

    /**
     * Updates an existing match with the given features in the specified competition / round.
     * @param competitionID the competition ID
     * @param roundNumber the round number (within the competition)
     * @param matchNumber the match number (within the round)
     * @param winnerIds the IDs of the winning players
     * @param fastWin whether the winning players had a fast win
     * 
     * @return The new match.
     * 
     * @throws PersistenceException storage error
     * @throws IllegalArgumentException empty or invalid argument
     */
    @SuppressWarnings({ "nls" })
    public FinskaMatch updateMatch(int competitionID, int roundNumber, int matchNumber, int[] winnerIds, boolean fastWin)
        throws PersistenceException
    {
        verifyParameters(winnerIds);

        // TODO: add players to comp
        FinskaCompetition competition = mRepository.getCompetition(competitionID);
        FinskaRound round = competition.getRound(roundNumber);
        FinskaMatch match = round.getMatch(matchNumber);
        Map<Integer, Player> players = mRepository.getPlayers();

        updateMatch(players, round, match, winnerIds, fastWin);

        mRepository.updateMatch(competition, round, match);
        LOG.info("Upadted match " + match.getKey() + " in round " + roundNumber + " in competition " + competition.getKey());

        mObserver.repositoryUpdated();

        return match;
    }

    /**
     * Updates the given match with the given new settings.
     * 
     * @param players the competition's players
     * @param round the match's round
     * @param match the match to update
     * @param winnerIds the IDs of the winning players
     * @param fastWin whether the winning players had a fast win
     * 
     * @throws IllegalArgumentException validation error
     */
    @SuppressWarnings({ "nls", "boxing", "static-method" })
    private void updateMatch(Map<Integer, Player> players, FinskaRound round, FinskaMatch match, int[] winnerIds, boolean fastWin)
        throws IllegalArgumentException
    {
        match.setWinnerIds(Collections.emptySet()); // TODO: is this the best way to clear the player IDs?
        for (int winnerId : winnerIds)
        {
            // TODO: handle duplicates
            if (!players.containsKey(winnerId))
            {
                throw new IllegalArgumentException("Cannot find player with ID: " + winnerId);
            }
            if (!round.hasPlayer(winnerId))
            {
                throw new IllegalArgumentException("Cannot find player eith ID " + winnerId +  " in round " + round.getKey());
            }
            match.addWinner(players.get(winnerId));
        }
        match.setFastWinner(fastWin);
    }

    /**
     * Verify the given parameters meet the minimum standard.
     * 
     * @param winnerIds the IDs of the winning players
     * 
     * @throws IllegalArgumentException invalid parameter
     */
    @SuppressWarnings({ "nls", "static-method" })
    private void verifyParameters(int[] winnerIds)
        throws IllegalArgumentException
    {
        if ((winnerIds == null) || (winnerIds.length == 0))
        {
            throw new IllegalArgumentException("Empty winner IDs");
        }
    }
}
