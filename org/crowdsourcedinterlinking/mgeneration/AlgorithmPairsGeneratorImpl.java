package org.crowdsourcedinterlinking.mgeneration;

import java.io.File;
import java.nio.charset.Charset;

import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.AlignmentParser;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.util.ObjectMapping;
import org.crowdsourcedinterlinking.util.Time;

import com.google.common.io.Files;

public class AlgorithmPairsGeneratorImpl extends PairsGeneratorImpl {

	private File algorithmAlignment;

	public AlgorithmPairsGeneratorImpl(Ontology o1, Ontology o2,
			File algorithmAlignment) {
		try {
			this.setOntology1(o1);
			this.setOntology2(o2);

			this.algorithmAlignment = algorithmAlignment;
			this.loadAlignmentElements();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Alignment generatePairs() {
		Alignment result = null;

		try {
			this.registerOntologiesInTrackFile();

			AlignmentParser p = new AlignmentParser(this.algorithmAlignment, oMap);
			p.parseAlignment(this.getOntology1(), this.getOntology2());
			// they will contain relations, but when generating the type of
			// Microtask, the relation should be hidden
			result = p.getAlignment();

			File resultsFile = new File(
					"C:/Users/ASUS12DE/workspace/ISWC2012experiment/testPairsGeneration.txt");
			Files.write("ALGORITHM " + Time.currentTime(), resultsFile,
					Charset.defaultCharset());
			String ls = System.getProperty("line.separator");
			Files.append(ls, resultsFile, Charset.defaultCharset());

			for (Mapping m : result.getSetOfMappings()) {

				Files.append(
						"Mapping: elem1: " + m.getElementA().getURI()
								+ " elem2: " + m.getElementB().getURI()
								+ " rel: " + m.getRelation(), resultsFile,
						Charset.defaultCharset());
				Files.append(ls, resultsFile, Charset.defaultCharset());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public File getAlgorithmAlignment() {
		return algorithmAlignment;
	}

	public void setAlgorithmAlignment(File algorithmAlignment) {
		this.algorithmAlignment = algorithmAlignment;
	}

}
