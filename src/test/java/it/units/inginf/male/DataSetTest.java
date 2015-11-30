/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.units.inginf.male;

import it.units.inginf.male.inputs.DataSet;
import it.units.inginf.male.inputs.DataSet.Bounds;
import it.units.inginf.male.inputs.DataSet.Example;
import org.junit.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author MaleLabTs
 */
public class DataSetTest {
    

    

    /**
     * Test of initStripedDatasetView method, of class DataSet.
     */
    @Test
    public void testInitStripedDatasetView() {

        DataSet dataSet = getExampleDataSet();

        int marginSize = 2;
        DataSet stripedDataset = dataSet.initStripedDatasetView(marginSize);
        int expExperimentsNumber = 2;
        assertEquals(expExperimentsNumber, stripedDataset.getNumberExamples());
        for(Example stripedExample : stripedDataset.getExamples()){
            stripedExample.populateAnnotatedStrings();
            assertEquals("PROVA".length()*(marginSize+1), stripedExample.getString().length());
            for(String matchString : stripedExample.getMatchedStrings()){
                assertEquals("PROVA", matchString);
            }
        }

        Example example = dataSet.getExample(0);
        
        //Test the boundires merge operation
        marginSize = 20;
        stripedDataset = dataSet.initStripedDatasetView(marginSize);
        expExperimentsNumber = 1;
        assertEquals(expExperimentsNumber, stripedDataset.getNumberExamples());
        for(Example stripedExample : stripedDataset.getExamples()){
            stripedExample.populateAnnotatedStrings();
            //Example should be unaltered
            assertEquals(example.getString(), stripedExample.getString());
            for(String matchString : stripedExample.getMatchedStrings()){
                assertEquals("PROVA", matchString);
            }
            assertArrayEquals(example.getMatch().toArray(), stripedExample.getMatch().toArray());
        }
    }

    public static DataSet getExampleDataSet() {
        DataSet dataSet = new DataSet("test", "striping test", "");
        Example example = new Example();
        example.setString("123456789123456789PROVA123456789123456789 123456789123456789123456789123456789PROVA123456789123456789");
        int provaIndex1 = example.getString().indexOf("PROVA");
        int provaIndex2 = example.getString().indexOf("PROVA", provaIndex1+2);
        example.getMatch().add(new Bounds(provaIndex1,provaIndex1+"PROVA".length()));
        example.getMatch().add(new Bounds(provaIndex2,provaIndex2+"PROVA".length()));
        dataSet.getExamples().add(example);
        dataSet.updateStats();
        return dataSet;
    }

    public static DataSet getExampleDataSetRewrite(String input, String output) {
        DataSet dataSet = new DataSet("test", "striping test", "");
        Example example = new Example();

        String noise = "dsfsdfs6789 ";
        String noise2 = " 283rh9238r3r";

        example.setString(noise + input + noise2 + output + noise);
        int provaIndex1 = example.getString().indexOf(input);
        int provaIndex2 = example.getString().indexOf(output, provaIndex1+1);
        example.getMatch().add(new Bounds(provaIndex1,provaIndex1+ input.length()));
        example.getMatch().add(new Bounds(provaIndex2,provaIndex2+ output.length()));

        dataSet.getExamples().add(example);
        dataSet.updateStats();
        return dataSet;
    }


}
