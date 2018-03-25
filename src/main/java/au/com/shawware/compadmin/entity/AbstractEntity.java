/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

/**
 * The base class for all entities.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class AbstractEntity implements Comparable<AbstractEntity>
{
    /** The default ID to use when an entity is yet to be assigned one. */
    public static final int DEFAULT_ID = 0;

    /** The entity's ID. */
    private int mId;

    /**
     * Constructs a new base entity with the default ID.
     */
    protected AbstractEntity()
    {
        mId = DEFAULT_ID;
    }

    /**
     * Constructs a new base entity with the given ID.
     * 
     * @param id the entity's ID
     */
    protected AbstractEntity(int id)
    {
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
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        mId = id;
    }

    @Override
    public int compareTo(AbstractEntity that)
    {
        if (that == null)
        {
            throw new IllegalArgumentException("Null entity");
        }
        return this.mId - that.mId;
    }
}
