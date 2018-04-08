/*
 * Copyright (C) 2018 shawware.com.au
 *
 * License: GNU General Public License V3 (or later)
 * https://www.gnu.org/copyleft/gpl.html
 */

package au.com.shawware.compadmin.scoring;

import org.junit.Assert;
import org.junit.Test;

import au.com.shawware.util.test.AbstractUnitTest;

/**
 * Exercises and verifies {@link ResultSpec}.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({"nls", "boxing", "static-method" })
public class ResultSpecUnitTest extends AbstractUnitTest
{
    /**
     * Verify the management of a result specification.
     */
    @Test
    public void testManagement()
    {
        ResultSpec spec = new ResultSpec();

        Assert.assertEquals(0, spec.getItemNames().size());

        spec.addItem("alpha");
        Assert.assertEquals(1, spec.getItemNames().size());
        Assert.assertEquals(true, spec.isInteger("alpha"));
        Assert.assertEquals(false, spec.isFloatingPoint("alpha"));

        spec.addItem("gamma", false);
        Assert.assertEquals(2, spec.getItemNames().size());
        Assert.assertEquals(false, spec.isInteger("gamma"));
        Assert.assertEquals(true, spec.isFloatingPoint("gamma"));

        spec.addItem("beta", true);
        Assert.assertEquals(3, spec.getItemNames().size());
        Assert.assertEquals(true, spec.isInteger("beta"));
        Assert.assertEquals(false, spec.isFloatingPoint("beta"));

        String[] expectedNames = { "alpha", "gamma", "beta" };
        int i = 0;
        for (String actualName : spec.getItemNames())
        {
            Assert.assertEquals(expectedNames[i], actualName);
            i++;
        }
    }

    /**
     * Verify the error handling.
     */
    @Test
    public void testValidation()
    {
        ResultSpec spec = new ResultSpec();

        verifyExceptionThrown(() -> spec.isInteger(null),       IllegalArgumentException.class, "Empty item name");
        verifyExceptionThrown(() -> spec.isInteger(""),         IllegalArgumentException.class, "Empty item name");

        verifyExceptionThrown(() -> spec.isFloatingPoint(null), IllegalArgumentException.class, "Empty item name");
        verifyExceptionThrown(() -> spec.isFloatingPoint(""),   IllegalArgumentException.class, "Empty item name");

        verifyExceptionThrown(() -> spec.addItem(null),         IllegalArgumentException.class, "Empty item name");
        verifyExceptionThrown(() -> spec.addItem(""),           IllegalArgumentException.class, "Empty item name");
        verifyExceptionThrown(() -> spec.addItem(null, true),   IllegalArgumentException.class, "Empty item name");
        verifyExceptionThrown(() -> spec.addItem("",   true),   IllegalArgumentException.class, "Empty item name");
        verifyExceptionThrown(() -> spec.addItem(null, false),  IllegalArgumentException.class, "Empty item name");
        verifyExceptionThrown(() -> spec.addItem("",   false),  IllegalArgumentException.class, "Empty item name");

        spec.addItem("a");
        verifyExceptionThrown(() -> spec.addItem("a"),          IllegalArgumentException.class, "Item already present: a");
        verifyExceptionThrown(() -> spec.addItem("a", true),    IllegalArgumentException.class, "Item already present: a");
        verifyExceptionThrown(() -> spec.addItem("a", false),   IllegalArgumentException.class, "Item already present: a");

        verifyExceptionThrown(() -> spec.isInteger("b"),        IllegalArgumentException.class, "Unknown item name: b");
        verifyExceptionThrown(() -> spec.isFloatingPoint("b"),  IllegalArgumentException.class, "Unknown item name: b");
    }
}
