package com.kirbysmashyeet.simpletimerapplication;

public class Timer {
    private int hour;
    private int minute;
    private int second;
    private volatile boolean running;

    public Timer() {
        this.hour = 0;
        this.minute = 0;
        this.second = 0;
        running = false;
    }

    public void set(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public void start() {
        running = true;

        Thread timerThread = new Thread(() -> {
            while (running && (hour > 0 || minute > 0 || second > 0)) {
                second--;

                if (second < 0) {
                    second = 59;
                    if (minute > 0) {
                        minute--;
                    } else {
                        minute = 59;
                        hour--;
                    }
                }

                if (hour == 0 && minute == 0 && second == 0) {
                    running = false;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        timerThread.start();
    }

    public void stop() {
        running = false;
    }

    public void reset() {
        hour = 0;
        minute = 0;
        second = 0;
        running = false;
    }

    public String getTime() {
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }
}
