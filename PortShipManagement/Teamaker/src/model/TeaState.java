package model;

public interface TeaState {
    void fill(int cups);

    void start();

    void boil();

    void timerExpired();

    void reset();

    String getStateName();

    void onEnter();
}