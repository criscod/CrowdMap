package org.crowdsourcedinterlinking.mgeneration;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.Relation;
import org.crowdsourcedinterlinking.util.ClassesAndProperties;
import org.crowdsourcedinterlinking.util.ObjectMapping;
import org.crowdsourcedinterlinking.util.Time;

import com.google.common.io.Files;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class CartesianPairsGeneratorImpl extends PairsGeneratorImpl {

	// ontology1 and ontology 2 are inherited from PairsGeneratorImpl

	public CartesianPairsGeneratorImpl(Ontology o1, Ontology o2) {
		try {
			this.setOntology1(o1);
			this.setOntology2(o2);
			
			this.loadAlignmentElements();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * public Alignment generatePairs() { Alignment result = null; Set<Mapping>
	 * setOfMappings = new HashSet<Mapping>();
	 * 
	 * Mapping map;
	 * 
	 * 
	 * File resultsFile = new File(
	 * "/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/testPairsGeneration.txt"
	 * );
	 * 
	 * 
	 * try {
	 * 
	 * 
	 * Files.write("CARTESIAN"+ Time.currentTime(), resultsFile,
	 * Charset.defaultCharset()); String ls =
	 * System.getProperty("line.separator"); Files.append(ls, resultsFile,
	 * Charset.defaultCharset());
	 * 
	 * 
	 * 
	 * OntModel o1 = ModelFactory.createOntologyModel();
	 * o1.add(this.getOntology1().getModel());
	 * 
	 * ExtendedIterator<OntClass> eIt=o1.listClasses(); Set<Resource>
	 * setClassesOnto1 = new HashSet<Resource>(); while (eIt.hasNext()) {
	 * setClassesOnto1.add((eIt.next().asResource())); }
	 * 
	 * 
	 * 
	 * OntModel o2 = ModelFactory.createOntologyModel();
	 * o2.add(this.getOntology2().getModel());
	 * 
	 * ExtendedIterator<OntClass> eIt2=o2.listClasses();
	 * 
	 * Set<Resource> setClassesOnto2 = new HashSet<Resource>(); while
	 * (eIt2.hasNext()) { setClassesOnto2.add(eIt2.next().asResource()); }
	 * 
	 * for (Resource class1: setClassesOnto1) {
	 * 
	 * for (Resource class2: setClassesOnto1) {
	 * 
	 * Files.append("Mapping", resultsFile, Charset.defaultCharset());
	 * Files.append(ls, resultsFile, Charset.defaultCharset());
	 * 
	 * 
	 * Files.append("Class1: "+class1.getURI(), resultsFile,
	 * Charset.defaultCharset()); Files.append(ls, resultsFile,
	 * Charset.defaultCharset());
	 * 
	 * 
	 * 
	 * 
	 * Files.append("Class2: "+class2.getURI(), resultsFile,
	 * Charset.defaultCharset()); Files.append(ls, resultsFile,
	 * Charset.defaultCharset());
	 * 
	 * map = new Mapping(class1, class2, Relation.UNKNOWN);
	 * setOfMappings.add(map); } }
	 * 
	 * 
	 * 
	 * result = new Alignment(this.getOntology1(), this.getOntology2(),
	 * setOfMappings);
	 * 
	 * 
	 * } catch(Exception e) { e.printStackTrace(); }
	 * 
	 * 
	 * return result;
	 * 
	 * 
	 * }
	 */

	public Alignment generatePairs() {
		Alignment result = null;
		Set<Mapping> setOfMappings = new HashSet<Mapping>();

		Mapping map;
		
		

		File resultsFile = new File(
				"/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/testPairsGeneration.txt");

		try {
			this.registerOntologiesInTrackFile();

			Files.write("CARTESIAN " + Time.currentTime(), resultsFile,
					Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, resultsFile, Charset.defaultCharset());

			
			
			if (oMap.equals(ObjectMapping.CLASSES))
			{
			
			Set<Resource> setClassesOnto1 = this.getOntology1()
					.listClassesToBeMapped();
			Set<Resource> setClassesOnto2 = this.getOntology2()
					.listClassesToBeMapped();

			Files.append("Mapping", resultsFile, Charset.defaultCharset());
			Files.append(ls, resultsFile, Charset.defaultCharset());

			Files.append("#Classes in Ontology1: " + setClassesOnto1.size(),
					resultsFile, Charset.defaultCharset());
			Files.append(ls, resultsFile, Charset.defaultCharset());
			Files.append("#Classes in Ontology2: " + setClassesOnto2.size(),
					resultsFile, Charset.defaultCharset());
			Files.append(ls, resultsFile, Charset.defaultCharset());

			for (Resource class1 : setClassesOnto1) {

				for (Resource class2 : setClassesOnto2) {

					/*
					 * Files.append("Class1: "+class1.getURI(), resultsFile,
					 * Charset.defaultCharset()); Files.append(ls, resultsFile,
					 * Charset.defaultCharset());
					 */

					/*
					 * Files.append("Class2: "+class2.getURI(), resultsFile,
					 * Charset.defaultCharset()); Files.append(ls, resultsFile,
					 * Charset.defaultCharset());
					 */

					map = new Mapping(class1, class2, Relation.UNKNOWN);
					setOfMappings.add(map);
				}
			}

			Files.append(
					"number of pairs generated for ontologies:"
							+ this.getOntology1().getName() + " AND "
							+ this.getOntology2().getName() + " is: "
							+ setOfMappings.size(), resultsFile,
					Charset.defaultCharset());

			}
			else if (oMap.equals(ObjectMapping.PROPERTIES))
			{
				Set<Resource> setDatatypePropertiesOnto1 = this.getOntology1().listDatatypePropertiesToBeMapped();
				Set<Resource> setDatatypePropertiesOnto2 = this.getOntology2().listDatatypePropertiesToBeMapped();
				
				for (Resource datatypeProp1 : setDatatypePropertiesOnto1) {

					for (Resource datatypeProp2 : setDatatypePropertiesOnto2) {
						
						map = new Mapping(datatypeProp1, datatypeProp2, Relation.UNKNOWN);
						setOfMappings.add(map);
					}
				}
					
				Set<Resource> setObjectPropertiesOnto1 = this.getOntology1().listDatatypePropertiesToBeMapped();
				Set<Resource> setObjectPropertiesOnto2 = this.getOntology2().listDatatypePropertiesToBeMapped();
				
				for (Resource objectProp1 : setObjectPropertiesOnto1) {

					for (Resource objectProp2 : setObjectPropertiesOnto2) {
						
						map = new Mapping(objectProp1, objectProp2, Relation.UNKNOWN);
						setOfMappings.add(map);
					}
				}
			}

			
			
			
			result = new Alignment(this.getOntology1(), this.getOntology2(),
					setOfMappings);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

}
