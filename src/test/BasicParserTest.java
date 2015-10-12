package test;

import basics.BasicParser;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit testing for BasicParser.
 *
 * @author Cassio dos Santos Sousa
 * @version 1.0
 */
public class BasicParserTest {

    private String[][] parsedEtot;
    private String[][] parsedPOSCAR;

    @Before
    public void setParsing() {
        String filePathEtot = "res/etot/results/0000.dat";
        String filePathPOSCAR = "res/poscar/POSCAR0000";
        parsedEtot = BasicParser.initialParsing(filePathEtot);
        parsedPOSCAR = BasicParser.initialParsing(filePathPOSCAR);
    }

    @Test
    public void testInitialEtotParsing() {
        assert parsedEtot.length == 17;
        assert parsedEtot[0].length == 9;
    }

    @Test
    public void testInitialPOSCARParsing() {
        assert parsedPOSCAR.length == 17;
        assert parsedPOSCAR[0].length == 1;
        assert parsedPOSCAR[1].length == 1;
        assert parsedPOSCAR[2].length == 3;
        assert parsedPOSCAR[5].length == 1;
        assert parsedPOSCAR[6].length == 1;
        assert parsedPOSCAR[7].length == 2;
        assert parsedPOSCAR[8].length == 1;
        assert parsedPOSCAR[9].length == 6;
        assert parsedPOSCAR[13].length == 3;
    }

}