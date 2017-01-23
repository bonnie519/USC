
public class Literal {
	String op;
	String [] args=new String[100];
	int vars;
	int quality;
	String exp;
	public Literal(String expr,int no)
	{
		int start=0;
		quality =1;
		this.exp=expr;
		if(expr.charAt(0)=='~')
		{	
			quality=-1;
			start =1;
		}
		int indx = expr.indexOf('(');
		
		op = expr.substring(start, indx);//exp.length()-1
		//this.exp=expr.substring(0,indx+1);
		//System.out.println(expr);
		args = expr.substring(indx+1,expr.length()-1).split(",");
		vars =args.length;
	}
	public String getExp()
	{
		//return this.exp;
		
		String exp="";
		if(quality==-1)
		{
			exp+="~";
		}
		exp+=op+"(";
		for(int i=0;i<args.length;i++)
		{	
			exp+=args[i];
			if(i!=args.length-1)
				exp+=",";
		}
		exp+=")";
		return exp;
	}
	public int getVars()
	{
		return vars;
	}
	public int getQulty()
	{
		return quality;
	}
	public String[] getArgs()
	{
		return args;
	}
	public void setArg(int indx, String arg)
	{
		args[indx] = arg;
	}
	public String getOP()
	{
		return op;
	}

}