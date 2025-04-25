import com.google.gson.*;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {
        String[] inputFiles = {"input1.json", "input2.json"};

        for (String filename : inputFiles) {
            // Parse JSON
            try (FileReader reader = new FileReader(filename)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
                JsonObject meta = root.getAsJsonObject("keys");

                int threshold = meta.get("k").getAsInt();

                List<BigInteger> xCoords = new ArrayList<>();
                List<BigInteger> yCoords = new ArrayList<>();

                for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                    if (entry.getKey().equals("keys")) continue;

                    int x = Integer.parseInt(entry.getKey());
                    JsonObject point = entry.getValue().getAsJsonObject();
                    int base = point.get("base").getAsInt();
                    String yValue = point.get("value").getAsString();

                    xCoords.add(BigInteger.valueOf(x));
                    yCoords.add(new BigInteger(yValue, base));

                    if (xCoords.size() == threshold) break;
                }

                BigInteger reconstructedSecret = interpolateAtZero(xCoords, yCoords);
                System.out.println("Secret for " + filename + " is: " + reconstructedSecret);
            }
        }
    }

    // Lagrange interpolation at x = 0
    private static BigInteger interpolateAtZero(List<BigInteger> xCoords, List<BigInteger> yCoords) {
        BigInteger secret = BigInteger.ZERO;
        int size = xCoords.size();

        for (int i = 0; i < size; i++) {
            BigInteger term = yCoords.get(i);

            for (int j = 0; j < size; j++) {
                if (i == j) continue;

                BigInteger numerator = xCoords.get(j).negate(); // 0 - xj
                BigInteger denominator = xCoords.get(i).subtract(xCoords.get(j));
                term = term.multiply(numerator).divide(denominator);
            }

            secret = secret.add(term);
        }

        return secret;
    }
}
