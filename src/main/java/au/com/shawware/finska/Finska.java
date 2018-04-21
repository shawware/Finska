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
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.EntityLoader;
import au.com.shawware.finska.persistence.IEntityLoader;
import au.com.shawware.finska.persistence.PersistenceFactory;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.finska.service.ResultsService;

/**
 * Simple Finska program to generate output from specified competition data.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Finska
{
    /** The competition data source. */
    private final IEntityLoader mLoader;
    /** The results service to use. */
    private final ResultsService mService;
    /** The directory to store output in. */
    private final String mOutputDir;

    /**
     * Constructs a new Finska program
     * 
     * @param dataDir the data directory
     * @param outputDir the output directory
     */
    private Finska(String dataDir, String outputDir)
    {
        mLoader    = EntityLoader.getLoader(PersistenceFactory.getFactory(dataDir));
        mService   = new ResultsService(mLoader, new ScoringSystem(3, 1, 1, 1, 0));
        mOutputDir = outputDir;
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
        Finska finska = new Finska(args[0], args[1]);
        System.exit(finska.run());
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
            Map<Integer, Player> players = mService.getPlayers();
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
