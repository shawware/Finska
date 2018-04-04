/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * http://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.entity;

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

import au.com.shawware.compadmin.entity.AbstractEntity;
import au.com.shawware.util.StringUtil;

/**
 * Models a Finska competition.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class Competition extends AbstractEntity
{
    /** The competition's name. */
    private final String mName;
    /** The competition start date. */
    private final LocalDate mStartDate;
    /** The matches that make up this competition. */
    private final Map<Integer, Match> mMatches;
    /** The set of match IDs. */
    private final Set<Integer> mMatchIds;

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
        mMatches   = new HashMap<>();
        mMatchIds  = new HashSet<>();
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
     * Adds the given match to this competition.
     * 
     * @param match the match to add
     */
    @SuppressWarnings("boxing")
    public void addMatch(Match match)
    {
        if (match == null) {
            throw new IllegalArgumentException("Null match"); //$NON-NLS-1$
        }
        if (match.getId() == DEFAULT_ID) {
            throw new IllegalArgumentException("Invalid match ID"); //$NON-NLS-1$
        }
        if (mMatches.containsKey(match.getId())) {
            throw new IllegalArgumentException("Competition " + getId() + " already contains match " + match.getId());  //$NON-NLS-1$//$NON-NLS-2$
        }
        // Duplicate is okay.
        mMatchIds.add(match.getId());
        mMatches.put(match.getId(), match);
    }

    /**
     * Retrieve the given match from this competition.
     * 
     * @param id the match's ID
     * 
     * @return The corresponding match.
     * 
     * @throws IllegalArgumentException match cannot be found
     */
    @SuppressWarnings("boxing")
    @JsonIgnore
    public Match getMatch(int id)
        throws IllegalArgumentException
    {
        if (!mMatches.containsKey(id))
        {
            throw new IllegalArgumentException("Match " + id + " is not present in this competition"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return mMatches.get(id);
    }

    /**
     * @return The competition's matches.
     */
    public Set<Integer> getMatchIds()
    {
        return mMatchIds;
    }

    /**
     * Updates this competition's match IDs.
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
        return StringUtil.toString(getId(), mName, mStartDate, mMatchIds);
    }
}
