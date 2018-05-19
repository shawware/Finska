/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.time.LocalDate;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Exercise the persistence layer.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({"nls", "boxing" })
public class PersistenceUnitTest extends AbstractFinskaPersistenceUnitTest
{
    /**
     * Verifies that entities can be stored and retrieved correctly.
     * 
     * @throws PersistenceException persistence error
     */
    @Test
    public void entityChecks()
        throws PersistenceException
    {
        final int ROUND = 100;
        final int MATCH = 42;

        Player p1 = new Player("David");
        verifyBasicStorage(sPlayerStore, p1);

        FinskaRound r1 = new FinskaRound(ROUND, LocalDate.of(2018, 3, 10));
        FinskaMatch m1 = new FinskaMatch(MATCH, r1.getRoundDate());
        m1.addWinner(p1);
        m1.setFastWin(true);
        verifyBasicStorage(sMatchStore, m1);

        r1.addPlayer(p1);
        r1.addMatch(m1);
        verifyBasicStorage(sRoundStore, r1);

        FinskaCompetition c1 = new FinskaCompetition("C1", LocalDate.of(2018, 3, 9));
        c1.addEntrant(p1);
        c1.addRound(r1);
        verifyBasicStorage(sCompetitionStore, c1);

        Map<Integer, FinskaCompetition> allComps = EntityRepository.getRepository(sFactory).getCompetitions();
        verifyEntityMap(allComps, c1);

        FinskaCompetition c2 = allComps.get(c1.getId());
        Player p2 = c2.getEntrant(p1.getId());
        Assert.assertEquals(p1.toString(), p2.toString());
        FinskaRound r2 = c2.getRound(r1.getKey());
        Assert.assertEquals(r1.toString(), r2.toString());
        FinskaMatch m2 = r2.getMatch(m1.getKey());
        Assert.assertEquals(m1.toString(), m2.toString());
        p2 = r2.getPlayer(p1.getId());
        Assert.assertEquals(p1.toString(), p2.toString());
    }
}
