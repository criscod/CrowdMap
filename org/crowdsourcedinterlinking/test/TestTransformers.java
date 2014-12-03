package org.crowdsourcedinterlinking.test;

import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.transformation.SimpleBioportal2OAEITransformer;

public class TestTransformers {
	
	public static void main(String args[]) {
	
		transform1();
		transform2();
		
	}
	
	private static void transform1()
	{
		Ontology o1 = new Ontology(
				"http://who.int/bodysystem.owl",
				"/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/ontologies/bodysystem.owl",
				"bodysystem");
		Ontology o2 = new Ontology(
				"http://bioontology.org/projects/ontologies/birnlex",
				"/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/ontologies/birnlex.owl",
				"birnlex");
		SimpleBioportal2OAEITransformer transformer = new SimpleBioportal2OAEITransformer("refalignbodysystembirnlex.rdf", o1, o2);
		transformer.transform();
	}
	
	private static void transform2()
	{
		Ontology o1 = new Ontology(
				"http://mouse.brain-map.org/atlas/index.html",
				"/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/ontologies/aba.owl",
				"aba");
		Ontology o2 = new Ontology(
				"http://bioontology.org/projects/ontologies/birnlex",
				"/Users/cristinasarasua/Documents/workspaceOAEI/ISWC2012experiment/ontologies/birnlex.owl",
				"birnlex");
		
		SimpleBioportal2OAEITransformer transformer = new SimpleBioportal2OAEITransformer("refalignababirnlex.rdf", o1, o2);
		transformer.transform();
	}

}
