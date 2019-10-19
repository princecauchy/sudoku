package com.mathscs.app.sudoku;

import com.mathscs.app.sudoku.model.Cell;
import com.mathscs.app.sudoku.model.Sudoku;
import com.mathscs.app.sudoku.tool.SudokuHelper;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        try {
            URL path = Thread.currentThread().getContextClassLoader().getResource("in.csv");
            Sudoku soudu = new Sudoku(path);
            write(soudu, false);
            SudokuHelper souduHelper = new SudokuHelper(soudu);
            soudu.splitBySquare().stream().flatMap(list -> list.stream()).forEach(souduHelper::initUnknownCell);
            write(soudu, true);
            souduHelper.ifChanged = true;
            while (!soudu.remainingCells.isEmpty()) {
                souduHelper.ifChanged = false;
                List<List<Cell>> squareList = soudu.splitBySquare();
                squareList.stream().forEach(cellList -> {
                    souduHelper.excludeByUnique(cellList);
                    souduHelper.excludeBySet(cellList);
                    souduHelper.excludeByAcrossList(cellList);
                });
                List<List<Cell>> rowList = soudu.splitByRow();
                rowList.stream().forEach(cellList -> {
                    souduHelper.excludeByUnique(cellList);
                    souduHelper.excludeBySet(cellList);
                });
                List<List<Cell>> columnList = soudu.splitByColumn();
                columnList.stream().forEach(cellList -> {
                    souduHelper.excludeByUnique(cellList);
                    souduHelper.excludeBySet(cellList);
                });
                if (!souduHelper.ifChanged) {
                    break;
                }
                write(soudu, true);
            }
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    public static void write(Sudoku soudu, boolean append) {
        URL outUrl = Thread.currentThread().getContextClassLoader().getResource("out.csv");
        soudu.writeToFile(outUrl, append);

    }
}
