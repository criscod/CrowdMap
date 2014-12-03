package org.crowdsourcedinterlinking.model;

import java.util.Set;

public class Alignment {

	private Ontology ontology1;
	private Ontology ontology2;

	private Set<Mapping> setOfMappings;

	public Alignment(Ontology o1, Ontology o2, Set<Mapping> setMC) {
		this.ontology1 = o1;
		this.ontology2 = o2;
		this.setOfMappings = setMC;
	}

	public Set<Mapping> getSetOfMappings() {
		return setOfMappings;
	}

	public void setSetOfMappings(Set<Mapping> setOfMappings) {
		this.setOfMappings = setOfMappings;
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
