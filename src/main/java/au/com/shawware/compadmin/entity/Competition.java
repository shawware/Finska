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
 * Models a competition.
 * 
 * @param <RoundType> the type of rounds we support
 * @param <MatchType> the type of matches we support
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class Competition<RoundType extends Round<MatchType>, MatchType extends Match> extends AbstractEntity
{
    /** The competition's name. */
    private final String mName;
    /** The competition start date. */
    private final LocalDate mStartDate;
    /** The rounds that make up this competition. */
    private final Map<Integer, RoundType> mRounds;
    /** The set of round IDs. */
    private final Set<Integer> mRoundIds;

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
        mName      = name;
        mStartDate = startDate;
        mRounds    = new HashMap<>();
        mRoundIds  = new HashSet<>();
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

    /**
     * Adds the given round to this competition.
     * 
     * @param round the round to add
     */
    @SuppressWarnings("boxing")
    public void addRound(RoundType round)
    {
        if (round == null) {
            throw new IllegalArgumentException("Null round"); //$NON-NLS-1$
        }
        if (round.getId() == DEFAULT_ID) {
            throw new IllegalArgumentException("Invalid round ID"); //$NON-NLS-1$
        }
        if (mRounds.containsKey(round.getId())) {
            throw new IllegalArgumentException("Competition " + getId() + " already contains round " + round.getId());  //$NON-NLS-1$//$NON-NLS-2$
        }
        // Duplicate is okay.
        mRoundIds.add(round.getId());
        mRounds.put(round.getId(), round);
    }

    /**
     * @return The number of rounds in this competition so far.
     */
    public int numberOfRounds()
    {
        return mRoundIds.size();
    }

    /**
     * Retrieve the given round from this competition.
     * 
     * @param id the round's ID
     * 
     * @return The corresponding round.
     * 
     * @throws IllegalArgumentException round cannot be found
     */
    @SuppressWarnings("boxing")
    @JsonIgnore
    public RoundType getRound(int id)
        throws IllegalArgumentException
    {
        if (!mRounds.containsKey(id))
        {
            throw new IllegalArgumentException("Round " + id + " is not present in this competition"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return mRounds.get(id);
    }

    /**
     * @return The competition's round IDs.
     */
    public Set<Integer> getRoundIds()
    {
        return mRoundIds;
    }

    /**
     * Updates this competition's round IDs.
     * 
     * @param roundIds the new round IDs
     */
    public void setRoundIds(Set<Integer> roundIds)
    {
        mRoundIds.clear();
        mRoundIds.addAll(roundIds);
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), mName, mStartDate, mRoundIds);
    }
}
