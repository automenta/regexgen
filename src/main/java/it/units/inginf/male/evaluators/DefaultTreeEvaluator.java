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
package it.units.inginf.male.evaluators;

import com.gs.collections.impl.list.mutable.FastList;
import it.units.inginf.male.inputs.Context;
import it.units.inginf.male.inputs.DataSet.Bounds;
import it.units.inginf.male.inputs.DataSet.Example;
import it.units.inginf.male.tree.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 *
 * @author MaleLabTs
 */
public class DefaultTreeEvaluator implements TreeEvaluator {

    @Override
    public List<List<Bounds>> evaluate(Node root, Context context) throws TreeEvaluationException {

        List<List<Bounds>> results = new ArrayList<>(context.getCurrentDataSetLength());

        StringBuilder sb = new StringBuilder();
        root.describe(sb);

        try {
             
            Pattern regex = Pattern.compile(sb.toString());
            Matcher matcher = regex.matcher("");

            context.getCurrentDataSet().
                getExamples().
                    forEach(e ->
                        eval(results, matcher, e));

        } catch (PatternSyntaxException ex) {
            throw new TreeEvaluationException(ex);
        }
        return results;
    }

    private static void eval(List<List<Bounds>> results, Matcher matcher, Example example) {
        try {
            Matcher m = matcher.reset(example.getString());
            List<Bounds> b = new FastList<>();
            while (m.find()) {
                b.add(new Bounds(matcher.start(0), matcher.end(0)));
            }
            results.add(b);
        } catch (StringIndexOutOfBoundsException ex) {
            /**
             * Workaround: ref BUG: 6984178
             * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6984178
             * with greedy quantifiers returns exception
             * instead than "false".
             */
            results.add(Collections.<Bounds>emptyList());
        }
    }

    @Override
    public void setup(Map<String, String> parameters) {
    }
}
