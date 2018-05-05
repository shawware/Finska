/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.BeforeClass;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.entity.FinskaMatch;
import au.com.shawware.finska.entity.FinskaRound;
import au.com.shawware.finska.entity.Player;
import au.com.shawware.util.persistence.AbstractPersistenceUnitTest;
import au.com.shawware.util.persistence.IEntityStore;
import au.com.shawware.util.persistence.PersistenceFactory;

/**
 * Base class when dealing with persisting Finska entities.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("nls")
public abstract class AbstractFinskaPersistenceUnitTest extends AbstractPersistenceUnitTest
{
    /** Persisted match sub-directory. */
    private static final String MATCH_DIR  = "match";
    /** Persisted round sub-directory. */
    private static final String ROUND_DIR  = "round";
    /** Persisted player sub-directory. */
    private static final String PLAYER_DIR = "player";
    /** Persisted competition sub-directory. */
    private static final String COMP_DIR   = "competition";

    /** The repository factory. */
    protected static PersistenceFactory sFactory;

    /** The test store for players. */
    protected static IEntityStore<Player> sPlayerStore;
    /** The test store for competitions. */
    protected static IEntityStore<FinskaCompetition> sCompetitionStore;
    /** The test store for rounds. */
    protected static IEntityStore<FinskaRound> sRoundStore;
    /** The test store for matches. */
    protected static IEntityStore<FinskaMatch> sMatchStore;

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

        sFactory          = PersistenceFactory.getFactory(PERSISTENCE_ROOT);
        sPlayerStore      = sFactory.getStore(Player.class);
        sCompetitionStore = sFactory.getStore(FinskaCompetition.class, "Finska");
        sRoundStore       = sFactory.getStore(FinskaRound.class, "Finska");
        sMatchStore       = sFactory.getStore(FinskaMatch.class, "Finska");
    }
}
