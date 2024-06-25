package com.kirbysmashyeet.simpletimerapplication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class TimerTest {
    Timer timer;

    @BeforeEach
    public void setup() {
        timer = new Timer();
    }

    @Test
    public void testSet() {
        timer.set(1, 30, 15);
        assertEquals("01:30:15", timer.getTime());
    }

    @Test
    public void testStart() throws InterruptedException {
        timer.set(0, 0, 3); // Set timer for 3 seconds

        Thread timerThread = new Thread(timer::start);
        timerThread.start();

        Thread.sleep(4000); // Wait for 4 seconds

        assertFalse(timerThread.isAlive());
        assertEquals("00:00:00", timer.getTime());
    }

    @Test
    public void testStop() throws InterruptedException {
        timer.set(0, 1, 0); // Set timer for 1 minute

        Thread timerThread = new Thread(timer::start);
        timerThread.start();

        Thread.sleep(2000); // Let the timer run for 2 seconds

        timer.stop();
        timerThread.join(); // Wait for the timer thread to stop

        assertTrue(timer.getTime().startsWith("00:00"));
    }

    @Test
    public void testReset() {
        timer.set(1, 30, 15);
        timer.reset();
        assertEquals("00:00:00", timer.getTime());
    }
}
