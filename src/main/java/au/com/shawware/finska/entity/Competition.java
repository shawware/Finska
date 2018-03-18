/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

/**
 * Models a Finska competition
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Competition extends AbstractEntity
{
    /** The competition's name. */
    private final String mName;
    /** The competition start date. */
    private final LocalDate mStartDate;

    /**
     * Constructs a new, identified competition.
     * 
     * @param id the competition's ID
     * @param name the competition's name
     * @param startDate the competition's start date
     */
    public Competition(@JsonProperty("id") int id,
                       @JsonProperty("name") String name,
                       @JsonProperty("startDate") LocalDate startDate)
    {
        super(id);
        mName = name;
        mStartDate = startDate;
    }

    /**
     * Constructs a new, unidentified competition.
     * 
     * @param name the competition's name
     * @param startDate the competition's start date
     */
    public Competition(String name, LocalDate startDate)
    {
        this(DEFAULT_ID, name, startDate);
    }

    /**
     * @return The competition's name.
     */
    public String getName()
    {
        return mName;
    }

    /**
     * @return The competition's start date.
     */
    @JsonDeserialize(using = LocalDateDeserializer.class) 
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getStartDate()
    {
        return mStartDate;
    }
}
