package responses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.io.IOUtils;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.transcribathon.properties.PropertiesCache;
import objects.ApiKey;
import objects.Dataset;
import objects.Project;

@Path("/projects")
public class ProjectResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<Project> projectList = new ArrayList<Project>();
		   ResultSet rs = null;
		   Connection conn = null;
		   Statement stmt = null;

		   // Register JDBC driver
		   try {
			Class.forName(PropertiesCache.getInstance().getProperty("DRIVER"));

		   // Open a connection
		   conn = DriverManager.getConnection(
				   PropertiesCache.getInstance().getProperty("DB_URL"),
				   PropertiesCache.getInstance().getProperty("USER"),
				   PropertiesCache.getInstance().getProperty("PASS")
				   );
		   // Execute SQL query
		   stmt = conn.createStatement();
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   return type +" succesful";
			   }
			   else {
				   return type +" could not be executed";
			   }
		   }
		   rs = stmt.executeQuery(query);

		   // Extract data from result set
		   while(rs.next()){
		      //Retrieve by column name
			  Project Project = new Project();
			  Project.setProjectId(rs.getInt("ProjectId"));
			  Project.setName(rs.getString("Name"));
			  projectList.add(Project);
		   }

		   // Clean-up environment
		   rs.close();
		   stmt.close();
		   conn.close();
		   } catch(SQLException se) {
		       //Handle errors for JDBC
			   se.printStackTrace();
		   } catch (ClassNotFoundException e) {
			   e.printStackTrace();
		}  finally {
		    try { rs.close(); } catch (Exception e) { /* ignored */ }
		    try { stmt.close(); } catch (Exception e) { /* ignored */ }
		    try { conn.close(); } catch (Exception e) { /* ignored */ }
		}

	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(projectList);
	    return result;
	}

	public String getApiKeys() throws SQLException{
			String query = "SELECT * FROM ApiKey";
		   List<ApiKey> apiKeys = new ArrayList<ApiKey>();
		   ResultSet rs = null;
		   Connection conn = null;
		   Statement stmt = null;

		   // Register JDBC driver
		   try {
			Class.forName(PropertiesCache.getInstance().getProperty("DRIVER"));

		   // Open a connection
		   conn = DriverManager.getConnection(
				   PropertiesCache.getInstance().getProperty("DB_URL"),
				   PropertiesCache.getInstance().getProperty("USER"),
				   PropertiesCache.getInstance().getProperty("PASS")
				   );
		   // Execute SQL query
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery(query);

		   // Extract data from result set
		   while(rs.next()){
		      //Retrieve by column name
			  ApiKey apiKey = new ApiKey();
			  apiKey.setApiKeyId(rs.getInt("ApiKeyId"));
			  apiKey.setKeyString(rs.getString("KeyString"));
			  apiKey.setProjectId(rs.getInt("ProjectId"));
			  apiKey.setRoleId(rs.getInt("RoleId"));
			  apiKeys.add(apiKey);
		   }

		   // Clean-up environment
		   rs.close();
		   stmt.close();
		   conn.close();
		   } catch(SQLException se) {
		       //Handle errors for JDBC
			   se.printStackTrace();
		   } catch (ClassNotFoundException e) {
			   e.printStackTrace();
		}  finally {
		    try { rs.close(); } catch (Exception e) { /* ignored */ }
		    try { stmt.close(); } catch (Exception e) { /* ignored */ }
		    try { conn.close(); } catch (Exception e) { /* ignored */ }
	   }

	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(apiKeys);
	    return result;
	}

	public String executeDatasetQuery(String query, String type) throws SQLException{
	    List<Dataset> datasetList = new ArrayList<Dataset>();
	   ResultSet rs = null;
	   Connection conn = null;
	   Statement stmt = null;

		   // Register JDBC driver
		   try {
			Class.forName(PropertiesCache.getInstance().getProperty("DRIVER"));

		   // Open a connection
		   conn = DriverManager.getConnection(
				   PropertiesCache.getInstance().getProperty("DB_URL"),
				   PropertiesCache.getInstance().getProperty("USER"),
				   PropertiesCache.getInstance().getProperty("PASS")
				   );
		   // Execute SQL query
		   stmt = conn.createStatement();
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   return type +" succesful";
			   }
			   else {
				   return type +" could not be executed";
			   }
		   }
		   rs = stmt.executeQuery(query);

		   // Extract data from result set
		   while(rs.next()){
		      //Retrieve by column name
			  Dataset Dataset = new Dataset();
			  Dataset.setDatasetId(rs.getInt("DatasetId"));
			  Dataset.setName(rs.getString("Name"));
			  Dataset.setProjectId(rs.getInt("ProjectId"));
			  datasetList.add(Dataset);
		   }

		   // Clean-up environment
		   rs.close();
		   stmt.close();
		   conn.close();
		   } catch(SQLException se) {
		       //Handle errors for JDBC
			   se.printStackTrace();
		   } catch (ClassNotFoundException e) {
			   e.printStackTrace();
		}  finally {
		    try { rs.close(); } catch (Exception e) { /* ignored */ }
		    try { stmt.close(); } catch (Exception e) { /* ignored */ }
		    try { conn.close(); } catch (Exception e) { /* ignored */ }
	   }

	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(datasetList);
	    return result;
	}


	public Integer executeStoryQuery(String query, String type) throws SQLException{
	   ResultSet rs = null;
	   Connection conn = null;
	   Statement stmt = null;

		   // Register JDBC driver
		   try {
			Class.forName(PropertiesCache.getInstance().getProperty("DRIVER"));

		   // Open a connection
		   conn = DriverManager.getConnection(
				   PropertiesCache.getInstance().getProperty("DB_URL"),
				   PropertiesCache.getInstance().getProperty("USER"),
				   PropertiesCache.getInstance().getProperty("PASS")
				   );
		   // Execute SQL query
		   stmt = conn.createStatement();
		   rs = stmt.executeQuery(query);

		   // Extract data from result set
		   while(rs.next()){
			    return rs.getInt("StoryId");
		   }

		   // Clean-up environment
		   rs.close();
		   stmt.close();
		   conn.close();
		   } catch(SQLException se) {
		       //Handle errors for JDBC
			   se.printStackTrace();
		   } catch (ClassNotFoundException e) {
			   e.printStackTrace();
		}  finally {
		    try { rs.close(); } catch (Exception e) { /* ignored */ }
		    try { stmt.close(); } catch (Exception e) { /* ignored */ }
		    try { conn.close(); } catch (Exception e) { /* ignored */ }
	   }

	    return 0;
	}

	//Get Entries

	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo, String body) throws SQLException {
		String query = "SELECT * FROM Project WHERE 1";

		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

		for(String key : queryParams.keySet()){
			String[] values = queryParams.getFirst(key).split(",");
			query += " AND (";
		    int valueCount = values.length;
		    int i = 1;
		    for(String value : values) {
		    	query += key + " = '" + value + "'";
			    if (i < valueCount) {
			    	query += " OR ";
			    }
			    i++;
		    }
		    query += ")";
		}
		String resource = executeQuery(query, "Select");
		if (resource == "Failed") {
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
	        return rBuild.build();
		}
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Add new entry

	@POST
	public String add(String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Project project = gson.fromJson(body, Project.class);

	    //Check if all mandatory fields are included
	    if (project.ProjectId != null && project.Name != null) {
			String query = "INSERT INTO Project (ProjectId, Name) "
							+ "VALUES (" + project.ProjectId
							+ ", '" + project.Name + "')";
			String resource = executeQuery(query, "Insert");
			return resource;
	    } else {
	    	return "Fields missing";
	    }
	}


	//Add new entry
	@Path("/test")
	@POST
	public String test(String body) throws SQLException, IOException {
	    URL storySolr = new URL(PropertiesCache.getInstance().getProperty("SOLR") + "/solr/Stories/dataimport?command=full-import&clean=true");
	    HttpURLConnection con = (HttpURLConnection) storySolr.openConnection();
	    con.setRequestMethod("GET");
		  con.setConnectTimeout(Integer.parseInt(PropertiesCache.getInstance().getProperty("TIMEOUT")));
	    BufferedReader in = new BufferedReader(
	    new InputStreamReader(con.getInputStream()));
	    String inputLine;
	    StringBuffer content = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	        content.append(inputLine);
	    }
	    in.close();
	    con.disconnect();

	    URL itemSolr = new URL(PropertiesCache.getInstance().getProperty("SOLR") + "/solr/Items/dataimport?command=full-import&clean=true");
	    con = (HttpURLConnection) itemSolr.openConnection();
	    con.setRequestMethod("GET");
		  con.setConnectTimeout(Integer.parseInt(PropertiesCache.getInstance().getProperty("TIMEOUT")));
	    in = new BufferedReader(
	    new InputStreamReader(con.getInputStream()));
	    content = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	        content.append(inputLine);
	    }
	    in.close();
		return content.toString();
	}


	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);

	    //Check if field is allowed to be changed
	    if (changes.get("ProjectId") != null) {
	    	return "Prohibited change attempt";
	    }

	    //Check if NOT NULL field is attempted to be changed to NULL
	    if (changes.get("Name") == null || !changes.get("Name").isJsonNull()){
		    String query = "UPDATE Project SET ";

		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE ProjectId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited changes to null";
	    }
	}


	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("DELETE FROM Project WHERE ProjectId = " + id, "Delete");
		return resource;
	}


	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id, String body) throws SQLException {
		String resource = executeQuery("SELECT * FROM Project WHERE ProjectId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Get entry by id
	@Path("/{project_id}/datasets")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getDatasets(@PathParam("project_id") int projectId, String body) throws SQLException {
		String query = "SELECT * FROM Dataset WHERE ProjectId = " + projectId;
		String resource = executeDatasetQuery(query, "Select");
		if (resource == "Failed") {
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
	        return rBuild.build();
		}
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}


	public String executeInsertQuery(String query, String type) throws SQLException, ClientProtocolException, IOException{
		   Connection conn = null;
		   Statement stmt = null;
		   try {

    		HttpClient httpclient = HttpClients.createDefault();

            HttpPost httppost = new HttpPost("https://sso.apps.paas-dev.psnc.pl/auth/realms/EnrichEuropeana/protocol/openid-connect/token");

    	        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
    	        params.add(new BasicNameValuePair("grant_type", "client_credentials"));
    	        params.add(new BasicNameValuePair("client_secret", PropertiesCache.getInstance().getProperty("SECRET")));
    	        params.add(new BasicNameValuePair("client_id", "tp-api-client"));
    	        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
    	        HttpResponse response = httpclient.execute(httppost);
    	        HttpEntity entity = response.getEntity();

    	        if (entity != null) {
    	            try (InputStream instream = entity.getContent()) {
    	                StringWriter writer = new StringWriter();
    	                IOUtils.copy(instream, writer, StandardCharsets.UTF_8);
    	                JsonObject data = new JsonParser().parse(writer.toString()).getAsJsonObject();

    	    	        //String authHeader = data.get("access_token").toString();
    	    	        //httppost2.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
    	            }
    	        }


	   // Register JDBC driver
	   try {
			Class.forName(PropertiesCache.getInstance().getProperty("DRIVER"));

		   // Open a connection
		   conn = DriverManager.getConnection(
				   PropertiesCache.getInstance().getProperty("DB_URL"),
				   PropertiesCache.getInstance().getProperty("USER"),
				   PropertiesCache.getInstance().getProperty("PASS")
				   );
		   // Execute SQL query
		   stmt = conn.createStatement();
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   conn.close();
				   stmt.close();
				   return type +" succesful";
			   }
			   else {
				   conn.close();
				   stmt.close();
				   return "Failed";
			   }
		   }
		   else {
			   conn.close();
			   stmt.close();
			   return "test2";
		   }
	   } catch(SQLException se) {
	       //Handle errors for JDBC
		   se.printStackTrace();
	   } catch (ClassNotFoundException e) {
		   e.printStackTrace();
	   }  finally {
		    try { stmt.close(); } catch (Exception e) { /* ignored */ }
		    try { conn.close(); } catch (Exception e) { /* ignored */ }
	   }
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}  finally {
		    try { stmt.close(); } catch (Exception e) { /* ignored */ }
		    try { conn.close(); } catch (Exception e) { /* ignored */ }
	   }
		return "Failed";
	}

	//Get entry by id
	@Path("/{project_id}/stories")
	@POST
	public Response insertStory(@PathParam("project_id") int projectId, @Context UriInfo uriInfo, String body, @Context HttpHeaders headers) throws Exception {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

		boolean auth = false;
		String authorizationToken = "";
		if (headers.getRequestHeader(HttpHeaders.AUTHORIZATION) != null) {
			List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
			authorizationToken = authHeaders.get(0);
			String tokens = getApiKeys();
			JsonArray data = new JsonParser().parse(tokens).getAsJsonArray();

			for (int i = 0; i < data.size(); i++) {
				if (data.get(i).getAsJsonObject().get("KeyString").toString().replace("\"", "").equals(authorizationToken)) {
					auth = true;
					break;
				}
			}
		}
		if (auth != true) {
			ResponseBuilder authResponse = Response.status(Response.Status.UNAUTHORIZED);
			return authResponse.build();
		}
		JsonObject data = new JsonParser().parse(body).getAsJsonObject();
		JsonArray dataArray = data.getAsJsonObject().get("@graph").getAsJsonArray();
		List<String> fields = new ArrayList<String>();
		fields.add("dc:title");
		fields.add("dc:description");
		fields.add("edm:landingPage");
		fields.add("dc:creator");
		fields.add("dc:source");
		fields.add("edm:country");
		fields.add("edm:dataProvider");
		fields.add("edm:provider");
		fields.add("edm:rights");
		fields.add("edm:begin");
		fields.add("edm:end");
		fields.add("dc:contributor");
		fields.add("edm:year");
		fields.add("dc:publisher");
		fields.add("dc:coverage");
		fields.add("dc:date");
		fields.add("dc:type");
		fields.add("dc:relation");
		fields.add("dcterms:medium");
		fields.add("edm:datasetName");
		fields.add("edm:isShownAt");
		fields.add("dc:rights");
		fields.add("dc:identifier");
		fields.add("dc:language");
		fields.add("edm:language");
		fields.add("edm:agent");
		fields.add("dcterms:provenance");
		fields.add("dcterms:created");
		boolean placeAdded = false;
	    int keyCount = dataArray.size();

		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		String manifestUrl = "";
		Boolean converted = false;
		String storyTitle = "";
		String externalRecordId = "";
		String recordId = "";
		String imageLink = "";
		String pdfImage = "";
		List<String> imageLinks = new ArrayList<String>();

		if (data.getAsJsonObject().has("iiif_url")) {
			manifestUrl = data.getAsJsonObject().get("iiif_url").getAsString();
			converted = true;
		}

		for (int i = 0; i < keyCount; i++) {
			for(Map.Entry<String, JsonElement> entry : dataArray.get(i).getAsJsonObject().entrySet()) {
				if (fields.contains(entry.getKey())) {
					if (entry.getValue().isJsonObject()) {
						if (entry.getValue().getAsJsonObject().has("@value")) {
							if (!keys.contains(entry.getKey())) {
								keys.add(entry.getKey());
								values.add(entry.getValue().getAsJsonObject().get("@value").toString());
								if (entry.getKey().equals("dc:title")) {
									storyTitle = entry.getValue().getAsJsonObject().get("@value").toString();
								}
							}
							else {
								int index = keys.indexOf(entry.getKey());
								values.set(index, "\"" + values.get(index).replace("\"", "") + " || " + entry.getValue().getAsJsonObject().get("@value").toString().replace("\"", "") + "\"");
								if (entry.getKey().equals("dc:title")) {
									storyTitle = "\"" + storyTitle.replace("\"", "") + " || " + entry.getValue().getAsJsonObject().get("@value").toString().replace("\"", "") + "\"";
								}
							}
						}
						else if (entry.getValue().getAsJsonObject().has("@id")) {
							if (!keys.contains(entry.getKey())) {
								keys.add(entry.getKey());
								values.add(entry.getValue().getAsJsonObject().get("@id").toString());
								if (entry.getKey().equals("dc:title")) {
									storyTitle = entry.getValue().getAsJsonObject().get("@id").toString();
								}
							}
							else {
								int index = keys.indexOf(entry.getKey());
								values.set(index, "\"" + values.get(index).replace("\"", "") + " || " + entry.getValue().getAsJsonObject().get("@id").toString().replace("\"", "") + "\"");
								if (entry.getKey().equals("dc:title")) {
									storyTitle = "\"" + storyTitle.replace("\"", "") + " || " + entry.getValue().getAsJsonObject().get("@value").toString().replace("\"", "") + "\"";
								}
							}
						}
					}
					else if (entry.getValue().isJsonArray()) {
						if (!keys.contains(entry.getKey())) {
							String key = "";
							String value = "";
							for (int j = 0; j < entry.getValue().getAsJsonArray().size(); j++) {
								if (entry.getValue().getAsJsonArray().get(j).isJsonObject()) {
									JsonObject element = entry.getValue().getAsJsonArray().get(j).getAsJsonObject();
									if (key == "") {
										if (element.has("@value")) {
											key = entry.getKey();
											value = element.get("@value").toString();
										}
										if (element.has("@id")) {
											key = entry.getKey();
											value = element.get("@id").toString();
										}
									}
									else {
										if (element.has("@language") && element.get("@language").toString().contains("en")) {
											key = entry.getKey();
											value = element.get("@value").toString();
										}
										else {
											if (element.has("@value")) {
												value = "\"" + value.replace("\"", "") + " || " +  element.get("@value").toString().replace("\"", "") + "\"";
											}
											if (element.has("@id")) {
												value = "\"" + value.replace("\"", "") + " || " +  element.get("@id").toString().replace("\"", "") + "\"";
											}
										}
									}
								}
								else {
									if (key == "") {
										key = entry.getKey();
										value = entry.getValue().getAsJsonArray().get(j).toString();
									}
									else {
										value = "\"" + value.replace("\"", "") + " || " + entry.getValue().getAsJsonArray().get(j).toString().replace("\"", "") + "\"";
									}
								}
							}
							if (key != "" && value != "") {
								keys.add(key);
								if (entry.getKey().equals("dc:description")) {
									values.add("\"" + value.toString().replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
								}
								else {
									values.add(value);
									if (entry.getKey().equals("dc:title")) {
										storyTitle = "\"" + value.replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"";
									}
								}
							}
						}
						else {
							int index = keys.indexOf(entry.getKey());
							String value = "";
							for (int j = 0; j < entry.getValue().getAsJsonArray().size(); j++) {
								if (entry.getValue().getAsJsonArray().get(j).isJsonObject()) {
									JsonObject element = entry.getValue().getAsJsonArray().get(j).getAsJsonObject();
									if (element.has("@language") && element.get("@language").toString().contains("en")) {
										value = "\"" + value.replace("\"", "") + " || " +  element.get("@value").toString().replace("\"", "") + "\"";
									}
									else {
										if (element.has("@value")) {
											value = "\"" + value.replace("\"", "") + " || " +  element.get("@value").toString().replace("\"", "") + "\"";
										}
										if (element.has("@id")) {
											value = "\"" + value.replace("\"", "") + " || " +  element.get("@id").toString().replace("\"", "") + "\"";
										}
									}
								}
								else {
									value = "\"" + value.replace("\"", "") + " || " + entry.getValue().getAsJsonArray().get(j).toString().replace("\"", "") + "\"";
								}
							}
							if (entry.getKey().equals("dc:description")) {
								values.set(index, "\"" + values.get(index).replace("\"", "") + " || " + value.replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
							}
							else {
								values.set(index, "\"" + values.get(index).replace("\"", "") + " || " + value.replace("\"", "") + "\"");
								if (entry.getKey().equals("dc:title")) {
									storyTitle = "\"" + storyTitle.replace("\"", "") + " || " + value + "\"";
								}
							}
						}
					}
					else {
						if (!keys.contains(entry.getKey())) {
							keys.add(entry.getKey());
							if (entry.getKey().equals("dc:description")) {
								values.add("\"" + entry.getValue().toString().replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
							}
							else {
								values.add("\"" + entry.getValue().toString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
								if (entry.getKey().equals("dc:title")) {
									storyTitle = "\"" + entry.getValue().toString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"";
								}
							}
						}
						else {
							int index = keys.indexOf(entry.getKey());
							if (entry.getKey().equals("dc:description")) {
								values.set(index, "\"" + entry.getValue().toString().replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
							}
							else {
								values.set(index, "\"" + values.get(index).replace("\"", "") + " || " + entry.getValue().toString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
								if (entry.getKey().equals("dc:title")) {
									storyTitle = "\"" + entry.getValue().toString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"";
								}
							}
						}
					}
				}
				else {
					if (entry.getKey().equals("iiif_url")) {
						manifestUrl = entry.getValue().getAsString();
					}
					if (entry.getKey().equals("@type") && !entry.getValue().isJsonArray() && entry.getValue().getAsString().equals("edm:Place") && placeAdded == false) {
						if (dataArray.get(i).getAsJsonObject().keySet().contains("geo:lat")
								&& dataArray.get(i).getAsJsonObject().keySet().contains("geo:long")) {
							if (!keys.contains("PlaceLatitude")) {
								keys.add("PlaceLatitude");
								keys.add("PlaceLongitude");
								values.add(dataArray.get(i).getAsJsonObject().get("geo:lat").toString());
								values.add(dataArray.get(i).getAsJsonObject().get("geo:long").toString());
							}
						}
						else if (dataArray.get(i).getAsJsonObject().keySet().contains("wgs84_pos:lat")
								&& dataArray.get(i).getAsJsonObject().keySet().contains("wgs84_pos:long")) {
							if (!keys.contains("PlaceLatitude")) {
								keys.add("PlaceLatitude");
								keys.add("PlaceLongitude");
								values.add(dataArray.get(i).getAsJsonObject().get("wgs84_pos:lat").toString());
								values.add(dataArray.get(i).getAsJsonObject().get("wgs84_pos:long").toString());
							}
						}
						if (dataArray.get(i).getAsJsonObject().keySet().contains("skos:prefLabel")) {
							JsonArray placeName = new JsonArray();
							if (dataArray.get(i).getAsJsonObject().get("skos:prefLabel").isJsonArray()) {
								if (!keys.contains("PlaceName")) {
									placeName = dataArray.get(i).getAsJsonObject().get("skos:prefLabel").getAsJsonArray();
									for (int j = 0; j < placeName.size(); j++) {
										if (placeName.get(j) instanceof JsonObject && placeName.get(j).getAsJsonObject().get("@language").toString() == "en") {
											keys.add("PlaceName");
											values.add(placeName.get(j).getAsJsonObject().get("@value").toString());
											break;
										}
									}
								}
							}
							else if (dataArray.get(i).getAsJsonObject().get("skos:prefLabel").isJsonObject()) {
								if (!keys.contains("PlaceName")) {
									keys.add("PlaceName");
									values.add(dataArray.get(i).getAsJsonObject().get("skos:prefLabel").getAsJsonObject().get("@value").toString());
								}
							}
							else {
								if (!keys.contains("PlaceName")) {
									keys.add("PlaceName");
									values.add(dataArray.get(i).getAsJsonObject().get("skos:prefLabel").toString());
								}
							}

						}
					}
					else if (entry.getKey().equals("@type") && !entry.getValue().isJsonArray() && entry.getValue().getAsString().equals("edm:Agent")) {
						if (!keys.contains("edm:agent")) {
							if (dataArray.get(i).getAsJsonObject().keySet().contains("skos:prefLabel")) {
								keys.add("edm:agent");
								values.add("\"" + dataArray.get(i).getAsJsonObject().get("skos:prefLabel").getAsString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "")
										+ " | " + dataArray.get(i).getAsJsonObject().get("@id").getAsString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
							}
						}
						else {
							if (dataArray.get(i).getAsJsonObject().keySet().contains("skos:prefLabel")) {
								int index = keys.indexOf("edm:agent");
								values.set(index, "\"" + values.get(index).replace("\"", "") + " || " + dataArray.get(i).getAsJsonObject().get("skos:prefLabel").getAsString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "")
										+ " | " + dataArray.get(i).getAsJsonObject().get("@id").getAsString().replace(",", " | ").replace("\\\"", "").replaceAll("[\"{}\\[\\]]", "") + "\"");
							}
						}
					}
					else {
						if (entry.getKey().equals("@type") &&
								(!entry.getValue().isJsonArray() && entry.getValue().getAsString().equals("edm:WebResource"))
								|| (entry.getValue().isJsonArray() && entry.getValue().getAsJsonArray().toString().contains("edm:WebResource"))) {
							if (dataArray.get(i).getAsJsonObject().keySet().contains("dcterms:isReferencedBy")){
								if (dataArray.get(i).getAsJsonObject().get("dcterms:isReferencedBy").isJsonObject()
										&& dataArray.get(i).getAsJsonObject().get("dcterms:isReferencedBy").getAsJsonObject().get("@id").getAsString().endsWith("manifest.json")) {
									if (manifestUrl == "") {
										manifestUrl = dataArray.get(i).getAsJsonObject().get("dcterms:isReferencedBy").getAsJsonObject().get("@id").getAsString();
									}
									//manifestUrl = dataArray.get(i).getAsJsonObject().get("dcterms:isReferencedBy").getAsJsonObject().get("@id").getAsString();
								}
							}
							// BEWARE:
							// isReferencedBy is optional and amount of images in the imageLinks array
							// may be difffent then in the maniftest.json aoth arays not matching
							// see on item import below
							// else {
								if (dataArray.get(i).getAsJsonObject().has("ebucore:hasMimeType") && dataArray.get(i).getAsJsonObject().get("ebucore:hasMimeType").toString().contains("application/pdf")) {
									pdfImage = dataArray.get(i).getAsJsonObject().get("@id").toString();
								}
								else {
									imageLinks.add(dataArray.get(i).getAsJsonObject().get("@id").toString());
								}
							// }
						}
						else if (entry.getKey().equals("@type") && !entry.getValue().isJsonArray() && entry.getValue().getAsString().equals("edm:ProvidedCHO")) {
							if (dataArray.get(i).getAsJsonObject().keySet().contains("@id")){
								//if (dataArray.get(i).getAsJsonObject().get("@id").getAsString().startsWith("http://data.europeana")) {
									externalRecordId = dataArray.get(i).getAsJsonObject().get("@id").getAsString();
									String[] arr = dataArray.get(i).getAsJsonObject().get("@id").getAsString().split("/");
									recordId = "/" + arr[arr.length - 2] + "/" + arr[arr.length - 1];
								//}
							}
						}
					}
				}
			}
		}

		keys.add("PlaceUserGenerated");
		values.add("1");
		keys.add("ProjectId");
		values.add("" + projectId);

		String checkQuery = "SELECT * FROM Story WHERE RecordId = '" + recordId + "'";
		Integer checkQueryResult = executeStoryQuery(checkQuery, "Select");

		String query = "";
		String resource = "";

		// Check if record doesn't exist yet
		if (checkQueryResult == 0) {
			query += "INSERT INTO Story (";

		    query += "ExternalRecordId, ";
		    query += "RecordId, ";
		    query += "ImportName, ";
		    query += "DatasetId, ";
			Iterator<String> keysIterator = keys.iterator();
		    while (keysIterator.hasNext()) {
				query += "`" + keysIterator.next() + "`";
		        if (keysIterator.hasNext()) {
		        	query += ", ";
		        }
			}
		    query += ") VALUES (";
		    query += "\"" + externalRecordId + "\", ";
		    query += "\"" + recordId + "\", ";
		    query += "\"" + queryParams.getFirst("importName") + "\", ";
		    query += queryParams.getFirst("datasetId") + ", ";
			Iterator<String> valuesIterator = values.iterator();
		    while (valuesIterator.hasNext()) {
				query += valuesIterator.next();
		        if (valuesIterator.hasNext()) {
		        	query += ", ";
		        }
			}
		    query += ")";

			resource = executeInsertQuery(query, "Import");
			if (resource == "Failed") {
				ResponseBuilder rBuild = Response.ok(query);
		        //ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
		        return rBuild.build();
			}

			String itemQuery = "";
			if (manifestUrl == "") {
				itemQuery = "";
				itemQuery += "INSERT INTO Item ("
						+ "Title, "
						+ "StoryId, "
						+ "ImageLink, "
						+ "OrderIndex, "
						+ "Manifest"
						+ ") "
						+ "VALUES ("
						+ "\"" + storyTitle.replace("\"", "") + " Item "  + "1" + "\"" +  ", "
						+ "(SELECT StoryId FROM Story WHERE `dc:title` = " + "\"" + storyTitle.replace("\"", "") + "\" ORDER BY StoryId DESC LIMIT 1), "
						+ "\"" + imageLink.replace("\"", "") + "\"" + ", "
						+ "1" + ", "
						+ "\"" + manifestUrl + "\"" + ")";
				String itemResponse = executeInsertQuery(itemQuery, "Import");

				String storyImageQuery = "UPDATE Story SET PreviewImage = " + "\"" + imageLink.replace("\"", "\\\"") + "\"" +
						"WHERE\r\n" +
						"    StoryId = \r\n" +
						"	(\r\n" +
						"		SELECT \r\n" +
						"            StoryId\r\n" +
						"        FROM\r\n" +
						"        (\r\n" +
						"			SELECT StoryId \r\n" +
						"            FROM\r\n" +
						"				Story\r\n" +
						"			WHERE `dc:title` = " + "\"" + storyTitle.replace("\"", "") + "\"" +
						"			ORDER BY StoryId DESC\r\n" +
						"			LIMIT 1\r\n" +
						"		) a\r\n" +
						"	)";
				String storyImageResponse = executeInsertQuery(storyImageQuery, "Import");

				if (itemResponse == "Failed") {
					ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			        return rBuild.build();
				}
			}
			else {

		    // get the property value and print it out

		    HttpClient httpclient = HttpClients.createDefault();

		    HttpPost httppost = new HttpPost("https://sso.apps.paas-dev.psnc.pl/auth/realms/EnrichEuropeana/protocol/openid-connect/token");

	    	List<NameValuePair> params = new ArrayList<NameValuePair>(2);
	    	params.add(new BasicNameValuePair("grant_type", "client_credentials"));
	    	params.add(new BasicNameValuePair("client_secret", PropertiesCache.getInstance().getProperty("SECRET")));
	    	params.add(new BasicNameValuePair("client_id", "tp-api-client"));
	    	httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
	    	HttpResponse response = httpclient.execute(httppost);
	    	HttpEntity entity = response.getEntity();
	    	HttpURLConnection con = null;
	    	BufferedReader in = null;

	    	if (entity != null) {
	    	  try (InputStream instream = entity.getContent()) {
	    	    StringWriter writer = new StringWriter();
	    	    IOUtils.copy(instream, writer, StandardCharsets.UTF_8);
	    	    JsonObject authData = new JsonParser().parse(writer.toString()).getAsJsonObject();

	    	    String authHeader = authData.get("access_token").toString();

	        	URL url = new URL(manifestUrl);
	        	con = (HttpURLConnection) url.openConnection();

						con.setRequestMethod("GET");
						con.setRequestProperty("Content-Type", "application/json");
						con.setRequestProperty("Authorization", "Bearer " + authHeader.replace("\"", "") );

						if (converted == false) {
		    	    String redirect = con.getHeaderField("Location");

			    		if (redirect != null){
			    			con.disconnect();
			    			con = (HttpURLConnection) new URL(redirect).openConnection();
			    		}
			    		else {
			    			con.disconnect();
			    			con = (HttpURLConnection) new URL(con.getURL().toString()).openConnection();
			    		}
						}


						in = new BufferedReader(
							  new InputStreamReader(con.getInputStream(), "UTF-8"));
						String inputLine;
						StringBuffer content = new StringBuffer();
						while ((inputLine = in.readLine()) != null) {
							content.append(inputLine);
						}
						in.close();
						con.disconnect();

	    			JsonObject manifest = new JsonParser().parse(content.toString()).getAsJsonObject();

	    			JsonArray imageArray = manifest.get("sequences").getAsJsonArray().get(0).getAsJsonObject().get("canvases").getAsJsonArray();
	    			int imageCount = imageArray.size();

	    			if (pdfImage != "") {
	    				imageLinks.clear();
		    			for (int i = 0; i < imageCount; i++) {
		    				imageLinks.add("\"" + pdfImage.replace("\"", "") + "?page=" + i + "\"");
		    			}
	    			}

	    			itemQuery = "INSERT INTO Item ("
	    				+ "Title, "
	    				+ "StoryId, "
	    				+ "ImageLink, "
	    				+ "OrderIndex, "
	    				+ "Manifest, "
	    				+ "`edm:WebResource`"
	    				+ ") VALUES ";
	    			for (int i = 0; i < imageCount; i++) {
	    				imageLink = imageArray.get(i).getAsJsonObject().get("images").getAsJsonArray().get(0).getAsJsonObject().get("resource").getAsJsonObject().toString();

	    				// if first item, add imageLink to story
	    				if (i == 0) {
	    					String storyImageQuery = "UPDATE Story SET PreviewImage = " + "\"" + imageLink.replace("\"", "\\\"") + "\"" +
	    						"WHERE\r\n" +
	    						"    StoryId = \r\n" +
	    						"	(\r\n" +
	    						"		SELECT \r\n" +
	    						"            StoryId\r\n" +
	    						"        FROM\r\n" +
	    						"        (\r\n" +
	    						"			SELECT StoryId \r\n" +
	    						"            FROM\r\n" +
	    						"				Story\r\n" +
	    						"			WHERE `dc:title` = " + "\"" + storyTitle.replace("\"", "") + "\"" +
	    						"			ORDER BY StoryId DESC\r\n" +
	    						"			LIMIT 1\r\n" +
	    						"		) a\r\n" +
	    						"	)";
	    					String storyImageResponse = executeInsertQuery(storyImageQuery, "Import");
	    				}

	    				if (i == 0) {
	    					itemQuery += "("
	    						+ "\"" + storyTitle.replace("\"", "") + " Item "  + (i + 1) + "\"" +  ", "
	    						+ "(SELECT StoryId FROM Story WHERE `dc:title` = " + "\"" + storyTitle.replace("\"", "") + "\" ORDER BY StoryId DESC LIMIT 1), "
	    						+ "\"" + imageLink.replace("\"", "\\\"") + "\"" + ", "
	    						+ (i + 1) + ", "
	    						+ "\"" + manifestUrl + "\"" + ", "
	    						+ imageLinks.get(i) + ")";
	    				}
	    				else {
	    					itemQuery += ", ("
	    						+ "\"" + storyTitle.replace("\"", "") + " Item "  + (i + 1) + "\"" +  ", "
	    	    			+ "(SELECT StoryId FROM Story WHERE `dc:title` = " + "\"" + storyTitle.replace("\"", "") + "\" ORDER BY StoryId DESC LIMIT 1), "
	    						+ "\"" + imageLink.replace("\"", "\\\"") + "\"" + ", "
	    						+ (i + 1) + ", "
	    	    			+ "\"" + manifestUrl + "\"" + ", "
	    	    			+ imageLinks.get(i) + ")";
	    				}
	    			}
	    			String itemResponse = executeInsertQuery(itemQuery, "Import");


	    			if (itemResponse == "Failed") {
	    				ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
	    				return rBuild.build();
	    			}
	    	  }  catch (Exception e) {
	    			LogFactory.getLog(ProjectResponse.class).error("Error: ", e);
	    	  } finally {
        		try {
        			if (in != null) {
								in.close();
        			}
        			if (con != null) {
								con.disconnect();
        			}
        		} catch (Exception e) {
        		} finally {
        			in = null;
        			con = null;
        		}
	    		}
	    	}
			}
		}
		else {
			query += "Update Story SET ";

		    query += "ExternalRecordId = " + "\"" + recordId + "\", ";
		    query += "RecordId = " + "\"" + recordId + "\", ";
		    query += "ImportName = " + "\"" + queryParams.getFirst("importName") + "\", ";
		    query += "DatasetId = " + queryParams.getFirst("datasetId") + ", ";
			Iterator<String> keysIterator = keys.iterator();
			Iterator<String> valuesIterator = values.iterator();
		    while (keysIterator.hasNext()) {
				query += "`" + keysIterator.next() + "` = " + valuesIterator.next();
		        if (keysIterator.hasNext()) {
		        	query += ", ";
		        }
			}
		    query += " WHERE StoryId = " + checkQueryResult;
			resource = executeInsertQuery(query, "Import");
			if (resource == "Failed") {
				ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
		        return rBuild.build();
			}
		}

		if (recordId.contains("/")) {
			String[] recordIdSplit = recordId.split("/");
			recordId = recordIdSplit[recordIdSplit.length - 2]	+ "_" +recordIdSplit[recordIdSplit.length - 1];
		}
		File file = new File(PropertiesCache.getInstance().getProperty("HOME") + "/imports/" + queryParams.getFirst("importName") + "/" + recordId + ".txt");
		file.getParentFile().mkdirs();
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.write(body);
	    fileWriter.close();

	    HttpURLConnection con = null;
	    BufferedReader in = null;
	    try {
		    URL storySolr = new URL(PropertiesCache.getInstance().getProperty("SOLR") + "/solr/Stories/dataimport?command=full-import&clean=true");
		    con = (HttpURLConnection) storySolr.openConnection();
		    con.setRequestMethod("GET");
		  	con.setConnectTimeout(Integer.parseInt(PropertiesCache.getInstance().getProperty("TIMEOUT")));
		    in = new BufferedReader(
		    new InputStreamReader(con.getInputStream()));
		    String inputLine;
		    StringBuffer content = new StringBuffer();
		    while ((inputLine = in.readLine()) != null) {
		        content.append(inputLine);
		    }
		    in.close();
		    con.disconnect();

		    URL itemSolr = new URL(PropertiesCache.getInstance().getProperty("SOLR") + "/solr/Items/dataimport?command=full-import&clean=true");
		    con = (HttpURLConnection) itemSolr.openConnection();
		    con.setRequestMethod("GET");
		  	con.setConnectTimeout(Integer.parseInt(PropertiesCache.getInstance().getProperty("TIMEOUT")));
		    in = new BufferedReader(
		    new InputStreamReader(con.getInputStream()));
		    content = new StringBuffer();
		    while ((inputLine = in.readLine()) != null) {
		        content.append(inputLine);
		    }
		    in.close();
		    con.disconnect();
	    }  catch (Exception e) {
	    	LogFactory.getLog(ProjectResponse.class).error("Error: ", e);
      } finally {
        try {
        	if (in != null) {
						in.close();
        	}
        	if (con != null) {
						con.disconnect();
        	}
        } catch (Exception e) {
        } finally {
        	in = null;
        	con = null;
        }
	   	}

		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


