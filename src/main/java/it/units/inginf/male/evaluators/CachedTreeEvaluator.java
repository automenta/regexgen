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

import it.units.inginf.male.inputs.Context;
import it.units.inginf.male.inputs.Context.EvaluationPhases;
import it.units.inginf.male.inputs.DataSet.Bounds;
import it.units.inginf.male.tree.Node;
import it.units.inginf.male.utils.Triplet;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 *
 * @author MaleLabTs
 */
public class CachedTreeEvaluator extends DefaultTreeEvaluator implements CachedEvaluator{

    private final Map<Triplet<EvaluationPhases, Boolean, String>, List<Bounds[]>> cache = new WeakHashMap<>();
    private long hit = 0;
    private long miss = 0;

    @Override
    public List<Bounds[]> evaluate(Node root, Context context)  {

        StringBuilder sb = new StringBuilder();
        root.describe(sb);

        Triplet<EvaluationPhases, Boolean, String> key = new Triplet<>(context.getPhase(), context.isStripedPhase(), sb.toString());
        synchronized (cache) {
            return cache.compute(key, (k, res) -> {
                if (res!=null) {
                    hit++;
                    return res;
                }
                else {
                    miss++;
                    try {
                        return CachedTreeEvaluator.super.evaluate(root, context);
                    }
                    catch (TreeEvaluationException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            });
        }

        //return results;
    }

    @Override
    public double getRatio(){
        return (double)this.hit/(this.hit+this.miss);
    }
    
    @Override
    public long getCacheSizeBytes(){
        synchronized (cache) {
            long cacheSize = 0;
            for (Map.Entry<Triplet<EvaluationPhases, Boolean, String>, List<Bounds[]>> entry : cache.entrySet()) {
                List<Bounds[]> list = entry.getValue();
                for (Bounds[] exampleResult : list) {
                    cacheSize+=exampleResult.length;
                }            
            }
            cacheSize*=(Integer.SIZE/4);
            return cacheSize;
        }
    }
}
