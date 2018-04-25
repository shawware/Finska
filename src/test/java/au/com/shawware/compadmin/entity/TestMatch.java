/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.entity;

import java.time.LocalDate;

import au.com.shawware.util.StringUtil;

/**
 * Models a test match.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class TestMatch extends Match
{
    /** The ID of the first team. */
    private final int mTeam1;
    /** The ID of the second team. */
    private final int mTeam2;
    /** The first team's score. */
    private final int mScore1;
    /** The second team's score. */
    private final int mScore2;

    /**
     * Construct a new test match.
     * 
     * @param id the match's ID
     * @param number the match's number
     * @param matchDate the match's date
     * @param team1 the first team's ID
     * @param team2 the second team's ID
     * @param score1 the first team's score
     * @param score2 the second team's score
     */
    public TestMatch(int id, int number, LocalDate matchDate,
                    int team1, int team2, int score1, int score2)
    {
        super(id, number, matchDate);
        mTeam1  = team1;
        mTeam2  = team2;
        mScore1 = score1;
        mScore2 = score2;
    }

    /**
     * @return The ID of the first team.
     */
    public int getTeam1()
    {
        return mTeam1;
    }

    /**
     * @return The ID of the second team.
     */
    public int getTeam2()
    {
        return mTeam2;
    }

    /**
     * @return The first team's score.
     */
    public int getScore1()
    {
        return mScore1;
    }

    /**
     * @return The second team's score.
     */
    public int getScore2()
    {
        return mScore2;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(getId(), getNumber(), getMatchDate(), mTeam1, mTeam2, mScore1, mScore2);
    }
}
