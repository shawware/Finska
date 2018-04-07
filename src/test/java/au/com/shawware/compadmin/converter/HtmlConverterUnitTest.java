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

import au.com.shawware.compadmin.scoring.AbstractScoringUnitTest;
import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.ILeaderBoardAssistant;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.compadmin.scoring.TestAssistant;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.CompetitionLoader;
import au.com.shawware.finska.persistence.PersistenceException;
import au.com.shawware.finska.persistence.PersistenceFactory;
import au.com.shawware.finska.scoring.CompetitionAnalyser;
import au.com.shawware.finska.scoring.ScoringSystem;

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
        final int[][] results = new int[][]
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
        TestAssistant assistant = new TestAssistant(convertFixture(results), sComparisonItems);
        List<EntrantResult> leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);

        Writer output = new BufferedWriter(new OutputStreamWriter(System.out));
        IConverter converter = new HtmlConverter("finska");
        outputLeaderboard(sPlayers, leaderBoard, converter, output);
    }

    /**
     * Generates HTML from a set of persisted entities.
     */
    @Test
    public void leaderBoardTest()
    {
        try
        {
            CompetitionLoader loader = CompetitionLoader.getLoader(PersistenceFactory.getFactory("./data"));
            Map<Integer, Player> players = loader.getPlayers();
            ILeaderBoardAssistant assistant = buildAssistant(loader, players);
            List<EntrantResult> leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);
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
            CompetitionLoader loader = CompetitionLoader.getLoader(PersistenceFactory.getFactory("./data"));
            Map<Integer, Player> players = loader.getPlayers();
            ILeaderBoardAssistant assistant = buildAssistant(loader, players);
            List<List<EntrantResult>> roundResults = assistant.compileRoundResults();
            Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/tmp/rounds.html")));
            IConverter converter = new HtmlConverter("finska");
            converter.convertRoundResults(players, roundResults, output);
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
     * Builds a leader board assistant from the given players and data.
     * 
     * @param loader the loader to source the competition from
     * @param players the players in the competition
     * 
     * @return The built assistant.
     * 
     * @throws PersistenceException error loading data
     */
    private ILeaderBoardAssistant buildAssistant(CompetitionLoader loader, Map<Integer, Player> players)
        throws PersistenceException
    {
        Map<Integer, Competition> comps = loader.getCompetitions();
        Competition competition = comps.get(1);
        ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1);
        return new CompetitionAnalyser(players, competition, scoringSystem);
    }

    /**
     * Outputs the given leader board data using the given converter to the given output.
     * 
     * @param players the player data
     * @param leaderBoard the leader board
     * @param converter the converter to use
     * @param output where to send the output
     */
    private void outputLeaderboard(Map<Integer, Player> players, List<EntrantResult> leaderBoard, IConverter converter, Writer output)
    {
        try
        {
            converter.convertOverallResults(players, leaderBoard, output);
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
