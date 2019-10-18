package com.mathscs.app.sudoku.model;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * @author hushu.czh
 * @date 2019/10/17
 */
public class Sudoku {
    public Set<Cell> remainingCells = new LinkedHashSet<>();
    public Cell[][] cells;
    private List<List<Cell>> rowSplit;
    private List<List<Cell>> columnSplit;
    private List<List<Cell>> squareSplit;
    //private Map</**number**/Integer,/**size**/Integer>

    public Sudoku(URL url) {
        cells = new Cell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Cell cell = new Cell(i, j, new ArrayList<>());
                cells[i][j] = cell;
                remainingCells.add(cell);
            }
        }
        if (url != null) {
            try {
                DataInputStream dataInputStream = new DataInputStream(url.openStream());
                String line = dataInputStream.readLine();
                int i = 0;
                while (line != null) {
                    String[] row = line.split(",");
                    for (int j = 0; j < row.length; j++) {
                        int cellValue = NumberUtils.toInt(row[j]);
                        if (cellValue != 0) {
                            cells[i][j].getPossibleValues().add(cellValue);
                            remainingCells.remove(cells[i][j]);
                        }
                    }
                    line = dataInputStream.readLine();
                    i++;
                }
            } catch (IOException e) {
            }
        }
    }

    /**
     * 将所有cells按行拆分.
     *
     * @return
     */
    public List<List<Cell>> splitByRow() {
        if (rowSplit != null) {
            return rowSplit;
        }
        rowSplit = new ArrayList<>(9);
        for (int i = 0; i < 9; i++) {
            rowSplit.add(Arrays.asList(cells[i]));
        }
        return rowSplit;
    }

    /**
     * 将所有cells按列拆分.
     *
     * @return
     */
    public List<List<Cell>> splitByColumn() {
        if (columnSplit != null) {
            return columnSplit;
        }
        columnSplit = new ArrayList<>(9);
        for (int j = 0; j < 9; j++) {
            columnSplit.add(new ArrayList<>());
            for (int i = 0; i < 9; i++) {
                columnSplit.get(j).add(cells[i][j]);
            }
        }
        return columnSplit;
    }

    /**
     * 将所有cells按方格拆分
     *
     * @return
     */
    public List<List<Cell>> splitBySquare() {
        if (squareSplit != null) {
            return squareSplit;
        }
        squareSplit = new ArrayList<>(9);
        for (int k = 0; k < 9; k++) {
            int i = k / 3 * 3;
            int j = k % 3 * 3;
            squareSplit.add(new ArrayList<>());
            for (int ii = 0; ii < 3; ii++) {
                for (int jj = 0; jj < 3; jj++) {
                    squareSplit.get(k).add(cells[i + ii][j + jj]);
                }
            }
        }
        return squareSplit;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                stringBuilder.append(cell).append(",");
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public void writeToFile(URL url) {
        if (url != null) {
            try {
                FileOutputStream in = new FileOutputStream(url.getPath(),true);
                DataOutputStream dif = new DataOutputStream(in);
                dif.write(toString().getBytes("utf-8"));
                dif.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
