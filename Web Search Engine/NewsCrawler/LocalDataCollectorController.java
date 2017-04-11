import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.io.*;
import com.opencsv.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class LocalDataCollectorController {
    private static final Logger logger =
        LoggerFactory.getLogger(LocalDataCollectorController.class);
    public static final String name = "Bei Gao";
    public static final String ID = "6863049908";
    public static final String NewsSite = "latimes.com";
    
    public static void writeFetchCSV(String crawlStorageFolder, String mywebsite, CrawlStat sumState) throws Exception {
        String fileName = crawlStorageFolder + "/fetch_"+mywebsite+".csv";
        FileWriter writer = new FileWriter(fileName);
        writer.append("URL,HTTP Status\n");
        
        for (URL info : sumState.attemptedURLs) {
            writer.append(info.url + "," + info.statusCode + "\n");
        }
        writer.flush();
        writer.close();
        
        //CSVWriter csvWriter = new CSVWriter(writer,',');
		//String[] strs = {"abc" , "abc" , "abc"};  
        //csvWriter.writeNext(strs);  
        //csvWriter.close();
    }
    
    public static void writeVisitCSV(String crawlStorageFolder, String mywebsite,CrawlStat sumState) throws Exception {
        String fileName = crawlStorageFolder + "/visit_"+mywebsite+".csv";
        FileWriter writer = new FileWriter(fileName);
        writer.append("URL,Size,OutLinks#,ContentType\n");
        for (URL info : sumState.visitedURLs) {
            if (info.type != "unknown") {
                writer.append(info.url + "," + info.filesize + "," + info.outgoingURLs.size() + "," + info.type + "\n");
            }
        }
        writer.flush();
        writer.close();
    }

    public static void writeUrlsCSV(String crawlStorageFolder, String mywebsite, CrawlStat sumState) throws Exception {
        String fileName = crawlStorageFolder + "/urls_"+mywebsite+".csv";
        FileWriter writer = new FileWriter(fileName);
        writer.append("URL,Type\n");
        for (URL info : sumState.discoveredURLs) {
            writer.append(info.url + "," + info.isOK + "\n");
        }
        writer.flush();
        writer.close();
    }
    
    public static void writeStatisticsTXT(String crawlStorageFolder, String mywebsite, CrawlStat Stat) throws Exception {
        String fileName = crawlStorageFolder + "/CrawlReport_"+mywebsite+".txt";
        FileWriter writer = new FileWriter(fileName);

        // Personal Info
        writer.append("Name: " + name + "\n");
        writer.append("USC ID: " + ID + "\n");
        writer.append("News site crawled: " + NewsSite + "\n");
        writer.append("\n");

        // Fetch Statistics
        writer.append("Fetch Statistics\n");
        writer.append("================\n");
        
        writer.append("# fetches attempted: " + Stat.attemptedURLs.size() + "\n");
        writer.append("# fetched succeeded: " + Stat.visitedURLs.size() + "\n");

        // get failed url and aborted urls
        int failedUrlsCount = 0;
        int abortedUrlsCount = 0;
        for (URL info : Stat.attemptedURLs) {
            if (info.statusCode >= 300 && info.statusCode < 400) {
                abortedUrlsCount++;
            } else if (info.statusCode != 200) {
                failedUrlsCount++;
            }
        }

        writer.append("# fetched aborted: " + abortedUrlsCount + "\n");
        writer.append("# fetched failed: " + failedUrlsCount + "\n");
        writer.append("\n");

        // Outgoing URLS
        HashSet<String> hashSet = new HashSet<String>();
        int uniqueUrls = 0;
        int withinUrls = 0;
        int outUrls = 0;
        writer.append("Outgoing URLs:\n"+
        			  "==============\n");
		writer.append("Total URLS extracted: " + Stat.discoveredURLs.size() + "\n");
        for (URL info : Stat.discoveredURLs) {
            if (!hashSet.contains(info.url)) {
                hashSet.add(info.url);
                uniqueUrls++;
                if (info.isOK.equals("OK")) {
                    withinUrls++;
                } 
                else {
                    outUrls++;
                }
            }
        }
        writer.append("# unique URLs extracted: " + uniqueUrls + "\n");
        writer.append("# unique URLs within News Site: " + withinUrls + "\n");
        writer.append("# unique USC URLs outside News Site: " + outUrls + "\n");
        writer.append("\n");

        // Status Code
        writer.append("Status Codes:\n"+
        			  "=============\n");
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        for (URL info : Stat.attemptedURLs) {
            if (hashMap.containsKey(info.statusCode)) {
                hashMap.put(info.statusCode, hashMap.get(info.statusCode) + 1);
            } else {
                hashMap.put(info.statusCode, 1);
            }
        }
        HashMap<Integer, String> statusCodeMapping = new HashMap<Integer, String>();
        statusCodeMapping.put(200, "OK");
        statusCodeMapping.put(301, "Moved Permanently");
        statusCodeMapping.put(302, "Found");
        statusCodeMapping.put(401, "Unauthorized");
        statusCodeMapping.put(403, "Forbidden");
        statusCodeMapping.put(404, "Not Found");
        statusCodeMapping.put(405, "Method Not Allowed");
        statusCodeMapping.put(500, "Internal Server Error");

        for (Integer key : hashMap.keySet()) {
            writer.append("" + key + " " + statusCodeMapping.get(key) + ": " + hashMap.get(key) + "\n");
        }
        writer.append("\n");

        // File Size
        writer.append("File Sizes:\n"+
        			  "===========\n");
        int oneKB = 0;
        int tenKB = 0;
        int hunKB = 0;
        int oneMB = 0;
        int bigger = 0;
        for (URL info : Stat.visitedURLs) {
            if (info.filesize < 1024) {
                oneKB++;
            } else if (info.filesize < 10240) {
                tenKB++;
            } else if (info.filesize < 102400) {
                hunKB++;
            } else if (info.filesize < 1024 * 1024) {
                oneMB++;
            } else {
                bigger++;
            }
        }
        writer.append("< 1KB: " + oneKB + "\n");
        writer.append("1KB ~ <10KB: " + tenKB + "\n");
        writer.append("10KB ~ <100KB: " + hunKB + "\n");
        writer.append("100KB ~ <1MB: " + oneMB + "\n");
        writer.append(">= 1MB: " + bigger + "\n");
        writer.append("\n");

        // Content Types
        HashMap<String, Integer> hashCT = new HashMap<String, Integer>();
        writer.append("Content Types:\n"+
        			  "==============\n");
        for (URL info : Stat.visitedURLs) {
            if (info.type.equals("unknown")) {
                continue;
            }
            if (hashCT.containsKey(info.type)) {
                hashCT.put(info.type, hashCT.get(info.type) + 1);
            } else {
                hashCT.put(info.type, 1);
            }
        }
        for (String key : hashCT.keySet()) {
            writer.append("" + key + ": " + hashCT.get(key) + "\n");
        }
        writer.append("\n");

        writer.flush();
        writer.close();
    }
    
    public static void main(String[] args) throws Exception {
        /*if (args.length != 2) {
            logger.info("Needed parameters: ");
            logger.info("\t rootFolder (it will contain intermediate crawl data)");
            logger.info("\t numberOfCralwers (number of concurrent threads)");
            return;
        }*/

        String rootFolder = "e:/samplecrawler";//args[0];
        int numberOfCrawlers = 7;//Integer.parseInt(args[1]);

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(rootFolder);
        config.setMaxPagesToFetch(2000);
        config.setMaxDepthOfCrawling(16);
        //config.setPolitenessDelay(1000);
        config.setIncludeBinaryContentInCrawling(true);
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

        controller.addSeed("http://www.latimes.com/");
        controller.start(LocalDataCollectorCrawler.class, numberOfCrawlers);

        List<Object> crawlersLocalData = controller.getCrawlersLocalData();
        int totalJpgs = 0;
        int totalPngs = 0;
        int totalPdfs = 0;
        CrawlStat Stat = new CrawlStat();
        Map<Integer,Integer> statuscode = new HashMap<Integer,Integer>();
        for (Object localData : crawlersLocalData) {
            CrawlStat stat = (CrawlStat) localData;
            
            Stat.attemptedURLs.addAll(stat.attemptedURLs);
            Stat.visitedURLs.addAll(stat.visitedURLs);
            Stat.discoveredURLs.addAll(stat.discoveredURLs);
            /*totalLinks += stat.getTotalLinks();
            totalTextSize += stat.getTotalTextSize();
            totalProcessedPages += stat.getTotalAttemptedPages();
            totalUniquePages += stat.getTotalUniquePages();
            totalSuccessFetches += stat.getTotalSuccessFetches();
            totalOutWebsiteURLs += stat.getTotalOutWebsiteURLs();
            totalInWebsiteURLs += stat.getTotalWithinWebsiteURLs();*/
            totalJpgs+=stat.getTotalJpgs();
            totalPngs+=stat.getTotalPngs();
            totalPdfs+=stat.getTotalPdfs();
            for (Map.Entry<Integer, Integer> entry : stat.getStatusCode().entrySet()) {  
            	  
               // System.out.println("Key = " + entry.getKey() + 
                //		", Value = " + entry.getValue());  
              statuscode.put(entry.getKey(),entry.getValue());
            }  
        }
        
        writeFetchCSV(rootFolder,"LATimes",Stat);
        writeVisitCSV(rootFolder,"LATimes",Stat);
        writeUrlsCSV(rootFolder,"LATimes",Stat);
        
        writeStatisticsTXT(rootFolder,"LATimes",Stat);
        /*String fileName ="e:/samplecrawler/myfinaldatastat.txt";
        // You can configure the log to output to file
        FileWriter out =null;
    	BufferedWriter writer = null;
    	try{
    		out=new FileWriter(fileName);
            writer = new BufferedWriter(out);   		
    		
    		writer.write("Attempted Pages: "+ totalProcessedPages);
    		writer.newLine();
    		writer.write("Unique Pages: "+ totalUniquePages);
    		writer.newLine();
    		writer.write("Total Links Found: "+ totalLinks);
    		writer.newLine();
    		writer.write("Total Text Size Found: " + totalTextSize);
    		writer.newLine();
    		writer.write("Total Success Fetches: " + totalSuccessFetches);
    		writer.newLine();
    		writer.write("Total Outside URLs: " + totalOutWebsiteURLs);
    		writer.newLine();
    		writer.write("Total Inside URLs: " + totalInWebsiteURLs);
    		writer.newLine();
    		writer.write("Total Jpgs: " + totalJpgs);
    		writer.newLine();
    		writer.write("Total Pngs: " + totalPngs);
    		writer.newLine();
    		writer.write("Total Pdfs: " + totalPdfs);
    		
    		for (Map.Entry<Integer, Integer> entry : statuscode.entrySet()) {  
    			writer.newLine();
    			writer.write(entry.getKey()+": "+entry.getValue());  
    		    //System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
    		  
    		}  
    		writer.flush();
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	finally{
    		try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}*/
        /*logger.info("Aggregated Statistics:");
        logger.info("\tProcessed Pages: {}", totalProcessedPages);
        logger.info("\tTotal Links found: {}", totalLinks);
        logger.info("\tTotal Text Size: {}", totalTextSize);*/
    }
}