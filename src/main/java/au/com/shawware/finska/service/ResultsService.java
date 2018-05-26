/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.util.List;
import java.util.Map;

import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.IResultsCompiler;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.finska.scoring.CompetitionAnalyser;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Provides results-based services.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class ResultsService implements IChangeObserver
{
    /** The source for competition data. */
    private final IEntityRepository mRepository;
    /** The Finska scoring system to use. */
    private final ScoringSystem mScoringSystem;

    // Items based on others, created during initialisation.
    /** The competition we are processing. */
    private FinskaCompetition mCompetition;
    /** The results compiler for the competition, players and scoring system. */
    private IResultsCompiler mCompiler;

    /**
     * Constructs a new service.
     * 
     * @param repository the competition data source
     * @param scoringSystem the scoring system to use
     */
    /*package*/ ResultsService(IEntityRepository repository, ScoringSystem scoringSystem)
    {
        mRepository    = repository;
        mScoringSystem = scoringSystem;
    }

    @Override
    public void repositoryUpdated()
        throws PersistenceException
    {
        Integer ID = new Integer(1); // TODO: inject ID?
        Map<Integer, FinskaCompetition> competitions = mRepository.getCompetitions();
        if (competitions.containsKey(ID)) 
        {
            mCompetition = competitions.get(ID);
            mCompiler = new CompetitionAnalyser(mCompetition, mScoringSystem);
        }
    }

    /**
     * Retrieve the competition data and calculate the latest leader board.
     * 
     * The result is never <code>null</code> but can be empty if there is
     * no data is found.
     * 
     * @return The calculated leader board.
     */
    public List<EntrantResult> getLeaderBoard()
    {
        return LeaderBoardGenerator.generateLeaderBoard(mCompiler);
    }

    /**
     * Retrieve the competition data and calculate the latest leader board
     * for the given number of rounds.
     * 
     * The result is never <code>null</code> but can be empty if there is
     * no data found.
     * 
     * @param rounds the number of rounds
     * 
     * @return The calculated leader board.
     *
     * @throws IllegalArgumentException invalid number of rounds 
     */
    public List<EntrantResult> getLeaderBoard(int rounds)
    {
        return LeaderBoardGenerator.generateLeaderBoard(mCompiler, rounds);
    }

    /**
     * Retrieve the results and the running for each round.
     * 
     * @return The results after each round in time sequence.
     */
    public List<List<EntrantResult>> getRoundResults()
    {
        return mCompiler.compileRoundResults();
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
        return mCompetition.getRounds();
    }

    /**
     * Retrieves a specific round for the current competition.
     * 
     * @param number the round number
     *
     * @return The round.
     */
    public FinskaRound getRound(int number)
    {
        return mCompetition.getRound(number);
    }

    /**
     * Retrieve the player data for the current competition.
     * The result is never <code>null</code> but can be empty if there
     * is an error or no players are found.
     * 
     * @return The player data map - never null.
     */
    public Map<Integer, Player> getPlayers()
    {
        return mCompetition.getEntrantMap();
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
        return mCompetition.getEntrant(id);
    }
}
