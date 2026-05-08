package model;

public class DoneState implements TeaState {
    private TeaMakerModel model;

    public DoneState(TeaMakerModel model) {
        this.model = model;
    }

    public void onEnter() {
        model.saveToDatabase();
        model.addToTotalCups(model.getCurrentCups());
    }

    @Override
    public void fill(int cups) {
    }

    @Override
    public void start() {
    }

    @Override
    public void boil() {
    }

    @Override
    public void timerExpired() {
    }

    @Override
    public void reset() {
        model.setCups(0);
        model.setCurrentState(model.getEmptyState());
    }

    @Override
    public String getStateName() {
        return "DONE";
    }
}