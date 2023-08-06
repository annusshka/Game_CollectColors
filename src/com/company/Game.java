package com.company;

import java.util.Random;

public class Game {

    public enum GameState {
        NOT_STARTED,
        PLAYING,
        WIN,
        FAIL
    }

    public GameState getState() {
        return state;
    }

    GameState state = GameState.NOT_STARTED;

    private final Random rnd = new Random();

    private int[][] field = null;

    private int colourCount = 0;
    private int moneyEnd = 0;
    private int money = 0;

    public Game() {
    }

    public void newGame(int rowCount, int colCount, int colourCount, int money, int moneyEnd) {
        field = new int[rowCount][colCount];
        this.colourCount = colourCount;
        this.money = money;

        int k = 0;
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                int count = rnd.nextInt(colCount / 2);
                if (r > count && k < moneyEnd) {
                    field[r][c] = rnd.nextInt(getColourCount()) + 1;
                    k++;
                } else {
                    field[r][c] = 0;
                }
            }
        }
        /*for (int r = 0; r < rowCount && k < moneyEnd; r++) {
            for (int c = 0; c < colCount; c++) {
                if (k < moneyEnd && field[r][c] != 0) {
                    field[r][c] = rnd.nextInt(getColourCount()) + 1;
                    k++;
                } else {
                    break;
                }
            }
        }*/
        down();

        int fullMoney = 10 - (int) (moneyEnd * 0.85) % 10;
        this.moneyEnd = (int) (moneyEnd * 0.85) + fullMoney;

        int count = 0;
        for (int c = 0; c < colCount; c++) {
            if (field[rowCount - 1][c] == 0) {
                count++;
            }
        }
        replace(rowCount, colCount, count);

        state = GameState.PLAYING;
    }

    public void down() {
        for (int c = 0; c < getColCount(); c++) {
            for (int r = getRowCount() - 1; r > 0; r--) {
                if (field[r][c] == 0) {
                    for (int r1 = r - 1; r1 >= 0; r1--) {
                        if (field[r1][c] != 0) {
                            field[r][c] = field[r1][c];
                            field[r1][c] = 0;
                            break;
                        }
                    }
                }
            }
        }
    }

    public void replace(int rowCount, int colCount, int k) {
        int index = -1;

        for (int c = 0; c < colCount - k; c++) {
            if (field[rowCount - 1][c] == 0) {
                index = c;
                break;
            }
        }
        if (index == -1) {
            return;
        }

        int countNull = 1;
        for (int c = index; c < colCount - k; c++) {
            if (field[rowCount - 1][c] == 0 && field[rowCount - 1][c + 1] == 0) {
                countNull++;
            } else if (field[rowCount - 1][c] == 0 && field[rowCount - 1][c + 1] != 0) {
                break;
            }
        }

        for (int r = 0; r < rowCount; r++) {
            for (int c = index; c < colCount - countNull; c++) {
                field[r][c] = field[r][c + countNull];
                field[r][c + countNull] = 0;
            }
        }
    }

    public void addNewLine() {
        for (int c = 0; c < getColCount(); c++) {
            if (field[0][c] == 0) {
                field[0][c] = rnd.nextInt(getColourCount());
            }
        }
        down();
        state = GameState.PLAYING;
    }

    public int haveNoAction(int row, int col) {
        int k = 0;
        one:
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if ((0 <= r && r < getRowCount() && 0 <= c && c < getColCount()) &&
                        !(r == row && c == col) && (r == row || c == col)) {
                    if (field[r][c] == field[row][col] && field[row][col] != 0) {
                        k++;
                        break one;
                    }
                }
            }
        }
        return k;
    }

    public void haveNoAction() {
        int k = 0;
        one:
        for (int row = 0; row < getRowCount(); row++) {
            for (int col = 0; col < getColCount(); col++) {
                k += haveNoAction(row, col);
                if (k != 0) {
                    break one;
                }
            }
        }
        if (k == 0) {
            moneyAction();
        }
    }

    public void repaint(int rowCount, int colCount, int colourCount) {
        this.colourCount = colourCount;

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                if (getCell(r, c) != 0) {
                    field[r][c] = rnd.nextInt(getColourCount()) + 1;
                }
            }
        }

        state = GameState.PLAYING;
    }

    public boolean nullField() {
        int rowCount = getRowCount(), colCount = getColCount();
        int k = 0;
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < colCount; c++) {
                if (field[r][c] == 0) {
                    k++;
                }
            }
        }
        return (k == colCount * rowCount || k == colCount * rowCount - 1);
    }

    public void action(int row, int col, int value) {
        int rowCount = getRowCount(), colCount = getColCount();
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = col - 1; c <= col + 1; c++) {
                if (!(r == row && c == col) && (r == row || c == col) &&
                        (0 <= r && r < rowCount && 0 <= c && c < colCount)) {
                    if (field[r][c] == value) {
                        field[row][col] = 0;
                        money++;
                        action(r, c, value);
                    } else if (field[r][c] != value && field[row][col] == value) {
                        field[row][col] = 0;
                    }
                }
            }
        }
    }

    public void moneyAction() {
        if (money < moneyEnd) {
            state = GameState.FAIL;
        } else {
            state = GameState.WIN;
        }
    }

    public void calcState() {
        if (nullField()) {
            state = GameState.WIN;
        }
    }

    public void leftMouseClick(int row, int col) {
        int rowCount = getRowCount(), colCount = getColCount();

        if (row < 0 || row >= rowCount || col < 0 || col >= colCount) {
            return;
        }

        if (state != GameState.PLAYING || field[row][col] == 0) {
            return;
        }

        if (field[row][col] > 0) {
            int k = 0;
            one:
            for (int r = row - 1; r <= row + 1; r++) {
                for (int c = col - 1; c <= col + 1; c++) {
                    if (k > 0) {
                        break one;
                    }
                    if (!(r == row && c == col) && (r == row || c == col) &&
                            (0 <= r && r < rowCount && 0 <= c && c < colCount) &&
                            (field[r][c] == field[row][col])) {
                        action(row, col, field[row][col]);
                        money++;
                        k++;
                    }
                }
            }
            down();
        }
        calcState();

        if (state == GameState.PLAYING) {
            int count = 0;
            for (int c = 0; c < colCount; c++) {
                if (field[rowCount - 1][c] == 0) {
                    count++;
                }
            }
            for (int c = 0; c < colCount - count; c++) {
                replace(rowCount, colCount, count);
            }
        }
    }

    public int getMoney() {
        return money;
    }

    public int getRowCount() {
        return field == null ? 0 : field.length;
    }

    public int getColCount() {
        return field == null ? 0 : field[0].length;
    }

    public int getColourCount() {
        return colourCount;
    }

    public int getCell(int row, int col) {
        return (row < 0 || row >= getRowCount() || col < 0 || col >= getColCount()) ? 0 : field[row][col];
    }
}