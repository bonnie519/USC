import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrawlStat {
    private long totalLinks;
    private int totalUniquePages;
    private int totalAttemptedPages;
    List<URL> visitedURLs;
    List<URL> attemptedURLs;
    List<URL> discoveredURLs;
    private long totalTextSize;
    private int totalSuccessFetches;
    private int totalWithinWebsiteURLs;
    private int totalOutWebsiteURLs;
    private int totalJpgs;
    private int totalPngs;
    private int totalPdfs;
    Map<Integer,Integer> statuscode = new HashMap<Integer,Integer>();
    
    public CrawlStat()
    {
    	visitedURLs = new ArrayList<URL>();
    	attemptedURLs = new ArrayList<URL>();
    	discoveredURLs = new ArrayList<URL>();
    }
    public long getTotalLinks() {
        return totalLinks;
    }

    public int getTotalAttemptedPages() {
        return totalAttemptedPages;
    }

    public long getTotalTextSize() {
        return totalTextSize;
    }

    public void incAttemptedPages() {
        this.totalAttemptedPages++;
    }

    public void incTotalLinks(int count) {
        this.totalLinks += count;
    }

    public void incTotalTextSize(int count) {
        this.totalTextSize += count;
    }

    public void setTotalLinks(long totalLinks) {
        this.totalLinks = totalLinks;
    }

    public void setTotalAttemptedPages(int totalAttemptedPages) {
        this.totalAttemptedPages = totalAttemptedPages;
    }

    public void setTotalTextSize(long totalTextSize) {
        this.totalTextSize = totalTextSize;
    }

	public void incTotalSuccessFetches() {
		// TODO Auto-generated method stub
		this.totalSuccessFetches++;
	}

	public int getTotalSuccessFetches() {
		return totalSuccessFetches;
	}

	public void setTotalSuccessFetches(int totalSuccessFetches) {
		this.totalSuccessFetches = totalSuccessFetches;
	}

	public int getTotalWithinWebsiteURLs() {
		return totalWithinWebsiteURLs;
	}

	public void setTotalWithinWebsiteURLs(int totalWithinWebsiteURLs) {
		this.totalWithinWebsiteURLs = totalWithinWebsiteURLs;
	}
	
	public void incTotalWithinWebsiteURLs() {
		// TODO Auto-generated method stub
		this.totalWithinWebsiteURLs++;
	}
	public int getTotalOutWebsiteURLs() {
		return totalOutWebsiteURLs;
	}

	public void setTotalOutWebsiteURLs(int totalOutWebsiteURLs) {
		this.totalOutWebsiteURLs = totalOutWebsiteURLs;
	}
	
	public void incTotalOutWebsiteURLs() {
		this.totalOutWebsiteURLs++;
	}

	public int getTotalUniquePages() {
		return totalUniquePages;
	}

	public void setTotalUniquePages(int totalUniquePages) {
		this.totalUniquePages = totalUniquePages;
	}
	
	public void incTotalUniquePages() {
		this.totalUniquePages ++;
	}

	public int getTotalJpgs() {
		return totalJpgs;
	}

	public void setTotalJpgs(int totalJpgs) {
		this.totalJpgs = totalJpgs;
	}
	public void incTotalJpgs() {
		this.totalJpgs ++;
	}
	public int getTotalPngs() {
		return totalPngs;
	}

	public void setTotalPngs(int totalPngs) {
		this.totalPngs = totalPngs;
	}
	public void incTotalPngs() {
		this.totalPngs ++;
	}

	public int getTotalPdfs() {
		return totalPdfs;
	}

	public void setTotalPdfs(int totalPdfs) {
		this.totalPdfs = totalPdfs;
	}
	
	public void incTotalPdfs() {
		this.totalPdfs ++;
	}
	public void addStatusCode(int status)
	{
		if(this.statuscode.containsKey(status))
			this.statuscode.put(status, this.statuscode.get(status)+1);
		else
			this.statuscode.put(status, 1);
	}
	public Map<Integer,Integer> getStatusCode()
	{
		return this.statuscode;
	}
}