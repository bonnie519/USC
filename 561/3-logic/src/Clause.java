import java.util.*;
public class Clause {
	List<Literal> literals=new ArrayList<Literal>();
	//int lineno;
	String allvars;
	int lsize;
	Map<String,Integer> pindx = new HashMap<String,Integer>();
	public Clause()
	{
		lsize=0;
		allvars="";
	}
	public boolean isEqual(Clause clause)
	{	
		int count=0;
		for(int i=0;i<lsize;i++)
		{
			for(int j=0;j<clause.lsize;j++)
			{
				//System.out.println(literals.get(i).exp+"....."+clause.getLiteral(j).exp);
				if(literals.get(i).getExp().equals(clause.getLiteral(j).getExp()))
				{
					count++;
				}
			}
		}
		if(count==lsize) return true;
		else return false;
	}
	public boolean isTrue()
	{
		for(int i=0;i<lsize-1;i++)
		{
			for(int j=0;j<lsize;j++)
			{
				if(literals.get(i).getQulty()+literals.get(j).getQulty()==0
						&&(literals.get(i).getOP().equals(literals.get(j).getOP())))
				{
					String li=literals.get(i).getExp(),lj =literals.get(j).getExp();
					if(literals.get(i).getQulty()==-1) li=li.substring(1);
					else lj=lj.substring(1);
					if(li.equals(lj))return true;
				}
			}
		}
		return false;
	}
	public Clause(Clause cp)
	{
		this.lsize =cp.lsize;
		int i=0;
		for(;i<cp.literals.size();i++)
			this.literals.add(cp.literals.get(i));
	}
	public Clause(String clause,int no)
	{
		
		String[] cls =clause.split("\\|");
		
		lsize =cls.length;
		//System.out.println(lsize);
		int i=0;
		for(;i<lsize;i++)
		{
			Literal lt =new Literal(cls[i],no);
			String[] ags=lt.getArgs();
			literals.add(lt);
			pindx.put(lt.getOP(), i);
		}
	}
	public void addLiteral(Literal lt)
	{
		literals.add(lt);
		pindx.put(lt.getOP(), lsize);
		lsize++;
	}
	public void appendClause(Clause c)
	{
		int i=0;
		for(;i<c.lsize;i++)
		{
			literals.add(c.getLiteral(i));
			pindx.put(c.getP(i), lsize+i);
		}
		lsize +=c.lsize;
	}
	public int findLiteral(String predicate)
	{
		if(pindx.get(predicate)==null)
			return -1;
		else
			return pindx.get(predicate);
	}
	
	public void remvLiteral(String exp)
	{
		Literal l=new Literal(exp,0);
		for(int i=0;i<literals.size();i++)
		{	if(literals.get(i).getExp().equals(exp))
			{
				literals.remove(i);
				lsize=lsize-1;
			}
		}
	}
	public String getP(int indx)
	{
		return literals.get(indx).getOP();
	}
	public Literal getLiteral(int indx)
	{
		return literals.get(indx);
	}
	public void setLiteralArg(int indxl, int indxa, String arg)
	{
		literals.get(indxl).setArg(indxa, arg);
	}
}