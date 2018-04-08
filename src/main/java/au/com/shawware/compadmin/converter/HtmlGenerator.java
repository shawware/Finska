/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.converter;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringJoiner;

/**
 * HTML utility class.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls" })
public class HtmlGenerator
{
    /** The base CSS class prefix to use for all CSS classes. */
    private static final String CSS_CLASS_PREFIX = "sw"; //$NON-NLS-1$
    /** The base CSS class prefix to use for all CSS classes. */
    private static final String CSS_CLASS_NAME_SEPARATOR = "-"; //$NON-NLS-1$
    /** The writer to generate HTML to. */
    private final Writer mWriter;
    /** The CSS class prefix to use. */
    private final String mCssClassPrefix;
    /** Track the nesting of tags. */
    private final Deque<String> mTagStack;

    /**
     * Creates a new HTML generator.
     * 
     * @param writer where to output the HTML
     * @param cssClassPrefixes the set of CSS class prefixes
     */
    public HtmlGenerator(Writer writer, String... cssClassPrefixes)
    {
        // TODO: arg checks
        StringJoiner sj = new StringJoiner(CSS_CLASS_NAME_SEPARATOR);
        for (String prefix : cssClassPrefixes) {
            sj.add(prefix);
        }
        mWriter         = writer;
        mCssClassPrefix = CSS_CLASS_PREFIX + CSS_CLASS_NAME_SEPARATOR + sj.toString() + CSS_CLASS_NAME_SEPARATOR;
        mTagStack       = new ArrayDeque<>();
    }

    /**
     * Outputs a new opening tag.
     * 
     * @param tag the tag name
     * 
     * @throws IOException output error
     */
    public void openTag(String tag)
        throws IOException
    {
        mWriter.write(indent());
        mWriter.write('<');
        mWriter.write(tag);
        mWriter.write(">\n");
        mTagStack.push(tag);
    }

    /**
     * Outputs a new opening tag.
     * 
     * @param tag the tag name
     * @param clazz the CSS class
     * @param classes optional additional CSS classes
     * 
     * @throws IOException output error
     */
    public void openTag(String tag, String clazz, String... classes)
        throws IOException
    {
        mWriter.write(indent());
        mWriter.write('<');
        mWriter.write(tag);
        mWriter.write(" class=\"");
        mWriter.write(mCssClassPrefix + clazz);
        for (String c : classes)
        {
            mWriter.write(" " + mCssClassPrefix + c);
        }
        mWriter.write("\">\n");
        mTagStack.push(tag);
    }

    /**
     * Outputs a new value.
     * 
     * @param value the value
     * 
     * @throws IOException output error
     */
    public void value(int value)
        throws IOException
    {
        value(String.valueOf(value));
    }

    /**
     * Outputs a new value.
     * 
     * @param value the value
     * 
     * @throws IOException output error
     */
    public void value(String value)
        throws IOException
    {
        mWriter.write(indent());
        mWriter.write(value);
        mWriter.write("\n");
    }

    /**
     * Outputs a closing tag to match the current open tag.
     * 
     * @throws IOException output error
     */
    public void closeTag()
        throws IOException
    {
        String tag = mTagStack.pop();
        mWriter.write(indent());
        mWriter.write("</");
        mWriter.write(tag);
        mWriter.write(">\n");
    }

    /**
     * Calculate the indent based on the number of open tags.
     * 
     * @return The indent to use.
     */
    private String indent()
    {
        String indent = "";
        for (int i=0; i<mTagStack.size(); i++)
        {
            indent += "  ";
        }
        return indent;
    }
}
