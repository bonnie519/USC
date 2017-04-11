/**
**author:Bei Gao
**USC ID:6863049908
**InvertedIndx.java
**Assignment 3
**/

import java.io.IOException;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class InvertedIndx {

  public static class InvertedMapper 
       extends Mapper<Object, Text, Text, Text>{
    
    private Text word = new Text();
    private Text valueInfo = new Text();
    
	@Override
	public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
	  //get the content of file by splitting the tab after the docID
	  String[] content = value.toString().trim().split("\t");
	  //get the docID
	  String docID = content[0].trim();
	  
	  //real content of file
      StringTokenizer itr = new StringTokenizer(content[1]);
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());//word
		valueInfo.set(docID);//docID
        context.write(word, valueInfo);
      }
    }
  }
  
  public static class InvertedReducer 
       extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();

   @Override  
	public void reduce (Text key,Iterable<Text> values,Context context)throws IOException, InterruptedException  {  
		StringBuilder sb = new StringBuilder();
		
		//hashmap used to count the same docIDs
	    Map<String,Integer> hs = new HashMap<String,Integer>();
		for(Text value : values) {  
			String ts = value.toString().trim();
			int cnt = 0;
			if(hs.containsKey(ts))//docID already exists in hashmap
				cnt = hs.get(ts);
			hs.put(ts,cnt+1);
		}
		for(String docId: hs.keySet())
		{//concat the results, StringBuilder is more efficient than String for frequent concat operations
			sb.append(docId).append(':').append(hs.get(docId)).append("\t");  
		}			
		result.set(sb.toString());
		context.write(key, result);  
	}
  }

  public static void main(String[] args) throws Exception {
    if(args.length !=2)
    {
      System.err.println("Usage: inverse <in> <out>");
      System.exit(-1);
    }
    Job job = new Job();
    job.setJarByClass(InvertedIndx.class);//main class
	job.setJobName("Inverted Index");
	FileInputFormat.addInputPath(job,new Path(args[0]));//input path
	FileOutputFormat.setOutputPath(job,new Path(args[1]));//output path
    job.setMapperClass(InvertedMapper.class);//mapper class
    job.setReducerClass(InvertedReducer.class);//reducer class
    job.setOutputKeyClass(Text.class);//output key: 'word'
    job.setOutputValueClass(Text.class);//output value: 'docID:count\t....'

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
