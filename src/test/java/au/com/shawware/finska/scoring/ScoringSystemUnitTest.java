/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.IResultsCompiler;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.test.AbstractUnitTest;

/**
 * Exercise and verify the scoring system and related outputs.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "boxing", "static-method" })
public class ScoringSystemUnitTest extends AbstractUnitTest
{
    /**
     * Verify the correct result items are used for each scoring system setting.
     */
    @Test
    public void testResultItems()
    {
        // Setup a very basic competition with enough data to ensure one result item.
        Player p1 = new Player(1, "David");
        FinskaRound r1 = new FinskaRound(1, 1, LocalDate.of(2018, 3, 9));
        FinskaMatch m1 = new FinskaMatch(1, 1, r1.getRoundDate());
        m1.addWinner(p1);
        r1.addPlayer(p1);
        r1.addMatch(m1);
        FinskaCompetition c1 = new FinskaCompetition(1, "C1", LocalDate.of(2018, 3, 10));
        c1.addRound(r1);

        verifyResultItems(c1, p1, 3, 0, -1,  0, -7, false, false, false, false);
        verifyResultItems(c1, p1, 3, 1,  0,  0,  0,  true, false, false, false);
        verifyResultItems(c1, p1, 3, 1,  2,  0,  0,  true,  true, false, false);
        verifyResultItems(c1, p1, 3, 1,  0,  2,  0,  true, false,  true, false);
        verifyResultItems(c1, p1, 3, 1,  0,  0,  3,  true, false, false,  true);
        verifyResultItems(c1, p1, 3, 1,  1,  0,  1,  true,  true, false,  true);

        verifyExceptionThrown(() -> new ScoringSystem(3, 0, 0, 1, 1),
                IllegalArgumentException.class, "Win both and win all specified simultaneously");
    }

    /**
     * Verify the correct results items are being added to results
     * as per the given configuration items for a scoring system.
     * 
     * @param competition the competition under test
     * @param player the test player in the competition
     * @param win points for a win
     * @param play points for playing
     * @param fast points for fast wins
     * @param both points for winning both
     * @param all points for winning all
     * @param expectScoreForPlaying whether scoring for playing is expected
     * @param expectScoreFastWins whether scoring for fast wins is expected
     * @param expectScoreWinBoth whether scoring for win both is expected
     * @param expectScoreWinAll whether scoring for win all is expected
     */
    private void verifyResultItems(FinskaCompetition competition, Player player,
            int win, int play, int fast, int both, int all,
            boolean expectScoreForPlaying, boolean expectScoreFastWins,
            boolean expectScoreWinBoth, boolean expectScoreWinAll)
    {
        Map<Integer, Player> players = new HashMap<>();
        players.put(player.getId(), player);

        ScoringSystem scoringSystem = new ScoringSystem(win, play, fast, both, all);

        verifyScoringSystem(scoringSystem, win, play, fast, both, all,
                expectScoreForPlaying, expectScoreFastWins,
                expectScoreWinBoth, expectScoreWinAll);

        IResultsCompiler compiler = new CompetitionAnalyser(players, competition, scoringSystem);

        List<EntrantResult> results = compiler.compileCurrentResults();
        Assert.assertNotNull(results);
        Assert.assertEquals(1, results.size());

        EntrantResult result = results.get(0);

        // Verify the result items that are always present
        result.getResultItemValueAsInt(ResultItem.MATCHES.toString());
        result.getResultItemValueAsInt(ResultItem.WINS.toString());
        result.getResultItemValueAsInt(ResultItem.POINTS.toString());
        // Verify the result items whose presence depends on the scoring system
        verifyResultItem(result, ResultItem.FAST_WINS, expectScoreFastWins);
        verifyResultItem(result, ResultItem.WIN_BOTH, expectScoreWinBoth);
        verifyResultItem(result, ResultItem.WIN_ALL, expectScoreWinAll);
    }

    /**
     * Verify the given scoring system has been configured as expected
     * 
     * @param scoringSystem the scoring system under test
     * @param win points for a win
     * @param play points for playing
     * @param fast points for fast wins
     * @param both points for winning both
     * @param all points for winning all
     * @param expectScoreForPlaying whether scoring for playing is expected
     * @param expectScoreFastWins whether scoring for fast wins is expected
     * @param expectScoreWinBoth whether scoring for win both is expected
     * @param expectScoreWinAll whether scoring for win all is expected
     */
    private void verifyScoringSystem(ScoringSystem scoringSystem,
            int win, int play, int fast, int both, int all,
            boolean expectScoreForPlaying, boolean expectScoreFastWins,
            boolean expectScoreWinBoth, boolean expectScoreWinAll)
    {
        Assert.assertNotNull(scoringSystem);
        Assert.assertEquals(expectScoreForPlaying, scoringSystem.scorePointsForPlaying());
        Assert.assertEquals(expectScoreFastWins,   scoringSystem.scoreFastWins());
        Assert.assertEquals(expectScoreWinBoth,    scoringSystem.scoreWinBoth());
        Assert.assertEquals(expectScoreWinAll,     scoringSystem.scoreWinAll());
        Assert.assertEquals(win,                   scoringSystem.pointsForWin());
        Assert.assertEquals(expectScoreForPlaying ? play : 0, scoringSystem.pointsForPlaying());
        Assert.assertEquals(expectScoreFastWins   ? fast : 0, scoringSystem.pointsForFastWin());
        Assert.assertEquals(expectScoreWinBoth    ? both : 0, scoringSystem.pointsForWinBoth());
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
            result.getResultItemValueAsInt(item.toString());
            Assert.assertEquals(true, present);
        }
        catch (IllegalArgumentException e)
        {
            Assert.assertEquals(false, present);
        }
    }
}
