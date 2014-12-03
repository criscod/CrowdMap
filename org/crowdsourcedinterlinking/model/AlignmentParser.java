package org.crowdsourcedinterlinking.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.crowdsourcedinterlinking.util.ClassesAndProperties;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.ObjectMapping;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class AlignmentParser {

	private File fAlignment;
	private Alignment alignment;
	private ObjectMapping objectsToBeMapped; 

	public AlignmentParser(File f, ObjectMapping oMap) {
		this.fAlignment = f;
		this.objectsToBeMapped = oMap; 
		
	}

	public void parseAlignment(Ontology ontology1, Ontology ontology2) {
		try {
			Set<Mapping> setOfMappings = new HashSet<Mapping>();
			String queryString;
			String entity1, entity2;
			String relation = null; 
			Resource resourceE1, resourceE2; 
			Relation r; 

			Model model = ModelFactory.createDefaultModel();
			Model resultModel = ModelFactory.createDefaultModel();
			System.out.println("FILE TO BE: " + "file:///"
					+ fAlignment.getAbsolutePath());

			model.read("file:///" + fAlignment.getAbsolutePath());

			// model.write(System.out);

			
			
			//Because of OAEI AlignmentFormat the URL is extracted from here - delete it because it is taken from the input as some of the URLs were broken in the OAEI files
			queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#> SELECT ?o ?location WHERE { ?o rdf:type align:Ontology . ?o align:location ?location }";

			QueryExecution qe = QueryExecutionFactory
					.create(queryString, model);
			ResultSet results = qe.execSelect();

			Ontology onto1 = null;
			Ontology onto2 = null;
			int i = 1;
			QuerySolution qs = null;
			while (results.hasNext()) {
				qs = results.nextSolution();

				if (i == 1) {
					String o1 = qs.getResource("o").getURI();
					String name1 = qs.getResource("o").getLocalName();
					String location1 = qs.getLiteral("location").toString();
					onto1 = new Ontology(o1, location1, name1);

					i = i + 1;
				} else if (i == 2) {
					String o2 = qs.getResource("o").getURI();
					String name2 = qs.getResource("o").getLocalName();
					String location2 = qs.getLiteral("location").toString();
					onto2 = new Ontology(o2, location2, name2);
				}

			}// end while results
			
			if(ConfigurationManager
					.getInstance().getAlignmentParsingFormat().equals("oaei"))
			{
			queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#> SELECT ?c ?e1 ?e2 ?re WHERE {?c rdf:type align:Cell . ?c align:entity1 ?e1 . ?c align:entity2 ?e2 . ?c align:relation ?re }";

			}
			else if (ConfigurationManager
					.getInstance().getAlignmentParsingFormat().equals("bioportal"))
			{
				queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX mappings: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> SELECT ?e1 ?e2 ?re WHERE {?m rdf:type mappings:One_To_One_Mapping . ?m mappings:source ?e1 . ?m mappings:target ?e2 . ?m mappings:relation ?re }";
			}
			// NOW ONLY CLASSES BUT THEY ARE NOT DECLARED AS CLASSES SO NO:
			// String queryString =
			// "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#> PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?c ?e1 ?e2 ?re WHERE { { ?c rdf:type align:Cell . ?c align:entity1 ?e1 . ?e1 rdf:type owl:Class . ?c align:entity2 ?e2 . ?e2 rdf:type owl:Class . ?c align:relation ?re } UNION { ?c rdf:type align:Cell . ?c align:entity1 ?e1 . ?e1 rdf:type rdfs:Class . ?c align:entity2 ?e2 . ?e2 rdf:type rdfs:Class . ?c align:relation ?re } }";

			qe = QueryExecutionFactory.create(queryString, model);
			results = qe.execSelect();

			while (results.hasNext()) {
				qs = results.nextSolution();
				
				//String cell = qs.getResource("c").getURI();

				entity1 = qs.getResource("e1").getURI();
				resourceE1 = resultModel.createResource(entity1);

				entity2 = qs.getResource("e2").getURI();
				resourceE2 = resultModel.createResource(entity2);

				if (ConfigurationManager
						.getInstance().getAlignmentParsingFormat().equals("oaei"))
				{
				relation = qs.getLiteral("re").toString();
				}
				else if (ConfigurationManager
						.getInstance().getAlignmentParsingFormat().equals("bioportal"))
				{
					relation = qs.getResource("re").getURI();
				}
				r = Relation.SIMILAR; //?

				if (relation.equals("=") || (relation.equals("http://www.w3.org/2004/02/skos/core#closeMatch"))) {
					r = Relation.SIMILAR;
				}
				if (relation.equals(">") || relation.equals("&gt;")
						|| relation.equals("&#62;") || (relation.equals("http://www.w3.org/2004/02/skos/core#broadMatch"))) {
					r = Relation.GENERAL;
				}
				if (relation.equals("<") || relation.equals("&lt;")
						|| relation.equals("&#60;") || (relation.equals("http://www.w3.org/2004/02/skos/core#narrowMatch"))) {
					r = Relation.SPECIFIC;
				}

			
				System.out.println("-");
				if ((this.objectsToBeMapped.equals(ObjectMapping.CLASSES) && ClassesAndProperties.isClass(resourceE1)
						&& ClassesAndProperties.isClass(resourceE2)) 
						|| (this.objectsToBeMapped.equals(ObjectMapping.PROPERTIES)&& ClassesAndProperties.isProperty(resourceE1) && ClassesAndProperties.isProperty(resourceE2))){

					// Check that e1 belongs to ontology o1 and e2 belongs to
					// ontology o2, otherwise change order and inverse relation
					// if (entity1.startsWith(ontology2.getUri()) &&
					// entity2.startsWith(ontology1.getUri()) &&
					// r.equals(Relation.SPECIFIC))
					if (this.elementInOntology(entity1, ontology2)
							&& this.elementInOntology(entity2, ontology1)
							&& r.equals(Relation.SPECIFIC)) {
						/*
						 * change resourceE1, resourceE2 and r e1-ontology2
						 * e2-ontology1 SPECIFIC ==> e1-ontology1 e2-ontology2
						 * GENERAL
						 */

						resourceE1 = resultModel.createResource(entity2);
						resourceE2 = resultModel.createResource(entity1);
						r = Relation.GENERAL;

					}
					// Probably this case will never be true because apparently
					// they only show &lt; relations in the OAEI reference
					// alignemnts
					// else if (entity1.startsWith(ontology2.getUri()) &&
					// entity2.startsWith(ontology1.getUri()) &&
					// r.equals(Relation.GENERAL))
					else if (this.elementInOntology(entity1, ontology2)
							&& this.elementInOntology(entity2, ontology1)
							&& r.equals(Relation.GENERAL)) {
						/*
						 * change resourceE1, resourceE2 and r e1-ontology2
						 * e2-ontology1 GENERAL ==> e1-ontology1 e2-ontology2
						 * SPECIFIC
						 */
						resourceE1 = resultModel.createResource(entity2);
						resourceE2 = resultModel.createResource(entity1);
						r = Relation.SPECIFIC;
					}

					Mapping map = new Mapping(resourceE1, resourceE2, r);
					setOfMappings.add(map);
				}

			}// end while results

			// queryString =
			// "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX align: <http://knowledgeweb.semanticweb.org/heterogeneity/alignment#> SELECT ?o1 ?o2 ?location1 ?location2 WHERE { ?o1 rdf:type align:Ontology . ?o2 rdf:type align:Ontology . ?o1 align:location ?location1 . ?o2 align:location ?location2 }";

			// this.alignment = new Alignment (onto1, onto2, setOfMappings);
			// it can be checked to control
			
			
			this.alignment = new Alignment(ontology1, ontology2, setOfMappings);
			// qe.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean elementInOntology(String element, Ontology ontology) {

		boolean result = false;

		try {
			Model model = ontology.getModel();
			String queryString = "SELECT ?p ?o WHERE { <" + element
					+ "> ?p ?o }";
			QueryExecution qe = QueryExecutionFactory
					.create(queryString, model);
			ResultSet results = qe.execSelect();
			if (results.hasNext()) {
				result = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;

	}

	public File getfAlignment() {
		return fAlignment;
	}

	public void setfAlignment(File fAlignment) {
		this.fAlignment = fAlignment;
	}

	public Alignment getAlignment() {
		return alignment;
	}

	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

}
