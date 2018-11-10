/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import au.com.shawware.compadmin.entity.Competition;
import au.com.shawware.compadmin.scoring.EntrantResult;
import au.com.shawware.compadmin.scoring.IResultsCompiler;
import au.com.shawware.compadmin.scoring.LeaderBoardGenerator;
import au.com.shawware.finska.entity.FinskaCompetition;
import au.com.shawware.finska.persistence.IEntityRepository;
import au.com.shawware.finska.scoring.CompetitionAnalyser;
import au.com.shawware.finska.scoring.ResultItem;
import au.com.shawware.finska.scoring.ScoringSystem;
import au.com.shawware.util.persistence.PersistenceException;

/**
 * Provides results-based services.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class ResultsService implements IChangeObserver
{
    /** The source for competition data. */
    private final IEntityRepository mRepository;
    /** The Finska scoring system to use. */
    private final ScoringSystem mScoringSystem;

    /** A comparator for sorting competitions by the start date. */
    private final Comparator<FinskaCompetition> mNaturalSortByStartDate;
    /** A comparator for sorting competitions by the start date in reverse. */
    private final Comparator<FinskaCompetition> mReverseSortByStartDate;

    // Items based on others, created during initialisation.
    /** The full set of competitions. */
    private Map<Integer, FinskaCompetition> mCompetitions;
    /** The current competition (if any) we are processing. */
    private FinskaCompetition mCurrentCompetition;
    /** The results compiler for each competition. */
    private Map<Integer, IResultsCompiler> mCompilers;

    /**
     * Constructs a new service.
     * 
     * @param repository the competition data source
     * @param scoringSystem the scoring system to use
     */
    /*package*/ ResultsService(IEntityRepository repository, ScoringSystem scoringSystem)
    {
        mRepository             = repository;
        mScoringSystem          = scoringSystem;
        mCompilers              = new HashMap<>();
        mNaturalSortByStartDate = Comparator.comparing(Competition::getStartDate, Comparator.naturalOrder());
        mReverseSortByStartDate = Comparator.comparing(Competition::getStartDate, Comparator.reverseOrder());
    }

    @Override
    @SuppressWarnings("boxing")
    public void repositoryUpdated()
        throws PersistenceException
    {
        mCompetitions = mRepository.getCompetitions();
        mCurrentCompetition = getCurrentCompetition(mCompetitions);
        mCompilers.clear();
        mCompetitions.values().forEach(competition -> {
            mCompilers.put(competition.getId(), new CompetitionAnalyser(competition, mScoringSystem));
        });
    }

    /**
     * Calculates the current competition.
     * 
     * @param competitions the full set of competitions
     * 
     * @return The current competition.
     */
    private FinskaCompetition getCurrentCompetition(Map<Integer, FinskaCompetition> competitions)
    {
        FinskaCompetition current;
        if (competitions.size() == 0)
        {
            current = null;
        }
        else if (competitions.size() == 1)
        {
            current = competitions.values().stream().findFirst().get();
        }
        else
        {
            LocalDate today = LocalDate.now();
            Optional<FinskaCompetition> possible;
            possible = competitions.values().stream()
                            .filter(c -> c.getStartDate().isBefore(today))
                            .max(mNaturalSortByStartDate);
            if (possible.isPresent())
            {
                current = possible.get();
            }
            else
            {
                possible = competitions.values().stream().min(mNaturalSortByStartDate);
                current = possible.get();
            }
        }
        return current;
    }

    /**
     * Retrieve the competition data and calculate the latest leader board.
     * 
     * The result is never <code>null</code> but can be empty if there is
     * no data is found.
     * 
     * @return The calculated leader board.
     */
    @SuppressWarnings("boxing")
    public List<EntrantResult> getLeaderBoard()
    {
        List<EntrantResult> leaderBoard;
        if (mCurrentCompetition == null)
        {
            leaderBoard = new ArrayList<>();
        }
        else
        {
            leaderBoard = LeaderBoardGenerator.generateLeaderBoard(mCompilers.get(mCurrentCompetition.getId()));
        }
        return leaderBoard;
    }

    /**
     * Retrieve the competition data and calculate the latest leader board
     * for the given number of rounds.
     * 
     * The result is never <code>null</code> but can be empty if there is
     * no data found.
     * 
     * @param competitionID the competition ID
     * @param rounds the number of rounds
     * 
     * @return The calculated leader board.
     *
     * @throws IllegalArgumentException invalid competition ID or number of rounds 
     */
    @SuppressWarnings("boxing")
    public List<EntrantResult> getLeaderBoard(int competitionID, int rounds)
    {
        if (!mCompetitions.containsKey(competitionID))
        {
            throw new IllegalArgumentException("Competition does not exist: " + competitionID); //$NON-NLS-1$
        }
        return LeaderBoardGenerator.generateLeaderBoard(mCompilers.get(competitionID), rounds);
    }

    /**
     * Retrieve the results and the running total for each round.
     * 
     * @return The results after each round in time sequence.
     */
    @SuppressWarnings("boxing")
    public List<List<EntrantResult>> getRoundResults()
    {
        List<List<EntrantResult>> roundResults;
        if (mCurrentCompetition == null)
        {
            roundResults = new ArrayList<>();
        }
        else
        {
            roundResults = mCompilers.get(mCurrentCompetition.getId()).compileRoundResults();
        }
        return roundResults;
    }

    @SuppressWarnings("boxing")
    public Map<Integer, Number[]> getRankHistory()
    {
        Map<Integer, Number[]> history;
        if (mCurrentCompetition == null)
        {
            history = new HashMap<>();
        }
        else
        {
            history = LeaderBoardGenerator.generateRankHistory(mCompilers.get(mCurrentCompetition.getId()), mCurrentCompetition.numberOfRounds());
        }
        return history;
    }

    @SuppressWarnings("boxing")
    public Map<Integer, Number[]> getResultHistory()
    {
        Map<Integer, Number[]> history;
        if (mCurrentCompetition == null)
        {
            history = new HashMap<>();
        }
        else
        {
            history = LeaderBoardGenerator.generateResultHistory(mCompilers.get(mCurrentCompetition.getId()), mCurrentCompetition.numberOfRounds(), ResultItem.POINTS.toString());
        }
        return history;
    }

    /**
     * All of the competitions.
     * 
     * @return The competitions .
     */
    public List<FinskaCompetition> getCompetitions()
    {
        return mCompetitions.values().stream()
                .sorted(mReverseSortByStartDate)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the specified competition.
     * 
     * @param id the competition ID
     * 
     * @return The specified competition or null if there is none.
     */
    @SuppressWarnings("boxing")
    public FinskaCompetition getCompetition(int id)
    {
        return mCompetitions.get(id);
    }

    /**
     * Retrieves the current competition.
     * 
     * @return The current competition or null if there is none.
     */
    public FinskaCompetition getCurrentCompetition()
    {
        return mCurrentCompetition;
    }
}
