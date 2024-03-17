package malte0811.resistors.solver;

import java.util.Arrays;
import java.util.stream.IntStream;

public class LUDecomposer {
    public static Matrix invert(Matrix mat) {
        final var decomposition = decompose(mat);
        final var inverse = new Matrix.MutableMatrix(mat.numRows(), mat.numCols());
        final double[] rhs = new double[mat.numRows()];
        for (int i = 0; i < mat.numRows(); ++i) {
            rhs[i] = 1;
            final var inverseCol = decomposition.solveUsingLU(rhs);
            inverse.setColumn(i, inverseCol);
            rhs[i] = 0;
        }
        return inverse;
    }

    // Don't look at this code too closely, it is a rough port of a quick&dirty C implementation of LU-decomposition I
    // wrote a few years ago. It is based on the description in "Algorithmic Mathematics" by Hougardy and Vygen.
    public static LUDecomposition decompose(Matrix mat) {
        int numCols = mat.numCols();
        int numRows = mat.numRows();
        Matrix.MutableMatrix out = mat.copy();
        int[] leftPermutation = IntStream.range(0, numRows).toArray();
        int[] rightPermutation = IntStream.range(0, numCols).toArray();
        int r = 0;
        Pivot pivot = getNextForLU(out, r);
        while (pivot != null) {
            if (pivot.p()!=r) {
                out.swapRows(pivot.p(), r);
                swap(leftPermutation, pivot.p(), r);
            }
            if (pivot.q()!=r) {
                out.swapCols(pivot.q(), r);
                swap(rightPermutation, pivot.q(), r);
            }
            final double factor = out.get(r, r);
            for (int row = r+1;row<numRows;row++) {
                final double newIR = out.get(row, r)/factor;
                out.set(row, r, newIR);
                for (int col = r+1;col<numCols;col++) {
                    out.add(row, col, -out.get(r, col)*newIR);
                }
            }
            r++;
            pivot = getNextForLU(out, r);
        }
        return new LUDecomposition(out, leftPermutation, rightPermutation);
    }

    private static void swap(int[] arr, int index1, int index2) {
        int tmp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = tmp;
    }

    private static Pivot getNextForLU(Matrix m, int r) {
        int p = Integer.MAX_VALUE;
        int q = Integer.MAX_VALUE;
        double max = 0;
        for (int j = r; j < m.numCols(); j++) {
            for (int i = r; i < m.numRows(); i++) {
                final double tmp = Math.abs(m.get(i, j));
                if (tmp > max) {
                    p = i;
                    q = j;
                    max = tmp;
                }
            }
        }
        if (p < Integer.MAX_VALUE) {
            return new Pivot(p, q);
        } else {
            return null;
        }
    }

    private static boolean isZero(double d) {
        return Math.abs(d) < 1e-9;
    }

    public record LUDecomposition(Matrix decomposition, int[] leftPermutation, int[] rightPermutation) {
        public double[] solveUsingLU(double[] rhs) {
            final var middle = solveL(rhs);
            return solveU(middle);
        }

        private double[] solveU(double[] right) {
            right = Arrays.copyOf(right, right.length);
            double[] out = new double[right.length];
            for (int i = right.length-1;i!=-1;i--) {
                double diagEntry = decomposition.get(i, i);
                double rightEntry = right[i];
                if (!isZero(rightEntry)&&isZero(diagEntry)) {
                    throw new IllegalStateException("No solution");
                }
                double solution = isZero(diagEntry)?0:rightEntry/diagEntry;
                out[rightPermutation[i]] = solution;
                for (int j = 0;j<i;j++) {
                    right[j] -= solution*decomposition.get(j, i);
                }
            }
            return out;
        }

        private double[] solveL(double[] right) {
            right = Arrays.copyOf(right, right.length);
            double[] out = new double[right.length];
            for (int i = 0;i<right.length;i++) {
                double solution = right[leftPermutation[i]];
                out[i] = solution;
                for (int j = i+1;j<right.length;j++) {
                    right[leftPermutation[j]] -= solution*decomposition.get(j, i);
                }
            }
            return out;
        }

    }

    private record Pivot(int p, int q){}
}
