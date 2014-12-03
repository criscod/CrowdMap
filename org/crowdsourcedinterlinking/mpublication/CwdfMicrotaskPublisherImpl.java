package org.crowdsourcedinterlinking.mpublication;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import org.crowdsourcedinterlinking.model.JobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingIdentificationJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingIdentificationUnitDataEntryImpl;
import org.crowdsourcedinterlinking.model.MappingIdentificationWithFullContextJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingIdentificationWithFullContextUnitDataEntryImpl;
import org.crowdsourcedinterlinking.model.MappingValidationJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingValidationUnitDataEntryImpl;
import org.crowdsourcedinterlinking.model.MappingValidationWithFullContextJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingValidationWithFullContextUnitDataEntryImpl;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.UnitDataEntryImpl;
import org.crowdsourcedinterlinking.util.ConfigurationManager;
import org.crowdsourcedinterlinking.util.Time;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.entity.ContentProducer;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.common.io.Files;

public class CwdfMicrotaskPublisherImpl implements MicrotaskPublisher {

	private int unitsToOrder = 0;
	
	
	
	//TODO: change this method + the attribute jsCode !!!! - only temporary
	private String jsWorkerCode;
	private void loadCustomJSworkerID()
	{

		try {
			String jsCode = new String();
			File jsCodeFile;

			// validation
			
			jsCodeFile = new File("C:/Users/csarasua/workspace_PHD/ISWC2012experiment/JS/jsworker.txt");
		
			List<String> lines = Files.readLines(jsCodeFile,
					Charset.defaultCharset());
			for (String s : lines) {
				jsCode = jsCode + s;

			}
			this.jsWorkerCode = jsCode; 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public String uploadMicrotask(Microtask microtask, Service service) {
		String id = null;
		try {

			String typeOfJob = null;
			if (microtask instanceof MappingValidationWithFullContextJobMicrotaskImpl) {
				typeOfJob = "MappingValidationWithFullContextJobMicrotaskImpl";
 			} else {
				if (microtask instanceof MappingValidationJobMicrotaskImpl) {
					typeOfJob = "MappingValidationJobMicrotaskImpl";
				} else {
					if (microtask instanceof MappingIdentificationWithFullContextJobMicrotaskImpl) {
						typeOfJob = "MappingIdentificationWithFullContextJobMicrotaskImpl";
					} else {
						if (microtask instanceof MappingIdentificationJobMicrotaskImpl) {
							typeOfJob = "MappingIdentificationJobMicrotaskImpl";
						}
					}
				}
			}

			JobMicrotaskImpl job = (JobMicrotaskImpl) microtask;
			this.unitsToOrder = job.getSetOfUnits().size()
					+ job.getGoldenUnits().size();
			System.out.println("job: " + job.getId() + " units to order: "
					+ this.unitsToOrder);
			CwdfService cwdf = (CwdfService) service;

			// --------------------- Create the job (this method is called for
			// each of the jobs to be created) ---------------

			// HttpClient client = CwdfHttpClient.getInstance().getClient();
			HttpClient client = new DefaultHttpClient();

			HttpPost postJob = new HttpPost(cwdf.getCreateJobURL());
			postJob.setHeader("Accept", cwdf.getCreateJobAccept());
			postJob.setHeader("Content-Type", cwdf.getCreateJobContentType());

			// get the info that should be used for creating the
			String title = job.getTitle();
			String instructions = job.getInstructions();
			String cml = job.getCml();
			String language = job.getLanguage();
			String judgmentsPerUnit = new Integer(job.getJudgmentsPerUnit())
					.toString();
			String maxJudgmentsPerWorker = new Integer(
					job.getMaxJudgmentsPerWorker()).toString();
			String pagesPerAssignment = new Integer(job.getPagesPerAssignment())
					.toString();
			String unitsPerAssignment = new Integer(job.getUnitsPerAssignment())
					.toString();
			String goldPerAssignment = new Integer(job.getGoldPerAssignment())
					.toString();
			String cents = new Integer(ConfigurationManager.getInstance()
					.getCentsPerPager()).toString();

			if (job.getMaxJudgmentsPerWorker() < 28) // a message says error
														// must be <=28
			{
				maxJudgmentsPerWorker = "28";
			}

			//TODO: change the way the HS is prepared before
			
			//this.loadCustomJSworkerID();
			/*this.jsWorkerCode="var cookies = document.cookie.split(\";\"); var index; var widcookie; var workerid; var key = \"wid\";  for(index = 0; index < cookies.length; index++) { cookieEntry = cookies[index].split(\"=\"); "
					+ "if (key == cookieEntry[0].trim()) { widcookie = cookieEntry[1]; } } var components = widcookie.split(\"-\"); workerid= components[0]; alert(\"Worker ID: \"+workerid); ";
			*/
			
			this.jsWorkerCode="var a=\"b\";%XXvar c=\"d\";";
			
			
			String parameters = "job[title]=" + title + "&job[instructions]="
					+ instructions + "&job[cml]=" + cml + "&job[js]=" + jsWorkerCode
					+ "&job[language]="
					+ language + "&job[judgments_per_unit]=" + judgmentsPerUnit
					+ "&job[max_judgments_per_worker]=" + maxJudgmentsPerWorker
					+ "&job[pages_per_assignment]=" + pagesPerAssignment
					+ "&job[units_per_assignment]=" + unitsPerAssignment
					+ "&job[gold_per_assignment]=" + goldPerAssignment
					+ "&job[payment_cents]=" + cents;
			
			

			// String title = "job generated via API";
			// String instructions = "read read";
			// String titleEncoded = URLEncoder.encode(title, "UTF-8");
			// String instructionsEncoded =
			// URLEncoder.encode(instructions,"UTF-8");

			// String parameters = "job[title]=" + title +
			// "&job[instructions]="+ instructions;

			// checking
			System.out.println("create job url: " + cwdf.getCreateJobURL());
			System.out.println("Accept: " + cwdf.getCreateJobAccept());
			System.out.println("Content-Type :"
					+ cwdf.getCreateJobContentType());
			System.out.println("urlEncodedParameters: " + parameters);
			// end checking

			postJob.setEntity(new StringEntity(parameters));

			HttpResponse response = client.execute(postJob);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("Success in creating the job");

				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ObjectMapper mapper = new ObjectMapper();
					InputStream in = responseEntity.getContent();
					Map<String, Object> jobData = mapper.readValue(in,
							new TypeReference<Map<String, Object>>() {
							});

					for (Map.Entry<String, Object> attribute : jobData
							.entrySet()) {
						if (attribute.getKey().equals("id")) {
							String generatedJobId = attribute.getValue()
									.toString();
							id = generatedJobId;
							ConfigurationManager.getInstance().addJobToControl(
									id);
							System.out.println("JOB WITH ID: " + generatedJobId
									+ " has been generated");
						}
						if (attribute.getKey().equals("title")) {
							String titleRead = attribute.getValue().toString();
							System.out.println("title: " + titleRead
									+ " has been generated");
						}
					}

				}

				File resultsFile = new File(ConfigurationManager.getInstance()
						.getCurrentTrackFile());

				Files.append(id + "," + typeOfJob, resultsFile,
						Charset.defaultCharset());
				String ls = System.getProperty("line.separator");
				Files.append(ls, resultsFile, Charset.defaultCharset());

				// id = "95575";
				HttpClient client2 = new DefaultHttpClient();

				System.out.println("create job url: "
						+ cwdf.getUploadUnitsToJobURL(id));
				HttpPost postJob2 = new HttpPost(
						cwdf.getUploadUnitsToJobURL(id));
				postJob2.setHeader("Accept", cwdf.getUploadUnitsToJobAccept());
				postJob2.setHeader("Content-Type",
						cwdf.getUploadUnitsToJobContentType());

				/*
				 * String jsonUnits = "{ " + " \"a\":\"one\", " +
				 * " \"b\":\"one\"  " + " } " + " { " + " \"a\":\"two\", " +
				 * " \"b\":\"two\"  " + "}";
				 */

				Set<UnitDataEntryImpl> setOfUnits = job.getSetOfUnits();

				CharArrayWriter out = new CharArrayWriter();

				JsonFactory jsonFactory = new JsonFactory().configure(
						JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
				ObjectMapper mapper2 = new ObjectMapper(jsonFactory);

				String validationRadioLabel = null;

				// upload the units in a bulk

				for (UnitDataEntryImpl u : setOfUnits) {
					Map<String, String> unitData = new HashMap<String, String>();

					if (u instanceof MappingValidationWithFullContextUnitDataEntryImpl) {

						unitData.put("a",
								((MappingValidationUnitDataEntryImpl) u)
										.getLabelA());
						unitData.put("b",
								((MappingValidationUnitDataEntryImpl) u)
										.getLabelB());
						unitData.put(
								"definitiona",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getCommentA());

						unitData.put(
								"definitionb",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getCommentB());

						unitData.put(
								"relation",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getRelation());

						unitData.put(
								"superclassesa",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringSuperClassesA());

						unitData.put(
								"superclassesb",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringSuperClassesB());

						unitData.put(
								"siblingsa",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringSiblingsA());

						unitData.put(
								"siblingsb",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringSiblingsB());

						unitData.put(
								"subclassesa",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringSubClassesA());

						unitData.put(
								"subclassesb",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringSubClassesB());

						unitData.put(
								"instancesa",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringInstancesA());

						unitData.put(
								"instancesb",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getStringInstancesB());

						unitData.put(
								"uria",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getElementA());

						unitData.put(
								"urib",
								((MappingValidationWithFullContextUnitDataEntryImpl) u)
										.getElementB());

						String relation = ((MappingValidationWithFullContextUnitDataEntryImpl) u)
								.getRelation();

						if (relation.equals("=")) {
							validationRadioLabel = ConfigurationManager
									.getInstance()
									.getCmlValidationRadioLabelSame();
						} else if (relation.equals(">")
								|| relation.equals("&gt;")
								|| relation.equals("&#62;")) {
							validationRadioLabel = ConfigurationManager
									.getInstance()
									.getCmlValidationRadioLabelGeneral();
						} else if (relation.equals("<")
								|| relation.equals("&lt;")
								|| relation.equals("&#60;")) {
							validationRadioLabel = ConfigurationManager
									.getInstance()
									.getCmlValidationRadioLabelSpecific();
						}
						unitData.put("validationradiolabel",
								validationRadioLabel);

					}

					else {

						if (u instanceof MappingValidationUnitDataEntryImpl) {
							unitData.put("a",
									((MappingValidationUnitDataEntryImpl) u)
											.getLabelA());
							unitData.put("b",
									((MappingValidationUnitDataEntryImpl) u)
											.getLabelB());
							unitData.put("definitiona",
									((MappingValidationUnitDataEntryImpl) u)
											.getCommentA());

							unitData.put("definitionb",
									((MappingValidationUnitDataEntryImpl) u)
											.getCommentB());

							unitData.put("relation",
									((MappingValidationUnitDataEntryImpl) u)
											.getRelation());

							unitData.put("uria",
									((MappingValidationUnitDataEntryImpl) u)
											.getElementA());

							unitData.put("urib",
									((MappingValidationUnitDataEntryImpl) u)
											.getElementB());

							String relation = ((MappingValidationUnitDataEntryImpl) u)
									.getRelation();

							if (relation.equals("=")) {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getCmlValidationRadioLabelSame();
							} else if (relation.equals(">")
									|| relation.equals("&gt;")
									|| relation.equals("&#62;")) {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getCmlValidationRadioLabelGeneral();
							} else if (relation.equals("<")
									|| relation.equals("&lt;")
									|| relation.equals("&#60;")) {
								validationRadioLabel = ConfigurationManager
										.getInstance()
										.getCmlValidationRadioLabelSpecific();
							}
							unitData.put("validationradiolabel",
									validationRadioLabel);

						}

						else {

							if (u instanceof MappingIdentificationWithFullContextUnitDataEntryImpl) {

								unitData.put(
										"a",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getLabelA());

								unitData.put(
										"b",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getLabelB());

								unitData.put(
										"definitiona",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getCommentA());

								unitData.put(
										"definitionb",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getCommentB());

								unitData.put(
										"superclassesa",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringSuperClassesA());

								unitData.put(
										"superclassesb",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringSuperClassesB());

								unitData.put(
										"siblingsa",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringSiblingsA());

								unitData.put(
										"siblingsb",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringSiblingsB());

								unitData.put(
										"subclassesa",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringSubClassesA());

								unitData.put(
										"subclassesb",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringSubClassesB());

								unitData.put(
										"instancesa",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringInstancesA());

								unitData.put(
										"instancesb",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getStringInstancesB());

								unitData.put(
										"uria",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getElementA());

								unitData.put(
										"urib",
										((MappingIdentificationWithFullContextUnitDataEntryImpl) u)
												.getElementB());

							}

							else {
								if (u instanceof MappingIdentificationUnitDataEntryImpl) {

									unitData.put(
											"a",
											((MappingIdentificationUnitDataEntryImpl) u)
													.getLabelA());
									unitData.put(
											"b",
											((MappingIdentificationUnitDataEntryImpl) u)
													.getLabelB());
									unitData.put(
											"definitiona",
											((MappingIdentificationUnitDataEntryImpl) u)
													.getCommentA());

									unitData.put(
											"definitionb",
											((MappingIdentificationUnitDataEntryImpl) u)
													.getCommentB());

									unitData.put(
											"uria",
											((MappingIdentificationUnitDataEntryImpl) u)
													.getElementA());

									unitData.put(
											"urib",
											((MappingIdentificationUnitDataEntryImpl) u)
													.getElementB());

								}
							}
						}
					}

					mapper2.writeValue(out, unitData);

				} // for end

				// postJob2.setEntity(new EntityTemplate(cp));
				// postJob2.setEntity(new EntityTemplate(cp));
				String data = out.toString();
				postJob2.setEntity(new StringEntity(data));

				HttpResponse response2 = client2.execute(postJob2);

				int statusCode2 = response2.getStatusLine().getStatusCode();

				if (statusCode2 == 200) {
					System.out.println("success in uploading the units");
				} else {
					throw new Exception(
							"CrowdFlower did not succeed in uploading the units: "
									+ statusCode2
									+ response2.getStatusLine()
											.getReasonPhrase());
				}

				// golden units

				// -------
				String parameters3 = null;

				HttpPost postJob3 = new HttpPost(cwdf.getCreateUnitInJobURL(id));
				postJob3.setHeader("Accept", cwdf.getCreateUnitInJobAccept());
				postJob3.setHeader("Content-Type",
						cwdf.getCreateUnitInJobContentType());

				Set<UnitDataEntryImpl> goldenUnits = job.getGoldenUnits();
				String elementA = null;
				String elementB = null;
				String relation = null;
				String goldenAnswer = null;
				String difficulty = null;
				for (UnitDataEntryImpl g : goldenUnits) {

					if (g instanceof MappingValidationWithFullContextUnitDataEntryImpl
							|| g instanceof MappingValidationUnitDataEntryImpl) {
						MappingValidationUnitDataEntryImpl validationG = (MappingValidationUnitDataEntryImpl) g;
						elementA = validationG.getElementA();
						elementB = validationG.getElementB();
						relation = validationG.getRelation();
						goldenAnswer = ConfigurationManager.getInstance()
								.getGoldMessageValidation();
						difficulty = new Integer(validationG.getDifficulty())
								.toString();
					} else {
						if (g instanceof MappingIdentificationWithFullContextUnitDataEntryImpl
								|| g instanceof MappingIdentificationUnitDataEntryImpl) {
							// Since WithFullContext extends
							// MappingIdentificationUnitDataEntryImpl for this
							// purpose we only need to cast to the superclass
							MappingIdentificationUnitDataEntryImpl identificationG = (MappingIdentificationUnitDataEntryImpl) g;
							elementA = identificationG.getElementA();
							elementB = identificationG.getElementB();
							goldenAnswer = ConfigurationManager.getInstance()
									.getGoldMessageIdentification();
							difficulty = new Integer(
									identificationG.getDifficulty()).toString();

						}
					}
					System.out.println("golden units");
					// golden units
					if (g instanceof MappingValidationWithFullContextUnitDataEntryImpl) {
						if (cml.contains(ConfigurationManager.getInstance()
								.getCmlVerifwordNotTurnedString())) {
							// parameters3
							// ="unit[golden]="+true+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold][]="+goldenAnswer+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][relation]==&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available"+"&unit[difficulty]="+difficulty;
							// parameters3
							// ="unit[golden]="+true+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][relation]==&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[difficulty]="+difficulty;
							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ elementA
									+ "&unit[data][b]="
									+ elementB
									+ "&unit[data][relation]="
									+ relation
									+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassesa]=not available&unit[data][superclassesb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"
									+ "&unit[data][main_gold][]="
									+ goldenAnswer
									+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
									+ elementA
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
									+ difficulty
									+ "&unit[data][validationradiolabel]="
									+ ConfigurationManager.getInstance()
											.getCmlValidationRadioLabelSame();

						} else if (cml.contains(ConfigurationManager
								.getInstance().getCmlVerifwordTurnedString())) {
							parameters3 = "unit[golden]="
									+ true
									+ "&unit[data][a]="
									+ elementA
									+ "&unit[data][b]="
									+ elementB
									+ "&unit[data][relation]="
									+ relation
									+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassesa]=not available&unit[data][superclassesb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"
									+ "&unit[data][main_gold][]="
									+ goldenAnswer
									+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
									+ elementB
									+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
									+ difficulty
									+ "&unit[data][validationradiolabel]="
									+ ConfigurationManager.getInstance()
											.getCmlValidationRadioLabelSame();

						}
					} else {
						if (g instanceof MappingValidationUnitDataEntryImpl) {

							// parameters3 =
							// "unit[golden]="+true+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold][]="+goldenAnswer+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][relation]==&unit[difficulty]="+difficulty;
							if (cml.contains(ConfigurationManager.getInstance()
									.getCmlVerifwordNotTurnedString())) {
								// parameters3 =
								// "unit[golden]="+true+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][relation]==&unit[data][uria]=not available&unit[data][urib]=not available&unit[difficulty]="+difficulty;
								parameters3 = "unit[golden]="
										+ true
										+ "&unit[data][a]="
										+ elementA
										+ "&unit[data][b]="
										+ elementB
										+ "&unit[data][relation]="
										+ relation
										+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][uria]=not available&unit[data][urib]=not available&unit[data][main_gold][]="
										+ goldenAnswer
										+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
										+ elementA
										+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
										+ difficulty
										+ "&unit[data][validationradiolabel]="
										+ ConfigurationManager
												.getInstance()
												.getCmlValidationRadioLabelSame();

							} else if (cml.contains(ConfigurationManager
									.getInstance()
									.getCmlVerifwordTurnedString())) {
								parameters3 = "unit[golden]="
										+ true
										+ "&unit[data][a]="
										+ elementA
										+ "&unit[data][b]="
										+ elementB
										+ "&unit[data][relation]="
										+ relation
										+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][uria]=not available&unit[data][urib]=not available&unit[data][main_gold][]="
										+ goldenAnswer
										+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
										+ elementB
										+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
										+ difficulty
										+ "&unit[data][validationradiolabel]="
										+ ConfigurationManager
												.getInstance()
												.getCmlValidationRadioLabelSame();

							}
						} else {
							if (g instanceof MappingIdentificationWithFullContextUnitDataEntryImpl) {
								// parameters3
								// ="unit[golden]="+true+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold][]="+goldenAnswer+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available"+"&unit[difficulty]="+difficulty;
								// parameters3
								// ="unit[golden]="+true+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[difficulty]="+difficulty;
								// if
								// (cml.contains("Select the name of Concept A"))
								if (cml.contains(ConfigurationManager
										.getInstance()
										.getCmlVerifwordNotTurnedString())) {
									// parameters3
									// ="unit[golden]="+true+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="+elementA+"&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="+difficulty;
									parameters3 = "unit[golden]="
											+ true
											+ "&unit[data][a]="
											+ elementA
											+ "&unit[data][b]="
											+ elementB
											+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassesa]=not available&unit[data][superclassesb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"
											+ "&unit[data][main_gold][]="
											+ goldenAnswer
											+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
											+ elementA
											+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
											+ difficulty;

									System.out
											.println("cml contains Concept A question");
								} else if (cml.contains(ConfigurationManager
										.getInstance()
										.getCmlVerifwordTurnedString())) {
									// parameters3
									// ="unit[golden]="+true+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"+"&unit[data][main_gold][]="+goldenAnswer+"&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="+elementB+"&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="+difficulty;
									parameters3 = "unit[golden]="
											+ true
											+ "&unit[data][a]="
											+ elementA
											+ "&unit[data][b]="
											+ elementB
											+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassesa]=not available&unit[data][superclassesb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available&unit[data][uria]=not available&unit[data][urib]=not available"
											+ "&unit[data][main_gold][]="
											+ goldenAnswer
											+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
											+ elementB
											+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
											+ difficulty;

									System.out
											.println("cml contains Concept B question");
								}
							} else {
								if (g instanceof MappingIdentificationUnitDataEntryImpl) {
									// parameters3 =
									// "unit[golden]="+true+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold][]="+goldenAnswer+"&unit[data][do_you_see_any_connection_between_concept_a_and_concept_b_gold_reason][]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[difficulty]="+difficulty;
									if (cml.contains(ConfigurationManager
											.getInstance()
											.getCmlVerifwordNotTurnedString())) {
										parameters3 = "unit[golden]="
												+ true
												+ "&unit[data][a]="
												+ elementA
												+ "&unit[data][b]="
												+ elementB
												+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][uria]=not available&unit[data][urib]=not available&unit[data][main_gold][]="
												+ goldenAnswer
												+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
												+ elementA
												+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
												+ difficulty;

									} else if (cml
											.contains(ConfigurationManager
													.getInstance()
													.getCmlVerifwordTurnedString())) {
										parameters3 = "unit[golden]="
												+ true
												+ "&unit[data][a]="
												+ elementA
												+ "&unit[data][b]="
												+ elementB
												+ "&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][uria]=not available&unit[data][urib]=not available&unit[data][main_gold][]="
												+ goldenAnswer
												+ "&unit[data][main_gold_reason][]=They have almost the same name and they refer to the same element&unit[data][verifword_gold][]="
												+ elementB
												+ "&unit[data][verifword_gold_reason][]=This is the word that is shown&unit[data][verifnumber_gold][]=1&unit[data][verifnumber_gold_reason][]=The answer must be a number that shows the number of words shown &unit[difficulty]="
												+ difficulty;

									}
								}
							}
						}
					}

					// "unit[golden]="+true+"&unit[data][check_all_that_apply_gold]="+goldenAnswer+"&unit[check_all_that_apply_gold_reason]=They have almost the same name and they refer to the same element"+"&unit[data][a]="+elementA+"&unit[data][b]="+elementB+"&unit[data][definitiona]=not available&unit[data][definitionb]=not available&unit[data][superclassa]=not available&unit[data][superclassb]=not available&unit[data][siblingsa]=not available&unit[data][siblingsb]=not available&unit[data][subclassesa]=not available&unit[data][subclassesb]=not available&unit[data][instancesa]=not available&unit[data][instancesb]=not available"+"&unit[difficulty]="+difficulty;

					// checking
					System.out.println("create unit url: "
							+ cwdf.getCreateUnitInJobURL(id));
					System.out.println("Accept: "
							+ cwdf.getCreateUnitInJobAccept());
					System.out.println("Content-Type :"
							+ cwdf.getCreateUnitInJobContentType());
					System.out.println("urlEncodedParameters: " + parameters3);
					// end checking

					postJob3.setEntity(new StringEntity(parameters3));

					HttpClient client3 = new DefaultHttpClient();
					HttpResponse response3 = client3.execute(postJob3);
					int statusCode3 = response3.getStatusLine().getStatusCode();
					if (statusCode3 == 200) {
						System.out
								.println("Success in creating the golden unit for the job");
					} else {
						throw new Exception(
								"CrowdFlower did not succeed in creating a golden unit for the job: "
										+ statusCode3
										+ response3.getStatusLine()
												.getReasonPhrase());
					}

				}

				// ------

				// Change the excluded countries

				String parameters4 = null;
				HttpPut putJob = new HttpPut(cwdf.getChangeJobURL(id));
				putJob.setHeader("Accept", cwdf.getChangeJobAccept());
				putJob.setHeader("Content-Type", cwdf.getChangeJobContentType());

				parameters4 = "job[excluded_countries][]=IN";
				putJob.setEntity(new StringEntity(parameters4));
				HttpClient client4 = new DefaultHttpClient();
				HttpResponse response4 = client4.execute(putJob);

				int statusCode4 = response4.getStatusLine().getStatusCode();
				if (statusCode4 == 200) {
					System.out
							.println("Success in updating the job with excluded countries");
				} else {
					throw new Exception(
							"CrowdFlower did not succeed in updating the job with excluded countries: "
									+ statusCode4
									+ response4.getStatusLine()
											.getReasonPhrase());
				}

			} else {
				throw new Exception(
						"CrowdFlower did not succeed in creating a job: "
								+ statusCode);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	public void orderMicrotask(String idMicrotask, Service service) {
		try {

			CwdfService cwdf = (CwdfService) service;
			HttpClient client = new DefaultHttpClient();

			System.out.println("create job url: "
					+ cwdf.getOrderJobURL(idMicrotask));
			HttpPost postJob = new HttpPost(cwdf.getOrderJobURL(idMicrotask));
			postJob.setHeader("Accept", cwdf.getOrderJobAccept());
			postJob.setHeader("Content-Type", cwdf.getOrderJobContentType());

			// String parameters3="order[debit]=2&order[channels]=MobMerge";
			// String parameters3 = "debit[units_count]=2&channels[]=MobMerge";
			String parameters = "debit[units_count]=" + this.unitsToOrder
					+ "&channels[]=mob";

			postJob.setEntity(new StringEntity(parameters));

			HttpResponse response = client.execute(postJob);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("Success in ordering the job");
			} else {
				throw new Exception(
						"CrowdFlower did not succeed in ordering the job: "
								+ statusCode
								+ response.getStatusLine().getReasonPhrase());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void uploadCSVfile(String idJob, String pathOfFile, Service service) {
		try {
			// subirlo con idJob - en web server //The CSV files must be
			// publicly available otherwise this cannot be done
		} catch (Exception e) {

		}
	}

}
