package responses;

import java.io.BufferedReader;
import java.io.IOException;
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
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import objects.Dataset;
import objects.Project;
import objects.Story;

@Path("/projects")
public class ProjectResponse {


	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<Project> projectList = new ArrayList<Project>();
		   // Register JDBC driver
		   try {
			Class.forName("com.mysql.jdbc.Driver");
		
		   // Open a connection
		   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		   // Execute SQL query
		   Statement stmt = conn.createStatement();
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   return type +" succesful";
			   }
			   else {
				   return type +" could not be executed";
			   }
		   }
		   ResultSet rs = stmt.executeQuery(query);
		   
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
		}
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(projectList);
	    return result;
	}
	
	public String executeDatasetQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<Dataset> datasetList = new ArrayList<Dataset>();
		   // Register JDBC driver
		   try {
			Class.forName("com.mysql.jdbc.Driver");
		
		   // Open a connection
		   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		   // Execute SQL query
		   Statement stmt = conn.createStatement();
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   return type +" succesful";
			   }
			   else {
				   return type +" could not be executed";
			   }
		   }
		   ResultSet rs = stmt.executeQuery(query);
		   
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
		}
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(datasetList);
	    return result;
	}
	
	//Get Entries
	@Path("")
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
		    	query += key + " = " + value;
			    if (i < valueCount) {
			    	query += " OR ";
			    }
			    i++;
		    }
		    query += ")";
		}
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	
	//Add new entry
	@Path("")
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
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	

	public String executeInsertQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?allowMultiQueries=true&serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		
	   // Register JDBC driver
	   try {
			Class.forName("com.mysql.jdbc.Driver");
		
		   // Open a connection
		   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
		   // Execute SQL query
		   Statement stmt = conn.createStatement();
		   if (type != "Select") {
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   conn.close();
				   return type +" succesful";
			   }
			   else {
				   conn.close();
				   return type +" could not be executed";
			   }
		   }
		   else {
			   conn.close();
			   return "test2";
		   }
	   } catch(SQLException se) {
	       //Handle errors for JDBC
		   se.printStackTrace();
	   } catch (ClassNotFoundException e) {
		   e.printStackTrace();
	   }
	   return "test";
	}
	
	//Get entry by id
	@Path("/{project_id}/stories")
	@POST
	public String insertStory(@PathParam("project_id") int projectId, @Context UriInfo uriInfo, String body) throws Exception {
		JsonObject data = new JsonParser().parse(body).getAsJsonObject();
		JsonArray dataArray = data.getAsJsonObject().get("@graph").getAsJsonArray();
		List<String> fields = new ArrayList<String>();
		fields.add("dc:title");
		fields.add("dc:description");
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
		boolean placeAdded = false;
	    int keyCount = dataArray.size();

		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		String manifestUrl = "";
		String storyTitle = "";
		String imageLink = "";

		for (int i = 0; i < keyCount; i++) {
			for(Map.Entry<String, JsonElement> entry : dataArray.get(i).getAsJsonObject().entrySet()) {
				if (fields.contains(entry.getKey())) {
					if (!entry.getValue().isJsonObject()) {
						if (!keys.contains(entry.getKey())) {
							keys.add(entry.getKey());
							values.add("\"" + entry.getValue().toString().replace(",", " | ").replaceAll("[\"{}\\[\\]]", "") + "\"");
							if (entry.getKey().equals("dc:title")) {
								storyTitle = "\"" + entry.getValue().toString().replace(",", " | ").replaceAll("[\"{}\\[\\]]", "") + "\"";
							}
						}
					}
					else {
						if (entry.getValue().getAsJsonObject().has("@value")) {
							if (!keys.contains(entry.getKey())) {
								keys.add(entry.getKey());
								values.add(entry.getValue().getAsJsonObject().get("@value").toString());
								if (entry.getKey().equals("dc:title")) {
									storyTitle = entry.getValue().getAsJsonObject().get("@value").toString();
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
						}
					}
				}
				else {
					if (entry.getKey().equals("@type") && entry.getValue().getAsString().equals("edm:Place") && placeAdded == false) {
						if (dataArray.get(i).getAsJsonObject().keySet().contains("geo:lat")
								&& dataArray.get(i).getAsJsonObject().keySet().contains("geo:long")) {
							if (!keys.contains("PlaceLatitude")) {
								keys.add("PlaceLatitude");
								keys.add("PlaceLongitude");
								values.add(dataArray.get(i).getAsJsonObject().get("geo:lat").toString());
								values.add(dataArray.get(i).getAsJsonObject().get("geo:long").toString());
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
										}
									}
								}
							}
						}
					}
					else {
						if (entry.getKey().equals("@type") && entry.getValue().getAsString().equals("edm:WebResource")) {
							if (dataArray.get(i).getAsJsonObject().keySet().contains("dcterms:isReferencedBy")){
								if (dataArray.get(i).getAsJsonObject().get("dcterms:isReferencedBy").isJsonObject()
										&& dataArray.get(i).getAsJsonObject().get("dcterms:isReferencedBy").getAsJsonObject().get("@id").getAsString().endsWith("manifest.json")) {
									manifestUrl = dataArray.get(i).getAsJsonObject().get("dcterms:isReferencedBy").getAsJsonObject().get("@id").getAsString();
								}
							}
							else {
								imageLink = dataArray.get(i).getAsJsonObject().get("@id").toString();
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
		
		String query = "";
		query += "INSERT INTO Story (";

		Iterator<String> keysIterator = keys.iterator();
	    while (keysIterator.hasNext()) {
			query += "`" + keysIterator.next() + "`";
	        if (keysIterator.hasNext()) {
	        	query += ", ";
	        }
		}
	    query += ") VALUES (";
		Iterator<String> valuesIterator = values.iterator();
	    while (valuesIterator.hasNext()) {
			query += valuesIterator.next();
	        if (valuesIterator.hasNext()) {
	        	query += ", ";
	        }
		}
	    query += ")";
		String resource = executeInsertQuery(query, "Insert");
		
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
					+ "(SELECT StoryId FROM Story ORDER BY StoryId DESC LIMIT 1), "
					+ "\"" + imageLink.replace("\"", "") + "\"" + ", "
					+ "1" + ", "
					+ "\"" + manifestUrl + "\"" + ")";
			String itemResponse = executeInsertQuery(itemQuery, "Insert");
		}
		else {
			URL url = new URL(manifestUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			String redirect = con.getHeaderField("Location");
		    try {
				URL url2 = new URL(con.getURL().toString());	
				if (redirect != null){
					con = (HttpURLConnection) new URL(redirect).openConnection();
					url2 = new URL(redirect);		
				}
				else {
					con = (HttpURLConnection) new URL(con.getURL().toString()).openConnection();
				}
				con.setRequestMethod("GET");
				con.setRequestProperty("Content-Type", "application/json");
				BufferedReader in = new BufferedReader(
				  new InputStreamReader(url2.openStream(), "UTF-8"));
				String inputLine;
				StringBuffer content = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
				    content.append(inputLine);
				}
				in.close();
				con.disconnect();
			
				//String json = readUrl(manifestUrl);
				JsonObject manifest = (JsonObject) new JsonParser().parse(content.toString());
				
				JsonArray imageArray = manifest.get("sequences").getAsJsonArray().get(0).getAsJsonObject().get("canvases").getAsJsonArray();
				int imageCount = imageArray.size();
				
				for (int i = 0; i <= imageCount; i++) {
					imageLink = imageArray.get(i).getAsJsonObject().get("images").getAsJsonArray().get(0).getAsJsonObject().get("resource").getAsJsonObject().get("@id").getAsString();
					
					itemQuery = "";
					itemQuery += "INSERT INTO Item ("
							+ "Title, "
							+ "StoryId, "
							+ "ImageLink, "
							+ "OrderIndex, "
							+ "Manifest"
							+ ") "
							+ "VALUES ("
							+ "\"" + storyTitle.replace("\"", "") + " Item "  + i + "\"" +  ", "
							+ "(SELECT StoryId FROM Story ORDER BY StoryId DESC LIMIT 1), "
							+ "\"" + imageLink.replace("\"", "") + "\"" + ", "
							+ i + ", "
							+ "\"" + manifestUrl + "\"" + ")";
					String itemResponse = executeInsertQuery(itemQuery, "Insert");
				}
		    } catch (IOException e) {
		        throw new RuntimeException(e);
		    }
		}
		
		
		ResponseBuilder rBuild = Response.ok(resource);
        //return rBuild.build();
		return itemQuery;
	}
}


