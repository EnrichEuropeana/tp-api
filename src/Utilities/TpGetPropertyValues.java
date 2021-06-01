package Utilities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TpGetPropertyValues {
	
			InputStream inputStream;
			String dbUrl = "";
			String user = "";
			String pass = "";
			String secretkey = "";
			String driver = "";
		 
			public String[] getPropValues() throws IOException {
		 
				try {
					Properties prop = new Properties();
					String propFileName = "config.properties";
		 
					inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		 
					if (inputStream != null) {
						prop.load(inputStream);
					} else {
						throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
					}
		 
					 // get the property values
					 dbUrl = prop.getProperty("DB_URL");
					 user = prop.getProperty("USER");
			         pass = prop.getProperty("PASS");
			         secretkey = prop.getProperty("SECRET_KEY");
			         driver = prop.getProperty("DRIVER");
				} catch (Exception e) {
					System.out.println("Exception: " + e);
				} finally {
					inputStream.close();
				}
				return new String[] {dbUrl, user, pass, secretkey, driver};
			}

	}
