package com.bank.util;

import com.bank.service.InterestService;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.SQLException;

public class InterestScheduler {
    public static void start() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    new InterestService().calculateMonthlyInterest();
                } catch (SQLException e) {
                    System.err.println("Interest calculation failed: " + e.getMessage());
                }
            }
        }, 0, 24 * 60 * 60 * 1000); // Run daily
    }
}