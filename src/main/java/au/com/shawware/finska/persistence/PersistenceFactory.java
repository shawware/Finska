/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import au.com.shawware.finska.entity.AbstractEntity;

/**
 * Factory for creating persistence stores.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class PersistenceFactory
{
    private static final String ROOT = "/tmp/finska";

    /**
     * Prevent construction.
     */
    private PersistenceFactory() {}

    /**
     * Creates an entity store for the given class.
     * 
     * @param clazz the entity type to store
     * 
     * @return The store.
     */
    public static <EntityType extends AbstractEntity> IEntityStore<EntityType> getStore(Class<EntityType> clazz)
    {
        String name = clazz.getSimpleName();
        String directory = ROOT + '/' + name.toLowerCase();
        return new EntityDiskStore<EntityType>(directory, name, name.substring(0, 1), clazz);
    }
}
