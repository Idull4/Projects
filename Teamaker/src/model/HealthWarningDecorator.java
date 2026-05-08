package model;

public class HealthWarningDecorator implements MessageDisplay {
    private MessageDisplay md;

    public HealthWarningDecorator(MessageDisplay md) {
        this.md = md;
    }

    @Override
    public String getMessage() {
        return md.getMessage() + " | *** WARNING: Caffeine limit reached! ***";
    }
}