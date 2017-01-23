# -*- coding:utf-8 -*-
import os,sys,string

class Vertex:
    def __init__(self, key, hval = -1):
        self.id = key
        self.neigbr=[]  #adjacent list
        self.visited = False
        self.cost = -1
        self.parent = " "
        self.hvalue = hval
        self.gvalue = 0
    def addNeigbr(self, neigbr, weight):
        neigbpair =(neigbr,weight)
        self.neigbr.append(neigbpair)
        
    def getNeigbr(self):
        neigbrs=[]
        for each in self.neigbr:
            neigbrs.append(each[0])
        return neigbrs
    def getId(self):
        return self.id
    def getWeight(self,key): #weight of edge 
        for each in self.neigbr:
            if each[0] == key:
                return each[1]
        return -1
    
    def setCost(self,cost):
        self.cost = cost
    def getCost(self):
        return self.cost
    def getParent(self):
        return self.parent
    def setParent(self,key):
        self.parent = key
    def getHVal(self):
        return self.hvalue
    def setGVal(self,gval):
        self.gvalue = gval
    def getGVal(self):
        return self.gvalue
class Graph:

    def __init__(self):
        self.vertexlst={} #nodelist

    def addVertex(self,key,hval = -1):
        vertex=Vertex(key, hval) #create a node
        self.vertexlst[key]=vertex  #add the node to nodelist

    def addEdge(self, lft,rght,weight):
        if lft not in self.vertexlst:
            self.addVertex(lft)
        if rght not in self.vertexlst:
            self.addVertex(rght)
        self.vertexlst[lft].addNeigbr(rght, weight)

    def getVertex(self,key): #get some vertex
        return self.vertexlst.get(key)

    def bfs(self,root,goal):
        queue = []
        explored = []
        r = Vertex(root)
        r.setCost(0)
        queue.append(r)
        
        #####loop start
        while True:
            if not queue:
                return False
            
            node = queue.pop(0)
            print node.getId()
                                   
            #goal-test
            if node.getId() == goal:
                trace = node
                order = []
                while explored:
                    order.insert(0,trace)
                    if trace.getId() != root: 
                        trace = explored[self.findstate(explored, trace.getParent())]
                    else:
                        break
                return order
            
            if self.findstate(explored,node.getId()) == -1:
                explored.append(node)
                
            #expand queue
                for each in self.getVertex(node.getId()).getNeigbr():                  
                    if self.findstate(explored, each)==-1 and(self.findstate(queue,each)==-1):###
                        t = Vertex(each)
                        t.setCost(node.getCost()+1)
                        t.setParent(node.getId())
                        queue.append(t)

    def dfs(self,root,goal):
        stack = []
        order = []
        r = Vertex(root)
        r.setCost(0)
        stack.append(r)
        explored = []        
       
        #####loop start
        while True:
            if not stack:
                return False
            
            node = stack.pop(0)
            #goal-test
            if node.getId() == goal:
                order = []
                trace = node
                while explored:
                    if trace.getId() != root:
                        order.insert(0,trace)
                        trace = explored[self.findstate(explored, trace.getParent())]
                    else:
                        order.insert(0,trace)
                        break
                return order                
           
            
            if self.findstate(explored, node.getId())==-1:
                explored.append(node)           
                
                #expand stack
                mark = 0 
                tmps = []   
                for each in self.getVertex(node.getId()).getNeigbr():
                    t = Vertex(each)
                    tmpcost =node.getCost()+1
                    if self.findstate(explored, each)==-1:
                        if(self.findstate(stack,each)!= -1):
                            sindx = self.findstate(stack,each)
                            if stack[sindx].getCost() <  tmpcost:
                                continue
                        t.setCost(tmpcost)
                        t.setParent(node.getId())
                        tmps.append(t)
                        mark = 1
                   
                tmps.extend(stack)
                stack = tmps   
           
    def ucs(self,root,goal):
        open =  []
        r = Vertex(root)
        r.setCost(0)
        open.append(r)
        closed = []
                
        #####loop start
        while True:
            if not open:
                return False
            
            currnode = open.pop(0)  
            
            if currnode.getId() == goal:
				closed.append(currnode)
                t = currnode
                order =[]
                
                while closed:
                    if t.getId()!= root:
                        order.insert(0,t)
                        t = closed[self.findstate(closed,t.getParent())]
                    else:
                        order.insert(0,t)
                        break
                return order

            children = self.getVertex(currnode.getId()).getNeigbr()
        
                    
            while children:
                child = children.pop(0)                
                ecost = self.getVertex(currnode.getId()).getWeight(child)
                
                t= Vertex(child)
                t.setCost(ecost + currnode.getCost())
                
                oindx = self.findstate(open,child)
                cindx = self.findstate(closed,child)
                
                if oindx ==-1 and (cindx==-1):
                    
                    t.setParent(currnode.getId())
                    open.append(t)
                
                    if t.getCost() < open[oindx].getCost():
                       t.setParent(currnode.getId())
                       open.remove(open[oindx])
                       open.append(t)
                       
                elif cindx != -1:
                    
                    if t.getCost() < closed[cindx].getCost():
                       t.setParent(currnode.getId())
                       closed.remove(closed[cindx])
                       open.append(t)                       
                       
                else:continue
            closed.append(currnode)                        
            
            #open sort by pathcost
            open = sorted(open,key=lambda x:x.getCost())
        
    def findstate(self,lst,state):
        i=0
        for each in lst:
            if state == each.getId():
                return i
            i=i+1
        return -1
    
    def astar(self,root,goal):
        open =  []
        r = Vertex(root)
        r.setGVal(0)
        r.setCost(self.getVertex(root).getHVal()) ##f(s)<--h(S)
        open.append(r)
        closed = []
        
        #####loop start
        while True:
            if not open:
                return False
            
            currnode = open.pop(0)## choose the node whose f is smallest  
            
            if currnode.getId() == goal:         
                
                closed.append(currnode)
                t = currnode
                order =[]
                
                while closed:
                    
                    if t.getId()!= root:
                        order.insert(0,t)
                        t = closed[self.findstate(closed,t.getParent())]
                    else:
                        order.insert(0,t)
                        break
                return order

            children = self.getVertex(currnode.getId()).getNeigbr()
             
            while children:
                child = children.pop(0)
                ecost = self.getVertex(currnode.getId()).getWeight(child)
                
                t= Vertex(child)
                tmpg = ecost + currnode.getGVal()
                
                oindx = self.findstate(open,child)
                cindx = self.findstate(closed,child)
                
                if oindx ==-1 and (cindx==-1):
                    t.setGVal(tmpg)
                    t.setCost(tmpg + self.getVertex(child).getHVal())
                    t.setParent(currnode.getId()) ######
                    open.append(t)
            
                elif oindx != -1:##
                
                    if tmpg < open[oindx].getGVal():
                       t.setGVal(tmpg)
                       t.setCost(tmpg + self.getVertex(child).getHVal())
                       t.setParent(currnode.getId()) #####
                       open.remove(open[oindx])
                       open.append(t)
                       
                
                elif cindx != -1:
                    
                    if t.getCost() < closed[cindx].getCost():
                       t.setParent(currnode.getId())
                       t.setGVal(tmpg)
                       t.setCost(tmpg + self.getVertex(child).getHVal())
                       closed.remove(closed[cindx])
                       open.append(t)                       
                else:continue
            closed.append(currnode)
                        
            #open sort by pathcost
            open = sorted(open,key=lambda x:x.getCost())
                             
#####################################################################
def DoTest():

    #Read file
    fsockr = open("input.txt", "r")
    AllLines = fsockr.readlines()
    fsockr.close()
    fsockw = open("output.txt", "w")

    Algo = AllLines[0].strip()
    Start = AllLines[1].strip()
    Goal = AllLines[2].strip()
    livel = int(AllLines[3].strip())
    sundl = int(AllLines[4+livel].strip())
    
    G = Graph() 
        
    #store sunday line
    sund=[]
    for i in range(sundl):
        s = AllLines[5+livel+i].split(' ')
        hval = int(s[1].strip())
        G.addVertex(s[0],hval)
        
    #store live line
    line=[]
    for i in range(livel):
        s = AllLines[4+i].split(' ')
        lft = s[0]
        rght = s[1]
        wght = int(s[2].strip())
        G.addEdge(lft,rght,wght)
    
 ###################################################################   
    if Algo=="BFS":
        order =G.bfs(Start,Goal)    
    elif Algo=="DFS":
        order = G.dfs(Start,Goal)
    elif Algo=="UCS":
        order =G.ucs(Start,Goal)
    elif Algo=="A*":
        order=G.astar(Start,Goal)
    fsockw.write(order[0].getId()+" "+str(order[0].getCost()))    
    for each in order[1:]:
        fsockw.write('\n'+each.getId()+" "+str(each.getCost()))
    fsockw.close()

if __name__ == '__main__':
    DoTest()