/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.util.persistence;

/**
 * The base class for all persisted entities.
 * We assume that the persistence mechanism will allocate a unique
 * ID to each entity (that is a sub-class). Each entity also has a
 * key (of the given type) which is used as a local identifier and
 * is not necessarily unique.
 *
 * @param <KeyType> the entity key type
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class AbstractEntity<KeyType> implements Comparable<AbstractEntity<KeyType>>
{
    /** The default ID to use when an entity is yet to be assigned one. */
    public static final int DEFAULT_ID = 0;

    /** The entity's unique ID. */
    private int mId;

    /** The entity's key. */
    private final KeyType mKey;

    /**
     * Constructs a new base entity with the default ID.
     * 
     * @param key the entity's key
     */
    protected AbstractEntity(KeyType key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Null key"); //$NON-NLS-1$
        }
        mId  = DEFAULT_ID;
        mKey = key;
    }

    /**
     * Constructs a new base entity with the given ID.
     * 
     * @param id the entity's ID
     * @param key the entity's key
     */
    protected AbstractEntity(int id, KeyType key)
    {
        this(key);
        setId(id);
    }

    /**
     * @return This entity's ID.
     */
    public final int getId()
    {
        return mId;
    }

    /**
     * Sets this entity's ID.
     * 
     * @param id the new ID
     */
    public void setId(int id)
    {
        if (id < DEFAULT_ID)
        {
            throw new IllegalArgumentException("Invalid id: " + id); //$NON-NLS-1$
        }
        mId = id;
    }

    /**
     * @return This entity's key.
     */
    public final KeyType getKey()
    {
        return mKey;
    }

    @Override
    public int compareTo(AbstractEntity<KeyType> that)
    {
        if (that == null)
        {
            throw new IllegalArgumentException("Null entity"); //$NON-NLS-1$
        }
        return this.mId - that.mId;
    }
}
