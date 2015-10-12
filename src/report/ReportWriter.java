package report;

import bounds.Cell;
import energy.EtotParser;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * Writes a thorough report for each converged cell.
 *
 * @author Cassio dos Santos Sousa
 * @version 1.0
 */
public class ReportWriter {

    private List<String> allCellCodes, allDegeneracies;

    public void run() {
        readInput();
        writeOutput();
    }

    public void readInput() {
        allCellCodes = new ArrayList<>();
        allDegeneracies = new ArrayList<>();
        String inputFilePath = "res/io/current_entry.txt";
        File inputFile = new File(inputFilePath);
        try {
            Scanner sc = new Scanner(inputFile);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.trim().isEmpty()) {
                    String[] splitLine = line.split("\\s+");
                    allCellCodes.add(splitLine[0]);
                    allDegeneracies.add(splitLine[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void writeOutput() {
        String outputFilePath = "res/io/output.txt";
        File outputFile = new File(outputFilePath);
        try {
            FileWriter fw = new FileWriter(outputFile);
            BufferedWriter bw = new BufferedWriter(fw);
            writeContent(bw);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void writeContent(BufferedWriter bw) throws IOException {
        bw.write("=============================================\n");
        bw.write("Results for the convergence of graphane cells\n");
        bw.write("=============================================\n\n");
        bw.write(commonParameters());
        bw.write("=================================\n");
        bw.write("2. Detailed results for each cell\n");
        bw.write("=================================\n\n");
        for (int i = 0; i < allCellCodes.size(); i++)
            writeContentForCell(bw, allCellCodes.get(i), i);
    }

    private String commonParameters() throws IOException {
        String common = "==================================\n";
        common += "1. Common parameters for all cells\n";
        common += "==================================\n\n";
        common += "===========\n1.1 KPOINTS\n===========\n\n";
        common += allLinesFrom("KPOINTS") + "\n\n";
        common += "=========\n1.2 INCAR\n=========\n\n";
        common += allLinesFrom("INCAR") + "\n\n";
        return common;
    }

    private String allLinesFrom(String fileName) throws IOException {
        File file = new File("res/io/" + fileName);
        return new Scanner(file).useDelimiter("\\Z").next();
    }

    private String allLinesFromPOSCAR(String cellCode) throws IOException {
        return allLinesFrom("../poscar/POSCAR" + cellCode) + "\n\n";
    }

    private void writeContentForCell(BufferedWriter bw, String cellCode, int index) throws IOException {
        bw.write("#########\n");
        bw.write("Cell " + cellCode + "\n");
        bw.write("#########\n\n");
        bw.write("Degeneracy: " + allDegeneracies.get(index) + "\n\n");
        bw.write("Composition: " + composition(cellCode));
        bw.write("===========================\n");
        bw.write("Energy convergence results:\n");
        bw.write("===========================\n\n" + etotResults(cellCode));
        bw.write("================\n");
        bw.write("Converged POSCAR\n");
        bw.write("================\n\n" + allLinesFromPOSCAR(cellCode));
        bw.write("===========================\n");
        bw.write("Bond lengths (in angstroms)\n");
        bw.write("===========================\n\n" + bondLengths(cellCode));
    }

    private String composition(String cellCode) {
        String composition = "4C";
        int oneCounter = 0;
        int twoCounter = 0;
        if (!Objects.equals(cellCode, "0000")) {
            for (char c : cellCode.toCharArray()) {
                if (c == '1')
                    oneCounter++;
                else if (c == '2')
                    twoCounter++;
            }
            int bothCounter = oneCounter + twoCounter;
            composition += " " + bothCounter + "H\n";
            if (twoCounter != 0) {
                composition += oneCounter + " above the graphene plane\n";
                composition += twoCounter + " below the graphene plane\n";
            }
        } else composition += "\n";
        return composition + "\n";
    }

    private String etotResults(String cellCode) {
        List<Double> energyList = EtotParser.energyList(cellCode);
        List<String> parameterList = EtotParser.parameterList(cellCode);
        Double smallest = EtotParser.smallestEnergy(cellCode);
        boolean expected = EtotParser.isGlobalMinimum(cellCode);
        String etot = "==========================================================\n";
        etot += "Energies (in eV) for each lattice parameter (in angstroms)\n";
        etot += "==========================================================\n\n";
        for (int i = 0; i < energyList.size(); i++)
            etot += parameterList.get(i) + " " + energyList.get(i).toString() + "\n";
        etot += "\nSmallest energy: " + smallest.toString() + " eV\n";
        etot += "\nIdeal parameter: " + EtotParser.idealParameterFor(cellCode) + " Å\n\n";
        etot += "Energy curve behaved as expected: ";
        etot += expected ? "Yes\n\n" : "No\n\n";
        return etot;
    }

    private String bondLengths(String cellCode) {
        String bond = "C-C bond lengths:\n\n";
        Cell cell = new Cell(cellCode);
        List<Double> cellLengths = cell.getRelevantDistances();
        int cellHs = cell.getNumberOfH();
        for (int i = 0; i < 4; i++)
            bond += cellLengths.get(i).toString() + "\n";
        bond += "\nAverage C-C bond length: ";
        bond += averageLengthString(cellLengths, 0, 4) + " Å\n";
        if (cellHs > 0) {
            bond += "\nC-H bond length(s):\n\n";
            for (int i = 0; i < cellHs; i++)
                bond += cellLengths.get(4 + i).toString() + "\n";
            bond += "\nAverage C-H bond length: ";
            bond += averageLengthString(cellLengths, 4, 4 + cellHs) + " Å\n";
        }
        return bond + "\n";
    }

    private Double averageLength(List<Double> cellLengths, int beginning, int end) {
        Double average = 0.0;
        for (int i = beginning; i < end; i++)
            average += cellLengths.get(i);
        return average / (1.0 * (end - beginning));
    }

    private String averageLengthString(List<Double> cellLengths, int beginning, int end){
        Double average = averageLength(cellLengths, beginning, end);
        return String.format("%.3f", average);
    }

    public static void main(String[] args) {
        ReportWriter rw = new ReportWriter();
        rw.run();
    }

}
