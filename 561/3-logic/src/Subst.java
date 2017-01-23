import java.util.*;
public class Subst {
	  int num; // -1 : failure
	  String [] vars=new String[1000];
	  String [] vals=new String[1000];
	
	 /**
	 * @param args
	 */
	class Branch{
		String text;
		//public Branch(String txt)
		//{
			//text=txt;
		//}
	} 
	public void UNIFY(String x,String y)
	{
		// recursive unification algorihm
		  //System.out.println("now:"+x+" "+y+" "+this.num);

		  Branch OPx=new Branch(),ARGx=new Branch(),FIRSTx=new Branch(),RESTx=new Branch();
		  Branch OPy=new Branch(),ARGy=new Branch(),FIRSTy=new Branch(),RESTy=new Branch();
		         
		  // use &OP, &ARG, ... in function calls!!
		  
		  if(this.num==-1) // theta=failure
		    return; // return failure
		    
		  else if(x.equals(y)) // x=y
		  {  //System.out.println(x+"="+y);  
		    return; // return theta
		  } 
		  else if(VARIABLE(x)==true){ // x is a variable
			  //System.out.println("x c");
			  UNIFY_VAR(x,y);
		    return;
		  }
		    
		  else if(VARIABLE(y)==true){ // y is a variable
			//System.out.println("c y");
			UNIFY_VAR(y,x);
		    return;
		  }
		    
		  else if(COMPOUND(x, OPx, ARGx)==true && COMPOUND(y, OPy, ARGy)==true){ // both are compound
		    //System.out.println("cc");
			UNIFY(OPx.text, OPy.text);
			//System.out.println(theta.num);
			//System.out.println(ARGx.text+"====="+ARGy.text);
			UNIFY(ARGx.text, ARGy.text);
		    return;
		  }
		    
		  else if(LIST(x, FIRSTx, RESTx)==true && LIST(y, FIRSTy, RESTy)==true){ // both are list 
		    //System.out.println("list list");
			UNIFY(FIRSTx.text, FIRSTy.text);
		    UNIFY(RESTx.text, RESTy.text);
		    return;
		  }

		  this.num=-1;        
		  return; // return failure
	}
	public boolean VARIABLE(String exp)
	{
		// test if the expression is a variable
		  // restrict : a variable MUST be a lowercase alphabet
		if(exp.matches("[a-z]"))
			return true;
		else
			return false;
	}
	
	public boolean COMPOUND(String exp,Branch op,Branch arg)
	{
		// test if the expression is a function
		// return its operation and arguments if true
		if(exp.matches("^[A-Z][a-zA-Z]*\\((([A-Z][a-z]*)|[a-z])(,(([A-Z][a-z]*)|[a-z]))*\\)$"))
		{
			int i=exp.indexOf('(');
			op.text =exp.substring(0,i);
			String rest =exp.substring(i+1,exp.length()-1);
			arg.text=rest;
			return true;
		}
		else
			return false;
	}
	public boolean LIST(String exp,Branch first,Branch rest)
	{
		int i;
		// test if the expression is a list
		// return its first and rest part if true
		if((i=exp.indexOf(','))==-1)
			return false;
		first.text =exp.substring(0, i);
		rest.text=exp.substring(i+1);
		return true;
	}
	public void UNIFY_VAR(String var,String x)
	{
		// unification with a variable and an expression
		//System.out.println(var+" "+x);  
		int i;
		  
		  // search theta for var or x
		  for(i=0;i<this.num;i++){
		    if(var.equals(this.vars[i])){
		      //System.out.println("var");
		    	UNIFY(this.vals[i],x);
		      return;
		    }
		      
		    else if(x.equals(this.vars[i])){
		    //	System.out.println("x");
		      UNIFY(var,this.vals[i]);
		      return;
		    }
		  }
		     
		  if(OCCUR_CHECK(var,x)){ // occur check
		    this.num=-1;
		    return; // return failure
		  }
		  
		  //System.out.println("rest");
		  i=this.num;
		  this.vars[i]=var;
		  this.vals[i]=x;
		  //strcpy(theta.vars[i],var);
		  //strcpy(theta.vals[i],x);
		  
		  (this.num)++;
		  return;
	}
	
	public boolean OCCUR_CHECK(String var,String x)
	{
		 // check if variable var occurs in expression x
		//case 1 F(x)
		//case 2 x,y,z
		int i;
		if(x.indexOf(',')==-1)
		{
			if(x.matches("^[A-Z][a-zA-Z]*\\((([A-Z][a-z]*)|[a-z])(,(([A-Z][a-z]*)|[a-z]))*\\)$"))
			{
				//System.out.println("single compound");
				i=x.indexOf('(');
				String sub =x.substring(i+1,x.length()-1);
				String[] args = sub.split(",");
				for(i=0;i<args.length;i++)
				{
					//System.out.println(args[i]);
					if(var.equals(args[i]))
						return true;
				}
			}
		}
		else
		{
			String[] subs =x.split(",");
			if(subs[0].matches("[a-z]"))
			{	
				for(i=0;i<subs.length;i++)
				{
					if(subs[i].equals(var))
						return true;
				}
				//return false;
			}
			else if(subs[0].matches("^[A-Z][a-zA-Z]*\\((([A-Z][a-z]*)|[a-z])(,(([A-Z][a-z]*)|[a-z]))*\\)$"))
			{
				for(i=0;i<subs.length;i++)
				{
					int indx =subs[i].indexOf('('),j=0;
					String sub =subs[i].substring(indx+1,subs[i].length()-1);
					String[] args = sub.split(",");
					for(j=0;j<args.length;j++)
					{
						if(var.equals(args[j]))
							return true;
					}
				}
			}
		}
		return false;
	}
}