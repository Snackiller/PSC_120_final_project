package aggregation;

import java.awt.Color;
import spaces.Spaces;
import sweep.SimStateSweep;
import sweep.GUIStateSweep;

public class AgentGUI extends GUIStateSweep {
    public AgentGUI(SimStateSweep state, int gridWidth, int gridHeight, Color backdrop, Color agentDefaultColor, boolean agentPortrayal) {
        super(state, gridWidth, gridHeight, backdrop, agentDefaultColor, agentPortrayal);
    }

    public static void main(String[] args) {
    	String[] title = {"Pair Correlation", "Mean Attractiveness"};
    	String[] x = {"Time Steps", "Time Steps"};
    	String[] y = {"Correlation", "Mean"};
    	AgentGUI.initializeArrayTimeSeriesChart(2, title, x, y);
    	String[] title2 = {"Frequency Distribution of Attractiveness"};
    	String[] x2 = {"Time Steps"};
    	String[] y2 = {"Frequency"};
    	AgentGUI.initializeArrayHistogramChart(1, title2, x2, y2, new int[10]);
    	AgentGUI.initialize(Environment.class, Experimenter.class, AgentGUI.class, 400, 400, Color.WHITE, Color.BLUE, true, Spaces.SPARSE);
    }
}