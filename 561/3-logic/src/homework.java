
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class homework {
	static int NQ=0;
	static int NS=0;
	List<String>  quiries = new ArrayList<String>();
	List<String>  ans = new ArrayList<String>();
	class CNF{
		String exp;//cnf表达式
		String op;//cnf的最低优先级的操作符(与、或)
		public CNF(String exp, String op)
		{
			this.exp = exp;
			this.op = op;
		}
	}
	private int findT(List<String>sentence,String target)
	{
		int i=0;
		for(;i<sentence.size();i++)
		{
			if(sentence.get(i).equals(target))
				return i;
		}
		return -1;
	}
	private boolean isLetter(char c)
	{
		if(c>='a'&&c<='z'||(c>='A'&&c<='Z'))
			return true;
		else return false;
	}
	public List<String> toList(String s)
	{
		List<String> ls=new ArrayList<String>();
		int i=0;
		s=s.trim();
		if(s.charAt(0)!='('){ ls.add(s); return ls;}
		ls.add("(");
		i=1;
		while(i<s.length())
		{
			if(s.charAt(i)==' '){i=i+1;}
			if(s.charAt(i)=='('&&( !isLetter(s.charAt(i-1)) ))
			{
				ls.add("(");i=i+1;
			}
			else if(s.charAt(i)=='|'||s.charAt(i)=='&'||s.charAt(i)=='~')
			{	ls.add(s.substring(i,i+1));i=i+1;}
			else if(s.charAt(i)=='='&&s.charAt(i+1)=='>')
			{	ls.add("=>"); i=i+2;}
			else if(s.charAt(i)>='A'&&s.charAt(i)<='Z')
			{
				String p="";//s.substring(i,i+1);
				int indx =i;
				while(s.charAt(indx)!='(') indx++;
				while(s.charAt(indx)!=')') indx++;
				p=s.substring(i,indx+1);
				ls.add(p);
				i=indx+1;
			}
			else if(s.charAt(i)==')'&&( !isLetter(s.charAt(i-1)) ))
			{
				ls.add(")");
				i=i+1;
			}
		}
		return ls;
	}
	public void EImply(List<String> sentence, int start, int end)
	//处理掉=>
	{
		//System.out.println(start+"--"+end);
		int i=0,left=start+1,right = end-1,indx=0,power=0;
		if (!sentence.get(start).equals("("))
			return ;
		//System.out.println(sentence.get(start+1)+".."+sentence.get(end-1));
		if(!sentence.get(start+1).equals("(")&&(!sentence.get(end-1).equals(")")))
		{	//! !
			//System.out.println("...");
			indx = findT(sentence,"=>");
			if(indx>=0)
			{	sentence.set(indx, "|");
				sentence.set(start+1,"~"+sentence.get(start+1));//~xx
			}
			return;
		}
		else if(sentence.get(start+1).equals("(")&&(!sentence.get(end-1).equals(")")))
		{//= !
			right =end-2;
			for(;!sentence.get(right).equals(")");right--);
			if(sentence.get(right+1).equals("=>"))
			{	
				sentence.set(right+1, "|");
				sentence.add(start+1,"~");//~(xxx)
				EImply(sentence,start+2,right+1);//
			}
			else
				EImply(sentence,start+1,right);
		}
		else if(!sentence.get(start+1).equals("(")&&(sentence.get(end-1).equals(")")))
		{//! =
			
			indx=start+2;right=end-1;
			for(;indx<end-1;indx++)
			{
				if(sentence.get(indx).equals("("))
					break;
			}
			if(sentence.get(indx-1).equals("=>"))
			{
				sentence.set(indx-1, "|");
				sentence.set(start+1,"~"+sentence.get(start+1));//~xx
				EImply(sentence,start+2,right+1);
			}
			else
				EImply(sentence,indx,end-1);
		}
		
		else if(sentence.get(start+1).equals("(")&&sentence.get(end-1).equals(")"))
		{//= =
			indx=start+1;right=end-1;
			for(;indx<=right;indx++)
			{
				if(sentence.get(indx).equals("("))
					power=power+1;
				else if(sentence.get(indx).equals(")"))
					power=power-1;
				else
				{
					if(power==0)
						break;
				}
			}
			if(sentence.get(indx).equals("=>"))
			{
				sentence.set(indx, "|");
				sentence.add(start+1,"~");//~(xx)
				EImply(sentence,start+2,indx);
				EImply(sentence,indx+2,end);
			}
			else
			{
				EImply(sentence,start+1,indx-1);
				EImply(sentence,indx+1,end-1);//
			}
		}
	}
	public void MNegt(List<String> sentence, int start, int end)
	//把非移进括号内
	{
		if(!sentence.get(start).equals("("))return;
		int power=0,i=start+1,flag=1;
		int count[]=new int[(end-start+1)/2];
		count[power]=0;
		String curr="";
		for(;i<sentence.size()-1;i++)
		{//~(A^B|~C)
			curr = sentence.get(i);
			//System.out.println(curr);
			if(curr.equals("~") && sentence.get(i+1).equals("("))
			{	
				power=power+1;
				count[power]=count[power-1]+1;
				sentence.remove(i);
				continue;
			}
			else if(curr.equals("("))
			{	power=power+1;
				count[power]=count[power-1];
			}	
			else if(curr.equals(")"))
			{	
				power=power-1;
			}
			else
			{
				if(count[power]%2==1)
				{
					if(curr.equals("&"))
						sentence.set(i, "|");
					else if(curr.equals("|"))
						sentence.set(i,"&");
					else if(curr.equals("~"))
					{	
						if(sentence.get(i-1).equals("(")&&sentence.get(i+2).equals(")"))
						{	sentence.remove(i-1);
							sentence.remove(i-1);
							sentence.remove(i);
							i--;
							power=power-1;
						}//~(~A)  A
						else 
							sentence.remove(i);
					}
					else
					{
						sentence.set(i,"~"+curr);
					}
						//System.out.println(power+" "+count[power]);
				}
			}
			curr=sentence.get(i);
		}
	}
	private String simplifyOr(String a, String b)
	{
		String sa,sb,rs;
		rs=a+"|"+b;
		//System.out.println(a+"==="+b);
		return rs;
	}
	private String Multiply(String[]va,String[]vb)
	{//分配律，字符串拼接
		String rs="",t="";
		int i=0,j=0;
		
		for(i=0;i<va.length;i++)
		{
			for(j=0;j<vb.length;j++)
			{
				//System.out.println(va[i]+"....."+vb[j]);
				t=simplifyOr(va[i],vb[j]);//可能需要化简
				//System.out.println(t);
				rs+=t;
				if(!(i==va.length-1 && (j==vb.length-1)))
					rs+="&";
			}
		}
		return rs;
	}
	private CNF HandleExp(String value1, String value2, String currop, String preop)
	{
		
		String op="&",res="";
		CNF rs;
		String[] v1s = null,v2s=null;
		//System.out.println(preop);
		if(preop.equals("--"))
		{	
			res=value1+currop+value2;
			op=currop;
		}
		else if(currop.equals("&"))
		{
			if(preop.equals("|&")||preop.equals("|-"))
				res=value1+"&"+value2;
				//.substring(1,value2.length()-2);
			else if(preop.equals("&|")||preop.equals("-|"))
				res= value1+"&"+value2;//a & b&(b|c)
			else if(preop.equals("||"))
				res=value1+"&"+value2;
			else
				res=value1+"&"+value2;
		}
		else
		{
			if(preop.equals("&&"))
			{	
				v1s=value1.split("&");
				v2s=value2.split("&");
				//获取value1 value2用&分割的n个子部分
				res=Multiply(v1s,v2s);
			}
			else if(preop.equals("|&"))
			{
				v1s=value1.split("\\|");
				v2s=value2.split("&");
				res=Multiply(v1s,v2s);
			}
			else if(preop.equals("-&"))
			{
				v1s=new String[1];
				v1s[0]=value1;
				v2s=value2.split("&");
				res=Multiply(v1s,v2s);
			}
			else if(preop.equals("&|"))
			{
				v1s=value1.split("&");
				v2s=value2.split("\\|");
				
				res=Multiply(v1s,v2s);
			}
			else if(preop.equals("&-"))
			{
				v1s=value1.split("&");
				v2s=new String[1];
				v2s[0]=value2;
				res=Multiply(v1s,v2s);
			}
			else
			{
				res= value1+currop+value2;
				op="|";//return "|";
			}
		}
		rs=new CNF(res,op);
		return rs;//op;
	}
	private void NParen(List<String> sentence)
	{//去掉可能的括号, （~A）变成~A
		//if(sentence.get(start)!="(")return;
		int i=0,power=0,count=0;
		String curr="";
		int[] op =new int[sentence.size()/2+1];
		for(i=0;i<op.length;i++)op[i]=-1;
		for(i=0;i<sentence.size();i++)
		{
			curr=sentence.get(i);
			if(curr.equals("(")&&sentence.get(i+1).equals("~")&& sentence.get(i+3).equals(")"))
			{//(~A)
				sentence.remove(i);
				sentence.remove(i);
				sentence.set(i,"~"+sentence.get(i));
				sentence.remove(i+1);
			}
		}
	}
	private List<String> toSuffix(List<String> exp)
	//中缀转后缀表达式
	{
		Stack<String> st =new Stack<String>();
		List<String> suff =new ArrayList<String>();
		String curr="";
		for(int i=0;i<exp.size();i++)
		{
			curr=exp.get(i);
			if(curr.equals("("))
			{
				st.push(curr);
			}
			else if(curr.equals("&")||curr.equals("|")||curr.equals("=>"))
			{
				if(!st.empty())
				{
					String s =st.pop();
					if(s.equals("("))//curr > s
					{
						st.push(s);
					}
					else
						suff.add(s);
				}
				st.push(curr);
			}
			else if(curr.equals(")"))
			{
				while(!st.empty())
				{
					String s=st.pop();
					if(!s.equals("("))
						suff.add(s);
					else
						break;
				}
			}
			else
				suff.add(curr);
		}
		while(!st.empty())
		{
			String s = st.pop();
			suff.add(s);
		}
		return suff;
	}
	public String[] Distri(List<String> senten)
	{
		int power=0,i=0;String curr="";String[] clauses=null;
		Stack<CNF> st=new Stack<CNF>();
		
		int left=0,right=senten.size()-1,pos=0,flag=1;
		
		//important!! convert to 后缀表达式
		List<String> sentence =new ArrayList<String>();
		NParen(senten);
		sentence = toSuffix(senten);
		
		for(i=0;i<sentence.size();i++)
		{
			curr=sentence.get(i);
			if(!curr.equals("&") && (!curr.equals("|")))
			{
				CNF cnf= new CNF(curr,"-");//如果是predicate 形如F(x) 设最低优先级操作符为空，用-表示
				st.push(cnf);
			}
			else
			{
				CNF v2 = st.pop();
				CNF v1 = st.pop();
				CNF rs =HandleExp(v1.exp,v2.exp,curr,v1.op+v2.op);
				st.push(rs);
			}
		}
		if(!st.empty())
		{
			CNF r = st.pop();
			//System.out.println("result: "+r.exp);
			clauses = r.exp.split("&");
			
		}
		//clauses cannot contain same variable names
		
		return clauses;
	}
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
	public String[] PreProcessing(List<String>sentence)
	{
		String[] s=null;int i=0;String ss="";
		EImply(sentence, 0, sentence.size()-1);
		MNegt(sentence, 0, sentence.size()-1);
		//for(;i<sentence.size();i++)
		//	ss+=sentence.get(i);
		//System.out.println(ss);
		String[] cnf = Distri(sentence);	
		//for(;i<cnf.length;i++)
		//	ss+=cnf[i]+"&";
		//System.out.println("sentence this time to cnf:"+ss);
		return cnf;
	}
	public KB readFileByLines(String fileName) {
        File file = new File(fileName);
        KB kb =new KB();
        BufferedReader reader = null;  
        int i=0,j=0;
        try {
            //System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader((new FileReader(file)));
            //UnicodeReader ur=new UnicodeReader(in);
            //BufferedReader reader = new BufferedReader(ur); 
            String tempString = null;
            int line = 1;
            
            //# of query lines
            NQ = Integer.parseInt(reader.readLine());
            //depth = Integer.parseInt(reader.readLine());
           
            // 一次读入一行，直到读入null为文件结束
            for(i=0;i<NQ;i++)
            {
            	if((tempString = reader.readLine())!=null)
            	quiries.add(tempString.trim());
            	//System.out.println(tempString);
            }
            //# of given sentences
            NS = Integer.parseInt(reader.readLine());
            for(i=0;i<NS;i++)
            {
            	List<String>  sentences = new ArrayList<String>();
            	if((tempString = reader.readLine())!=null)
            	{
            		sentences = toList(tempString);	
            		String[] clses = this.PreProcessing(sentences);
            		//for(int p=0;p<clses.length;p++)
            		//	System.out.println(clses[p]);
            		kb.AddASentence(clses);
            	}
            }
            reader.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    //out.close();
                } catch (IOException e1) {
                }
            }
        }
        return kb;
    }

    public void writeFile(String fileName){
    	FileWriter out =null;
    	BufferedWriter writer = null;
    	try{
    		out=new FileWriter(fileName);
            writer = new BufferedWriter(out);   		
    		for(int i=0;i<NQ;i++)
    		{
    			writer.write(ans.get(i));
    			if(i!=NQ-1)
    				writer.newLine();
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
    	}
    }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		homework rw = new homework();
		String fileName="input.txt";//读文件
		KB kb = rw.readFileByLines(fileName);
		
		for(int i=0;i<rw.quiries.size();i++)
		{	
			KB cp =new KB(kb);
			String alpha=rw.quiries.get(i);
			if(cp.Resolution(alpha))
				rw.ans.add("TRUE");
				//System.out.println(alpha+"true");
			else rw.ans.add("FALSE");//System.out.println(alpha+"false");
		}
		rw.writeFile("output.txt");//写文件
	}
}