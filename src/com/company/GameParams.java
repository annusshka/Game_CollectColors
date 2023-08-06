package com.company;

public class GameParams {
    private int rowCount;
    private int colCount;
    private int colourCount;

    public GameParams(int rowCount, int colCount, int colourCount) {
        this.rowCount = rowCount;
        this.colCount = colCount;
        this.colourCount = colourCount;
    }

    public GameParams() {
        this(7, 7, 12);
    }

    /**
     * @return the colCount
     */
    public int getColCount() {
        return colCount;
    }

    /**
     * @param colCount the colCount to set
     */
    public void setColCount(int colCount) {
        this.colCount = colCount;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    /**
     * @return the colorCount
     */
    public int getColourCount() {
        return colourCount;
    }

    /**
     * @param colourCount the colorCount to set
     */
    public void setColourCount(int colourCount) {
        this.colourCount = colourCount;
    }
}