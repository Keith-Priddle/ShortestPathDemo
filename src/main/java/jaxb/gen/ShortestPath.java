package jaxb.gen;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.boot.autoconfigure.SpringBootApplication;




@SpringBootApplication
public class ShortestPath {

	 
    // Driver Program
    public static void main(String args[]) throws IOException, JAXBException{
    	
    	
    	/* Start by unmarshalling the details for the topology 
    	 * (for convenience I have broken the topology into 
    	*  two files Entities and Associations
    	*/
    	
    	Scanner sc = new Scanner(System.in);
    	
    	JAXBContext jaxbContext     = JAXBContext.newInstance( Entities.class );
    	Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    	
    	
    	// No of vertices
    	//Initialises to zero and increments in for loop below 
        int v = 0 ;        
    	InputStream inStream = new FileInputStream( "src/main/resources/entities.topology" );
    	
    	Entities entities = (Entities) jaxbUnmarshaller.unmarshal( inStream );   
        
    	//create objects from xml
    	List<String> entityNames = new ArrayList<>();
    	List<String> entityTypes = new ArrayList<>();
    	
    
    	for (int e1 = 0; e1 < entities.clazz.size();e1++) {
    	
    		for (int e2 = 0; e2 < entities.clazz.get(e1).entity.size();e2++) {
    			entityNames.add(entities.clazz.get(e1).entity.get(e2).key);
    			entityTypes.add(entities.clazz.get(e1).key);
    			
    			v++;
    			}
    	}	
    	
    		
    	JAXBContext jaxbContext2     = JAXBContext.newInstance( Associations.class );
    	Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();
    	InputStream inStream2 = new FileInputStream( "src/main/resources/associations.topology" );
    	
    	Associations associations = (Associations) jaxbUnmarshaller2.unmarshal( inStream2 ); 	
    	
 
        // Adjacency list for storing which vertices are connected
        ArrayList<ArrayList<Integer>> adj = 
                     new ArrayList<ArrayList<Integer>>(v);
        for (int i = 0; i < v; i++) {
            adj.add(new ArrayList<Integer>());
        }
        System.out.println("From the following Entities:");
        for (int entnum = 0; entnum < entityNames.size(); entnum++) {
        	System.out.println(entnum + " - " + entityNames.get(entnum));
        }
        System.out.println("Enter the start point:");
        
        int source = sc.nextInt();
        
        System.out.println("Enter end point: ");
        
        int dest = sc.nextInt();
        
        // Creating graph given in the above diagram.
        // add_edge function takes adjacency list, source
        // and destination vertex as argument and forms
        // an edge between them.
        
        int assocs = associations.getAssociation().size();
        
        for (int a1 = 0; a1< assocs;a1++) {
        	addEdge(adj, entityNames.indexOf(associations.getAssociation().get(a1).primary), 
        			entityNames.indexOf(associations.getAssociation().get(a1).secondary));	
        	
        	}
        	

        //close scanner
        sc.close();
        
        printShortestDistance(adj, source, dest, v, entityNames);
     
        
    }
    
    
    
    // function to form edge between two vertices
    // source and dest
    private static void addEdge(ArrayList<ArrayList<Integer>> adj, int i, int j)
    {
        adj.get(i).add(j);
        //Uncomment line below for bidirectional flow
        //adj.get(j).add(i);
    }
    
    
    
    // function to print the shortest distance and path
    // between source vertex and destination vertex
    private static void printShortestDistance(
                     ArrayList<ArrayList<Integer>> adj,
                             int s, int dest, int v, List<String> entityNames)
    {
        // predecessor[i] array stores predecessor of
        // i and distance array stores distance of i
        // from s
        int pred[] = new int[v];
        int dist[] = new int[v];
 
        if (BFS(adj, s, dest, v, pred, dist) == false) {
            System.out.println("Given source and destination " + 
                                         "are not connected");
            return;
        }
 
        // LinkedList to store path
        LinkedList<Integer> path = new LinkedList<Integer>();
        int crawl = dest;
        path.add(crawl);
        while (pred[crawl] != -1) {
            path.add(pred[crawl]);
            crawl = pred[crawl];
        }
 
        // Print distance
        System.out.println("Shortest path length is: " + dist[dest]);
        
        // Print path
        System.out.println("Path is ::");
        for (int i = path.size() - 1; i >= 0; i--) {
            System.out.print(path.get(i) + " ");
            System.out.println(entityNames.get(path.get(i)));
        }
    }
 
    // a modified version of BFS that stores predecessor
    // of each vertex in array pred
    // and its distance from source in array dist
    private static boolean BFS(ArrayList<ArrayList<Integer>> adj, int src,
                                  int dest, int v, int pred[], int dist[])
    {
        // a queue to maintain queue of vertices whose
        // adjacency list is to be scanned as per normal
        // BFS algorithm using LinkedList of Integer type
        LinkedList<Integer> queue = new LinkedList<Integer>();
 
        // boolean array visited[] which stores the
        // information whether ith vertex is reached
        // at least once in the Breadth first search
        boolean visited[] = new boolean[v];
 
        // initially all vertices are unvisited
        // so v[i] for all i is false
        // and as no path is yet constructed
        // dist[i] for all i set to infinity
        for (int i = 0; i < v; i++) {
            visited[i] = false;
            dist[i] = Integer.MAX_VALUE;
            pred[i] = -1;
        }
 
        // now source is first to be visited and
        // distance from source to itself should be 0
        visited[src] = true;
        dist[src] = 0;
        queue.add(src);
 
        // bfs Algorithm
        while (!queue.isEmpty()) {
            int u = queue.remove();
            
            for (int i = 0; i < adj.get(u).size(); i++) {
            	if (visited[adj.get(u).get(i)] == false) {
                    visited[adj.get(u).get(i)] = true;
                    dist[adj.get(u).get(i)] = dist[u] + 1;
                    pred[adj.get(u).get(i)] = u;
                    queue.add(adj.get(u).get(i));
 
                    // stopping condition (when we find
                    // our destination)
                    if (adj.get(u).get(i) == dest)
                        return true;
                }
            }
        }
        return false;
    }
}
