package eu.erasmus.intelligentSystems.search.GARobotTuning;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.IChromosome;
import org.jgap.impl.*;

public class Environment {
	public static void main(String[] args) {	
		Configuration conf = new DefaultConfiguration();
		RobotFitnessFunction fitnessFunction = new RobotFitnessFunction();
		int fieldSize = 14;
		try {
			/**/ //Delete second asterisk and last backslash to skip evolution and run the previous best configuration 
			conf.setFitnessFunction(fitnessFunction); 
			Gene[] sampleGenes = new Gene[ 4 ];
			sampleGenes[0] = new DoubleGene(conf, 0, 500 );  // Minimal speed
			sampleGenes[1] = new DoubleGene(conf, 0, 700 );  // Range of speeds
			sampleGenes[2] = new DoubleGene(conf, 0, Math.nextUp(Math.sqrt(64*fieldSize*64*fieldSize))); // UpperBound is maximum distance that can be between two robots -> size of a diagonal of a field
			sampleGenes[3] = new DoubleGene(conf, 0, 1);  		// Probability of speed change
			Chromosome sampleChromosome = new Chromosome(conf,sampleGenes);
			conf.setSampleChromosome(sampleChromosome);
			conf.setPopulationSize(4);
			Genotype population = Genotype.randomInitialGenotype(conf);
			population.evolve(4);
			IChromosome bestSolutionSoFar = population.getFittestChromosome();
			// Show the best configuration found, write it to a config file and then load battle with this setup
			fitnessFunction.provideCongifuration(bestSolutionSoFar); /**/
			fitnessFunction.runBattleWithOneConfiguration(true); 
			// Make sure that the Java VM is shut down properly
			System.exit(0);
		}
		catch (Exception e) {
			System.out.println("Wrong GA setup");
		}
	}
	

	

	

}
