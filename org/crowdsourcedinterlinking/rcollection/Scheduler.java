package org.crowdsourcedinterlinking.rcollection;

import org.crowdsourcedinterlinking.util.ConfigurationManager;

public class Scheduler extends Thread {

	private CwdfResultProcessorImpl myService;

	public Scheduler(CwdfResultProcessorImpl serv) {
		super();
		this.myService = serv;

		this.setDaemon(true);
		this.start();
	}

	public void run() {
		try {
			while (true) {
				String listOfIds = ConfigurationManager.getInstance()
						.getJobsToControl();
				String delimiter = ",";
				String[] ids = listOfIds.split(delimiter);
				for (int i = 0; i < ids.length; i++) {
					// myService.controlJobJudgments(ids[i]);
				}

				sleep(ConfigurationManager.getInstance().getControlTime()); // milliseconds
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
