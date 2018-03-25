/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.converter;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import au.com.shawware.compadmin.entity.Entrant;
import au.com.shawware.compadmin.scoring.EntrantResult;

/**
 * Specifies the conversion API.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
public interface IConverter
{
    /**
     * Converts the given set of entrants and their results to the given output stream.
     * 
     * @param entrants the entrants
     * @param results the entrants' results
     * @param output the output stream
     * 
     * @throws IOException output stream error
     */
    public void convert(Map<Integer, ? extends Entrant> entrants, List<EntrantResult> results, Writer output)
        throws IOException;
}
