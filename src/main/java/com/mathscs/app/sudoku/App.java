package com.mathscs.app.sudoku;

import com.mathscs.app.sudoku.model.Cell;
import com.mathscs.app.sudoku.model.Sudoku;
import com.mathscs.app.sudoku.tool.SudokuHelper;

import java.net.URL;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        URL path = Thread.currentThread().getContextClassLoader().getResource("in.csv");
        Sudoku soudu = new Sudoku(path);
        SudokuHelper souduHelper = new SudokuHelper(soudu);
        for (Cell c : soudu.remainingCells) {
            souduHelper.initUnknownCell(c);
        }
        write(soudu);
        int i = 0;
        while (!soudu.remainingCells.isEmpty()) {
            i++;
            if (i == 10) {
                break;
            }
            List<List<Cell>> squareList = soudu.splitBySquare();
            squareList.stream().forEach(souduHelper::excludeByUnique);
            squareList.stream().forEach(souduHelper::excludeBySet);
            List<List<Cell>> rowList = soudu.splitByRow();
            rowList.stream().forEach(souduHelper::excludeByUnique);
            rowList.stream().forEach(souduHelper::excludeBySet);
            List<List<Cell>> columnList = soudu.splitByColumn();
            columnList.stream().forEach(souduHelper::excludeByUnique);
            columnList.stream().forEach(souduHelper::excludeBySet);
            write(soudu);
        }
    }

    public static void write(Sudoku soudu) {
        URL outUrl = Thread.currentThread().getContextClassLoader().getResource("out.csv");
        soudu.writeToFile(outUrl);

    }
}
