package org.crowdsourcedinterlinking.expblogic;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.crowdsourcedinterlinking.evaluation.ResultEvaluatorImpl;
import org.crowdsourcedinterlinking.mgeneration.AlgorithmPairsGeneratorImpl;
import org.crowdsourcedinterlinking.mgeneration.CartesianPairsGeneratorImpl;
import org.crowdsourcedinterlinking.mgeneration.CwdfMicrotaskGeneratorImpl;
import org.crowdsourcedinterlinking.mgeneration.HRFPPairsGeneratorImpl;
import org.crowdsourcedinterlinking.model.Dataset;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;
import org.crowdsourcedinterlinking.mpublication.CwdfMicrotaskPublisherImpl;
import org.crowdsourcedinterlinking.rcollection.CwdfResultProcessorImpl;
import org.crowdsourcedinterlinking.rcollection.CwdfResultReaderImpl;

public class ExperimentManager {

	public void runTrialCartesianExperiment(Ontology o1, Ontology o2,
			TypeOfMappingGoal mappingGoal, boolean context) {

		/*
		 * Ontology o1 = new
		 * Ontology("http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf"
		 * ,
		 * "/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/ontologies/301.rdf"
		 * , "301"); Ontology o2 = new
		 * Ontology("http://oaei.ontologymatching.org/2009/benchmarks/304/onto.rdf"
		 * ,
		 * "/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/ontologies/304.rdf"
		 * , "304");
		 */
		// PairsGenerator
		CartesianPairsGeneratorImpl pairsGen = new CartesianPairsGeneratorImpl(
				o1, o2);

		
		CwdfMicrotaskGeneratorImpl microGen = new CwdfMicrotaskGeneratorImpl(
				mappingGoal, context);

		
		CwdfMicrotaskPublisherImpl microPub = new CwdfMicrotaskPublisherImpl();

		MicrotaskManager microTaskManager = new MicrotaskManager(pairsGen,
				microGen, microPub);
		microTaskManager.prepareListOfMicrotasks();

		/*
		 * CwdfResultReaderImpl reader = new CwdfResultReaderImpl(o1, o2);
		 * 
		 * CwdfResultProcessorImpl processor = new CwdfResultProcessorImpl();
		 * 
		 * 
		 * 
		 * ResultManager resultManager = new ResultManager(reader, processor);
		 * resultManager.analyseResultsOfTheCrowd();
		 */

	}

	public void runTrialHRFPExperiment(Ontology o1, Ontology o2,
			File fReferenceAlignment, File fParticipantsAlignment,
			TypeOfMappingGoal mappingGoal, boolean context) {

		try {

			
			HRFPPairsGeneratorImpl pairsGen = new HRFPPairsGeneratorImpl(o1,
					o2, fReferenceAlignment, fParticipantsAlignment);

			CwdfMicrotaskGeneratorImpl microGen = new CwdfMicrotaskGeneratorImpl(
					mappingGoal, context);

			CwdfMicrotaskPublisherImpl microPub = new CwdfMicrotaskPublisherImpl();

			MicrotaskManager microTaskManager = new MicrotaskManager(pairsGen,
					microGen, microPub);
			microTaskManager.prepareListOfMicrotasks();
			

			
			/*  CwdfResultReaderImpl reader = new CwdfResultReaderImpl(o1, o2);
			  
			  CwdfResultProcessorImpl processor = new
			  CwdfResultProcessorImpl();
			  
			  
			  ResultManager resultManager = new ResultManager( reader,
			  processor); resultManager.analyseResultsOfTheCrowd(); 
			  */
			  
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	public void runTrialAlgorithmExperiment(Ontology o1, Ontology o2,
			File fParticipantAlignment,
			TypeOfMappingGoal mappingGoal, boolean context) {
		
		
		AlgorithmPairsGeneratorImpl pairsGen = new AlgorithmPairsGeneratorImpl(
				o1, o2, fParticipantAlignment);
		
		
		
		CwdfMicrotaskGeneratorImpl microGen = new CwdfMicrotaskGeneratorImpl(
				mappingGoal, context);

		CwdfMicrotaskPublisherImpl microPub = new CwdfMicrotaskPublisherImpl();

		MicrotaskManager microTaskManager = new MicrotaskManager(pairsGen,
				microGen, microPub);
		microTaskManager.prepareListOfMicrotasks();
		
		
		/*
		 CwdfResultReaderImpl reader = new CwdfResultReaderImpl(o1, o2);
		  
		  CwdfResultProcessorImpl processor = new
		  CwdfResultProcessorImpl();
		  
		  
		  ResultManager resultManager = new ResultManager( reader,
		  processor); resultManager.analyseResultsOfTheCrowd();
		  
		*/
		
		
		
		
		
		

	}

}
