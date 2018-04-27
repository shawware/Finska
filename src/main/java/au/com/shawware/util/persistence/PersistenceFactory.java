/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.util.persistence;

import java.util.HashMap;
import java.util.Map;

import au.com.shawware.util.StringUtil;

/**
 * Factory for creating persistence stores.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class PersistenceFactory
{
    /** Class name prefix to ignore. */
    private static final String PREFIX = "Finska"; //$NON-NLS-1$
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
     * @return This factory's root directory.
     */
    public String getRoot()
    {
        return mRoot;
    }

    /**
     * Creates an entity store for the given class.
     * 
     * @param clazz the entity type to store
     * @param <EntityType> the type of entity for the store
     * 
     * @return The store.
     */
    public <EntityType extends AbstractEntity> IEntityStore<EntityType> getStore(Class<EntityType> clazz)
    {
        String name = clazz.getSimpleName();
        if (name.startsWith(PREFIX))
        {
            name = name.substring(PREFIX.length());
        }
        String directory = mRoot + '/' + name.toLowerCase();
        return new EntityDiskStore<EntityType>(directory, name, name.substring(0, 1), clazz);
    }

    @Override
    public String toString()
    {
        return StringUtil.toString(mRoot, sFactories);
    }
}
