/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.finska.persistence.AbstractFinskaPersistenceUnitTest;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Exercise the business services - mostly around data.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({"nls", "boxing", "static-method" })
public class ServiceUnitTest extends AbstractFinskaPersistenceUnitTest
{
    /** The results service to use in our tests. */
    private static ResultsService sResultsService;
    /** The create service to use in our tests. */
    private static CreateService sCreateService;
    /** The test competition. */
    private static FinskaCompetition sCompetition;
    /** The test players. */
    private static Map<Integer, Player> sPlayers;

    /**
     * Setup test fixtures and the like before all tests.
     * 
     * @throws PersistenceException persistence error
     */
    @BeforeClass
    public static void setupServices()
        throws PersistenceException
    {
        FinskaCompetition comp = new FinskaCompetition("C1", LocalDate.of(2018,  5,  5));
        sCompetition = sCompetitionStore.create(comp);

        Player p1 = new Player("David");
        Player p2 = new Player("Paul");
        Player p3 = new Player("Jane");

        sPlayerStore.create(p1);
        sPlayerStore.create(p2);
        sPlayerStore.create(p3);

        sPlayers = sPlayerStore.getAll();

        ScoringSystem scoringSystem = new ScoringSystem(1, 0, 0, 0, 0);
        ServiceFactory services = ServiceFactory.getFactory(sFactory, scoringSystem);
        sResultsService = services.getResultsService();
        sCreateService = services.getCreateService();
    }

    /**
     * Test the round entity.
     * 
     * @throws PersistenceException error during storage
     */
    @Test
    public void testRounds()
        throws PersistenceException
    {
        /*
         * We've added three players to the store, but other tests may have also added players.
         * So we just get the first three we can find.
         */
        List<Integer> allPlayerIds = sPlayers.keySet().stream().collect(Collectors.toList());
        Assert.assertTrue(allPlayerIds.size() >= 3);
        Player p1 = sPlayers.get(allPlayerIds.get(0));
        Player p2 = sPlayers.get(allPlayerIds.get(1));
        Player p3 = sPlayers.get(allPlayerIds.get(2));

        int[] playerIds = new int[] { p1.getId(), p3.getId() };
        LocalDate roundDate = LocalDate.of(2018, 5, 6);

        Set<Integer> roundIds = sCompetition.getRoundIds();
        List<FinskaRound> rounds = sCompetition.getRounds();
        Assert.assertEquals(0, roundIds.size());
        Assert.assertEquals(0, rounds.size());

        FinskaRound round = sCreateService.createRound(sCompetition.getId(), roundDate, playerIds);

        verifyRound(round, roundDate, playerIds);

        int[] updatedPlayerIds = new int[] { p2.getId(), p3.getId() };
        LocalDate changedDate = roundDate.plusDays(1);

        round = sCreateService.updateRound(sCompetition.getId(), round.getKey(), changedDate, updatedPlayerIds);

        verifyRound(round, changedDate, updatedPlayerIds);
    }

    /**
     * Verify the given round is as expected.
     * 
     * @param round the round under test
     * @param roundDate the expected round date
     * @param playerIds the expected player IDs
     */
    private void verifyRound(FinskaRound round, LocalDate roundDate, int[] playerIds)
    {
        Assert.assertNotNull(round);
        Assert.assertTrue(round.getId() > FinskaRound.DEFAULT_ID);
        Assert.assertEquals(Integer.valueOf(1), round.getKey());
        Assert.assertEquals(roundDate, round.getRoundDate());

        Set<Integer> IDs = round.getPlayerIds();
        Assert.assertEquals(playerIds.length, IDs.size());
        Arrays.stream(playerIds).forEach(id -> {
            Assert.assertTrue(IDs.contains(id));
            round.getPlayer(id); // Will throw an exception if not present
        });

        FinskaCompetition comp = sResultsService.getCompetition();
        Set<Integer> roundIds = comp.getRoundIds();
        List<FinskaRound> rounds = comp.getRounds();
        FinskaRound copy = comp.getRound(round.getKey());
        Assert.assertEquals(1, roundIds.size());
        Assert.assertTrue(roundIds.contains(round.getKey()));
        Assert.assertEquals(1, rounds.size());
        Assert.assertEquals(round.toString(), copy.toString());
    }

    /**
     * Verifies the handling for the create service methods.
     */
    @Test
    public void verifyErrorHandling()
    {
        LocalDate roundDate = LocalDate.of(2018, 5, 6);
        int[] playerIds = new int[] { 1, 2, 3 };

        verifyCheckedExceptionThrown(() -> sCreateService.createRound(0, null, null),               IllegalArgumentException.class, "Empty round date");
        verifyCheckedExceptionThrown(() -> sCreateService.createRound(0, roundDate, null),          IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sCreateService.createRound(0, roundDate, new int[0]),    IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sCreateService.createRound(0, roundDate, playerIds),     PersistenceException.class,     "Competition does not exist: 0");

        verifyCheckedExceptionThrown(() -> sCreateService.updateRound(0, 0, null, null),            IllegalArgumentException.class, "Empty round date");
        verifyCheckedExceptionThrown(() -> sCreateService.updateRound(0, 0, roundDate, null),       IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sCreateService.updateRound(0, 0, roundDate, new int[0]), IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sCreateService.updateRound(0, 0, roundDate, playerIds),  PersistenceException.class,     "Competition does not exist: 0");
        verifyCheckedExceptionThrown(() -> sCreateService.updateRound(1, 0, roundDate, playerIds),  IllegalArgumentException.class, "Round 0 is not present in this competition");
    }
}
