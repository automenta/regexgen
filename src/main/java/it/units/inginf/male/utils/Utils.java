/*
 * Copyright (C) 2015 Machine Learning Lab - University of Trieste, 
 * Italy (http://machinelearning.inginf.units.it/)  
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.units.inginf.male.utils;

import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.primitive.CharSet;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.mutable.primitive.CharHashSet;
import it.units.inginf.male.inputs.DataSet;
import it.units.inginf.male.inputs.DataSet.Bounds;
import it.units.inginf.male.objective.Ranking;
import it.units.inginf.male.tree.Node;
import it.units.inginf.male.tree.RegexRange;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MaleLabTs
 */
public class Utils {


    /**
     * character to a digit, or -1 if it wasnt a digit
     */
    public static int i(final char c) {
        if ((c >= '0' && c <= '9'))
            return c - '0';
        return -1;
    }

    /**
     * fast parse a non-negative int under certain conditions, avoiding Integer.parse if possible
     *
     */
    public static int i(final String s, int ifMissing) {
        switch (s.length()) {
            case 0: return ifMissing;
            case 1: return i1(s, ifMissing);
            case 2: return i2(s, ifMissing);
            case 3: return i3(s, ifMissing);
            default:
                try {
                    return Integer.parseInt(s);
                }
                catch (NumberFormatException e) {
                    return ifMissing;
                }
        }
    }

    private static int i3(String s, int ifMissing) {
        int dig100 = i(s.charAt(0));
        if (dig100 == -1) return ifMissing;

        int dig10 = i(s.charAt(1));
        if (dig10 == -1) return ifMissing;

        int dig1 = i(s.charAt(2));
        if (dig1 == -1) return ifMissing;

        return dig100 * 100 + dig10 * 10 + dig1;
    }

    private static int i2(String s, int ifMissing) {
        int dig10 = i(s.charAt(0));
        if (dig10 == -1) return ifMissing;

        int dig1 = i(s.charAt(1));
        if (dig1 == -1) return ifMissing;

        return dig10 * 10 + dig1;
    }

    private static int i1(String s, int ifMissing) {
        int dig1 = i(s.charAt(0));
        if (dig1 != -1) return ifMissing;
        return dig1;
    }

    public static float[] calculateMeanFitness(List<Ranking> population) {
        float[] out = new float[population.get(0).getFitness().length];
        for (Ranking r : population) {
            double[] fitness = r.getFitness();
            for (int i = 0; i < out.length; i++) {
                out[i] += fitness[i];
            }
        }
        for (int i = 0; i < out.length; i++) {
            out[i] /= population.size();
        }
        return out;
    }

    public static boolean isAParetoDominateByB(double fitnessA[], double fitnessB[]) {
        final int n = fitnessA.length;
        boolean dominate = false;
        for (int i = 0; i < n; i++) {
            double a = fitnessA[i];
            double b = fitnessB[i];

            if (a > b)
                dominate = true;
            else if (a < b)
                return false;
        }
        return dominate;
    }

//    public static void getFirstParetoFront(List<Ranking> tmp, Consumer<Ranking> each) {
//
//    }


    /** return if it will be necessary to call again */
    public static MutableMap<Node,double[]> getFirstParetoFront(MutableMap<Node,double[]> r, int targetSize /*, Consumer<Ranking> withWinner*/) {

        if (r.size() <= targetSize)
            return r;

        List<Node> toRemove = new FastList(0);

        r.forEachKeyValue((n1,f1) -> {

            if (r.anySatisfy(f2 -> {
                if (f1==f2)
                    return false;

                return Utils.isAParetoDominateByB(f1, f2);
            })) {
                //n2 dominates n1
                toRemove.add(n1);
            }
        });

        if (toRemove.size() > 0) {
            return r.withoutAllKeys(toRemove);
        }

        return r;
    }

    public static String cpuInfo() throws IOException {
        if (!System.getProperty("os.name").toLowerCase().contains("linux")) {
            return "Unaviable";
        }
        FileInputStream fis = new FileInputStream(new File("/proc/cpuinfo"));
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.matches("model name.*")) {
                bufferedReader.close();
                return line.replace("model name	: ", "");
            }
        }
        return "";
    }

    public static double diversity(Collection<Ranking> population) {
        Set<String> tmp = new UnifiedSet(population.size());
        for (Ranking r : population) {
            tmp.add(r.getDescription());
        }
        return 100 * tmp.size() / (double) population.size();
    }

    //remove empty extractions 
    public static void removeEmptyExtractions(List<DataSet.Bounds> extractions) {
        for (Iterator<Bounds> it = extractions.iterator(); it.hasNext();) {
            Bounds bounds = it.next();
            if (bounds.size() == 0) {
                it.remove();
            }
        }
    }

    public static void saveFile(String text, String pathOfFile) {
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(pathOfFile), "utf-8");
            writer.write(text);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, "Cannot save:", ex);
        }
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private static transient final CharHashSet quoteList = new CharHashSet(
            '?', '+', '*', '.', '[', ']', '\\', '$', '(', ')', '^', '{', '|', '-', '&');


    /**
     * Returns a set with all n-grams; 1<n<4
     * @param word
     * @return
     */
    public static Set<String> subparts(String word) {
        return Utils.subparts(word,1,4);
    }

    /**
     * Returns a set with all n-grams; nMin<=n<=nMax
     * @param word
     * @return
     */
    public static Set<String> subparts(String word, int nMin, int nMax) {

        final int x = word.length() * (nMax-nMin);

        Set<String> subparts = new HashSet<>(x);

        for (int n = nMin; n <= nMax; n++) {
            for (int i = 0; i < word.length(); i++) {

                int end = Math.min(i + n, word.length());

                StringBuilder builder = new StringBuilder(end-i /* estimate */);
                for (int c = i; c < end; c++) {
                    builder.append(escape(word.charAt(c)));
                }
                subparts.add(builder.toString());
            }
        }
        return subparts;
    }
    
    public static String escape(char c) {
        if (quoteList.contains(c)) {
            return ("\\" + c);
        }
        return (String.valueOf(c));
    }
    
    public static String escape(String string){
        StringBuilder stringBuilder = new StringBuilder(string.length());
        char[] stringChars = string.toCharArray();
        for(char character : stringChars){
            stringBuilder.append(escape(character));
        }
        return stringBuilder.toString();
    }

    /**
     * Generates RegexRanges i.e. [a-z] from contiguous characters into the <code>charset</code> list.
     * Follows example where output is represented with regex strings:
     * When <code>charset</code> is {a,b,g,r,t,u,v,5}, the return value is {[a-b],[t-v]}.
     * @param charset the character list i.e. {a,b,g,r,t,u,v,5}
     * @return the contiguous character ranges i.e. {[a-b],[t-v]}
     */
    public static void generateRegexRanges(CharSet charset, Consumer<RegexRange> each) {
         

        char[] cc = charset.toSortedArray();
        //TreeSet<Character> orderedCharset = new CharSet(charset);
        char start;
        char old;
        char first = cc[0];
        char last = cc[cc.length-1];

        old = start = first;

        for (int i = 1; i < cc.length; i++) {
            char c = cc[i];

            //when we have an "hole" or is the last character it checks if the previous range (old -start) is larger than 1;
            //Ranges bigger than 1 char are saved
            if (((c - old) > 1 || last == c)) {
                if ((old - start) > 1) {
                    each.accept(
                        new RegexRange(escape(start) + '-' + escape(old))
                    );
                }
                start = c;
            }
            old = c;
        }
    }
}
