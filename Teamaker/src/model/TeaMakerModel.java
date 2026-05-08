package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TeaMakerModel implements Subject{
    private TeaState emptyState;
    private TeaState idleState;
    private TeaState makingTeaState;
    private TeaState boilingState;
    private TeaState doneState;
    private TeaState currentState;

    private int currentCups = 0;
    private int totalCupsDaily = 0;
    private int monthlyTotalCups = 0;
    private String statusMessage = "";

    private List<Observer> observers = new ArrayList<Observer>();


    public TeaMakerModel() {
        emptyState = new EmptyState(this);
        idleState = new IdleState(this);
        makingTeaState = new MakingTeaState(this);
        boilingState = new BoilingWaterState(this);
        doneState = new DoneState(this);

        currentState = emptyState;

        calculateMonthlyTotalFromDB();
    }

    public void addObserver(Observer o) {
        observers.add(o);
    }

    public void notifyObservers() {
        for (Observer o : observers) {
            o.update();
        }
    }

    public void setCurrentState(TeaState newState) {
        this.currentState = newState;
        this.currentState.onEnter();
        notifyObservers();
    }

    public void startBrewingProcess() {
        setStatusMessage("Tea is brewing...");
        notifyObservers();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getState().timerExpired();
            }
        }, 3000);
    }

    public void startBoilingProcess() {
        setStatusMessage("Water is boiling...");
        notifyObservers();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                getState().timerExpired();
            }
        }, 3000);
    }


    public TeaState getState() {
        return currentState;
    }

    public TeaState getEmptyState() {
        return emptyState;
    }

    public TeaState getIdleState() {
        return idleState;
    }

    public TeaState getMakingTeaState() {
        return makingTeaState;
    }

    public TeaState getBoilingState() {
        return boilingState;
    }

    public TeaState getDoneState() {
        return doneState;
    }

    public void setCups(int cups) {
        this.currentCups = cups;
    }

    public int getCurrentCups() {
        return currentCups;
    }

    public int getTotalCupsDaily() {
        return totalCupsDaily;
    }

    public void setStatusMessage(String msg) {
        this.statusMessage = msg;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getMonthlyTotalCups() {
        return monthlyTotalCups;
    }

    public void setMonthlyTotalCups(int monthlyTotalCups) {
        this.monthlyTotalCups = monthlyTotalCups;
    }

    public void addToTotalCups(int cups) {
        calculateMonthlyTotalFromDB();
        updateMessage();
    }

    private void updateMessage() {
        MessageDisplay msg = new BasicMessage(currentState.getStateName());
        if (totalCupsDaily >= 10) {
            msg = new HealthWarningDecorator(msg);
        }
        setStatusMessage(msg.getMessage());
        notifyObservers();
    }

    public void calculateMonthlyTotalFromDB() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tea_maker_db", "root", "Muhammed.1907");
            Statement stmt = conn.createStatement();


            String sql = "SELECT SUM(cups) FROM brewing_log WHERE MONTH(log_date) = MONTH(NOW()) AND YEAR(log_date) = YEAR(NOW())";

            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                this.monthlyTotalCups = rs.getInt(1);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("DB reading error: " + e.getMessage());
        }
    }

    public void calculateDailyTotalFromDB() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tea_maker_db", "root", "Muhammed.1907");
            Statement stmt = conn.createStatement();


            String sql = "SELECT SUM(cups) FROM brewing_log WHERE DAY(log_date) = DAY(NOW()) AND MONTH(log_date) = MONTH(NOW()) AND YEAR(log_date) = YEAR(NOW())";

            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                this.totalCupsDaily = rs.getInt(1);
            }
            conn.close();
        } catch (Exception e) {
            System.out.println("DB reading error: " + e.getMessage());
        }
    }

    public void saveToDatabase() {
        try {

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/tea_maker_db", "root", "Muhammed.1907");

            String sql = "INSERT INTO brewing_log (cups, log_date) VALUES (" + currentCups + ", NOW())";

            conn.createStatement().execute(sql);

            conn.close();
            calculateMonthlyTotalFromDB();
            calculateDailyTotalFromDB();
            System.out.println("Saved to database.");

        } catch (Exception e) {

            System.out.println("Database error: " + e.getMessage());
        }
    }
}