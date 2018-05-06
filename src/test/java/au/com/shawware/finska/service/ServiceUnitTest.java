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
import java.util.Set;

import org.junit.Assert;
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
@SuppressWarnings({"nls", "boxing" })
public class ServiceUnitTest extends AbstractFinskaPersistenceUnitTest
{
    /**
     * Test the round entity.
     * 
     * @throws PersistenceException error during storage
     */
    @Test
    public void testRounds()
        throws PersistenceException
    {
        Player p1 = new Player("David");
        Player p2 = new Player("Paul");
        Player p3 = new Player("Jane");

        p1 = sPlayerStore.create(p1);
        p2 = sPlayerStore.create(p2);
        p3 = sPlayerStore.create(p3);

        FinskaCompetition comp = new FinskaCompetition("C1", LocalDate.of(2018,  5,  5));

        comp = sCompetitionStore.create(comp);

        ScoringSystem scoringSystem = new ScoringSystem(1, 0, 0, 0, 0);
        ServiceFactory services = ServiceFactory.getFactory(sFactory, scoringSystem);
        CreateService service = services.getCreateService();

        int[] playerIds = new int[] {p1.getId(), p3.getId()};
        LocalDate roundDate = LocalDate.of(2018, 5, 6);

        Set<Integer> roundIds = comp.getRoundIds();
        List<FinskaRound> rounds = comp.getRounds();
        Assert.assertEquals(0, roundIds.size());
        Assert.assertEquals(0, rounds.size());

        FinskaRound round = service.createRound(comp.getId(), roundDate, playerIds);

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

        comp = services.getResultsService().getCompetition();
        roundIds = comp.getRoundIds();
        rounds = comp.getRounds();
        FinskaRound copy = comp.getRound(round.getKey());
        Assert.assertEquals(1, roundIds.size());
        Assert.assertTrue(roundIds.contains(round.getKey()));
        Assert.assertEquals(1, rounds.size());
        Assert.assertEquals(round.toString(), copy.toString());

        verifyCheckedExceptionThrown(() -> service.createRound(0, null, null),            IllegalArgumentException.class, "Empty round date");
        verifyCheckedExceptionThrown(() -> service.createRound(0, roundDate, null),       IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> service.createRound(0, roundDate, new int[0]), IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> service.createRound(0, roundDate, playerIds),  PersistenceException.class,     "Competition does not exist: 0");
    }
}
