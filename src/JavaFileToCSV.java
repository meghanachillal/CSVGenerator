import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;


public class JavaFileToCSV {
	
	static int valid = 0;
	static int invalid = 0;
			

	public void walk( String path ) throws FileNotFoundException {
		
		PrintWriter csvWriter = new PrintWriter(new FileOutputStream(new File("C://Users//megha//OneDrive//Desktop//a00425207_mcda5510//Assignment3//JavaFileToCSV//output//FinalCSVFile.csv"),true));
		
        File root = new File( path );
        File[] list = root.listFiles();
        List
        if (list == null) return;
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                walk(f.getAbsolutePath() );   
                System.out.println( "Dir:" + f.getAbsoluteFile());
            }
            else {
            	 System.out.println( "File:" + f.getAbsoluteFile());  
            	 csvWriter = JavaFileToCSV.simpleCSVParser(f.getAbsolutePath(), csvWriter);
          }
       }  
        
        csvWriter.close();
       
     }   	

	public static PrintWriter simpleCSVParser(String filepath, PrintWriter csvWriter) {
		Reader in;
		boolean firstRecord = false;
		try {
			in = new FileReader(filepath);
			Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
			for (CSVRecord record : records) {	
				StringBuilder sb = new StringBuilder();
				if (firstRecord) {
				    String firstName = record.get(0);
				    String lastName = record.get(1);
				    String streetNumber = record.get(2);
				    String street = record.get(3);
				    String city = record.get(4);
				    String province = record.get(5);
				    String postalCode = record.get(6);
				    String country = record.get(7);
				    String phoneNumber = record.get(8);
				    String emailAddress = record.get(9);	
				    
				   if (!firstName.isEmpty()&&!lastName.isEmpty()&&!streetNumber.isEmpty()&&!street.isEmpty()&&
				    			!city.isEmpty()&&!province.isEmpty()&&!country.isEmpty()&&!postalCode.isEmpty()&&
                            !phoneNumber.isEmpty()&&!emailAddress.isEmpty())
				   {
                       valid++;
				   }
				   else
				   {
                       invalid++;
				   }
				   
				   sb.append('\n');
				   csvWriter.write(sb.toString());
				   csvWriter.write(record.toString());
				   }  
				
			       firstRecord = true;
						    
			}			
		
			} catch ( IOException e) {
			e.printStackTrace();
		}
		return csvWriter;
		
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub		
		JavaFileToCSV fw = new JavaFileToCSV();
		long startTime = System.currentTimeMillis(); 

		String inputDir;
		
		if ((args.length == 0) || args[0]==null){
			inputDir= "C:/Users/megha/OneDrive/Desktop/a00425207_mcda5510/Assignment3/JavaFileToCSV/CSVFILES";
		}else{
			inputDir= args[0];
		}
		
        fw.walk(inputDir);
        
        Logger logger = Logger.getLogger("ErrorLogs");  
	    FileHandler fileHandler;  
        try {
        	
        	fileHandler = new FileHandler("C://Users//megha//OneDrive//Desktop//a00425207_mcda5510//Assignment3//JavaFileToCSV//output//Errorlogs.txt");
			logger.addHandler(fileHandler);
			SimpleFormatter formatter = new SimpleFormatter();  
			fileHandler.setFormatter(formatter);		
	        
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
                
        long endTime = System.currentTimeMillis();
        
        logger.info("Total execution time: " + (endTime - startTime) +" ms");
		logger.info("Total number of valid rows: " + valid);
		logger.info("Total number of invalid rows: " + invalid);
	
	}
	

	private List<File> getFilteredList(File[] list) {

		
		List<File> filteredFiles = new ArrayList<File>();

		// Per directory get the latest.. to all files
		// take the latest and go into one of the 2 parsing methods

		SortedMap<Date, File> filesByDate = new TreeMap<Date, File>();
		
		for (File f : list) {
			if (f.isDirectory()) {
				filteredFiles.add(f);
			}

			Date date = getDateFromFileName(f);
			if(date!=null){
				filesByDate.put(date, f);
			}
		}

		if (!filesByDate.isEmpty()) {
			Date firstDate = filesByDate.firstKey();

			filteredFiles.add(filesByDate.get(firstDate));

		}
		return filteredFiles;
	}

	private Date getDateFromFileName(File f) {

		String dateAsString = null;
		Date date = null;		
		
		try{		

			String fileName = f.getName();
			
			if (f.getName().indexOf("repo") == -1){
			
				if (fileName.endsWith(".xlsx")) {
					int endIndex = fileName.lastIndexOf("_inv.xls");
					int startIndex = fileName.indexOf("_", endIndex - 9); // _YY MM DD = (9 characters)
					if ((endIndex != -1) && (endIndex != -1)) {
						dateAsString = fileName.substring(startIndex + 1, endIndex);
						try {
	
							date = XLSDateformatter.parse(dateAsString);
	
						} catch (ParseException e) {
							getLogger().log(Level.WARNING,"ParseException from date '"+dateAsString+"' from file: " + f.getAbsolutePath().toString());
							e.printStackTrace();  //TODO S:\A4121016\Pk Def\Banked Bio\Inv\A4121016_Banked Biospec (Prep D1) Blood_Roster_QA_ 15 08 05_inv.xls
						}
	
					}
				} else if (fileName.endsWith(".csv")) {
					int endIndex = fileName.lastIndexOf("_inv.csv");
					int startIndex = fileName.indexOf("_", endIndex - 8); // 24aug15 ( 8 characters )
					if ((startIndex != -1) && (endIndex != -1)) {
						//System.out.println("Found date for" + fileName);
	
						dateAsString = fileName.substring(startIndex + 1, endIndex);
						try {
	
							date = CSVDateformatter.parse(dateAsString);
	
						} catch (ParseException e) {
							getLogger().log(Level.WARNING,"ParseException from date '"+dateAsString+"' from file: " + f.getAbsolutePath().toString());
						}
					}
				}
			}else{
				getLogger().log(Level.INFO, "Skipping repo csv file in getDateFromFileName: " + f.getAbsoluteFile());
				
			}
		} catch (Exception e) {
			// catchAll
			getLogger().log(Level.WARNING,"Failed to get date from filename: " + f.getAbsolutePath().toString());
			e.printStackTrace();
		}

		return date;
	}

}

