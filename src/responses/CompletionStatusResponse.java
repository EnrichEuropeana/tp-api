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

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.common.VerificationException;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;

import eu.europeana.apikey.keycloak.KeycloakTokenVerifier;

import objects.CompletionStatus;

import java.util.*;
import java.sql.*;

import com.google.gson.*;

@Path("/completionStatus")
public class CompletionStatusResponse {
    private static AccessTokenResponse getAccessToken(Keycloak keycloak) {
        try {
            return keycloak.tokenManager().getAccessToken();
        } catch (Exception anyException) {
            System.err.println("error getting access");
            return null;
        }
    }

	public String executeQuery(String query, String type) throws SQLException{
	    String realm = "DataExchangeInfrastructure";
	    String authServerUrl = "https://keycloak-server-test.eanadev.org/auth";
	    String clientId = "tp-api-client";
	    String clientSecret = "8b81cee4-ef9a-49a0-a3ed-fd7435e2496c";

	    Keycloak keycloak = KeycloakBuilder.builder()
	            .realm(realm)
	            .serverUrl(authServerUrl)
	            .clientId(clientId)
	            .clientSecret(clientSecret)
	            .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
	            .build();

	    eu.europeana.apikey.keycloak.KeycloakTokenVerifier.toPublicKey();
	    AccessTokenResponse token = getAccessToken(keycloak);
	    if (token == null) {
	        System.err.println("token null");
	    }
	    try {
	        AccessToken accessToken = eu.europeana.apikey.keycloak.KeycloakTokenVerifier.verifyToken(token.getToken());
	        if (accessToken != null) {
	            System.out.println(accessToken.toString());
	        }
	    } catch (VerificationException e) {
	        System.err.println("verification error");
	    }

	    if (1 == 1) {
	    	return(token.getToken());
	    }
	    
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<CompletionStatus> completionStatusList = new ArrayList<CompletionStatus>();
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
			  CompletionStatus CompletionStatus = new CompletionStatus();
			  CompletionStatus.setCompletionStatusId(rs.getInt("CompletionStatusId"));
			  CompletionStatus.setName(rs.getString("Name"));
			  CompletionStatus.setColorCode(rs.getString("ColorCode"));
			  completionStatusList.add(CompletionStatus);
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
	    String result = gsonBuilder.toJson(completionStatusList);
	    return result;
	}

	// Get entries
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM CompletionStatus WHERE 1";
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
	    CompletionStatus completionStatus = gson.fromJson(body, CompletionStatus.class);
	    
	    //Check if all mandatory fields are included
	    if (completionStatus.CompletionStatusId != null && completionStatus.Name != null) {
			String query = "INSERT INTO CompletionStatus (CompletionStatusId, Name) "
							+ "VALUES (" + completionStatus.CompletionStatusId
							+ ", '" + completionStatus.Name + "')";
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
	    if (changes.get("CompletionStatusId") != null) {
	    	return "Prohibited change attempt";
	    }
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if (changes.get("Name") == null || !changes.get("Name").isJsonNull()) {
		    String query = "UPDATE CompletionStatus SET ";
		    
		    int keyCount = changes.entrySet().size();
		    int i = 1;
			for(Map.Entry<String, JsonElement> entry : changes.entrySet()) {
			    query += entry.getKey() + " = " + entry.getValue();
			    if (i < keyCount) {
			    	query += ", ";
			    }
			    i++;
			}
			query += " WHERE CompletionStatusId = " + id;
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
		String resource = executeQuery("DELETE FROM CompletionStatus WHERE CompletionStatusId = " + id, "Delete");
		return resource;
	}
	

	//Get entry by id
	@Path("/{id}")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getEntry(@PathParam("id") int id) throws SQLException {
		String resource = executeQuery("SELECT * FROM CompletionStatus WHERE CompletionStatusId = " + id, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
}


