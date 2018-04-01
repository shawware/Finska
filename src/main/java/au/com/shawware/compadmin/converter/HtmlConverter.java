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
 * Converts entities to HTML.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "boxing" })
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
    public void convert(Map<Integer, ? extends Entrant> entrants, List<EntrantResult> results, Writer output)
        throws IOException
    {
        HtmlGenerator generator = new HtmlGenerator(output, CSS_CLASS_PREFIX, mCssClassPrefix);
        List<String> itemNames = results.get(0).getItemNames();

        generator.openTag("table", "table");

        generator.openTag("thead");
        generator.openTag("tr", "row");

        generator.openTag("th", "header");
        generator.value("Rank");
        generator.closeTag();

        generator.openTag("th", "header");
        generator.value("Entrant");
        generator.closeTag();

        for (String name : itemNames)
        {
            generator.openTag("th", "header");
            generator.value(name);
            generator.closeTag();
        }

        generator.closeTag();
        generator.closeTag();

        generator.openTag("tbody");
        for (EntrantResult result : results)
        {
            generator.openTag("tr", "row");

            generator.openTag("td", "cell", "numeric");
            generator.value(result.getRank());
            generator.closeTag();

            generator.openTag("td", "cell");
            generator.value(entrants.get(result.getEntrantID()).getName());
            generator.closeTag();

            for (String name : itemNames)
            {
                generator.openTag("td", "cell", "numeric");
                generator.value(result.getResultItemValue(name));
                generator.closeTag();
            }
 
            generator.closeTag();
        }
        generator.closeTag();

        generator.closeTag();
    }
}
