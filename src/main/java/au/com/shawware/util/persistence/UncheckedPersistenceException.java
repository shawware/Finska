/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.util.persistence;

/**
 * Unchecked exception for persistence errors.
 * Exists to supports Java streams, which struggle with checked exceptions.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class UncheckedPersistenceException extends RuntimeException
{
    /** The ID for serialisation. */
    private static final long serialVersionUID = -4698625682598799065L;

    /**
     * Creates a new exception.
     * 
     * @param message the error message
     */
    public UncheckedPersistenceException(String message)
    {
        super(message);
    }

    /**
     * Creates a new exception.
     * 
     * @param message the error message
     * @param cause the cause of the error
     */
    public UncheckedPersistenceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
