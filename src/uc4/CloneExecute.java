package uc4;

import java.io.IOException;

import com.uc4.api.TaskFilter;
import com.uc4.api.UC4ObjectName;
import com.uc4.api.objects.ExecuteRecurring;
import com.uc4.communication.Connection;
import com.uc4.communication.requests.ActivityList;
import com.uc4.communication.requests.CreateSession;
import com.uc4.communication.requests.ExecuteObject;
import com.uc4.communication.requests.GetExecutePeriod;

public class CloneExecute {
	private Connection sourceCon;
	private Connection destCon;
	private static Config config = new Config("config.properties");

	/*
	 * This example program executes an object in Automic.
	 */
	public CloneExecute(Config config) {
		CreateSession sourceLogin;
		try {
			/*
			 * Connect to the Automic system.
			 */
			sourceCon = Connection.open(config.sourceHost, config.port);
			sourceLogin = sourceCon.login(config.client, config.user, config.department, config.password, config.language);
			if(sourceLogin.isLoginSuccessful()) {
				System.out.println(String.format("Successful logging in using user %s/%s", config.user, config.department));
			} else {
				System.err.println(sourceLogin.getMessageBox().getText());
			}
		} catch (IOException e) {
			/*
			 * Exit the program if it's unable to connect to the Automic system.
			 */
			e.printStackTrace();
			System.exit(1);
		}
		
		CreateSession destLogin;
		try {
			/*
			 * Connect to the Automic system.
			 */
			destCon = Connection.open(config.destHost, config.port);
			destLogin = destCon.login(config.client, config.user, config.department, config.password, config.language);
			if(destLogin.isLoginSuccessful()) {
				System.out.println(String.format("Successful logging in using user %s/%s", config.user, config.department));
			} else {
				System.err.println(destLogin.getMessageBox().getText());
			}
		} catch (IOException e) {
			/*
			 * Exit the program if it's unable to connect to the Automic system.
			 */
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println("CE for Automic 1.0.0");
		if(args.length == 0) {
			System.out.println(String.format("Missing argument: %s", Args.START));
		} else if(args[0].equals(Args.START.toString())) {
			CloneExecute app = new CloneExecute(config);
			
			app.cloneExecute();
			
			app.close();
		} else {
			System.out.println("Invalid argument");
		}
		
	}

	/*
	 * Close the connection to the Automic system.
	 * 
	 * Calling this method assures that the application exits gracefully
	 * and there will be no hanging connections left open.
	 */
	public void close() throws IOException {
		try {
			sourceCon.close();
			destCon.close();
		} catch (IOException e) {
			throw e;
		}
	}
	
	public void cloneExecute() throws IOException {
		/*
		 * Set up filter for tasks that are:
		 * 		- active
		 * 		- type of C_PERIOD
		 */
		TaskFilter taskFilter = new TaskFilter();
		taskFilter.unselectAllObjects();
		taskFilter.unselectAllPlatforms();
		taskFilter.setTypeC_PERIOD(true);
		
		ActivityList activityList = new ActivityList(taskFilter);
		
		try {
			sourceCon.sendRequestAndWait(activityList);
		} catch (IOException e) {
			throw e;
		}
		
		System.out.println(String.format("Found tasks for cloning (%s) -", activityList.size()));	
		activityList.forEach(e -> {
			System.out.println(e.getObjectName());
			GetExecutePeriod period = new GetExecutePeriod(e.getRunID());
			try {
				sourceCon.sendRequestAndWait(period);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ExecuteRecurring rec = period.getExecuteRecurring();
			
			ExecuteObject exe = new ExecuteObject(new UC4ObjectName(e.getObjectName()));
			exe.executeRecurring(rec);
			
			System.out.println(String.format("Starting %s", e.getObjectName()));
			try {
				destCon.sendRequestAndWait(exe);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		});
				
//		System.out.println("TOTAL: " + taskArray.length);
		
	}
}
