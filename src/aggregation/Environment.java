package aggregation;


import spaces.Spaces;
import sim.util.Bag;
import sweep.SimStateSweep;

public class Environment extends SimStateSweep {
	
	// Parameters & global variables
    public int searchRadius = 1;
    public int gridWidth = 100;
    public int gridHeight = 100;
    public boolean oneCellPerAgent = true;
    public double active = 1.0;
	public double p = 1.0;
	public int males = 1000;
    public int females = 1000;
    public double maxAttractiveness = 10.0;
    public double choosiness = 3.0;
    public double maxDates = 100.0;
    public double maxFrustration = 50.0;
    public Rule rule = Rule.ATTRACTIVE; // Assuming Rule is an enum you've defined elsewhere
    public int ruleNumber = 0;
    public boolean charts = true;
    public boolean replacement = false;
    Bag male = new Bag();
    Bag female = new Bag();
    Bag nextMale = new Bag();
    Bag nextFemale = new Bag();
    public Experimenter experimenter = null; // Assuming Experimenter is a class you've defined elsewhere

    // Constructor
    public Environment(long seed, Class observer) {
        super(seed, observer);
        // Initialize environment with the seed and observer here
    }

    // Reset method
    public void reset() {
        male.clear();
        female.clear();
        nextMale.clear();
        nextFemale.clear();
    }

    // MakeAgents method
    public void makeAgents() {
        for (int i = 0; i < females; i++) {
            int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);
            int xdir = random.nextInt(3) - 1;
            int ydir = random.nextInt(3) - 1;
            double attractiveness = random.nextInt((int)maxAttractiveness) + 1;
            Agent agent = new Agent(x, y, xdir, ydir, true, attractiveness);
            // Add to Bag, schedule, and set location
            agent.event = schedule.scheduleRepeating(agent);
            sparseSpace.setObjectLocation(agent, x, y);
            female.add(agent);
        }
        for (int i = 0; i < males; i++) {
        	int x = random.nextInt(gridWidth);
            int y = random.nextInt(gridHeight);
            int xdir = random.nextInt(3) - 1;
            int ydir = random.nextInt(3) - 1;
            double attractiveness = random.nextInt((int)maxAttractiveness) + 1;
            Agent agent = new Agent(x, y, xdir, ydir, false, attractiveness);
            // Add to Bag, schedule, and set location
            agent.event = schedule.scheduleRepeating(agent);
            sparseSpace.setObjectLocation(agent, x, y);
            male.add(agent);
        }
    }

    // Start method
    public void start() {
        super.start(); // Initialize the simulation
        reset(); // Reset global variables
        // Create a 2D space and agents
        this.makeSpace( gridWidth, gridHeight); // Assuming make2DSpace is defined elsewhere
        makeAgents();
        if (observer != null) {
            observer.initialize(sparseSpace, Spaces.SPARSE); // Initialize the observer
            experimenter = (Experimenter) observer; // Cast observer to Experimenter
            experimenter.resetVariables();
            experimenter.setObservationInterval(this.dataSamplingInterval); // Assuming dataSamplingInterval is defined elsewhere
        }
    }

	public int getSearchRadius() {
		return searchRadius;
	}

	public void setSearchRadius(int searchRadius) {
		this.searchRadius = searchRadius;
	}

	public int getMales() {
		return males;
	}

	public void setMales(int males) {
		this.males = males;
	}

	public int getFemales() {
		return females;
	}

	public void setFemales(int females) {
		this.females = females;
	}

	public double getMaxAttractiveness() {
		return maxAttractiveness;
	}

	public void setMaxAttractiveness(double maxAttractiveness) {
		this.maxAttractiveness = maxAttractiveness;
	}

	public double getChoosiness() {
		return choosiness;
	}

	public void setChoosiness(double choosiness) {
		this.choosiness = choosiness;
	}

	public double getMaxDates() {
		return maxDates;
	}

	public void setMaxDates(double maxDates) {
		this.maxDates = maxDates;
	}

	public double getMaxFrustration() {
		return maxFrustration;
	}

	public void setMaxFrustration(double maxFrustration) {
		this.maxFrustration = maxFrustration;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public int getRuleNumber() {
		return ruleNumber;
	}

	public void setRuleNumber(int ruleNumber) {
		this.ruleNumber = ruleNumber;
	}

	public boolean isReplacement() {
		return replacement;
	}

	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}

	public Bag getMale() {
		return male;
	}

	public void setMale(Bag male) {
		this.male = male;
	}

	public Bag getFemale() {
		return female;
	}

	public void setFemale(Bag female) {
		this.female = female;
	}

	public Bag getNextMale() {
		return nextMale;
	}

	public void setNextMale(Bag nextMale) {
		this.nextMale = nextMale;
	}

	public Bag getNextFemale() {
		return nextFemale;
	}

	public void setNextFemale(Bag nextFemale) {
		this.nextFemale = nextFemale;
	}

	public boolean isOneCellPerAgent() {
		return oneCellPerAgent;
	}

	public void setOneCellPerAgent(boolean oneCellPerAgent) {
		this.oneCellPerAgent = oneCellPerAgent;
	}

	public double getActive() {
		return active;
	}

	public void setActive(double active) {
		this.active = active;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
	}

	public int getGridWidth() {
		return gridWidth;
	}

	public void setGridWidth(int gridWidth) {
		this.gridWidth = gridWidth;
	}

	public int getGridHeight() {
		return gridHeight;
	}

	public void setGridHeight(int gridHeight) {
		this.gridHeight = gridHeight;
	}

	public boolean isCharts() {
		return charts;
	}

	public void setCharts(boolean charts) {
		this.charts = charts;
	}


}

