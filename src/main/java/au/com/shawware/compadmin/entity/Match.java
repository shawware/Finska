/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import au.com.shawware.util.StringUtil;
import au.com.shawware.util.persistence.AbstractEntity;

/**
 * Models a single match.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class Match extends AbstractEntity
{
    /** The match's number (as opposed to ID). */
    private final int mNumber;
    /** The date the match was held. */
    private final LocalDate mMatchDate;

    /**
     * Constructs a new round.
     * 
     * @param id the round's ID
     * @param number the match's number
     * @param matchDate the date the match was held
     */
    public Match(@JsonProperty("id") int id,
                 @JsonProperty("number") int number,
                 @JsonProperty("matchDate") LocalDate matchDate)
     {
        super(id);
        mNumber    = number;
        mMatchDate = matchDate;
     }

    /**
     * @return This match's number.
     */
    public int getNumber()
    {
        return mNumber;
    }

    /**
     * @return The date this match was played.
     */
    @JsonDeserialize(using = LocalDateDeserializer.class) 
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getMatchDate()
    {
        return mMatchDate;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mNumber, mMatchDate);
    }
}
