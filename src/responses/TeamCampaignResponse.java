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

import objects.TeamCampaign;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

@Path("/TeamCampaign")
public class TeamCampaignResponse {
	
	
	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<TeamCampaign> teamCampaignList = new ArrayList<TeamCampaign>();
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
			  TeamCampaign teamCampaign = new TeamCampaign();
			  teamCampaign.setTeamId(rs.getInt("TeamId"));
			  teamCampaign.setCampaignId(rs.getInt("CampaignId"));
			  teamCampaignList.add(teamCampaign);
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
	    String result = gsonBuilder.toJson(teamCampaignList);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getAll() throws SQLException {
		String query = "SELECT * FROM TeamCampaign WHERE 1";
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
	    TeamCampaign teamCampaign = gson.fromJson(body, TeamCampaign.class);
	    
	    //Check if all mandatory fields are included
	    if (teamCampaign.TeamId != null && teamCampaign.CampaignId != null) {
			String query = "INSERT INTO TeamCampaign (TeamId, CampaignId) "
							+ "VALUES ('" + teamCampaign.TeamId + "'"
								+ ", " + teamCampaign.CampaignId + ")";
			String resource = executeQuery(query, "Insert");
			return resource;
	    } else {
	    	return "Fields missing";
	    }
	}

	

	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("DELETE FROM TeamCampaign WHERE TeamCampaignId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM TeamCampaign WHERE TeamCampaignId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}

	//Search using custom filters
	@Path("/search")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM TeamCampaign WHERE 1";
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


