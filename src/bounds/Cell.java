package bounds;

import basics.BasicParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds the length of each bound in a VASP cell.
 *
 * @author Cassio dos Santos Sousa
 * @version 1.0
 */
public class Cell {

    private static final String pathBeginning = "res/poscar/POSCAR";
    private static final double tolerance = 0.8;
    private String code;
    private int numberOfH;
    private static final int numberOfC = 4;
    private double latticeParameter;
    private double[][] latticeVectors;
    private double[][] directCoordinates;
    private double[][] cartesianCoordinates;
    private double[][] twoRelevantAtoms;
    private List<Double> relevantDistances;

    public Cell(String code) {
        this.code = code;
        String[][] parsedPOSCAR = BasicParser.initialParsing(pathBeginning + code);
        setCell(parsedPOSCAR);
    }

    private void setCell(String[][] parsedPOSCAR) {
        setLatticeParameter(parsedPOSCAR);
        setLatticeVectors(parsedPOSCAR);
        setNumberOfH(parsedPOSCAR);
        setDirectCoordinates(parsedPOSCAR);
        setPreCartesianCoordinates();
        adjustBiggerVectors();
        setTwoRelevantAtoms();
        calculateDistances();
    }

    private void setLatticeParameter(String[][] parsedPOSCAR) {
        latticeParameter = Double.valueOf(parsedPOSCAR[1][0]);
    }

    private void setLatticeVectors(String[][] parsedPOSCAR) {
        latticeVectors = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                latticeVectors[i][j] = Double.valueOf(parsedPOSCAR[2 + i][j]);
            }
        }
    }

    private void setNumberOfH(String[][] parsedPOSCAR) {
        if (parsedPOSCAR[6].length == 1)
            numberOfH = 0;
        else
            numberOfH = Integer.valueOf(parsedPOSCAR[6][1]);
    }

    private void setDirectCoordinates(String[][] parsedPOSCAR) {
        directCoordinates = new double[numberOfC + numberOfH][3];
        for (int i = 0; i < numberOfC + numberOfH; i++) {
            for (int j = 0; j < 3; j++)
                directCoordinates[i][j] = adjusted(Double.valueOf(parsedPOSCAR[9 + i][j]));
        }
    }

    private double adjusted(double direct) {
        return direct > tolerance ? direct - 1.0 : direct;
    }

    private void setPreCartesianCoordinates() {
        cartesianCoordinates = new double[numberOfC + numberOfH][3];
        for (int cellID = 0; cellID < numberOfC + numberOfH; cellID++) {
            for (int coord = 0; coord < 3; coord++) {
                cartesianCoordinates[cellID][coord] = latticeVectors[0][coord] * directCoordinates[cellID][0] + latticeVectors[1][coord] * directCoordinates[cellID][1] + latticeVectors[2][coord] * directCoordinates[cellID][2];
            }
        }
    }

    private void adjustBiggerVectors() {
        for (int id = 0; id < numberOfC + numberOfH; id++) {
            for (int coord = 0; coord < 3; coord++) {
                if (cartesianCoordinates[id][coord] > tolerance * latticeVectors[coord][coord])
                    subtractLatticeVectorAt(id, coord);
                cartesianCoordinates[id][coord] *= latticeParameter;
            }
        }
    }

    private void subtractLatticeVectorAt(int id, int coord) {
        for (int i = 0; i < 3; i++)
            cartesianCoordinates[id][i] -= latticeVectors[coord][i];
    }

    private void setTwoRelevantAtoms() {
        twoRelevantAtoms = new double[2][3];
        twoRelevantAtoms[0][0] = cartesianCoordinates[2][0] + latticeParameter * (latticeVectors[1][0] - latticeVectors[0][0]);
        twoRelevantAtoms[0][1] = cartesianCoordinates[2][1] + latticeParameter * (latticeVectors[1][1]);
        twoRelevantAtoms[0][2] = cartesianCoordinates[2][2];
        twoRelevantAtoms[1][0] = cartesianCoordinates[3][0] + latticeParameter * (latticeVectors[1][0]);
        twoRelevantAtoms[1][1] = cartesianCoordinates[3][1] + latticeParameter * (latticeVectors[1][1]);
        twoRelevantAtoms[1][2] = cartesianCoordinates[3][2];
    }

    private void calculateDistances() {
        relevantDistances = new ArrayList<>();
        calculateForCarbons();
        calculateForHydrogens();
    }

    private void calculateForCarbons() {
        addDistance(cartesianCoordinates[0], cartesianCoordinates[3]);
        addDistance(cartesianCoordinates[1], cartesianCoordinates[2]);
        addDistance(cartesianCoordinates[0], twoRelevantAtoms[0]);
        addDistance(cartesianCoordinates[1], twoRelevantAtoms[1]);
    }

    private void calculateForHydrogens() {
        int iterator = 0;
        if (numberOfH > 0) {
            for (int i = 0; i < code.length(); i++) {
                if (Integer.valueOf(String.valueOf(code.charAt(i))) != 0) {
                    addDistance(cartesianCoordinates[i], cartesianCoordinates[numberOfC + iterator]);
                    iterator++;
                }
            }
        }
    }

    private void addDistance(double[] vec1, double[] vec2) {
        relevantDistances.add(distance(vec1, vec2));
    }

    private double distance(double[] pos1, double[] pos2) {
        double result = 0.0;
        for (int i = 0; i < pos1.length; i++)
            result += (pos1[i] - pos2[i]) * (pos1[i] - pos2[i]);
        return Math.sqrt(result);
    }

    private void printDistances() {
        for (double distance : relevantDistances) {
            System.out.printf("%.5f\n", distance);
        }
    }

    public List<Double> getRelevantDistances() {
        return relevantDistances;
    }

    public int getNumberOfH() {
        return numberOfH;
    }

    public static void main(String[] args) {
        Cell graphene = new Cell("1122");
        graphene.printDistances();
    }

}
