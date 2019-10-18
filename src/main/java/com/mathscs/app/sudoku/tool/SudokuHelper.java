package com.mathscs.app.sudoku.tool;

import com.mathscs.app.sudoku.model.Cell;
import com.mathscs.app.sudoku.model.Sudoku;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hushu.czh
 * @date 2019/10/17
 */
public class SudokuHelper {
    private static List<Integer> NINE_NUMS = Collections.unmodifiableList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
    private Sudoku soudu;

    public SudokuHelper(Sudoku soudu) {
        this.soudu = soudu;
    }

    /**
     * 按照指定cell查询他的相关cells.
     *
     * @param cell
     * @return
     */
    public Set<Cell> obtainNeighborCellsByCell(Cell cell) {
        Cell[][] cells = soudu.cells;
        Set<Cell> result = new HashSet<>();
        int x = cell.getX();
        int y = cell.getY();
        //obtain row cells
        for (int j = 0; j < 9; j++) {
            if (j != y) {
                result.add(cells[x][j]);
            }
        }
        //obtain column cells
        for (int i = 0; i < 9; i++) {
            if (x != i) {
                result.add(cells[i][y]);
            }
        }
        //obtain square cells
        int k = x / 3 * 3 + y / 3;
        int i = k / 3 * 3;
        int j = k % 3 * 3;
        for (int ii = 0; ii < 3; ii++) {
            for (int jj = 0; jj < 3; jj++) {
                if (i + ii != x || j + jj != y) { result.add(cells[i + ii][j + jj]); }
            }
        }
        return result;
    }

    /**
     * 按照指定cell查询他的横向cells.
     *
     * @param cell
     * @return
     */
    public Set<Cell> obtainRowCellsByCell(Cell cell) {
        Cell[][] cells = soudu.cells;
        Set<Cell> result = new HashSet<>();
        int x = cell.getX();
        int y = cell.getY();
        //obtain row cells
        for (int j = 0; j < 9; j++) {
            if (j != y) {
                result.add(cells[x][j]);
            }
        }
        return result;
    }

    /**
     * 按照指定cell查询他的纵向cells.
     *
     * @param cell
     * @return
     */
    public Set<Cell> obtainColumnCellsByCell(Cell cell) {
        Cell[][] cells = soudu.cells;
        Set<Cell> result = new HashSet<>();
        int x = cell.getX();
        int y = cell.getY();

        //obtain column cells
        for (int i = 0; i < 9; i++) {
            if (x != i) {
                result.add(cells[i][y]);
            }
        }
        return result;
    }

    /**
     * 按照指定cell查询他的方格相关cells.
     *
     * @param cell
     * @return
     */
    public Set<Cell> obtainSquareCellsByCell(Cell cell) {
        Cell[][] cells = soudu.cells;
        Set<Cell> result = new HashSet<>();
        int x = cell.getX();
        int y = cell.getY();
        //obtain square cells
        int k = x / 3 * 3 + y / 3;
        int i = k / 3 * 3;
        int j = k % 3 * 3;
        for (int ii = 0; ii < 3; ii++) {
            for (int jj = 0; jj < 3; jj++) {
                if (i != x || j != y) { result.add(cells[i + ii][j + jj]); }
            }
        }
        return result;
    }

    public void initUnknownCell(Cell cell) {
        List<Integer> possibleValues = cell.getPossibleValues();
        if (possibleValues.isEmpty()) {
            possibleValues.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        }
        Set<Cell> neighborCells = obtainNeighborCellsByCell(cell);
        List<Integer> impossibleValues = neighborCells.stream().filter(nc -> nc.getPossibleValues().size() == 1)
            .map(nc -> nc.getPossibleValues().get(0)).collect(Collectors.toList());
        possibleValues.removeAll(impossibleValues);
        if (possibleValues.size() == 1) {
            removeImpossibleCell(cell);
        }
    }

    /**
     * 仅处理cell值确定的情况.
     *
     * @param cell
     */
    public void removeImpossibleCell(Cell cell) {
        if (cell.getPossibleValues().size() > 1 || cell.getPossibleValues().size() == 0) {
            return;
        }
        soudu.remainingCells.remove(cell);
        Set<Cell> neighborCells = obtainNeighborCellsByCell(cell);
        for (Cell neighborCell : neighborCells) {
            if (neighborCell.getPossibleValues().contains(cell.getPossibleValues().get(0))) {
                try {
                    neighborCell.getPossibleValues().remove(cell.getPossibleValues().get(0));
                    removeImpossibleCell(neighborCell);
                } catch (Exception e) {
                    System.out.println(e.getStackTrace());
                }
            }
        }
    }

    /**
     * 通过集合方式排除.
     *
     * @param nineCells
     */
    public void excludeBySet(List<Cell> nineCells) {
        List<Cell> celllist = nineCells.stream().sorted(
            Comparator.comparingInt(cell -> ((Cell)cell).getPossibleValues().size()).reversed())
            .collect(Collectors.toList());
        for (int i = 0; i < celllist.size(); i++) {
            Set<Cell> set = new HashSet<>();
            Set<Cell> otherSet = nineCells.stream().collect(Collectors.toSet());
            Cell c = celllist.get(i);
            set.add(c);
            for (int j = i+1; j < celllist.size(); j++) {
                Cell cc = celllist.get(j);
                if (c.getPossibleValues().containsAll(cc.getPossibleValues())) {
                    set.add(cc);
                }
            }
            if (set.size() >= c.getPossibleValues().size()) {
                //可以排除其他cell里可能值
                otherSet.removeAll(set);
                otherSet.stream().forEach(cell ->{
                    cell.getPossibleValues().removeAll(c.getPossibleValues());
                    if (cell.getPossibleValues().size() == 1) {
                        removeImpossibleCell(cell);
                    }
                });
            }
        }
    }

    /**
     * 通过唯一性排除
     *
     * @param nineCells
     */
    public void excludeByUnique(List<Cell> nineCells) {
        for (Integer num : NINE_NUMS) {
            List<Cell> list = nineCells.stream()
                .filter(c -> c.getPossibleValues().size() > 1)
                .filter(c -> c.getPossibleValues().contains(num)).collect(
                    Collectors.toList());
            if (list.size() == 1) {
                Cell cell = list.get(0);
                cell.setPossibleValues(Arrays.asList(num));
                removeImpossibleCell(cell);
            }
        }
    }

}
