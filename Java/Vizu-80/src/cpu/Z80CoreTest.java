package src.cpu;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * The z80 Core Test Class -- Used to test all of the functionality of the CPU, and ensure inputs
 * and outputs of the class are correct
 *
 * @author  Brendan Lesniak
 */
public class Z80CoreTest
{
    /**
     * Default constructor for test class Z80CoreTest
     */
    public Z80CoreTest()
    {
    }

    /**
     * Sets up the test fixture.
     *
     * Called before every test case method.
     */
    @Before
    public void setUp()
    {
        System.out.println("Starting unit test!");
    }

    /**
     * Tears down the test fixture.
     *
     * Called after every test case method.
     */
    @After
    public void tearDown()
    {
        System.out.println("Ending unit test!");
    }

    @Test
    public void testCPUReset()
    {
        src.cpu.Z80Core z80Core1 = new src.cpu.Z80Core();
        z80Core1.resetCPU();
    }
}

