/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.converter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.compadmin.entity.Entrant;
import au.com.shawware.compadmin.scoring.AbstractScoringUnitTest;
import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.compadmin.scoring.TestAssistant;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.EntityLoader;
import au.com.shawware.finska.persistence.IEntityLoader;
import au.com.shawware.finska.persistence.PersistenceException;
import au.com.shawware.finska.persistence.PersistenceFactory;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.finska.service.ResultsService;

/**
 * Exercise and verify HTML output.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "boxing", "static-method" })
public class HtmlConverterUnitTest extends AbstractScoringUnitTest
{
    /**
     * Generates some HTML output for a set of results.
     */
    @Test
    public void basicOutput()
    {
        final Number[][] results = new Number[][]
        {
            { 1, 9, 5, 20,  5, 16 },
            { 2, 9, 1,  3, -2, 10 },
            { 3, 9, 8, 30, 20, 24 },
            { 4, 9, 2,  3, -1, 10 },
            { 5, 9, 4, 19,  5, 16 },
            { 6, 9, 3, 18,  5, 16 },
            { 7, 9, 7, 25, 10, 21 },
            { 8, 9, 6, 25, 10, 21 },
            { 9, 9, 0,  5, -5,  0 },
        };
        TestAssistant assistant = new TestAssistant(convertFixture(results), sCompetition, sEntrants, sComparisonSpec);
        List<EntrantResult> leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);

        Writer output = new BufferedWriter(new OutputStreamWriter(System.out));
        IConverter converter = new HtmlConverter("finska");
        outputLeaderboard(sEntrants, leaderBoard, converter, output);
    }

    /**
     * Generates HTML from a set of persisted entities.
     */
    @Test
    public void leaderBoardTest()
    {
        try
        {
            IEntityLoader loader = EntityLoader.getLoader(PersistenceFactory.getFactory("./data"));
            ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1, 0);
            ResultsService service = new ResultsService(loader, scoringSystem);
            service.initialise();
            Map<Integer, Player> players = service.getPlayers();
            List<EntrantResult> leaderBoard = service.getLeaderBoard();
            Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/tmp/leaderboard.html")));
            IConverter converter = new HtmlConverter("finska");
            outputLeaderboard(players, leaderBoard, converter, output);
            output.close();
        }
        catch (PersistenceException | IOException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            Assert.fail("Unexpected error");
        }
    }

    /**
     * Generates HTML from a set of persisted entities.
     */
    @Test
    public void roundResultsTest()
    {
        try
        {
            IEntityLoader loader = EntityLoader.getLoader(PersistenceFactory.getFactory("./data"));
            ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1, 0);
            ResultsService service = new ResultsService(loader, scoringSystem);
            service.initialise();
            List<List<EntrantResult>> roundResults = service.getRoundResults();
            Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/tmp/rounds.html")));
            IConverter converter = new HtmlConverter("finska");
            converter.convertRoundResults(service.getPlayers(), roundResults, output);
            output.close();
        }
        catch (PersistenceException | IOException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            Assert.fail("Unexpected error");
        }
    }

    /**
     * Outputs the given leader board data using the given converter to the given output.
     * 
     * @param entrants the entrant data
     * @param leaderBoard the leader board
     * @param converter the converter to use
     * @param output where to send the output
     */
    private <EntrantType extends Entrant> void outputLeaderboard(Map<Integer, EntrantType> entrants, List<EntrantResult> leaderBoard, IConverter converter, Writer output)
    {
        try
        {
            converter.convertOverallResults(entrants, leaderBoard, output);
            output.flush();
        }
        catch (IOException | RuntimeException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            Assert.fail("Unexpected error");
        }
    }
}
