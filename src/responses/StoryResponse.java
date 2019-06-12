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

@Path("/stories")
public class StoryResponse {


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
			  story.setdcDescription(rs.getString("StorydcDescription"));
			  story.setedmLandingPage(rs.getString("StoryedmLandingPage"));
			  story.setExternalRecordId(rs.getString("StoryExternalRecordId"));
			  story.setPlaceName(rs.getString("StoryPlaceName"));
			  story.setPlaceLatitude(rs.getFloat("StoryPlaceLatitude"));
			  story.setPlaceLongitude(rs.getFloat("StoryPlaceLongitude"));
			  story.setPlaceUserGenerated(rs.getString("StoryPlaceUserGenerated"));
			  story.setdcCreator(rs.getString("StorydcCreator"));
			  story.setdcSource(rs.getString("StoryedmRights"));
			  story.setdcSource(rs.getString("StorydcSource"));
			  story.setedmCountry(rs.getString("StoryedmCountry"));
			  story.setedmDataProvider(rs.getString("StoryedmDataProvider"));
			  story.setedmProvider(rs.getString("StoryedmProvider"));
			  story.setedmYear(rs.getString("StoryedmYear"));
			  story.setdcPublisher(rs.getString("StorydcPublisher"));
			  story.setdcCoverage(rs.getString("StorydcCoverage"));
			  story.setdcDate(rs.getString("StorydcDate"));
			  story.setdcType(rs.getString("StorydcType"));
			  story.setdcRelation(rs.getString("StorydcRelation"));
			  story.setdctermsMedium(rs.getString("StorydctermsMedium"));
			  story.setedmDatasetName(rs.getString("StoryedmDatasetName"));
			  story.setdcContributor(rs.getString("StorydcContributor"));
			  story.setedmRights(rs.getString("StoryedmRights"));
			  story.setedmBegin(rs.getString("StoryedmBegin"));
			  story.setedmEnd(rs.getString("StoryedmEnd"));
			  story.setSummary(rs.getString("StorySummary"));
			  story.setParentStory(rs.getInt("StoryParentStory"));
			  story.setSearchText(rs.getString("StorySearchText"));
			  story.setDateStart(rs.getTimestamp("StoryDateStart"));
			  story.setDateEnd(rs.getTimestamp("StoryDateEnd"));
			  story.setOrderIndex(rs.getInt("StoryOrderIndex"));
			   
			  // Iterate through Items of the Story
			  List<Item> ItemList = new ArrayList<Item>();
			  if (rs.getString("ItemId") != null) {
				  String[] ItemIds = rs.getString("ItemId").split("§~§");
				  String[] ItemTitles = rs.getString("Title").split("§~§");
				  String[] ItemCompletionStatusColorCodes = rs.getString("CompletionStatusColorcode").split("§~§");
				  String[] ItemCompletionStatusNames = rs.getString("CompletionStatusName").split("§~§");
				  String[] ItemCompletionStatusIds = rs.getString("CompletionStatusId").split("§~§");
				  String[] ItemProjectItemIds = rs.getString("ProjectItemId").split("§~§");
				  String[] ItemProjectIds = rs.getString("ProjectId").split("§~§");
				  String[] ItemDescriptions = null;
				  if (rs.getString("Description") != null) {
					  ItemDescriptions = rs.getString("Description").split("§~§");
				  }
				  String[] ItemDateStarts = null;
				  if (rs.getString("DateStart") != null) {
					  ItemDateStarts = rs.getString("DateStart").split("§~§");
				  }
				  String[] ItemDateEnds = null;
				  if (rs.getString("DateEnd") != null) {
					  ItemDateEnds = rs.getString("DateEnd").split("§~§");
				  }
				  String[] ItemDatasetIds = null;
				  if (rs.getString("DateEnd") != null) {
					  ItemDatasetIds = rs.getString("DatasetId").split("§~§");
				  }
				  String[] ItemImageLinks = rs.getString("ImageLink").split("§~§");
				  String[] ItemOrderIndexs = rs.getString("OrderIndex").split("§~§");
				  String[] ItemTimestamps = rs.getString("Timestamp").split("§~§");

				  // Initialize lists split by Stories
				  String[] PlaceIdList = new String[ItemIds.length];
				  String[] PlaceNameList = new String[ItemIds.length];
				  String[] PlaceLatitudeList = new String[ItemIds.length];
				  String[] PlaceLongitudeList = new String[ItemIds.length];
				  String[] PlaceLinkList = new String[ItemIds.length];
				  String[] PlaceZoomList = new String[ItemIds.length];
				  String[] PlaceCommentList = new String[ItemIds.length];
				  String[] PlaceAccuracyList = new String[ItemIds.length];
				  String[] PlaceUserGeneratedList = new String[ItemIds.length];
				  if (rs.getString("PlaceId") != null) {
					  PlaceIdList = rs.getString("PlaceId").split("§~§");
				  }
				  if (rs.getString("PlaceName") != null) {
					  PlaceNameList = rs.getString("PlaceName").split("§~§");
				  }
				  if (rs.getString("PlaceLatitude") != null) {
					  PlaceLatitudeList = rs.getString("PlaceLatitude").split("§~§");
				  }
				  if (rs.getString("PlaceLongitude") != null) {
					  PlaceLongitudeList = rs.getString("PlaceLongitude").split("§~§");
				  }
				  if (rs.getString("PlaceLink") != null) {
					  PlaceLinkList = rs.getString("PlaceLink").split("§~§");
				  }
				  if (rs.getString("PlaceZoom") != null) {
					  PlaceZoomList = rs.getString("PlaceZoom").split("§~§");
				  }
				  if (rs.getString("PlaceComment") != null) {
					  PlaceCommentList = rs.getString("PlaceComment").split("§~§");
				  }
				  if (rs.getString("PlaceAccuracy") != null) {
					  PlaceAccuracyList = rs.getString("PlaceAccuracy").split("§~§");
				  }
				  if (rs.getString("PlaceUserGenerated") != null) {
					  PlaceUserGeneratedList = rs.getString("PlaceUserGenerated").split("§~§");
				  }
				  

				  for (int j = 0; j < ItemIds.length; j++) {
					  Item item = new Item();
					  item.setItemId(Integer.parseInt(ItemIds[j]));
					  item.setTitle(ItemTitles[j]);
					  item.setCompletionStatusColorCode(ItemCompletionStatusColorCodes[j]);
					  item.setCompletionStatusName(ItemCompletionStatusNames[j]);
					  item.setCompletionStatusId(Integer.parseInt(ItemCompletionStatusIds[j]));
					  if (ItemProjectItemIds != null) {
						  item.setProjectItemId(Integer.parseInt(ItemProjectItemIds[j]));
					  }
					  item.setProjectId(Integer.parseInt(ItemProjectIds[j]));
					  if (ItemDescriptions != null) {
						  item.setDescription(ItemDescriptions[j]);
					  }
					  if (ItemDateStarts != null) {
						  try {
					            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					            Date date = formatter.parse(ItemDateStarts[j]);
					            Timestamp timeStampDate = new Timestamp(date.getTime());
					            item.setDateStart(timeStampDate);
							} catch (ParseException e) {
							    System.out.println("Exception :" + e);
							    return "Exception :" + e;
							}
					  }
					  if (ItemDateEnds != null) {
						  try {
					            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					            Date date = formatter.parse(ItemDateEnds[j]);
					            Timestamp timeStampDate = new Timestamp(date.getTime());
					            item.setDateEnd(timeStampDate);
					        } catch (ParseException e) {
					            System.out.println("Exception :" + e);
					            return "Exception :" + e;
					        }
					  }
					  if (ItemDatasetIds != null) {
						  item.setDatasetId(Integer.parseInt(ItemDatasetIds[j]));
					  }
					  item.setImageLink(ItemImageLinks[j]);
					  item.setOrderIndex(Integer.parseInt(ItemOrderIndexs[j]));
				      item.setTimestamp(ItemTimestamps[j]);
				            
					  //Add Places
					  List<Place> PlaceList = new ArrayList<Place>();
					  if (rs.getString("PlaceId") != null) {
						  // Intitialize lists grouped by items
						  String[] PlaceIds = PlaceIdList[j].split("&~&", -1);
						  String[] PlaceNames = PlaceNameList[j].split("&~&", -1);
						  String[] PlaceLatitudes = PlaceLatitudeList[j].split("&~&", -1);
						  String[] PlaceLongitudes = PlaceLongitudeList[j].split("&~&", -1);
						  String[] PlaceLink = PlaceLinkList[j].split("&~&", -1);
						  String[] PlaceZoom = PlaceZoomList[j].split("&~&", -1);
						  String[] PlaceComment = PlaceCommentList[j].split("&~&", -1);
						  String[] PlaceAccuracy = PlaceAccuracyList[j].split("&~&", -1);
						  String[] PlaceUserGenerated = PlaceUserGeneratedList[j].split("&~&", -1);
						  for (int i = 0; i < PlaceIds.length; i++) {
							  if (!isNumeric(PlaceIds[i])) {
								  continue;
							  }
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
					
					  item.setPlaces(PlaceList);
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
				", s.`dc:Title` as StorydcTitle" +
				", s.`dc:description` as StorydcDescription" +
				", s.`edm:landingPage` as StoryedmLandingPage" +
				", s.ExternalRecordId as StoryExternalRecordId" +
				", s.PlaceName as StoryPlaceName" +
				", s.PlaceLatitude as StoryPlaceLatitude" +
				", s.PlaceLongitude as StoryPlaceLongitude" +
				", s.PlaceUserGenerated as StoryPlaceUserGenerated" +
				", s.`dc:creator` as StorydcCreator" +
				", s.`dc:source` as StorydcSource" +
				", s.`edm:country` as StoryedmCountry" +
				", s.`edm:dataProvider` as StoryedmDataProvider" +
				", s.`edm:provider` as StoryedmProvider" +
				", s.`edm:year` as StoryedmYear" +
				", s.`dc:publisher` as StorydcPublisher" +
				", s.`dc:coverage` as StorydcCoverage" +
				", s.`dc:date` as StorydcDate" +
				", s.`dc:type` as StorydcType" +
				", s.`dc:relation` as StorydcRelation" +
				", s.`dcterms:medium` as StorydctermsMedium" +
				", s.`edm:datasetName` as StoryedmDatasetName" +
				", s.`dc:contributor` as StorydcContributor" +
				", s.`edm:rights` as StoryedmRights" +
				", s.`edm:begin` as StoryedmBegin" +
				", s.`edm:end` as StoryedmEnd" +
				", s.Summary as StorySummary" +
				", s.ParentStory as StoryParentStory" +
				", s.SearchText as StorySearchText" +
				", s.DateStart as StoryDateStart" +
				", s.DateEnd as StoryDateEnd" +
				", s.OrderIndex as StoryOrderIndex" +
				", group_concat(i.ItemId SEPARATOR '§~§') as ItemId" +
				", group_concat(i.Title SEPARATOR '§~§') as Title" +
				", group_concat(i.CompletionStatusColorCode SEPARATOR '§~§') as CompletionStatusColorCode" +
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
						", c.ColorCode as CompletionStatusColorCode " + 
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
		JsonObject data = new JsonParser().parse(body).getAsJsonObject();
		JsonArray dataArray = data.getAsJsonObject().get("@graph").getAsJsonArray();
		List<String> fields = new ArrayList<String>();
		fields.add("dc:title");
		fields.add("dc:description");
		fields.add("dc:creator");
		fields.add("dc:source");
		fields.add("edm:country");
		fields.add("edm:dataProvider");
		fields.add("edm:provider");
		fields.add("edm:rights");
		fields.add("edm:begin");
		fields.add("edm:end");
		fields.add("dc:contributor");
		fields.add("edm:year");
		fields.add("dc:publisher");
		fields.add("dc:coverage");
		fields.add("dc:date");
		fields.add("dc:type");
		fields.add("dc:relation");
		fields.add("dcterms:medium");
		fields.add("edm:datasetName");
		boolean placeAdded = false;
	    int keyCount = dataArray.size();

		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();

		for (int i = 0; i < keyCount; i++) {
			for(Map.Entry<String, JsonElement> entry : dataArray.get(i).getAsJsonObject().entrySet()) {
				if (fields.contains(entry.getKey())) {
					if (!entry.getValue().isJsonObject()) {
						if (!keys.contains(entry.getKey())) {
							keys.add(entry.getKey());
							values.add("\"" + entry.getValue().toString().replace(",", " | ").replaceAll("[\"{}\\[\\]]", "") + "\"");
						}
					}
					else {
						if (entry.getValue().getAsJsonObject().has("@value")) {
							if (!keys.contains(entry.getKey())) {
								keys.add(entry.getKey());
								values.add(entry.getValue().getAsJsonObject().get("@value").toString());
							}
						}
						else if (entry.getValue().getAsJsonObject().has("@id")) {
							if (!keys.contains(entry.getKey())) {
								keys.add(entry.getKey());
								values.add(entry.getValue().getAsJsonObject().get("@id").toString());
							}
						}
					}
				}
				else {
					if (entry.getKey().equals("@type") && entry.getValue().getAsString().equals("edm:Place") && placeAdded == false) {
						if (dataArray.get(i).getAsJsonObject().keySet().contains("geo:lat")
								&& dataArray.get(i).getAsJsonObject().keySet().contains("geo:long")) {
							if (!keys.contains("PlaceLatitude")) {
								keys.add("PlaceLatitude");
								keys.add("PlaceLongitude");
								values.add(dataArray.get(i).getAsJsonObject().get("geo:lat").toString());
								values.add(dataArray.get(i).getAsJsonObject().get("geo:long").toString());
							}
						}
						if (dataArray.get(i).getAsJsonObject().keySet().contains("skos:prefLabel")) {
							JsonArray placeName = new JsonArray();
							if (dataArray.get(i).getAsJsonObject().get("skos:prefLabel").isJsonArray()) {
								if (!keys.contains("PlaceName")) {
									placeName = dataArray.get(i).getAsJsonObject().get("skos:prefLabel").getAsJsonArray();
									for (int j = 0; j < placeName.size(); j++) {
										if (placeName.get(j) instanceof JsonObject && placeName.get(j).getAsJsonObject().get("@language").toString() == "en") {
											keys.add("PlaceName");
											values.add(placeName.get(j).getAsJsonObject().get("@value").toString());
										}
									}
								}
							}
						}
					}
				}
			}
		}

		keys.add("PlaceUserGenerated");
		values.add("1");
		
		String query = "";
		query += "INSERT INTO Story (";

		Iterator<String> keysIterator = keys.iterator();
	    while (keysIterator.hasNext()) {
			query += "`" + keysIterator.next() + "`";
	        if (keysIterator.hasNext()) {
	        	query += ", ";
	        }
		}
	    query += ") VALUES (";
		Iterator<String> valuesIterator = values.iterator();
	    while (valuesIterator.hasNext()) {
			query += valuesIterator.next();
	        if (valuesIterator.hasNext()) {
	        	query += ", ";
	        }
		}
	    query += ")";
		String resource = executeQuery(query, "Insert");
		ResponseBuilder rBuild = Response.ok(resource);
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
		String query = "SELECT * FROM " +
				"(SELECT s.StoryId as StoryId" + 
				", s.`dc:Title` as StorydcTitle" +
				", s.`dc:description` as StorydcDescription" +
				", s.`edm:landingPage` as StoryedmLandingPage" +
				", s.ExternalRecordId as StoryExternalRecordId" +
				", s.PlaceName as StoryPlaceName" +
				", s.PlaceLatitude as StoryPlaceLatitude" +
				", s.PlaceLongitude as StoryPlaceLongitude" +
				", s.PlaceUserGenerated as StoryPlaceUserGenerated" +
				", s.`dc:creator` as StorydcCreator" +
				", s.`dc:source` as StorydcSource" +
				", s.`edm:country` as StoryedmCountry" +
				", s.`edm:dataProvider` as StoryedmDataProvider" +
				", s.`edm:provider` as StoryedmProvider" +
				", s.`edm:year` as StoryedmYear" +
				", s.`dc:publisher` as StorydcPublisher" +
				", s.`dc:coverage` as StorydcCoverage" +
				", s.`dc:date` as StorydcDate" +
				", s.`dc:type` as StorydcType" +
				", s.`dc:relation` as StorydcRelation" +
				", s.`dcterms:medium` as StorydctermsMedium" +
				", s.`edm:datasetName` as StoryedmDatasetName" +
				", s.`dc:contributor` as StorydcContributor" +
				", s.`edm:rights` as StoryedmRights" +
				", s.`edm:begin` as StoryedmBegin" +
				", s.`edm:end` as StoryedmEnd" +
				", s.Summary as StorySummary" +
				", s.ParentStory as StoryParentStory" +
				", s.SearchText as StorySearchText" +
				", s.DateStart as StoryDateStart" +
				", s.DateEnd as StoryDateEnd" +
				", s.OrderIndex as StoryOrderIndex" +
				", group_concat(i.ItemId SEPARATOR '§~§') as ItemId" +
				", group_concat(i.Title SEPARATOR '§~§') as Title" +
				", group_concat(i.CompletionStatusColorCode SEPARATOR '§~§') as CompletionStatusColorCode" +
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
						", c.ColorCode as CompletionStatusColorCode " + 
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
				"GROUP BY s.StoryId) s " +
				"WHERE s.StoryId = " + id;
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