package model;

public class EmptyState implements TeaState {
    private TeaMakerModel model;

    public EmptyState(TeaMakerModel model) {
        this.model = model;
    }

    @Override
    public void fill(int cups) {
        model.setCups(cups);
        model.setCurrentState(model.getIdleState());
    }

    @Override
    public void start() {
        model.setStatusMessage("Warning: Machine is EMPTY!");
    }

    @Override
    public void boil() {
        model.setStatusMessage("Warning: Machine is EMPTY!");
    }

    @Override
    public void timerExpired() {
    }

    @Override
    public void reset() {
    }

    @Override
    public String getStateName() {
        return "EMPTY";
    }

    @Override
    public void onEnter() {
    }
}