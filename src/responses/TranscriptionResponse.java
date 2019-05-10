package responses;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.Transcription;

import java.util.*;
import java.sql.*;
import java.text.ParseException;

import com.google.gson.*;

@Path("/Transcription")
public class TranscriptionResponse {


	public String executeQuery(String query, String type) throws SQLException, ParseException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<Transcription> transcriptionList = new ArrayList<Transcription>();
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
			  Transcription transcription = new Transcription();
			  transcription.setTranscriptionId(rs.getInt("TranscriptionId"));
			  transcription.setText(rs.getString("Text"));
			  transcription.setTimestamp(rs.getString("Timestamp"));
			  transcription.setUserId(rs.getInt("UserId"));
			  transcription.setWP_UserId(rs.getInt("WP_UserId"));
			  transcription.setItemId(rs.getInt("ItemId"));
			  transcription.setCurrentVersion(rs.getString("CurrentVersion"));
			  transcriptionList.add(transcription);
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
	    String result = gsonBuilder.toJson(transcriptionList);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getAll() throws SQLException, ParseException {
		String query = "SELECT * FROM ("
						+ "SELECT "
						+ "t.TranscriptionId, "
						+ "t.Text, "
						+ "t.Timestamp, "
						+ "t.UserId, "
						+ "t.ItemId, "
						+ "t.CurrentVersion, "
						+ "u.WP_UserId "
						+ "FROM Transcription t "
						+ "JOIN User u ON t.UserId = u.UserId) a "
						+ "WHERE 1";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	

	//Add new entry
	@Path("/add")
	@POST
	public String add(String body) throws SQLException, ParseException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Transcription transcription = gson.fromJson(body, Transcription.class);
	    
	    //Check if all mandatory fields are included
	    if (transcription.Text != null && transcription.UserId != null 
	    		&& transcription.ItemId != null && transcription.CurrentVersion != null) {
			String query = "INSERT INTO Transcription (Text, UserId, ItemId, CurrentVersion) "
							+ "VALUES ('" + transcription.Text + "'"
								+ ", " + transcription.UserId
								+ ", " + transcription.ItemId
								+ ", " + transcription.CurrentVersion + ")";
			String resource = executeQuery(query, "Insert");
			return resource;
	    } else {
	    	return "Fields missing";
	    }
	}
	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException, ParseException {
		String resource = executeQuery("DELETE FROM Transcription WHERE TranscriptionId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException, ParseException {
		String resource = executeQuery("SELECT * FROM Transcription WHERE TranscriptionId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Search using custom filters
	@Path("/search")
	@Produces("application/json;charset=utf-8")
	@POST
	public Response search(@Context UriInfo uriInfo, String body) throws SQLException, ParseException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonTree = jsonParser.parse(body);
		JsonObject bodyObject = jsonTree.getAsJsonObject();
		
		String query = "SELECT * FROM ("
				+ "SELECT "
				+ "t.TranscriptionId, "
				+ "t.Text, "
				+ "t.Timestamp, "
				+ "t.UserId, "
				+ "t.ItemId, "
				+ "t.CurrentVersion, "
				+ "u.WP_UserId "
				+ "FROM Transcription t "
				+ "JOIN User u ON t.UserId = u.UserId) a "
				+ "WHERE 1";

		for(String key : bodyObject.keySet()){
			String[] values = bodyObject.get(key).toString().split(",");
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