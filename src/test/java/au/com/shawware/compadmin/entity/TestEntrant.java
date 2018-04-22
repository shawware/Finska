/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

/**
 * Models a test entrant.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestEntrant extends Entrant
{
    /**
     * Constructs a new entrant.
     * 
     * @param id the entrant's ID
     * @param name the entrant's name
     */
    public TestEntrant(int id, String name)
    {
        super(id, name);
    }
}
