/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.shawware.compadmin.entity.Competition;

/**
 * Models a Finska competition.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class FinskaCompetition extends Competition<FinskaRound, FinskaMatch>
{
    /**
     * Constructs a new, identified Finska competition.
     * 
     * @param id the competition's ID
     * @param name the competition's name
     * @param startDate the competition's start date
     */
    public FinskaCompetition(@JsonProperty("id") int id,
                             @JsonProperty("name") String name,
                             @JsonProperty("startDate") LocalDate startDate)
    {
        super(id, name, startDate);
    }

    /**
     * Constructs a new, unidentified competition.
     * 
     * @param name the competition's name
     * @param startDate the competition's start date
     */
    public FinskaCompetition(String name, LocalDate startDate)
    {
        this(DEFAULT_ID, name, startDate);
    }
}
