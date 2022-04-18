/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DesignAlgorithm;

/**
 *
 * @author maccalman
 */
/*************************************************************************
 *  Compilation:  javac Matrix.java
 *  Execution:    java Matrix
 *
 *  A bare-bones immutable data type for M-by-N matrices.
 *
 *************************************************************************/
public class Matrix {

    private int N;             // number of rows
    private int K;             // number of columns
    private double[][] data;   // M-by-N array
    private SimpleStats[] stats;
    private double maxCorr;
    // create M-by-N matrix of 0's

    public Matrix(int N, int K) {
        this.N = N;
        this.K = K;
        this.data = new double[N][K];
        this.stats = new SimpleStats[K];
        this.maxCorr = 0;
        for (int j = 0; j < K; j++) {
            this.stats[j] = new SimpleStats();
        }
    }
    // create matrix based on 2d array

    public Matrix(double[][] data) {
        this.N = data.length;
        this.K = data[0].length;
        this.data = new double[N][K];
        this.stats = new SimpleStats[K];
        this.maxCorr = 0;
        for (int j = 0; j < K; j++) {
            this.stats[j] = new SimpleStats();
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                this.data[i][j] = data[i][j];
                this.stats[j].newObs(data[i][j]);
            }
        }
    }
    // copy constructor

    private Matrix(Matrix A) {

        this(A.data);
    }
    // create and return a random M-by-N matrix with values between 0 and 1

    public static Matrix random(int N, int K) {
        Matrix A = new Matrix(N, K);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                A.data[i][j] = Math.random();
            }
        }
        return A;
    }
    // create and return the N-by-N identity matrix

    public static Matrix identity(int K) {
        Matrix I = new Matrix(K, K);
        for (int i = 0; i < K; i++) {
            I.data[i][i] = 1;
        }
        return I;
    }
    // swap rows i and j

    private void swap(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
    // create and return the transpose of the invoking matrix

    public Matrix transpose() {
        Matrix A = new Matrix(K, N);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                A.data[j][i] = this.data[i][j];
            }
        }
        return A;
    }
    // return C = A + B

    public Matrix plus(Matrix B) {
        Matrix A = this;
        if (B.N != A.N || B.K != A.K) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(N, K);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                C.data[i][j] = A.data[i][j] + B.data[i][j];
            }
        }
        return C;
    }
    // return C = A - B

    public Matrix minus(Matrix B) {
        Matrix A = this;
        if (B.N != A.N || B.K != A.K) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(N, K);
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                C.data[i][j] = A.data[i][j] - B.data[i][j];
            }
        }
        return C;
    }
    // does A = B exactly?

    public boolean eq(Matrix B) {
        Matrix A = this;
        if (B.N != A.N || B.K != A.K) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                if (A.data[i][j] != B.data[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
    // return C = A * B

    public Matrix times(Matrix B) {
        Matrix A = this;
        if (A.K != B.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        Matrix C = new Matrix(A.N, B.K);
        for (int i = 0; i < C.N; i++) {
            for (int j = 0; j < C.K; j++) {
                for (int k = 0; k < A.K; k++) {
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
                }
            }
        }
        return C;
    }
    // return x = A^-1 b, assuming A is square and has full rank

    public Matrix solve(Matrix rhs) {
        if (N != K || rhs.N != K || rhs.K != 1) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        // create copies of the data
        Matrix A = new Matrix(this);
        Matrix b = new Matrix(rhs);
        // Gaussian elimination with partial pivoting
        for (int i = 0; i < K; i++) {
            // find pivot row and swap
            int max = i;
            for (int j = i + 1; j < K; j++) {
                if (Math.abs(A.data[j][i]) > Math.abs(A.data[max][i])) {
                    max = j;
                }
            }
            A.swap(i, max);
            b.swap(i, max);
            // singular
            if (A.data[i][i] == 0.0) {
                throw new RuntimeException("Matrix is singular.");
            }
            // pivot within b
            for (int j = i + 1; j < K; j++) {
                b.data[j][0] -= b.data[i][0] * A.data[j][i] / A.data[i][i];
            }
            // pivot within A
            for (int j = i + 1; j < K; j++) {
                double m = A.data[j][i] / A.data[i][i];
                for (int k = i + 1; k < K; k++) {
                    A.data[j][k] -= A.data[i][k] * m;
                }
                A.data[j][i] = 0.0;
            }
        }
        // back substitution
        Matrix x = new Matrix(K, 1);
        for (int j = K - 1; j >= 0; j--) {
            double t = 0.0;
            for (int k = j + 1; k < K; k++) {
                t += A.data[j][k] * x.data[k][0];
            }
            x.data[j][0] = (b.data[j][0] - t) / A.data[j][j];
        }
        return x;
    }

    // print matrix to standard output
    public void show() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                System.out.printf("%9.4f ", data[i][j]);
            }
            System.out.println();
        }
    }

    public double determinant(double[][] mat) {
        double result = 0;
        if (mat.length == 1) {
            result = mat[0][0];
            return result;
        }
        if (mat.length == 2) {
            result = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
            return result;
        }
        for (int i = 0; i < mat[0].length; i++) {
            double temp[][] = new double[mat.length - 1][mat[0].length - 1];
            for (int j = 1; j < mat.length; j++) {
                System.arraycopy(mat[j], 0, temp[j - 1], 0, i);
                System.arraycopy(mat[j], i + 1, temp[j - 1], i, mat[0].length - i - 1);
            }
            result += mat[0][i] * Math.pow(-1, i) * determinant(temp);
        }
        return result;

    }

    public static double[][] invert(double a[][]) {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i) {
            b[i][i] = 1;
        }
// Transform the matrix into an upper triangle
        gaussian(a, index);
// Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    b[index[j]][k] -= a[index[j]][i] * b[index[i]][k];
                }
            }
        }
// Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }
// Method to carry out the partial-pivoting Gaussian
// elimination. Here index[] stores pivoting order.

    public static void gaussian(double a[][],
            int index[]) {
        int n = index.length;
        double c[] = new double[n];
// Initialize the index
        for (int i = 0; i < n; ++i) {
            index[i] = i;
        }
// Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) {
                    c1 = c0;
                }
            }
            c[i] = c1;
        }
// Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }
// Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[index[i]][j] / a[index[j]][j];
// Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;
// Modify other elements accordingly
                for (int l = j + 1; l < n; ++l) {
                    a[index[i]][l] -= pj * a[index[j]][l];
                }
            }
        }
    }

    public static Matrix standardized(Matrix matrix) {
        double[][] newMatrix = new double[matrix.N][matrix.K];
        double x;
        for (int i = 0; i < matrix.N; i++) {
            for (int j = 0; j < matrix.K; j++) {
                x = matrix.data[i][j];
                newMatrix[i][j] = (x - matrix.stats[j].sampleMean()) / matrix.stats[j].sampleStdDev();
            }
        }
        return new Matrix(newMatrix);
    }

    public void elementDivide(double constant) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++) {
                data[i][j] = data[i][j] / constant;
            }
        }
    }

    public Matrix maxTimes(Matrix B) {
        Matrix A = this;
        if (A.K != B.N) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        double max = 0;
        double corr;
        double constant = 1;
        Matrix C = new Matrix(A.N, B.K);
        for (int i = 0; i < C.N; i++) {
            for (int j = 0; j < C.K; j++) {
                for (int k = 0; k < A.K; k++) {
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
                }
                if (i == 0 && j == 0) {
                    constant = C.data[i][j];
                }
                C.data[i][j] = C.data[i][j] / constant;
                corr = Math.abs(C.data[i][j]);
                if (i != j) {
                    if (corr > max) {
                        max = corr;
                    }
                }
            }
        }
        C.maxCorr = max;
        return C;
    }

    public static Matrix createxTx(Matrix matrix) {
        Matrix xTx = matrix.transpose().maxTimes(matrix);

        return xTx;
    }

    public void fillMatrix(Matrix M) {
        double[][] d = M.data;
        int startIndex = this.data[0].length - d.length;
        //copy new column
        for (int i = 0; i < d.length - 1; i++) {
            for (int j = 0; j < d[0].length - 1; j++) {
                this.data[i][startIndex + j] = d[i][j];
            }
        }
    }

    public static Matrix createNextMatrix(Matrix currentDesign) {
        int currentCols = currentDesign.K;
        int nextCols = currentCols + 1;
        int colSize = currentCols + 2;
        //  int colSize = 2 * nextCols + nextCols * (nextCols - 1) / 2;
        int rowSize = currentDesign.N;

        double[][] d = currentDesign.data;
        double[][] newMatrix = new double[rowSize][colSize];
        //copy sub matrix
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d[0].length - 1; j++) {
                newMatrix[i][j] = d[i][j];
            }
        }
        return new Matrix(newMatrix);
    }

    // test client
//    public static void main(String[] args) {
//        double[][] d = {{122, 2, 23}, {3, 5, 2}, {7, 5, 9}, {10, 1, 6}};
//        Matrix D = new Matrix(d);
//        D.show();
//        double[][] newd = {{22, 2}, {3, 54}, {2, 2}, {3, 6}};
//        Matrix newM = new Matrix(newd);
//        Matrix nextOne = createNextMatrix(D);
//        nextOne.fillMatrix(newM);
//        nextOne.show();
//
//
//        System.out.println("original:");
//        D.show();
//        System.out.println("new:");
//
//
//        D = standardized(D);
//
//        System.out.println("standarized:");
//        D.show();
//        System.out.println();
//
//
//        Matrix xTx = createxTx(D);
//
//
//        System.out.println("xTx:");
//        xTx.show();
//
//
//
//        System.out.println("maxCorr:" + xTx.maxCorr);
//
//
//
//
//    }
}
