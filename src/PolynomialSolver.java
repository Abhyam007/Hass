import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.math.BigInteger;
import java.util.Iterator;

public class PolynomialSolver {
    public static void main(String[] args) {
        try {
            System.out.println("Solving polynomial for input1.json:");
            String input1 = loadJsonFromResource("input1.json");
            solvePolynomialFromJson(input1);

            System.out.println("\nSolving polynomial for input2.json:");
            String input2 = loadJsonFromResource("input2.json");
            solvePolynomialFromJson(input2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String loadJsonFromResource(String filename) throws Exception {
        InputStream fis = PolynomialSolver.class.getClassLoader().getResourceAsStream(filename);
        if (fis == null) {
            throw new RuntimeException(filename + " not found in resources!");
        }
        JSONTokener tokener = new JSONTokener(fis);
        JSONObject json = new JSONObject(tokener);
        return json.toString();
    }

    private static void solvePolynomialFromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);

        JSONObject keys = json.getJSONObject("keys");
        int k = keys.getInt("k");

        double[][] A = new double[k][k];
        double[] B = new double[k];

        int row = 0;
        Iterator<String> iter = json.keys();
        while (iter.hasNext() && row < k) {
            String key = iter.next();
            if (key.equals("keys")) continue;

            JSONObject obj = json.getJSONObject(key);
            int base = Integer.parseInt(obj.getString("base"));
            String valueStr = obj.getString("value");
            BigInteger value = new BigInteger(valueStr, base);
            int x = Integer.parseInt(key);

            for (int j = 0; j < k; j++) {
                A[row][j] = Math.pow(x, j);
            }
            B[row] = value.doubleValue();

            row++;
        }

        double[] coeff = gaussianElimination(A, B);

        System.out.println("Polynomial Coefficients:");
        for (int i = 0; i < coeff.length; i++) {
            System.out.println("a" + i + " = " + coeff[i]);
        }
        System.out.println("Constant term (c) = " + coeff[0]);
    }

    public static double[] gaussianElimination(double[][] A, double[] B) {
        int n = B.length;
        for (int i = 0; i < n; i++) {
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) max = j;
            }

            double[] temp = A[i];
            A[i] = A[max];
            A[max] = temp;

            double t = B[i];
            B[i] = B[max];
            B[max] = t;

            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                B[j] -= factor * B[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0;
            for (int j = i + 1; j < n; j++) sum += A[i][j] * x[j];
            x[i] = (B[i] - sum) / A[i][i];
        }
        return x;
    }
}
