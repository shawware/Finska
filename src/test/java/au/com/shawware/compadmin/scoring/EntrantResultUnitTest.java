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
 * Exercises and verifies {@link EntrantResult}.
 *
 * @author <a href="mailto:david.shaw@shawware.com.au">David Shaw</a>
 */
@SuppressWarnings({ "nls", "static-method" })
public class EntrantResultUnitTest extends AbstractUnitTest
{
    /**
     * Verify entrant result scoring.
     */
    @Test
    public void testResult()
    {
        ResultSpec spec = new ResultSpec();
        spec.addItem("a", true);
        spec.addItem("b", false);
        EntrantResult result = new EntrantResult(1, spec);

        result.incrementResultItem("a", 1);
        Assert.assertEquals(1, result.getResultItemValueAsInt("a"));
        result.incrementResultItem("a", 3);
        Assert.assertEquals(4, result.getResultItemValueAsInt("a"));
        result.incrementResultItem("a", -2);
        Assert.assertEquals(2, result.getResultItemValueAsInt("a"));

        result.setResultItem("b", 3.5);
        Assert.assertEquals(3.5, result.getResultItemValueAsDouble("b"), 0.0001);
        result.setResultItem("b", 12.99);
        Assert.assertEquals(12.99, result.getResultItemValueAsDouble("b"), 0.0001);
    }

    /**
     * Verify the error handling.
     */
    @Test
    public void testVerification()
    {
        verifyExceptionThrown(() -> new EntrantResult(1, null), IllegalArgumentException.class, "Empty result specification");

        ResultSpec spec = new ResultSpec();
        spec.addItem("a", true);
        spec.addItem("b", false);
        EntrantResult result = new EntrantResult(1, spec);

        verifyExceptionThrown(() -> result.incrementResultItem("c", 1), IllegalArgumentException.class, "Unknown result item: c");
        verifyExceptionThrown(() -> result.incrementResultItem("b", 1), IllegalArgumentException.class, "Non integer result item: b");

        verifyExceptionThrown(() -> result.setResultItem("c", 1.0), IllegalArgumentException.class, "Unknown result item: c");
        verifyExceptionThrown(() -> result.setResultItem("a", 1.0), IllegalArgumentException.class, "Non floating point result item: a");

        verifyExceptionThrown(() -> result.getResultItemValueAsInt("c"), IllegalArgumentException.class, "Unknown result item: c");
        verifyExceptionThrown(() -> result.getResultItemValueAsInt("b"), IllegalArgumentException.class, "Non integer result item: b");

        verifyExceptionThrown(() -> result.getResultItemValueAsDouble("c"), IllegalArgumentException.class, "Unknown result item: c");
        verifyExceptionThrown(() -> result.getResultItemValueAsDouble("a"), IllegalArgumentException.class, "Non floating point result item: a");
    }
}
