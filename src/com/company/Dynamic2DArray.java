package com.company;

public class Dynamic2DArray
{
    private Object[][] matrix = new Object[1][1];

    public void set(int x, int y, Object value) {
        if (x >= matrix.length) {
            Object[][] tmp = matrix;
            matrix = new Object[x + 1][];
            System.arraycopy(tmp, 0, matrix, 0, tmp.length);
            for (int i = x; i < x + 1; i++) {
                matrix[i] = new Object[y];
            }
        }

        if (y >= matrix[x].length) {
            Object[] tmp = matrix[x];
            matrix[x] = new Object[y + 1];
            System.arraycopy(tmp, 0, matrix[x], 0, tmp.length);
        }

        matrix[x][y] = value;
    }
    public Object[][] getMatrix()
    {
        return matrix;
    }

    public void reset()
    {
        Object[][] newMat = new Object[1][1];
        matrix = newMat;
    }

    public String toString() {
        return matrix.toString();
    }

    public Object get(int x, int y) {
        return x >= matrix.length || y >= matrix[x].length ? 0 : matrix[x][y];
    }
    public Object[] getRow(int x, int y) {
        return matrix[x];
    }
}