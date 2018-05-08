package eu.erasmus.intelligentSystems.search.GARobotTuning;

import org.jgap.FitnessFunction;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgap.IChromosome;
import robocode.control.*;
import robocode.control.events.*;

public class RobotFitnessFunction extends FitnessFunction implements IBattleListener{
	private static final long serialVersionUID = 1L;
	
	// ----- Robocode Environment configuration properties ----- //
	// Create the RobocodeEngine
	private RobocodeEngine engine = new RobocodeEngine(new java.io.File("C://robocode")); // TODO this should be changed depending on the user
	// Create the battlefield
	private int fieldSize = 14;
	private int NumPixelRows = 64*fieldSize;
	private int NumPixelCols = 64*fieldSize;
	private int enemiesNum = 1;
	private BattlefieldSpecification battlefield = new BattlefieldSpecification(NumPixelRows, NumPixelCols);
	// Setup battle parameters
	private long inactivityTime = 10000000;
	private double gunCoolingRate = 1.0;
	private int sentryBorderSize = 50;
	private boolean hideEnemyNames = false;
	private int numberOfRounds = 1;
	
	RobotSpecification[] existingRobots = new RobotSpecification[enemiesNum + 1];
	RobotSetup[] robotSetups = new RobotSetup[enemiesNum + 1];//robocode
	// Every time robots start of the same positions so that a battle will be deterministic and GA more effective as well
	private int startingColFire = 2; 
	private int startingRowFire = 3;
	private int startingColTracker = 9;
	private int startingRowTracker = 13;
	// ----- End of Robocode Environment configuration properties ----- //
	//RobotSpecification[] modelRobots = engine.getLocalRepository("supersample.SuperRamFire,eu.erasmus.intelligentSystems.search.TunableBot.TunableTracker*");
	RobotSpecification[] modelRobots = engine.getLocalRepository("eu.erasmus.intelligentSystems.search.TunableBot.DeterministicRamFire*,eu.erasmus.intelligentSystems.search.TunableBot.TunableTracker*");
	//TODO this should also be changed depending on the user, the only location where can be placed files for I/O of robot is in his .data dir obtained 
	// by AdvancedRobots method getDataDirectory and concrete file byt getDataFile
	private String _config = "C:\\Users/Kacpix/Desktop/intsys/GATunableRobot-master/MyRobots/bin/eu/erasmus/intelligentSystems/search/TunableBot/TunableTracker.data/config.data";	
	private int _result = 0; // Fitness value that is going to be determined by function function
	private int _maximumDifference = 1000; // Maximum difference in scores of the two robots in one battle consisting of one round 
	
	public RobotFitnessFunction() {
		// For computing score
		engine.addBattleListener(this);
	}
	
	// The higher bigger value is difference between an individual robot and the enemy the more fit the individual is
	@Override
	protected double evaluate(IChromosome arg0) {
		provideCongifuration(arg0);
		runBattleWithOneConfiguration(false);
		return _result + _maximumDifference; // Adding the difference for compensating negative results, result value is determined 
											// by onBattleCompleted handler
	}
	
	// Run for one SuperTracker configuration, run for numberOfRounds times, record results to pass for evaluation
	public void runBattleWithOneConfiguration(boolean showBattle) {
		try {
		/* Do we want to show the particular Robocode battle view? 
		* While evolving populat there could be plenty of battles that run only to be evaluating and we may be not interested in 
		* seeing every battle of the process */
		engine.setVisible(showBattle);
		
		/* Create, place robots and prepare for handing them over to robocode engine  */
		
		// Assign robot types
		existingRobots[0] = modelRobots[0]; // RamFire
		existingRobots[enemiesNum] = modelRobots[enemiesNum]; // TunableTracker
		
		// Place them on the map
		robotSetups[0] = new RobotSetup(64.0*startingColTracker,64.0*startingRowTracker, 0.0);
		robotSetups[enemiesNum] = new RobotSetup(64.0*startingColFire,64.0*startingRowFire, 0.0);
		
		/* Create and run the battle */
		BattleSpecification battleSpec = new BattleSpecification(battlefield, numberOfRounds, inactivityTime,
										gunCoolingRate, sentryBorderSize, hideEnemyNames, existingRobots, robotSetups);

		engine.runBattle(battleSpec, true); // Runs the battle and waits till it finishes
		if (showBattle)try {System.in.read();} catch (IOException e) {} // For the sake of not closing instantly the window with final battle, wait for enter
		// Analyze results of battles with help of handler onBattleCompleted
		engine.close();
		}
		catch(Exception e) {
			System.out.println(e.toString());
		}
	}

	@Override
	public void onBattleCompleted(BattleCompletedEvent battle) {
		 _result = battle.getIndexedResults()[1].getScore() - battle.getIndexedResults()[0].getScore(); // The higher the better for the TunableTracker
	}
	
	public boolean provideCongifuration(IChromosome a_potentialSolution) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(_config))))
		{
			// By chromosome
			for (int i = 0; i < a_potentialSolution.getGenes().length; i++) {
				writer.write(getParameterValues(a_potentialSolution, i));
				writer.newLine();
			}
			writer.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	private String getParameterValues(IChromosome a_potentialSolution, int a_position )
	{
		return a_potentialSolution.getGene(a_position).getAllele() + "";
	}

	// Empty definitions just to satisfy IBattleListener interface
	@Override
	public void onBattleError(BattleErrorEvent arg0) {}

	@Override
	public void onBattleFinished(BattleFinishedEvent arg0) {}

	@Override
	public void onBattleMessage(BattleMessageEvent arg0) {}

	@Override
	public void onBattlePaused(BattlePausedEvent arg0) {}

	@Override
	public void onBattleResumed(BattleResumedEvent arg0) {}

	@Override
	public void onBattleStarted(BattleStartedEvent arg0) {}

	@Override
	public void onRoundEnded(RoundEndedEvent arg0) {}

	@Override
	public void onRoundStarted(RoundStartedEvent arg0) {}

	@Override
	public void onTurnEnded(TurnEndedEvent arg0) {}

	@Override
	public void onTurnStarted(TurnStartedEvent arg0) {}

}
