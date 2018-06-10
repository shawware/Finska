/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import au.com.shawware.compadmin.converter.HtmlConverter;
import au.com.shawware.compadmin.converter.IConverter;
import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.finska.service.ResultsService;
import au.com.shawware.finska.service.ServiceFactory;
import au.com.shawware.util.persistence.PersistenceException;
import au.com.shawware.util.persistence.PersistenceFactory;

/**
 * Simple Finska program to generate output from specified competition data.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("nls")
public class Finska
{
    /** The results service to use. */
    private final ResultsService mService;
    /** The directory to store output in. */
    private final String mOutputDir;

    /**
     * Constructs a new Finska program
     * 
     * @param dataDir the data directory
     * @param outputDir the output directory
     * 
     * @throws PersistenceException error during initialisation
     */
    private Finska(String dataDir, String outputDir)
        throws PersistenceException
    {
        PersistenceFactory factory = PersistenceFactory.getFactory(dataDir);
        ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1, 0);
        mService = ServiceFactory.getFactory(factory, scoringSystem).getResultsService();
        mOutputDir  = outputDir;
    }

    /**
     * The starting point.
     * 
     * @param args program arguments
     */
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.err.println("usage: finska <data dir> <output dir>");
            System.exit(1);
        }
        try
        {
            Finska finska = new Finska(args[0], args[1]);
            System.exit(finska.run());
        }
        catch (PersistenceException e)
        {
            System.err.println("Peristence error: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Execute the program.
     * 
     * @return A status code indicating success (0) or otherwise.
     */
    private int run()
    {
        int status = 0;
        try
        {
            FinskaCompetition competition = mService.getCurrentCompetition();
            Map<Integer, Player> players = competition.getEntrantMap();
            List<EntrantResult> leaderBoard = mService.getLeaderBoard();
            if ((leaderBoard.size() > 0) && (players.size() > 0))
            {
                Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mOutputDir + "/leaderboard.html")));
                IConverter converter = new HtmlConverter("finska");
                converter.convertOverallResults(players, leaderBoard, output);
                output.close();
            }
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            status = 1;
        }
        return status;
    }
}
