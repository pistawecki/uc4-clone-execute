package uc4;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
	public String sourceHost;
	public String destHost;
	public int port;
	public int client;
	public String user;
	public String department;
	public String password;
	public char language;
	
	public Config(String fileName) {
		/*
		 * Read the configuration file and exit the program if it does not exist or is wrongly formatted.
		 */
		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream(fileName);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			// System.getProperty("user.dir");
			System.err.println("Config file not found.");
			System.exit(1);
		} catch(IOException e) {
			System.err.println("Error reading config file.");
			System.exit(1);
		}
		
		this.sourceHost = prop.getProperty("sourceHost");
		this.destHost = prop.getProperty("destHost");
		this.port = Integer.parseInt(prop.getProperty("port"));
		this.client = Integer.parseInt(prop.getProperty("client"));
		this.user = prop.getProperty("user");
		this.department = prop.getProperty("department");
		this.password = prop.getProperty("password");
		this.language = prop.getProperty("language").charAt(0);
	}
	
}
