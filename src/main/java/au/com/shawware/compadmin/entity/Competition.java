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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
 * @param <EntrantType> the type of entrants we support
 * @param <RoundType> the type of rounds we support
 * @param <MatchType> the type of matches we support
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public abstract class Competition<EntrantType extends Entrant, RoundType extends Round<MatchType>, MatchType extends Match> extends AbstractEntity<String>
{
    /** The competition start date. */
    private LocalDate mStartDate;
    /** The entrants in this competition. */
    private final Map<Integer, EntrantType> mEntrants;
    /** The IDs of the entrants. */
    private final Set<Integer> mEntrantIds;
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
                       @JsonProperty("key") String name,
                       @JsonProperty("startDate") LocalDate startDate)
    {
        super(id, name);
        mStartDate  = startDate;
        mEntrants   = new HashMap<>();
        mEntrantIds = new HashSet<>();
        mRounds     = new HashMap<>();
        mRoundIds   = new HashSet<>();
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
     * @return The competition's start date.
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public LocalDate getStartDate()
    {
        return mStartDate;
    }

    /**
     * Sets this competition's start date.
     * 
     * @param startDate the new start date
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    public void setStartDate(LocalDate startDate)
    {
        mStartDate = startDate;
    }

    /**
     * @return The competition's entrant IDs.
     */
    public Set<Integer> getEntrantIds()
    {
        return mEntrantIds;
    }

    /**
     * Updates this competition's entrant IDs.
     * 
     * @param entrantIds the new entrant IDs
     */
    public void setEntrantIds(Set<Integer> entrantIds)
    {
        mEntrants.clear();
        mEntrantIds.clear();
        mEntrantIds.addAll(entrantIds);
    }

    /**
     * Adds a new entrant to this competition.
     * 
     * @param id the entrant's ID
     */
    @SuppressWarnings("boxing")
    public void addEntrantId(int id)
    {
        mEntrantIds.add(id);
    }

    /**
     * Adds the given entrant to this competition.
     * 
     * @param entrant the entrant to add
     */
    @SuppressWarnings("boxing")
    public void addEntrant(EntrantType entrant)
    {
        if (entrant == null) {
            throw new IllegalArgumentException("Null entrant"); //$NON-NLS-1$
        }
        if (entrant.getId() == DEFAULT_ID) {
            throw new IllegalArgumentException("Invalid entrant ID"); //$NON-NLS-1$
        }
        if (mEntrants.containsKey(entrant.getId())) {
            throw new IllegalArgumentException("Competition " + getId() + " already contains entrant " + entrant.getId());  //$NON-NLS-1$//$NON-NLS-2$
        }
        // Duplicate is okay.
        addEntrantId(entrant.getId());
        mEntrants.put(entrant.getId(), entrant);
    }

    /**
     * This competition's entrants, ordered by name.
     * 
     * @return The entrants.
     */
    @JsonIgnore
    public List<EntrantType> getEntrants()
    {
        return mEntrants.values()
                        .stream()
                        .sorted((p1, p2) -> p1.getKey().compareTo(p2.getKey()))
                        .collect(Collectors.toList());
    }

    /**
     * This competition's entrants.
     * 
     * @return The entrants as a map.
     */
    @JsonIgnore
    public Map<Integer, EntrantType> getEntrantMap()
    {
        return mEntrants;
    }

    /**
     * Retrieve the given entrant from this competition.
     * 
     * @param id the entrant's ID
     * 
     * @return The corresponding entrant.
     * 
     * @throws IllegalArgumentException entrant cannot be found
     */
    @JsonIgnore
    public EntrantType getEntrant(int id)
        throws IllegalArgumentException
    {
        Optional<EntrantType> entrant = mEntrants.values().stream().filter(e -> e.getId() == id).findAny();
        if (!entrant.isPresent())
        {
            throw new IllegalArgumentException("Entrant " + id + " is not present in this competition"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return entrant.get();
    }

    /**
     * Determines whether this competition has the given entrant.
     * 
     * @param id the entrant ID to test for
     * 
     * @return Whether the given entrant exists.
     */
    @SuppressWarnings("boxing")
    public boolean hasEntrant(int id)
    {
        return mEntrantIds.contains(id);
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
    @JsonIgnore
    public int numberOfRounds()
    {
        return mRoundIds.size();
    }

    /**
     * This competition's rounds, ordered by round number (within this competition).
     * 
     * @return The rounds in sequential order.
     */
    @JsonIgnore
    public List<RoundType> getRounds()
    {
        return mRounds.values()
                      .stream()
                      .sorted((r1, r2) -> r1.getKey().compareTo(r2.getKey()))
                      .collect(Collectors.toList());
    }

    /**
     * Retrieve the given round from this competition.
     * 
     * @param number the round's number
     * 
     * @return The corresponding round.
     * 
     * @throws IllegalArgumentException round cannot be found
     */
    @JsonIgnore
    @SuppressWarnings("boxing")
    public RoundType getRound(int number)
        throws IllegalArgumentException
    {
        Optional<RoundType> round = mRounds.values().stream().filter(r -> r.getKey().equals(number)).findAny();
        if (!round.isPresent())
        {
            throw new IllegalArgumentException("Round " + number + " is not present in this competition"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return round.get();
    }

    /**
     * Determines whether this competition has the given round.
     * 
     * @param id the round ID to test for
     * 
     * @return Whether the given round exists.
     */
    @SuppressWarnings("boxing")
    public boolean hasRound(int id)
    {
        return mRoundIds.contains(id);
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
        return StringUtil.toString(getId(), getKey(), mStartDate, mEntrantIds, mRoundIds);
    }
} 