package model;

import java.time.LocalDate;

public class BasicMessage implements MessageDisplay {
    private String day;
    private String date;
    private String stateName;

    public BasicMessage(String stateName) {
        this.stateName = stateName;
        this.day = LocalDate.now().getDayOfWeek().toString();
        this.date = LocalDate.now().toString();
    }

    @Override
    public String getMessage() {
        return "Day: " + day + ", Date: " + date + ", State: " + stateName;
    }
}