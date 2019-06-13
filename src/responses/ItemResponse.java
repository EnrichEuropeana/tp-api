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

@Path("/items")
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
			  item.setTitle(rs.getString("Title"));
			  item.setCompletionStatusColorCode(rs.getString("CompletionStatusColorCode"));
			  item.setCompletionStatusName(rs.getString("CompletionStatusName"));
			  item.setCompletionStatusId(rs.getInt("CompletionStatusId"));
			  item.setTranscriptionStatusColorCode(rs.getString("TranscriptionStatusColorCode"));
			  item.setTranscriptionStatusName(rs.getString("TranscriptionStatusName"));
			  item.setTranscriptionStatusId(rs.getInt("TranscriptionStatusId"));
			  item.setDescriptionStatusColorCode(rs.getString("DescriptionStatusColorCode"));
			  item.setDescriptionStatusName(rs.getString("DescriptionStatusName"));
			  item.setDescriptionStatusId(rs.getInt("DescriptionStatusId"));
			  item.setLocationStatusColorCode(rs.getString("LocationStatusColorCode"));
			  item.setLocationStatusName(rs.getString("LocationStatusName"));
			  item.setLocationStatusId(rs.getInt("LocationStatusId"));
			  item.setTaggingStatusColorCode(rs.getString("TaggingStatusColorCode"));
			  item.setTaggingStatusName(rs.getString("TaggingStatusName"));
			  item.setTaggingStatusId(rs.getInt("TaggingStatusId"));
			  item.setAutomaticEnrichmentStatusColorCode(rs.getString("AutomaticEnrichmentStatusColorCode"));
			  item.setAutomaticEnrichmentStatusName(rs.getString("AutomaticEnrichmentStatusName"));
			  item.setAutomaticEnrichmentStatusId(rs.getInt("AutomaticEnrichmentStatusId"));
			  item.setProjectItemId(rs.getInt("ProjectItemId"));
			  item.setDescription(rs.getString("Description"));
			  item.setDateStart(rs.getTimestamp("DateStart"));
			  item.setDateEnd(rs.getTimestamp("DateEnd"));
			  item.setDatasetId(rs.getInt("DatasetId"));
			  item.setImageLink(rs.getString("ImageLink"));
			  item.setOrderIndex(rs.getInt("OrderIndex"));
			  item.setTimestamp(rs.getString("Timestamp"));
			  item.setManifest(rs.getString("Manifest"));
			  item.setStoryId(rs.getInt("StoryId"));
			  item.setStorydcTitle(rs.getString("StorydcTitle"));
			  item.setStorydcDescription(rs.getString("StorydcDescription"));
			  item.setStoryedmLandingPage(rs.getString("StoryedmLandingPage"));
			  item.setStoryExternalRecordId(rs.getString("StoryExternalRecordId"));
			  item.setStoryPlaceName(rs.getString("StoryPlaceName"));
			  item.setStoryPlaceLatitude(rs.getFloat("StoryPlaceLatitude"));
			  item.setStoryPlaceLongitude(rs.getFloat("StoryPlaceLongitude"));
			  item.setStoryPlaceUserGenerated(rs.getString("StoryPlaceUserGenerated"));
			  item.setStorydcCreator(rs.getString("StorydcCreator"));
			  item.setStorydcSource(rs.getString("StoryedmRights"));
			  item.setStorydcSource(rs.getString("StorydcSource"));
			  item.setStoryedmCountry(rs.getString("StoryedmCountry"));
			  item.setStoryedmDataProvider(rs.getString("StoryedmDataProvider"));
			  item.setStoryedmProvider(rs.getString("StoryedmProvider"));
			  item.setStoryedmYear(rs.getString("StoryedmYear"));
			  item.setStorydcPublisher(rs.getString("StorydcPublisher"));
			  item.setStorydcCoverage(rs.getString("StorydcCoverage"));
			  item.setStorydcDate(rs.getString("StorydcDate"));
			  item.setStorydcType(rs.getString("StorydcType"));
			  item.setStorydcRelation(rs.getString("StorydcRelation"));
			  item.setStorydctermsMedium(rs.getString("StorydctermsMedium"));
			  item.setStoryedmDatasetName(rs.getString("StoryedmDatasetName"));
			  item.setStorydcContributor(rs.getString("StorydcContributor"));
			  item.setStoryedmRights(rs.getString("StoryedmRights"));
			  item.setStoryedmBegin(rs.getString("StoryedmBegin"));
			  item.setStoryedmEnd(rs.getString("StoryedmEnd"));
			  item.setStoryProjectId(rs.getInt("StoryProjectId"));
			  item.setStorySummary(rs.getString("StorySummary"));
			  item.setStoryParentStory(rs.getInt("StoryParentStory"));
			  item.setStorySearchText(rs.getString("StorySearchText"));
			  item.setStoryDateStart(rs.getTimestamp("StoryDateStart"));
			  item.setStoryDateEnd(rs.getTimestamp("StoryDateEnd"));
			  item.setStoryOrderIndex(rs.getInt("StoryOrderIndex"));

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
	@Path("")
	@Produces("application/json;charset=utf-8")
	@GET
	public Response getAll(@Context UriInfo uriInfo) throws SQLException {
		String query =  "SELECT * FROM (" +
				"SELECT \r\n" + 
				"    i.ItemId as ItemId, \r\n" + 
				"    i.Title as Title, \r\n" + 
				"    i.CompletionStatusId as CompletionStatusId, \r\n" + 
				"    i.CompletionStatusName as CompletionStatusName, \r\n" + 
				"    i.CompletionStatusColorCode as CompletionStatusColorCode, \r\n" + 
				"    i.TranscriptionStatusId as TranscriptionStatusId, \r\n" + 
				"    i.TranscriptionStatusName as TranscriptionStatusName, \r\n" + 
				"    i.TranscriptionStatusColorCode as TranscriptionStatusColorCode, \r\n" + 
				"    i.DescriptionStatusId as DescriptionStatusId, \r\n" + 
				"    i.DescriptionStatusName as DescriptionStatusName, \r\n" + 
				"    i.DescriptionStatusColorCode as DescriptionStatusColorCode, \r\n" + 
				"    i.LocationStatusId as LocationStatusId, \r\n" + 
				"    i.LocationStatusName as LocationStatusName, \r\n" + 
				"    i.LocationStatusColorCode as LocationStatusColorCode, \r\n" + 
				"    i.TaggingStatusId as TaggingStatusId, \r\n" + 
				"    i.TaggingStatusName as TaggingStatusName, \r\n" + 
				"    i.TaggingStatusColorCode as TaggingStatusColorCode, \r\n" + 
				"    i.AutomaticEnrichmentStatusId as AutomaticEnrichmentStatusId, \r\n" + 
				"    i.AutomaticEnrichmentStatusName as AutomaticEnrichmentStatusName, \r\n" + 
				"    i.AutomaticEnrichmentStatusColorCode as AutomaticEnrichmentStatusColorCode, \r\n" + 
				"    i.ProjectItemId as ProjectItemId, \r\n" + 
				"    i.Description as Description, \r\n" + 
				"    i.DateStart as DateStart, \r\n" + 
				"    i.DateEnd as DateEnd, \r\n" + 
				"    i.DatasetId as DatasetId, \r\n" + 
				"    i.ImageLink as ImageLink, \r\n" + 
				"    i.OrderIndex as OrderIndex, \r\n" + 
				"    i.Timestamp as Timestamp,\r\n" + 
				"    i.Manifest as Manifest,\r\n" + 
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
				"	, s.`dc:title` as StorydcTitle \r\n" + 
				"	, s.`dc:description` as StorydcDescription \r\n" + 
				"	, s.`edm:landingPage` as StoryedmLandingPage \r\n" + 
				"	, s.ExternalRecordId as StoryExternalRecordId \r\n" + 
				"	, s.PlaceName as StoryPlaceName \r\n" + 
				"	, s.PlaceLatitude as StoryPlaceLatitude \r\n" + 
				"	, s.PlaceLongitude as StoryPlaceLongitude \r\n" + 
				"	, s.PlaceUserGenerated as StoryPlaceUserGenerated \r\n" + 
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
				"	, s.`dc:contributor` as StorydcContributor \r\n" + 
				"	, s.`edm:rights` as StoryedmRights \r\n" + 
				"	, s.`edm:begin` as StoryedmBegin \r\n" + 
				"	, s.`edm:end` as StoryedmEnd \r\n" + 
				"	, s.ProjectId as StoryProjectId \r\n" + 
				"	, s.Summary as StorySummary \r\n" + 
				"	, s.ParentStory as StoryParentStory \r\n" + 
				"	, s.SearchText as StorySearchText \r\n" + 
				"	, s.DateStart as StoryDateStart \r\n" + 
				"	, s.DateEnd as StoryDateEnd \r\n" + 
				"	, s.OrderIndex as StoryOrderIndex  FROM " +
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
			        ") status  " +
			    "ON i.ItemId = status.CompletionStatusItemId " +
			    "LEFT JOIN ( " +
					"SELECT i.ItemId as TranscriptionStatusItemId" +
					", c.Name as TranscriptionStatusName " + 
					", c.ColorCode as TranscriptionStatusColorCode " + 
			        "FROM CompletionStatus c " +
			        "JOIN Item i " +
			        "ON i.TranscriptionStatusId = c.CompletionStatusId " +
			        ") trStatus  " +
			    "ON i.ItemId = trStatus.TranscriptionStatusItemId " +
			    "LEFT JOIN ( " +
					"SELECT i.ItemId as DescriptionStatusItemId" +
					", c.Name as DescriptionStatusName " + 
					", c.ColorCode as DescriptionStatusColorCode " + 
			        "FROM CompletionStatus c " +
			        "JOIN Item i " +
			        "ON i.DescriptionStatusId = c.CompletionStatusId " +
			        ") deStatus  " +
			    "ON i.ItemId = deStatus.DescriptionStatusItemId " +
			    "LEFT JOIN ( " +
					"SELECT i.ItemId as LocationStatusItemId" +
					", c.Name as LocationStatusName " + 
					", c.ColorCode as LocationStatusColorCode " + 
			        "FROM CompletionStatus c " +
			        "JOIN Item i " +
			        "ON i.LocationStatusId = c.CompletionStatusId " +
			        ") loStatus  " +
			    "ON i.ItemId = loStatus.LocationStatusItemId " +
			    "LEFT JOIN ( " +
					"SELECT i.ItemId as TaggingStatusItemId" +
					", c.Name as TaggingStatusName " + 
					", c.ColorCode as TaggingStatusColorCode " + 
			        "FROM CompletionStatus c " +
			        "JOIN Item i " +
			        "ON i.TaggingStatusId = c.CompletionStatusId " +
			        ") taStatus  " +
			    "ON i.ItemId = taStatus.TaggingStatusItemId " +
			    "LEFT JOIN ( " +
					"SELECT i.ItemId as AutomaticEnrichmentStatusItemId" +
					", c.Name as AutomaticEnrichmentStatusName " + 
					", c.ColorCode as AutomaticEnrichmentStatusColorCode " + 
			        "FROM CompletionStatus c " +
			        "JOIN Item i " +
			        "ON i.AutomaticEnrichmentStatusId = c.CompletionStatusId " +
			        ") auStatus  " +
			    "ON i.ItemId = auStatus.AutomaticEnrichmentStatusItemId " +
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
			"ON i.StoryId = s.StoryId ) a " +
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
	@Path("/{id}")
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
		String query =  "DELETE FROM Item " +
				"WHERE ItemId = " + id;
		String resource = executeQuery(query, "Delete");
		return resource;
	}
	*/
		//Get entry by id
		@Path("/{id}")
		@Produces("application/json;charset=utf-8")
		@GET
		public Response getEntryPost(@PathParam("id") int id) throws SQLException {
			String query =  "SELECT * FROM (" +
					"SELECT \r\n" + 
					"    i.ItemId as ItemId, \r\n" + 
					"    i.Title as Title, \r\n" + 
					"    i.CompletionStatusId as CompletionStatusId, \r\n" + 
					"    i.CompletionStatusName as CompletionStatusName, \r\n" + 
					"    i.CompletionStatusColorCode as CompletionStatusColorCode, \r\n" + 
					"    i.TranscriptionStatusId as TranscriptionStatusId, \r\n" + 
					"    i.TranscriptionStatusName as TranscriptionStatusName, \r\n" + 
					"    i.TranscriptionStatusColorCode as TranscriptionStatusColorCode, \r\n" + 
					"    i.DescriptionStatusId as DescriptionStatusId, \r\n" + 
					"    i.DescriptionStatusName as DescriptionStatusName, \r\n" + 
					"    i.DescriptionStatusColorCode as DescriptionStatusColorCode, \r\n" + 
					"    i.LocationStatusId as LocationStatusId, \r\n" + 
					"    i.LocationStatusName as LocationStatusName, \r\n" + 
					"    i.LocationStatusColorCode as LocationStatusColorCode, \r\n" + 
					"    i.TaggingStatusId as TaggingStatusId, \r\n" + 
					"    i.TaggingStatusName as TaggingStatusName, \r\n" + 
					"    i.TaggingStatusColorCode as TaggingStatusColorCode, \r\n" + 
					"    i.AutomaticEnrichmentStatusId as AutomaticEnrichmentStatusId, \r\n" + 
					"    i.AutomaticEnrichmentStatusName as AutomaticEnrichmentStatusName, \r\n" + 
					"    i.AutomaticEnrichmentStatusColorCode as AutomaticEnrichmentStatusColorCode, \r\n" + 
					"    i.ProjectItemId as ProjectItemId, \r\n" + 
					"    i.Description as Description, \r\n" + 
					"    i.DateStart as DateStart, \r\n" + 
					"    i.DateEnd as DateEnd, \r\n" + 
					"    i.DatasetId as DatasetId, \r\n" + 
					"    i.ImageLink as ImageLink, \r\n" + 
					"    i.OrderIndex as OrderIndex, \r\n" + 
					"    i.Timestamp as Timestamp,\r\n" + 
					"    i.Manifest as Manifest,\r\n" + 
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
					"	, s.`dc:title` as StorydcTitle \r\n" + 
					"	, s.`dc:description` as StorydcDescription \r\n" + 
					"	, s.`edm:landingPage` as StoryedmLandingPage \r\n" + 
					"	, s.ExternalRecordId as StoryExternalRecordId \r\n" + 
					"	, s.PlaceName as StoryPlaceName \r\n" + 
					"	, s.PlaceLatitude as StoryPlaceLatitude \r\n" + 
					"	, s.PlaceLongitude as StoryPlaceLongitude \r\n" + 
					"	, s.PlaceUserGenerated as StoryPlaceUserGenerated \r\n" + 
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
					"	, s.`dc:contributor` as StorydcContributor \r\n" + 
					"	, s.`edm:rights` as StoryedmRights \r\n" + 
					"	, s.`edm:begin` as StoryedmBegin \r\n" + 
					"	, s.`edm:end` as StoryedmEnd \r\n" + 
					"	, s.ProjectId as StoryProjectId \r\n" + 
					"	, s.Summary as StorySummary \r\n" + 
					"	, s.ParentStory as StoryParentStory \r\n" + 
					"	, s.SearchText as StorySearchText \r\n" + 
					"	, s.DateStart as StoryDateStart \r\n" + 
					"	, s.DateEnd as StoryDateEnd \r\n" + 
					"	, s.OrderIndex as StoryOrderIndex  FROM " +
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
				        ") status  " +
				    "ON i.ItemId = status.CompletionStatusItemId " +
				    "LEFT JOIN ( " +
						"SELECT i.ItemId as TranscriptionStatusItemId" +
						", c.Name as TranscriptionStatusName " + 
						", c.ColorCode as TranscriptionStatusColorCode " + 
				        "FROM CompletionStatus c " +
				        "JOIN Item i " +
				        "ON i.TranscriptionStatusId = c.CompletionStatusId " +
				        ") trStatus  " +
				    "ON i.ItemId = trStatus.TranscriptionStatusItemId " +
				    "LEFT JOIN ( " +
						"SELECT i.ItemId as DescriptionStatusItemId" +
						", c.Name as DescriptionStatusName " + 
						", c.ColorCode as DescriptionStatusColorCode " + 
				        "FROM CompletionStatus c " +
				        "JOIN Item i " +
				        "ON i.DescriptionStatusId = c.CompletionStatusId " +
				        ") deStatus  " +
				    "ON i.ItemId = deStatus.DescriptionStatusItemId " +
				    "LEFT JOIN ( " +
						"SELECT i.ItemId as LocationStatusItemId" +
						", c.Name as LocationStatusName " + 
						", c.ColorCode as LocationStatusColorCode " + 
				        "FROM CompletionStatus c " +
				        "JOIN Item i " +
				        "ON i.LocationStatusId = c.CompletionStatusId " +
				        ") loStatus  " +
				    "ON i.ItemId = loStatus.LocationStatusItemId " +
				    "LEFT JOIN ( " +
						"SELECT i.ItemId as TaggingStatusItemId" +
						", c.Name as TaggingStatusName " + 
						", c.ColorCode as TaggingStatusColorCode " + 
				        "FROM CompletionStatus c " +
				        "JOIN Item i " +
				        "ON i.TaggingStatusId = c.CompletionStatusId " +
				        ") taStatus  " +
				    "ON i.ItemId = taStatus.TaggingStatusItemId " +
				    "LEFT JOIN ( " +
						"SELECT i.ItemId as AutomaticEnrichmentStatusItemId" +
						", c.Name as AutomaticEnrichmentStatusName " + 
						", c.ColorCode as AutomaticEnrichmentStatusColorCode " + 
				        "FROM CompletionStatus c " +
				        "JOIN Item i " +
				        "ON i.AutomaticEnrichmentStatusId = c.CompletionStatusId " +
				        ") auStatus  " +
				    "ON i.ItemId = auStatus.AutomaticEnrichmentStatusItemId " +
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
				"ON i.StoryId = s.StoryId ) a " +
				"WHERE ItemId = " + id;
			String resource = executeQuery(query, "Select");
			ResponseBuilder rBuild = Response.ok(resource);
	        return rBuild.build();
		}
}