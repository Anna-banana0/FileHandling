import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class FileMonitor extends Thread {
	private String inputDir;
	private String inprocessDir;
	private Properties properties;
	private Map<String,Calendar> fileArrivalRecord = new HashMap<String, Calendar>();
	
	{ //loading the properties file
		properties = new Properties();
		try {
			properties.load(new FileReader("ValidFiles.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fileArrivalRecord = recoverArrivalRecord();
	}
	
	FileMonitor(String path, String inprocessFoler){
		this.inputDir = path;
		this.inprocessDir = inprocessFoler;
	}
	private Map<String, Calendar> recoverArrivalRecord() { // serializing
		Map<String,Calendar> recordMap = new HashMap<String, Calendar>();
		FileInputStream fis=null;
		ObjectInputStream ois=null;
		File file = new File("C:\\Users\\RDRIL\\eclipse-workspace\\CalendarDemo\\arrival.ser");
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			
			recordMap= (Map)ois.readObject();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recordMap;		
	}
	//Single Responsibility Principle(SRP)
	public void run() {	
		File dir = new File(this.inputDir);
		while(true) {
			File[] files = dir.listFiles();
			System.out.println(files.length);
			//System.out.println(files.length);
			for(File f : files) {
				if(isValid(f)) {
					System.out.println("Valid File");
					if(isInTime(f)) { 
						System.out.println("In time");
						if(isDuplicate(f)){
							System.out.println("procesing");
					saveOrUpdateArrivalRecord(f.getName(),new GregorianCalendar());
					 //f.renameTo(new File(inprocessDir));
					 File file = new File(inprocessDir + "\\" + f.getName());
					 System.out.println("Working on file "+file.getName());
					 
					 
					  if (!file.exists()) {
						  try {
							file.createNewFile();
						  } catch (IOException e) {
							System.out.println("error in file creation");
							// TODO Auto-generated catch block
							e.printStackTrace();
						  }
					  }
					  
					  
					  //f.renameTo(file.toPath())
					  try {
						  copyToFile(f,file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("error in file copy");
						e.printStackTrace();
					}
					  
					  
					/*File dir2 = new File(this.inprocessDir);
					//System.out.println(dir2.toString());
					File[] fi = dir2.listFiles();
					System.out.println(fi.length);*/
					WorkerThread worker = new WorkerThread(inprocessDir+"\\"+f.getName());
					worker.start();
					}
					}
				}	else {
					System.out.println("File has something wrong");
					f.delete();
				}
			}
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void copyToFile(File file1, File file2) throws IOException {
		//to copy content of file1 to file2
		BufferedReader fileIn = new BufferedReader(new FileReader(file1));
		BufferedWriter fileOut = new BufferedWriter(new FileWriter(file2));
		String s;
		while((s = fileIn.readLine())!=null) {
			fileOut.write(s + "\n");
		}
		fileOut.flush();
	}
	
	private void saveOrUpdateArrivalRecord(String name, GregorianCalendar calendar) {
		this.fileArrivalRecord.put(name, calendar);
		//System.out.println(fileArrivalRecord.toString());
		serializeMap(this.fileArrivalRecord);
	}
	private void serializeMap(Map<String, Calendar> fileArrivalRecord2) {
		FileOutputStream fos=null;
		ObjectOutputStream oos=null;
		try {
			fos = new FileOutputStream("arrival.ser");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(fileArrivalRecord2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private boolean isDuplicate(File f) {
		//System.out.println(f.lastModified());
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(f.lastModified());
		//System.out.println(cal.getTime());
		int earlierArrivalDay = cal.get(Calendar.DAY_OF_MONTH);
		//System.out.println(cal.get(Calendar.DAY_OF_MONTH));
		int today = new GregorianCalendar().get(Calendar.DAY_OF_MONTH);
		//System.out.println(today);
		//System.out.println("Checking for duplicacy!");
		boolean x = earlierArrivalDay==today;
		//System.out.println(x);
		return earlierArrivalDay==today;
	}
	private boolean isInTime(File f) {
		return f.lastModified() < getExpectedTime(f.getName());
	}	
	private boolean isValid(File f) {		
		return this.properties.containsKey(f.getName());
	}
	private long getExpectedTime(String fname) {
		String expectedTimeString = this.properties.getProperty(fname);
		int hour = Integer.parseInt(expectedTimeString.split(":")[0]);
		int min = Integer.parseInt(expectedTimeString.split(":")[1]);
		int second = Integer.parseInt(expectedTimeString.split(":")[2]);
		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		cal.set(Calendar.SECOND, second);
		return cal.getTimeInMillis();
	}

	
	 
}
