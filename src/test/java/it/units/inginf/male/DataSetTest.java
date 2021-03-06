/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.units.inginf.male;

import it.units.inginf.male.inputs.DataSet;
import it.units.inginf.male.inputs.DataSet.Bounds;
import it.units.inginf.male.inputs.DataSet.Example;
import org.junit.Test;

import java.util.Base64;
import java.util.Random;
import java.util.function.Supplier;

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
        int provaIndex2 = example.getString().indexOf("PROVA", provaIndex1+3);
        example.getMatch().add(new Bounds(provaIndex1,provaIndex1+"PROVA".length()));
        example.getMatch().add(new Bounds(provaIndex2,provaIndex2+"PROVA".length()));
        dataSet.getExamples().add(example);
        dataSet.updateStats();
        return dataSet;
    }

    public static String noise(int len) {
        byte[] b = new byte[len];
        Random rng = new Random();
        for (int i = 0; i < len; i++) {
            b[i] = (byte)rng.nextInt(255);
        }
        return Base64.getEncoder().encodeToString(b);
    }

    public static DataSet getExampleDataSet2(Supplier<String> noise, String... input) {
        DataSet dataSet = new DataSet("test", "striping test", "");
        Example example = new Example();

        int i = 0;
        String j = "";

        for (String x : input) {

            //TODO noise as lambda param
            String n = noise.get();  j += n; i+= n.length();

            int a = i;
            j += x; i += x.length();

            example.getMatch().add(new Bounds(a, i));
        }

        j += noise.get();

        System.out.println(j);
        example.setString(j);

        //example.getUnmatch().add(new Bounds(provaIndex2,provaIndex2+ input.length()));

        dataSet.getExamples().add(example);


        dataSet.updateStats();
        return dataSet;
    }


}
