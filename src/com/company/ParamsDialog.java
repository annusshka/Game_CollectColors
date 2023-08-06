package com.company;

import util.JTableUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParamsDialog extends JDialog{
    private JPanel panelMain;
    private JSpinner spinnerRowCount;
    private JSpinner spinnerColCount;
    private JSpinner spinnerColourCount;
    private JButton buttonOk;
    private JButton buttonNewGame;
    private JButton buttonCancel;
    private JSlider sliderCellSize;

    private GameParams params;
    private JTable gameFieldJTable;
    private ActionListener newGameAction;

    private int oldCellSize;

    public ParamsDialog(GameParams params, JTable gameFieldJTable, ActionListener newGameAction) {
        this.setTitle("Параметры");
        this.setContentPane(panelMain);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.pack();

        this.setResizable(false);

        this.params = params;
        this.gameFieldJTable = gameFieldJTable;
        this.newGameAction = newGameAction;

        this.oldCellSize = gameFieldJTable.getRowHeight();
        sliderCellSize.addChangeListener(e -> {
            int value = sliderCellSize.getValue();
            JTableUtils.resizeJTableCells(gameFieldJTable, value, value);
        });
        buttonCancel.addActionListener(e -> {
            JTableUtils.resizeJTableCells(gameFieldJTable, oldCellSize, oldCellSize);
            this.setVisible(false);
        });
        buttonNewGame.addActionListener(e -> {
            buttonOk.doClick();
            if (newGameAction != null) {
                newGameAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "newGame"));
            }
        });
        buttonOk.addActionListener(e -> {
            params.setRowCount((int) spinnerRowCount.getValue());
            params.setColCount((int) spinnerColCount.getValue());
            params.setColourCount((int) spinnerColourCount.getValue());
            oldCellSize = gameFieldJTable.getRowHeight();
            this.setVisible(false);
        });
    }

    public void updateView() {
        spinnerRowCount.setValue(params.getRowCount());
        spinnerColCount.setValue(params.getColCount());
        spinnerColourCount.setValue(params.getColourCount());
        sliderCellSize.setValue(gameFieldJTable.getRowHeight());
    }
}
