package org.crowdsourcedinterlinking.expblogic;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.crowdsourcedinterlinking.evaluation.ResultEvaluatorImpl;
import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.Response;
import org.crowdsourcedinterlinking.mpublication.CwdfService;
import org.crowdsourcedinterlinking.rcollection.ResultProcessor;
import org.crowdsourcedinterlinking.rcollection.ResultReader;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import com.google.common.io.Files;

public class ResultManager {

	// private Set<Microtask> setOfMicrotasksToAnalyse;

	private ResultReader resultReader;
	private ResultProcessor resultProcessor;

	public ResultManager(ResultReader reader, ResultProcessor processor) {
		// this.setOfMicrotasksToAnalyse=setMicrotasks;
		this.resultReader = reader;
		this.resultProcessor = processor;

	}

	public void analyseResultsOfTheCrowd() {
		// Ontologies files jobs, readResponses of one Job
		Set<Mapping> setOfMappings = new HashSet<Mapping>();

		CwdfService s = new CwdfService();
		try {

			File directory = new File(ConfigurationManager.getInstance()
					.getTrackDirectory());
			File files[] = directory.listFiles();
			for (File crowdFile : files) // each file represents an alignment
											// between two ontologies
			{
				String filePath = crowdFile.getName();
				if (filePath.startsWith("jobsToAnalyse")
						&& filePath.endsWith(".txt")) {

					List<String> lines = Files.readLines(crowdFile,
							Charset.defaultCharset());

					// Info about ontology 1
					String line0 = lines.get(0);
					String[] ontologyO1Attributes = line0.split(",");
					Ontology o1 = new Ontology(ontologyO1Attributes[0],
							ontologyO1Attributes[1], ontologyO1Attributes[2]);

					// Info about ontology 2
					String line1 = lines.get(1);
					String[] ontologyO2Attributes = line1.split(",");
					Ontology o2 = new Ontology(ontologyO2Attributes[0],
							ontologyO2Attributes[1], ontologyO2Attributes[2]);

					// Info about the generated microtasks for this pair of
					// ontologies
					for (int i = 2; i < lines.size(); i++) // for each microtask
															// created for the
															// alignment of the
															// two ontologies O1
															// and O2
					{
						String lineJobI = lines.get(i);
						String[] jobInfo = lineJobI.split(",");
						String microtaskId = jobInfo[0];
						String microtaskType = jobInfo[1];

						Set<Mapping> microtaskMappings = resultReader
								.readResponsesOfMicrotask(microtaskId,
										microtaskType, s);

						setOfMappings.addAll(microtaskMappings);

						resultReader.readResponsesZip(microtaskId, s);

					}
					Alignment alignment = new Alignment(o1, o2, setOfMappings);

					resultProcessor
							.serialiseSelectedAlignmentToAlignmentAPIFormat(alignment);
					File fCrowd = new File(ConfigurationManager.getInstance()
							.getCrowdAlignmentsDirectory()
							+ ConfigurationManager.getInstance()
									.getCrowdBaseFileName()
							+ o1.getName()
							+ o2.getName() + ".rdf");
					File fRef = new File(ConfigurationManager.getInstance()
							.getReferenceAlignmentsDirectory()
							+ ConfigurationManager.getInstance()
									.getReferenceBaseFileName()
							+ o1.getName()
							+ o2.getName() + ".rdf");
					ResultEvaluatorImpl ev = new ResultEvaluatorImpl(fCrowd,
							fRef);
					ev.evaluateResultsFromCrowdPR();
					ev.printResultsInConsole();
					ev.printResultsInFile();

				}

			}

			// read results
			// process result

			// save the zip file
			/*
			 * for (Microtask m: this.setOfMicrotasksToAnalyse) { Set<Response>
			 * setOfResponses = this.resultReader.readResponsesZip(m, s);
			 * System.out.println("responses of job: "); }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
