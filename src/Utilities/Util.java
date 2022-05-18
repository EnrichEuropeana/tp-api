package Utilities;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Util {
	public static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
	  ResultSetMetaData rsmd = rs.getMetaData();
	  int columns = rsmd.getColumnCount();
	  for (int x = 1; x <= columns; x++) {
	    if (columnName.equals(rsmd.getColumnName(x))) {
	      return true;
	    }
	  }
	  return false;
	}

	public static boolean isValidManifestUrl(String maniftestUrlString) throws MalformedURLException {
		// assume it is manifest by name
		if (maniftestUrlString.endsWith("manifest.json")) {
			return true;
		}

		// or then by occurence of @type = sc:Manifest in content
		URL url = new URL(maniftestUrlString);
		HttpURLConnection con = null;

		try {
			con = (HttpURLConnection)	url.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(30000);
			con.setReadTimeout(30000);

			int status = con.getResponseCode();

	 		if (status > 300) {
				return false;
	 		}

	 		try (
				final BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "UTF-8")
	 			)
	 		) {
				String inputLine;
				StringBuffer content = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					content.append(inputLine);
				}

				JsonObject manifest = new JsonParser().parse(content.toString()).getAsJsonObject();

				if (manifest.get("@type").getAsString().equals("sc:Manifest")) {
					return true;
				}
			}
		} catch (Exception e) {
	  	LogFactory.getLog(Util.class).error("Error: ", e);
		}

		return false;
	}

	public static JsonObject findNodeByIdValue (String IdValue, JsonArray dataArray) {
		JsonObject filtered = new JsonObject();
		int size = dataArray.size();

		for (int i = 0; i < size; i++) {
			JsonObject obj = dataArray.get(i).getAsJsonObject();
			String id = obj.get("@id").getAsString();
			if (id.equals(IdValue)) {
				filtered = obj;
				break;
			}
    }

    return filtered;
	}
}
