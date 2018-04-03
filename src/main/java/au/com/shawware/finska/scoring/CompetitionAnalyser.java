/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import au.com.shawware.compadmin.scoring.AbstractLeaderBoardAssistant;
import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Game;
import au.com.shawware.finska.entity.Match;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.MutableInteger;

/**
 * Analyses competitions to produce artifacts such as leader boards.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("boxing")
public class CompetitionAnalyser extends AbstractLeaderBoardAssistant
{
    /** The entrants in the competition. */
    private final Map<Integer, Player> mPlayers;
    /** The competition being analysed. */
    private final Competition mCompetition;
    /** The scoring system in use. */
    private final ScoringSystem mScoringSystem;

    /**
     * Constructs a new analyser for the given players, competition and scoring system.
     * 
     * @param players the full set of players in the competition
     * @param competition the competition to analyse
     * @param scoringSystem the scoring system to use
     */
    public CompetitionAnalyser(Map<Integer, Player> players, Competition competition, ScoringSystem scoringSystem)
    {
        super(ResultItem.getComparisonItems());
        mPlayers       = players;
        mCompetition   = competition;
        mScoringSystem = scoringSystem;
    }

    @Override
    public List<EntrantResult> compileOverallResults()
    {
        List<String> resultItems = determineResultItems(mScoringSystem, false);

        Map<Integer, EntrantResult> results = new HashMap<>();
        for (Integer playerID : mPlayers.keySet())
        {
            results.put(playerID, new EntrantResult(playerID, resultItems));
        }

        for (Integer matchID : mCompetition.getMatchIds())
        {
            Match match = mCompetition.getMatch(matchID);
            processMatch(results, match);
        }
        return results.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<List<EntrantResult>> compileRoundResults()
    {
        List<String> resultItems = determineResultItems(mScoringSystem, true);
        Set<Integer> matchIds = mCompetition.getMatchIds();
        List<List<EntrantResult>> results = new ArrayList<>(matchIds.size());

        Map<Integer, MutableInteger> runningTotals = new HashMap<>(mPlayers.size());
        for (Integer playerID : mPlayers.keySet())
        {
            runningTotals.put(playerID, new MutableInteger(0));
        }

        for (Integer matchID : matchIds)
        {
            Map<Integer, EntrantResult> roundResults = new HashMap<>(mPlayers.size());
            for (Integer playerID : mPlayers.keySet())
            {
                roundResults.put(playerID, new EntrantResult(playerID, resultItems));
            }

            Match match = mCompetition.getMatch(matchID);
            processMatch(roundResults, match);

            List<EntrantResult> roundResult = new ArrayList<>(mPlayers.size());
            for (Integer playerID : mPlayers.keySet())
            {
                EntrantResult playerResult = roundResults.get(playerID);
                MutableInteger runningTotal = runningTotals.get(playerID);
                runningTotal.incrementBy(playerResult.getResultItemValue(ResultItem.POINTS.toString()));
                playerResult.updateResultItem(ResultItem.RUNNING_TOTAL.toString(), runningTotal.getValue());
                roundResult.add(playerResult);
            }
            results.add(roundResult);
        }

        return results;
    }

    /**
     * Process the given match. That is, evaluate each game and update
     * the results for the entrants accordingly.
     * 
     * @param results the players' results so far
     * @param match the match to process
     */
    private void processMatch(Map<Integer, EntrantResult> results, Match match)
    {
        for (Integer playerID : match.getPlayersIds())
        {
            EntrantResult result = results.get(playerID);
            result.updateResultItem(ResultItem.MATCHES.toString(), 1);
            result.updateResultItem(ResultItem.GAMES.toString(), match.getGameIds().size());
            if (mScoringSystem.scorePointsForPlaying())
            {
                result.updateResultItem(ResultItem.POINTS.toString(), mScoringSystem.pointsForPlaying());
            }
        }
        Set<Integer> gameIDs = match.getGameIds();
        boolean recordWinAll = (mScoringSystem.scoreWinAll() && (gameIDs.size() > 1));
        boolean sameWinner = true;
        Set<Integer> lastWinners = new HashSet<>();
        for (Integer gameID : gameIDs)
        {
            Game game = match.getGame(gameID);
            if (!game.hasWinner())
            {
                continue; // Skip games that have not been played yet.
            }
            Set<Integer> winnerIds = game.getWinnerIds();
            for (Integer winnerId : winnerIds)
            {
                EntrantResult result = results.get(winnerId);
                result.updateResultItem(ResultItem.WINS.toString(), 1);
                result.updateResultItem(ResultItem.POINTS.toString(), mScoringSystem.pointsForWin());
                if (mScoringSystem.scoreFastWins() && game.isFastWinner(winnerId))
                {
                    result.updateResultItem(ResultItem.FAST_WINS.toString(), 1);
                    result.updateResultItem(ResultItem.POINTS.toString(), mScoringSystem.pointsForFastWin());
                }
            }
            if (recordWinAll && sameWinner)
            {
                if (lastWinners.size() == 0)
                {
                    lastWinners.addAll(winnerIds);
                }
                else
                {
                    // TODO: check this does what we expect
                    if (!lastWinners.equals(winnerIds))
                    {
                        sameWinner = false;
                    }
                }
            }
        }
        if (recordWinAll && sameWinner)
        {
            for (Integer winnerId : lastWinners)
            {
                EntrantResult result = results.get(winnerId);
                result.updateResultItem(ResultItem.WIN_ALL.toString(), 1);
                result.updateResultItem(ResultItem.POINTS.toString(), mScoringSystem.pointsForWinAll());
            }
        }
    }

    /**
     * Determine the result items to use in calculations based on the scoring system.
     * 
     * @param system the scoring system to use
     * @param runningTotal whether to include a running total (for multi-table outputs)
     * 
     * @return The result items.
     */
    @SuppressWarnings("static-method")
    private List<String> determineResultItems(ScoringSystem system, boolean runningTotal)
    {
        List<String>  resultItems = new ArrayList<>(ResultItem.values().length);

        resultItems.add(ResultItem.MATCHES.toString());
        resultItems.add(ResultItem.GAMES.toString());
        resultItems.add(ResultItem.WINS.toString());
        if (system.scoreFastWins())
        {
            resultItems.add(ResultItem.FAST_WINS.toString());
        }
        if (system.scoreWinAll())
        {
            resultItems.add(ResultItem.WIN_ALL.toString());
        }
        resultItems.add(ResultItem.POINTS.toString());
        if (runningTotal)
        {
            resultItems.add(ResultItem.RUNNING_TOTAL.toString());
        }

        return resultItems;
    }
}
