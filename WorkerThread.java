import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class WorkerThread extends Thread{
	
	private Properties properties;
	private Properties props;
	{ //loading the properties file
		properties = new Properties();
		props = new Properties();
		try {
			properties.load(new FileReader("C:\\Users\\RDRIL\\eclipse-workspace\\CalendarDemo\\src\\Specifications.properties"));
			props.load(new FileReader("C:\\Users\\RDRIL\\eclipse-workspace\\CalendarDemo\\src\\TableContent.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private String filePath;
	WorkerThread(String filePath){
		this.filePath = filePath;
	}
	public void run() {
		System.out.println("Worker Thread started for file "+filePath);
		try {
			//	String valid=null;
			//	String invalid=null;
			File myObj = new File(filePath);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				String[] data = myReader.nextLine().split(",");
				//System.out.println(Arrays.toString(data));
				int fieldLen = Integer.parseInt(properties.getProperty("fieldLength"));
				// System.out.println(fieldLen);
				if(data.length == fieldLen) {

					inspectFurther(data,myObj);

				}else {
					System.out.println("Invalid File");
					myObj.delete();
				}


			} 
			myReader.close();
		}catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}



	}
	private void copyToFile(File myObj, File vfile) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader fileIn = new BufferedReader(new FileReader(myObj));
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(vfile));
		String s;
		while((s = fileIn.readLine())!=null) {
			fileOut.write(s + "\n");
		}
		fileOut.flush();
		System.out.println("copied file from dir1 to dir2");
	}

	private void inspectFurther(String[] data,File myObj) {
		DBService database = new DBService();
		List<String> validKeys = new ArrayList<>();
		List<String> invalidKeys = new ArrayList<>();
		Map<String, String> Valids = new HashMap<>();
		Map<String, String> Invalids = new HashMap<>();
		Valids.put(props.getProperty("Column1"),data[0]);
		Invalids.put(props.getProperty("Column1"), data[0]);
		
		validKeys.add(props.getProperty("Column1"));
		invalidKeys.add(props.getProperty("Column1"));
		System.out.println("Inspecting all the fields");
		/*int fields1 = Integer.parseInt(properties.getProperty("field1"));
			if(data[0].length() == fields1)
		valids.add(data[0]);
		else
			invalids.add(data[0]);
		*/
		int fields2 = Integer.parseInt(properties.getProperty("field2"));
		if(data[1].length() == fields2) {
			//valids.add(data[1]);
			Valids.put(props.getProperty("Column2"),data[1]);
			validKeys.add(props.getProperty("Column2"));
		}
		else {
			//invalids.add(data[1]);
			Invalids.put(props.getProperty("Column2"),data[1]);
			invalidKeys.add(props.getProperty("Column2"));
		}
		
		int fields3 = Integer.parseInt(properties.getProperty("field3"));
		if(data[2].length() == fields3) {
			//valids.add(data[2]);
			Valids.put(props.getProperty("Column3"),data[2]);
			validKeys.add(props.getProperty("Column3"));
		}
		else {
			//invalids.add(data[2]);
			Invalids.put(props.getProperty("Column3"),data[2]);
			invalidKeys.add(props.getProperty("Column3"));
		}

		System.out.println("length of validfields = "+Valids.size());
		System.out.println(" length of invalid fields = "+Invalids.size());
		System.out.println("length if validkeys = "+validKeys.size());
		System.out.println("length of invalid keys = "+invalidKeys.size());
		
		if(Valids.size()>1) {
			File vfile = new File("C:\\Users\\RDRIL\\Directory2"+ "\\"+"valid" +myObj.getName());
			System.out.println(vfile.getName());


			if (!vfile.exists()) {
				try {
					vfile.createNewFile();
				} catch (IOException e) {
					System.out.println("error in file creation");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				copyToFile(myObj,vfile);

					database.tableCreation(vfile,Valids,validKeys);
					database.fieldAddition(vfile,Valids,validKeys);
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error in file copy");
				e.printStackTrace();
			}
		} 
		if(Invalids.size()>1) {
			File ivfile = new File("C:\\Users\\RDRIL\\Directory2"+ "\\"+"invalid" +myObj.getName());
			System.out.println(ivfile.getName());


			if (!ivfile.exists()) {
				try {
					ivfile.createNewFile();
				} catch (IOException e) {
					System.out.println("error in file creation");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			try {
				copyToFile(myObj,ivfile);
				
					database.tableCreation(ivfile,Invalids,invalidKeys);
					database.fieldAddition(ivfile,Invalids,invalidKeys);
					
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("error in file copy");
				e.printStackTrace();
			}

		}
	}
}
