import java.util.*;
public class KB {
	List<Clause> clauses;
	String vars;
	public KB()
	{ 
		clauses=new ArrayList<Clause>();	
	}
	public KB(KB cp)
	{
		clauses=new ArrayList<Clause>();
		for(int i=0;i<cp.clauses.size();i++)
			clauses.add(cp.clauses.get(i));
	}
	
	public void AddSomeClauses(List<Clause> clauses)
	{
		int i=0;
		for(;i<clauses.size();i++)
		{
			this.clauses.add(clauses.get(i));
		}
	}
	public void AddASentence(String[] s)
	{
		int i=0;
		for(i=0;i<s.length;i++)
		{
			Clause clse =new Clause(s[i],i);
			//consider duplicate variables
			clauses.add(clse);
		}
	}
	
	//resolve a b using theta except for predicate
	public Clause Resolve(Clause a, Clause b, Subst theta)
	{
		int i=0,j=0,k=0;String var="", val="";
		//a = literal1|literal2|literal3...
		//literal1: arg1 arg2 arg3
		Clause reslvt=new Clause();
		
		for(j=0;j<a.lsize;j++)
		{
			//if(a.getP(j).equals(predicate))continue;
			Literal lt =new Literal(a.getLiteral(j).getExp(),0);
			
			String[] args =a.getLiteral(j).getArgs();
			for(k=0;k<args.length;k++)
			{
				for(i=0;i<theta.num;i++)
				{
					var = theta.vars[i];
					val = theta.vals[i];
					
					if(var.equals(args[k]))
					{			
						lt.setArg(k, val);
						//a.setLiteralArg(j, k, val);
					}
				}
			}
			reslvt.addLiteral(lt);
			//A(x) F(x,y) F(A,J)  x:A, y:J if(var.equals(a.getLiteral(i).getArgs()))
		}
		for(j=0;j<b.lsize;j++)
		{
			Literal lt =new Literal(b.getLiteral(j).getExp(),0);
			String[] args =b.getLiteral(j).getArgs();
			for(k=0;k<args.length;k++)
			{
				for(i=0;i<theta.num;i++)
				{
					var = theta.vars[i];
					val = theta.vals[i];
					if(var.equals(args[k]))
					{
						
						lt.setArg(k, val);
					}
				}
			}
			reslvt.addLiteral(lt);
		}
		
		return reslvt;
	}
	
	public boolean Resolution(String alpha)
	{
		Clause alp;
		if(alpha.charAt(0)!='~')
			alp=new Clause("~"+alpha,clauses.size());
		else
			alp=new Clause(alpha.substring(1),clauses.size());
		clauses.add(alp);
		//negate alpha, add to kb
		
		
		int i=0,j=0,N=0,k=0,indx=0,count=0;String ss="";
		List<Clause> newcls =new ArrayList<Clause>();

		int flag;
		while(true)
		{
			N = clauses.size();
			//System.out.println(N);
			//find each pair in clauses
			for(i=N-1;i>0;i--)
			{
				for(j=i-1;j>=0;j--)
				{ 
					Clause a = clauses.get(i);Clause b = clauses.get(j);
					List<Clause> resolvents =new ArrayList<Clause>();
					//ss="";for(int p=0;p<a.lsize;p++){ss+=a.getLiteral(p).getExp();}System.out.println("a:"+ss);
					//ss="";for(int p=0;p<b.lsize;p++){ss+=b.getLiteral(p).getExp();}System.out.println("b:"+ss);
					for(k=0;k<a.lsize;k++)
					{
					for(int m=0;m<b.lsize;m++)
					{
						String predicate="";
						//System.out.println(a.getP(k)+" "+b.findLiteral(a.getP(k)));
						//if(b.findLiteral(a.getP(k))!=-1)//a b find same predicate
						if(b.getP(m).equals(a.getP(k)))
						{
							indx=m;//b.findLiteral(a.getP(k));
							if(b.getLiteral(indx).getQulty()+a.getLiteral(k).getQulty()==0)
							{//find opposite quality
								String la = a.getLiteral(k).getExp();String lb = b.getLiteral(indx).getExp();
								//System.out.println("#"+la+"---"+lb);
								if(a.getLiteral(k).getQulty()==-1)la=la.substring(1);else lb =lb.substring(1);
								flag=0;
								Subst theta=new Subst();theta.num=0;theta.UNIFY(la, lb);predicate= a.getP(k);
								Clause reslvt=new Clause();
						
								//resolve
								if(theta.num!=-1)
								{	
									Clause ta =new Clause(a);
									Clause tb =new Clause(b);
									ta.remvLiteral(a.getLiteral(k).getExp()); tb.remvLiteral(b.getLiteral(indx).getExp());
									reslvt=Resolve(ta,tb,theta);
									//if(reslvt.isTrue())continue;
									ss="";
									//for(int  p=0;p<reslvt.lsize;p++){ss+=reslvt.getLiteral(p).getExp();}System.out.println("rv:"+ss+"#");
									if(reslvt.lsize==0){ 
									//for(int p=0;p<a.lsize;p++){ss+=a.getLiteral(p).getExp();}System.out.println("a:"+ss);
									//ss="";for(int p=0;p<b.lsize;p++){ss+=b.getLiteral(p).getExp();}System.out.println("b:"+ss);
									return true;}
									//newcls.add(reslvt);
									unionAClause(newcls,reslvt);
								}//resolvents.add(reslvt); 
							}
						}
						}
					}

				}
				}
			//printS(newcls);
			count=count+1;
			if(isSubset(newcls,clauses)||count>=4)
				return false;//}//new is a subset of clauses, return false
			clauses=union(clauses,newcls);//clauses = clauses union new
		}
	}
	public void unionAClause(List<Clause>orig, Clause clause)
	{
		int i=0;
		for(;i<orig.size();i++)
		{
			if(orig.get(i).isEqual(clause))
				return;
		}
		orig.add(clause);
	}
 	public void printS(List<Clause>s)
	{
		int i=0;
		for(;i<s.size();i++)
		{
			Clause c =s.get(i);
			String ss="";
			for(int j=0;j<c.lsize;j++)
			{
				ss+=c.getLiteral(j).getExp();
			}
			System.out.println(ss);
		}
	}
	public boolean isSubset(List<Clause>a, List<Clause> b)
	{
		int i=0,j=0,flag=1,count=0;
		for(;i<a.size();i++)
		{
			flag=0;
			for(j=0;j<b.size();j++)
			{
				if(a.get(i).isEqual(b.get(j)))
				{
					flag=1;
					break;
				}
			}
			if(flag==1) count++;
		}
		//System.out.println(count);
		if(count==a.size()) return true;
		else return false;
	}
	public List<Clause> union(List<Clause>a, List<Clause>b)
	{  
		Set<Clause> set = new LinkedHashSet<Clause>();  
		set.addAll(a);
		set.addAll(b);
		List<Clause> c = new ArrayList<Clause>(set);
		return c;
	}
}