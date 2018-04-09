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
import au.com.shawware.compadmin.scoring.ResultSpec;

/**
 * Converts entities to HTML.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "boxing", "static-method" })
public class HtmlConverter implements IConverter
{
    /** The base CSS class prefix to use for all CSS classes. */
    private static final String CSS_CLASS_PREFIX = "ca";
    /** The CSS class prefix to use for this instance. */
    private final String mCssClassPrefix;

    /**
     * Constructs a new converter.
     * 
     * @param cssClassPrefix the CSS prefix to use in class names
     */
    public HtmlConverter(String cssClassPrefix)
    {
        mCssClassPrefix = cssClassPrefix;
    }

    @Override
    public void convertOverallResults(Map<Integer, ? extends Entrant> entrants, List<EntrantResult> results, Writer output)
        throws IOException
    {
        HtmlGenerator generator = new HtmlGenerator(output, CSS_CLASS_PREFIX, mCssClassPrefix);
        generateHtml(entrants, results, generator, true);

    }

    @Override
    public void convertRoundResults(Map<Integer, ? extends Entrant> entrants, List<List<EntrantResult>> results, Writer output)
            throws IOException
    {
        HtmlGenerator generator = new HtmlGenerator(output, CSS_CLASS_PREFIX, mCssClassPrefix);
        for (List<EntrantResult> result : results)
        {
            generateHtml(entrants, result, generator, false);
            generator.openTag("p");
            generator.closeTag();
        }
    }

    /**
     * Generates the HTML for the given results.
     * 
     * @param entrants the entrants who achieved the results
     * @param results the results to convert
     * @param generator the HTML generator to use
     * @param displayRank whether to include the rank in the output
     * 
     * @throws IOException output error
     */
    private void generateHtml(Map<Integer, ? extends Entrant> entrants, List<EntrantResult> results, HtmlGenerator generator, boolean displayRank)
        throws IOException
    {
        ResultSpec spec = results.get(0).getResultSpecification();

        generator.openTag("table", "table");

        generator.openTag("thead", "table-head");
        generator.openTag("tr", "row");

        if (displayRank)
        {
            generator.openTag("th", "header");
            generator.value("Rank");
            generator.closeTag();
        }

        generator.openTag("th", "header");
        generator.value("Entrant");
        generator.closeTag();

        for (String name : spec.getItemNames())
        {
            generator.openTag("th", "header");
            generator.value(name);
            generator.closeTag();
        }

        generator.closeTag();
        generator.closeTag();

        generator.openTag("tbody", "table-body");
        for (EntrantResult result : results)
        {
            generator.openTag("tr", "row");

            if (displayRank)
            {
                generator.openTag("td", "cell", "numeric");
                generator.value(result.getRank());
                generator.closeTag();
            }

            generator.openTag("td", "cell");
            generator.value(entrants.get(result.getEntrantID()).getName());
            generator.closeTag();

            for (String name : spec.getItemNames())
            {
                generator.openTag("td", "cell", "numeric");
                if (spec.isInteger(name))
                {
                    generator.value(result.getResultItemValueAsInt(name));
                }
                else
                {
                    generator.value(result.getResultItemValueAsDouble(name));
                }
                generator.closeTag();
            }
 
            generator.closeTag();
        }
        generator.closeTag();

        generator.closeTag();
    }
}
