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
import au.com.shawware.compadmin.scoring.ResultSpec;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
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
    private final FinskaCompetition mCompetition;
    /** The scoring system in use. */
    private final ScoringSystem mScoringSystem;

    /**
     * Constructs a new analyser for the given players, competition and scoring system.
     * 
     * @param players the full set of players in the competition
     * @param competition the competition to analyse
     * @param scoringSystem the scoring system to use
     */
    public CompetitionAnalyser(Map<Integer, Player> players, FinskaCompetition competition, ScoringSystem scoringSystem)
    {
        super(ResultItem.getComparisonSpecification());
        mPlayers       = players;
        mCompetition   = competition;
        mScoringSystem = scoringSystem;
    }

    @Override
    public List<EntrantResult> compileOverallResults()
    {
        return compileOverallResults(mCompetition.getRoundIds().size());
    }

    @Override
    public List<EntrantResult> compileOverallResults(int rounds)
    {
        Set<Integer> roundIDs = mCompetition.getRoundIds();
        if ((rounds <= 0) || (rounds > roundIDs.size()))
        {
            throw new IllegalArgumentException("Invalid number of rounds: " + rounds); //$NON-NLS-1$
        }

        ResultSpec spec = determineResultItems(mScoringSystem, false);

        Map<Integer, EntrantResult> results = new HashMap<>();
        for (Integer playerID : mPlayers.keySet())
        {
            results.put(playerID, new EntrantResult(playerID, spec));
        }

        // Process each round until we reach the limit;
        int i = 1;
        for (Integer roundID : roundIDs)
        {
            if (i > rounds)
            {
                break;
            }
            FinskaRound round = mCompetition.getRound(roundID);
            processRound(results, round);
            i++;
        }

        for (Integer playerID : mPlayers.keySet())
        {
            EntrantResult result = results.get(playerID);
            double roundsPlayed = result.getResultItemValueAsInt(ResultItem.ROUNDS.toString());
            if (roundsPlayed > 0.0)
            {
                result.setResultItem(ResultItem.POINTS_PER_ROUND.toString(),
                        result.getResultItemValueAsInt(ResultItem.POINTS.toString()) / roundsPlayed);
            }
        }

        return results.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<List<EntrantResult>> compileRoundResults()
    {
        ResultSpec spec = determineResultItems(mScoringSystem, true);
        Set<Integer> roundIds = mCompetition.getRoundIds();
        List<List<EntrantResult>> results = new ArrayList<>(roundIds.size());

        Map<Integer, MutableInteger> runningTotals = new HashMap<>(mPlayers.size());
        for (Integer playerID : mPlayers.keySet())
        {
            runningTotals.put(playerID, new MutableInteger(0));
        }

        for (Integer roundID : roundIds)
        {
            Map<Integer, EntrantResult> roundResults = new HashMap<>(mPlayers.size());
            for (Integer playerID : mPlayers.keySet())
            {
                roundResults.put(playerID, new EntrantResult(playerID, spec));
            }

            FinskaRound round = mCompetition.getRound(roundID);
            processRound(roundResults, round);

            List<EntrantResult> roundResult = new ArrayList<>(mPlayers.size());
            for (Integer playerID : mPlayers.keySet())
            {
                EntrantResult playerResult = roundResults.get(playerID);
                MutableInteger runningTotal = runningTotals.get(playerID);
                runningTotal.incrementBy(playerResult.getResultItemValueAsInt(ResultItem.POINTS.toString()));
                playerResult.incrementResultItem(ResultItem.RUNNING_TOTAL.toString(), runningTotal.getValue());
                roundResult.add(playerResult);
            }
            results.add(roundResult);
        }

        return results;
    }

    /**
     * Process the given round. That is, evaluate each match and update
     * the results for the entrants accordingly.
     * 
     * @param results the players' results so far
     * @param round the round to process
     */
    private void processRound(Map<Integer, EntrantResult> results, FinskaRound round)
    {
        for (Integer playerID : round.getPlayersIds())
        {
            EntrantResult result = results.get(playerID);
            result.incrementResultItem(ResultItem.ROUNDS.toString(), 1);
            result.incrementResultItem(ResultItem.MATCHES.toString(), round.getMatchIds().size());
            if (mScoringSystem.scorePointsForPlaying())
            {
                result.incrementResultItem(ResultItem.POINTS.toString(), mScoringSystem.pointsForPlaying());
            }
        }
        Set<Integer> matchIDs    = round.getMatchIds();
        boolean recordWinBoth    = (mScoringSystem.scoreWinBoth() && (matchIDs.size() > 1));
        boolean recordWinAll     = (mScoringSystem.scoreWinAll() && (matchIDs.size() > 1));
        boolean sameWinner       = true;
        int matchCount           = 0;
        Set<Integer> lastWinners = new HashSet<>();
        for (Integer matchID : matchIDs)
        {
            FinskaMatch match = round.getMatch(matchID);
            if (!match.hasWinner())
            {
                continue; // Skip matches that have not been played yet.
            }
            matchCount++;
            Set<Integer> winnerIds = match.getWinnerIds();
            for (Integer winnerId : winnerIds)
            {
                EntrantResult result = results.get(winnerId);
                result.incrementResultItem(ResultItem.WINS.toString(), 1);
                result.incrementResultItem(ResultItem.POINTS.toString(), mScoringSystem.pointsForWin());
                if (mScoringSystem.scoreFastWins() && match.isFastWinner(winnerId))
                {
                    result.incrementResultItem(ResultItem.FAST_WINS.toString(), 1);
                    result.incrementResultItem(ResultItem.POINTS.toString(), mScoringSystem.pointsForFastWin());
                }
            }
            // Track "win both" and "win all"
            if ((recordWinBoth && sameWinner && matchCount <= 2) ||
                (recordWinAll && sameWinner))
            {
                if (lastWinners.size() == 0)
                {
                    lastWinners.addAll(winnerIds);
                }
                else
                {
                    if (!lastWinners.equals(winnerIds))
                    {
                        sameWinner = false;
                    }
                }
            }
        }
        // Score "win both" and "win all"
        if (sameWinner && (recordWinBoth || recordWinAll))
        {
            ResultItem winItem = recordWinBoth ? ResultItem.WIN_BOTH : ResultItem.WIN_ALL;
            int winPoints      = recordWinBoth ? mScoringSystem.pointsForWinBoth() : mScoringSystem.pointsForWinAll();
            for (Integer winnerId : lastWinners)
            {
                EntrantResult result = results.get(winnerId);
                result.incrementResultItem(winItem.toString(), 1);
                result.incrementResultItem(ResultItem.POINTS.toString(), winPoints);
            }
        }
    }

    /**
     * Calculates the result specification to use in calculations based on the scoring system.
     * 
     * @param system the scoring system to use
     * @param runningTotal whether to include a running total (for multi-table outputs)
     * 
     * @return The result items.
     */
    @SuppressWarnings("static-method")
    private ResultSpec determineResultItems(ScoringSystem system, boolean runningTotal)
    {
        ResultSpec spec = new ResultSpec();

        spec.addItem(ResultItem.ROUNDS.toString());
        spec.addItem(ResultItem.MATCHES.toString());
        spec.addItem(ResultItem.WINS.toString());
        if (system.scoreFastWins())
        {
            spec.addItem(ResultItem.FAST_WINS.toString());
        }
        if (system.scoreWinBoth())
        {
            spec.addItem(ResultItem.WIN_BOTH.toString());
        }
        if (system.scoreWinAll())
        {
            spec.addItem(ResultItem.WIN_ALL.toString());
        }
        spec.addItem(ResultItem.POINTS.toString());
        if (runningTotal)
        {
            spec.addItem(ResultItem.RUNNING_TOTAL.toString());
        }
        else
        {
            spec.addItem(ResultItem.POINTS_PER_ROUND.toString(), false);
        }

        return spec;
    }
}
