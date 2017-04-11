from decimal import *
getcontext().prec = 6
import networkx as nx
def createGraph():
    graph = nx.read_edgelist("edgeList2.txt",create_using = nx.DiGraph())
    print graph.number_of_nodes()
    print graph.number_of_edges()
    pr = nx.pagerank(graph, alpha=0.85,personalization=None,max_iter=30,tol=1.0e-6,nstart=None,weight='weight',dangling=None);
    output = open("external1.txt", "w+")
    for key in pr:
        output.write("/home/bei/solr-6.5.0/../shared/LATimes/"+key + "=" + ("%.6f" % pr[key]) + "\n")
    output.close()
if __name__ == '__main__':
    createGraph()