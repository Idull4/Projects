package controller;

import model.TeaMakerModel;
import view.TeaMakerView;
import javax.swing.JOptionPane;

public class TeaMakerController {
    private TeaMakerModel model;
    private TeaMakerView view;

    public TeaMakerController(TeaMakerModel model, TeaMakerView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
    }

    public void handleFilled(String cupsText) {
        try {
            int cups = Integer.parseInt(cupsText);
            if(cups <= 0) throw new NumberFormatException();

            model.getState().fill(cups);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(view, "Please enter valid number!");
        }
    }

    public void handleStart() { model.getState().start(); }
    public void handleBoil() { model.getState().boil(); }
    public void handleReset() { model.getState().reset(); }
}