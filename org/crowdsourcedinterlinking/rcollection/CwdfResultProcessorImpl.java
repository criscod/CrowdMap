package org.crowdsourcedinterlinking.rcollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.Relation;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Constants;


import com.google.common.io.Files;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public class CwdfResultProcessorImpl implements ResultProcessor {

	/*
	 * public CwdfResultProcessorImpl(Alignment crowdAlign) {
	 * this.crowdAlignment = crowdAlign; }
	 */

	// to be deleted
	/*
	 * public CwdfResultProcessorImpl (Ontology oA, Ontology oB) {
	 * 
	 * Set<Mapping> setOfMappings=new HashSet<Mapping>();
	 * 
	 * this.crowdAlignment = new Alignment(oA, oB, setOfMappings); }
	 */

	public CwdfResultProcessorImpl() {

	}

	public void serialiseSelectedAlignmentToAlignmentAPIFormat(
			Alignment crowdAlignment) {

		try {
			Model model = ModelFactory.createDefaultModel();
			model.getWriter("RDF/XML-ABBREV").setProperty("showXmlDeclaration",
					true);
			model.setNsPrefix("", Constants.NS_ALIGN);

			// model.setNsPrefix(null, Constants.NS_ALIGN);

			Ontology onto1 = crowdAlignment.getOntology1();
			Resource onto1Resource = model.createResource(onto1.getUri());
			Ontology onto2 = crowdAlignment.getOntology2();
			Resource onto2Resource = model.createResource(onto2.getUri());
			Set<Mapping> setOfMapCells = crowdAlignment.getSetOfMappings();

			
			// File f = new File(confManager.getAlignmentResultFile());
			File f = new File(ConfigurationManager.getInstance()
					.getCrowdAlignmentsDirectory()
					+ ConfigurationManager.getInstance().getCrowdBaseFileName()
					+ onto1.getName() + onto2.getName() + ".rdf");
			// System.out.println("confManager.getResultFile: "+confManager.getAlignmentResultFile());
			// System.out.println("the file for resulst: "+f.getAbsolutePath());

			Resource alignmentResource = model.createResource();
			Resource alignment = model.createResource(Constants.NS_ALIGN
					+ "Alignment");
			alignmentResource.addProperty(RDF.type, alignment);
			Property pXml = model.createProperty(Constants.NS_ALIGN + "xml");
			alignmentResource.addProperty(pXml, "yes");

			Property pOnto1 = model
					.createProperty(Constants.NS_ALIGN + "onto1");
			Resource ontology = model.createResource(Constants.NS_ALIGN
					+ "Ontology");
			onto1Resource.addProperty(RDF.type, ontology);
			Property pLocation = model.createProperty(Constants.NS_ALIGN
					+ "location");
			onto1Resource.addProperty(pLocation, onto1.getLocation());
			alignmentResource.addProperty(pOnto1, onto1Resource);

			Property pOnto2 = model
					.createProperty(Constants.NS_ALIGN + "onto2");
			alignmentResource.addProperty(pOnto2, onto2Resource);
			onto2Resource.addProperty(RDF.type, ontology);
			onto2Resource.addProperty(pLocation, onto2.getLocation());
			alignmentResource.addProperty(pOnto1, onto1Resource);

			Property pMap = model.createProperty(Constants.NS_ALIGN + "map");
			Resource cell = model.createResource(Constants.NS_ALIGN + "Cell");
			Property pEntity1 = model.createProperty(Constants.NS_ALIGN
					+ "entity1");
			Property pEntity2 = model.createProperty(Constants.NS_ALIGN
					+ "entity2");
			Property pRelation = model.createProperty(Constants.NS_ALIGN
					+ "relation");
			Property pMeasure = model.createProperty(Constants.NS_ALIGN + "measure");
			// Property pMeasure =
			// model.createProperty(Constants.NS_ALIGN+"measure");

			for (Mapping mp : setOfMapCells) {

				Resource cellResource = model.createResource(Constants.NS_CROWD
						+ mp.getId());
				cellResource.addProperty(RDF.type, cell);

				// If the relation is GENERAL it must not be serialised as
				// general, but as specific instead and changing the order of
				// elementA and elementB
				if (mp.getRelation().equals("&gt;")) // general
				{
					cellResource.addProperty(pEntity1, mp.getElementB());
					cellResource.addProperty(pEntity2, mp.getElementA());
					// cellResource.addProperty(pRelation, "&lt;");
					cellResource.addProperty(pRelation, "&lt;");
					//when without measure delete the next instruction
					cellResource.addProperty(pMeasure, mp.getMeasure(), new XSDDatatype("float"));
				} else {
					cellResource.addProperty(pEntity1, mp.getElementA());
					cellResource.addProperty(pEntity2, mp.getElementB());
					cellResource.addProperty(pRelation, mp.getRelation());
					//when without measure delete the next instruction
					cellResource.addProperty(pMeasure, mp.getMeasure(), new XSDDatatype("float"));
				}

				alignmentResource.addProperty(pMap, cellResource);

			}

			OutputStream out = new FileOutputStream(f);

			PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
			writer.println("<?xml version='1.0' encoding='utf-8' standalone='no'?>");
			writer.flush();

			model.write(writer, "RDF/XML-ABBREV");

			writer.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteJob() {

	}

}
