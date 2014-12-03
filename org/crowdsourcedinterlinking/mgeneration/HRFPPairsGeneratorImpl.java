package org.crowdsourcedinterlinking.mgeneration;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.crowdsourcedinterlinking.evaluation.DiffMappings;
import org.crowdsourcedinterlinking.evaluation.ResultEvaluator;
import org.crowdsourcedinterlinking.evaluation.ResultEvaluatorImpl;
import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.AlignmentParser;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Pair;
import org.crowdsourcedinterlinking.model.Relation;
import org.crowdsourcedinterlinking.util.ClassesAndProperties;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.ObjectMapping;
import org.crowdsourcedinterlinking.util.Time;
import org.semanticweb.owl.align.Cell;

import com.google.common.io.Files;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import fr.inrialpes.exmo.align.impl.BasicParameters;
import fr.inrialpes.exmo.align.impl.eval.PRecEvaluator;

public class HRFPPairsGeneratorImpl extends PairsGeneratorImpl {

	private File referenceAlignment;
	private File participantsAlgorithmAlignment;
	
	

	public HRFPPairsGeneratorImpl(Ontology o1, Ontology o2,
			File referenceAlignment, File participantsAlgorithmAlignment) {
		try {
			this.setOntology1(o1);
			this.setOntology2(o2);

			this.referenceAlignment = referenceAlignment;
			this.participantsAlgorithmAlignment = participantsAlgorithmAlignment;
			
			this.loadAlignmentElements();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Alignment generatePairs() {

		
		
		
		Alignment result = null;
		Set<Mapping> setOfMappings = new HashSet<Mapping>();
		Set<Mapping> setOfParticipantsMappings = new HashSet<Mapping>();

		try {

			this.registerOntologiesInTrackFile();

			Model model = ModelFactory.createDefaultModel();

			AlignmentParser p = new AlignmentParser(this.referenceAlignment, oMap);
			

			p.parseAlignment(this.getOntology1(), this.getOntology2());
			// they will contain relations, but when generating the type of
			// Microtask, the relation should be hidden
			Alignment refAlign = p.getAlignment();
			Set<Mapping> setOfRefMappings = refAlign.getSetOfMappings();
			int numberOfRefMappings = setOfRefMappings.size();
			int mappingsLeft = numberOfRefMappings;

			File resultsFile = new File(
					"C:/Users/ASUS12DE/workspace/ISWC2012experiment/testPairsGeneration.txt");

			Files.write("HRFP " + Time.currentTime(), resultsFile,
					Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, resultsFile, Charset.defaultCharset());
			
			Set<Resource> setClassesOnto1 = null;
			Set<Resource> setClassesOnto2 = null; 
			
			Set<Resource> setDataytpePropertiesOnto1 = null;
			Set<Resource> setDataytpePropertiesOnto2 = null;

			Set<Resource> setObjectPropertiesOnto1 = null;
			Set<Resource> setObjectPropertiesOnto2 = null;
			
			//CLASSES
			if(oMap.equals(ObjectMapping.CLASSES)){
			setClassesOnto1 = this.getOntology1()
					.listClassesToBeMapped();
			setClassesOnto2 = this.getOntology2()
					.listClassesToBeMapped();

			Files.append("Mapping", resultsFile, Charset.defaultCharset());
			Files.append(ls, resultsFile, Charset.defaultCharset());

			Files.append("#Classes in Ontology1: " + setClassesOnto1.size(),
					resultsFile, Charset.defaultCharset());
			Files.append(ls, resultsFile, Charset.defaultCharset());
			Files.append("#Classes in Ontology2: " + setClassesOnto2.size(),
					resultsFile, Charset.defaultCharset());
			Files.append(ls, resultsFile, Charset.defaultCharset());
			
			}
			

			
			//PROPERTIES
			else if(oMap.equals(ObjectMapping.PROPERTIES))
			{
			setDataytpePropertiesOnto1 = this.getOntology1().listDatatypePropertiesToBeMapped();
			setDataytpePropertiesOnto2 = this.getOntology2().listDatatypePropertiesToBeMapped();

			setObjectPropertiesOnto1 = this.getOntology1().listObjectPropertiesToBeMapped();
			setObjectPropertiesOnto2 = this.getOntology2().listObjectPropertiesToBeMapped();
			}
			
			if (this.participantsAlgorithmAlignment != null) {

				ResultEvaluatorImpl resEval = new ResultEvaluatorImpl(
						this.participantsAlgorithmAlignment,
						this.referenceAlignment);
				DiffMappings diffMappings = resEval.evaluateDiff();

				Files.append("# REF alignments: " + numberOfRefMappings,
						resultsFile, Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());

				int falsePositives = diffMappings.getFalsePositives().size();
				int falseNegatives = diffMappings.getFalseNegatives().size();
				int candidates = falsePositives + falseNegatives;
				Files.append("# Candidate Participants alignments: "
						+ candidates, resultsFile, Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());

				/*
				 * System.out.println("--- Ref mappings: ---- "); for (Mapping
				 * m: setOfRefMappings) {
				 * 
				 * System.out.println("source: "+m.getElementA().getURI());
				 * System.out.println("relation: "+m.getRelation());
				 * System.out.println("target: "+m.getElementB().getURI()); }
				 * 
				 * Set<Cell> setFalsePositives =
				 * diffMappings.getFalsePositives();
				 * System.out.println("------- False positives: -----"); for
				 * (Cell c1: setFalsePositives) {
				 * 
				 * System.out.println("source: "+c1.getObject1AsURI().toString())
				 * ;
				 * System.out.println("relation: "+c1.getRelation().getRelation
				 * ());
				 * System.out.println("target: "+c1.getObject2AsURI().toString
				 * ());
				 * 
				 * 
				 * } Set<Cell> setFalseNegatives =
				 * diffMappings.getFalseNegatives();
				 * System.out.println("---- False negatives: ----"); for (Cell
				 * c2: setFalseNegatives) {
				 * 
				 * System.out.println("source: "+c2.getObject1AsURI().toString())
				 * ;
				 * System.out.println("relation: "+c2.getRelation().getRelation
				 * ());
				 * System.out.println("target: "+c2.getObject2AsURI().toString
				 * ());
				 * 
				 * 
				 * }
				 */

				for (Cell c : diffMappings.getFalsePositives()) {
					if (mappingsLeft > 0) {
						Resource r1 = model.createResource(c.getObject1AsURI()
								.toString());
						Resource r2 = model.createResource(c.getObject2AsURI()
								.toString());
						String rel = c.getRelation().getRelation();
						Relation relat = null;
						if (rel.equals("=")) {
							relat = Relation.SIMILAR;
						} else if (rel.equals(">") || rel.equals("&gt;")
								|| rel.equals("&#62;")) {
							relat = Relation.GENERAL;
						} else if (rel.equals("<") || rel.equals("&lt;")
								|| rel.equals("&#60;")) {
							relat = Relation.SPECIFIC;
						}

						System.out.println("falsePositives: r1:" + r1.getURI()
								+ " r2: " + r2.getURI());

						//here the distinction between Classes and Properties is only known by comparing the lower / upper case
						if ((oMap.equals(ObjectMapping.CLASSES) && ClassesAndProperties.isClass(r1)
								&& ClassesAndProperties.isClass(r2)) || (oMap.equals(ObjectMapping.PROPERTIES) && ClassesAndProperties.isProperty(r1)
										&& ClassesAndProperties.isProperty(r2)) ){
							
							System.out.println("selected! mappingsLeft: "
									+ mappingsLeft);
							Mapping map = new Mapping(r1, r2, relat);
							if (!isSelected(setOfRefMappings,
									setOfParticipantsMappings, map)) {
								setOfParticipantsMappings.add(map);
								mappingsLeft = mappingsLeft - 1;
							}
						} else {
							System.out.println("not selected mappingsLeft: "
									+ mappingsLeft);

					
						}
					} else {
						break;
					}

				}
				if (mappingsLeft > 0) {
					for (Cell c2 : diffMappings.getFalseNegatives()) {
						if (mappingsLeft > 0) {
							Resource r1 = model.createResource(c2
									.getObject1AsURI().toString());
							Resource r2 = model.createResource(c2
									.getObject2AsURI().toString());
							String rel = c2.getRelation().getRelation();
							Relation relat = null;
							if (rel.equals("=")) {
								relat = Relation.SIMILAR;
							}
							if (rel.equals(">") || rel.equals("&gt;")
									|| rel.equals("&#62;")) {
								relat = Relation.GENERAL;
							}
							if (rel.equals("<") || rel.equals("&lt;")
									|| rel.equals("&#60;")) {
								relat = Relation.SPECIFIC;
							}

							System.out.println("falseNegatives: r1:"
									+ r1.getURI() + " r2: " + r2.getURI());

							if ((oMap.equals(ObjectMapping.CLASSES) && ClassesAndProperties.isClass(r1)
									&& ClassesAndProperties.isClass(r2)) || (oMap.equals(ObjectMapping.PROPERTIES) && ClassesAndProperties.isProperty(r1)
											&& ClassesAndProperties.isProperty(r2)) ){
								System.out.println("selected! mappingsLeft: "
										+ mappingsLeft);

								Mapping map = new Mapping(r1, r2, relat);
								if (!isSelected(setOfRefMappings,
										setOfParticipantsMappings, map)) {
									setOfParticipantsMappings.add(map);
									mappingsLeft = mappingsLeft - 1;
								}

							} else {

								System.out
										.println("not selected mappingsLeft: "
												+ mappingsLeft);
							}
						} else {
							break;
						}

					}
					if (mappingsLeft > 0) {
						///int maxIndex = setOfRefMappings.size()- setOfParticipantsMappings.size();

						// Preparation
						ArrayList<Object> list1 = new ArrayList<Object>();
						ArrayList<Object> list2 = new ArrayList<Object>();
						
						ArrayList<Object> list3 = new ArrayList<Object>();
						ArrayList<Object> list4 = new ArrayList<Object>();

						if (oMap.equals(ObjectMapping.CLASSES))
						{
						list1.addAll(setClassesOnto1);
						list2.addAll(setClassesOnto2);
						}
						else if(oMap.equals(ObjectMapping.PROPERTIES))
						{
							list1.addAll(setDataytpePropertiesOnto1);
							list2.addAll(setDataytpePropertiesOnto2);
							
							list3.addAll(setObjectPropertiesOnto1);
							list4.addAll(setObjectPropertiesOnto2);
						}
						// for (int i=0; i<maxIndex; i++) //with this
						// mappingsLeft>0 is guaranteed, no need to check inside
						// the for
						while (mappingsLeft > 0 && !list1.isEmpty() && !list2.isEmpty()) {
							Pair pairOfRandomClasses = getRandomPair(list1,
									list2);
							Mapping map = new Mapping(
									(Resource) pairOfRandomClasses.getLeft(),
									(Resource) pairOfRandomClasses.getRight(),
									Relation.SIMILAR);
							map.setInvented(true);
							System.out
									.println("generate RANDOM: MAP elementA: "
											+ map.getElementA().getURI()
											+ "MAP elementB: "
											+ map.getElementB().getURI());
							if (!isSelected(setOfRefMappings,
									setOfParticipantsMappings, map)) {
								setOfParticipantsMappings.add(map);
								mappingsLeft = mappingsLeft - 1; // added
																	// otherwise
																	// it will
																	// not
																	// control
																	// whether
																	// it was
																	// added or
																	// not

							}
						}
						while (mappingsLeft > 0 && !list3.isEmpty() && !list4.isEmpty()) {
							Pair pairOfRandomClasses = getRandomPair(list3,
									list4);
							Mapping map = new Mapping(
									(Resource) pairOfRandomClasses.getLeft(),
									(Resource) pairOfRandomClasses.getRight(),
									Relation.SIMILAR);
							map.setInvented(true);
							System.out
									.println("generate RANDOM: MAP elementA: "
											+ map.getElementA().getURI()
											+ "MAP elementB: "
											+ map.getElementB().getURI());
							if (!isSelected(setOfRefMappings,
									setOfParticipantsMappings, map)) {
								setOfParticipantsMappings.add(map);
								mappingsLeft = mappingsLeft - 1; // added
																	// otherwise
																	// it will
																	// not
																	// control
																	// whether
																	// it was
																	// added or
																	// not

							}
						}
					
				}
				}

			} else { // this.participantsAlgorithmAlignment == null

				/*// Preparation
				ArrayList<Object> list1 = new ArrayList<Object>();
				ArrayList<Object> list2 = new ArrayList<Object>();

				list1.addAll(setClassesOnto1);
				list2.addAll(setClassesOnto2);

				// for (int i=0; i<numberOfRefMappings+1; i++)
				while (mappingsLeft > 0) {
					Pair pairOfRandomClasses = getRandomPair(list1, list2);
					Mapping map = new Mapping(
							(Resource) pairOfRandomClasses.getLeft(),
							(Resource) pairOfRandomClasses.getRight(),
							Relation.SIMILAR);
					map.setInvented(true);
					System.out.println("generate RANDOM: MAP elementA: "
							+ map.getElementA().getURI() + "MAP elementB: "
							+ map.getElementB().getURI());
					if (!isSelected(setOfRefMappings,
							setOfParticipantsMappings, map)) {
						setOfParticipantsMappings.add(map);
						mappingsLeft = mappingsLeft - 1;
					}

				}*/
				
				// Preparation
				ArrayList<Object> list1 = new ArrayList<Object>();
				ArrayList<Object> list2 = new ArrayList<Object>();
				
				ArrayList<Object> list3 = new ArrayList<Object>();
				ArrayList<Object> list4 = new ArrayList<Object>();

				if (oMap.equals(ObjectMapping.CLASSES))
				{
				list1.addAll(setClassesOnto1);
				list2.addAll(setClassesOnto2);
				}
				else if(oMap.equals(ObjectMapping.PROPERTIES))
				{
					list1.addAll(setDataytpePropertiesOnto1);
					list2.addAll(setDataytpePropertiesOnto2);
					
					list3.addAll(setObjectPropertiesOnto1);
					list4.addAll(setObjectPropertiesOnto2);
				}
				// for (int i=0; i<maxIndex; i++) //with this
				// mappingsLeft>0 is guaranteed, no need to check inside
				// the for
				while (mappingsLeft > 0 && !list1.isEmpty() && !list2.isEmpty()) {
					Pair pairOfRandomClasses = getRandomPair(list1,
							list2);
					Mapping map = new Mapping(
							(Resource) pairOfRandomClasses.getLeft(),
							(Resource) pairOfRandomClasses.getRight(),
							Relation.SIMILAR);
					map.setInvented(true);
					System.out
							.println("generate RANDOM: MAP elementA: "
									+ map.getElementA().getURI()
									+ "MAP elementB: "
									+ map.getElementB().getURI());
					if (!isSelected(setOfRefMappings,
							setOfParticipantsMappings, map)) {
						setOfParticipantsMappings.add(map);
						mappingsLeft = mappingsLeft - 1; // added
															// otherwise
															// it will
															// not
															// control
															// whether
															// it was
															// added or
															// not

					}
				}
				while (mappingsLeft > 0 && !list3.isEmpty() && !list4.isEmpty()) {
					Pair pairOfRandomClasses = getRandomPair(list3,
							list4);
					Mapping map = new Mapping(
							(Resource) pairOfRandomClasses.getLeft(),
							(Resource) pairOfRandomClasses.getRight(),
							Relation.SIMILAR);
					map.setInvented(true);
					System.out
							.println("generate RANDOM: MAP elementA: "
									+ map.getElementA().getURI()
									+ "MAP elementB: "
									+ map.getElementB().getURI());
					if (!isSelected(setOfRefMappings,
							setOfParticipantsMappings, map)) {
						setOfParticipantsMappings.add(map);
						mappingsLeft = mappingsLeft - 1; // added
															// otherwise
															// it will
															// not
															// control
															// whether
															// it was
															// added or
															// not

					}
				}

			}

			for (Mapping m : setOfRefMappings) {
				System.out.println("Refmappings " + m.getElementA().getURI()
						+ ", " + m.getElementB().getURI() + ","
						+ m.getRelation());
			}

			for (Mapping m2 : setOfParticipantsMappings) {
				System.out.println("Participants " + m2.getElementA().getURI()
						+ ", " + m2.getElementB().getURI() + ","
						+ m2.getRelation());
			}
			setOfMappings.addAll(setOfRefMappings);
			setOfMappings.addAll(setOfParticipantsMappings);

			Files.append(
					"number of pairs generated for ontologies:"
							+ this.getOntology1().getName() + " AND "
							+ this.getOntology2().getName() + " is: "
							+ setOfMappings.size(), resultsFile,
					Charset.defaultCharset());

			int i = 0;
			for (Mapping m : setOfMappings) {
				Files.append("Mapping m" + i + ": ", resultsFile,
						Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());
				Files.append("Element1: " + m.getElementA().getURI(),
						resultsFile, Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());
				Files.append("Element2: " + m.getElementB().getURI(),
						resultsFile, Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());
				i = i + 1;

			}

			result = new Alignment(this.getOntology1(), this.getOntology2(),
					setOfMappings);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	private Pair getRandomPair(ArrayList<Object> list1, ArrayList<Object> list2) {
		Random r = new Random();

		int random1 = r.nextInt(list1.size());
		int random2 = r.nextInt(list2.size());

		return new Pair(list1.get(random1), list2.get(random2));
	}

	private boolean isSelected(Set<Mapping> ref, Set<Mapping> part, Mapping map) {
		System.out.println("---at isSelected ----");
		System.out.println("map is the mapping being checked as unique: uriA  "
				+ map.getElementA().getURI() + "uriB "
				+ map.getElementB().getURI());

		for (Mapping m : ref) {
			System.out.println("m: uriA  " + m.getElementA().getURI() + "uriB "
					+ m.getElementB().getURI());

			if ((m.getElementA().getURI().equals(map.getElementA().getURI()))
					&& (m.getElementB().getURI().equals(map.getElementB()
							.getURI()))) {

				return true;
			}
		}

		for (Mapping m2 : part) {
			System.out.println("m2: uriA  " + m2.getElementA().getURI()
					+ "uriB " + m2.getElementB().getURI());

			if ((m2.getElementA().getURI().equals(map.getElementA().getURI()))
					&& (m2.getElementB().getURI().equals(map.getElementB()
							.getURI()))) {
				return true;
			}
		}

		return false;

	}
}
