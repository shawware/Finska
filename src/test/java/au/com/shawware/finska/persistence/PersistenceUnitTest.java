/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.persistence.AbstractPersistenceUnitTest;
import au.com.shawware.util.persistence.IEntityStore;
import au.com.shawware.util.persistence.PersistenceException;
import au.com.shawware.util.persistence.PersistenceFactory;

/**
 * Exercise the persistence layer.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({"nls", "boxing" })
public class PersistenceUnitTest extends AbstractPersistenceUnitTest
{
    /** Persisted match sub-directory. */
    private static final String MATCH_DIR  = "match";
    /** Persisted round sub-directory. */
    private static final String ROUND_DIR  = "round";
    /** Persisted player sub-directory. */
    private static final String PLAYER_DIR = "player";
    /** Persisted competition sub-directory. */
    private static final String COMP_DIR   = "competition";

    /**
     * Setup test fixtures and the like before all tests.
     * 
     * @throws IOException file error
     */
    @BeforeClass
    public static void setup()
        throws IOException
    {
        Files.createDirectory(sRoot.resolve(PLAYER_DIR));
        Files.createDirectory(sRoot.resolve(COMP_DIR));
        Files.createDirectory(sRoot.resolve(ROUND_DIR));
        Files.createDirectory(sRoot.resolve(MATCH_DIR));
    }

    /**
     * Verifies that entities can be stored and retrieved correctly.
     * 
     * @throws PersistenceException persistence error
     */
    @Test
    public void entityChecks()
        throws PersistenceException
    {
        PersistenceFactory factory = PersistenceFactory.getFactory(PERSISTENCE_ROOT);
        IEntityStore<Player> playerStore = factory.getStore(Player.class);
        IEntityStore<FinskaCompetition> competitionStore = factory.getStore(FinskaCompetition.class, "Finska");
        IEntityStore<FinskaRound> roundStore = factory.getStore(FinskaRound.class, "Finska");
        IEntityStore<FinskaMatch> matchStore = factory.getStore(FinskaMatch.class, "Finska");

        final int ROUND = 100;
        final int MATCH = 42;

        Player p1 = new Player("David");
        verifyBasicStorage(playerStore, p1);

        FinskaRound r1 = new FinskaRound(ROUND, LocalDate.of(2018, 3, 10));
        FinskaMatch m1 = new FinskaMatch(MATCH, r1.getRoundDate());
        m1.addWinner(p1, true);
        verifyBasicStorage(matchStore, m1);

        r1.addPlayer(p1);
        r1.addMatch(m1);
        verifyBasicStorage(roundStore, r1);

        FinskaCompetition c1 = new FinskaCompetition("C1", LocalDate.of(2018, 3, 9));
        c1.addRound(r1);
        verifyBasicStorage(competitionStore, c1);

        Map<Integer, Player> allPlayers = EntityLoader.getLoader(factory).getPlayers();
        verifyEntityMap(allPlayers, p1);

        Map<Integer, FinskaCompetition> allComps = EntityLoader.getLoader(factory).getCompetitions();
        verifyEntityMap(allComps, c1);

        FinskaCompetition c2 = allComps.get(c1.getId());
        FinskaRound r2 = c2.getRound(r1.getKey());
        Assert.assertEquals(r1.toString(), r2.toString());
        FinskaMatch m2 = r2.getMatch(m1.getKey());
        Assert.assertEquals(m1.toString(), m2.toString());
        Player p2 = r2.getPlayer(p1.getId());
        Assert.assertEquals(p1.toString(), p2.toString());
    }
}
