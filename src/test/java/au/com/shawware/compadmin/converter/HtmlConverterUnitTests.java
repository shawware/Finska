/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.converter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.compadmin.scoring.AbstractScoringUnitTest;
import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.compadmin.scoring.TestAssistant;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.CompetitionLoader;
import au.com.shawware.finska.persistence.PersistenceException;

/**
 * Exercise and verify HTML output.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("nls")
public class HtmlConverterUnitTests extends AbstractScoringUnitTest
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
        try
        {
            converter.convert(sPlayers, leaderBoard, output);
            output.flush();
        }
        catch (IOException | RuntimeException e)
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
    public void extendedTest()
    {
        try
        {
            CompetitionLoader loader = CompetitionLoader.getLoader("./data");
            Map<Integer, Player> players = loader.getPlayers();
            Map<Integer, Competition> comps = loader.getCompetitions();
            System.out.println("Players: " + players.size());
            System.out.println("Comp: " + comps.get(1).toString());
        }
        catch (PersistenceException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            Assert.fail("Unexpected error");
        }
    }
}
