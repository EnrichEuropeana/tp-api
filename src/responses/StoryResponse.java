package responses;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

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

@Path("/Story")
public class StoryResponse {


	public String executeQuery(String query, String type) throws SQLException{
		final String DB_URL="jdbc:mysql://mysql-db1.man.poznan.pl:3307/transcribathon?allowMultiQueries=true";
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
			  story.setProjectItemUrl(rs.getString("StoryProjectItemUrl"));
			  story.setDateStartDisplay(rs.getString("StoryDateStartDisplay"));
			  story.setDateEndDisplay(rs.getString("StoryDateEndDisplay"));
			  story.setPlaceName(rs.getString("StoryPlaceName"));
			  story.setPlaceLatitude(rs.getFloat("StoryPlaceLatitude"));
			  story.setPlaceLongitute(rs.getFloat("StoryPlaceLongitute"));
			  story.setPlaceEditable(rs.getString("StoryPlaceEditable"));
			  story.setContributor(rs.getString("StoryContributor"));
			  story.setRights(rs.getString("StoryRights"));
			  story.setSummary(rs.getString("StorySummary"));
			  story.setParentStory(rs.getInt("StoryParentStory"));
			  story.setManifest(rs.getString("StoryManifest"));
			  story.setSearchText(rs.getString("StorySearchText"));
			  story.setDateStart(rs.getTimestamp("StoryDateStart"));
			  story.setDateEnd(rs.getTimestamp("StoryDateEnd"));
			  story.setOrderIndex(rs.getInt("StoryOrderIndex"));
			   
			  // Iterate through Items of the Story
			  List<Item> ItemList = new ArrayList<Item>();
			  if (rs.getString("ItemId") != null) {
				  String[] ItemIds = rs.getString("ItemId").split("§~§");
				  String[] ItemTitles = rs.getString("Title").split("§~§");
				  String[] ItemCompletionStatusIds = rs.getString("CompletionStatusId").split("§~§");
				  String[] ItemProjectItemIds = rs.getString("ProjectItemId").split("§~§");
				  String[] ItemProjectIds = rs.getString("ProjectId").split("§~§");
				  String[] ItemDescriptions = rs.getString("Description").split("§~§");
				  String[] ItemDateStarts = rs.getString("DateStart").split("§~§");
				  String[] ItemDateEnds = rs.getString("DateEnd").split("§~§");
				  String[] ItemDatasetIds = rs.getString("DatasetId").split("§~§");
				  String[] ItemImageLinks = rs.getString("ImageLink").split("§~§");
				  String[] ItemOrderIndexs = rs.getString("OrderIndex").split("§~§");
				  String[] ItemTimestamps = rs.getString("Timestamp").split("§~§");

				  // Initialize lists split by Stories
				  
				  String[] PropertyIdList = new String[ItemIds.length];
				  String[] PropertyValueList = new String[ItemIds.length];
				  String[] PropertyTypeNameList = new String[ItemIds.length];
				  String[] PropertyEditableList = new String[ItemIds.length];
				  if (rs.getString("PropertyId") != null) {
					  PropertyIdList = rs.getString("PropertyId").split("§~§");
				  }
				  if (rs.getString("PropertyValue") != null) {
					  PropertyValueList = rs.getString("PropertyValue").split("§~§");
				  }
				  if (rs.getString("PropertyTypeName") != null) {
					  PropertyTypeNameList = rs.getString("PropertyTypeName").split("§~§");
				  }
				  if (rs.getString("PropertyEditable") != null) {
					  PropertyEditableList = rs.getString("PropertyEditable").split("§~§");
				  }

				  String[] CommentIdList = new String[ItemIds.length];
				  String[] CommentTextList = new String[ItemIds.length];
				  String[] CommentUserIdList = new String[ItemIds.length];
				  String[] CommentTimestampList = new String[ItemIds.length];
				  if (rs.getString("CommentId") != null) {
					  CommentIdList = rs.getString("CommentId").split("§~§");
				  }
				  if (rs.getString("CommentText") != null) {
					  CommentTextList = rs.getString("CommentText").split("§~§");
				  }
				  if (rs.getString("CommentUserId") != null) {
					  CommentUserIdList = rs.getString("CommentUserId").split("§~§");
				  }
				  if (rs.getString("CommentUserId") != null) {
					  CommentTimestampList = rs.getString("CommentTimestamp").split("§~§");
				  }

				  String[] PlaceIdList = new String[ItemIds.length];
				  String[] PlaceNameList = new String[ItemIds.length];
				  String[] PlaceLatitudeList = new String[ItemIds.length];
				  String[] PlaceLongitudeList = new String[ItemIds.length];
				  String[] PlaceLinkList = new String[ItemIds.length];
				  String[] PlaceZoomList = new String[ItemIds.length];
				  String[] PlaceCommentList = new String[ItemIds.length];
				  String[] PlaceAccuracyList = new String[ItemIds.length];
				  String[] PlaceEditableList = new String[ItemIds.length];
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
				  if (rs.getString("PlaceEditable") != null) {
					  PlaceEditableList = rs.getString("PlaceEditable").split("§~§");
				  }
				  

				  String[] TranscriptionIdList = new String[ItemIds.length];
				  String[] TranscriptionTextList = new String[ItemIds.length];
				  String[] TranscriptionUserIdList = new String[ItemIds.length];
				  String[] TranscriptionCurrentVersionList = new String[ItemIds.length];
				  String[] TranscriptionTimestampList = new String[ItemIds.length];
				  if (rs.getString("TranscriptionId") != null) {
					  TranscriptionIdList = rs.getString("TranscriptionId").split("§~§");
				  }
				  if (rs.getString("TranscriptionText") != null) {
					  TranscriptionTextList = rs.getString("TranscriptionText").split("§~§");
				  }
				  if (rs.getString("TranscriptionUserId") != null) {
					  TranscriptionUserIdList = rs.getString("TranscriptionUserId").split("§~§");
				  }
				  if (rs.getString("TranscriptionCurrentVersion") != null) {
					  TranscriptionCurrentVersionList = rs.getString("TranscriptionCurrentVersion").split("§~§");
				  }
				  if (rs.getString("TranscriptionTimestamp") != null) {
					  TranscriptionTimestampList = rs.getString("TranscriptionTimestamp").split("§~§");
				  }

				  String[] AnnotationIdList = new String[ItemIds.length];
				  String[] AnnotationTypeList = new String[ItemIds.length];
				  String[] AnnotationTextList = new String[ItemIds.length];
				  String[] AnnotationUserIdList = new String[ItemIds.length];
				  String[] AnnotationX_CoordList = new String[ItemIds.length];
				  String[] AnnotationY_CoordList = new String[ItemIds.length];
				  String[] AnnotationWidthList = new String[ItemIds.length];
				  String[] AnnotationHeightList = new String[ItemIds.length];
				  if (rs.getString("AnnotationId") != null) {
					  AnnotationIdList = rs.getString("AnnotationId").split("§~§");
				  }
				  if (rs.getString("AnnotationType") != null) {
					  AnnotationTypeList = rs.getString("AnnotationType").split("§~§");
				  }
				  if (rs.getString("AnnotationText") != null) {
					  AnnotationTextList = rs.getString("AnnotationText").split("§~§");
				  }
				  if (rs.getString("AnnotationUserId") != null) {
					  AnnotationUserIdList = rs.getString("AnnotationUserId").split("§~§");
				  }
				  if (rs.getString("AnnotationX_Coord") != null) {
					  AnnotationX_CoordList = rs.getString("AnnotationX_Coord").split("§~§");
				  }
				  if (rs.getString("AnnotationY_Coord") != null) {
					  AnnotationY_CoordList = rs.getString("AnnotationY_Coord").split("§~§");
				  }
				  if (rs.getString("AnnotationWidth") != null) {
					  AnnotationWidthList = rs.getString("AnnotationWidth").split("§~§");
				  }
				  if (rs.getString("AnnotationHeight") != null) {
					  AnnotationHeightList = rs.getString("AnnotationHeight").split("§~§");
				  }

				  for (int j = 0; j < ItemIds.length; j++) {
					  Item item = new Item();
					  item.setItemId(Integer.parseInt(ItemIds[j]));
					  item.setTitle(ItemTitles[j]);
					  item.setCompletionStatusId(Integer.parseInt(ItemCompletionStatusIds[j]));
					  item.setProjectItemId(Integer.parseInt(ItemProjectItemIds[j]));
					  item.setProjectId(Integer.parseInt(ItemProjectIds[j]));
					  item.setDescription(ItemDescriptions[j]);
					  try {
				            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				            Date date = formatter.parse(ItemDateStarts[j]);
				            Timestamp timeStampDate = new Timestamp(date.getTime());
				            item.setDateStart(timeStampDate);
	
				        } catch (ParseException e) {
				            System.out.println("Exception :" + e);
				            return "Exception :" + e;
				        }
					  try {
				            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				            Date date = formatter.parse(ItemDateEnds[j]);
				            Timestamp timeStampDate = new Timestamp(date.getTime());
				            item.setDateEnd(timeStampDate);
	
				        } catch (ParseException e) {
				            System.out.println("Exception :" + e);
				            return "Exception :" + e;
				        }
					  item.setDatasetId(Integer.parseInt(ItemDatasetIds[j]));
					  item.setImageLink(ItemImageLinks[j]);
					  item.setOrderIndex(Integer.parseInt(ItemOrderIndexs[j]));
					  try {
				            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				            Date date = formatter.parse(ItemTimestamps[j]);
				            Timestamp timeStampDate = new Timestamp(date.getTime());
				            item.setTimestamp(timeStampDate);
	
				        } catch (ParseException e) {
				            System.out.println("Exception :" + e);
				            return "Exception :" + e;
				        }
					  
					  // Add Properties
					  List<Property> PropertyList = new ArrayList<Property>();
					  if (rs.getString("PropertyId") != null) {
						  // Intitialize lists grouped by items
						  String[] PropertyIds = PropertyIdList[j].split("&~&", -1);
						  String[] PropertyValues = PropertyValueList[j].split("&~&", -1);
						  String[] PropertyTypeNames = PropertyTypeNameList[j].split("&~&", -1);
						  String[] PropertyEditables = PropertyEditableList[j].split("&~&", -1);
						  for (int i = 0; i < PropertyIds.length; i++) {
							  if (!isNumeric(PropertyIds[i])) {
								  continue;
							  }
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
						  // Intitialize lists grouped by items
						  String[] PlaceIds = PlaceIdList[j].split("&~&", -1);
						  String[] PlaceNames = PlaceNameList[j].split("&~&", -1);
						  String[] PlaceLatitudes = PlaceLatitudeList[j].split("&~&", -1);
						  String[] PlaceLongitudes = PlaceLongitudeList[j].split("&~&", -1);
						  String[] PlaceLink = PlaceLinkList[j].split("&~&", -1);
						  String[] PlaceZoom = PlaceZoomList[j].split("&~&", -1);
						  String[] PlaceComment = PlaceCommentList[j].split("&~&", -1);
						  String[] PlaceAccuracy = PlaceAccuracyList[j].split("&~&", -1);
						  String[] PlaceEditable = PlaceEditableList[j].split("&~&", -1);
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
							  place.setEditable(PlaceEditable[i]);
							  PlaceList.add(place);
						  }
					  }
					  
					  //Add Transcriptions
					  List<Transcription> TranscriptionList = new ArrayList<Transcription>();
					  if (rs.getString("TranscriptionId") != null) {
						  String[] TranscriptionIds = TranscriptionIdList[j].split("&~&", -1);
						  String[] TranscriptionTexts = TranscriptionTextList[j].split("&~&", -1);
						  String[] TranscriptionUserIds = TranscriptionUserIdList[j].split("&~&", -1);
						  String[] TranscriptionCurrentVersions = TranscriptionCurrentVersionList[j].split("&~&", -1);
						  String[] TranscriptionTimestamps = TranscriptionTimestampList[j].split("&~&", -1);
						  for (int i = 0; i < TranscriptionIds.length; i++) {
							  if (!isNumeric(TranscriptionIds[i])) {
								  continue;
							  }
							  Transcription transcription = new Transcription();
							  transcription.setTranscriptionId(Integer.parseInt(TranscriptionIds[i]));
							  transcription.setText(TranscriptionTexts[i]);
							  transcription.setUserId(Integer.parseInt(TranscriptionUserIds[i]));
							  transcription.setCurrentVersion(TranscriptionCurrentVersions[i]);
							  
							  // String to Timestamp conversion
							  try {
						            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						            Date date = formatter.parse(TranscriptionTimestamps[i]);
						            Timestamp timeStampDate = new Timestamp(date.getTime());
						            transcription.setTimestamp(timeStampDate);
			
						        } catch (ParseException e) {
						            System.out.println("Exception :" + e);
						            return null;
						        }
							  TranscriptionList.add(transcription);
						  }
					  }
					  

					  //Add Annotations
					  List<Annotation> AnnotationList = new ArrayList<Annotation>();
					  if (rs.getString("AnnotationId") != null) {
						  String[] AnnotationIds = AnnotationIdList[j].split("&~&", -1);
						  String[] AnnotationTexts = AnnotationTextList[j].split("&~&", -1);
						  String[] AnnotationUserIds = AnnotationUserIdList[j].split("&~&", -1);
						  String[] AnnotationX_Coords = AnnotationX_CoordList[j].split("&~&", -1);
						  String[] AnnotationY_Coords = AnnotationY_CoordList[j].split("&~&", -1);
						  String[] AnnotationWidths = AnnotationWidthList[j].split("&~&", -1);
						  String[] AnnotationHeights = AnnotationHeightList[j].split("&~&", -1);
						  String[] AnnotationTypes = AnnotationTypeList[j].split("&~&", -1);
						  for (int i = 0; i < AnnotationIds.length; i++) {
							  if (!isNumeric(AnnotationIds[i])) {
								  continue;
							  }
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
						  // Intitialize lists grouped by items
						  String[] CommentIds = CommentIdList[j].split("&~&", -1);
						  String[] CommentTexts = CommentTextList[j].split("&~&", -1);
						  String[] CommentUserIds = CommentUserIdList[j].split("&~&", -1);
						  String[] CommentTimestamps = CommentTimestampList[j].split("&~&", -1);
						  for (int i = 0; i < CommentIds.length; i++) {
							  if (!isNumeric(CommentIds[i])) {
								  continue;
							  }
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
					  item.setAnnotations(AnnotationList);
					  item.setTranscriptions(TranscriptionList);
					  item.setPlaces(PlaceList);
					  item.setComments(CommentList);
					  item.setProperties(PropertyList);
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

	//Get all Entries
	@Path("/all")
	@GET
	public String getAll() throws SQLException {
		String query = "SELECT s.StoryId as StoryId" + 
				", s.dcTitle as StorydcTitle" +
				", s.dcDescription as StorydcDescription" +
				", s.ProjectItemUrl as StoryProjectItemUrl" +
				", s.DateStartDisplay as StoryDateStartDisplay" +
				", s.DateEndDisplay as StoryDateEndDisplay" +
				", s.PlaceName as StoryPlaceName" +
				", s.PlaceLatitude as StoryPlaceLatitude" +
				", s.PlaceLongitute as StoryPlaceLongitute" +
				", s.PlaceEditable as StoryPlaceEditable" +
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
				", group_concat(a.PropertyId SEPARATOR '§~§') as PropertyId " +
				", group_concat(a.PropertyValue SEPARATOR '§~§') as PropertyValue " +
				", group_concat(a.PropertyTypeName SEPARATOR '§~§') as PropertyTypeName " +
				", group_concat(a.PropertyEditable SEPARATOR '§~§') as PropertyEditable " +
				", group_concat(b.CommentId SEPARATOR '§~§') as CommentId " +
				", group_concat(b.CommentText SEPARATOR '§~§') as CommentText " +
				", group_concat(b.CommentUserId SEPARATOR '§~§') as CommentUserId " +
				", group_concat(b.CommentTimestamp SEPARATOR '§~§') as CommentTimestamp " +
				", group_concat(c.PlaceId SEPARATOR '§~§') as PlaceId " +
				", group_concat(c.PlaceName SEPARATOR '§~§') as PlaceName " +
				", group_concat(c.PlaceLatitude SEPARATOR '§~§') as PlaceLatitude " +
				", group_concat(c.PlaceLongitude SEPARATOR '§~§') as PlaceLongitude " +
				", group_concat(c.PlaceLink SEPARATOR '§~§') as PlaceLink " +
				", group_concat(c.PlaceZoom SEPARATOR '§~§') as PlaceZoom " +
				", group_concat(c.PlaceComment SEPARATOR '§~§') as PlaceComment " +
				", group_concat(c.PlaceAccuracy SEPARATOR '§~§') as PlaceAccuracy " +
				", group_concat(c.PlaceEditable SEPARATOR '§~§') as PlaceEditable " +
				", group_concat(d.TranscriptionId SEPARATOR '§~§') as TranscriptionId " +
				", group_concat(d.TranscriptionText SEPARATOR '§~§') as TranscriptionText " +
				", group_concat(d.TranscriptionUserId SEPARATOR '§~§') as TranscriptionUserId " +
				", group_concat(d.TranscriptionCurrentVersion SEPARATOR '§~§') as TranscriptionCurrentVersion " +
				", group_concat(d.TranscriptionTimestamp SEPARATOR '§~§') as TranscriptionTimestamp " +
				", group_concat(e.AnnotationId SEPARATOR '§~§') as AnnotationId " +
				", group_concat(e.AnnotationType SEPARATOR '§~§') as AnnotationType " +
				", group_concat(e.AnnotationText SEPARATOR '§~§') as AnnotationText " +
				", group_concat(e.AnnotationUserId SEPARATOR '§~§') as AnnotationUserId " +
				", group_concat(e.AnnotationX_Coord SEPARATOR '§~§') as AnnotationX_Coord " +
				", group_concat(e.AnnotationY_Coord SEPARATOR '§~§') as AnnotationY_Coord " +
				", group_concat(e.AnnotationWidth SEPARATOR '§~§') as AnnotationWidth " +
				", group_concat(e.AnnotationHeight SEPARATOR '§~§') as AnnotationHeight " +
				"FROM " +
				"(" +
					"SELECT * " +
					"FROM Item i " + 
				") i " +
				"LEFT JOIN " + 
				"(" +
					"SELECT i.ItemId as ItemId " +
					", group_concat(IFNULL(p.PropertyId, 'NULL') SEPARATOR '&~&') as PropertyId" +
					", group_concat(IFNULL(pt.Name, 'NULL') SEPARATOR '&~&') as PropertyTypeName " +
					", group_concat(IFNULL(p.Value, 'NULL') SEPARATOR '&~&') as PropertyValue " +
					", group_concat(IFNULL(pt.Editable + 0, 'NULL') SEPARATOR '&~&') as PropertyEditable " +
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
					", group_concat(IFNULL(c.CommentId, 'NULL') SEPARATOR '&~&') as CommentId " +
					", group_concat(IFNULL(c.Text, 'NULL') SEPARATOR '&~&') as CommentText " +
					", group_concat(IFNULL(c.UserId, 'NULL') SEPARATOR '&~&') as CommentUserId " +
					", group_concat(IFNULL(c.Timestamp, 'NULL') SEPARATOR '&~&') as CommentTimestamp " +
					"FROM Item i " + 
					"LEFT JOIN Comment c on i.ItemId = c.ItemId " +  
					"GROUP BY i.ItemId " +
				") b " +
				"ON i.ItemId = b.ItemId " +
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
					", group_concat(IFNULL(pl.Editable + 0, 'NULL') SEPARATOR '&~&') as PlaceEditable " +
					"FROM Item i " + 
					"LEFT JOIN Place pl on i.ItemId = pl.ItemId " +  
					"GROUP BY i.ItemId " +
				") c " + 
				"ON i.ItemId = c.ItemId " +
				"LEFT JOIN " + 
				"(" + 
					"SELECT i.ItemId as ItemId" +
					", group_concat(IFNULL(t.TranscriptionId, 'NULL') SEPARATOR '&~&') as TranscriptionId " +
					", group_concat(IFNULL(t.Text, 'NULL') SEPARATOR '&~&') as TranscriptionText " +
					", group_concat(IFNULL(t.UserId, 'NULL') SEPARATOR '&~&') as TranscriptionUserId " +
					", group_concat(IFNULL(t.CurrentVersion + 0, 'NULL') SEPARATOR '&~&') as TranscriptionCurrentVersion " +
					", group_concat(IFNULL(t.Timestamp, 'NULL') SEPARATOR '&~&') as TranscriptionTimestamp " +
					"FROM Item i " + 
					"LEFT JOIN Transcription t on i.ItemId = t.ItemId " +  
					"GROUP BY i.ItemId " +
				") d " +
				"ON i.ItemId = d.ItemId " +
				"LEFT JOIN " + 
				"(" + 
					"SELECT i.ItemId as ItemId" +
					", group_concat(IFNULL(a.AnnotationId, 'NULL') SEPARATOR '&~&') as AnnotationId " +
					", group_concat(IFNULL(at.Name, 'NULL') SEPARATOR '&~&') as AnnotationType " +
					", group_concat(IFNULL(a.Text, 'NULL') SEPARATOR '&~&') as AnnotationText " +
					", group_concat(IFNULL(a.UserId, 'NULL') SEPARATOR '&~&') as AnnotationUserId " +
					", group_concat(IFNULL(a.X_Coord, 'NULL') SEPARATOR '&~&') as AnnotationX_Coord " +
					", group_concat(IFNULL(a.Y_Coord, 'NULL') SEPARATOR '&~&') as AnnotationY_Coord " +
					", group_concat(IFNULL(a.Width, 'NULL') SEPARATOR '&~&') as AnnotationWidth " +
					", group_concat(IFNULL(a.Height, 'NULL') SEPARATOR '&~&') as AnnotationHeight " +
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
				"GROUP BY s.StoryId ";
		String resource = executeQuery(query, "Select");
		return resource;
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
	}
*/

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
	@GET
	public String getEntry(@PathParam("id") int id) throws SQLException {
		String query = "SELECT s.StoryId as StoryId" + 
						", s.dcTitle as StorydcTitle" +
						", s.dcDescription as StorydcDescription" +
						", s.ProjectItemUrl as StoryProjectItemUrl" +
						", s.DateStartDisplay as StoryDateStartDisplay" +
						", s.DateEndDisplay as StoryDateEndDisplay" +
						", s.PlaceName as StoryPlaceName" +
						", s.PlaceLatitude as StoryPlaceLatitude" +
						", s.PlaceLongitute as StoryPlaceLongitute" +
						", s.PlaceEditable as StoryPlaceEditable" +
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
						", group_concat(a.PropertyId SEPARATOR '§~§') as PropertyId " +
						", group_concat(a.PropertyValue SEPARATOR '§~§') as PropertyValue " +
						", group_concat(a.PropertyTypeName SEPARATOR '§~§') as PropertyTypeName " +
						", group_concat(a.PropertyEditable SEPARATOR '§~§') as PropertyEditable " +
						", group_concat(b.CommentId SEPARATOR '§~§') as CommentId " +
						", group_concat(b.CommentText SEPARATOR '§~§') as CommentText " +
						", group_concat(b.CommentUserId SEPARATOR '§~§') as CommentUserId " +
						", group_concat(b.CommentTimestamp SEPARATOR '§~§') as CommentTimestamp " +
						", group_concat(c.PlaceId SEPARATOR '§~§') as PlaceId " +
						", group_concat(c.PlaceName SEPARATOR '§~§') as PlaceName " +
						", group_concat(c.PlaceLatitude SEPARATOR '§~§') as PlaceLatitude " +
						", group_concat(c.PlaceLongitude SEPARATOR '§~§') as PlaceLongitude " +
						", group_concat(c.PlaceLink SEPARATOR '§~§') as PlaceLink " +
						", group_concat(c.PlaceZoom SEPARATOR '§~§') as PlaceZoom " +
						", group_concat(c.PlaceComment SEPARATOR '§~§') as PlaceComment " +
						", group_concat(c.PlaceAccuracy SEPARATOR '§~§') as PlaceAccuracy " +
						", group_concat(c.PlaceEditable SEPARATOR '§~§') as PlaceEditable " +
						", group_concat(d.TranscriptionId SEPARATOR '§~§') as TranscriptionId " +
						", group_concat(d.TranscriptionText SEPARATOR '§~§') as TranscriptionText " +
						", group_concat(d.TranscriptionUserId SEPARATOR '§~§') as TranscriptionUserId " +
						", group_concat(d.TranscriptionCurrentVersion SEPARATOR '§~§') as TranscriptionCurrentVersion " +
						", group_concat(d.TranscriptionTimestamp SEPARATOR '§~§') as TranscriptionTimestamp " +
						", group_concat(e.AnnotationId SEPARATOR '§~§') as AnnotationId " +
						", group_concat(e.AnnotationType SEPARATOR '§~§') as AnnotationType " +
						", group_concat(e.AnnotationText SEPARATOR '§~§') as AnnotationText " +
						", group_concat(e.AnnotationUserId SEPARATOR '§~§') as AnnotationUserId " +
						", group_concat(e.AnnotationX_Coord SEPARATOR '§~§') as AnnotationX_Coord " +
						", group_concat(e.AnnotationY_Coord SEPARATOR '§~§') as AnnotationY_Coord " +
						", group_concat(e.AnnotationWidth SEPARATOR '§~§') as AnnotationWidth " +
						", group_concat(e.AnnotationHeight SEPARATOR '§~§') as AnnotationHeight " +
						"FROM " +
						"(" +
							"SELECT * " +
							"FROM Item i " + 
						") i " +
						"LEFT JOIN " + 
						"(" +
							"SELECT i.ItemId as ItemId " +
							", group_concat(IFNULL(p.PropertyId, 'NULL') SEPARATOR '&~&') as PropertyId" +
							", group_concat(IFNULL(pt.Name, 'NULL') SEPARATOR '&~&') as PropertyTypeName " +
							", group_concat(IFNULL(p.Value, 'NULL') SEPARATOR '&~&') as PropertyValue " +
							", group_concat(IFNULL(pt.Editable + 0, 'NULL') SEPARATOR '&~&') as PropertyEditable " +
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
							", group_concat(IFNULL(c.CommentId, 'NULL') SEPARATOR '&~&') as CommentId " +
							", group_concat(IFNULL(c.Text, 'NULL') SEPARATOR '&~&') as CommentText " +
							", group_concat(IFNULL(c.UserId, 'NULL') SEPARATOR '&~&') as CommentUserId " +
							", group_concat(IFNULL(c.Timestamp, 'NULL') SEPARATOR '&~&') as CommentTimestamp " +
							"FROM Item i " + 
							"LEFT JOIN Comment c on i.ItemId = c.ItemId " +  
							"GROUP BY i.ItemId " +
						") b " +
						"ON i.ItemId = b.ItemId " +
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
							", group_concat(IFNULL(pl.Editable + 0, 'NULL') SEPARATOR '&~&') as PlaceEditable " +
							"FROM Item i " + 
							"LEFT JOIN Place pl on i.ItemId = pl.ItemId " +  
							"GROUP BY i.ItemId " +
						") c " + 
						"ON i.ItemId = c.ItemId " +
						"LEFT JOIN " + 
						"(" + 
							"SELECT i.ItemId as ItemId" +
							", group_concat(IFNULL(t.TranscriptionId, 'NULL') SEPARATOR '&~&') as TranscriptionId " +
							", group_concat(IFNULL(t.Text, 'NULL') SEPARATOR '&~&') as TranscriptionText " +
							", group_concat(IFNULL(t.UserId, 'NULL') SEPARATOR '&~&') as TranscriptionUserId " +
							", group_concat(IFNULL(t.CurrentVersion + 0, 'NULL') SEPARATOR '&~&') as TranscriptionCurrentVersion " +
							", group_concat(IFNULL(t.Timestamp, 'NULL') SEPARATOR '&~&') as TranscriptionTimestamp " +
							"FROM Item i " + 
							"LEFT JOIN Transcription t on i.ItemId = t.ItemId " +  
							"GROUP BY i.ItemId " +
						") d " +
						"ON i.ItemId = d.ItemId " +
						"LEFT JOIN " + 
						"(" + 
							"SELECT i.ItemId as ItemId" +
							", group_concat(IFNULL(a.AnnotationId, 'NULL') SEPARATOR '&~&') as AnnotationId " +
							", group_concat(IFNULL(at.Name, 'NULL') SEPARATOR '&~&') as AnnotationType " +
							", group_concat(IFNULL(a.Text, 'NULL') SEPARATOR '&~&') as AnnotationText " +
							", group_concat(IFNULL(a.UserId, 'NULL') SEPARATOR '&~&') as AnnotationUserId " +
							", group_concat(IFNULL(a.X_Coord, 'NULL') SEPARATOR '&~&') as AnnotationX_Coord " +
							", group_concat(IFNULL(a.Y_Coord, 'NULL') SEPARATOR '&~&') as AnnotationY_Coord " +
							", group_concat(IFNULL(a.Width, 'NULL') SEPARATOR '&~&') as AnnotationWidth " +
							", group_concat(IFNULL(a.Height, 'NULL') SEPARATOR '&~&') as AnnotationHeight " +
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
						"WHERE s.StoryId = " + id + " " +
						"GROUP BY s.StoryId ";
		String resource = executeQuery(query, "Select");
		return resource;
	}

	//Search using custom filters
	@Path("/search")
	@GET
	public String search(@Context UriInfo uriInfo) throws SQLException {
		String query = "SELECT * FROM Item WHERE 1";
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