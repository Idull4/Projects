package model;

public class IdleState implements TeaState {
    private TeaMakerModel model;

    public IdleState(TeaMakerModel model) {
        this.model = model;
    }

    @Override
    public void fill(int cups) {
        model.setCups(cups);
    }

    @Override
    public void start() {
        model.setCurrentState(model.getMakingTeaState());
        model.startBrewingProcess();
    }

    @Override
    public void boil() {
        model.setCurrentState(model.getBoilingState());
        model.startBoilingProcess();
    }

    @Override
    public void timerExpired() {
    }

    @Override
    public void reset() {
    }

    @Override
    public String getStateName() {
        return "IDLE";
    }
    @Override
    public void onEnter() {
    }
}