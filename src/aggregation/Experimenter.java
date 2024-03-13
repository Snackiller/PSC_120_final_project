package aggregation;
import observer.Observer;
import sim.engine.SimState;
import sim.util.Bag;
import sweep.ParameterSweeper;
import sweep.SimStateSweep;

public class Experimenter extends Observer {
    // Variables for correlation calculation
    private int n = 0;
    private double sX = 0;
    private double sY = 0;
    private double sX2 = 0;
    private double sY2 = 0;
    private double sXY = 0;

    // Constructor
    public Experimenter(String fileName, String folderName, SimStateSweep state, ParameterSweeper sweeper,
			String precision, String[] headers) {
		super(fileName, folderName, state, sweeper, precision, headers);
	}

    // Reset variables
    public void resetVariables() {
        n = 0;
        sX = 0;
        sY = 0;
        sX2 = 0;
        sY2 = 0;
        sXY = 0;
    }

    // Get data for correlation from agents
    public void getData(Agent x, Agent y) {
        getData(x.attractiveness, y.attractiveness);
    }

    // Get data for correlation from numerical values
    public void getData(double x, double y) {
        sXY += x * y;
        sX += x;
        sY += y;
        sX2 += x * x;
        sY2 += y * y;
        n++;
    }

    // Calculate the correlation coefficient
    public double correlation() {
        return (sXY - (sX * sY) / n) / Math.sqrt((sX2 - (sX * sX) / n) * (sY2 - (sY * sY) / n));
    }

    // Print data
    public void printData(Environment state) {
        int percent = (int) ((n / (double) state.females) * 100.0);
        if (n > 1) {
            System.out.println(state.schedule.getSteps() + " " + percent + " " + correlation() + " " + ((sX + sY) / (2 * n)));
        }
    }

    // Stop simulation if no agents are left
    public void stop(Environment state) {
        Bag agents = state.sparseSpace.getAllObjects();
        if (agents == null || agents.numObjs == 0) {
            event.stop();
        }
    }

    // Swap populations
    public void populations(Environment state) {
        Bag tempMale = state.male;
        Bag tempFemale = state.female;
        state.male = state.nextMale;
        state.female = state.nextFemale;
        state.nextFemale = tempFemale;
        state.nextMale = tempMale;
        state.nextFemale.clear();
        state.nextMale.clear();
        // Assuming Agent class has 'dated' field
        for (int i = 0; i<state.male.numObjs; i++) {
            Agent a = (Agent)state.male.objs[i];
            a.dated = false;
        }
        for (int i = 0; i<state.female.numObjs; i++) {
        	Agent a = (Agent)state.female.objs[i];
            a.dated = false;
        }
        for (int i = 0; i<state.sparseSpace.allObjects.numObjs; i++) {
        	if(state.sparseSpace.allObjects.objs[i].getClass().equals(Agent.class)) {
        		Agent a = (Agent)state.sparseSpace.allObjects.objs[i];
                a.dated = false;
        	}
        }
    }

    // Update pair correlation chart
    public void pairCorrelation(Environment state) {
    	double time = (double)state.schedule.getTime();
        this.upDateTimeChart(0, time, correlation(), true, 1000);
    }

    // Update attractiveness chart
    public void attractiveness(Environment state) {
    	double a = 0.0;
        
        if (n > 1) {
            a = (sX + sY) / (2.0 * n);
        }
        
        double time = state.schedule.getTime();
        
        this.upDateTimeChart(1, time, a, true, 1000);
    }

    // Update attractiveness distribution histogram
    public void attractivenessDistribution(Environment state) {
    	Bag agents = state.sparseSpace.allObjects;
        double[] data = new double[agents.numObjs];
        
        for (int i = 0; i < agents.numObjs; i++) {
            Agent agent = (Agent) agents.get(i);
            data[i] = agent.attractiveness;
        }
        if(agents.numObjs > 0) {
        	this.upDateHistogramChart(0, (int)state.schedule.getSteps(), data, 10);
        }
    }

    // Perform actions at each simulation step
    @Override
    public void step(SimState state) {
        super.step(state);
        Environment environment = (Environment) state;
        stop(environment);
        if (state.schedule.getSteps() % environment.dataSamplingInterval == 0) {
            pairCorrelation(environment);
            attractiveness(environment);
            attractivenessDistribution(environment);
        } else {
        	// printData(environment);
        }
        populations(environment);
    }

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public double getsX() {
		return sX;
	}

	public void setsX(double sX) {
		this.sX = sX;
	}

	public double getsY() {
		return sY;
	}

	public void setsY(double sY) {
		this.sY = sY;
	}

	public double getsX2() {
		return sX2;
	}

	public void setsX2(double sX2) {
		this.sX2 = sX2;
	}

	public double getsY2() {
		return sY2;
	}

	public void setsY2(double sY2) {
		this.sY2 = sY2;
	}

	public double getsXY() {
		return sXY;
	}

	public void setsXY(double sXY) {
		this.sXY = sXY;
	}
}
