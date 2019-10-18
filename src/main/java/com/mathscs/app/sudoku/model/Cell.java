package com.mathscs.app.sudoku.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hushu.czh
 * @date 2019/10/17
 */
public class Cell {
    @Getter
    private int x;
    @Getter
    private int y;

    public Cell(int x, int y, List<Integer> possibleValues) {
        this.x = x;
        this.y = y;
        this.possibleValues = possibleValues;
    }

    @Getter
    @Setter
    private List<Integer> possibleValues;

    @Override
    public String toString() {
        return possibleValues.stream().map(String::valueOf)
            .collect(Collectors.joining("|"));
    }
}
