package view;

import controller.TeaMakerController;
import model.Observer;
import model.TeaMakerModel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;

public class TeaMakerView extends JFrame implements Observer {
    private TeaMakerModel model;
    private TeaMakerController controller;

    // UI Components
    private JTextField tfCups;
    private JButton btnFilled, btnStart, btnBoil, btnReset;
    private JLabel lblTotalCupsValue;
    private JLabel lblMessage;


    private JLabel lblLedIdle, lblLedMaking, lblLedBoiling, lblLedDone;

    public TeaMakerView(TeaMakerModel model) {
        this.model = model;
        this.model.addObserver(this);
        initializeUI();
    }

    public void setController(TeaMakerController controller) {
        this.controller = controller;
    }

    private void initializeUI() {
        setTitle("Tea Maker");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        LineBorder border = new LineBorder(Color.BLACK, 1);
        Font boldFont = new Font("Arial", Font.BOLD, 14);


        JPanel pnlRow1 = new JPanel(new GridLayout(1, 2));
        pnlRow1.setBorder(border);

        btnFilled = new JButton("FILLED");
        btnFilled.setFont(boldFont);
        btnFilled.setBackground(Color.WHITE);

        tfCups = new JTextField("0");
        tfCups.setHorizontalAlignment(JTextField.CENTER);
        tfCups.setFont(boldFont);

        pnlRow1.add(btnFilled);
        pnlRow1.add(tfCups);


        JPanel pnlRow2 = new JPanel(new GridLayout(1, 2));
        pnlRow2.setBorder(border);


        btnStart = new JButton("START");
        btnStart.setFont(new Font("Arial", Font.BOLD, 24));
        btnStart.setBackground(Color.WHITE);


        JPanel pnlLeds = new JPanel(new GridLayout(4, 1));

        lblLedIdle = createStatusLabel("IDLE", border);
        lblLedMaking = createStatusLabel("MAKING TEA", border);
        lblLedBoiling = createStatusLabel("BOILING WATER", border);
        lblLedDone = createStatusLabel("DONE", border);

        pnlLeds.add(lblLedIdle);
        pnlLeds.add(lblLedMaking);
        pnlLeds.add(lblLedBoiling);
        pnlLeds.add(lblLedDone);

        pnlRow2.add(btnStart);
        pnlRow2.add(pnlLeds);


        JPanel pnlRow3 = new JPanel(new GridLayout(1, 2));
        pnlRow3.setBorder(border);

        JLabel lblTotalTitle = new JLabel("Total Cups", SwingConstants.CENTER);
        lblTotalTitle.setFont(boldFont);
        lblTotalTitle.setBorder(border);

        lblTotalCupsValue = new JLabel(String.valueOf(model.getMonthlyTotalCups()), SwingConstants.CENTER);
        lblTotalCupsValue.setFont(boldFont);

        pnlRow3.add(lblTotalTitle);
        pnlRow3.add(lblTotalCupsValue);


        JPanel pnlRow4 = new JPanel(new GridLayout(1, 1));
        btnBoil = new JButton("BOIL WATER");
        btnBoil.setFont(boldFont);
        btnBoil.setBackground(Color.WHITE);
        pnlRow4.add(btnBoil);
        pnlRow4.setBorder(border);


        JPanel pnlRow5 = new JPanel(new GridLayout(1, 1));
        lblMessage = new JLabel("Messages/Warnings/Notifications", SwingConstants.CENTER);
        lblMessage.setForeground(Color.RED);
        lblMessage.setFont(new Font("Arial", Font.BOLD, 12));
        pnlRow5.add(lblMessage);
        pnlRow5.setBorder(border);


        JPanel pnlRow6 = new JPanel(new GridLayout(1, 2));
        JLabel lblDay = new JLabel(LocalDate.now().getDayOfWeek().toString(), SwingConstants.CENTER);
        JLabel lblDate = new JLabel(LocalDate.now().toString(), SwingConstants.CENTER);

        lblDay.setBorder(border);
        lblDate.setBorder(border);

        pnlRow6.add(lblDay);
        pnlRow6.add(lblDate);
        pnlRow6.setBorder(border);


        JPanel pnlRow7 = new JPanel(new GridLayout(1, 1));
        btnReset = new JButton("Reset");
        btnReset.setFont(boldFont);
        btnReset.setBackground(Color.WHITE);
        pnlRow7.add(btnReset);
        pnlRow7.setBorder(border);

        // add to main frame
        add(pnlRow1);
        add(pnlRow2);
        add(pnlRow3);
        add(pnlRow4);
        add(pnlRow5);
        add(pnlRow6);
        add(pnlRow7);

        // Listeners
        btnFilled.addActionListener(e -> controller.handleFilled(tfCups.getText()));
        btnStart.addActionListener(e -> controller.handleStart());
        btnBoil.addActionListener(e -> controller.handleBoil());
        btnReset.addActionListener(e -> controller.handleReset());

        // initial state
        updateButtons("EMPTY");
    }


    private JLabel createStatusLabel(String text, javax.swing.border.Border border) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        lbl.setBorder(border);
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        return lbl;
    }

    public void updateView() {
        String stateName = model.getState().getStateName();

        lblMessage.setText(model.getStatusMessage());

        lblTotalCupsValue.setText(String.valueOf(model.getMonthlyTotalCups()));

        updateIndicatorColor(stateName);
        updateButtons(stateName);
    }

    public void update() {
        updateView();
    }

    private void updateIndicatorColor(String state) {
        lblLedIdle.setBackground(Color.WHITE);
        lblLedMaking.setBackground(Color.WHITE);
        lblLedBoiling.setBackground(Color.WHITE);
        lblLedDone.setBackground(Color.WHITE);


        switch (state) {
            case "IDLE":
                lblLedIdle.setBackground(Color.YELLOW);
                break;
            case "MAKING TEA":
                lblLedMaking.setBackground(Color.YELLOW);
                break;
            case "BOILING WATER":
                lblLedBoiling.setBackground(Color.YELLOW);
                break;
            case "DONE":
                lblLedDone.setBackground(Color.YELLOW);
                break;
            case "EMPTY":

                break;
        }
    }

    private void updateButtons(String state) {
        boolean isWorking = state.equals("MAKING TEA") || state.equals("BOILING WATER");
        btnFilled.setEnabled(!isWorking && state.equals("EMPTY"));
        btnStart.setEnabled(state.equals("IDLE"));
        btnBoil.setEnabled(state.equals("IDLE"));
        btnReset.setEnabled(state.equals("DONE"));
        if (state.equals("EMPTY")) tfCups.setText("");
    }
}