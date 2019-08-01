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

import objects.ItemLanguage;

import java.util.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

import com.google.gson.*;

@Path("/itemLanguages")
public class ItemLanguageResponse {


	public String executeQuery(String query, String type) throws SQLException{
		   List<ItemLanguage> itemLanguageList = new ArrayList<ItemLanguage>();
	       try (InputStream input = new FileInputStream("/home/enrich/tomcat/apache-tomcat-9.0.13/webapps/tp-api/WEB-INF/config.properties")) {

	            Properties prop = new Properties();

	            // load a properties file
	            prop.load(input);

	            // get the language value and print it out
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
			  ItemLanguage itemLanguage = new ItemLanguage();;
			  itemLanguage.setItemId(rs.getInt("ItemId"));
			  itemLanguage.setLanguageId(rs.getInt("LanguageId"));
			  itemLanguage.setName(rs.getString("Name"));
			  itemLanguage.setNameEnglish(rs.getString("NameEnglish"));
			  itemLanguage.setShortName(rs.getString("ShortName"));
			  itemLanguage.setCode(rs.getString("Code"));
			  itemLanguageList.add(itemLanguage);
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
	    String result = gsonBuilder.toJson(itemLanguageList);
	    return result;
	}

	//Search using custom filters
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM ItemLanguage il "
						+ "JOIN Language l "
						+ "ON il.LanguageId = l.LanguageId "
						+ "WHERE 1";
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
	@Path("/add")
	@POST
	public Response add(String body) throws SQLException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    ItemLanguage itemLanguage = gson.fromJson(body, ItemLanguage.class);
	    
	    //Check if all mandatory fields are included
	    if (itemLanguage.ItemId != null && itemLanguage.LanguageId != null) {
			String query = "INSERT INTO ItemLanguage (ItemId, LanguageId) "
							+ "VALUES ('" + itemLanguage.ItemId + "'"
									+ ", " + itemLanguage.LanguageId + ")";
			String resource = executeQuery(query, "Insert");
			ResponseBuilder rBuild = Response.ok(resource);
			return rBuild.build();
	    } else {
			ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
			return rBuild.build();
	    }
	}

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("DELETE FROM ItemLanguage WHERE ItemLanguageId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM ItemLanguage WHERE ItemLanguageId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

}