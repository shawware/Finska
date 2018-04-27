/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;

import au.com.shawware.util.StringUtil;
import au.com.shawware.util.persistence.AbstractEntity;

/**
 * Models a competition round as a set of matches.
 * TODO: consider start date and end date.
 *
 * @param <MatchType> the type of matches we support
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class Round<MatchType extends Match> extends AbstractEntity
{
    /** The round's number (as opposed to ID). */
    private final int mNumber;
    /** The date the round was held. */
    private final LocalDate mRoundDate;
    /** The matches that make up this round. */
    private final Map<Integer, MatchType> mMatches;
    /** The set of match IDs. */
    private final Set<Integer> mMatchIds;

    /**
     * Constructs a new round.
     * 
     * @param id the round's ID
     * @param number the round's number
     * @param roundDate the date the round was held
     */
    public Round(@JsonProperty("id") int id,
                 @JsonProperty("number") int number,
                 @JsonProperty("roundDate") LocalDate roundDate)
     {
        super(id);
        mNumber     = number;
        mRoundDate  = roundDate;
        mMatches    = new HashMap<>();
        mMatchIds   = new HashSet<>();
     }

    /**
     * Constructs a new round.
     * 
     * @param round the round's number
     * @param roundDate the date the round was held
     */
    public Round(int round, LocalDate roundDate)
    {
        this(DEFAULT_ID, round, roundDate);
    }

    /**
     * @return This round's number.
     */
    public int getNumber()
    {
        return mNumber;
    }

    /**
     * @return The date this round was played.
     */
    @JsonDeserialize(using = LocalDateDeserializer.class) 
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getRoundDate()
    {
        return mRoundDate;
    }

    /**
     * Adds the given match to this round.
     * 
     * @param match the match to add
     */
    @SuppressWarnings("boxing")
    public void addMatch(MatchType match)
    {
        if (match == null) {
            throw new IllegalArgumentException("Null match"); //$NON-NLS-1$
        }
        if (match.getId() == DEFAULT_ID) {
            throw new IllegalArgumentException("Invalid match ID"); //$NON-NLS-1$
        }
        if (mMatches.containsKey(match.getId())) {
            throw new IllegalArgumentException("Round " + getId() + " already contains match " + match.getId());  //$NON-NLS-1$//$NON-NLS-2$
        }
        // Duplicate is okay.
        mMatchIds.add(match.getId());
        mMatches.put(match.getId(), match);
    }

    /**
     * Retrieve the given match from this round.
     * 
     * @param id the match's ID
     * 
     * @return The corresponding match.
     * 
     * @throws IllegalArgumentException match cannot be found
     */
    @SuppressWarnings("boxing")
    @JsonIgnore
    public MatchType getMatch(int id)
        throws IllegalArgumentException
    {
        if (!mMatches.containsKey(id))
        {
            throw new IllegalArgumentException("Match " + id + " is not present in this round"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return mMatches.get(id);
    }

    /**
     * @return This round's matches.
     */
    public Set<Integer> getMatchIds()
    {
        return mMatchIds;
    }

    /**
     * Updates this round's match IDs.
     * 
     * @param matchIds the new match IDs
     */
    public void setMatchIds(Set<Integer> matchIds)
    {
        mMatchIds.clear();
        mMatchIds.addAll(matchIds);
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mNumber, mRoundDate, mMatchIds);
    }
}
