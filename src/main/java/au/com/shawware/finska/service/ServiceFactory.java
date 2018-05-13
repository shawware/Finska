/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.util.HashMap;
import java.util.Map;

import au.com.shawware.finska.persistence.EntityRepository;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.util.persistence.PersistenceException;
import au.com.shawware.util.persistence.PersistenceFactory;

/**
 * Factory for building services.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class ServiceFactory
{
    private static Map<String, ServiceFactory> sInstances;
    /** The wrapped results service. */
    private final ResultsService mResultsService;
    /** The wrapped round service. */
    private final RoundService mRoundService;
    /** The wrapped match service. */
    private final MatchService mMatchService;

    /**
     * Constructs a new service factory.
     * 
     * @param repository the competition data source
     * @param scoringSystem the scoring system to use
     * 
     * @throws PersistenceException error during initialisation
     */
    private ServiceFactory(IEntityRepository repository, ScoringSystem scoringSystem)
        throws PersistenceException
    {
        mResultsService = new ResultsService(repository, scoringSystem);
        mRoundService   = new RoundService(repository, mResultsService);
        mMatchService   = new MatchService(repository, mResultsService);
        mResultsService.repositoryUpdated();
    }

    /**
     * Constructs a new service factory for the given persistence layer and scoring system.
     * 
     * @param factory the persistence factory
     * @param scoringSystem the scoring system
     * 
     * @return The service factory
     * 
     * @throws PersistenceException error during initialisation
     */
    public static synchronized ServiceFactory getFactory(PersistenceFactory factory, ScoringSystem scoringSystem)
        throws PersistenceException
    {
        if (sInstances == null)
        {
            sInstances = new HashMap<>();
        }
        if (!sInstances.containsKey(factory.getRoot()))
        {
            IEntityRepository repository = EntityRepository.getRepository(factory);
            sInstances.put(factory.getRoot(), new ServiceFactory(repository, scoringSystem));
        }
        return sInstances.get(factory.getRoot());
    }

    /**
     * @return The results service.
     */
    public ResultsService getResultsService()
    {
        return mResultsService;
    }

    /**
     * @return The round service.
     */
    public RoundService getRoundService()
    {
        return mRoundService;
    }

    /**
     * @return The match service.
     */
    public MatchService getMatchService()
    {
        return mMatchService;
    }
}
