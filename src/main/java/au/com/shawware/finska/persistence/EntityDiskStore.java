/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import au.com.shawware.compadmin.entity.AbstractEntity;
import au.com.shawware.util.StringUtil;

/**
 * Persists entities to disk as JSON.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 * 
 * @param <EntityType> the entity to be persisted
 */
public class EntityDiskStore<EntityType extends AbstractEntity> implements IEntityStore<EntityType>
{
    /** The file extension to use for entities. */
    private static final String JSON_EXTENSION = ".json"; //$NON-NLS-1$
    /** The length of the extension prefix. */
    private static final int EXTENSION_LENGTH = JSON_EXTENSION.length();
    /** The separator between the prefix and the ID. */
    private static final String ID_SEPARATOR = "-"; //$NON-NLS-1$
    /** The format to use for the IDs. */
    private static final String ID_FORMAT = "%02d"; //$NON-NLS-1$

    /** The directory where entities are to be stored. */
    private final String mDirectory;
    /** The entity's name - mostly for error messages. */
    private final String mEntityName;
    /** The file prefix to use when storing an entity. */
    private final String mPrefix;
    /** The length of the file prefix. */
    private final int mPrefixLength;
    /** The JSON writer to use. */
    private final ObjectWriter mWriter;
    /** The JSON reader to use. */
    private final ObjectReader mReader;

    /**
     * Constructs a new data store for the given class.
     * 
     * @param directory the directory to store entities in
     * @param name the name of the entity
     * @param prefix the prefix to use for the entity
     * @param clazz the entity's class
     */
    public EntityDiskStore(String directory, String name, String prefix, Class<EntityType> clazz)
    {
        if (StringUtil.isEmpty(directory) ||
            StringUtil.isEmpty(name) ||
            StringUtil.isEmpty(prefix))
        {
            throw new IllegalArgumentException("Empty parameter"); //$NON-NLS-1$
        }
        mDirectory = directory;
        mEntityName = name;
        mPrefix = prefix + ID_SEPARATOR;
        mPrefixLength = mPrefix.length();
        ObjectMapper mapper = new ObjectMapper();
        mWriter = mapper.writerWithDefaultPrettyPrinter();
        mReader = mapper.readerFor(clazz);
    }

    @Override
    public Map<Integer, EntityType> getAll()
        throws PersistenceException
    {
        try
        {
            return getIdStream()
                    .mapToObj(id -> getForStream(id))
                    .collect(Collectors.toMap(AbstractEntity::getId, Function.identity()));
        }
        catch (IOException | UncheckedPersistenceException e)
        {
            throw new PersistenceException("Error retrieving every " + mEntityName, e); //$NON-NLS-1$
        }
    }

    @Override
    public EntityType create(EntityType entity)
        throws PersistenceException
    {
        try
        {
            int maxID = getIdStream().max().orElse(0);
            entity.setId(maxID + 1);
            mWriter.writeValue(getFile(entity.getId()), entity);
            return entity;
        }
        catch (IOException e)
        {
            throw new PersistenceException("Error creating " + mEntityName, e); //$NON-NLS-1$
        }
    }

    @Override
    public EntityType get(int id)
        throws PersistenceException
    {
        File file = getFile(id);
        return get(id, file);
    }

    @Override
    public void update(EntityType entity)
        throws PersistenceException
    {
        try
        {
            File file = getFile(entity.getId());
            get(entity.getId(), file);
            mWriter.writeValue(file, entity);
        }
        catch (IOException e)
        {
            throw new PersistenceException("Error updating " + mEntityName + ": " + entity.getId(), e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    @Override
    public void delete(int id)
        throws PersistenceException
    {
        File file = getFile(id);
        file.delete();
    }

    /**
     * Builds a stream of entity IDs from the store.
     * 
     * @return The stream of entity IDs.
     * 
     * @throws IOException error reading store
     */
    private IntStream getIdStream()
        throws IOException
    {
        Path path = Paths.get(mDirectory);
        return Files.list(path)
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(name -> name.startsWith(mPrefix) && name.endsWith(JSON_EXTENSION))
                .mapToInt(name -> Integer.parseInt(name.substring(mPrefixLength, name.length() - EXTENSION_LENGTH)));
    }

    /**
     * Loads the entity with the given ID for streams, ie. with no checked exception.
     * 
     * @param id the entity's ID
     * 
     * @return The corresponding entity.
     *
     * @throws UncheckedPersistenceException error reading the file
     */
    private EntityType getForStream(int id)
        throws UncheckedPersistenceException
    {
        try
        {
            return get(id);
        }
        catch (PersistenceException e)
        {
            throw new UncheckedPersistenceException("Error accessing " + mEntityName + ": " + id, e.getCause()); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Loads the entity with the given ID from the given file.
     * 
     * @param id the entity's ID
     * @param file the file to load from
     * 
     * @return The corresponding entity.
     *
     * @throws PersistenceException error reading the file
     */
    private EntityType get(int id, File file)
        throws PersistenceException
    {
        try
        {
            return mReader.readValue(file);
        }
        catch (IOException e)
        {
            throw new PersistenceException("Error accessing " + mEntityName + ": " + id, e); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Calculates the corresponding data file for the given ID.
     *
     * @param id the ID
     *
     * @return The corresponding file.
     */
    @SuppressWarnings("boxing")
    private File getFile(int id)
    {
        if (id < 0)
        {
            throw new IllegalArgumentException("Invalid ID:"  + id); //$NON-NLS-1$
        }
        return new File(mDirectory, mPrefix + String.format(ID_FORMAT, id) + JSON_EXTENSION);
    }

    @Override
    public String toString()
    {
        return StringUtil.toString(mEntityName, mDirectory);
    }
}
