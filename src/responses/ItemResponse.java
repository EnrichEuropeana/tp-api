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
import objects.Person;
import objects.Place;
import objects.Property;
import objects.Transcription;

import java.util.*;
import java.util.Date;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.google.gson.*;

@Path("/Item")
public class ItemResponse {


	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?serverTimezone=CET";
		final String USER = "enrichingeuropeana";
		final String PASS = "Ke;u5De)u8sh";
		   List<Item> itemList = new ArrayList<Item>();
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
			  Item item = new Item();
			  item.setItemId(rs.getInt("ItemId"));
			  
			  // Add Properties
			  List<Property> PropertyList = new ArrayList<Property>();
			  if (rs.getString("PropertyId") != null) {
				  String[] PropertyIds = rs.getString("PropertyId").split(",");
				  String[] PropertyValues = rs.getString("PropertyValue").split(",");
				  String[] PropertyTypeNames = rs.getString("PropertyTypeName").split(",");
				  String[] PropertyEditables = rs.getString("PropertyEditable").split(",");
				  for (int i = 0; i < PropertyIds.length; i++) {
					  Property property = new Property();
					  property.setPropertyId(Integer.parseInt(PropertyIds[i]));
					  property.setValue(PropertyValues[i]);
					  property.setTypeName(PropertyTypeNames[i]);
					  property.setEditable(PropertyEditables[i]);
					  PropertyList.add(property);
				  }
			  }
			  
			  //Add Places
			  List<Place> PlaceList = new ArrayList<Place>();
			  if (rs.getString("PlaceId") != null) {
				  String[] PlaceIds = rs.getString("PlaceId").split(",");
				  String[] PlaceNames = rs.getString("PlaceName").split(",");
				  String[] PlaceLatitudes = rs.getString("PlaceLatitude").split(",");
				  String[] PlaceLongitudes = rs.getString("PlaceLongitude").split(",");
				  String[] PlaceLink = rs.getString("PlaceLink").split(",", -1);
				  String[] PlaceZoom = rs.getString("PlaceZoom").split(",");
				  String[] PlaceComment = rs.getString("PlaceComment").split(",", -1);
				  String[] PlaceAccuracy = rs.getString("PlaceAccuracy").split(",");
				  String[] PlaceUserGenerated = rs.getString("PlaceUserGenerated").split(",");
				  for (int i = 0; i < PlaceIds.length; i++) {
					  Place place = new Place();
					  place.setPlaceId(Integer.parseInt(PlaceIds[i]));
					  place.setName(PlaceNames[i]);
					  place.setLatitude(Float.parseFloat(PlaceLatitudes[i]));
					  place.setLongitude(Float.parseFloat(PlaceLongitudes[i]));
					  place.setLink(PlaceLink[i]);
					  place.setZoom(Integer.parseInt(PlaceZoom[i]));
					  place.setComment(PlaceComment[i]);
					  place.setAccuracy(Integer.parseInt(PlaceAccuracy[i]));
					  place.setUserGenerated(PlaceUserGenerated[i]);
					  PlaceList.add(place);
				  }
			  }
			  
			  //Add Transcriptions
			  List<Transcription> TranscriptionList = new ArrayList<Transcription>();
			  if (rs.getString("TranscriptionId") != null) {
				  String[] TranscriptionIds = rs.getString("TranscriptionId").split(",");
				  String[] TranscriptionTexts = rs.getString("TranscriptionText").split(",");
				  String[] TranscriptionUserIds = rs.getString("TranscriptionUserId").split(",");
				  String[] TranscriptionCurrentVersions = rs.getString("TranscriptionCurrentVersion").split(",");
				  String[] TranscriptionTimestamps = rs.getString("TranscriptionTimestamp").split(",");
				  for (int i = 0; i < TranscriptionIds.length; i++) {
					  Transcription transcription = new Transcription();
					  transcription.setTranscriptionId(Integer.parseInt(TranscriptionIds[i]));
					  transcription.setText(TranscriptionTexts[i]);
					  transcription.setUserId(Integer.parseInt(TranscriptionUserIds[i]));
					  transcription.setCurrentVersion(TranscriptionCurrentVersions[i]);
				      transcription.setTimestamp(TranscriptionTimestamps[i]);
					  TranscriptionList.add(transcription);
				  }
			  }
			  
			  
			  //Add Annotations
			  List<Annotation> AnnotationList = new ArrayList<Annotation>();
			  if (rs.getString("AnnotationId") != null) {
				  String[] AnnotationIds = rs.getString("AnnotationId").split(",");
				  String[] AnnotationTexts = rs.getString("AnnotationText").split(",");
				  String[] AnnotationUserIds = rs.getString("AnnotationUserId").split(",");
				  String[] AnnotationX_Coords = rs.getString("AnnotationX_Coord").split(",", -1);
				  String[] AnnotationY_Coords = rs.getString("AnnotationY_Coord").split(",", -1);
				  String[] AnnotationWidths = rs.getString("AnnotationWidth").split(",", -1);
				  String[] AnnotationHeights = rs.getString("AnnotationHeight").split(",", -1);
				  String[] AnnotationTypes = rs.getString("AnnotationType").split(",");
				  for (int i = 0; i < AnnotationIds.length; i++) {
					  Annotation annotation = new Annotation();
					  annotation.setAnnotationId(Integer.parseInt(AnnotationIds[i]));
					  annotation.setText(AnnotationTexts[i]);
					  annotation.setUserId(Integer.parseInt(AnnotationUserIds[i]));
					  annotation.setX_Coord(Float.parseFloat(AnnotationX_Coords[i]));
					  annotation.setY_Coord(Float.parseFloat(AnnotationY_Coords[i]));
					  annotation.setHeight(Float.parseFloat(AnnotationWidths[i]));
					  annotation.setWidth(Float.parseFloat(AnnotationHeights[i]));
					  annotation.setAnnotationType(AnnotationTypes[i]);
					  AnnotationList.add(annotation);
				  }
			  }
			  
			  
			  //Add Comments
			  List<Comment> CommentList = new ArrayList<Comment>();
			  if (rs.getString("CommentId") != null) {
				  String[] CommentIds = rs.getString("CommentId").split(",");
				  String[] CommentTexts = rs.getString("CommentText").split(",");
				  String[] CommentUserIds = rs.getString("CommentUserId").split(",");
				  String[] CommentTimestamps = rs.getString("CommentTimestamp").split(",");
				  for (int i = 0; i < CommentIds.length; i++) {
					  Comment comment = new Comment();
					  comment.setCommentId(Integer.parseInt(CommentIds[i]));
					  comment.setText(CommentTexts[i]);
					  comment.setUserId(Integer.parseInt(CommentUserIds[i]));
					  
					  // String to Timestamp conversion
					  try {
				            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				            Date date = formatter.parse(CommentTimestamps[i]);
				            Timestamp timeStampDate = new Timestamp(date.getTime());
				            comment.setTimestamp(timeStampDate);
	
				        } catch (ParseException e) {
				            System.out.println("Exception :" + e);
				            return null;
				        }
					  CommentList.add(comment);
				  }
			  }

			  item.setProperties(PropertyList);
			  item.setPlaces(PlaceList);
			  item.setComments(CommentList);
			  item.setTranscriptions(TranscriptionList);
			  item.setAnnotations(AnnotationList);
			  item.setTitle(rs.getString("i.Title"));
			  item.setCompletionStatusName(rs.getString("i.CompletionStatusName"));
			  item.setCompletionStatusId(rs.getInt("i.CompletionStatusId"));
			  item.setProjectItemId(rs.getInt("i.ProjectItemId"));
			  item.setProjectId(rs.getInt("i.ProjectId"));
			  item.setDescription(rs.getString("i.Description"));
			  item.setDateStart(rs.getTimestamp("i.DateStart"));
			  item.setDateEnd(rs.getTimestamp("i.DateEnd"));
			  item.setDatasetId(rs.getInt("i.DatasetId"));
			  item.setImageLink(rs.getString("i.ImageLink"));
			  item.setOrderIndex(rs.getInt("i.OrderIndex"));
			  item.setTimestamp(rs.getString("i.Timestamp"));
			  item.setStoryId(rs.getInt("s.StoryId"));
			  item.setStorydcTitle(rs.getString("s.dcTitle"));
			  item.setStorydcDescription(rs.getString("s.dcDescription"));
			  item.setStoryProjectStoryUrl(rs.getString("s.ProjectStoryUrl"));
			  item.setStoryDateStartDisplay(rs.getString("s.DateStartDisplay"));
			  item.setStoryDateEndDisplay(rs.getString("s.DateEndDisplay"));
			  item.setStoryPlaceName(rs.getString("s.PlaceName"));
			  item.setStoryPlaceLatitude(rs.getFloat("s.PlaceLatitude"));
			  item.setStoryPlaceLongitute(rs.getFloat("s.PlaceLongitute"));
			  item.setStoryPlaceUserGenerated(rs.getString("s.PlaceUserGenerated"));
			  item.setStoryContributor(rs.getString("s.Contributor"));
			  item.setStoryRights(rs.getString("s.Rights"));
			  item.setStorySummary(rs.getString("s.Summary"));
			  item.setStoryParentStory(rs.getInt("s.ParentStory"));
			  item.setStoryManifest(rs.getString("s.Manifest"));
			  item.setStorySearchText(rs.getString("s.SearchText"));
			  item.setStoryDateStart(rs.getTimestamp("s.DateStart"));
			  item.setStoryDateEnd(rs.getTimestamp("s.DateEnd"));
			  item.setStoryOrderIndex(rs.getInt("s.OrderIndex"));

			  itemList.add(item);
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
	    String result = gsonBuilder.toJson(itemList);
	    return result;
	}

	//Get all Entries
	@Path("/all")
	@Produces("application/json;charset=utf-8")
	@POST
	public Response getAll(String body) throws SQLException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonTree = jsonParser.parse(body);
		JsonObject bodyObject = jsonTree.getAsJsonObject();
		String query =  "SELECT * FROM " +
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
							"SELECT i.ItemId as ItemId " +
							", group_concat(p.PropertyId) as PropertyId" +
							", group_concat(pt.Name) as PropertyTypeName " +
							", group_concat(p.Value) as PropertyValue " +
							", group_concat(pt.Editable + 0) as PropertyEditable " +
							"FROM Item i " + 
							"LEFT JOIN ItemProperty ip on i.ItemId = ip.ItemId " + 
							"LEFT JOIN Property p on ip.PropertyId = p.PropertyId " + 
							"LEFT JOIN PropertyType pt on p.PropertyTypeId = pt.PropertyTypeId " + 
							"GROUP BY i.ItemId " +
						") a " +
						"ON i.ItemId = a.ItemId " +
						"LEFT JOIN " + 
						"(" + 
							"SELECT i.ItemId as ItemId" +
							", group_concat(c.CommentId) as CommentId " +
							", group_concat(c.Text) as CommentText " +
							", group_concat(c.UserId) as CommentUserId " +
							", group_concat(c.Timestamp) as CommentTimestamp " +
							"FROM Item i " + 
							"LEFT JOIN Comment c on i.ItemId = c.ItemId " +  
							"GROUP BY i.ItemId " +
						") b " +
						"ON i.ItemId = b.ItemId " +
						"LEFT JOIN " + 
						"(" + 
							"SELECT i.ItemId as ItemId" +
							", group_concat(pl.PlaceId) as PlaceId " +
							", group_concat(pl.Name) as PlaceName " +
							", group_concat(pl.Latitude) as PlaceLatitude " +
							", group_concat(pl.Longitude) as PlaceLongitude " +
							", group_concat(pl.Link) as PlaceLink " +
							", group_concat(pl.Zoom) as PlaceZoom " +
							", group_concat(pl.Comment) as PlaceComment " +
							", group_concat(pl.Accuracy) as PlaceAccuracy " +
							", group_concat(pl.UserGenerated + 0) as PlaceUserGenerated " +
							"FROM Item i " + 
							"LEFT JOIN Place pl on i.ItemId = pl.ItemId " +  
							"GROUP BY i.ItemId " +
						") c " + 
						"ON i.ItemId = c.ItemId " +
						"LEFT JOIN " + 
						"(" + 
							"SELECT i.ItemId as ItemId" +
							", group_concat(t.TranscriptionId) as TranscriptionId " +
							", group_concat(t.Text) as TranscriptionText " +
							", group_concat(t.UserId) as TranscriptionUserId " +
							", group_concat(t.CurrentVersion + 0) as TranscriptionCurrentVersion " +
							", group_concat(t.Timestamp) as TranscriptionTimestamp " +
							"FROM Item i " + 
							"LEFT JOIN Transcription t on i.ItemId = t.ItemId " +  
							"GROUP BY i.ItemId " +
						") d " +
						"ON i.ItemId = d.ItemId " +
						"LEFT JOIN " + 
						"(" + 
							"SELECT i.ItemId as ItemId" +
							", group_concat(a.AnnotationId) as AnnotationId " +
							", group_concat(at.Name) as AnnotationType " +
							", group_concat(a.Text) as AnnotationText " +
							", group_concat(a.UserId) as AnnotationUserId " +
							", group_concat(a.X_Coord) as AnnotationX_Coord " +
							", group_concat(a.Y_Coord) as AnnotationY_Coord " +
							", group_concat(a.Width) as AnnotationWidth " +
							", group_concat(a.Height) as AnnotationHeight " +
							"FROM Item i " + 
							"LEFT JOIN Annotation a on i.ItemId = a.ItemId " +
							"LEFT JOIN AnnotationType at on a.AnnotationTypeId = at.AnnotationTypeId " +  
							"GROUP BY i.ItemId " +
						") e " + 
						"ON i.ItemId = e.ItemId " +
						"LEFT JOIN " + 
						"(" +
							"SELECT * " +
							"FROM Story " + 
						") s " +
						"ON i.StoryId = s.StoryId ";
		String resource = executeQuery(query, "Select");
		ResponseBuilder rBuild = Response.ok(resource);
        return rBuild.build();
	}
	
/*
	//Add new entry
	@Path("/add")
	@POST
	public String add(String body) throws SQLException {	
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    Item item = gson.fromJson(body, Item.class);
	    
	    //Check if all mandatory fields are included
	    if (item.Name != null && item.Public != null) {
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
	}
*/

	//Edit entry by id
	@Path("/update/{id}")
	@POST
	public String update(@PathParam("id") int id, String body) throws SQLException {
	    GsonBuilder gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
	    Gson gson = gsonBuilder.create();
	    JsonObject  changes = gson.fromJson(body, JsonObject.class);
	    
	    //Check if NOT NULL field is attempted to be changed to NULL
	    if ((changes.get("Name") == null || !changes.get("Name").isJsonNull())
	    		&& (changes.get("Public") == null || !changes.get("Public").isJsonNull())) {
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

/*
	//Delete entry by id
	@Path("/{id}")
	@DELETE
	public String delete(@PathParam("id") int id) throws SQLException {
		String query =  "SELECT * FROM " +
				"(" +
					"SELECT * " +
					"FROM Item i " + 
				") i " +
				"LEFT JOIN " + 
				"(" +
					"SELECT i.ItemId as ItemId " +
					", group_concat(p.PropertyId) as PropertyId" +
					", group_concat(pt.Name) as PropertyTypeName " +
					", group_concat(p.Value) as PropertyValue " +
					", group_concat(pt.Editable + 0) as PropertyEditable " +
					"FROM Item i " + 
					"LEFT JOIN ItemProperty ip on i.ItemId = ip.ItemId " + 
					"LEFT JOIN Property p on ip.PropertyId = p.PropertyId " + 
					"LEFT JOIN PropertyType pt on p.PropertyTypeId = pt.PropertyTypeId " + 
					"GROUP BY i.ItemId " +
				") a " +
				"ON i.ItemId = a.ItemId " +
				"LEFT JOIN " + 
				"(" + 
					"SELECT i.ItemId as ItemId" +
					", group_concat(c.CommentId) as CommentId " +
					", group_concat(c.Text) as CommentText " +
					", group_concat(c.UserId) as CommentUserId " +
					", group_concat(c.Timestamp) as CommentTimestamp " +
					"FROM Item i " + 
					"LEFT JOIN Comment c on i.ItemId = c.ItemId " +  
					"GROUP BY i.ItemId " +
				") b " +
				"ON i.ItemId = b.ItemId " +
				"LEFT JOIN " + 
				"(" + 
					"SELECT i.ItemId as ItemId" +
					", group_concat(pl.PlaceId) as PlaceId " +
					", group_concat(pl.Name) as PlaceName " +
					", group_concat(pl.Latitude) as PlaceLatitude " +
					", group_concat(pl.Longitude) as PlaceLongitude " +
					", group_concat(pl.Link) as PlaceLink " +
					", group_concat(pl.Zoom) as PlaceZoom " +
					", group_concat(pl.Comment) as PlaceComment " +
					", group_concat(pl.Accuracy) as PlaceAccuracy " +
					", group_concat(pl.UserGenerated + 0) as PlaceUserGenerated " +
					"FROM Item i " + 
					"LEFT JOIN Place pl on i.ItemId = pl.ItemId " +  
					"GROUP BY i.ItemId " +
				") c " + 
				"ON i.ItemId = c.ItemId " +
				"LEFT JOIN " + 
				"(" + 
					"SELECT i.ItemId as ItemId" +
					", group_concat(t.TranscriptionId) as TranscriptionId " +
					", group_concat(t.Text) as TranscriptionText " +
					", group_concat(t.UserId) as TranscriptionUserId " +
					", group_concat(t.CurrentVersion + 0) as TranscriptionCurrentVersion " +
					", group_concat(t.Timestamp) as TranscriptionTimestamp " +
					"FROM Item i " + 
					"LEFT JOIN Transcription t on i.ItemId = t.ItemId " +  
					"GROUP BY i.ItemId " +
				") d " +
				"ON i.ItemId = d.ItemId " +
				"LEFT JOIN " + 
				"(" + 
					"SELECT i.ItemId as ItemId" +
					", group_concat(a.AnnotationId) as AnnotationId " +
					", group_concat(at.Name) as AnnotationType " +
					", group_concat(a.Text) as AnnotationText " +
					", group_concat(a.UserId) as AnnotationUserId " +
					", group_concat(a.X_Coord) as AnnotationX_Coord " +
					", group_concat(a.Y_Coord) as AnnotationY_Coord " +
					", group_concat(a.Width) as AnnotationWidth " +
					", group_concat(a.Height) as AnnotationHeight " +
					"FROM Item i " + 
					"LEFT JOIN Annotation a on i.ItemId = a.ItemId " +
					"LEFT JOIN AnnotationType at on a.AnnotationTypeId = at.AnnotationTypeId " +  
					"GROUP BY i.ItemId " +
				") e " + 
				"ON i.ItemId = e.ItemId " +
				"LEFT JOIN " + 
				"(" +
					"SELECT * " +
					"FROM Story " + 
				") s " +
				"ON i.StoryId = s.StoryId " +
				"WHERE i.ItemId = " + id;
		String resource = executeQuery(query, "Delete");
		return resource;
	}
	*/

		//Get entry by id
		@Path("/{id}")
		@Produces("application/json;charset=utf-8")
		@POST
		public Response getEntryPost(@PathParam("id") int id, String body) throws SQLException {
			JsonParser jsonParser = new JsonParser();
			JsonElement jsonTree = jsonParser.parse(body);
			JsonObject bodyObject = jsonTree.getAsJsonObject();
			String query =  "SELECT * FROM " +
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
								"SELECT i.ItemId as ItemId " +
								", group_concat(p.PropertyId) as PropertyId" +
								", group_concat(pt.Name) as PropertyTypeName " +
								", group_concat(p.Value) as PropertyValue " +
								", group_concat(pt.Editable + 0) as PropertyEditable " +
								"FROM Item i " + 
								"LEFT JOIN ItemProperty ip on i.ItemId = ip.ItemId " + 
								"LEFT JOIN Property p on ip.PropertyId = p.PropertyId " + 
								"LEFT JOIN PropertyType pt on p.PropertyTypeId = pt.PropertyTypeId " + 
								"GROUP BY i.ItemId " +
							") a " +
							"ON i.ItemId = a.ItemId " +
							"LEFT JOIN " + 
							"(" + 
								"SELECT i.ItemId as ItemId" +
								", group_concat(c.CommentId) as CommentId " +
								", group_concat(c.Text) as CommentText " +
								", group_concat(c.UserId) as CommentUserId " +
								", group_concat(c.Timestamp) as CommentTimestamp " +
								"FROM Item i " + 
								"LEFT JOIN Comment c on i.ItemId = c.ItemId " +  
								"GROUP BY i.ItemId " +
							") b " +
							"ON i.ItemId = b.ItemId " +
							"LEFT JOIN " + 
							"(" + 
								"SELECT i.ItemId as ItemId" +
								", group_concat(pl.PlaceId) as PlaceId " +
								", group_concat(pl.Name) as PlaceName " +
								", group_concat(pl.Latitude) as PlaceLatitude " +
								", group_concat(pl.Longitude) as PlaceLongitude " +
								", group_concat(pl.Link) as PlaceLink " +
								", group_concat(pl.Zoom) as PlaceZoom " +
								", group_concat(pl.Comment) as PlaceComment " +
								", group_concat(pl.Accuracy) as PlaceAccuracy " +
								", group_concat(pl.UserGenerated + 0) as PlaceUserGenerated " +
								"FROM Item i " + 
								"LEFT JOIN Place pl on i.ItemId = pl.ItemId " +  
								"GROUP BY i.ItemId " +
							") c " + 
							"ON i.ItemId = c.ItemId " +
							"LEFT JOIN " + 
							"(" + 
								"SELECT i.ItemId as ItemId" +
								", group_concat(t.TranscriptionId) as TranscriptionId " +
								", group_concat(t.Text) as TranscriptionText " +
								", group_concat(t.UserId) as TranscriptionUserId " +
								", group_concat(t.CurrentVersion + 0) as TranscriptionCurrentVersion " +
								", group_concat(t.Timestamp) as TranscriptionTimestamp " +
								"FROM Item i " + 
								"LEFT JOIN Transcription t on i.ItemId = t.ItemId " +  
								"GROUP BY i.ItemId " +
							") d " +
							"ON i.ItemId = d.ItemId " +
							"LEFT JOIN " + 
							"(" + 
								"SELECT i.ItemId as ItemId" +
								", group_concat(a.AnnotationId) as AnnotationId " +
								", group_concat(at.Name) as AnnotationType " +
								", group_concat(a.Text) as AnnotationText " +
								", group_concat(a.UserId) as AnnotationUserId " +
								", group_concat(a.X_Coord) as AnnotationX_Coord " +
								", group_concat(a.Y_Coord) as AnnotationY_Coord " +
								", group_concat(a.Width) as AnnotationWidth " +
								", group_concat(a.Height) as AnnotationHeight " +
								"FROM Item i " + 
								"LEFT JOIN Annotation a on i.ItemId = a.ItemId " +
								"LEFT JOIN AnnotationType at on a.AnnotationTypeId = at.AnnotationTypeId " +  
								"GROUP BY i.ItemId " +
							") e " + 
							"ON i.ItemId = e.ItemId " +
							"LEFT JOIN " + 
							"(" +
								"SELECT * " +
								"FROM Story " + 
							") s " +
							"ON i.StoryId = s.StoryId " +
							"WHERE i.ItemId = " + id;
			String resource = executeQuery(query, "Select");
			ResponseBuilder rBuild = Response.ok(resource);
	        return rBuild.build();
		}

	//Search using custom filters
	@Path("/search")
	@Produces("application/json;charset=utf-8")
	@POST
	public Response search(@Context UriInfo uriInfo, String body) throws SQLException {
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonTree = jsonParser.parse(body);
		JsonObject bodyObject = jsonTree.getAsJsonObject();
		String query =  "SELECT \r\n" + 
				"    i.ItemId as ItemId, \r\n" + 
				"    i.Title as Title, \r\n" + 
				"    i.CompletionStatusId as CompletionStatusId, \r\n" + 
				"    i.ProjectItemId as ProjectItemId, \r\n" + 
				"    i.ProjectId as ProjectId, \r\n" + 
				"    i.Description as Description, \r\n" + 
				"    i.DateStart as DateStart, \r\n" + 
				"    i.DateEnd as DateEnd, \r\n" + 
				"    i.DatasetId as DatasetId, \r\n" + 
				"    i.ImageLink as ImageLink, \r\n" + 
				"    i.OrderIndex as OrderIndex, \r\n" + 
				"    i.Timestamp as Timestamp,\r\n" + 
				"    a.PropertyId as PropertyId,\r\n" + 
				"    a.PropertyTypeName as PropertyTypeName,\r\n" + 
				"    a.PropertyValue as PropertyValue,\r\n" + 
				"    a.PropertyEditable as PropertyEditable,\r\n" + 
				"    b.CommentId as CommentId,\r\n" + 
				"    b.CommentText as CommentText,\r\n" + 
				"    b.CommentUserId as CommentUserId,\r\n" + 
				"    b.CommentTimestamp as CommentTimestamp,\r\n" + 
				"    c.PlaceId as PlaceId,\r\n" + 
				"    c.PlaceName as PlaceName,\r\n" + 
				"    c.PlaceLatitude as PlaceLatitude,\r\n" + 
				"    c.PlaceLongitude as PlaceLongitude,\r\n" + 
				"    c.PlaceLink as PlaceLink,\r\n" + 
				"    c.PlaceZoom as PlaceZoom,\r\n" + 
				"    c.PlaceComment as PlaceComment,\r\n" + 
				"    c.PlaceAccuracy as PlaceAccuracy,\r\n" + 
				"    c.PlaceUserGenerated as PlaceUserGenerated,\r\n" + 
				"    d.TranscriptionId as TranscriptionId,\r\n" + 
				"    d.TranscriptionText as TranscriptionText,\r\n" + 
				"    d.TranscriptionUserId as TranscriptionUserId,\r\n" + 
				"    d.TranscriptionCurrentVersion as TranscriptionCurrentVersion,\r\n" + 
				"    d.TranscriptionTimestamp as TranscriptionTimestamp,\r\n" + 
				"    e.AnnotationId as AnnotationId,\r\n" + 
				"    e.AnnotationType as AnnotationType,\r\n" + 
				"    e.AnnotationText as AnnotationText,\r\n" + 
				"    e.AnnotationUserId as AnnotationUserId,\r\n" + 
				"    e.AnnotationX_Coord as AnnotationX_Coord,\r\n" + 
				"    e.AnnotationY_Coord as AnnotationY_Coord,\r\n" + 
				"    e.AnnotationWidth as AnnotationWidth,\r\n" + 
				"    e.AnnotationHeight as AnnotationHeight,\r\n" + 
				"    s.StoryId as StoryId\r\n" + 
				"	, s.dcTitle as StorydcTitle \r\n" + 
				"	, s.dcDescription as StorydcDescription \r\n" + 
				"	, s.ProjectStoryUrl as StoryProjectStoryUrl \r\n" + 
				"	, s.DateStartDisplay as StoryDateStartDisplay \r\n" + 
				"	, s.DateEndDisplay as StoryDateEndDisplay \r\n" + 
				"	, s.PlaceName as StoryPlaceName \r\n" + 
				"	, s.PlaceLatitude as StoryPlaceLatitude \r\n" + 
				"	, s.PlaceLongitute as StoryPlaceLongitute \r\n" + 
				"	, s.PlaceUserGenerated as StoryPlaceUserGenerated \r\n" + 
				"	, s.Contributor as StoryContributor \r\n" + 
				"	, s.Rights as StoryRights \r\n" + 
				"	, s.Summary as StorySummary \r\n" + 
				"	, s.ParentStory as StoryParentStory \r\n" + 
				"	, s.Manifest as StoryManifest \r\n" + 
				"	, s.SearchText as StorySearchText \r\n" + 
				"	, s.DateStart as StoryDateStart \r\n" + 
				"	, s.DateEnd as StoryDateEnd \r\n" + 
				"	, s.OrderIndex as StoryOrderIndex  FROM " +
				"(" +
				"SELECT * " +
				"FROM Item i " + 
			") i " +
			"LEFT JOIN " + 
			"(" +
				"SELECT i.ItemId as ItemId " +
				", group_concat(p.PropertyId) as PropertyId" +
				", group_concat(pt.Name) as PropertyTypeName " +
				", group_concat(p.Value) as PropertyValue " +
				", group_concat(pt.Editable + 0) as PropertyEditable " +
				"FROM Item i " + 
				"LEFT JOIN ItemProperty ip on i.ItemId = ip.ItemId " + 
				"LEFT JOIN Property p on ip.PropertyId = p.PropertyId " + 
				"LEFT JOIN PropertyType pt on p.PropertyTypeId = pt.PropertyTypeId " + 
				"GROUP BY i.ItemId " +
			") a " +
			"ON i.ItemId = a.ItemId " +
			"LEFT JOIN " + 
			"(" + 
				"SELECT i.ItemId as ItemId" +
				", group_concat(c.CommentId) as CommentId " +
				", group_concat(c.Text) as CommentText " +
				", group_concat(c.UserId) as CommentUserId " +
				", group_concat(c.Timestamp) as CommentTimestamp " +
				"FROM Item i " + 
				"LEFT JOIN Comment c on i.ItemId = c.ItemId " +  
				"GROUP BY i.ItemId " +
			") b " +
			"ON i.ItemId = b.ItemId " +
			"LEFT JOIN " + 
			"(" + 
				"SELECT i.ItemId as ItemId" +
				", group_concat(pl.PlaceId) as PlaceId " +
				", group_concat(pl.Name) as PlaceName " +
				", group_concat(pl.Latitude) as PlaceLatitude " +
				", group_concat(pl.Longitude) as PlaceLongitude " +
				", group_concat(pl.Link) as PlaceLink " +
				", group_concat(pl.Zoom) as PlaceZoom " +
				", group_concat(pl.Comment) as PlaceComment " +
				", group_concat(pl.Accuracy) as PlaceAccuracy " +
				", group_concat(pl.UserGenerated + 0) as PlaceUserGenerated " +
				"FROM Item i " + 
				"LEFT JOIN Place pl on i.ItemId = pl.ItemId " +  
				"GROUP BY i.ItemId " +
			") c " + 
			"ON i.ItemId = c.ItemId " +
			"LEFT JOIN " + 
			"(" + 
				"SELECT i.ItemId as ItemId" +
				", group_concat(t.TranscriptionId) as TranscriptionId " +
				", group_concat(t.Text) as TranscriptionText " +
				", group_concat(t.UserId) as TranscriptionUserId " +
				", group_concat(t.CurrentVersion + 0) as TranscriptionCurrentVersion " +
				", group_concat(t.Timestamp) as TranscriptionTimestamp " +
				"FROM Item i " + 
				"LEFT JOIN Transcription t on i.ItemId = t.ItemId " +  
				"GROUP BY i.ItemId " +
			") d " +
			"ON i.ItemId = d.ItemId " +
			"LEFT JOIN " + 
			"(" + 
				"SELECT i.ItemId as ItemId" +
				", group_concat(a.AnnotationId) as AnnotationId " +
				", group_concat(at.Name) as AnnotationType " +
				", group_concat(a.Text) as AnnotationText " +
				", group_concat(a.UserId) as AnnotationUserId " +
				", group_concat(a.X_Coord) as AnnotationX_Coord " +
				", group_concat(a.Y_Coord) as AnnotationY_Coord " +
				", group_concat(a.Width) as AnnotationWidth " +
				", group_concat(a.Height) as AnnotationHeight " +
				"FROM Item i " + 
				"LEFT JOIN Annotation a on i.ItemId = a.ItemId " +
				"LEFT JOIN AnnotationType at on a.AnnotationTypeId = at.AnnotationTypeId " +  
				"GROUP BY i.ItemId " +
			") e " + 
			"ON i.ItemId = e.ItemId " +
			"LEFT JOIN " + 
			"(" +
				"SELECT * " +
				"FROM Story " + 
			") s " +
			"ON i.StoryId = s.StoryId " +
			"WHERE 1";
		//MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
		
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