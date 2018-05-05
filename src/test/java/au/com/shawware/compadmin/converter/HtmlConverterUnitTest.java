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
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.EntityRepository;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.finska.service.ResultsService;
import au.com.shawware.util.persistence.PersistenceException;
import au.com.shawware.util.persistence.PersistenceFactory;

/**
 * Exercise and verify HTML output.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "static-method" })
public class HtmlConverterUnitTest extends AbstractScoringUnitTest
{
    /**
     * Generates HTML from a set of persisted entities.
     */
    @Test
    public void leaderBoardTest()
    {
        try
        {
            IEntityRepository repository = EntityRepository.getRepository(PersistenceFactory.getFactory("./data"));
            ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1, 0);
            ResultsService service = new ResultsService(repository, scoringSystem);
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
            IEntityRepository repository = EntityRepository.getRepository(PersistenceFactory.getFactory("./data"));
            ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1, 0);
            ResultsService service = new ResultsService(repository, scoringSystem);
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
