/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.util.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Exercise the utility persistence layer and disk-based persistence.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings("nls")
public class DiskPersistenceUnitTest extends AbstractPersistenceUnitTest
{
    /** Persisted entity sub-directory. */
    private static final String ENTITY_DIR = "entity";
    /** Persisted address sub-directory. */
    private static final String ADDRESS_DIR = "address";

    /**
     * Setup test fixtures and the like before all tests.
     * 
     * @throws IOException file error
     */
    @BeforeClass
    public static void setup()
        throws IOException
    {
        Files.createDirectory(sRoot.resolve(ENTITY_DIR));
        Files.createDirectory(sRoot.resolve(ADDRESS_DIR));
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
        IEntityStore<NamedEntity> entityStore = factory.getStore(NamedEntity.class, "Named");
        IEntityStore<Address> addressStore = factory.getStore(Address.class);

        NamedEntity e1 = new NamedEntity("David");
        verifyBasicStorage(entityStore, e1);

        Map<Integer, NamedEntity> allEntities = entityStore.getAll();
        verifyEntityMap(allEntities, e1);

        Address a1 = new Address(123, "Key Street");
        verifyBasicStorage(addressStore, a1);

        Map<Integer, Address> allAddresses = addressStore.getAll();
        verifyEntityMap(allAddresses, a1);
    }
}
