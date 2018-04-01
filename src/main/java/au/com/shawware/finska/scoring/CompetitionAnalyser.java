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

/**
 * Analyses competitions to produce artifacts such as leader boards.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class CompetitionAnalyser extends AbstractLeaderBoardAssistant
{
    /** The competition being analysed. */
    private final Competition mCompetition;
    /** The scoring system in use. */
    private final ScoringSystem mScoringSystem;

    /**
     * Constructs a new analyser for the given competition and scoring system.
     * 
     * @param competition the competition to analyse
     * @param scoringSystem the scoring system to use
     */
    public CompetitionAnalyser(Competition competition, ScoringSystem scoringSystem)
    {
        super(ResultItem.getComparisonItems());
        mCompetition   = competition;
        mScoringSystem = scoringSystem;
    }

    @SuppressWarnings("boxing")
    @Override
    public List<EntrantResult> compileResults()
    {
        List<String> resultItems = determineResultItems(mScoringSystem);

        Map<Integer, EntrantResult> results = new HashMap<>();
        for (Integer matchID : mCompetition.getMatchIds())
        {
            Match match = mCompetition.getMatch(matchID);
            for (Integer playerID : match.getPlayersIds())
            {
                if (!results.containsKey(playerID))
                {
                    results.put(playerID, new EntrantResult(playerID, resultItems));
                }
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
        return results.values().stream().collect(Collectors.toList());
    }

    /**
     * Determine the result items to use in calculations based on the scoring system.
     * 
     * @param system the scoring system to use
     * 
     * @return The result items.
     */
    @SuppressWarnings("static-method")
    private List<String> determineResultItems(ScoringSystem system)
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

        return resultItems;
    }
}
