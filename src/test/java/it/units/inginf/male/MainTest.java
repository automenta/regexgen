package it.units.inginf.male;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.units.inginf.male.dto.SimpleConfig;
import it.units.inginf.male.inputs.DataSet;
import it.units.inginf.male.outputs.Results;
import org.junit.Test;

/**
 * Created by me on 11/29/15.
 */
public class MainTest {

    /** JSON example from wiki: https://github.com/MaLeLabTs/RegexGenerator/wiki/Annotated-Dataset */
    String j = "{\n" +
            "  \"name\": \"Log/MAC\",\n" +
            "  \"description\": \"\",\n" +
            "  \"regexTarget\": \"\",\n" +
            "  \"examples\": [\n" +
            "    {\n" +
            "    \"string\": \"Jan 12 06:26:19: ACCEPT service http from 119.63.193.196 to firewall(pub-nic), prefix: \\\"none\\\" (in: eth0 119.63.193.196(5c:0a:5b:63:4a:82):4399 -> 140.105.63.164(50:06:04:92:53:44):80 TCP flags: ****S* len:60 ttl:32)\",\n" +
            "    \"match\": [\n" +
            "        { “start\": 119, \"end\": 136 },\n" +
            "        { \"start\": 161, \"end\": 178 }\n" +
            "    ],\n" +
            "    \"unmatch\": [\n" +
            "        {\"start\": 0,\"end\": 119},\n" +
            "        {\"start\": 136,\"end\": 161},\n" +
            "        {\"start\": 178,\"end\": 215}\n" +
            "    ]\n" +
            "    } ] }";

    @Test
    public void test1() throws Exception {
        run(DataSetTest.getExampleDataSet());
    }
    @Test
    public void test2() throws Exception {
        run(DataSetTest.getExampleDataSetRewrite("xyzabc", "abcxyz"));
    }

    static final Gson resultOutput = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().create();

    private void run(DataSet d) throws Exception {
        Results r = Main.run(new SimpleConfig(
                d, 100, 5000)
                .buildConfiguration()
        );


        System.out.println(resultOutput.toJson( r.getBestSolution()) );
        System.out.println(resultOutput.toJson(r.getBestExtractionsStats()));


        //Configurator.configure(j));
    }

}