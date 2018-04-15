/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.ILeaderBoardAssistant;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.CompetitionLoader;
import au.com.shawware.finska.persistence.PersistenceException;
import au.com.shawware.finska.scoring.CompetitionAnalyser;
import au.com.shawware.finska.scoring.ScoringSystem;

/**
 * Provides results-based services.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class ResultsService
{
    private static final Logger LOG = LoggerFactory.getLogger(ResultsService.class);

    /** The source for competition data. */
    private final CompetitionLoader mLoader;
    /** The Finska scoring system to use. */
    private final ScoringSystem mScoringSystem;

    /**
     * Constructs a new service.
     * 
     * @param loader the competition data source
     * @param scoringSystem the scoring system to use
     */
    public ResultsService(CompetitionLoader loader, ScoringSystem scoringSystem)
    {
        mLoader        = loader;
        mScoringSystem = scoringSystem;
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
        List<EntrantResult> leaderBoard = new ArrayList<>();
        try
        {
            Map<Integer, Player> players = mLoader.getPlayers();
            Map<Integer, Competition> comps = mLoader.getCompetitions();
            if (comps.size() > 0)
            {
                Competition competition = comps.get(1);
                ILeaderBoardAssistant assistant = new CompetitionAnalyser(players, competition, mScoringSystem);
                leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);
            }
        }
        catch (PersistenceException e)
        {
            LOG.error("Error generating leaderboard", e); //$NON-NLS-1$
        }
        return leaderBoard;
    }

    /**
     * Retrieve the player data. The result is never <code>null</code>
     * but can be empty if there is an error or no players are found.
     * 
     * @return The player data map - never null.
     */
    public Map<Integer, Player> getPlayers()
    {
        Map<Integer, Player> players = new HashMap<>();
        try
        {
            players = mLoader.getPlayers();
        }
        catch (PersistenceException e)
        {
            LOG.error("Error retrieving players", e); //$NON-NLS-1$
        }
        return players;
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
        Map<Integer, Player> players = getPlayers();
        if (!players.containsKey(id))
        {
            String msg = "Player does not exist: " + id; //$NON-NLS-1$
            LOG.error(msg); 
            throw new IllegalArgumentException(msg);
        }
        return players.get(id);
    }
}
