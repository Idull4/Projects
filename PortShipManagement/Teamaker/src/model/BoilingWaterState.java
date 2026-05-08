package model;

public class BoilingWaterState implements TeaState {
    private TeaMakerModel model;

    public BoilingWaterState(TeaMakerModel model) {
        this.model = model;
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
        model.setCurrentState(model.getDoneState());
    }

    @Override
    public void reset() {
    }

    @Override
    public String getStateName() {
        return "BOILING WATER";
    }
    @Override
    public void onEnter() {
    }
}
