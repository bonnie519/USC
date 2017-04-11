import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class LocalDataCollectorCrawler extends WebCrawler {
    private static final Logger logger = LoggerFactory.getLogger(LocalDataCollectorCrawler.class);

    private static final Pattern myPattern = Pattern.compile(
        ".*(\\.(doc|pdf|bmp|gif|jpe?g|png|tiff?))$");
    
    private static final Pattern imgPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?))$");
    private static Map<String,Boolean> hash = new HashMap<String,Boolean>();
    CrawlStat myCrawlStat;

    public LocalDataCollectorCrawler() {
        myCrawlStat = new CrawlStat();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        //myCrawlStat.incDiscoveredPages();
        String isOK ="N_OK";
        /*PageFetchResult fetchResult = null;
        CrawlConfig config = new  CrawlConfig();
        PageFetcher pageFetcher = new PageFetcher(config);
        try {
			fetchResult = pageFetcher.fetchPage(url);
		} catch (InterruptedException | IOException | PageBiggerThanMaxSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        //System.out.println("status code: "+fetchResult.getStatusCode());
       // myCrawlStat.addStatusCode(fetchResult.getStatusCode());
        
		// 遍历目标域名 指定目标爬虫域名
		//urls_NewsSite.csv  OK N_OK
        if (href.startsWith("http://www.latimes.com/"))
		{	
        	isOK ="OK";
        	myCrawlStat.discoveredURLs.add(new URL(href,isOK));
        	return true;
        }//within newssite ok, should visit
        else
        {
        	myCrawlStat.discoveredURLs.add(new URL(href,isOK));
        	if(myPattern.matcher(href).matches()) return true;
        	//if pattern matches, should visit
        	else return false;//
        }
		 
		//return false;
        //return imgPatterns.matcher(href).matches() && href.startsWith("http://www.latimes.com/");
    }

    @Override
    public void visit(Page page) {
        logger.info("Visited: {}", page.getWebURL().getURL());
        String url = page.getWebURL().getURL();
        
        List<String> outgoingURLs = new ArrayList<String>();
        String contentType = page.getContentType().split(";")[0];
        URL urlinfo;
        if(!hash.containsKey(url))
        {	hash.put(url,true);
        	myCrawlStat.incTotalUniquePages();
        }
        myCrawlStat.incTotalSuccessFetches();
        //myCrawlStat.incProcessedPages();
        
        String extension = "html";
        if (!(page.getParseData() instanceof HtmlParseData)) {
        	extension = url.substring(url.lastIndexOf('.')+1);
        	if(extension.equals("jpg")){myCrawlStat.incTotalJpgs();}
        	if(extension.equals("png")){myCrawlStat.incTotalPngs();}
        	if(extension.equals("pdf")){myCrawlStat.incTotalPdfs();}
        }
        //System.out.println("\ncontent type: "+extension);
        //System.out.println("file size： "+page.getContentData().length/1024+"KB");
        if (contentType.equals("text/html")) { // html
        	if (page.getParseData() instanceof HtmlParseData) {
	            HtmlParseData parseData = (HtmlParseData) page.getParseData();
	            Set<WebURL> links = parseData.getOutgoingUrls();
	            myCrawlStat.incTotalLinks(links.size());
	            for (WebURL link : links) {
	                outgoingURLs.add(link.getURL());
	            }
	            urlinfo = new URL(url,page.getContentData().length,outgoingURLs,"text/html",".html");//, page.getContentData().length, , "text/html", ".html");
	            myCrawlStat.visitedURLs.add(urlinfo);
            
        	}
        	/*
        	 * else {
                urlInfo = new UrlInfo(url, page.getContentData().length, outgoingUrls, "text/html", ".html");
                crawlState.visitedUrls.add(urlInfo);
            }
        	 * */
        	
        }
        else if (contentType.equals("application/msword")) { // doc
            urlinfo = new URL(url, page.getContentData().length, outgoingURLs, "application/msword", ".doc");
            myCrawlStat.visitedURLs.add(urlinfo);
        } else if (contentType.equals("application/pdf")) { // pdf
            urlinfo = new URL(url, page.getContentData().length, outgoingURLs, "application/pdf", ".pdf");
            myCrawlStat.visitedURLs.add(urlinfo);
        }
        if(page.getParseData() instanceof BinaryParseData &&
        		(imgPatterns.matcher(url).matches()))//image
        {
        	extension = url.substring(url.lastIndexOf('.')+1);
        	urlinfo = new URL(url, page.getContentData().length, outgoingURLs, "image/"+extension, "."+extension);
            myCrawlStat.visitedURLs.add(urlinfo);
            System.out.println("image: "+extension);
        }
        // We dump this crawler statistics after processing every 50 pages
        //if ((myCrawlStat.getTotalProcessedPages() % 50) == 0) {
          //  dumpMyData();
        //}
    }

    /**
     * This function is called by controller to get the local data of this crawler when job is
     * finished
     */
    @Override
    public Object getMyLocalData() {
        return myCrawlStat;
    }

    /**
     * This function is called by controller before finishing the job.
     * You can put whatever stuff you need here.
     */
    //@Override
    //public void onBeforeExit() {
    	//dumpMyData();
		
    //}
    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        myCrawlStat.incAttemptedPages();
    	myCrawlStat.addStatusCode(statusCode);
    	myCrawlStat.attemptedURLs.add(new URL(webUrl.getURL(),statusCode));
	}
    /*public void dumpMyData() {
        int id = getMyId();
        
        //downloader.processUrl("http://en.wikipedia.org/wiki/Main_Page/");
        //downloader.processUrl("http://www.latimes.com/");
        String fileName ="e:/samplecrawler/mydatastat.txt";
        // You can configure the log to output to file
        FileWriter out =null;
    	BufferedWriter writer = null;
    	try{
    		out=new FileWriter(fileName);
            writer = new BufferedWriter(out);   		
    		
    		writer.write("Crawler "+id+" > Processed Pages: "+ myCrawlStat.getTotalProcessedPages());
    		writer.newLine();
    		writer.write("Crawler "+id+" > Total Links Found: "+ myCrawlStat.getTotalLinks());
    		writer.newLine();
    		writer.write("Crawler "+id+" > Total Text Size Found: " + myCrawlStat.getTotalTextSize());
    		//writer.newLine();
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
        //logger.info("Crawler {} > Processed Pages: {}", id, myCrawlStat.getTotalProcessedPages());
        //logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
        //logger.info("Crawler {} > Total Links Found: {}", id, myCrawlStat.getTotalLinks());
    //}
}