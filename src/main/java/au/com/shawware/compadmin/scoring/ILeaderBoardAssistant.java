/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import java.util.Comparator;
import java.util.List;

/**
 * Specifies what a competition must provide to help created a leader board.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public interface ILeaderBoardAssistant extends Comparator<EntrantResult>
{
    /**
     * Compile the results for all entrants.
     * 
     * @return The set of results for all entrants.
     */
    List<EntrantResult> compileResults();
}
