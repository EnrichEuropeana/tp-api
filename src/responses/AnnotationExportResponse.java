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

import objects.AnnotationExport;
import objects.Person;

import java.util.*;
import java.sql.*;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;

@Path("/AnnotationExport")
public class AnnotationExportResponse {

	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<AnnotationExport> annotationExports = new ArrayList<AnnotationExport>();
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
			  AnnotationExport annotationExport = new AnnotationExport();
			  annotationExport.setText(rs.getString("Text"));
			  annotationExport.setTimestamp(rs.getTimestamp("Timestamp"));
			  annotationExport.setX_Coord(rs.getFloat("X_Coord"));
			  annotationExport.setY_Coord(rs.getFloat("Y_Coord"));
			  annotationExport.setWidth(rs.getFloat("Width"));
			  annotationExport.setHeight(rs.getFloat("Height"));
			  annotationExport.setMotivation(rs.getString("Motivation"));
			  annotationExport.setItemId(rs.getString("ProjectItemId"));
			  annotationExport.setStoryUrl(rs.getString("ProjectStoryUrl"));
			  annotationExport.setStoryId(rs.getString("ProjectStoryId"));
			  annotationExports.add(annotationExport);
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
	    String result = gsonBuilder.toJson(annotationExports);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getAll() throws SQLException {
		String query = "(SELECT  " + 
				"    a.Text, " + 
				"    a.Timestamp, " + 
				"    a.X_Coord, " + 
				"    a.Y_Coord, " + 
				"    a.Width, " + 
				"    a.Height, " + 
				"    m.Name AS Motivation, " + 
				"    i.ProjectItemId, " + 
				"    s.ProjectStoryUrl, " + 
				"    s.ProjectStoryId " + 
				"FROM " + 
				"    Annotation a " + 
				"        LEFT JOIN " + 
				"    AnnotationType at ON a.AnnotationTypeId = at.AnnotationTypeId " + 
				"        LEFT JOIN " + 
				"    Motivation m ON at.MotivationId = m.MotivationId " + 
				"        LEFT JOIN " + 
				"    Item i ON i.ItemId = a.ItemId " + 
				"        LEFT JOIN " + 
				"    Story s ON s.StoryId = i.StoryId)  " + 
				"UNION ( " + 
				"	SELECT  " + 
				"    t.Text, " + 
				"    t.Timestamp, " + 
				"    0 AS X_Coord, " + 
				"    0 AS Y_Coord, " + 
				"    0 AS Width, " + 
				"    0 AS Height, " + 
				"    'transcribing' AS Motivation, " + 
				"    i.ProjectItemId, " + 
				"    s.ProjectStoryUrl, " + 
				"    s.ProjectStoryId " + 
				"FROM " + 
				"    Transcription t " + 
				"        LEFT JOIN " + 
				"    Item i ON i.ItemId = t.ItemId " + 
				"        LEFT JOIN " + 
				"    Story s ON s.StoryId = i.StoryId " + 
				"WHERE " + 
				"    CurrentVersion = 1)";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}


	//Add new entry
	@Path("/add")
	@POST
	public Response add(String body) throws SQLException {	
	    /*
		GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Person person = gson.fromJson(body, Person.class);
	    
	    //Check if all mandatory fields are included
	    if (person.Name != null) {
			String query = "INSERT INTO Person (Name, BirthPlace, BirthDate, DeathPlace, DeathDate, Link) "
							+ "VALUES ('" + person.Name + "'"
							+ ", '" + person.BirthPlace + "'"
							+ ", '" + person.BirthDate + "'"
							+ ", '" + person.DeathPlace + "'"
							+ ", '" + person.DeathDate + "'"
							+ ", '" + person.Link + "')";
			String resource = executeQuery(query, "Insert");
			return resource;
	    } else {
	    	return "Fields missing";
	    }
	    */
		ResponseBuilder rBuild = Response.created(null);
        return rBuild.build();
	}

	
	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if field is allowed to be changed
	    if (changes.get("AnnotationId") != null || changes.get("Timestamp") != null ) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if ((changes.get("Text") == null || !changes.get("Text").isJsonNull())
	    		&& (changes.get("UserId") == null || !changes.get("UserId").isJsonNull())
	    		&& (changes.get("ItemId") == null || !changes.get("ItemId").isJsonNull())
				&& (changes.get("X_Coord") == null || !changes.get("X_Coord").isJsonNull())
				&& (changes.get("Y_Coord") == null || !changes.get("Y_Coord").isJsonNull())
				&& (changes.get("Width") == null || !changes.get("Width").isJsonNull())
				&& (changes.get("Height") == null || !changes.get("Height").isJsonNull())) {
		    String query = "UPDATE Annotation SET ";
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE AnnotationId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited change to null";
	    }
	}
	
	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("DELETE FROM Annotation WHERE AnnotationId = " + id, "Delete");
		return resource;
	}
	
	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM Annotation WHERE AnnotationId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	
	//Search using custom filters
	@Path("/search")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM (" +
				"(SELECT  " + 
				"    a.Text, " + 
				"    a.Timestamp, " + 
				"    a.X_Coord, " + 
				"    a.Y_Coord, " + 
				"    a.Width, " + 
				"    a.Height, " + 
				"    m.Name AS Motivation, " + 
				"    i.ProjectItemId, " + 
				"    s.ProjectStoryUrl, " + 
				"    s.ProjectStoryId " + 
				"FROM " + 
				"    Annotation a " + 
				"        LEFT JOIN " + 
				"    AnnotationType at ON a.AnnotationTypeId = at.AnnotationTypeId " + 
				"        LEFT JOIN " + 
				"    Motivation m ON at.MotivationId = m.MotivationId " + 
				"        LEFT JOIN " + 
				"    Item i ON i.ItemId = a.ItemId " + 
				"        LEFT JOIN " + 
				"    Story s ON s.StoryId = i.StoryId)  " + 
				"UNION ( " + 
				"	SELECT  " + 
				"    t.Text, " + 
				"    t.Timestamp, " + 
				"    0 AS X_Coord, " + 
				"    0 AS Y_Coord, " + 
				"    0 AS Width, " + 
				"    0 AS Height, " + 
				"    'transcribing' AS Motivation, " + 
				"    i.ProjectItemId, " + 
				"    s.ProjectStoryUrl, " + 
				"    s.ProjectStoryId " + 
				"FROM " + 
				"    Transcription t " + 
				"        LEFT JOIN " + 
				"    Item i ON i.ItemId = t.ItemId " + 
				"        LEFT JOIN " + 
				"    Story s ON s.StoryId = i.StoryId " + 
				"WHERE " + 
				"    CurrentVersion = 1) " + 
				") a WHERE 1";
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
}
