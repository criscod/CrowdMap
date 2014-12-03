package org.crowdsourcedinterlinking.expblogic;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.crowdsourcedinterlinking.mgeneration.MicrotaskGenerator;
import org.crowdsourcedinterlinking.mgeneration.PairsGenerator;
import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;
import org.crowdsourcedinterlinking.mpublication.CwdfService;

import org.crowdsourcedinterlinking.mpublication.MicrotaskPublisher;
import org.crowdsourcedinterlinking.mpublication.Service;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

public class MicrotaskManager {

	private PairsGenerator pairsGenerator;
	
	
	private MicrotaskGenerator microtaskGenerator;
	private MicrotaskPublisher microtaskPublisher;

	private Alignment candidates;
	
	private Set<Microtask> setOfMicrotasks;

	// when creating the specific PairsGenerato, first create alignment to
	// insert the two ontologies and then set the pairs inside the

	public MicrotaskManager(PairsGenerator pairsGen,
			MicrotaskGenerator microtaskGen, MicrotaskPublisher microtaskPub) {
		this.pairsGenerator = pairsGen;
		this.microtaskGenerator = microtaskGen;
		this.microtaskPublisher = microtaskPub;

		UUID id = UUID.randomUUID();
		String trackFilePath = ConfigurationManager.getInstance()
				.getListOfOnlineJobsToAnalyseFile() + id.toString() + ".txt";
		ConfigurationManager.getInstance().setCurrentTrackFile(trackFilePath);
	}
	
	

	public void prepareListOfMicrotasks() {
		try {

			this.candidates = this.pairsGenerator.generatePairs();

			this.setOfMicrotasks = this.microtaskGenerator
					.createMicrotasks(this.candidates);
			this.uploadMicrotasksToCwdf();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

	public void uploadMicrotasksToCwdf() {
		try {
			CwdfService s = new CwdfService();

			for (Microtask m : this.setOfMicrotasks) {
				String idGeneratedJob = this.microtaskPublisher
						.uploadMicrotask(m, s);
				if (idGeneratedJob != null) {
					// this.microtaskPublisher.orderMicrotask(idGeneratedJob,
					// s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
