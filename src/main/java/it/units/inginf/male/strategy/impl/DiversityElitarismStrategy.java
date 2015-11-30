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
package it.units.inginf.male.strategy.impl;

import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import it.units.inginf.male.configuration.Configuration;
import it.units.inginf.male.generations.Generation;
import it.units.inginf.male.generations.Ramped;
import it.units.inginf.male.objective.Ranking;
import it.units.inginf.male.selections.Selection;
import it.units.inginf.male.tree.Node;
import it.units.inginf.male.utils.UniqueList;
import it.units.inginf.male.utils.Utils;

import java.util.*;

/**
 * Optional accepted parameters:
 * "terminationCriteria", Boolean, then True the termination criteria is enabled when false is disabled, Default value: false
 * "terminationCriteriaGenerations", Integer, number of generations for the termination criteria.Default value: 200  
 * "deepDiversity", Boolean, when false the diversity is imposed only on new generated individuals (then those individuals are merged to the older ones)
 * when true, a new individual is accepted when it is unique thru the current populations and the new generated individuals (more strict condition) 
 * @author MaleLabTs
 */
public class DiversityElitarismStrategy extends DefaultStrategy{
    
    boolean deepDiversity = false;
    
    @Override
    protected void readParameters(Configuration configuration) {
        super.readParameters(configuration); 
         Map<String, String> parameters = configuration.getStrategyParameters();
        if (parameters != null) {
            //add parameters if needed
            if (parameters.containsKey("deepDiversity")) {
                deepDiversity = Boolean.valueOf(parameters.get("deepDiversity"));
            }
        }
    }
    
    @Override
    protected void evolve() {
        int popSize = population.size();
        int oldPopSize = (int) (popSize * 0.9); //oldPopsize are the number of individuals generated from the older population

        List<Node> newPopulation = new UniqueList<>(popSize);
        
        if(deepDiversity){
            newPopulation.addAll(population);
        }
        
        boolean allPerfect = true;
        for (double fitness : rankings.get(0).getFitness()) {
            if (Math.round(fitness * 10000) != 0) {
                allPerfect = false;
                break;
            }
        }
        if (allPerfect) {
            return;
        }        
        

        
        int stepPopSize = deepDiversity? popSize+oldPopSize : oldPopSize;

        Random rng = context.getRandom();
        float crossoverProb = param.getCrossoverProbability();
        Selection sel = this.selection;
        List<Ranking> r = this.rankings;

        while (newPopulation.size() < stepPopSize) {

            double random = rng.nextDouble();

            if (random <= crossoverProb && oldPopSize - newPopulation.size() >= 2) {
                Node selectedA = sel.select(r);
                Node selectedB = sel.select(r);

                Twin<Node> newIndividuals = variation.crossover(selectedA, selectedB, maxCrossoverTries);
                if (newIndividuals != null) {
                    newPopulation.add(newIndividuals.getOne());
                    newPopulation.add(newIndividuals.getTwo());
                }
            } else if (random <= crossoverProb + param.getMutationPobability()) {
                Node mutant = sel.select(r);
                mutant = variation.mutate(mutant);
                newPopulation.add(mutant);
            } else {
                Node duplicated = sel.select(r);
                newPopulation.add(duplicated);
            }
        }

        Generation ramped = new Ramped(maxDepth, context);
        List<Node> generated = ramped.generate(popSize - oldPopSize);
        newPopulation.addAll(generated);
        
        if(!deepDiversity){
            newPopulation.addAll(population);
        }

        //IntObjectHashMap<Ranking> remaining = new IntObjectHashMap(newPopulation.size());
        UnifiedSet<Ranking> remaining = new UnifiedSet(newPopulation.size());
        eachRankings(newPopulation, objective, remaining::add);

        //while (!remaining.isEmpty()) {
        int nextPopSize = (int)Math.ceil(remaining.size() * 0.75);
        while (Utils.getFirstParetoFront(remaining, nextPopSize));
            //sortByFirst(rankings);
        //}


        rankings = remaining.toSortedList(RankingComparator);
//        //rankings = new ArrayList<>(rankings.subList(0, popSize));
//        System.out.print("after: \t" + rankings.get(0) + " ");
//        System.out.println(rankings.get(rankings.size()-1));

        //Obtain an ordinated (as Rankings are) population
        population.clear();
        rankings.forEach(rr -> population.add(rr.getTree()));
    }   

     
}
