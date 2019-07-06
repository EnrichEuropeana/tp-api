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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import com.google.gson.*;
import com.google.gson.stream.MalformedJsonException;

@Path("/enrichments")
public class AnnotationExportResponse {

	public String executeQuery(String query, String type) throws SQLException{
		   List<AnnotationExport> annotationExports = new ArrayList<AnnotationExport>();
	       try (InputStream input = new FileInputStream("/home/enrich/tomcat/apache-tomcat-9.0.13/webapps/tp-api/WEB-INF/config.properties")) {

	            Properties prop = new Properties();

	            // load a properties file
	            prop.load(input);

	            // get the property value and print it out
	            final String DB_URL = prop.getProperty("DB_URL");
	            final String USER = prop.getProperty("USER");
	            final String PASS = prop.getProperty("PASS");
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
			  annotationExport.setEuropeanaAnnotationId(rs.getInt("EuropeanaAnnotationId"));
			  annotationExport.setAnnotationId(rs.getInt("AnnotationId"));
			  annotationExport.setText(rs.getString("Text"));
			  annotationExport.setTimestamp(rs.getTimestamp("Timestamp"));
			  annotationExport.setX_Coord(rs.getFloat("X_Coord"));
			  annotationExport.setY_Coord(rs.getFloat("Y_Coord"));
			  annotationExport.setWidth(rs.getFloat("Width"));
			  annotationExport.setHeight(rs.getFloat("Height"));
			  annotationExport.setMotivation(rs.getString("Motivation"));
			  annotationExport.setItemId(rs.getString("ItemId"));
			  annotationExport.setStoryUrl(rs.getString("StoryUrl"));
			  annotationExport.setStoryId(rs.getString("StoryId"));
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
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(annotationExports);
	    return result;
	}

	//Search using custom filters
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM (" + 
				"(SELECT  " + 
				"	 a.AnnotationId, " +
				"    a.Text, " + 
				"    a.Timestamp, " + 
				"    a.X_Coord, " + 
				"    a.Y_Coord, " + 
				"    a.Width, " + 
				"    a.Height, " + 
				"    a.EuropeanaAnnotationId, " + 
				"    m.Name AS Motivation, " + 
				"    i.ProjectItemId as ItemId, " + 
				"    s.`edm:landingPage` as StoryUrl, " + 
				"    s.ExternalRecordId as StoryId " + 
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
				"	 t.TranscriptionId, " +
				"    t.Text, " + 
				"    t.Timestamp, " + 
				"    t.EuropeanaAnnotationId, " + 
				"    0 AS X_Coord, " + 
				"    0 AS Y_Coord, " + 
				"    0 AS Width, " + 
				"    0 AS Height, " + 
				"    'transcribing' AS Motivation, " + 
				"    i.ProjectItemId, " + 
				"    s.`edm:landingPage`, " + 
				"    s.ExternalRecordId " + 
				"FROM " + 
				"    Transcription t " + 
				"        LEFT JOIN " + 
				"    Item i ON i.ItemId = t.ItemId " + 
				"        LEFT JOIN " + 
				"    Story s ON s.StoryId = i.StoryId " + 
				"WHERE " + 
				"    CurrentVersion = 1) "
				+ ") a WHERE 1";
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
