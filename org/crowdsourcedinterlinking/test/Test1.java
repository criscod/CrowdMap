package org.crowdsourcedinterlinking.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentProducer;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.crowdsourcedinterlinking.evaluation.DiffMappings;
import org.crowdsourcedinterlinking.evaluation.ResultEvaluator;
import org.crowdsourcedinterlinking.evaluation.ResultEvaluatorImpl;
import org.crowdsourcedinterlinking.expblogic.ExperimentManager;
import org.crowdsourcedinterlinking.mgeneration.CwdfMicrotaskGeneratorImpl;
import org.crowdsourcedinterlinking.mgeneration.HRFPPairsGeneratorImpl;
import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.AlignmentParser;
import org.crowdsourcedinterlinking.model.JobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.Relation;
import org.crowdsourcedinterlinking.model.Response;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;
import org.crowdsourcedinterlinking.mpublication.CwdfService;
import org.crowdsourcedinterlinking.rcollection.CwdfResultProcessorImpl;
import org.crowdsourcedinterlinking.rcollection.CwdfResultReaderImpl;
import org.crowdsourcedinterlinking.util.ClassesAndProperties;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.ObjectMapping;
import org.semanticweb.owl.align.AlignmentException;
import org.semanticweb.owl.align.Cell;

import com.clarkparsia.pellet.owlapiv3.PelletReasoner;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.function.library.eval;

import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.eval.ExtPREvaluator;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.eval.SemPRecEvaluator;

public class Test1 {

	public static void main(String args[]) {
		try {

			//testNullPointerDefaultLabel();
			
			// testReadMappGetUnits();
			// testConfiguration();
			// testSerialisationAlignmentFormat();

			// testEvaluator1(); //-- last uncommented test when ISWC
			// testEvaluator2();
			// testEvaluator3();

			// t();
			// testPairsGeneration();

			// testRead();

			// testParseAndEvaluate();
			// test1(); //DONE
			// test2(); //DONE
			
			// correcting 2011/301
			 //test1b(); //REPE-DONE
			 //test2b(); //REPE-DONE
			
			// test3(); //DONE
			 //test4(); //DONE
			// test5(); //DONE
			// test6();//DONE

			 //test7();//DONE
			//test8();//DONE
			// test9();//DONE
			//test10();// DONE
			// test11();//DONE
		//	 test12();//DONE
			// test13();//DONE
			// test14();//DONE
			// test15();//DONE
			// test16();//DONE
			 //test17();//DONE
			// test18();//DONE
			//test19();// DONE
			// test20();//DONE
			// test21();//DONE
			// test22();//DONE

			//test23();// DONE

			// test24();//DONE
			//test25();// DONE
			// test26();//DONE
			 test27();//DONE

			// test28();//DONE
			// test29();//DONE + //WE ALREADY HAVE THIS PUBLISHED 100349, 352
			// with the error of order but if they are OK, then take it

			// test30();//DONE

			//BIOONTOLOGIES
			
			//test31(); 
		
			//test32(); 
			//test33(); 
			
			//test34();
			
			//checkNumbers();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void t() {
		/*
		 * Ontology o1 = new
		 * Ontology("http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf"
		 * ,
		 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf"
		 * , "101"); Ontology o2 = new
		 * Ontology("http://oaei.ontologymatching.org/2011/benchmarks/301/onto.rdf"
		 * ,
		 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf"
		 * , "301");
		 * 
		 * File fReferenceAlignment = new File(
		 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101301.rdf"
		 * );
		 */
		Ontology o1 = new Ontology(
				"http://cmt",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
				"cmt");
		Ontology o2 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");

		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtconference.rdf");

		AlignmentParser p = new AlignmentParser(fReferenceAlignment, ObjectMapping.CLASSES);
		p.parseAlignment(o1, o2);
	}

	private static void testReadMappGetUnits() throws ClientProtocolException,
			IOException {
		// https://api.crowdflower.com/v1/jobs/97860/units?key=32c441799be374c58a0b4a0dc92644f78949cdf3
		HttpGet get = new HttpGet(
				"https://api.crowdflower.com/v1/jobs/97860/units?key=32c441799be374c58a0b4a0dc92644f78949cdf3");
		get.addHeader("Accept", "application/json");

		HttpClient client = new DefaultHttpClient();

		// check when the unit is finalized
		HttpResponse response = client.execute(get);

		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		ObjectMapper mapper = new ObjectMapper();
		result = mapper.readValue(response.getEntity().getContent(),
				new TypeReference<Map<String, Map<String, String>>>() {
				});

		for (Map.Entry<String, Map<String, String>> entry : result.entrySet()) {
			System.out.println("Unit: " + entry.getKey());
			for (Map.Entry<String, String> attr : entry.getValue().entrySet()) {
				System.out.println(attr.getKey() + " = " + attr.getValue());
			}
			System.out.println("=============================");
		}
	}

	/*
	 * private static void testParseAndEvaluate() {
	 * 
	 * //TEST FOR THE 301-304 for the small pilot
	 * 
	 * //read from report file CwdfResultReaderImpl reader = new
	 * CwdfResultReaderImpl(); Set<String> setOfFiles = new HashSet<String>();
	 * String path1 = "/Users/cristinasarasua/Downloads/a97860-1.csv"; String
	 * path2 = "/Users/cristinasarasua/Downloads/a97861-1.csv";
	 * setOfFiles.add(path1); setOfFiles.add(path2);
	 * 
	 * Ontology o1 = new
	 * Ontology("http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf",
	 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf"
	 * , "301"); Ontology o2 = new
	 * Ontology("http://oaei.ontologymatching.org/2009/benchmarks/304/onto.rdf",
	 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/304.rdf"
	 * , "304");
	 * 
	 * 
	 * 
	 * Alignment alignment = reader.createAlignmentFromCrowdFlowerReport(o1, o2,
	 * setOfFiles, TypeOfMappingGoal.IDENTIFICATIONB, true);
	 * 
	 * //serialise the alignment into a alignment file CwdfResultProcessorImpl
	 * processor = new CwdfResultProcessorImpl(alignment);
	 * processor.serialiseSelectedAlignmentToAlignmentAPIFormat();
	 * 
	 * 
	 * //evaluate the alignment file }
	 */

	private static void testEvaluator1() {

		try {
			// Not absolute path because it automatically recognises and
			// otherwises concatenates
			// File fCrowd = new File ("resultAlignmentFormatretocada.rdf");
			// File fReference = new File
			// ("referencealignments/resultAlignmentFormatretocada.rdf");

			/*
			 * File fCrowd = new File ("resultAlignmentFormat.rdf"); File
			 * fReference = new File
			 * ("referencealignments/resultAlignmentFormat.rdf");
			 */

			/*
			 * File fCrowd = new File ("crowdalignments/t/cwdalign301304.rdf");
			 * File fReference = new File
			 * ("referencealignments/refalign301304.rdf");
			 */

			/*
			 * File fCrowd = new File
			 * ("crowdalignments/t/cwdalign301304_2.rdf"); File fReference = new
			 * File ("referencealignments/refalign301304.rdf");
			 */

			/*
			 * File fCrowd = new File ("crowdalignments/cwdalign101301.rdf");
			 * File fReference = new File
			 * ("referencealignments/refalign101301.rdf");
			 */

			// For the case of the algorithm simulated experiment (AROMA), we
			// don«t have answers on CrowdFlower, that's why we evaluate it
			// directly with the evaluator -ISWC2012 need
			/*File fCrowd = new File(
					"crowdalignments/cwdalign301304_CartPImp.rdf");
			File fReference = new File("referencealignments/refalign301304.rdf");*/
			
			//end ISWC2012 need
			File fCrowd = new File(
			"crowdalignments/cwdalignekawiasted.rdf");
	File fReference = new File("referencealignments/refalignekawiasted.rdf");

			ResultEvaluatorImpl ev = new ResultEvaluatorImpl(fCrowd, fReference);
			ev.evaluateResultsFromCrowdPR();
			ev.printResultsInConsole();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testEvaluator2() {

		try {
			// Not absolute path because it automatically recognises and
			// otherwises concatenates
			// File fCrowd = new File ("resultAlignmentFormatretocada.rdf");
			// File fReference = new File
			// ("referencealignments/resultAlignmentFormatretocada.rdf");

			File fCrowd = new File("crowdalignments/cwdalign101301.rdf");
			File fReference = new File("referencealignments/refalign101301.rdf");

			ResultEvaluatorImpl ev = new ResultEvaluatorImpl(fCrowd, fReference);
			DiffMappings diffMappings = ev.evaluateDiff();

			int falsePositives = diffMappings.getFalsePositives().size();
			System.out.println("number of false positives: " + falsePositives);
			int falseNegatives = diffMappings.getFalseNegatives().size();
			System.out.println("number of false negatives: " + falseNegatives);

			/*
			 * int candidates = falsePositives+falseNegatives;
			 * System.out.println("# Candidate Participants alignments: "+
			 * candidates);
			 */

			System.out.println("-------FALSE POSITIVES-----");

			for (Cell c : diffMappings.getFalsePositives()) {
				System.out.println("------------");

				System.out
						.println("entity1: " + c.getObject1AsURI().toString());
				System.out
						.println("entity2: " + c.getObject2AsURI().toString());
				System.out
						.println("relation: " + c.getRelation().getRelation());

				System.out.println("------------");
			}

			System.out.println("-------FALSE NEGATIVES-----");

			for (Cell c2 : diffMappings.getFalseNegatives()) {
				System.out.println("------------");

				System.out.println("entity1: "
						+ c2.getObject1AsURI().toString());
				System.out.println("entity2: "
						+ c2.getObject2AsURI().toString());
				System.out.println("relation: "
						+ c2.getRelation().getRelation());

				System.out.println("------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testEvaluator3() {

		try {

			File fCrowd = new File("crowdalignments/cwdaligntest.rdf");
			File fReference = new File("referencealignments/refaligntest.rdf");

			ResultEvaluatorImpl ev = new ResultEvaluatorImpl(fCrowd, fReference);
			ev.evaluateResultsFromCrowdPR();
			ev.printResultsInConsole();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testConfiguration() {

		ConfigurationManager cm = ConfigurationManager.getInstance();
		cm.test();

	}

	/*
	 * private static void testSerialisationAlignmentFormat() {
	 * 
	 * try {
	 * 
	 * Ontology o1 = new Ontology("http://www.ontologies.org/cmt#",
	 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl"
	 * ,"CMT"); Ontology o2 = new Ontology("http://www.ontologies.org/ekaw#",
	 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl"
	 * ,"EKAW");
	 * 
	 * //Ontology o1 = new Ontology("http://cmt#",
	 * "http://oaei.ontologymatching.org/2011/conference/data/cmt.owl","CMT");
	 * //Ontology o2 = new Ontology("http://ekaw#",
	 * "http://oaei.ontologymatching.org/2011/conference/data/ekaw.owl","EKAW");
	 * 
	 * 
	 * Set<Mapping> setOfCells= new HashSet<Mapping>(); Model model=
	 * ModelFactory.createDefaultModel(); Resource r1=
	 * model.createResource("http://www.ontologies.org/resources#R1"); Resource
	 * r2=model.createResource("http://www.ontologies.org/resources#R2");
	 * Relation rel = Relation.SIMILAR; Mapping cell12 = new Mapping(r1, r2,
	 * rel); setOfCells.add(cell12); //even if it's repeated Resource r3=
	 * model.createResource("http://www.ontologies.org/resources#R3"); Resource
	 * r4=model.createResource("http://www.ontologies.org/resources#R4");
	 * Mapping cell34 = new Mapping(r3, r4, rel); setOfCells.add(cell34);
	 * Alignment al = new Alignment(o1, o2, setOfCells); CwdfResultProcessorImpl
	 * rProc = new CwdfResultProcessorImpl(al);
	 * 
	 * rProc.serialiseSelectedAlignmentToAlignmentAPIFormat(); } catch(Exception
	 * e) { e.printStackTrace(); } }
	 */

	public static void testPairsGeneration() {
		ExperimentManager expBuild = new ExperimentManager();

		// --------------------------------------------------------------------------
		// expBuild.runTrialCartesianExperiment(); //OK

		// --------------------------------------------------------------------------
		// Ontology o1 = new Ontology("http://www.ontologies/cmt#",
		// "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
		// "CMT");
		// Ontology o2 = new Ontology("http://www.ontologies/ekaw#",
		// "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl",
		// "Ekaw");

		/*
		 * Ontology o1 = new Ontology("http://cmt",
		 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl"
		 * , "CMT"); Ontology o2 = new Ontology("http://ekaw",
		 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl"
		 * , "Ekaw");
		 * 
		 * File fReferenceAlignemnt = new File(
		 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtekaw.rdf"
		 * ); File fParticipantsAlignemnt = new File(
		 * "C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-cmt-ekaw.rdf"
		 * );
		 * 
		 * 
		 * expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignemnt,
		 * fParticipantsAlignemnt,TypeOfMappingGoal.VALIDATION, true);
		 */
		// version B
		Ontology o1b = new Ontology(
				"http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf",
				"301");
		Ontology o2b = new Ontology(
				"http://oaei.ontologymatching.org/2009/benchmarks/304/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/304.rdf",
				"304");
		File fReferenceAlignemntB = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign301304.rdf");

		expBuild.runTrialHRFPExperiment(o1b, o2b, fReferenceAlignemntB, null,
				TypeOfMappingGoal.IDENTIFICATIONA, true);

		// --------------------------------------------------------------------------

		// expBuild.runTrialAlgorithmExperiment(); //OK

	}

	public static void testRead() {
		Set<Response> setOfResponses = new HashSet<Response>();
		String idJob = null;
		try {
			HttpClient client = new DefaultHttpClient();

			// JobMicrotaskImpl job = (JobMicrotaskImpl) microtask;
			CwdfService cwdf = new CwdfService();

			// for testing
			// idJob="94847"; - the one I tried on Windows
			// idJob="96671";
			idJob = "97860";
			// idJob = job.getId();

			HttpGet getJob = new HttpGet(cwdf.getJudgmentsOfAJobURL(idJob));

			// without Accept it also worked
			// getJob.setHeader("Accept", cwdf.getJudgmentsOfAJobAccept());

			HttpResponse response = client.execute(getJob);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				ZipInputStream zip = new ZipInputStream(
						new ByteArrayInputStream(bytes));

				ZipEntry entry = zip.getNextEntry();
				while (entry != null) {
					String readed;
					InputStreamReader reader = new InputStreamReader(zip);
					BufferedReader in = new BufferedReader(reader);

					while ((readed = in.readLine()) != null) {
						System.out.println("judgment" + readed);
						// it comes in CSV not in JSON
					}
					entry = zip.getNextEntry();
				}
			} else {
				throw new Exception(
						"CrowdFlower did not success in retrieveing all the judgments related to the requested Job "
								+ response.getStatusLine().getReasonPhrase());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ==============================================================================================

	// HRFP 101-301 WITHOUT CONTEXT
	public static void test1() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf",
				"301");
		File fReferenceAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101301.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignemnt, null,
				TypeOfMappingGoal.VALIDATION, false);

	}

	// correcting 2011/301
	public static void test1b() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301_2011.rdf",
				"301");
		/*File fReferenceAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101301_2011.rdf");
*/
		File fReferenceAlignemnt = new File(
		"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101301.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignemnt, null,
				TypeOfMappingGoal.VALIDATION, false);

	}

	// HRFP 101-301 WITH CONTEXT
	public static void test2() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf",
				"301");
		File fReferenceAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101301.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignemnt, null,
				TypeOfMappingGoal.VALIDATION, true);
	}

	// with 2011/301 corrected
	public static void test2b() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301_2011.rdf",
				"301");
		/*File fReferenceAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101301_2011.rdf");
		*/
		//refalign101301 there is only one, and I update the name to the correct one - the first trial was when trying with different files because initially it was written 2010 and 2011 in the upper part, but the problem was mainly with the ontology that it should take 301-2011 and not the other version which is the one of 2010 used in 301304
		File fReferenceAlignemnt = new File(
		"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101301.rdf");

		
		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignemnt, null,
				TypeOfMappingGoal.VALIDATION, true);
	}

	// HRFP 101-204 WITHOUT CONTEXT
	public static void test3() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/204/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/204.rdf",
				"204");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101204.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.VALIDATION, false);

	}

	// HRFP 101-204 WITH CONTEXT
	public static void test4() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/204/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/204.rdf",
				"204");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101204.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP 101-205 WITHOUT CONTEXT
	public static void test5() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/205/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/205.rdf",
				"205");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101205.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.VALIDATION, false);

	}

	// HRFP 101-205 WITH CONTEXT
	public static void test6() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/101/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/101.rdf",
				"101");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2011/benchmarks/205/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/205.rdf",
				"205");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign101205.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.VALIDATION, true);

	}

	// ==============================================================================================

	// HRFP cmt-conference WITH CONTEXT
	public static void test7() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://cmt",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
				"cmt");
		Ontology o2 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtconference.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-cmt-conference.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP cmt-confOf WITH CONTEXT
	public static void test8() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://cmt",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
				"cmt");
		Ontology o2 = new Ontology(
				"http://confOf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/confof.owl",
				"confof");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtconfOf.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-cmt-confof.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP cmt-edas WITH CONTEXT
	public static void test9() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://cmt",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
				"cmt");
		Ontology o2 = new Ontology(
				"http://edas",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/edas.owl",
				"edas");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtedas.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-cmt-edas.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP cmt-ekaw WITH CONTEXT
	public static void test10() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://cmt",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
				"cmt");
		Ontology o2 = new Ontology(
				"http://ekaw",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl",
				"ekaw");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtekaw.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-cmt-ekaw.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP cmt-iasted WITH CONTEXT
	public static void test11() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://cmt",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
				"cmt");
		Ontology o2 = new Ontology(
				"http://iasted",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/iasted.owl",
				"iasted");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtiasted.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-cmt-iasted.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP cmt-sigkdd WITH CONTEXT
	public static void test12() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://cmt",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/cmt.owl",
				"cmt");
		Ontology o2 = new Ontology(
				"http://sigkdd",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/sigkdd.owl",
				"sigkdd");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligncmtsigkdd.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-cmt-sigkdd.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP conference-confOf WITH CONTEXT
	public static void test13() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");
		Ontology o2 = new Ontology(
				"http://confOf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/confof.owl",
				"confof");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconferenceconfof.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-conference-confof.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP conference-edas WITH CONTEXT
	public static void test14() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");
		Ontology o2 = new Ontology(
				"http://edas",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/edas.owl",
				"edas");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconferenceedas.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-conference-edas.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP conference-ekaw WITH CONTEXT
	public static void test15() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");
		Ontology o2 = new Ontology(
				"http://ekaw",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl",
				"ekaw");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconferenceekaw.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-conference-ekaw.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP conference-iasted WITH CONTEXT
	public static void test16() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");
		Ontology o2 = new Ontology(
				"http://iasted",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/iasted.owl",
				"iasted");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconferenceiasted.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-conference-iasted.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP conference-sigkdd WITH CONTEXT
	public static void test17() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");
		Ontology o2 = new Ontology(
				"http://sigkdd",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/sigkdd.owl",
				"sigkdd");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconferencesigkdd.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-conference-sigkdd.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP confOf-edas WITH CONTEXT
	public static void test18() {

		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://confOf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/confof.owl",
				"confof");
		Ontology o2 = new Ontology(
				"http://edas",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/edas.owl",
				"edas");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconfofedas.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-confof-edas.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP confOf-ekaw WITH CONTEXT
	public static void test19() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://confOf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/confof.owl",
				"confof");
		Ontology o2 = new Ontology(
				"http://ekaw",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl",
				"ekaw");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconfofekaw.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-confof-ekaw.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP confOf-iasted WITH CONTEXT
	public static void test20() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://confOf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/confof.owl",
				"confof");
		Ontology o2 = new Ontology(
				"http://iasted",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/iasted.owl",
				"iasted");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconfofiasted.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-confof-iasted.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP confOf-sigkdd WITH CONTEXT
	public static void test21() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://confOf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/confof.owl",
				"confof");
		Ontology o2 = new Ontology(
				"http://sigkdd",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/sigkdd.owl",
				"sigkdd");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignconfofsigkdd.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-confof-sigkdd.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP edas-ekaw WITH CONTEXT
	public static void test22() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://edas",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/edas.owl",
				"edas");
		Ontology o2 = new Ontology(
				"http://ekaw",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl",
				"ekaw");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignedasekaw.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-edas-ekaw.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP edas-iasted WITH CONTEXT
	public static void test23() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://edas",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/edas.owl",
				"edas");
		Ontology o2 = new Ontology(
				"http://iasted",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/iasted.owl",
				"iasted");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignedasiasted.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-edas-iasted.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP edas-sigkdd WITH CONTEXT
	public static void test24() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://edas",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/edas.owl",
				"edas");
		Ontology o2 = new Ontology(
				"http://sigkdd",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/sigkdd.owl",
				"sigkdd");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignedassigkdd.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-edas-sigkdd.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP ekaw-iasted WITH CONTEXT
	public static void test25() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://ekaw",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl",
				"ekaw");
		Ontology o2 = new Ontology(
				"http://iasted",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/iasted.owl",
				"iasted");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignekawiasted.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-ekaw-iasted.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP ekaw-sigkdd WITH CONTEXT
	public static void test26() {

		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://ekaw",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/ekaw.owl",
				"ekaw");
		Ontology o2 = new Ontology(
				"http://sigkdd",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/sigkdd.owl",
				"sigkdd");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignekawsigkdd.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-ekaw-sigkdd.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// HRFP iasted-sigkdd WITH CONTEXT
	public static void test27() {

		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://iasted",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/iasted.owl",
				"iasted");
		Ontology o2 = new Ontology(
				"http://sigkdd",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/sigkdd.owl",
				"sigkdd");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refaligniastedsigkdd.rdf");
		File fParticipantsAlignemnt = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/agrmaker-iasted-sigkdd.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment,
				fParticipantsAlignemnt, TypeOfMappingGoal.VALIDATION, true);

	}

	// ==============================================================================================

	// HRFP 301-304 WITHOUT CONTEXT
	public static void test28() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf",
				"301");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2009/benchmarks/304/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/304.rdf",
				"304");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign301304.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.IDENTIFICATIONA, false);

	}

	// HRFP 301-304 WITH CONTEXT
	public static void test29() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf",
				"301");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2009/benchmarks/304/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/304.rdf",
				"304");
		File fReferenceAlignment = new File(
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalign301304.rdf");

		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.IDENTIFICATIONA, true);

	}

	// ==============================================================================================

	// Cartesian 301-304 WITH CONTEXT
	public static void test30() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://oaei.ontologymatching.org/2010/benchmarks/301/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/301.rdf",
				"301");
		Ontology o2 = new Ontology(
				"http://oaei.ontologymatching.org/2009/benchmarks/304/onto.rdf",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/304.rdf",
				"304");

		expBuild.runTrialCartesianExperiment(o1, o2,
				TypeOfMappingGoal.IDENTIFICATIONB, true);

	}
	
	// ------------------------------------- BIOONTOLOGIES ---------------
	// HRFP Human - Mouse WITH CONTEXT
	public static void test31() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://mouse.owl",
				"C:/Users/csarasua/workspace/ISWC2012experiment/ontologies/mouse.owl",
				"mouse");
		Ontology o2 = new Ontology(
				"http://human.owl",
				"C:/Users/csarasua/workspace/ISWC2012experiment/ontologies/human.owl",
				"human");
				
		
		/*Ontology o1 = new Ontology(
		"http://mouse.owl",
		"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/mouse.owl",
		"mouse");
Ontology o2 = new Ontology(
		"http://human.owl",
		"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/human.owl",
		"human");
		*/
		File fReferenceAlignment = new File(
		"C:/Users/csarasua/workspace/ISWC2012experiment/referencealignments/refalignmousehuman.rdf");


	
		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.VALIDATION, true);
		

	}
	
	// HRFP BodySystem - BIRNlex (BIOPORTAL) WITH CONTEXT
	public static void test32() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://who.int/bodysystem.owl",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/bodysystem.owl",
				"bodysystem");
		Ontology o2 = new Ontology(
				"http://bioontology.org/projects/ontologies/birnlex",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/birnlex.owl",
				"birnlex");

		File fReferenceAlignment = new File(
		"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignbodysystembirnlex.rdf");


	
		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.VALIDATION, true);
		

	}
	
	// HRFP ABA adult mouse brain - BIRNlex WITH CONTEXT
	public static void test33() {
		ExperimentManager expBuild = new ExperimentManager();

		Ontology o1 = new Ontology(
				"http://mouse.brain-map.org/atlas/index.html",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/aba.owl",
				"aba");
		Ontology o2 = new Ontology(
				"http://bioontology.org/projects/ontologies/birnlex",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/birnlex.owl",
				"birnlex");

		File fReferenceAlignment = new File(
		"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/referencealignments/refalignababirnlex.rdf");


	
		expBuild.runTrialHRFPExperiment(o1, o2, fReferenceAlignment, null,
				TypeOfMappingGoal.VALIDATION, true);
		

	}
	
	// (LOOM) Imp NCI Thesaurus - ABA Adult Mouse WITH CONTEXT -- both from BioPortal
		public static void test34() {
			ExperimentManager expBuild = new ExperimentManager();

			Ontology o1 = new Ontology(
					"http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl",
					"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/nci.owl",
					"nci");
			Ontology o2 = new Ontology(
					"http://mouse.brain-map.org/atlas/index.html",
					"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/aba.owl",
					"aba");
			//the ABA URI is what they show in the file

			File fParticipantAlignment = new File(
			"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/participantalignments/loom-nci-aba.rdf");


			expBuild.runTrialAlgorithmExperiment(o1, o2, fParticipantAlignment, TypeOfMappingGoal.VALIDATION, true);
		
			

		}
	
	public static void testNullPointerDefaultLabel()
	{
		Ontology o2 = new Ontology(
				"http://conference",
				"C:/Users/csarasua/workspace_PHD/ISWC2012experiment/ontologies/conference.owl",
				"conference");
		Set<Resource>  listClasses= o2.listClassesToBeMapped();
		for (Resource r: listClasses)
		{
			System.out.println("class: "+r.getURI());
			
			
		}
	}
	public static void checkNumbers()
	{
		/*this method was implemented to obtain the Precision and Recall values that the AROMA algorithm shows in the 301-304 alignment.
		this was needed to show the table with the results, in order to compare the result of CrowdMAP and the AROMA algorithm (Imp 301-304 AROMA algorithm)
		*/
		
		File fCrowd = new File(
		//"crowdalignments/cwdalign301304_CartPImp.rdf");
		"participantalignments/aroma301304.rdf");
File fReference = new File("referencealignments/refalign301304_onlyequal.rdf");

		ResultEvaluatorImpl ev = new ResultEvaluatorImpl(fCrowd, fReference);
		ev.evaluateResultsFromCrowdPR();
		ev.printResultsInConsole();
	}
}
