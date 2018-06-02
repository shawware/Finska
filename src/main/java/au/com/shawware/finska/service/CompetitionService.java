/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.util.StringUtil;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Provides services for maintaining competitions.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class CompetitionService extends AbstractService
{
    private static final Logger LOG = LoggerFactory.getLogger(CompetitionService.class);

    /**
     * Constructs a new service.
     * 
     * @param repository the competition data source
     * @param observer observes changes to the repository made by this service
     */
    /*package*/ CompetitionService(IEntityRepository repository, IChangeObserver observer)
    {
        super(repository, observer);
    }

    /**
     * Creates a new competition with the given features.
     * 
     * @param name the competition name
     * @param startDate the start date
     * @param playerIds the IDs of the players in the competition
     * 
     * @return The new competition.
     * 
     * @throws PersistenceException storage error
     * @throws IllegalArgumentException empty or invalid argument
     */
    @SuppressWarnings({ "nls" })
    public FinskaCompetition createCompetition(String name, LocalDate startDate, int[] playerIds)
        throws PersistenceException, IllegalArgumentException
    {
        verifyParameters(name, startDate, playerIds);

        FinskaCompetition competition = new FinskaCompetition(name, startDate);
        Arrays.stream(playerIds).forEach(competition::addEntrantId);

        competition = mRepository.createCompetition(competition);

        LOG.info("Created new competition " + competition.getKey());

        mObserver.repositoryUpdated();

        return competition;
    }

    /**
     * Updates the given competition.
     * 
     * @param competitionID the competition ID
     * @param name the competition name
     * @param startDate the start date
     * @param playerIds the IDs of the players in the competition
     * 
     * @return The updated competition.
     * 
     * @throws PersistenceException storage error
     * @throws IllegalArgumentException empty or invalid argument
     */
    @SuppressWarnings({ "nls" })
    public FinskaCompetition updateCompetition(int competitionID, String name, LocalDate startDate, int[] playerIds)
        throws PersistenceException
    {
        verifyParameters(name, startDate, playerIds);

        FinskaCompetition competition = mRepository.getCompetition(competitionID);

        competition.setKey(name);
        competition.setStartDate(startDate);
        competition.setEntrantIds(Collections.emptySet());
        Arrays.stream(playerIds).forEach(competition::addEntrantId);

        mRepository.updateCompetition(competition);
 
        LOG.info("Updated competition " + competition.getKey());

        mObserver.repositoryUpdated();

        return competition;
    }

    /**
     * Verify the given parameters meet the minimum standard.
     * 
     * @param name the competition name
     * @param startDate the competition start date
     * @param playerIds the IDs of the players in the competition
     * 
     * @throws IllegalArgumentException invalid parameter
     */
    @SuppressWarnings({ "nls", "static-method" })
    private void verifyParameters(String name, LocalDate startDate, int[] playerIds)
        throws IllegalArgumentException
    {
        if (StringUtil.isEmpty(name))
        {
            throw new IllegalArgumentException("Empty competition name");
        }
        if (startDate == null)
        {
            throw new IllegalArgumentException("Empty competition start date");
        }
        if ((playerIds == null) || (playerIds.length == 0))
        {
            throw new IllegalArgumentException("Empty player IDs");
        }
    }
}
