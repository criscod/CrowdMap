package org.crowdsourcedinterlinking.mgeneration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Set;

import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.ObjectMapping;

import com.google.common.io.Files;

public abstract class PairsGeneratorImpl implements PairsGenerator {

	private Ontology ontology1;
	private Ontology ontology2;
	
	protected ObjectMapping oMap; 

	// this class cannot be instantiated - abstract class

	public abstract Alignment generatePairs();

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

	public void registerOntologiesInTrackFile() throws IOException {
		File f = new File(ConfigurationManager.getInstance()
				.getCurrentTrackFile());

		String o1 = this.ontology1.getUri().toString() + ","
				+ this.ontology1.getLocation().toString() + ","
				+ this.ontology1.getName().toString();

		Files.append(o1, f, Charset.defaultCharset());
		String ls = System.getProperty("line.separator");
		Files.append(ls, f, Charset.defaultCharset());

		String o2 = this.ontology2.getUri().toString() + ","
				+ this.ontology2.getLocation().toString() + ","
				+ this.ontology2.getName().toString();

		Files.append(o2, f, Charset.defaultCharset());
		Files.append(ls, f, Charset.defaultCharset());
	}
	
	protected void loadAlignmentElements()
	{
		if (ConfigurationManager
				.getInstance().getAlignmentElements().equals("classes"))
				{
					this.oMap = ObjectMapping.CLASSES;
				}
				else if (ConfigurationManager
						.getInstance().getAlignmentElements().equals("properties"))
				{
					this.oMap = ObjectMapping.PROPERTIES;
				}
	}

}
