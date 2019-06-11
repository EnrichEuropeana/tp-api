package responses;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.Dataset;
import objects.Project;
import objects.Story;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

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
	

	public String executeStoryInsertQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?allowMultiQueries=true&serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<Story> storyList = new ArrayList<Story>();
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
	@Produces("application/json;charset=utf-8")
	@POST
	public Response insertStory(@PathParam("project_id") int projectId, @Context UriInfo uriInfo, String body) throws SQLException {
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

		for (int i = 0; i < keyCount; i++) {
			for(Map.Entry<String, JsonElement> entry : dataArray.get(i).getAsJsonObject().entrySet()) {
				if (fields.contains(entry.getKey())) {
					if (!entry.getValue().isJsonObject()) {
						if (!keys.contains(entry.getKey())) {
							keys.add(entry.getKey());
							values.add("\"" + entry.getValue().toString().replace(",", " | ").replaceAll("[\"{}\\[\\]]", "") + "\"");
						}
					}
					else {
						if (entry.getValue().getAsJsonObject().has("@value")) {
							if (!keys.contains(entry.getKey())) {
								keys.add(entry.getKey());
								values.add(entry.getValue().getAsJsonObject().get("@value").toString());
							}
						}
						else if (entry.getValue().getAsJsonObject().has("@id")) {
							if (!keys.contains(entry.getKey())) {
								keys.add(entry.getKey());
								values.add(entry.getValue().getAsJsonObject().get("@id").toString());
							}
						}
					}
				}
				else {
					if (entry.getKey().equals("@type") && entry.getValue().getAsString().equals("edm:Place") && placeAdded == false) {
						if (dataArray.get(i).getAsJsonObject().keySet().contains("geo:lat")
								&& dataArray.get(i).getAsJsonObject().keySet().contains("geo:long")) {
							keys.add("PlaceLatitude");
							keys.add("PlaceLongitude");
							values.add(dataArray.get(i).getAsJsonObject().get("geo:lat").toString());
							values.add(dataArray.get(i).getAsJsonObject().get("geo:long").toString());
						}
						if (dataArray.get(i).getAsJsonObject().keySet().contains("skos:prefLabel")) {
							JsonArray placeName = new JsonArray();
							if (dataArray.get(i).getAsJsonObject().get("skos:prefLabel").isJsonArray()) {
								
								placeName = dataArray.get(i).getAsJsonObject().get("skos:prefLabel").getAsJsonArray();
								for (int j = 0; j < placeName.size(); j++) {
									if (placeName.get(j) instanceof JsonObject && placeName.get(j).getAsJsonObject().get("@language").toString() == "en") {
										keys.add("PlaceName");
										values.add(placeName.get(j).getAsJsonObject().get("@value").toString());
									}
								}
							}
							/*
							keys.add("PlaceName");
							
							values.add(dataArray.get(i).getAsJsonObject().get("skos:prefLabel").toString());
							*/
						}
					}
				}
			}
		}

		keys.add("PlaceUserGenerated");
		values.add("1");
		
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
		String resource = executeStoryInsertQuery(query, "Insert");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

}


