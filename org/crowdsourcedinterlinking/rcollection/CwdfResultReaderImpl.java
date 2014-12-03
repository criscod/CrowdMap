package org.crowdsourcedinterlinking.rcollection;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.crowdsourcedinterlinking.model.Alignment;
import org.crowdsourcedinterlinking.model.JobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.Mapping;
import org.crowdsourcedinterlinking.model.MappingIdentificationJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingIdentificationWithFullContextJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingValidationJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.MappingValidationWithFullContextJobMicrotaskImpl;
import org.crowdsourcedinterlinking.model.Microtask;
import org.crowdsourcedinterlinking.model.Ontology;
import org.crowdsourcedinterlinking.model.Relation;
import org.crowdsourcedinterlinking.model.Response;
import org.crowdsourcedinterlinking.model.TypeOfMappingGoal;
import org.crowdsourcedinterlinking.model.UnitDataEntryImpl;
import org.crowdsourcedinterlinking.mpublication.CwdfService;
import org.crowdsourcedinterlinking.mpublication.Service;
import org.crowdsourcedinterlinking.util.ClassesAndProperties;
import org.crowdsourcedinterlinking.util.ConfigurationManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
//import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import java.util.zip.*;

public class CwdfResultReaderImpl implements ResultReader {

	private Ontology o1 = null;
	private Ontology o2 = null;

	public CwdfResultReaderImpl(Ontology o1, Ontology o2) {
		this.o1 = o1;
		this.o2 = o2;
	}

	public Set<Mapping> readResponsesOfMicrotask(String microtaskId,
			String type, Service service) {
		// depending on the type of MappingGoal the attributes to read should be
		// different

		Set<Mapping> mappingsResult = new HashSet<Mapping>();

		try {
			// it will be "main" no matter it is MappingValidation... or
			// MappingIdentification -- the label will be different
			String agg = ConfigurationManager.getInstance()
					.getNameFormElementToWatch();
			/*
			 * if
			 * (type.equals("MappingIdentificationWithFullContextJobMicrotaskImpl"
			 * )||type.equals("MappingIdentificationJobMicrotaskImpl")) { agg =
			 * "Do you see any connection between Concept A and Concept B"; }
			 * else { if
			 * (type.equals("MappingValidationWithFullContextJobMicrotaskImpl"
			 * )||type.equals("MappingValidationJobMicrotaskImpl")) { //add
			 * validation agg=""; } }
			 */

			//
			CwdfService cwdf = (CwdfService) service;

			// the job should be checked to guarantee it is finalized!!

			/*
			 * HttpGet get = new
			 * HttpGet(cwdf.getReadUnitsOfJobURL(microtaskId));
			 * get.addHeader("Accept", cwdf.getReadUnitsOfJobAccept());
			 * 
			 * HttpClient client = new DefaultHttpClient();
			 * 
			 * //check when the unit is finalized HttpResponse response =
			 * client.execute(get);
			 * 
			 * Map<String,Map<String,String>> result = new HashMap<String,
			 * Map<String,String>>(); ObjectMapper mapper = new ObjectMapper();
			 * result = mapper.readValue(response.getEntity().getContent(), new
			 * TypeReference<Map<String,Map<String,String>>>() {});
			 * 
			 * for(Map.Entry<String,Map<String,String>> entry :
			 * result.entrySet()) { System.out.println("Unit: "+entry.getKey());
			 * for(Map.Entry<String,String> attr : entry.getValue().entrySet())
			 * { System.out.println(attr.getKey() + " = " + attr.getValue()); }
			 * System.out.println("============================="); }
			 */
			// cwdf.getReadUnitsOfJobURL(microtaskId)

			// HttpGet get = new
			// HttpGet("https://api.crowdflower.com/v1/jobs/97860/units?key=32c441799be374c58a0b4a0dc92644f78949cdf3");

			// Get info about the microtask
			HttpGet get = new HttpGet(cwdf.getReadUnitsOfJobURL(microtaskId));
			System.out.println(cwdf.getReadUnitsOfJobURL(microtaskId));
			// HttpGet get = new
			// HttpGet(cwdf.getReadUnitsOfJobURL(microtaskId));

			get.addHeader("Accept", cwdf.getReadUnitsOfJobAccept());

			HttpClient client = new DefaultHttpClient();

			// check when the unit is finalized
			HttpResponse response = client.execute(get);
			HttpEntity responseEntity = response.getEntity();

			if (responseEntity != null) {

				System.out.println("Started reading ...");
				Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
				ObjectMapper mapper = new ObjectMapper();
				result = mapper.readValue(responseEntity.getContent(),
						new TypeReference<Map<String, Map<String, Object>>>() {
						});

				for (Map.Entry<String, Map<String, Object>> entry : result
						.entrySet()) {

					String unitId = entry.getKey(); // only the Unit id is
													// needed
					// UNDO COMMENT - JUST NOW
					// storeUnit(unitId, entry.getValue(), microtaskId);

					// System.out.println("Unit: " + unitId);
					boolean added = false;

					// Get info about the unit to check the agg response
					HttpGet get2 = new HttpGet(cwdf.getReadUnitURL(microtaskId,
							unitId));
					get2.addHeader("Accept", cwdf.getReadUnitAccept());
					HttpClient client2 = new DefaultHttpClient();
					HttpResponse response2 = client2.execute(get2);

					HttpEntity responseEntity2 = response2.getEntity();
					if (responseEntity2 != null) {
						Map<String, Object> unitData = new HashMap<String, Object>();

						ObjectMapper mapper2 = new ObjectMapper();
						unitData = mapper2.readValue(
								responseEntity2.getContent(),
								new TypeReference<Map<String, Object>>() {
								});

						String state = "none";
						double confidence = 0.0;
						String aggResponse = null;
						Map<String, Object> results;
						Map<String, Object> aggItem;
						Map<String, String> dataItem;
						String uria = null;
						String urib = null;
						String validationRadioLabel = null;
						double agreement = 0.0;

						for (Map.Entry<String, Object> entry2 : unitData
								.entrySet()) {

							// UNDO COMMENT - JUST NOW
							// storeJudgmentOfUnit(entry2, unitId);

							if (entry2.getKey().equals("results")
									&& entry2.getValue() instanceof Map) {
								results = (Map<String, Object>) entry2
										.getValue();
								// System.out.println("checking results ");
								// System.out.println("================= ");
								for (Map.Entry<String, Object> entry3 : results
										.entrySet()) {
									// depending on the type of microtask I
									// should ask for one or another title of
									// agg answer
									// validation vs identification no?
									//
									// Please select only one of the following
									// possible answers

									if (entry3.getValue() instanceof Map
											&& entry3.getKey().equals(agg)) {
										aggItem = (Map<String, Object>) entry3
												.getValue();

										for (Map.Entry<String, Object> entry4 : aggItem
												.entrySet()) {
											if (entry4.getKey().equals("agg")
													&& entry4.getValue() instanceof String) {
												aggResponse = (String) entry4
														.getValue();
												// System.out.println("aggResponse: "+
												// aggResponse);
											} else if (entry4.getKey().equals(
													"confidence")
													&& entry4.getValue() instanceof Double) {

												confidence = (Double) entry4
														.getValue();
												// System.out.println("confidence: "+
												// confidence);
											}
										}
									}
								}
							} else if (entry2.getValue() instanceof String
									&& entry2.getKey().equals("state")) {
								state = entry2.getValue().toString();
								// System.out.println("state:" + state);
							} else if (entry2.getKey().equals("data")
									&& entry2.getValue() instanceof Map) {
								dataItem = (Map<String, String>) entry2
										.getValue();
								for (Map.Entry<String, String> entry5 : dataItem
										.entrySet()) {
									if (entry5.getKey().equals("uria")) {
										uria = entry5.getValue();
										// System.out.println("uria: " + uria);
									} else if (entry5.getKey().equals("urib")) {
										urib = entry5.getValue();
										// System.out.println("urib: " + urib);
									} else if (entry5.getKey().equals(
											"validationradiolabel")) {
										validationRadioLabel = entry5
												.getValue();
									}

									// for testing: the cases where the job
									// units did not include uria and urib

									/*
									 * if(entry5.getKey().equals("a")) {
									 * 
									 * uria =this.o1.getUri()+"#"+
									 * entry5.getValue(); }else if
									 * (entry5.getKey().equals("b")) {
									 * urib=this.o2.getUri()+"#"+
									 * entry5.getValue(); }
									 */

									added = false;
								}
							} else if (entry2.getKey().equals("agreement")
									&& entry2.getValue() instanceof Double) {
								agreement = (Double) entry2.getValue();
								

							}

							// ask about the state and the confidence
							// confidence >= 0.5
							if (!added && state != null
									& state.equals("finalized")
									&& confidence > 0.5 && uria != null
									&& urib != null)

							{
								Model model = ModelFactory.createDefaultModel();
								Resource uriaResource = model
										.createResource(uria);
								Resource uribResource = model
										.createResource(urib);
								Mapping map = null;

								// Change the case of yes//similar yes//general
								// yes//specific
								if (type.equals("MappingIdentificationWithFullContextJobMicrotaskImpl")
										|| type.equals("MappingIdentificationJobMicrotaskImpl")) {
									if (aggResponse.equals(ConfigurationManager
											.getInstance()
											.getResponseValidation())
											|| aggResponse
													.equals(ConfigurationManager
															.getInstance()
															.getResponseIdentificationABSame())) {
										map = new Mapping(uriaResource,
												uribResource, Relation.SIMILAR, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
													.getInstance()
													.getResponseIdentificationAGeneral())) {
										map = new Mapping(uriaResource,
												uribResource, Relation.GENERAL, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
													.getInstance()
													.getResponseIdentificationASpecific())) {
										map = new Mapping(uriaResource,
												uribResource, Relation.SPECIFIC, agreement);
									}

								} else if (type
										.equals("MappingValidationWithFullContextJobMicrotaskImpl")
										|| type.equals("MappingValidationJobMicrotaskImpl")) {
									// if ?? and aggResponse yes then similar,
									// if ?? aggResponse yes then general if ??
									// and aggresponse no then specific
									if (aggResponse.equals(ConfigurationManager
											.getInstance()
											.getResponseValidation())
											&& validationRadioLabel
													.equals(ConfigurationManager
															.getInstance()
															.getCmlValidationRadioLabelSame())) {
										map = new Mapping(uriaResource,
												uribResource, Relation.SIMILAR, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
													.getInstance()
													.getResponseValidation())
											&& validationRadioLabel
													.equals(ConfigurationManager
															.getInstance()
															.getCmlValidationRadioLabelGeneral())) {
										map = new Mapping(uriaResource,
												uribResource, Relation.GENERAL, agreement);
									} else if (aggResponse
											.equals(ConfigurationManager
													.getInstance()
													.getResponseValidation())
											&& validationRadioLabel
													.equals(ConfigurationManager
															.getInstance()
															.getCmlValidationRadioLabelSpecific())) {
										map = new Mapping(uriaResource,
												uribResource, Relation.SPECIFIC, agreement);
									}
								}

								if (map != null) {
									System.out.println("Selected!: " + uria
											+ " " + urib + " " + "=");
									mappingsResult.add(map);
									added = true;
								}
							}
						}

					}

				}
			}

			// UNDO COMMENT - JUST NOW
			// storeJob(microtaskId, type);

		} catch (Exception e) {
			e.printStackTrace();
		}
		/*
		 * for (Mapping m : mappingsResult) {
		 * System.out.println(m.getElementA().getURI() + "," +
		 * m.getElementB().getURI() + "," + m.getRelation()); }
		 */
		return mappingsResult;
	}

	/*
	 * public Alignment createAlignmentFromCrowdFlowerReport(Ontology oA,
	 * Ontology oB, Set<String> filePaths, TypeOfMappingGoal typeOfGoal, boolean
	 * context) { Set<Mapping> setOfMappings = new HashSet<Mapping>(); Alignment
	 * alignment = new Alignment(oA, oB, setOfMappings);
	 * 
	 * // Here I read everything at the same time
	 * 
	 * try {
	 * 
	 * for (String path : filePaths) { File cwdfReport = new File(path);
	 * String[] aggPerUnit;
	 * 
	 * List<String> lines = Files.readLines(cwdfReport,
	 * Charset.defaultCharset());
	 * 
	 * // adhoc for the case- change code if it can be done generally // in
	 * order to put away the line with title lines.remove(0); Model model =
	 * ModelFactory.createDefaultModel();
	 * 
	 * for (String s : lines) { System.out.println("line: " + s); aggPerUnit =
	 * s.split(","); for (int i = 0; i < aggPerUnit.length; i++) { if
	 * (typeOfGoal .equals(TypeOfMappingGoal.IDENTIFICATIONB) && context) { //
	 * the comparison with configurationmanager!!
	 * 
	 * // golden & answer.confidence
	 * 
	 * System.out .println("value for the confidence read: " + aggPerUnit[6]);
	 * // "1.0"
	 * 
	 * // double confidence = // Double.parseDouble(aggPerUnit[6].substring(1,
	 * // aggPerUnit[6].length()-2)); double confidence = Double
	 * .parseDouble(aggPerUnit[6]); // String golden =
	 * aggPerUnit[1].substring(1, // aggPerUnit[1].length()-2); String golden =
	 * aggPerUnit[1]; if (golden.equals(false) && confidence >= 0.5) // relevant
	 * // answer { // String elemA = aggPerUnit[7].substring(1, //
	 * aggPerUnit[7].length()-2); String elemA = aggPerUnit[7]; // CHANGE: //
	 * this.crowdAlignment.getOntology1().getNamespace // including the
	 * namespace in the constructor of // Ontology
	 * 
	 * // elemA is a label I have to check for the URI // OR if there is no
	 * label, in the local name of // the URI - I shoul d
	 * 
	 * // String elemB = aggPerUnit[8].substring(1, //
	 * aggPerUnit[8].length()-2); String elemB = aggPerUnit[8];
	 * 
	 * Resource uriElemA = null; Resource uriElemB = null;
	 * 
	 * // String answer = aggPerUnit[5].substring(1, //
	 * aggPerUnit[7].length()-2); String answer = aggPerUnit[5]; if (answer
	 * .equals("Concept A is similar to Concept B")) { // query about elemA real
	 * URI and elemB real // URI String queryString =
	 * "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?uria ?urib WHERE {?uria rdfs:label "
	 * + elemA + " . ?urib rdfs:label " + elemB + "}";
	 * 
	 * QueryExecution qe = QueryExecutionFactory .create(queryString, model);
	 * ResultSet results = qe.execSelect(); QuerySolution qs = null;
	 * 
	 * while (results.hasNext()) { qs = results.nextSolution();
	 * 
	 * uriElemA = qs.getResource("uria"); uriElemB = qs.getResource("urib");
	 * 
	 * }// end while results
	 * 
	 * Mapping map = new Mapping(uriElemA, uriElemB, Relation.SIMILAR);
	 * setOfMappings.add(map); } // Else --> there is no mapping to add } } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (Exception e) { e.printStackTrace(); } return alignment; }
	 */

	// Method that downloads the CSV info in a zip - for storage purposes

	public Set<Response> readResponsesZip(String microtaskId, Service service) {

		// CHANGE

		// If job is completed and unit state is finalized - even if it will be
		// executed one week after and I will analyse things
		// read list of jobs in the file - check the typeofjob is not null or ""

		Set<Response> setOfResponses = new HashSet<Response>();

		try {
			HttpClient client = new DefaultHttpClient();

			CwdfService cwdf = (CwdfService) service;

			// for testing
			// jobId="94847"; // must be microtaskId

			HttpGet getJob = new HttpGet(
					cwdf.getJudgmentsOfAJobURL(microtaskId));

			// without Accept it also worked
			// getJob.setHeader("Accept", cwdf.getJudgmentsOfAJobAccept());

			HttpResponse response = client.execute(getJob);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				byte[] bytes = EntityUtils.toByteArray(response.getEntity());
				ZipInputStream zip = new ZipInputStream(
						new ByteArrayInputStream(bytes));

				ZipEntry entry = zip.getNextEntry();
				while (entry != null) {
					String readed;
					InputStreamReader reader = new InputStreamReader(zip);
					BufferedReader in = new BufferedReader(reader);

					// reading the CSV file that is downloaded in the ZIP
					File judgmentsFile = new File(ConfigurationManager
							.getInstance()
							.getResultsJudgmentsCsvFileDirectory()
							+ ConfigurationManager.getInstance()
									.getResultsJudgmentsCsvBaseFileName()
							+ microtaskId + ".csv");

					while ((readed = in.readLine()) != null) {
						// it comes in CSV not in JSON
						System.out.println("judgment" + readed);
						Files.append(readed, judgmentsFile,
								Charset.defaultCharset());
						String ls = System.getProperty("line.separator");
						Files.append(ls, judgmentsFile,
								Charset.defaultCharset());

					}
					entry = zip.getNextEntry();
				}
			} else {
				throw new Exception(
						"CrowdFlower did not success in retrieveing all the judgments related to the requested Job "
								+ response.getStatusLine().getReasonPhrase());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return setOfResponses;
	}

	public void controlJobJudgments(String idJob) {
		// for all the jobs with 4 options
		// check whether it is completed - only the ones of 5 will come here
		// if no agreement on 3 answers
		// then extend max to 9 - also configurable!!
	}

	private void storeJob(String jobId, String jobType) {
		// etc
		// get and save
		String id = null;
		String o1Uri = null;
		String o2Uri = null;
		String typeOfMapping = null;
		String cml = null;
		String title = null;
		String instructions = null;
		int unitsPerAssignment = 0;
		int pagesPerAssignment = 0;
		int judgmentsCount = 0;
		int unitsCount = 0;
		int maxJudgmentsPerWorker = 0;
		int judgmentsPerUnit = 0;
		int goldPerAssignment = 0;
		String createdAt = null;

		try {

			id = jobId;
			o1Uri = this.o1.getUri();
			o2Uri = this.o2.getUri();
			typeOfMapping = jobType;

			Connection connect = null;
			Statement statement = null;
			Statement statement2 = null;

			java.sql.ResultSet resultSet = null;
			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			connect = DriverManager.getConnection(ConfigurationManager
					.getInstance().getMySqlURL()
					+ "?user="
					+ ConfigurationManager.getInstance().getMySqlUser()
					+ "&password="
					+ ConfigurationManager.getInstance().getMySqlPassword());

			CwdfService cwdf = new CwdfService();

			// if (!resultSet.next()) {
			HttpClient client = new DefaultHttpClient();
			HttpGet getJob = new HttpGet(cwdf.getReadJobURL(jobId));
			getJob.setHeader("Accept", cwdf.getReadJobAccept());
			HttpResponse response = client.execute(getJob);

			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("Success in reading the job");

				HttpEntity responseEntity = response.getEntity();
				if (responseEntity != null) {
					ObjectMapper mapper = new ObjectMapper();
					InputStream in = responseEntity.getContent();
					Map<String, Object> jobData = mapper.readValue(in,
							new TypeReference<Map<String, Object>>() {
							});

					for (Map.Entry<String, Object> entry : jobData.entrySet()) {
						if (entry.getKey().equals("cml")
								&& entry.getValue() instanceof String) {

							cml = entry.getValue().toString();

						} else if (entry.getKey().equals("title")
								&& entry.getValue() instanceof String) {
							title = entry.getValue().toString();
						} else if (entry.getKey().equals("instructions")
								&& entry.getValue() instanceof String) {
							instructions = entry.getValue().toString();
						} else if (entry.getKey().equals("created_at")
								&& entry.getValue() instanceof String) {
							createdAt = entry.getValue().toString();
						} else if (entry.getKey()
								.equals("units_per_assignment")
								&& entry.getValue() instanceof Integer) {
							unitsPerAssignment = (Integer) entry.getValue();
						} else if (entry.getKey()
								.equals("pages_per_assignment")
								&& entry.getValue() instanceof Integer) {
							pagesPerAssignment = (Integer) entry.getValue();
						} else if (entry.getKey().equals("judgments_count")
								&& entry.getValue() instanceof Integer) {
							judgmentsCount = (Integer) entry.getValue();
						} else if (entry.getKey().equals("units_count")
								&& entry.getValue() instanceof Integer) {
							unitsCount = (Integer) entry.getValue();
						} else if (entry.getKey().equals(
								"max_judgments_per_worker")
								&& entry.getValue() instanceof Integer) {
							maxJudgmentsPerWorker = (Integer) entry.getValue();
						} else if (entry.getKey().equals("judgments_per_unit")
								&& entry.getValue() instanceof Integer) {
							judgmentsPerUnit = (Integer) entry.getValue();
						} else if (entry.getKey().equals("gold_per_assignment")
								&& entry.getValue() instanceof Integer) {
							goldPerAssignment = (Integer) entry.getValue();
						}

					}
				}
				System.out.println("cml: " + cml);
				/*
				 * statement2 = connect.createStatement(); int result =
				 * statement2 .executeUpdate(
				 * "INSERT INTO crowdlink.job (id, typeofmappinggoalcontext, onto1, onto2, title, instructions, unitsperassignment, pagesperassignment, judgmentscount, unitscount, maxjudgmentsperworker, judgmentsperunit, goldperassignment, createtat) VALUES ('"
				 * + id + "', '" + typeOfMapping + "', '" + o1Uri + "', '" +
				 * o2Uri + "', '" + title + "', '" + instructions + "', '" +
				 * unitsPerAssignment + "', '" + pagesPerAssignment + "', '" +
				 * judgmentsCount + "', '" + unitsCount + "', '" +
				 * maxJudgmentsPerWorker + "', '" + judgmentsPerUnit + "', '" +
				 * goldPerAssignment + "', '" + createdAt+"')"); //+ "', '" +
				 * cml + "')");
				 */
				// add cml in the table ()
				// "INSERT INTO crowdlink.job (id, typeofmappinggoalcontext, onto1, onto2, title, instructions, unitsperassignment, pagesperassignment, judgmentscount, unitscount, maxjudgmentsperworker, judgmentsperunit, goldperassignment, createtat, cml ) VALUES ('"

				statement = connect.createStatement();
				// check that the job does not exist -- otherwise do not store /
				// USE
				// ON DUPLICATE KEY UPDATE?
				resultSet = statement
						.executeQuery("SELECT * FROM crowdlink.job WHERE id="
								+ jobId);
				PreparedStatement preparedStatement = null;

				if (!resultSet.next()) {
					preparedStatement = connect
							.prepareStatement("INSERT INTO crowdlink.job (id, typeofmappinggoalcontext, onto1, onto2, title, instructions, unitsperassignment, pagesperassignment, judgmentscount, unitscount, maxjudgmentsperworker, judgmentsperunit, goldperassignment, createtat, cml ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?) ");

					preparedStatement.setString(1, id);
					preparedStatement.setString(2, typeOfMapping);
					preparedStatement.setString(3, o1Uri);
					preparedStatement.setString(4, o2Uri);
					preparedStatement.setString(5, title);
					preparedStatement.setString(6, instructions);
					preparedStatement.setInt(7, unitsPerAssignment);
					preparedStatement.setInt(8, pagesPerAssignment);

					preparedStatement.setInt(9, judgmentsCount);
					preparedStatement.setInt(10, unitsCount);
					preparedStatement.setInt(11, maxJudgmentsPerWorker);
					preparedStatement.setInt(12, judgmentsPerUnit);

					preparedStatement.setInt(13, goldPerAssignment);
					preparedStatement.setString(14, createdAt);
					preparedStatement.setString(15, cml);
				} else {
					preparedStatement = connect
							.prepareStatement("UPDATE crowdlink.job SET typeofmappinggoalcontext=?, onto1=?, onto2=?, title=?, instructions=?, unitsperassignment=?, pagesperassignment=?, judgmentscount=?, unitscount=?, maxjudgmentsperworker=?, judgmentsperunit=?, goldperassignment=?, createtat=?, cml=? WHERE id="
									+ jobId);
					preparedStatement.setString(1, typeOfMapping);
					preparedStatement.setString(2, o1Uri);
					preparedStatement.setString(3, o2Uri);
					preparedStatement.setString(4, title);
					preparedStatement.setString(5, instructions);
					preparedStatement.setInt(6, unitsPerAssignment);
					preparedStatement.setInt(7, pagesPerAssignment);

					preparedStatement.setInt(8, judgmentsCount);
					preparedStatement.setInt(9, unitsCount);
					preparedStatement.setInt(10, maxJudgmentsPerWorker);
					preparedStatement.setInt(11, judgmentsPerUnit);

					preparedStatement.setInt(12, goldPerAssignment);
					preparedStatement.setString(13, createdAt);
					preparedStatement.setString(14, cml);
				}

				int result = preparedStatement.executeUpdate();

			}

			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
			// }
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void storeUnit(String unitId, Map<String, Object> unitData,
			String jobId) {

		String a = null;
		String b = null;
		String relation = null;
		String definitiona = null;
		String definitionb = null;
		String superclassesa = null;
		String superclassesb = null;
		String siblingsa = null;
		String siblingsb = null;
		String subclassesa = null;
		String subclassesb = null;
		String instancesa = null;
		String instancesb = null;
		String uria = null;
		String urib = null;

		String mainGold = null;
		String mainGoldReason = null;
		String verifwordGold = null;
		String verifwordGoldReason = null;
		String verifnumberGold = null;
		String verifnumberGoldReason = null;
		boolean golden = false;

		// read the unit data
		try {
			for (Map.Entry<String, Object> entry : unitData.entrySet()) {

				/*
				 * if((entry.getKey().contains("_gold")||
				 * entry.getKey().contains("_gold_reason"))&&entry.getValue()
				 * instanceof String) { String
				 * value=entry.getValue().toString();
				 * goldConcatenated=goldConcatenated
				 * +entry.getKey().toString()+":"+value+";"; golden=true; }
				 */
				if (entry.getKey().equals("main_gold")
						&& entry.getValue() instanceof ArrayList) {
					ArrayList<String> array = (ArrayList<String>) entry
							.getValue();
					mainGold = array.get(0); // in this case only one answer is
												// possible
					golden = true;
				} else if (entry.getKey().equals("main_gold_reason")
						&& entry.getValue() instanceof ArrayList) {
					ArrayList<String> array = (ArrayList<String>) entry
							.getValue();
					mainGoldReason = array.get(0);
				} else if (entry.getKey().equals("verifword_gold")
						&& entry.getValue() instanceof ArrayList) {
					ArrayList<String> array = (ArrayList<String>) entry
							.getValue();
					verifwordGold = array.get(0);
				} else if (entry.getKey().equals("verifword_gold_reason")
						&& entry.getValue() instanceof ArrayList) {
					ArrayList<String> array = (ArrayList<String>) entry
							.getValue();
					verifwordGoldReason = array.get(0);
				} else if (entry.getKey().equals("verifnumber_gold")
						&& entry.getValue() instanceof ArrayList) {
					ArrayList<String> array = (ArrayList<String>) entry
							.getValue();
					verifnumberGold = array.get(0);
				} else if (entry.getKey().equals("verifnumber_gold_reason")
						&& entry.getValue() instanceof ArrayList) {
					ArrayList<String> array = (ArrayList<String>) entry
							.getValue();
					verifnumberGoldReason = array.get(0);
				} else if (entry.getKey().equals("a")
						&& entry.getValue() instanceof String) {
					a = entry.getValue().toString();

				} else if (entry.getKey().equals("b")
						&& entry.getValue() instanceof String) {
					b = entry.getValue().toString();

				} else if (entry.getKey().equals("relation")
						&& entry.getValue() instanceof String) {
					relation = entry.getValue().toString();

				} else if (entry.getKey().equals("definitiona")
						&& entry.getValue() instanceof String) {
					definitiona = entry.getValue().toString();

				} else if (entry.getKey().equals("definitionb")
						&& entry.getValue() instanceof String) {
					definitionb = entry.getValue().toString();

				} else if (entry.getKey().equals("superclassesa")
						&& entry.getValue() instanceof String) {
					superclassesa = entry.getValue().toString();

				} else if (entry.getKey().equals("superclassesb")
						&& entry.getValue() instanceof String) {
					superclassesb = entry.getValue().toString();

				} else if (entry.getKey().equals("siblingsa")
						&& entry.getValue() instanceof String) {
					siblingsa = entry.getValue().toString();

				} else if (entry.getKey().equals("siblingsb")
						&& entry.getValue() instanceof String) {
					siblingsb = entry.getValue().toString();

				} else if (entry.getKey().equals("subclassesa")
						&& entry.getValue() instanceof String) {
					subclassesa = entry.getValue().toString();

				} else if (entry.getKey().equals("subclassesb")
						&& entry.getValue() instanceof String) {
					subclassesb = entry.getValue().toString();

				} else if (entry.getKey().equals("instancesa")
						&& entry.getValue() instanceof String) {
					instancesa = entry.getValue().toString();

				} else if (entry.getKey().equals("instancesb")
						&& entry.getValue() instanceof String) {
					instancesb = entry.getValue().toString();

				} else if (entry.getKey().equals("uria")
						&& entry.getValue() instanceof String) {
					uria = entry.getValue().toString();

				} else if (entry.getKey().equals("urib")
						&& entry.getValue() instanceof String) {
					urib = entry.getValue().toString();

				}
			}
			// insert into database unit

			Class.forName("com.mysql.jdbc.Driver");
			// Setup the connection with the DB
			Connection connect = DriverManager
					.getConnection(ConfigurationManager.getInstance()
							.getMySqlURL()
							+ "?user="
							+ ConfigurationManager.getInstance().getMySqlUser()
							+ "&password="
							+ ConfigurationManager.getInstance()
									.getMySqlPassword());

			// check that the job does not exist -- otherwise do not store / USE
			// ON DUPLICATE KEY UPDATE?

			Statement statement = connect.createStatement();
			// check that the unit does not exist -- otherwise do not store /
			// USE

			ResultSet resultSet = statement
					.executeQuery("SELECT * FROM crowdlink.unit WHERE id="
							+ unitId);
			// if (!resultSet.next()) {

			/*
			 * correct one! int result = statement2.executeUpdate(
			 * "INSERT INTO crowdlink.unit (id, a, b, relation, definitiona, definitionb, superclassa, superclassb, siblingsa,siblingsb, subclassesa, subclassesb, instancesa, instancesb, uria, urib, golden, goldconcatenated) VALUES ('"
			 * +
			 * unitId+"', '"+a+"', '"+b+"', '"+relation+"', '"+definitiona+"', '"
			 * +
			 * definitionb+"', '"+superclassa+"', '"+superclassb+"', '"+siblingsa
			 * +"', '"+siblingsb+"', '"+subclassesa+"', '"+subclassesb+"', '"+
			 * instancesa
			 * +"', '"+instancesb+"', '"+uria+"', '"+urib+"', '"+golden
			 * +"', '"+goldConcatenated+"')");
			 */
			// MySQL bug BOOLEAN not working --> TinyInt?
			int goldenValue = 0;
			if (golden) {
				goldenValue = 1;
			}
			/*
			 * int result = statement2.executeUpdate(
			 * "INSERT INTO crowdlink.unit (id, a, b, relation, superclassa, superclassb,  instancesa, instancesb, uria, urib, golden, maingold, maingoldreason, verifwordgold, verifwordgoldreason, verifnumbergold, verifnumbergoldreason) "
			 * +"VALUES ('"+unitId+"', '"+a+"', '"+b+"', '"+relation+"', '"+
			 * superclassa
			 * +"', '"+superclassb+"', '"+instancesa+"', '"+instancesb
			 * +"', '"+uria
			 * +"', '"+urib+"', '"+goldenValue+"', '"+mainGold+"', '"
			 * +mainGoldReason
			 * +"', '"+verifwordGold+"', '"+verifwordGoldReason+"', '"
			 * +verifnumberGold+"', '"+verifnumberGoldReason+"')");
			 */
			PreparedStatement preparedStatement2 = null;

			if (!resultSet.next()) {
				preparedStatement2 = connect
						.prepareStatement("INSERT INTO crowdlink.unit (id, a, b, relation, definitiona, definitionb, superclassesa, superclassesb, siblingsa,siblingsb, subclassesa, subclassesb, instancesa, instancesb, uria, urib, golden, maingold, maingoldreason, verifwordgold, verifwordgoldreason, verifnumbergold, verifnumbergoldreason) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				preparedStatement2.setString(1, unitId);
				preparedStatement2.setString(2, a);
				preparedStatement2.setString(3, b);
				preparedStatement2.setString(4, relation);
				preparedStatement2.setString(5, definitiona);
				preparedStatement2.setString(6, definitionb);
				preparedStatement2.setString(7, superclassesa);
				preparedStatement2.setString(8, superclassesb);
				preparedStatement2.setString(9, siblingsa);
				preparedStatement2.setString(10, siblingsb);
				preparedStatement2.setString(11, subclassesa);
				preparedStatement2.setString(12, subclassesb);
				preparedStatement2.setString(13, instancesa);
				preparedStatement2.setString(14, instancesb);
				preparedStatement2.setString(15, uria);
				preparedStatement2.setString(16, urib);
				preparedStatement2.setInt(17, goldenValue);
				preparedStatement2.setString(18, mainGold);
				preparedStatement2.setString(19, mainGoldReason);
				preparedStatement2.setString(20, verifwordGold);
				preparedStatement2.setString(21, verifwordGoldReason);
				preparedStatement2.setString(22, verifnumberGold);
				preparedStatement2.setString(23, verifnumberGoldReason);
			} else {
				preparedStatement2 = connect
						.prepareStatement("UPDATE crowdlink.unit SET a=?, b=?, relation=?, definitiona=?, definitionb=?, superclassesa=?, superclassesb=?, siblingsa=?,siblingsb=?, subclassesa=?, subclassesb=?, instancesa=?, instancesb=?, uria=?, urib=?, golden=?, maingold=?, maingoldreason=?, verifwordgold=?, verifwordgoldreason=?, verifnumbergold=?, verifnumbergoldreason=? WHERE id="
								+ unitId);
				preparedStatement2.setString(1, a);
				preparedStatement2.setString(2, b);
				preparedStatement2.setString(3, relation);
				preparedStatement2.setString(4, definitiona);
				preparedStatement2.setString(5, definitionb);
				preparedStatement2.setString(6, superclassesa);
				preparedStatement2.setString(7, superclassesb);
				preparedStatement2.setString(8, siblingsa);
				preparedStatement2.setString(9, siblingsb);
				preparedStatement2.setString(10, subclassesa);
				preparedStatement2.setString(11, subclassesb);
				preparedStatement2.setString(12, instancesa);
				preparedStatement2.setString(13, instancesb);
				preparedStatement2.setString(14, uria);
				preparedStatement2.setString(15, urib);
				preparedStatement2.setInt(16, goldenValue);
				preparedStatement2.setString(17, mainGold);
				preparedStatement2.setString(18, mainGoldReason);
				preparedStatement2.setString(19, verifwordGold);
				preparedStatement2.setString(20, verifwordGoldReason);
				preparedStatement2.setString(21, verifnumberGold);
				preparedStatement2.setString(22, verifnumberGoldReason);
				// System.out.println(preparedStatement2.toString());
			}

			int result2 = preparedStatement2.executeUpdate();

			// int result =
			// statement2.executeUpdate("INSERT INTO crowdlink.unit (id, a, b, relation, definitiona, definitionb, superclassa, superclassb, siblingsa,siblingsb, subclassesa, subclassesb, instancesa, instancesb, uria, urib, golden, goldconcatenated) VALUES ('"+unitId+"', '"+a+"', '"+b+"', '"+relation+"', '"+definitiona+"', '"+definitionb+"', '"+superclassa+"', '"+superclassb+"', '"+siblingsa+"', '"+siblingsb+"', '"+subclassesa+"', '"+subclassesb+"', '"+instancesa+"', '"+instancesb+"', '"+uria+"', '"+urib+"', '"+golden+"', '"+goldConcatenated+"')");

			Statement statement3 = connect.createStatement();
			ResultSet resultSet3 = statement3
					.executeQuery("SELECT * FROM crowdlink.jobunit WHERE jobid="
							+ jobId + "&& unitid=" + unitId);
			// if (!resultSet3.next())
			// {
			PreparedStatement preparedStatement3 = null;
			if (!resultSet3.next()) {
				preparedStatement3 = connect
						.prepareStatement("INSERT INTO crowdlink.jobunit (jobid, unitid) VALUES (?,?)");
				preparedStatement3.setString(1, jobId);
				preparedStatement3.setString(2, unitId);
				int result3 = preparedStatement3.executeUpdate();
			}
			// in the else there is nothing else to UPDATE

			// }
			// }

			// insert into database jobunit
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void storeJudgmentOfUnit(Map.Entry<String, Object> judgmentData,
			String unitId) {

		// read the judgments of unit

		// The worker_id, the id come in Integer format in the API as values --
		// UnitId is the key, not the Value in /units
		Integer idInt = 0;
		String id = null;
		Integer workerIdInt = 0;
		String workerId = null;
		String city = null;
		String startedAt = null;
		String createdAt = null;
		int trust = 0;
		String main = null;
		String verifword = null;
		String verifnumber = null;
		// they can be null ?À
		Boolean tainted = null;
		Boolean rejected = null;
		Boolean missed = null;
		int region = 0;
		String country = null;
		boolean golden = false;
		int judgment = 0;

		double unitAgreement = 0.0;
		String aggResponse = null;
		double confidence = 0.0;

		Map<String, Object> results;
		Map<String, Object> aggItem;
		int taintedValue = 0;
		int rejectedValue = 0;
		int missedValue = 0;
		int goldenValue = 0;

		try {
			String agg = ConfigurationManager.getInstance()
					.getNameFormElementToWatch();

			// read normal judgments & agg judgment

			if (judgmentData.getKey().equals("results")
					&& judgmentData.getValue() instanceof Map) {
				results = (Map<String, Object>) judgmentData.getValue();

				for (Map.Entry<String, Object> entry : results.entrySet()) {

					if (entry.getValue() instanceof Map
							&& entry.getKey().equals(agg)) {

						aggItem = (Map<String, Object>) entry.getValue();

						for (Map.Entry<String, Object> entry2 : aggItem
								.entrySet()) {
							if (entry2.getKey().equals("agg")
									&& entry2.getValue() instanceof String) {
								aggResponse = (String) entry2.getValue();
								// System.out.println("aggResponse: "+
								// aggResponse);
							} else if (entry2.getKey().equals("confidence")
									&& entry2.getValue() instanceof Double) {

								confidence = (Double) entry2.getValue();
								// System.out.println("confidence: "+
								// confidence);
							}
						}
					} else if (entry.getKey().equals("judgments")
							&& entry.getValue() instanceof ArrayList) {
						ArrayList<Map<String, Object>> judgmentsArray = (ArrayList<Map<String, Object>>) entry
								.getValue();

						for (Map<String, Object> elem : judgmentsArray) {
							for (Map.Entry<String, Object> entry3 : elem
									.entrySet()) {
								if (entry3.getKey().equals("id")
										&& entry3.getValue() instanceof Integer) {
									idInt = (Integer) entry3.getValue();
									id = idInt.toString();
									// System.out.println("id: "+id+
									// " idInt: "+idInt+ " unitID: "+unitId);

								} else if (entry3.getKey().equals("worker_id")
										&& entry3.getValue() instanceof Integer) {
									workerIdInt = (Integer) entry3.getValue();
									workerId = workerIdInt.toString();
								} else if (entry3.getKey().equals("city")
										&& entry3.getValue() instanceof String) {
									city = entry3.getValue().toString();
								} else if (entry3.getKey().equals("started_at")
										&& entry3.getValue() instanceof String) {
									startedAt = entry3.getValue().toString();
								} else if (entry3.getKey().equals("created_at")
										&& entry3.getValue() instanceof String) {
									createdAt = entry3.getValue().toString();
								} else if (entry3.getKey().equals("trust")
										&& entry3.getValue() instanceof Integer) {
									trust = (Integer) entry3.getValue();
								}

								else if (entry3.getKey().equals("data")
										&& entry3.getValue() instanceof Map) {
									Map<String, Object> dataMap = (Map<String, Object>) entry3
											.getValue();
									for (Map.Entry<String, Object> entry4 : dataMap
											.entrySet()) {
										if (entry4.getKey().equals("main")
												&& entry4.getValue() instanceof String) {
											main = entry4.getValue().toString();
										} else if (entry4.getKey().equals(
												"verifword")
												&& entry4.getValue() instanceof String) {
											verifword = entry4.getValue()
													.toString();
										} else if (entry4.getKey().equals(
												"verifnumber")
												&& entry4.getValue() instanceof String) {
											verifnumber = entry4.getValue()
													.toString();
										}
									}
								}
								// tainted is fulfilled but missed and rejected
								// no -- In cwdf
								else if (entry3.getKey().equals("tainted")
										&& entry3.getValue() instanceof Boolean) {
									tainted = (Boolean) entry3.getValue();
									if (tainted.booleanValue()) {
										taintedValue = 1;
									}
								} else if (entry3.getKey().equals("rejected")
										&& entry3.getValue() instanceof Boolean) {
									rejected = (Boolean) entry3.getValue();
									if (rejected.booleanValue()) {
										rejectedValue = 1;
									}
								} else if (entry3.getKey().equals("missed")
										&& entry3.getValue() instanceof Boolean) {
									missed = (Boolean) entry3.getValue();
									if (missed.booleanValue()) {
										missedValue = 1;
									}
								}
								// -
								else if (entry3.getKey().equals("region")
										&& entry3.getValue() instanceof Integer) {
									region = (Integer) entry3.getValue();
								} else if (entry3.getKey().equals("country")
										&& entry3.getValue() instanceof String) {
									country = entry3.getValue().toString();
								} else if (entry3.getKey().equals("golden")
										&& entry3.getValue() instanceof Boolean) {
									golden = (Boolean) entry3.getValue();
									if (golden) {
										goldenValue = 1;
									}
								} else if (entry3.getKey().equals("judgment")
										&& entry3.getValue() instanceof Integer) {
									judgment = (Integer) entry3.getValue();
								}

							}
							Class.forName("com.mysql.jdbc.Driver");
							// Setup the connection with the DB
							Connection connect = DriverManager
									.getConnection(ConfigurationManager
											.getInstance().getMySqlURL()
											+ "?user="
											+ ConfigurationManager
													.getInstance()
													.getMySqlUser()
											+ "&password="
											+ ConfigurationManager
													.getInstance()
													.getMySqlPassword());
							// save judgment

							// check does not exist
							Statement statement = connect.createStatement();
							// check that the unit does not exist -- otherwise
							// do not store / USE

							ResultSet resultSet = statement
									.executeQuery("SELECT * FROM crowdlink.judgment WHERE id="
											+ id);
							// if (!resultSet.next()) {
							PreparedStatement preparedStatement = null;

							if (!resultSet.next()) {
								preparedStatement = connect
										.prepareStatement("INSERT INTO crowdlink.judgment (id, workerid, city, startedat, createdat, trust, main, verifword, verifnumber, tainted, rejected, missed, region, country, golden, judgment) VALUE (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
								preparedStatement.setString(1, id);
								preparedStatement.setString(2, workerId);
								preparedStatement.setString(3, city);
								preparedStatement.setString(4, startedAt);
								preparedStatement.setString(5, createdAt);
								preparedStatement.setInt(6, trust);
								preparedStatement.setString(7, main);
								preparedStatement.setString(8, verifword);
								preparedStatement.setString(9, verifnumber);
								preparedStatement.setInt(10, taintedValue);

								preparedStatement.setInt(11, rejectedValue);
								preparedStatement.setInt(12, missedValue);

								preparedStatement.setInt(13, region);
								preparedStatement.setString(14, country);
								preparedStatement.setInt(15, goldenValue);
								preparedStatement.setInt(16, judgment);
							} else {
								preparedStatement = connect
										.prepareStatement("UPDATE crowdlink.judgment SET workerid=?, city=?, startedat=?, createdat=?, trust=?, main=?, verifword=?, verifnumber=?, tainted=?, rejected=?, missed=?, region=?, country=?, golden=?, judgment=? WHERE id="
												+ id);

								preparedStatement.setString(1, workerId);
								preparedStatement.setString(2, city);
								preparedStatement.setString(3, startedAt);
								preparedStatement.setString(4, createdAt);
								preparedStatement.setInt(5, trust);
								preparedStatement.setString(6, main);
								preparedStatement.setString(7, verifword);
								preparedStatement.setString(8, verifnumber);
								preparedStatement.setInt(9, taintedValue);

								preparedStatement.setInt(10, rejectedValue);
								preparedStatement.setInt(11, missedValue);

								preparedStatement.setInt(12, region);
								preparedStatement.setString(13, country);
								preparedStatement.setInt(14, goldenValue);
								preparedStatement.setInt(15, judgment);
							}

							preparedStatement.executeUpdate();

							Statement statement2 = connect.createStatement();
							// check that the unit does not exist -- otherwise
							// do not store / USE

							ResultSet resultSet2 = statement2
									.executeQuery("SELECT * FROM crowdlink.unitjudgment WHERE unitid="
											+ unitId + " && judgmentid=" + id);
							// if (!resultSet2.next()) {

							PreparedStatement preparedStatement2 = null;
							if (!resultSet2.next()) {
								preparedStatement2 = connect
										.prepareStatement("INSERT INTO crowdlink.unitjudgment (unitid, judgmentid) VALUES (?,?)");
								preparedStatement2.setString(1, unitId);
								preparedStatement2.setString(2, id);
								int result2 = preparedStatement2
										.executeUpdate();

							}
							// in the case of the else there is nothing else to
							// update here

							// }

							// save aggResponse, confidence and unitagreement ni
							// the unit table
							Statement statement3 = connect.createStatement();
							// check that the unit exists!
							ResultSet resultSet3 = statement3
									.executeQuery("SELECT * FROM crowdlink.unit WHERE id="
											+ unitId);
							if (resultSet3.next()) {
								PreparedStatement preparedStatement3 = connect
										.prepareStatement("UPDATE crowdlink.unit SET aggresponse=?, confidence=?, unitagreement=?");
								preparedStatement3.setString(1, aggResponse);
								preparedStatement3.setDouble(2, confidence);
								preparedStatement3.setDouble(3, unitAgreement);
								int result3 = preparedStatement3
										.executeUpdate();

							}

							// }
						}
					}
				}

			} else if (judgmentData.getKey().equals("agreement")
					&& judgmentData.getValue() instanceof Double) {
				unitAgreement = (Double) judgmentData.getValue();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
