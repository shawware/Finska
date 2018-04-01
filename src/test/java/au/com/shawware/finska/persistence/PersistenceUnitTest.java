/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import au.com.shawware.compadmin.entity.AbstractEntity;
import au.com.shawware.finska.entity.Competition;
import au.com.shawware.finska.entity.Game;
import au.com.shawware.finska.entity.Match;
import au.com.shawware.finska.entity.Player;

/**
 * Exercise the persistence layer.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({"nls", "static-method", "boxing" })
public class PersistenceUnitTest
{
    /** Test root directory for persisted entities. */
    private static final String PERSISTENCE_ROOT = "/tmp/finska";
    /** Persisted game sub-directory. */
    private static final String GAME_DIR   = "game";
    /** Persisted match sub-directory. */
    private static final String MATCH_DIR  = "match";
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
        Path root = new File(PERSISTENCE_ROOT).toPath();
        Files.createDirectories(root);
        Files.createDirectory(root.resolve(PLAYER_DIR));
        Files.createDirectory(root.resolve(COMP_DIR));
        Files.createDirectory(root.resolve(GAME_DIR));
        Files.createDirectory(root.resolve(MATCH_DIR));
    }

    /**
     * Cleanup after all tests.
     * 
     * @throws IOException file error
     */
    @AfterClass
    public static void tearDown()
        throws IOException
    {
        Runtime.getRuntime().exec("rm -r " + PERSISTENCE_ROOT);
    }

    /**
     * Verifies that entities can be stored and retrieved correctly.
     * 
     * @throws PersistenceException persistence error
     */
    @Test
    public void basicChecks()
        throws PersistenceException
    {
        PersistenceFactory factory = PersistenceFactory.getFactory(PERSISTENCE_ROOT);
        IEntityStore<Player> playerStore = factory.getStore(Player.class);
        IEntityStore<Competition> competitionStore = factory.getStore(Competition.class);
        IEntityStore<Match> matchStore = factory.getStore(Match.class);
        IEntityStore<Game> gameStore = factory.getStore(Game.class);

        Player p1 = new Player("David");
        verifyBasicStorage(playerStore, p1);

        Game g1 = new Game(1);
        g1.addWinner(p1, true);
        verifyBasicStorage(gameStore, g1);

        Match m1 = new Match(1, LocalDate.of(2018, 3, 10));
        m1.addPlayer(p1);
        m1.addGame(g1);
        verifyBasicStorage(matchStore, m1);

        Competition c1 = new Competition("C1", LocalDate.of(2018, 3, 9));
        c1.addMatch(m1);
        verifyBasicStorage(competitionStore, c1);

        Map<Integer, Player> allPlayers = CompetitionLoader.getLoader(PERSISTENCE_ROOT).getPlayers();
        verifyEntityMap(allPlayers, p1);

        Map<Integer, Competition> allComps = CompetitionLoader.getLoader(PERSISTENCE_ROOT).getCompetitions();
        verifyEntityMap(allComps, c1);

        Competition c2 = allComps.get(c1.getId());
        Match m2 = c2.getMatch(m1.getId());
        Assert.assertEquals(m1.toString(), m2.toString());
        Game g2 = m2.getGame(g1.getId());
        Assert.assertEquals(g1.toString(), g2.toString());
        Player p2 = m2.getPlayer(p1.getId());
        Assert.assertEquals(p1.toString(), p2.toString());
    }

    /**
     * Verifies the basic storage and retrieval of an entity.
     * 
     * @param store the entity store to use
     * @param instance an entity instance
     * 
     * @throws PersistenceException persistence error
     */
    private <T extends AbstractEntity> void verifyBasicStorage(IEntityStore<T> store, T instance)
        throws PersistenceException
    {
        Map<Integer, T> ts = store.getAll();
        Assert.assertNotNull(ts);
        Assert.assertEquals(0, ts.size());

        T t1 = store.create(instance);
        Assert.assertNotNull(t1);
        T t2 = store.get(t1.getId());
        Assert.assertNotNull(t2);
        Assert.assertEquals(t1.toString(), t2.toString());

        verifyEntityMap(store.getAll(), t1);
    }

    /**
     * Verifies the given entity map contains the given entity instance.
     * 
     * @param map the map to verify
     * @param instance the instance to verify
     */
    private <T extends AbstractEntity> void verifyEntityMap(Map<Integer, T> map, T instance)
    {
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(true, map.containsKey(instance.getId()));
        Assert.assertEquals(map.get(instance.getId()).toString(), instance.toString());
    }
}
