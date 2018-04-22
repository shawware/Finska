/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import java.time.LocalDate;

/**
 * Models a test round.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestRound extends Round<TestMatch>
{
    /**
     * Construct a new test round.
     * 
     * @param id the round's ID
     * @param number the round's number
     * @param roundDate the round's date
     */
    public TestRound(int id, int number, LocalDate roundDate)
    {
        super(id, number, roundDate);
    }
}
