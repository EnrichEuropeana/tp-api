package responses;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import objects.Annotation;
import objects.Comment;
import objects.CompletionStatus;
import objects.Item;
import objects.Place;
import objects.Property;
import objects.Story;
import objects.Transcription;

import java.util.*;
import java.util.Date;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.*;

@Path("/storiesMinimal")
public class StoryMinimalResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<Story> storyList = new ArrayList<Story>();
	       try (InputStream input = new FileInputStream("/home/enrich/tomcat/apache-tomcat-9.0.13/webapps/tp-api/WEB-INF/config.properties")) {

	            Properties prop = new Properties();

	            // load a properties file
	            prop.load(input);

	            // get the property value and print it out
	            final String DB_URL = prop.getProperty("DB_URL");
	            final String USER = prop.getProperty("USER");
	            final String PASS = prop.getProperty("PASS");
		   // Register JDBC driver
				Class.forName("com.mysql.jdbc.Driver");
				
				   // Open a connection
				   Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
				   // Execute SQL query
				   Statement stmt = conn.createStatement();
		   try {
		   if (type != "Select") {
			   if (type == "Select count") {
				   ResultSet rs = stmt.executeQuery(query);
				   rs.next();
				   String count = rs.getString("count");
				   return count;
			   }
			   int success = stmt.executeUpdate(query);
			   if (success > 0) {
				   stmt.close();
				   conn.close();
				   return type +" succesful";
			   }
			   else {
				   stmt.close();
				   conn.close();
				   return type +" could not be executed";
			   }
		   }
		   stmt.execute("SET group_concat_max_len = 1000000;");
		   ResultSet rs = stmt.executeQuery(query);

		   // Extract data from result set
		   while(rs.next()){
			  Story story = new Story();
			  story.setStoryId(rs.getInt("StoryId")); 
			  story.setdcTitle(rs.getString("StorydcTitle"));
			  story.setdcDescription(rs.getString("StorydcDescription"));
			  story.setPreviewImage(rs.getString("StoryPreviewImage"));
			  story.setDatasetName(rs.getString("DatasetName"));
			  
			  // Iterate through CompletionStatus of the Items
			  List<CompletionStatus> CompletionStatusList = new ArrayList<CompletionStatus>();
			  if (rs.getString("CompletionStatus") != null) {
				  String[] CompletionStatus = rs.getString("CompletionStatus").split(",");
				  String[] ColorCodes = rs.getString("ColorCode").split(",");
				  String[] ColorCodeGradients = rs.getString("ColorCodeGradient").split(",");
				  String[] Amounts = rs.getString("Amount").split(",");
				  

				  for (int j = 0; j < CompletionStatus.length; j++) {
					  CompletionStatus completionStatus = new CompletionStatus();
					  completionStatus.setName(CompletionStatus[j]);
					  completionStatus.setColorCode(ColorCodes[j]);
					  completionStatus.setColorCodeGradient(ColorCodeGradients[j]);
					  completionStatus.setAmount(Integer.parseInt(Amounts[j]));
					  
					  CompletionStatusList.add(completionStatus);
				  }
			  }
			  story.setCompletionStatus(CompletionStatusList);
			  storyList.add(story);
		   }
		
		   // Clean-up environment
		   rs.close();
		   stmt.close();
		   conn.close();
		   } catch(SQLException se) {
		       //Handle errors for JDBC
			   se.printStackTrace();
		   } finally {
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			    try { conn.close(); } catch (Exception e) { /* ignored */ }
		   }
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    Gson gsonBuilder = new GsonBuilder().create();
	    String result = gsonBuilder.toJson(storyList);
	    return result;
	}


	//GET entries
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo, @QueryParam("pa") int page) throws SQLException {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		String query = "SELECT \r\n" + 
				"	s.StoryId,\r\n" + 
				"	GROUP_CONCAT(IFNULL(CompletionStatus, 'NULL')) AS CompletionStatus,\r\n" + 
				"	GROUP_CONCAT(IFNULL(ColorCode, 'NULL')) AS ColorCode,\r\n" + 
				"	GROUP_CONCAT(IFNULL(ColorCodeGradient, 'NULL')) AS ColorCodeGradient,\r\n" + 
				"	GROUP_CONCAT(IFNULL(Count, 'NULL')) AS Amount,\r\n" + 
				"    s.StorydcTitle as StorydcTitle,\r\n" + 
				"    s.StoryPreviewImage as StoryPreviewImage,\r\n" + 
				"    s.StorydcDescription as StorydcDescription,\r\n" + 
				"    s.DatasetName as DatasetName " + 
				"FROM (\r\n" + 
				"	SELECT \r\n" + 
				"		StoryId, \r\n" + 
				"        MIN(ItemId) as ItemId,\r\n" + 
				"		c.Name as CompletionStatus,\r\n" + 
				"		c.ColorCode as ColorCode,\r\n" + 
				"		c.ColorCodeGradient as ColorCodeGradient,\r\n" + 
				"		Count(*) as Count\r\n" + 
				"	FROM Item i\r\n" + 
				"	JOIN CompletionStatus c ON c.CompletionStatusId = i.CompletionStatusId \r\n";
		if (queryParams.containsKey("storyId")) {
			String[] values = queryParams.getFirst("storyId").split(",");
			query += " WHERE StoryId IN (";
		    int valueCount = values.length;
		    int i = 1;
		    for(String value : values) {
		    	query += value;
			    if (i < valueCount) {
			    	query += ", ";
			    }
			    i++;
		    }
		    query += ") ";
		}
	    query += "	GROUP BY StoryId, c.Name, c.ColorCode, c.ColorCodeGradient\r\n" + 
				") a\r\n" + 
				"JOIN \r\n" + 
				"	(\r\n" + 
				"	SELECT \r\n" + 
				"		i.ItemId,\r\n" + 
				"       i.ImageLink\r\n" + 
				"	FROM Item i\r\n";
		if (queryParams.containsKey("storyId") && queryParams.getFirst("storyId") != "") {
			String[] values = queryParams.getFirst("storyId").split(",");
			query += " WHERE StoryId IN (";
		    int valueCount = values.length;
		    int i = 1;
		    for(String value : values) {
		    	query += value;
			    if (i < valueCount) {
			    	query += ", ";
			    }
			    i++;
		    }
		    query += ") ";
		}
	    query += "    ) b ON a.ItemId = b.ItemId\r\n" + 
				"JOIN \r\n" + 
				"	(\r\n" + 
				"    SELECT \r\n" + 
				"		StoryId as StoryId,\r\n" + 
				"		`dc:title` as StorydcTitle,\r\n" + 
				"		PreviewImage as StoryPreviewImage,\r\n" + 
				"		`dc:description` as StorydcDescription,\r\n" + 
				"        d.Name as DatasetName" + 
				"	FROM\r\n" + 
				"		Story s\r\n\r\n" + 
				"		LEFT JOIN Dataset d ON d.DatasetId = s.DatasetId " + 
				"	) s ON s.StoryId = a.StoryId\r\n" + 
				"GROUP BY StoryId " +
				" ORDER BY StoryId DESC";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
		//ResponseBuilder rBuild = Response.ok(query);
        return rBuild.build();
	}

	//Add new entry
	@Path("")
	@POST
	public Response add(String body) throws SQLException {	
	    /*
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Story story = gson.fromJson(body, Story.class);
	    //Check if all mandatory fields are included
	    if (item.Name != null &~&&~& item.Public != null) {
			String query = "INSERT INTO Item (Name, Start, End, Public) "
							+ "VALUES ('" + item.Name + "'"
								+ ", '" + item.Start + "'"
								+ ", '" + item.End + "'"
								+ ", " + item.Public + ")";
			String resource = executeQuery(query, "Insert");
			return resource;
	    } else {
	    	return "Fields missing";
	    }
	    */
		//String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok("");
        return rBuild.build();
	}


/*
	//Edit entry by id
	@Path("/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if NOT NULL field is attNULLted to be changed to NULL
	    if ((changes.get("Name") == null || !changes.get("Name").isJsonNull())
	    		&~&&~& (changes.get("Public") == null || !changes.get("Public").isJsonNull())) {
		    String query = "UPDATE Item SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE ItemId = " + id;
			String resource = executeQuery(query, "Update");
			return resource;
	    } else {
	    	return "Prohibited change to null";
	    }
	}
*/

/*
	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String query =  "DELETE FROM Story " +
						"WHERE i.StoryId = " + id;
		String resource = executeQuery(query, "Delete");
		return resource;
	}
	*/
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@Context UriInfo uriInfo, @PathParam("id") int id, String body) throws SQLException {
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		String query = "SELECT \r\n" + 
				"	s.StoryId,\r\n" + 
				"	GROUP_CONCAT(IFNULL(CompletionStatus, 'NULL')) AS CompletionStatus,\r\n" + 
				"	GROUP_CONCAT(IFNULL(ColorCode, 'NULL')) AS ColorCode,\r\n" + 
				"	GROUP_CONCAT(IFNULL(ColorCodeGradient, 'NULL')) AS ColorCodeGradient,\r\n" + 
				"	GROUP_CONCAT(IFNULL(Count, 'NULL')) AS Amount,\r\n" + 
				"    s.StorydcTitle as StorydcTitle,\r\n" + 
				"    s.StoryPreviewImage as StoryPreviewImage,\r\n" + 
				"    s.StorydcDescription as StorydcDescription,\r\n" + 
				"    s.DatasetName as DatasetName\r\n" + 
				"FROM (\r\n" + 
				"	SELECT \r\n" + 
				"		StoryId, \r\n" + 
				"        MIN(ItemId) as ItemId,\r\n" + 
				"		c.Name as CompletionStatus,\r\n" + 
				"		c.ColorCode as ColorCode,\r\n" + 
				"		c.ColorCodeGradient as ColorCodeGradient,\r\n" + 
				"		Count(*) as Count\r\n" + 
				"	FROM Item i\r\n" + 
				"	JOIN CompletionStatus c ON c.CompletionStatusId = i.CompletionStatusId \r\n";
		if (queryParams.containsKey("storyId")) {
			String[] values = queryParams.getFirst("storyId").split(",");
			query += " WHERE StoryId IN (";
		    int valueCount = values.length;
		    int i = 1;
		    for(String value : values) {
		    	query += value;
			    if (i < valueCount) {
			    	query += ", ";
			    }
			    i++;
		    }
		    query += ") ";
		}
	    query += "	GROUP BY StoryId, c.Name, c.ColorCode, c.ColorCodeGradient\r\n" + 
				") a\r\n" + 
				"JOIN \r\n" + 
				"	(\r\n" + 
				"	SELECT \r\n" + 
				"		ItemId,\r\n" + 
				"        ImageLink\r\n" + 
				"	FROM Item i\r\n";
		if (queryParams.containsKey("storyId") && queryParams.getFirst("storyId") != "") {
			String[] values = queryParams.getFirst("storyId").split(",");
			query += " WHERE StoryId IN (";
		    int valueCount = values.length;
		    int i = 1;
		    for(String value : values) {
		    	query += value;
			    if (i < valueCount) {
			    	query += ", ";
			    }
			    i++;
		    }
		    query += ") ";
		}
	    query += "    ) b ON a.ItemId = b.ItemId\r\n" + 
				"JOIN \r\n" + 
				"	(\r\n" + 
				"    SELECT \r\n" + 
				"		StoryId as StoryId,\r\n" + 
				"		`dc:title` as StorydcTitle,\r\n" + 
				"		PreviewImage as StoryPreviewImage,\r\n" + 
				"		`dc:description` as StorydcDescription,\r\n" + 
				"        d.Name as DatasetName\r\n" + 
				"	FROM\r\n" + 
				"		Story s\r\n\r\n" + 
				"		LEFT JOIN Dataset d ON d.DatasetId = s.DatasetId " + 
				"	) s ON s.StoryId = a.StoryId\r\n" + 
				"WHERE s.StoryId = " + id;
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
		//ResponseBuilder rBuild = Response.ok(query);
        return rBuild.build();
	}

	//Return Story count
	@Path("/count")
	@GET
	public String count() throws SQLException {
		String query =  "SELECT count(DISTINCT(StoryId)) as count FROM Item";
		String resource = executeQuery(query, "Select count");
		return resource;
	}

	public static boolean isNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
}