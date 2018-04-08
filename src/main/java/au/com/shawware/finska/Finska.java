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
import au.com.shawware.compadmin.scoring.ILeaderBoardAssistant;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.CompetitionLoader;
import au.com.shawware.finska.persistence.PersistenceException;
import au.com.shawware.finska.persistence.PersistenceFactory;
import au.com.shawware.finska.scoring.CompetitionAnalyser;
import au.com.shawware.finska.scoring.ScoringSystem;

/**
 * Simple Finska program to generate output from specified competition data.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Finska
{
    /** The data directory that holds competition data. */
    private final String mDataDir;
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
        mDataDir   = dataDir;
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
            CompetitionLoader loader = CompetitionLoader.getLoader(PersistenceFactory.getFactory(mDataDir));
            Map<Integer, Player> players = loader.getPlayers();
            Map<Integer, Competition> comps = loader.getCompetitions();
            if (comps.size() > 0)
            {
                Competition competition = comps.get(1);
                ScoringSystem scoringSystem = new ScoringSystem(3, 1, 1, 1, 0);
                ILeaderBoardAssistant assistant = new CompetitionAnalyser(players, competition, scoringSystem);
                List<EntrantResult> leaderBoard = LeaderBoardGenerator.generateLeaderBoard(assistant);
                Writer output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mOutputDir + "/leaderboard.html")));
                IConverter converter = new HtmlConverter("finska");
                converter.convertOverallResults(players, leaderBoard, output);
                output.close();
            }
        }
        catch (PersistenceException | IOException e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            status = 1;
        }
        return status;
    }
}
