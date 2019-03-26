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

import objects.Place;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

@Path("/Place")
public class PlaceResponse {


	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<Place> placeList = new ArrayList<Place>();
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
			  Place Place = new Place();
			  Place.setPlaceId(rs.getInt("PlaceId"));
			  Place.setName(rs.getString("Name"));
			  Place.setLatitude(rs.getFloat("Latitude"));
			  Place.setLongitude(rs.getFloat("Longitude"));
			  Place.setItemId(rs.getInt("ItemId"));
			  Place.setLink(rs.getString("Link"));
			  Place.setZoom(rs.getInt("Zoom"));
			  Place.setComment(rs.getString("Comment"));
			  Place.setAccuracy(rs.getInt("Accuracy"));
			  Place.setEditable(rs.getString("Editable"));
			  placeList.add(Place);
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
	    String result = gsonBuilder.toJson(placeList);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getAll() throws SQLException {
		String query = "SELECT * FROM Place WHERE 1";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}


	//Add new entry
	@Path("/add")
	@POST
	public String add(String body) throws SQLException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Place place = gson.fromJson(body, Place.class);
	    
	    //Check if all mandatory fields are included
	    if (place.Latitude != null && place.Longitude != null && place.ItemId != null) {
			String query = "INSERT INTO Place (Name, Latitude, Longitude, ItemId, Link, Zoom, Comment, Accuracy, Editable) "
							+ "VALUES ('" + place.Name + "'"
							+ ", " + place.Latitude
							+ ", " + place.Longitude
							+ ", " + place.ItemId
							+ ", '" + place.Link + "'"
							+ ", " + place.Zoom
							+ ", '" + place.Comment + "'"
							+ ", " + place.Accuracy
							+ ", '" + place.Editable + "')";
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
	    if (changes.get("PlaceId") != null || changes.get("ItemId") != null) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if ((changes.get("Latitude") == null || !changes.get("Latitude").isJsonNull())
	    		&& (changes.get("Longitude") == null || !changes.get("Longitude").isJsonNull())
	    		&& (changes.get("ItemId") == null || !changes.get("ItemId").isJsonNull())
	    		&& (changes.get("Editable") == null || !changes.get("Editable").isJsonNull())){
		    String query = "UPDATE Place SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE PlaceId = " + id;
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
		String resource = executeQuery("DELETE FROM Place WHERE PlaceId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM Place WHERE PlaceId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Search using custom filters
	@Path("/search")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM Place WHERE 1";
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


