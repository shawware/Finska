/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import au.com.shawware.finska.persistence.IEntityRepository;

/**
 * Base class for services.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class AbstractService
{
    /** The source for competition data. */
    protected final IEntityRepository mRepository;
    /** The observer to be notified when a service updates the repository. */
    protected final IChangeObserver mObserver;

    /**
     * Constructs a new service.
     * 
     * @param repository the competition data source
     * @param observer observes changes to the repository made by a service
     */
    AbstractService(IEntityRepository repository, IChangeObserver observer)
    {
        mRepository = repository;
        mObserver   = observer;
    }
}
