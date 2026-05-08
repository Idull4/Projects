package model;

public class MakingTeaState implements TeaState {
    private TeaMakerModel model;

    public MakingTeaState(TeaMakerModel model) {
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
        return "MAKING TEA";
    }
    @Override
    public void onEnter() {
    }
}