/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.util.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

import au.com.shawware.util.test.AbstractUnitTest;

/**
 * Base class for persistence unit tests.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({"nls", "static-method", "boxing" })
public abstract class AbstractPersistenceUnitTest extends AbstractUnitTest
{
    /** Test root directory for persisted entities. */
    protected static final String PERSISTENCE_ROOT = "/tmp/persist";
    /** The root path to persist to. */
    protected static Path sRoot;

    /**
     * Setup test fixtures and the like before all tests.
     * 
     * @throws IOException file error
     */
    @BeforeClass
    public static void initialSetup()
        throws IOException
    {
        sRoot = new File(PERSISTENCE_ROOT).toPath();
        Files.createDirectories(sRoot);
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
     * Verifies the basic storage and retrieval of an entity.
     * 
     * @param store the entity store to use
     * @param instance an entity instance
     * 
     * @throws PersistenceException persistence error
     */
    protected final <T extends AbstractEntity<?>> void verifyBasicStorage(IEntityStore<T> store, T instance)
        throws PersistenceException
    {
        Map<Integer, T> ts = store.getAll();
        Assert.assertNotNull(ts);
        Assert.assertEquals(0, ts.size());

        T t1 = store.create(instance);
        Assert.assertNotNull(t1);
        T t2 = store.get(t1.getId());
        Assert.assertNotNull(t2);
        Assert.assertEquals(t1.getId(), t2.getId());
        Assert.assertEquals(t1.getKey(), t2.getKey());
        Assert.assertEquals(t1.toString(), t2.toString());

        verifyEntityMap(store.getAll(), t1);
    }

    /**
     * Verifies the given entity map contains the given entity instance.
     * 
     * @param map the map to verify
     * @param instance the instance to verify
     */
    protected final <T extends AbstractEntity<?>> void verifyEntityMap(Map<Integer, T> map, T instance)
    {
        Assert.assertNotNull(map);
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(true, map.containsKey(instance.getId()));
        Assert.assertEquals(map.get(instance.getId()).toString(), instance.toString());
    }
}
