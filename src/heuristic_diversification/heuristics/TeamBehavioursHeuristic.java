package heuristic_diversification.heuristics;

import core.game.Game;
import core.game.StateObservation;
import core.heuristic.StateHeuristic;

import java.awt.*;
import java.util.ArrayList;

public class TeamBehavioursHeuristic extends StateHeuristic {
    private ArrayList<StateHeuristic> mHeuristics;
    private ArrayList<Double> mHeuristicsWeights;

    public TeamBehavioursHeuristic(ArrayList<StateHeuristic> heuristics, ArrayList<Double> weights){
        // We assume the number of heuristics and their weights are the same and provided in the same order
        mHeuristics = heuristics;
        setHeuristicsWeights(weights);
    }

    @Override
    public void initHeuristicInternalInformation(StateObservation stateObs) {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.initHeuristicInternalInformation(stateObs);   
        }
    }

    @Override
    public void updateHeuristicInternalInformation(StateObservation stateObs) {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.updateHeuristicInternalInformation(stateObs);
        }
    }

    @Override
    public double evaluateState(StateObservation stateObs) {
        double finalH = 0;

        if (!checkHeuristicsArraySize()){
            // Avoid error in the iteration
            return 0;
        }

        for (int i = 0; i < mHeuristics.size(); i++) {
            StateHeuristic heuristic = mHeuristics.get(i);
            double h = heuristic.evaluateState(stateObs);
            double hNorm = heuristic.normaliseHeuristic(h);

            // add the heuristic value based on its weight to the final result
            double hWeight = mHeuristicsWeights.get(i);
            finalH += (hNorm * hWeight); 
        }

        return finalH;
    }

    @Override
    public void restartFutureStateData() {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.restartFutureStateData();;
        }
    }

    @Override
    public void updateFutureStateData(StateObservation stateObs) {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.updateFutureStateData(stateObs);
        }
    }

    @Override
    public String relevantInfoStr(StateObservation stateObs) {
        String s = "";
        for (StateHeuristic heuristic : mHeuristics) {
            s.concat(heuristic.relevantInfoStr(stateObs)+"\n");
        }
        return s;
    }

    @Override
    public void recordDataOnFile(Game played, String fileName, int randomSeed, int[] recordIds) {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.recordDataOnFile(played, fileName, randomSeed, recordIds);
        }
    }

    @Override
    public void drawInScreen(Graphics2D g) {
        for (StateHeuristic heuristic : mHeuristics) {
            heuristic.drawInScreen(g);
        }
    }

    public void setHeuristicsWeights(ArrayList<Double> weights){
        mHeuristicsWeights = weights;
    }

    private boolean checkHeuristicsArraySize(){
        return (mHeuristics.size() == mHeuristicsWeights.size());
    }
}
