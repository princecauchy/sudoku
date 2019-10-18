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
        write(soudu, false);
        SudokuHelper souduHelper = new SudokuHelper(soudu);
        try {
            soudu.remainingCells.stream().forEach(souduHelper::initUnknownCell);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        write(soudu, true);
        souduHelper.ifChanged = true;
        while (!soudu.remainingCells.isEmpty()) {
            souduHelper.ifChanged = false;
            List<List<Cell>> squareList = soudu.splitBySquare();
            squareList.stream().forEach(cellList -> {
                souduHelper.excludeByUnique(cellList);
                souduHelper.excludeBySet(cellList);
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
                //跨list<Cell>分析
                break;
            }
            write(soudu, true);
        }
    }

    public static void write(Sudoku soudu, boolean append) {
        URL outUrl = Thread.currentThread().getContextClassLoader().getResource("out.csv");
        soudu.writeToFile(outUrl, append);

    }
}
