/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.finska.scoring;

import au.com.shawware.util.StringUtil;

/**
 * Specifies the Finska scoring system in place.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public class ScoringSystem
{
    /** The number of points for a win. */
    private final int mPointsForWin;
    /** Whether to score points for playing a match. */
    private final boolean mScorePlaying;
    /** The number of points for playing a match. */
    private final int mPointsForPlaying;
    /** Whether to score fast wins in games. */
    private final boolean mScoreFastWins;
    /** The number of points for a fast win in a game. */
    private final int mPointsForFastWin;
    /** Whether to score win both. */
    private final boolean mScoreWinBoth;
    /** The number of points for the first two games in a match. */
    private final int mPointsForWinBoth;
    /** Whether to score win all. */
    private final boolean mScoreWinAll;
    /** The number of points for winning all games in a match. */
    private final int mPointsForWinAll;

    /**
     * Specifies a Finska scoring system. Points for playing, fast wins, win
     * both and win all can be turned on by specifying a positive value.
     * Note: you cannot specify win both and win all simultaneously.
     * 
     * @param win the number of points for a win
     * @param play if &gt;0, then score these points for playing a match
     * @param fast if &gt;0, then score these points for a fast win in a game
     * @param both if &gt;0 then score these points for winning the first two games in match
     * @param all if &gt;0, then score these points for winning all games in a match
     * 
     * @throws IllegalArgumentException win both and win all specified simultaneously
     */
    public ScoringSystem(int win, int play, int fast, int both, int all)
        throws IllegalArgumentException
    {
        if ((both > 0) && (all > 0))
        {
            throw new IllegalArgumentException("Win both and win all specified simultaneously"); //$NON-NLS-1$
        }
        mPointsForWin = win;
        if (play > 0)
        {
            mScorePlaying     = true;
            mPointsForPlaying = play;
        }
        else
        {
            mScorePlaying     = false;
            mPointsForPlaying = 0;
        }
        if (fast > 0)
        {
            mScoreFastWins    = true;
            mPointsForFastWin = fast;
        }
        else
        {
            mScoreFastWins    = false;
            mPointsForFastWin = 0;
        }
        if (both > 0)
        {
            mScoreWinBoth     = true;
            mPointsForWinBoth = both;
        }
        else
        {
            mScoreWinBoth     = false;
            mPointsForWinBoth = 0;
        }
        if (all > 0)
        {
            mScoreWinAll     = true;
            mPointsForWinAll = all;
        }
        else
        {
            mScoreWinAll     = false;
            mPointsForWinAll = 0;
        }
    }

    /**
     * @return The number of points for a win.
     */
    public int pointsForWin()
    {
        return mPointsForWin;
    }

    /**
     * @return Whether to score points for playing.
     */
    public boolean scorePointsForPlaying()
    {
        return mScorePlaying;
    }

    /**
     * @return The number of points for playing a match.
     */
    public int pointsForPlaying()
    {
        return mPointsForPlaying;
    }

    /**
     * @return Whether to score fast wins in a game.
     */
    public boolean scoreFastWins()
    {
        return mScoreFastWins;
    }

    /**
     * @return The number of points for a fast win.
     */
    public int pointsForFastWin()
    {
        return mPointsForFastWin;
    }

    /**
     * @return Whether to score winning the first two games in a match.
     */
    public boolean scoreWinBoth()
    {
        return mScoreWinBoth;
    }

    /**
     * @return The number of points for winning the first two games in a match.
     */
    public int pointsForWinBoth()
    {
        return mPointsForWinBoth;
    }

    /**
     * @return Whether to score winning all games in a match.
     */
    public boolean scoreWinAll()
    {
        return mScoreWinAll;
    }

    /**
     * @return The number of points for winning all games in a match.
     */
    public int pointsForWinAll()
    {
        return mPointsForWinAll;
    }

    @Override
    @SuppressWarnings("boxing")
    public String toString()
    {
        return StringUtil.toString(mPointsForWin,
                mScorePlaying, mPointsForPlaying,
                mScoreFastWins, mPointsForFastWin,
                mScoreWinBoth, mPointsForWinBoth,
                mScoreWinAll, mPointsForWinAll);
    }
}
