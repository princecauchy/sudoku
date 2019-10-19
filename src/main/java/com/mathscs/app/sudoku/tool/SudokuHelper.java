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
    public volatile boolean ifChanged;
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
        ifChanged |= possibleValues.removeAll(impossibleValues);
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
        ifChanged |= soudu.remainingCells.remove(cell);
        Set<Cell> neighborCells = obtainNeighborCellsByCell(cell);
        for (Cell neighborCell : neighborCells) {
            if (neighborCell.getPossibleValues().contains(cell.getPossibleValues().get(0))) {
                ifChanged |= neighborCell.getPossibleValues().remove(cell.getPossibleValues().get(0));
                removeImpossibleCell(neighborCell);
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
            for (int j = i + 1; j < celllist.size(); j++) {
                Cell cc = celllist.get(j);
                if (c.getPossibleValues().containsAll(cc.getPossibleValues())) {
                    set.add(cc);
                }
            }
            if (set.size() >= c.getPossibleValues().size()) {
                //可以排除其他cell里可能值
                otherSet.removeAll(set);
                otherSet.stream().forEach(cell -> {
                    ifChanged |= cell.getPossibleValues().removeAll(c.getPossibleValues());
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
                ifChanged = true;
                removeImpossibleCell(cell);
            }
        }
    }

    public void excludeByAcrossList(List<Cell> squareList) {
        Cell cell = squareList.get(0);
        int k = cell.getX() / 3 * 3 + cell.getY() / 3; //获取square序号
        int i = k / 3 * 3;
        int j = k % 3 * 3;
        for (int ii = 0; ii < 3; ii++) {
            List<Cell> list2 = new ArrayList<>(obtainRowCellsByCell(soudu.cells[i + ii][j]));
            list2.add(soudu.cells[i + ii][j]);
            excludeByAcrossList(squareList, list2);
        }
            for (int jj = 0; jj < 3; jj++) {
                List<Cell> list2 = new ArrayList<>(obtainColumnCellsByCell(soudu.cells[i][j + jj]));
                list2.add(soudu.cells[i][j + jj]);
                excludeByAcrossList(squareList, list2);
            }
    }

    private void excludeByAcrossList(List<Cell> list1, List<Cell> list2) {
        //计算交集
        List<Cell> intersection = new ArrayList<>(list1);
        intersection.retainAll(list2);
        //各list的差集
        List<Cell> l1 = new ArrayList<>(list1);
        l1.removeAll(intersection);
        List<Cell> l2 = new ArrayList<>(list2);
        l2.removeAll(intersection);
        //计算l1和l2的possibleValues交集
        Set<Integer> possiblevalues1 = l1.stream().flatMap(cell -> cell.getPossibleValues().stream()).collect(
            Collectors.toSet());
        Set<Integer> possiblevalues2 = l2.stream().flatMap(cell -> cell.getPossibleValues().stream()).collect(
            Collectors.toSet());
        Set<Integer> possibleValues = new HashSet<>(possiblevalues1);
        possibleValues.retainAll(possiblevalues2);
        //遍历l1和l2,利用possibleValuses缩小值域
        l1.stream().forEach(cell -> {
            if (cell.getPossibleValues().size() > 1) {
                ifChanged |= cell.getPossibleValues().retainAll(possibleValues);
                if (cell.getPossibleValues().size() == 1) {
                    removeImpossibleCell(cell);
                }
            }
        });
        l2.stream().forEach(cell -> {
            if (cell.getPossibleValues().size() > 1) {
                ifChanged |= cell.getPossibleValues().retainAll(possibleValues);
                if (cell.getPossibleValues().size() == 1) {
                    removeImpossibleCell(cell);
                }
            }
        });

    }

}
