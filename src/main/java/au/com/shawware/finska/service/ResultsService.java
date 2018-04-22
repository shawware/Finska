/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.ILeaderBoardAssistant;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.IEntityLoader;
import au.com.shawware.finska.persistence.PersistenceException;
import au.com.shawware.finska.scoring.CompetitionAnalyser;
import au.com.shawware.finska.scoring.ScoringSystem;

/**
 * Provides results-based services.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("boxing")
public class ResultsService
{
    private static final Logger LOG = LoggerFactory.getLogger(ResultsService.class);

    /** The source for competition data. */
    private final IEntityLoader mLoader;
    /** The Finska scoring system to use. */
    private final ScoringSystem mScoringSystem;

    // Items based on others, created during initialisation.
    /** The competition we are processing. */
    private FinskaCompetition mCompetition;
    /** The players in the competitions. */
    private Map<Integer, Player> mPlayers;
    /** The leader board assistant for the competition, players and scoring system. */
    private ILeaderBoardAssistant mAssistant;

    /**
     * Constructs a new service.
     * 
     * @param loader the competition data source
     * @param scoringSystem the scoring system to use
     */
    public ResultsService(IEntityLoader loader, ScoringSystem scoringSystem)
    {
        mLoader        = loader;
        mScoringSystem = scoringSystem;
    }

    /**
     * Initialises the results service.
     * 
     * @throws PersistenceException persistence error
     */
    public void initialise()
        throws PersistenceException
    {
        mPlayers = mLoader.getPlayers();
        mCompetition = mLoader.getCompetition(1); // TODO: inject ID?
        mAssistant = new CompetitionAnalyser(mPlayers, mCompetition, mScoringSystem);
    }

    /**
     * Retrieve the competition data and calculate the latest leader board.
     * The result is never <code>null</code> but can be empty if there is
     * an error or no data is found.
     * 
     * @return The calculated leader board.
     */
    public List<EntrantResult> getLeaderBoard()
    {
        return LeaderBoardGenerator.generateLeaderBoard(mAssistant);
    }

    /**
     * Retrieve the results and the running for each round.
     * 
     * @return The results after each round in time sequence.
     */
    public List<List<EntrantResult>> getRoundResults()
    {
        return mAssistant.compileRoundResults();
    }

    /**
     * Retrieves the current competition.
     * 
     * @return The current competition or null if there is none.
     */
    public FinskaCompetition getCompetition()
    {
        return mCompetition;
    }

    /**
     * Retrieves the rounds for the current competition.
     * 
     * @return The list of rounds in time order.
     */
    public List<FinskaRound> getRounds()
    {
        List<FinskaRound> rounds = new ArrayList<>();
        for (Integer id : mCompetition.getRoundIds())
        {
            rounds.add(mCompetition.getRound(id));
        }
        return rounds;
    }

    /**
     * Retrieve the player data. The result is never <code>null</code>
     * but can be empty if there is an error or no players are found.
     * 
     * @return The player data map - never null.
     */
    public Map<Integer, Player> getPlayers()
    {
        return mPlayers;
    }

    /**
     * Retrieve the player data for the specified player.
     * 
     * @param id the player's ID
     *
     * @return The player.
     */
    public Player getPlayer(int id)
    {
        if (!mPlayers.containsKey(id))
        {
            String msg = "Player does not exist: " + id; //$NON-NLS-1$
            LOG.error(msg); 
            throw new IllegalArgumentException(msg);
        }
        return mPlayers.get(id);
    }
}
