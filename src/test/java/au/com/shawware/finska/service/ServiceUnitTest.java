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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
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
    /** The competition service to use in our tests. */
    private static CompetitionService sCompetitionService;
    /** The round service to use in our tests. */
    private static RoundService sRoundService;
    /** The match service to use in our tests. */
    private static MatchService sMatchService;
    /** The match service to use in our tests. */
    private static PlayerService sPlayerService;
    /** Today. */
    private static LocalDate sToday;

    /**
     * Setup test fixtures and the like before all tests.
     * 
     * @throws PersistenceException persistence error
     */
    @BeforeClass
    public static void setupServices()
        throws PersistenceException
    {
        ScoringSystem scoringSystem = new ScoringSystem(1, 0, 0, 0, 0);
        ServiceFactory services = ServiceFactory.getFactory(sFactory, scoringSystem);
        sResultsService     = services.getResultsService();
        sCompetitionService = services.getCompetitionService();
        sRoundService       = services.getRoundService();
        sMatchService       = services.getMatchService();
        sPlayerService      = services.getPlayerService();
        sToday              = LocalDate.now();

        // Create some players to use in the tests.
        sPlayerService.createPlayer("David");
        sPlayerService.createPlayer("Paul");
        sPlayerService.createPlayer("Jane");
    }

    /**
     * Test the competition entity.
     * 
     * @throws PersistenceException error during storage
     */
    @Test
    public void testCompetitionService()
        throws PersistenceException
    {
        Map<Integer, Player> players = sPlayerService.getPlayers();
        int[] playerIds = players.keySet().stream().mapToInt(Integer::intValue).toArray();

        LocalDate startDate = sToday.plusDays(1); // Ensure start date is in the future.
        FinskaCompetition c1 = sCompetitionService.createCompetition("T1 - A", startDate, playerIds);
 
        verifyCompetition(c1, c1.getId(), "T1 - A", startDate, playerIds, false);
 
        FinskaCompetition c2 = sResultsService.getCompetition(c1.getId());
        verifyCompetition(c2, c1.getId(), "T1 - A", startDate, playerIds, true);

        LocalDate updatedStartDate = startDate.plusDays(2);
        int[] updatedPlayerIds = new int[playerIds.length - 1];
        for (int i=1; i<playerIds.length; i++) {
            updatedPlayerIds[i-1] = playerIds[i];
        }
 
        sCompetitionService.updateCompetition(c1.getId(), "T1 - B", updatedStartDate, updatedPlayerIds);

        c2 = sResultsService.getCompetition(c1.getId());
        verifyCompetition(c2, c1.getId(), "T1 - B", updatedStartDate, updatedPlayerIds, true);
    }

    /**
     * Verify the given competition is as expected.
     * 
     * @param competition the competition under test
     * @param competitionID the expected competition ID
     * @param name the expected name
     * @param startDate the expected start date
     * @param playerIds the expected player IDs
     * @param playersPresent whether the players will be present in the competition
     */
    private void verifyCompetition(FinskaCompetition competition, int competitionID, String name,
                                   LocalDate startDate, int[] playerIds, boolean playersPresent)
    {
        Assert.assertNotNull(competition);
        Assert.assertEquals(competitionID, competition.getId());
        Assert.assertEquals(name, competition.getKey());
        Assert.assertEquals(startDate, competition.getStartDate());

        Set<Integer> IDs = competition.getEntrantIds();
        Assert.assertEquals(playerIds.length, IDs.size());
        Arrays.stream(playerIds).forEach(id -> {
            Assert.assertTrue(IDs.contains(id));
            if (playersPresent) {
                competition.getEntrant(id); // Will throw an exception if not present
            }
        });
    }

    /**
     * Test the round entity.
     * 
     * @throws PersistenceException error during storage
     */
    @Test
    public void testRoundAndMatchServices()
        throws PersistenceException
    {
        Player p1 = sPlayerService.createPlayer("Michael");
        Player p2 = sPlayerService.createPlayer("Peter");
        Player p3 = sPlayerService.createPlayer("Tom");

        int[] playerIds = new int[] { p1.getId(), p2.getId(), p3.getId() };

        FinskaCompetition competition = sCompetitionService.createCompetition("C1", sToday.minusDays(7), playerIds);
        int numberOfRounds = competition.numberOfRounds(); // Other tests may have added rounds

        playerIds = new int[] { p1.getId(), p3.getId() };
        LocalDate roundDate = competition.getStartDate().plusDays(1);

        Set<Integer> roundIds = competition.getRoundIds();
        List<FinskaRound> rounds = competition.getRounds();
        Assert.assertEquals(numberOfRounds, roundIds.size());
        Assert.assertEquals(numberOfRounds, rounds.size());

        FinskaRound round = sRoundService.createRound(competition.getId(), roundDate, playerIds);

        // Refresh the data after the change
        competition = sResultsService.getCurrentCompetition();

        Assert.assertEquals(numberOfRounds + 1, competition.numberOfRounds());
        verifyRound(round, numberOfRounds + 1, roundDate, playerIds);

        int[] updatedPlayerIds = new int[] { p2.getId(), p3.getId() };
        LocalDate changedDate = roundDate.plusDays(1);

        round = sRoundService.updateRound(competition.getId(), round.getKey(), changedDate, updatedPlayerIds);

        Assert.assertEquals(numberOfRounds + 1, competition.numberOfRounds());
        Assert.assertEquals(0, round.numberOfMatches());
        verifyRound(round, numberOfRounds + 1, changedDate, updatedPlayerIds);

        int[] winnerIds = new int[] { p3.getId() };

        FinskaMatch match = sMatchService.createMatch(competition.getId(), round.getKey(), winnerIds, false);

        // Refresh the data after the change
        competition = sResultsService.getCurrentCompetition();
        round = competition.getRound(round.getKey());

        Assert.assertEquals(1, round.numberOfMatches());

        verifyMatch(match, round.getKey(), winnerIds, false);
    }

    /**
     * Verify the given round is as expected.
     * 
     * @param round the round under test
     * @param roundNumber the expected round number
     * @param roundDate the expected round date
     * @param playerIds the expected player IDs
     */
    private void verifyRound(FinskaRound round, int roundNumber, LocalDate roundDate, int[] playerIds)
    {
        Assert.assertNotNull(round);
        Assert.assertTrue(round.getId() > FinskaRound.DEFAULT_ID);
        Assert.assertEquals(Integer.valueOf(roundNumber), round.getKey());
        Assert.assertEquals(roundDate, round.getRoundDate());

        Set<Integer> IDs = round.getPlayerIds();
        Assert.assertEquals(playerIds.length, IDs.size());
        Arrays.stream(playerIds).forEach(id -> {
            Assert.assertTrue(IDs.contains(id));
            round.getPlayer(id); // Will throw an exception if not present
            Assert.assertTrue(round.hasPlayer(id));
        });

        FinskaCompetition comp = sResultsService.getCurrentCompetition();
        Set<Integer> roundIds = comp.getRoundIds();
        List<FinskaRound> rounds = comp.getRounds();
        FinskaRound copy = comp.getRound(round.getKey());
        Assert.assertEquals(roundNumber, roundIds.size());
        Assert.assertTrue(roundIds.contains(round.getKey()));
        Assert.assertEquals(roundNumber, rounds.size());
        Assert.assertEquals(round.toString(), copy.toString());
    }

    /**
     * Verifies the given match against the given data.
     * 
     * @param match the match to verify
     * @param roundNumber the match's round number
     * @param winnerIds the expected IDs of the winners
     * @param fastWin whether the winners are expected to have had a fast win
     */
    private void verifyMatch(FinskaMatch match, int roundNumber, int[] winnerIds, boolean fastWin)
    {
        Assert.assertNotNull(match);
        Assert.assertTrue(match.getId() > FinskaMatch.DEFAULT_ID);
        Assert.assertEquals(Integer.valueOf(1), match.getKey());
        Assert.assertEquals(fastWin, match.isFastWin());

        Set<Integer> IDs = match.getWinnerIds();
        Assert.assertEquals(winnerIds.length, IDs.size());
        Arrays.stream(winnerIds).forEach(id -> {
            Assert.assertTrue(IDs.contains(id));
            match.getWinner(id); // Will throw an exception if not present
        });

        FinskaCompetition comp = sResultsService.getCurrentCompetition();
        FinskaRound round = comp.getRound(roundNumber);
        Set<Integer> matchIds = round.getMatchIds();
        List<FinskaMatch> matches = round.getMatches();
        FinskaMatch copy = round.getMatch(match.getKey());
        Assert.assertEquals(1, matchIds.size());
        Assert.assertTrue(matchIds.contains(match.getKey()));
        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(match.toString(), copy.toString());
    }

    /**
     * Test the player entity.
     * 
     * @throws PersistenceException error during storage
     */
    @Test
    public void testPlayerService()
        throws PersistenceException
    {
        Map<Integer, Player> players = sPlayerService.getPlayers();
        int initialCount = players.size();

        Player p1 = sPlayerService.createPlayer("Test");
        Assert.assertNotNull(p1);
        Assert.assertEquals("Test", p1.getKey());

        Player p2 = sPlayerService.getPlayer(p1.getId());
        Assert.assertNotNull(p2);
        Assert.assertEquals(p1.getId(), p2.getId());
        Assert.assertEquals("Test", p2.getKey());

        players = sPlayerService.getPlayers();
        Assert.assertEquals(initialCount + 1, players.size());
        Assert.assertTrue(players.containsKey(p1.getId()));
        Assert.assertEquals(p1.toString(), players.get(p1.getId()).toString());

        p2 = sPlayerService.updatePlayer(p1.getId(), "Delta");
        Assert.assertNotNull(p2);
        Assert.assertEquals(p1.getId(), p2.getId());
        Assert.assertEquals("Delta", p2.getKey());

        p2 = sPlayerService.getPlayer(p1.getId());
        Assert.assertNotNull(p2);
        Assert.assertEquals(p1.getId(), p2.getId());
        Assert.assertEquals("Delta", p2.getKey());

        players = sPlayerService.getPlayers();
        Assert.assertEquals(initialCount + 1, players.size());
        Assert.assertTrue(players.containsKey(p2.getId()));
        Assert.assertEquals(p2.toString(), players.get(p2.getId()).toString());
    }

    /**
     * Verifies the handling for service methods.
     * 
     * @throws PersistenceException persistence error
     */
    @Test
    public void verifyErrorHandling()
        throws PersistenceException
    {
        int[] playerIds = new int[] { 1, 2, 3 };
        FinskaCompetition competition = sCompetitionService.createCompetition("Error Test", sToday.plusDays(14), playerIds);
        LocalDate roundDate = competition.getStartDate().plusDays(5);

        verifyCheckedExceptionThrown(() -> sPlayerService.createPlayer(null),                      IllegalArgumentException.class, "Empty player name");
        verifyCheckedExceptionThrown(() -> sPlayerService.createPlayer(""),                        IllegalArgumentException.class, "Empty player name");

        verifyCheckedExceptionThrown(() -> sPlayerService.updatePlayer(0, null),                   IllegalArgumentException.class, "Empty player name");
        verifyCheckedExceptionThrown(() -> sPlayerService.updatePlayer(0, ""),                     IllegalArgumentException.class, "Empty player name");

        verifyCheckedExceptionThrown(() -> sRoundService.createRound(0, null, null),               IllegalArgumentException.class, "Empty round date");
        verifyCheckedExceptionThrown(() -> sRoundService.createRound(0, roundDate, null),          IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sRoundService.createRound(0, roundDate, new int[0]),    IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sRoundService.createRound(0, roundDate, playerIds),     PersistenceException.class,     "Competition does not exist: 0");

        verifyCheckedExceptionThrown(() -> sRoundService.updateRound(0, 0, null, null),            IllegalArgumentException.class, "Empty round date");
        verifyCheckedExceptionThrown(() -> sRoundService.updateRound(0, 0, roundDate, null),       IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sRoundService.updateRound(0, 0, roundDate, new int[0]), IllegalArgumentException.class, "Empty player IDs");
        verifyCheckedExceptionThrown(() -> sRoundService.updateRound(0, 0, roundDate, playerIds),  PersistenceException.class,     "Competition does not exist: 0");
        verifyCheckedExceptionThrown(() -> sRoundService.updateRound(1, 0, roundDate, playerIds),  IllegalArgumentException.class, "Round 0 is not present in this competition");

        verifyCheckedExceptionThrown(() -> sMatchService.createMatch(0, 0, null, false),           IllegalArgumentException.class, "Empty winner IDs");
        verifyCheckedExceptionThrown(() -> sMatchService.createMatch(0, 0, new int[0], false),     IllegalArgumentException.class, "Empty winner IDs");
        verifyCheckedExceptionThrown(() -> sMatchService.createMatch(0, 0, playerIds, false),      PersistenceException.class,     "Competition does not exist: 0");
        verifyCheckedExceptionThrown(() -> sMatchService.createMatch(1, 0, playerIds, false),      IllegalArgumentException.class, "Round 0 is not present in this competition");

        verifyCheckedExceptionThrown(() -> sMatchService.updateMatch(0, 0, 0, null, false),        IllegalArgumentException.class, "Empty winner IDs");
        verifyCheckedExceptionThrown(() -> sMatchService.updateMatch(0, 0, 0, new int[0], false),  IllegalArgumentException.class, "Empty winner IDs");
        verifyCheckedExceptionThrown(() -> sMatchService.updateMatch(0, 0, 0, playerIds, false),   PersistenceException.class,     "Competition does not exist: 0");
        verifyCheckedExceptionThrown(() -> sMatchService.updateMatch(1, 0, 0, playerIds, false),   IllegalArgumentException.class, "Round 0 is not present in this competition");
 
        FinskaRound round = sRoundService.createRound(competition.getId(), roundDate, playerIds);
        verifyCheckedExceptionThrown(() ->
            sMatchService.updateMatch(competition.getId(), round.getKey(), 0, playerIds, false),   IllegalArgumentException.class, "Match 0 is not present in this round");
    }
}
