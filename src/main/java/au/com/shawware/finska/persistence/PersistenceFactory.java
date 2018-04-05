/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.util.HashMap;
import java.util.Map;

import au.com.shawware.compadmin.entity.AbstractEntity;
import au.com.shawware.util.StringUtil;

/**
 * Factory for creating persistence stores.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class PersistenceFactory
{
    /** The singleton instance. */
    private static Map<String, PersistenceFactory> sFactories = new HashMap<>();
    /** The root directory for entity sub-directories. */
    private final String mRoot;

    /**
     * Construct a new instance.
     * 
     * @param root the root directory to store entities in
     */
    private PersistenceFactory(String root)
    {
        if (StringUtil.isEmpty(root))
        {
            throw new IllegalArgumentException("Empty root directory"); //$NON-NLS-1$
        }
        mRoot = root;
    }

    /**
     * Retrieves a single instance of this factory.
     * 
     * @param root the root directory to store entities in
     * 
     * @return The factory.
     */
    public static synchronized PersistenceFactory getFactory(String root)
    {
        if (!sFactories.containsKey(root))
        {
            sFactories.put(root, new PersistenceFactory(root));
        }
        return sFactories.get(root);
    }

    /**
     * Creates an entity store for the given class.
     * 
     * @param clazz the entity type to store
     * 
     * @return The store.
     */
    public <EntityType extends AbstractEntity> IEntityStore<EntityType> getStore(Class<EntityType> clazz)
    {
        String name = clazz.getSimpleName();
        String directory = mRoot + '/' + name.toLowerCase();
        return new EntityDiskStore<EntityType>(directory, name, name.substring(0, 1), clazz);
    }
}
