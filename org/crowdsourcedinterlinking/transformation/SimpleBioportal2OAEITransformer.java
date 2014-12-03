package org.crowdsourcedinterlinking.transformation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.AlignmentParser;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.Relation;
import org.crowdsourcedinterlinking.util.ClassesAndProperties;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Constants;
import org.crowdsourcedinterlinking.util.ObjectMapping;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

public class SimpleBioportal2OAEITransformer 
{
	
	private String fileName; 
	private Ontology ontology1; 
	private Ontology ontology2; 
	
	public SimpleBioportal2OAEITransformer(String file, Ontology o1, Ontology o2)
	{
		this.fileName = file; 
		this.ontology1 = o1; 
		this.ontology2 = o2; 
	}
	
	public void transform()
	{
		
		/*We can use the already implemented parser to read the input and the already implemented serialiser to write the reference alignment from bioportal format to OAEI format
		but since the CwdfResultProcessorImpl creates the crowdalignment, we extract this into another method here -- should be configurable in teh future
		*/
		try
		{
		
	
		File inputFile = new File (ConfigurationManager.getInstance().getTransformationsDirectory()+this.fileName);
		
		// The parsing format is something to be configured in teh configuration file, but here we make sure that it is set to bioportal, because this is the format we know we must transform into OAEI in this transformer
		ConfigurationManager.getInstance().setAlignmentParsingFormat("bioportal");
		AlignmentParser p = new AlignmentParser(inputFile, ObjectMapping.CLASSES);
		p.parseAlignment(this.getOntology1(), this.getOntology2());
		Alignment resultAlignment = p.getAlignment();
		
		this.serialiseOAEIformat(resultAlignment);
		
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	
	}
	private void serialiseOAEIformat(Alignment align)
	{
		try {
			Model model = ModelFactory.createDefaultModel();
			model.getWriter("RDF/XML-ABBREV").setProperty("showXmlDeclaration",
					true);
			model.setNsPrefix("", Constants.NS_ALIGN);

			// model.setNsPrefix(null, Constants.NS_ALIGN);

			Ontology onto1 = align.getOntology1();
			Resource onto1Resource = model.createResource(onto1.getUri());
			Ontology onto2 = align.getOntology2();
			Resource onto2Resource = model.createResource(onto2.getUri());
			Set<Mapping> setOfMapCells = align.getSetOfMappings();

			
			File f = new File(ConfigurationManager.getInstance()
					.getTransformationsDirectory()+ConfigurationManager.getInstance().getTransformationsBaseFileName()+this.fileName);

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
				} else {
					cellResource.addProperty(pEntity1, mp.getElementA());
					cellResource.addProperty(pEntity2, mp.getElementB());
					cellResource.addProperty(pRelation, mp.getRelation());
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



	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Ontology getOntology1() {
		return ontology1;
	}

	public void setOntology1(Ontology ontology1) {
		this.ontology1 = ontology1;
	}

	public Ontology getOntology2() {
		return ontology2;
	}

	public void setOntology2(Ontology ontology2) {
		this.ontology2 = ontology2;
	}
}
