/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.persistence;

import au.com.shawware.util.SwException;

/**
 * Thrown when the persistence layer encounters an error.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class PersistenceException extends SwException
{
    /** The ID for serialisation. */
    private static final long serialVersionUID = -7730452928436012624L;

    /**
     * Creates a new exception.
     * 
     * @param message the error message
     */
    public PersistenceException(String message)
    {
        super(message);
    }

    /**
     * Creates a new exception.
     * 
     * @param message the error message
     * @param cause the cause of the error
     */
    public PersistenceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
