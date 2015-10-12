package test;

import org.junit.*;
import energy.EtotParser;

import java.util.List;

/**
 * Unit testing for EtotParser.
 *
 * @author Cassio dos Santos Sousa
 * @version 1.0
 */
public class EtotParserTest {

    private List<Double> energyList;
    private List<Double> problematicEnergyList;

    @Before
    public void setParsing() {
        String filePath = "0000";
        String problematicFilePath = "../tests/0000problematic";
        energyList = EtotParser.energyList(filePath);
        problematicEnergyList = EtotParser.energyList(problematicFilePath);
    }

    @Test
    public void testEnergyList() {
        assert energyList.size() == 17;
        assert energyList.get(0) == -40.400908;
    }

    @Test
    public void testSmallestEnergy() {
        assert EtotParser.smallestEnergy(energyList) == -40.410485;
    }

    @Test
    public void testGlobalMinimumTrue() {
        assert EtotParser.isGlobalMinimum(energyList);
    }

    @Test
    public void testGlobalMinimumFalse() {
        assert !EtotParser.isGlobalMinimum(problematicEnergyList);
    }

}