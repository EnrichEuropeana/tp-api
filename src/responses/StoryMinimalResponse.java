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

import objects.Annotation;
import objects.Comment;
import objects.Item;
import objects.Place;
import objects.Property;
import objects.Story;
import objects.Transcription;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.*;

@Path("/storiesMinimal")
public class StoryMinimalResponse {


	public String executeQuery(String query, String type) throws SQLException{
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
		   stmt.execute("SET group_concat_max_len = 1000000;");
		   ResultSet rs = stmt.executeQuery(query);

		   // Extract data from result set
		   while(rs.next()){
			  Story story = new Story();
			  story.setStoryId(rs.getInt("StoryId")); 
			  story.setdcTitle(rs.getString("StorydcTitle"));
			   
			  // Iterate through Items of the Story
			  List<Item> ItemList = new ArrayList<Item>();
			  if (rs.getString("ItemId") != null) {
				  String[] ItemIds = rs.getString("ItemId").split("§~§");
				  String[] ItemCompletionStatusNames = rs.getString("CompletionStatusName").split("§~§");
				  String[] ItemCompletionStatusIds = rs.getString("CompletionStatusId").split("§~§");
				  

				  for (int j = 0; j < ItemIds.length; j++) {
					  Item item = new Item();
					  item.setItemId(Integer.parseInt(ItemIds[j]));
					  item.setCompletionStatusName(ItemCompletionStatusNames[j]);
					  item.setCompletionStatusId(Integer.parseInt(ItemCompletionStatusIds[j]));
					  
					  ItemList.add(item);
				  }
			  }
			  story.setItems(ItemList);
			  storyList.add(story);
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
	    String result = gsonBuilder.toJson(storyList);
	    return result;
	}


	//GET entries
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo, String body) throws SQLException {
		String query = "SELECT * FROM " +
				"(SELECT s.StoryId as StoryId" + 
				", s.dcTitle as StorydcTitle" +
				", group_concat(i.ItemId SEPARATOR '§~§') as ItemId" +
				", group_concat(i.CompletionStatusName SEPARATOR '§~§') as CompletionStatusName" +
				", group_concat(i.CompletionStatusId SEPARATOR '§~§') as CompletionStatusId " +
				"FROM " +
					"(" +
					"SELECT * " +
				    "FROM Item i " +
				    "LEFT JOIN ( " +
						"SELECT i.ItemId as CompletionStatusItemId" +
						", c.Name as CompletionStatusName " + 
				        "FROM CompletionStatus c " +
				        "JOIN Item i " +
				        "ON i.CompletionStatusId = c.CompletionStatusId " +
				        ") c  " +
				        "ON i.ItemId = c.CompletionStatusItemId " +
					") i " +
				"LEFT JOIN " + 
				"(" +
					"SELECT * " +
					"FROM Story " + 
				") s " +
				"ON i.StoryId = s.StoryId " +
				"GROUP BY s.StoryId) s " +
				"WHERE 1";
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
	public Response getEntry(@PathParam("id") int id, String body) throws SQLException {
		String query = "SELECT s.StoryId as StoryId" + 
						", s.dcTitle as StorydcTitle" +
						", s.dcDescription as StorydcDescription" +
						", s.ProjectStoryUrl as StoryProjectStoryUrl" +
						", s.DateStartDisplay as StoryDateStartDisplay" +
						", s.DateEndDisplay as StoryDateEndDisplay" +
						", s.PlaceName as StoryPlaceName" +
						", s.PlaceLatitude as StoryPlaceLatitude" +
						", s.PlaceLongitute as StoryPlaceLongitute" +
						", s.PlaceUserGenerated as StoryPlaceUserGenerated" +
						", s.Contributor as StoryContributor" +
						", s.Rights as StoryRights" +
						", s.Summary as StorySummary" +
						", s.ParentStory as StoryParentStory" +
						", s.Manifest as StoryManifest" +
						", s.SearchText as StorySearchText" +
						", s.DateStart as StoryDateStart" +
						", s.DateEnd as StoryDateEnd" +
						", s.OrderIndex as StoryOrderIndex" +
						", group_concat(i.ItemId SEPARATOR '§~§') as ItemId" +
						", group_concat(i.Title SEPARATOR '§~§') as Title" +
						", group_concat(i.CompletionStatusName SEPARATOR '§~§') as CompletionStatusName" +
						", group_concat(i.CompletionStatusId SEPARATOR '§~§') as CompletionStatusId" +
						", group_concat(i.ProjectItemId SEPARATOR '§~§') as ProjectItemId" +
						", group_concat(i.ProjectId SEPARATOR '§~§') as ProjectId" +
						", group_concat(i.Description SEPARATOR '§~§') as Description" +
						", group_concat(i.DateStart SEPARATOR '§~§') as DateStart" +
						", group_concat(i.DateEnd SEPARATOR '§~§') as DateEnd" +
						", group_concat(i.DatasetId SEPARATOR '§~§') as DatasetId" +
						", group_concat(i.ImageLink SEPARATOR '§~§') as ImageLink" +
						", group_concat(i.OrderIndex SEPARATOR '§~§') as OrderIndex" +
						", group_concat(i.Timestamp SEPARATOR '§~§') as Timestamp" +
						", group_concat(c.PlaceId SEPARATOR '§~§') as PlaceId " +
						", group_concat(c.PlaceName SEPARATOR '§~§') as PlaceName " +
						", group_concat(c.PlaceLatitude SEPARATOR '§~§') as PlaceLatitude " +
						", group_concat(c.PlaceLongitude SEPARATOR '§~§') as PlaceLongitude " +
						", group_concat(c.PlaceLink SEPARATOR '§~§') as PlaceLink " +
						", group_concat(c.PlaceZoom SEPARATOR '§~§') as PlaceZoom " +
						", group_concat(c.PlaceComment SEPARATOR '§~§') as PlaceComment " +
						", group_concat(c.PlaceAccuracy SEPARATOR '§~§') as PlaceAccuracy " +
						", group_concat(c.PlaceUserGenerated SEPARATOR '§~§') as PlaceUserGenerated " +
						"FROM " +
						"(" +
							"SELECT * " +
						    "FROM Item i " +
						    "LEFT JOIN ( " +
							"SELECT i.ItemId as CompletionStatusItemId" +
							", c.Name as CompletionStatusName " +  
					        "FROM CompletionStatus c " +
					        "JOIN Item i " +
					        "ON i.CompletionStatusId = c.CompletionStatusId " +
					        ") c  " +
					        "ON i.ItemId = c.CompletionStatusItemId " +
						") i " +
						"LEFT JOIN " + 
						"(" + 
							"SELECT i.ItemId as ItemId" +
							", group_concat(IFNULL(pl.PlaceId, 'NULL') SEPARATOR '&~&') as PlaceId " +
							", group_concat(IFNULL(pl.Name, 'NULL') SEPARATOR '&~&') as PlaceName " +
							", group_concat(IFNULL(pl.Latitude, 'NULL') SEPARATOR '&~&') as PlaceLatitude " +
							", group_concat(IFNULL(pl.Longitude, 'NULL') SEPARATOR '&~&') as PlaceLongitude " +
							", group_concat(IFNULL(pl.Link, 'NULL') SEPARATOR '&~&') as PlaceLink " +
							", group_concat(IFNULL(pl.Zoom, 'NULL') SEPARATOR '&~&') as PlaceZoom " +
							", group_concat(IFNULL(pl.Comment, 'NULL') SEPARATOR '&~&') as PlaceComment " +
							", group_concat(IFNULL(pl.Accuracy, 'NULL') SEPARATOR '&~&') as PlaceAccuracy " +
							", group_concat(IFNULL(pl.UserGenerated + 0, 'NULL') SEPARATOR '&~&') as PlaceUserGenerated " +
							"FROM Item i " + 
							"LEFT JOIN Place pl on i.ItemId = pl.ItemId " +  
							"GROUP BY i.ItemId " +
						") c " + 
						"ON i.ItemId = c.ItemId " +
						"LEFT JOIN " + 
						"(" +
							"SELECT * " +
							"FROM Story " + 
						") s " +
						"ON i.StoryId = s.StoryId " +
						"WHERE s.StoryId = " + id + " " +
						"GROUP BY s.StoryId ";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
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