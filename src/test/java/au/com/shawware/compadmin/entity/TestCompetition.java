/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import java.time.LocalDate;

/**
 * Model a test competition.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestCompetition extends Competition<TestRound, TestMatch>
{
    /**
     * Constructs a test competition.
     * 
     * @param id the competition's ID
     * @param name the competition's name
     * @param startDate the competition's start date
     */
    public TestCompetition(int id, String name, LocalDate startDate)
    {
        super(id, name, startDate);
    }
}
