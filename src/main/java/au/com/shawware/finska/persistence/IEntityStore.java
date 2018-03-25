/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.util.Map;

import au.com.shawware.compadmin.entity.AbstractEntity;

/**
 * The persistence API for all entities.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 * 
 * @param <Entity> the type of entity being persisted
 */
public interface IEntityStore<Entity extends AbstractEntity>
{
    /**
     * Retrieves all the entities in the store.
     * 
     * @return The list of entities. Can be empty, but is never null.
     * 
     * @throws PersistenceException error accessing the store
     */
    Map<Integer, Entity> getAll()
        throws PersistenceException;

    /**
     * Creates a new entity and returns it with its ID.
     * 
     * @param entity the entity to create (sans ID)
     *
     * @return The created entity.
     * 
     * @throws PersistenceException error accessing the store
     */
    Entity create(Entity entity)
        throws PersistenceException;

    /**
     * Retrieves the given entity from the store.
     * 
     * @param id the entity's ID
     *
     * @return The corresponding entity.
     * 
     * @throws PersistenceException error accessing the store
     */
    Entity get(int id)
        throws PersistenceException;

    /**
     * Updates the given entity in the store.
     * 
     * @param entity the entity to update
     * 
     * @throws PersistenceException error accessing the store
     */
    void update(Entity entity)
        throws PersistenceException;

    /**
     * Deletes the given entity from the store*.
     * 
     * @param id the entity's ID
     * 
     * @throws PersistenceException error accessing the store
     */
    void delete(int id)
        throws PersistenceException;
}
