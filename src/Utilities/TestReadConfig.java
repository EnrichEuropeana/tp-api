package Utilities;

import java.io.IOException;
import java.util.Properties;

public class TestReadConfig {

	public static void main(String[] args) {
		TpGetPropertyValues prop = new TpGetPropertyValues();
		try {
			prop.getPropValues();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
