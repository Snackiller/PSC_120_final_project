package aggregation;

import sim.engine.SimState;
import java.util.Random;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Bag;
import sim.util.Int2D;

public class Agent implements Steppable {
	static final Random random = new Random(); // Use a static Random instance
    int x, y, dirx = 0, diry = 0;
    boolean female;
    double attractiveness;
    double dates = 1;
    double frustration = 1;
    boolean dated = false;
    Stoppable event;

    // Constructors
    public Agent(int x, int y, int dirx, int diry, boolean female, double attractiveness) {
        this.x = x;
        this.y = y;
        this.dirx = dirx;
        this.diry = diry;
        this.female = female;
        this.attractiveness = attractiveness;
    }

    // Method to find a potential date for the agent
 
    public Agent findLocalDate(Environment state) {
    	Bag agents = state.sparseSpace.getMooreNeighbors(x, y, state.searchRadius, state.sparseSpace.TOROIDAL, true);
                if (agents.numObjs == 0) {
                    return null;
                }
                while (agents.numObjs > 0) {
                	int r = state.random.nextInt(agents.numObjs);
                	Agent a = (Agent) agents.objs[r];
                    if (!a.dated && female != a.female) {
                        return a;
                    }
                    agents.remove(a);
                }
        return null;
    }

    // Methods for calculating probabilities and rules for dating
    public double ctRule(Environment state, double p) {
    	if (dates > state.maxDates) {
            return 1;
    	} else {
            return Math.pow(p, ((double)(state.maxDates + 1 - dates) / state.maxDates));
    	}
    }

    public double p1(Environment state, Agent a) {
        return Math.pow(a.attractiveness, state.choosiness) / Math.pow(state.maxAttractiveness, state.choosiness);
    }

    public double p2(Environment state, Agent a) {
        return Math.pow(state.maxAttractiveness - Math.abs(this.attractiveness - a.attractiveness), state.choosiness) / Math.pow(state.maxAttractiveness, state.choosiness);
    }

    public double p3(Environment state, Agent a) {
        return (p1(state, a) + p2(state, a)) / 2.0;
    }

    public double frRule(Environment state) {
    	if (frustration > state.maxFrustration) {
    		return 0;
    	} else {
            return ((state.maxFrustration + 1 - frustration) / state.maxFrustration);
    	}
    }

    public double p4(Environment state, Agent a) {
    	double fr = frRule(state);
    	return p1(state, a) * fr + p2(state, a) * (1 - fr);
    }

    public void remove(Environment state) {
        state.sparseSpace.remove(this);
        event.stop();
    }

    public void nextPopulationStep(Environment state) {
    	dated = true;
    	if (female) {
            state.nextFemale.add(this);
            state.female.remove(this);
    	} else {
            state.nextMale.add(this);
            state.male.remove(this);
        }
    }

    public void date(Environment state, Agent a) {
    	// Perform dating between two agents based on specified rule
        // Evaluate probabilities and rules for dating
        // Update agents' states after dating
    	double p;
		double q;
		switch(state.rule) {
		case ATTRACTIVE:
			p = p1(state,a);
			q = a.p1(state, this);
			p = ctRule(state,p);
			q = ctRule(state,q);
			break;
		case SIMILARITY:
			p = p2(state,a);
			q = a.p2(state, this);
			p = ctRule(state,p);
			q = ctRule(state,q);
			break;
		case FRUSTRATION:
			p = p4(state,a);
			q = a.p4(state, this);
			frustration++;
			break;
		default:
			p = p1(state,a);
			q = a.p1(state, this);
			p = ctRule(state,p);
			q = ctRule(state,q);
			break;	
		}
		if(state.random.nextBoolean(p)&& state.random.nextBoolean(q)) {//couple decision
			if(female) {
				state.experimenter.getData(this, a);
			}
			else {
				state.experimenter.getData(a, this);
			}
			remove(state);
			a.remove(state);
			if(state.replacement) {
				replicate(state);
				a.replicate(state);
			}		
		}
		else {
			this.nextPopulationStep(state);
			a.nextPopulationStep(state);
		}
		if(dates < state.maxDates) {
			dates++;
		}
		if(a.dates < state.maxDates) {
			a.dates++;
		}
    }

    public void replicate (Environment state){
		int x = state.random.nextInt(state.gridWidth);
		int y = state.random.nextInt(state.gridHeight);
		int xdir = state.random.nextInt(3) - 1;
		int ydir = state.random.nextInt(3) - 1;
		boolean gender = state.random.nextBoolean();
		double attractiveness = state.random.nextInt((int)state.maxAttractiveness) + 1;
		Agent new_agent = new Agent(x, y, xdir, ydir, gender, attractiveness);
		new_agent.event = state.schedule.scheduleRepeating(new_agent);
		state.sparseSpace.setObjectLocation(new_agent,x, y);
	}

    public void placeAgent(Environment state) {
    	if(state.oneCellPerAgent) {
            int tempx = state.sparseSpace.stx(x + dirx);
            int tempy = state.sparseSpace.sty(y + diry);
            Bag b = state.sparseSpace.getObjectsAtLocation(tempx, tempy);
            if(b == null){
                  x = tempx;
                  y = tempy;
                  state.sparseSpace.setObjectLocation(this, x, y);
            }
       }
       else {               
            x = state.sparseSpace.stx(x + dirx);
            y = state.sparseSpace.sty(y + diry);
            state.sparseSpace.setObjectLocation(this, x, y);
       }
    }

    public void move(Environment state) {
    	if(!state.random.nextBoolean(state.active)) {
			return;
		}
		if(state.random.nextBoolean(state.p)) {
			dirx = state.random.nextInt(3)-1;
			diry = state.random.nextInt(3)-1;
		}
		placeAgent(state);
    }

    public void step(SimState state) {
    	// Perform one step of simulation for the agent
        // Move agent
        // Check if agent has dated and perform dating if not
    	Environment environment = (Environment)state;
        move(environment);
        if(!dated) {
        	Agent a = findLocalDate(environment);
        	if(a != null) {
        		date(environment,a);
        		return;
        	}
        }
    }

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getDirx() {
		return dirx;
	}

	public void setDirx(int dirx) {
		this.dirx = dirx;
	}

	public int getDiry() {
		return diry;
	}

	public void setDiry(int diry) {
		this.diry = diry;
	}

	public boolean isFemale() {
		return female;
	}

	public void setFemale(boolean female) {
		this.female = female;
	}

	public double getAttractiveness() {
		return attractiveness;
	}

	public void setAttractiveness(double attractiveness) {
		this.attractiveness = attractiveness;
	}

	public double getDates() {
		return dates;
	}

	public void setDates(double dates) {
		this.dates = dates;
	}

	public double getFrustration() {
		return frustration;
	}

	public void setFrustration(double frustration) {
		this.frustration = frustration;
	}

	public boolean isDated() {
		return dated;
	}

	public void setDated(boolean dated) {
		this.dated = dated;
	}

	public Stoppable getEvent() {
		return event;
	}

	public void setEvent(Stoppable event) {
		this.event = event;
	}

	public static Random getRandom() {
		return random;
	}
}
