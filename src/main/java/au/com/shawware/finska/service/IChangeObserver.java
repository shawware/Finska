/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import au.com.shawware.util.persistence.PersistenceException;

/**
 * Simple observer API to monitor changes to the underlying repository.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public interface IChangeObserver
{
    /**
     * Notify the observer that the repository has been updated.
     * 
     * @throws PersistenceException error accessing repository
     */
    void repositoryUpdated()
        throws PersistenceException;
}
