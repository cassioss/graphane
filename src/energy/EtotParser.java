package energy;

import basics.BasicParser;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * Parser for Etot (total energy of cells) obtained during convergence simulations.
 *
 * @author Cassio dos Santos Sousa
 * @version 1.0
 */
public class EtotParser extends BasicParser {

    private static final String basicPath = "res/etot/results/";
    private static final String extension = ".dat";

    /**
     * Gets the cell energy for each iteration, out of the parsed double array of Etot.
     *
     * @param parsedEtot a double String array out of an Etot.
     * @return a list of double precision values for the energy of the cell.
     */
    public static List<Double> energyList(String[][] parsedEtot) {
        List<Double> energyList = new ArrayList<>();
        for (String[] aParsedEtot : parsedEtot) energyList.add(Double.valueOf(aParsedEtot[5]));
        return energyList;
    }

    public static List<String> parameterList(String[][] parsedEtot) {
        List<String> parameterList = new ArrayList<>();
        for (String[] aParsedEtot : parsedEtot) parameterList.add(aParsedEtot[0]);
        return parameterList;
    }

    /**
     * A union of the parsing methods.
     *
     * @param cellCode the cell code.
     * @return a list of double precision values for the energy of the cell.
     */
    public static List<Double> energyList(String cellCode) {
        return energyList(initialParsing(basicPath + cellCode + extension));
    }

    public static List<String> parameterList(String cellCode) {
        return parameterList(initialParsing(basicPath + cellCode + extension));
    }

    /**
     * Gets the smallest energy from the Etot. It does not check for global minimum.
     *
     * @param energyList a list containing the energy values.
     * @return a double precision value containing the smallest energy of the iteration.
     */
    public static double smallestEnergy(List<Double> energyList) {
        double smallestEnergy = energyList.get(0);
        for (double energy : energyList)
            if (smallestEnergy > energy)
                smallestEnergy = energy;
        return smallestEnergy;
    }

    public static double smallestEnergy(String cellCode) {
        return smallestEnergy(energyList(cellCode));
    }

    public static String idealParameterFor(String cellCode) {
        List<Double> energyList = energyList(cellCode);
        double smallestEnergy = smallestEnergy(energyList);
        int index = energyList.indexOf(smallestEnergy);
        return parameterList(cellCode).get(index);
    }

    /**
     * Checks if the smallest energy of the iterations is a global minimum. This means that the convergence
     * is prone to have occurred as expected.
     *
     * @param energyList a list containing the energy values.
     * @return <em>true</em> if the energy is a global minimum.
     */
    public static boolean isGlobalMinimum(List<Double> energyList) {
        double smallestEnergy = smallestEnergy(energyList);
        int itsIndex = energyList.indexOf(smallestEnergy);
        for (int i = 1; i < energyList.size() - 1; i++) {
            double current = energyList.get(i);
            if (i < itsIndex) {
                double previous = energyList.get(i - 1);
                if (previous < current)
                    return false;
            } else {
                double next = energyList.get(i + 1);
                if (next < current)
                    return false;
            }
        }
        return true;
    }

    public static boolean isGlobalMinimum(String cellCode) {
        return isGlobalMinimum(energyList(cellCode));
    }

}
