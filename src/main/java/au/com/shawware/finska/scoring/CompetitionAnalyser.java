/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.com.shawware.compadmin.scoring.AbstractResultsCompiler;
import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.ResultSpec;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;

/**
 * Analyses competitions to produce artifacts such as leader boards.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("boxing")
public class CompetitionAnalyser extends AbstractResultsCompiler<FinskaCompetition, FinskaRound, FinskaMatch, Player>
{
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
        super(competition, players, ResultItem.getComparisonSpecification());
        mScoringSystem = scoringSystem;
    }

    @Override
    protected ResultSpec createResultSpecification(boolean includeRunningTotal)
    {
        ResultSpec spec = new ResultSpec();

        spec.addItem(ResultItem.ROUNDS.toString());
        spec.addItem(ResultItem.MATCHES.toString());
        spec.addItem(ResultItem.WINS.toString());
        if (mScoringSystem.scoreFastWins())
        {
            spec.addItem(ResultItem.FAST_WINS.toString());
        }
        if (mScoringSystem.scoreWinBoth())
        {
            spec.addItem(ResultItem.WIN_BOTH.toString());
        }
        if (mScoringSystem.scoreWinAll())
        {
            spec.addItem(ResultItem.WIN_ALL.toString());
        }
        spec.addItem(ResultItem.POINTS.toString());
        if (includeRunningTotal)
        {
            spec.addItem(ResultItem.RUNNING_TOTAL.toString());
        }
        else
        {
            spec.addItem(ResultItem.POINTS_PER_ROUND.toString(), false);
        }

        return spec;
    }

    @Override
    protected void postCompile(EntrantResult result)
    {
        double roundsPlayed = result.getResultItemValueAsInt(ResultItem.ROUNDS.toString());
        if (roundsPlayed > 0.0)
        {
            result.setResultItem(ResultItem.POINTS_PER_ROUND.toString(),
                    result.getResultItemValueAsInt(ResultItem.POINTS.toString()) / roundsPlayed);
        }
    }

    @Override
    protected String getPointsItemName()
    {
        return ResultItem.POINTS.toString();
    }

    @Override
    protected String getRunningTotalItemName()
    {
        return ResultItem.RUNNING_TOTAL.toString();
    }

    @Override
    protected void processRound(Map<Integer, EntrantResult> results, FinskaRound round)
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
}
