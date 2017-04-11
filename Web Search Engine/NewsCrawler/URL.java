import java.util.ArrayList;
import java.util.List;

public class URL {
	public List<String> outgoingURLs;
	public String url;
	public int statusCode;
	public String extension;
	public String type;
	public int filesize;
	public String isOK;
	public URL(String url, int statusCode)
	{
		this.url = url;
		this.statusCode = statusCode;
	}
	
	public URL(String url, int filesize, List<String> outgoingURLs,String type, String extension)
	{
		this.url = url;
		this.outgoingURLs = new ArrayList<String>(outgoingURLs);
		this.filesize = filesize;
		this.type = type;
		this.extension = extension;
	}
	
	public URL(String url, String isOK)
	{
		this.url = url;
		this.isOK = isOK;
	}
}
