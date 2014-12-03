package org.crowdsourcedinterlinking.test;

import java.io.File;
import java.util.Properties;

import org.semanticweb.owl.align.Alignment;
import org.semanticweb.owl.align.AlignmentException;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.ObjectAlignment;
import fr.inrialpes.exmo.align.impl.URIAlignment;
import fr.inrialpes.exmo.align.impl.eval.ExtPREvaluator;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;
import fr.inrialpes.exmo.align.impl.eval.SemPRecEvaluator;
import fr.inrialpes.exmo.align.parser.AlignmentParser;

public class TestEvaluators {

	public static void main(String[] args) {
		//testSemanticEvaluator();
		//testExtEvaluator();
		testPREvaluator();
	}

	private static void testExtEvaluator() {
		try {
			//needs the OWL API to be in the build path

			System.out.println("Extc Evaluator");

			File fCrowd = new File("crowdalignments/cwdaligntest.rdf");
			File fReference = new File("referencealignments/refaligntest.rdf");

			AlignmentParser aparser = new AlignmentParser(0);

			
			Alignment crowd = aparser.parse(fCrowd.toURI().toString());
			Alignment reference = aparser.parse(fReference.toURI().toString());

			/*
			 * Properties p = new BasicParameters(); PRecEvaluator evaluator =
			 * new PRecEvaluator(reference, crowd); evaluator.eval(p);
			 */

			// needs and ontology
			// PelletReasoner reasoner =
			// PelletReasonerFactory.getInstance().createReasoner( );

			Properties p = new BasicParameters();

			// SemPRecEvaluator evaluator = new SemPRecEvaluator(reference,
			// crowd);

			
			Alignment oaReference = ObjectAlignment.toObjectAlignment((URIAlignment)reference);

			Alignment oaCrowd = ObjectAlignment.toObjectAlignment((URIAlignment)crowd);
			
			//ExtPREvaluator evaluator = new ExtPREvaluator(reference, crowd);
			ExtPREvaluator evaluator = new ExtPREvaluator(oaReference, oaCrowd);

			evaluator.eval(p);

			System.out.println("------------------");
			System.out.println("PRECISION ORIENTED PRECISION: "
					+ evaluator.getPrecisionOrientedPrecision());
			System.out.println("RECALL ORIENTED PRECISION: "
					+ evaluator.getRecallOrientedPrecision());
			System.out.println("------------------");

			System.out.println("RECALL ORIENTED RECALL: "
					+ evaluator.getRecallOrientedRecall());
			System.out.println("PRECISION ORIENTED RECALL: "
					+ evaluator.getPrecisionOrientedRecall());
			System.out.println("------------------");

			System.out.println("EFF PRECISION:" + evaluator.getEffPrecision());
			System.out.println("EFF RECALL:" + evaluator.getEffRecall());
			System.out.println("------------------");

			System.out.println("SYM PRECISION:" + evaluator.getSymPrecision());
			System.out.println("SYM RECALL:" + evaluator.getSymRecall());
			System.out.println("------------------");

		} catch (AlignmentException e) {
			e.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private static void testSemanticEvaluator() {

		try {

			System.out.println("Semantic Evaluator");
			File fCrowd = new File("crowdalignments/cwdaligntest.rdf");
			File fReference = new File("referencealignments/refaligntest.rdf");

			fr.inrialpes.exmo.align.parser.AlignmentParser aparser = new fr.inrialpes.exmo.align.parser.AlignmentParser(
					0);

			org.semanticweb.owl.align.Alignment crowd = aparser.parse(fCrowd
					.toURI());
			org.semanticweb.owl.align.Alignment reference = aparser
					.parse(fReference.toURI());

			Properties p = new BasicParameters();

			SemPRecEvaluator evaluator = new SemPRecEvaluator(reference, crowd);
			evaluator.equals(p);
			
			//???

		} catch (AlignmentException e) {
			e.printStackTrace();
		}

	}

	private static void testPREvaluator() {

		try {
			/*File fCrowd = new File("crowdalignments/cwdaligntest.rdf");
			File fReference = new File("referencealignments/refaligntest.rdf");*/
			
			File fCrowd = new File("crowdalignments/cwdalign101204_label+superclasses.rdf");
			File fReference = new File("referencealignments/refalign101204.rdf");

			fr.inrialpes.exmo.align.parser.AlignmentParser aparser = new fr.inrialpes.exmo.align.parser.AlignmentParser(
					0);

			org.semanticweb.owl.align.Alignment crowd = aparser.parse(fCrowd
					.toURI());
			org.semanticweb.owl.align.Alignment reference = aparser
					.parse(fReference.toURI());

			Properties p = new BasicParameters();
			PRecEvaluator evaluator2 = new PRecEvaluator(reference, crowd);
			evaluator2.eval(p);

			System.out.println("PRecEvaluator");
			System.out.println("------------------");

			System.out.println("PRECISION:" + evaluator2.getPrecision());
			System.out.println("RECALL:" + evaluator2.getRecall());
			System.out.println("------------------");
		} catch (AlignmentException e) {
			e.printStackTrace();
		}

	}

}
