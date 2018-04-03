/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.ILeaderBoardAssistant;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Game;
import au.com.shawware.finska.entity.Match;
import au.com.shawware.finska.entity.Player;

/**
 * Exercise and verify the scoring system and related outputs.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "boxing", "static-method" })
public class ScoringSystemUnitTest
{
    /**
     * Verify the correct result items are used for each scoring system setting.
     */
    @Test
    public void testResultItems()
    {
        // Setup a very basic competition with enough data to ensure one result item.
        Player p1 = new Player(1, "David");
        Game g1 = new Game(1, 1);
        g1.addWinner(p1);
        Match m1 = new Match(1, 1, LocalDate.of(2018, 3, 9));
        m1.addPlayer(p1);
        m1.addGame(g1);
        Competition c1 = new Competition(1, "C1", LocalDate.of(2018, 3, 10));
        c1.addMatch(m1);

        verifyResultItems(c1, 3, 0, -1, -7, false, false, false);
        verifyResultItems(c1, 3, 1,  0,  0, true,  false, false);
        verifyResultItems(c1, 3, 1,  2,  0, true,   true, false);
        verifyResultItems(c1, 3, 1,  0,  3, true,  false,  true);
        verifyResultItems(c1, 3, 1,  1,  1, true,   true,  true);
    }

    /**
     * Verify the correct results items are being added to results
     * as per the given configuration items for a scoring system.
     * 
     * @param competition the competition under test
     * @param win points for a win
     * @param play points for playing
     * @param fast points for fast wins
     * @param all points for winning all
     * @param expectScoreForPlaying whether scoring for playing is expected
     * @param expectScoreFastWins whether scoring for fast wins is expected
     * @param expectScoreWinAll whether scoring for win all is expected
     */
    private void verifyResultItems(Competition competition, int win, int play, int fast, int all,
            boolean expectScoreForPlaying, boolean expectScoreFastWins, boolean expectScoreWinAll)
    {
        ScoringSystem scoringSystem = new ScoringSystem(win, play, fast, all);

        verifyScoringSystem(scoringSystem, win, play, fast, all, expectScoreForPlaying, expectScoreFastWins, expectScoreWinAll);

        ILeaderBoardAssistant a1 = new CompetitionAnalyser(null, competition, scoringSystem);

        List<EntrantResult> results = a1.compileOverallResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

        EntrantResult result = results.get(0);

        // Verify the result items that are always present
        result.getResultItemValue(ResultItem.GAMES.toString());
        result.getResultItemValue(ResultItem.WINS.toString());
        result.getResultItemValue(ResultItem.POINTS.toString());
        // Verify the result items whose presence depends on the scoring system
        verifyResultItem(result, ResultItem.FAST_WINS, expectScoreFastWins);
        verifyResultItem(result, ResultItem.WIN_ALL, expectScoreWinAll);
    }

    /**
     * Verify the given scoring system has been configured as expected
     * 
     * @param scoringSystem the scoring system under test
     * @param win points for a win
     * @param play points for playing
     * @param fast points for fast wins
     * @param all points for winning all
     * @param expectScoreForPlaying whether scoring for playing is expected
     * @param expectScoreFastWins whether scoring for fast wins is expected
     * @param expectScoreWinAll whether scoring for win all is expected
     */
    private void verifyScoringSystem(ScoringSystem scoringSystem, int win, int play, int fast, int all,
            boolean expectScoreForPlaying, boolean expectScoreFastWins, boolean expectScoreWinAll)
    {
        Assert.assertNotNull(scoringSystem);
        Assert.assertEquals(expectScoreForPlaying, scoringSystem.scorePointsForPlaying());
        Assert.assertEquals(expectScoreFastWins,   scoringSystem.scoreFastWins());
        Assert.assertEquals(expectScoreWinAll,     scoringSystem.scoreWinAll());
        Assert.assertEquals(win,                   scoringSystem.pointsForWin());
        Assert.assertEquals(expectScoreForPlaying ? play : 0, scoringSystem.pointsForPlaying());
        Assert.assertEquals(expectScoreFastWins   ? fast : 0, scoringSystem.pointsForFastWin());
        Assert.assertEquals(expectScoreForPlaying ? all  : 0, scoringSystem.pointsForWinAll());
    }

    /**
     * Verify the given result item is present in the result as expected.
     * 
     * @param result the result to inspect
     * @param item the result item under test
     * @param present whether the result item is expected
     */
    private void verifyResultItem(EntrantResult result, ResultItem item, boolean present)
    {
        try
        {
            result.getResultItemValue(item.toString());
            Assert.assertEquals(true, present);
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertEquals(false, present);
        }
    }
}
