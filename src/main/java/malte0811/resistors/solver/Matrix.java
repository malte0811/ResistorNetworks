package malte0811.resistors.solver;

import com.google.common.base.Preconditions;

import java.util.Arrays;

public interface Matrix {
    MutableMatrix copy();

    double get(int row, int col);

    int numRows();

    int numCols();

    record MutableMatrix(double[] data, int numRows) implements Matrix {
        public MutableMatrix(int numRows, int numCols) {
            this(new double[numRows * numCols], numRows);
        }

        @Override
        public MutableMatrix copy() {
            return new MutableMatrix(Arrays.copyOf(data, data.length), numRows);
        }

        @Override
        public double get(int row, int col) {
            return data[index(row, col)];
        }

        @Override
        public int numCols() {
            return data.length / numRows;
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < numRows; ++i) {
                for (int j = 0; j < numCols(); ++j) {
                    result.append(get(i, j)).append("\t");
                }
                result.append('\n');
            }
            return result.toString();
        }

        public MutableMatrix set(int row, int col, double value) {
            data[index(row, col)] = value;
            return this;
        }

        public MutableMatrix add(int row, int col, double value) {
            data[index(row, col)] += value;
            return this;
        }

        public void swapRows(int row1, int row2) {
            for (int col = 0; col < numCols(); ++col) {
                final double temp = get(row1, col);
                set(row1, col, get(row2, col));
                set(row2, col, temp);
            }
        }

        public void swapCols(int col1, int col2) {
            for (int row = 0; row < numCols(); ++row) {
                final double temp = get(row, col1);
                set(row, col1, get(row, col2));
                set(row, col2, temp);
            }
        }

        public void setColumn(int col, double[] values) {
            Preconditions.checkArgument(values.length == numRows);
            for (int i = 0; i < values.length; ++i) {
                set(i, col, values[i]);
            }
        }

        private int index(int row, int col) {
            return row + col * numRows;
        }
    }
}