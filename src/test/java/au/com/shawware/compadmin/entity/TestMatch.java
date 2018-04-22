/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import java.time.LocalDate;

/**
 * Models a test match.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestMatch extends Match
{
    /**
     * Construct a new test match.
     * 
     * @param id the match's ID
     * @param number the match's number
     * @param matchDate the match's date
     */
    public TestMatch(int id, int number, LocalDate matchDate)
    {
        super(id, number, matchDate);
    }
}
