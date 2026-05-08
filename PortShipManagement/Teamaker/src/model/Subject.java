package model;

public interface Subject {
    void addObserver(Observer o);
    void notifyObservers();
}
