package  ucsd.rmkelley.BetweenPathway;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import java.awt.Dimension;

class BetweenPathwayThread2 extends Thread{
  double absent_score = .00001;
  double logBeta = Math.log(0.9);
  double logOneMinusBeta = Math.log(0.1);
  BetweenPathwayOptions options;
  double [][] physicalScores;
  double [][] geneticScores;
  CyNetwork physicalNetwork;
  CyNetwork geneticNetwork;
  Vector results;

  public BetweenPathwayThread2(BetweenPathwayOptions options){
    this.options = options;
  }

  public void setPhysicalNetwork(CyNetwork physicalNetwork){
    this.physicalNetwork = physicalNetwork;
  }

  public void setGeneticNetwork(CyNetwork geneticNetwork){
    this.geneticNetwork = geneticNetwork;
  }

  public void loadPhysicalScores(File physicalFile){
    //set up the array of physical scores
    try{
      physicalScores = getScores(physicalFile,physicalNetwork);
    }catch(IOException io){
      throw new RuntimeException("Error loading physical score file");
    }
  }

  public void loadGeneticScores(File geneticFile){
    try{
      geneticScores = getScores(geneticFile,geneticNetwork);
    }catch(IOException io){
      throw new RuntimeException("Error loading genetic score file");
    }
  }

  public void run(){
    //number of physical interactions allowed between pathways
    int cross_count_limit = 1;
    //get the two networks which will be used for the search

    //validate the user input
    if(physicalNetwork == null){
      throw new RuntimeException("No physical network");
    }
    if(geneticNetwork == null){
      throw new RuntimeException("No genetic network");
    }
    
    /*
     * Create a map which will map from each edge 
     * to a list of adjacent edges and another list
     * We consider an edge adjacent if it shares a path of lenght <=4
     * which goes through both the genetic and physical networks.
     */
    HashMap edgeNeighborMap = new HashMap(geneticNetwork.getEdgeCount());
    for(Iterator it = geneticNetwork.edgesIterator();it.hasNext();){
      Vector neighbors = new Vector();
      Edge edge = (Edge)it.next();
      Node source = edge.getSource();
      Node target = edge.getTarget();

      int max_depth = 2;
      /*
       * These arrays will keep a list of all paths of nodes of a particular
       * length from the source node
       */
      List [] sourcePaths = new List[max_depth+1];
      List [] targetPaths = new List[max_depth+1];
      for(int idx = 0;idx < sourcePaths.length;idx++){
	sourcePaths[idx] = new Vector();
	targetPaths[idx] = new Vector();
      }
      sourcePaths[0].add(source);
      targetPaths[0].add(target);
      Stack nodeStack = new Stack();
      nodeStack.push(source);
      /*
       * The find all paths function will recursively
       * fill the paths array will all paths of the
       * length corresponding to the index. We only keep
       * those paths that are present in the genetic
       * network.
       */
      findAllPaths(nodeStack,max_depth,sourcePaths);
      nodeStack = new Stack();
      nodeStack.push(target);
      findAllPaths(nodeStack,max_depth,targetPaths);

      for(int idx=0;idx<max_depth+1;idx++){
	for(int idy=0;idy<max_depth+1-idx;idy++){
	  for(Iterator sourcePathIt = sourcePaths[idx].iterator();sourcePathIt.hasNext();){
	    for(Iterator targetPathIt = targetPaths[idy].iterator();targetPathIt.hasNext();){
	      List sourcePath = (List)sourcePathIt.next();
	      List targetPath = (List)targetPathIt.next();
	      Node sourceNeighbor = (Node)sourcePath.get(sourcePath.size()-1);
	      Node targetNeighbor = (Node)targetPath.get(targetPath.size()-1);
	      List edges = geneticNetwork.edgesList(sourceNeighbor,targetNeighbor);
	      if(edges != null){
		neighbors.add(new NeighborEdge((Edge)edges.get(0),sourcePath,targetPath,false));
	      }
	      edges = geneticNetwork.edgesList(targetNeighbor,sourceNeighbor);
	      if(edges != null){
		neighbors.add(new NeighborEdge((Edge)edges.get(0),targetPath,sourcePath,true));
	      }
	    }
	  }
	}
      }

      /*
       * Get a list of all nodes that are neighbors of the source
       *
      List sourceNeighbors = physicalNetwork.neighborsList(source);
      if(sourceNeighbors == null){
	sourceNeighbors = new Vector();
      }
      sourceNeighbors.add(source);
      /*
       * Get a list of all nodes that are neighbors of the target
       *
      List targetNeighbors = physicalNetwork.neighborsList(target);
      if(targetNeighbors == null){
	targetNeighbors = new Vector();
      }
      targetNeighbors.add(target);

      /*
       * Find all edges that exist between any member of the source set and
       * any member of the target set.
       *
      for(Iterator sourceNeighborIt = sourceNeighbors.iterator();sourceNeighborIt.hasNext();){
	Node sourceNeighbor = (Node)sourceNeighborIt.next();
	for(Iterator targetNeighborIt = targetNeighbors.iterator();targetNeighborIt.hasNext();){
	  Node targetNeighbor = (Node)targetNeighborIt.next();
	  if((sourceNeighbor != source || targetNeighbor != target) && sourceNeighbor != targetNeighbor){
	    List edges = geneticNetwork.edgesList(sourceNeighbor,targetNeighbor);
	    if(edges != null){
	      neighbors.add(new NeighborEdge((Edge)edges.get(0),false));
	      continue;
	    }
	    edges = geneticNetwork.edgesList(targetNeighbor, sourceNeighbor);
	    if(edges != null){
	      neighbors.add(new NeighborEdge((Edge)edges.get(0),true));
	    }
	  }
	}
      }
      */
      neighbors.trimToSize();
      edgeNeighborMap.put(edge,neighbors);
    }

    results = new Vector();
    //start a search from each individual genetic interactions
    ProgressMonitor myMonitor = null;
    
    int progress = 0;
    Iterator geneticIt = null;
    if(options.selectedSearch){
      geneticIt = geneticNetwork.getFlaggedEdges().iterator();
      myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,geneticNetwork.getFlaggedEdges().size());
    }
    else{
      geneticIt = geneticNetwork.edgesIterator();
      myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),null,"Searching for Network Models",0,geneticNetwork.getEdgeCount());    
    }
    myMonitor.setMillisToPopup(1);
    while(geneticIt.hasNext()){
      if(myMonitor.isCanceled()){
	throw new RuntimeException("Search cancelled");
      }
      myMonitor.setProgress(progress++);
      Edge seedInteraction = (Edge)geneticIt.next();
      int cross_count_total = 0;
      double score = 0;
      //performs a sanity check, shouldn't have a genetic interaction between a gene and itself
      if(seedInteraction.getSource().getRootGraphIndex() == seedInteraction.getTarget().getRootGraphIndex()){
	continue;
      }
      
      //initialize the sets that represent the two pathways
      Set sourceMembers = new HashSet();
      Set targetMembers = new HashSet();
      sourceMembers.add(seedInteraction.getSource());
      targetMembers.add(seedInteraction.getTarget());
      
      //initialize the set that represents the neighboring interactions in the 
      //genetic network
      Set neighbors = new HashSet((Vector)edgeNeighborMap.get(seedInteraction));
      //initialize the sets that represent the neighbors in the physical network
      if(physicalNetwork.edgeExists(seedInteraction.getSource(),seedInteraction.getTarget())){
	cross_count_total++;
      }
            
      /*
       * Start searching through neighbor interactions to improve
       * our model score
       */
      boolean improved = true;
      double significant_increase = 0;
      while(improved){
	improved = false;
	if(myMonitor.isCanceled()){
	  throw new RuntimeException("Search cancelled");
	}

	NeighborEdge bestCandidate = null;
	double best_increase = Double.NEGATIVE_INFINITY;
	int best_cross_count = 0;
	for(Iterator candidateIt = neighbors.iterator();candidateIt.hasNext();){
	  double increase = 0;
	  NeighborEdge candidate = (NeighborEdge)candidateIt.next();
	  List sourceCandidates = candidate.reverse ? candidate.targetCandidates : candidate.sourceCandidates;
	  List targetCandidates = candidate.reverse ? candidate.sourceCandidates : candidate.targetCandidates;
	  //this edge has effectively already been included, don't try to add it in again
	  if(sourceMembers.containsAll(sourceCandidates) && targetMembers.containsAll(targetCandidates)){
	    candidateIt.remove();
	    continue;
	  }
	  //adding this edge will make the two sets overlapping
	  boolean cont = false;
	  for(Iterator sourceCandidateIt = sourceCandidates.iterator();sourceCandidateIt.hasNext();){
	    if(targetMembers.contains(sourceCandidateIt.next())){
	      candidateIt.remove();
	      cont = true;
	      break;
	    }
	  }
	  for(Iterator targetCandidateIt = targetCandidates.iterator();targetCandidateIt.hasNext();){
	    if(sourceMembers.contains(targetCandidateIt.next())){
	      candidateIt.remove();
	      cont = true;
	      break;
	    }
	  }

	  if(cont){
	    continue;
	  }

	  
	  /*
	   * this is a genetic edge between a protein and itself,
	   * this doesn't make sense, so we are skipping
	   */
	  
	  /*
	   * I am going to remove this check because I think it is
	   * extraneous
	   */
	  
	  /*
	    if(sourceCandidate.getRootGraphIndex() == targetCandidate.getRootGraphIndex()){
	    candidateIt.remove();
	    continue;
	    }
	  */
	  
	  int remaining_cross_count = cross_count_limit-cross_count_total+1;
	  int cross_count = crossPhysicalInteraction(sourceCandidates,targetCandidates,sourceMembers,targetMembers,remaining_cross_count);
	  /*
	   * Check to see if adding this edge will exceed our cross
	   * count limit
	   */
	  if(cross_count == remaining_cross_count){
	    candidateIt.remove();
	    continue;
	  }

	  increase += calculateGeneticIncrease(sourceCandidates,targetCandidates,sourceMembers,targetMembers);
	  increase += calculatePhysicalIncrease(sourceCandidates,targetCandidates,sourceMembers,targetMembers);
	  
	  if(increase > best_increase){
	    bestCandidate = candidate;
	    best_increase = increase;
	    best_cross_count = cross_count;
	  }

	}
	if(best_increase > significant_increase){
	  improved = true;
	  cross_count_total += best_cross_count;
	  score += best_increase;
	  sourceMembers.add(bestCandidate.reverse ? bestCandidate.edge.getTarget() : bestCandidate.edge.getSource());
	  targetMembers.add(bestCandidate.reverse ? bestCandidate.edge.getSource() : bestCandidate.edge.getTarget());
	  if(bestCandidate.reverse){
	    for(Iterator it = ((Vector)edgeNeighborMap.get(bestCandidate.edge)).iterator();it.hasNext();){
	      NeighborEdge newNeighbor = (NeighborEdge)it.next();
	      neighbors.add(newNeighbor.reverseEdge());
	    }
	  }
	  else{
	    neighbors.addAll((Vector)edgeNeighborMap.get(bestCandidate.edge));
	  }
	}
      }
      results.add(new NetworkModel(progress,sourceMembers,targetMembers,score));
    }
    myMonitor.close();
    Collections.sort(results);
    results = prune(results);
  }

  protected void findAllPaths(Stack nodeStack, int max_depth, List [] sourcePaths){
    Node currentNode = (Node)nodeStack.peek();
    List currentNodeNeighbors = physicalNetwork.neighborsList(currentNode);
    for(Iterator neighborIt = currentNodeNeighbors.iterator();neighborIt.hasNext();){
      Node neighbor = (Node)neighborIt.next();
      /*
       * I want the path to consist only
       * of unique nodes
       */
      if(!nodeStack.contains(neighbor)){
	nodeStack.push(neighbor);
	if(geneticNetwork.containsNode(neighbor)){
	  Vector path = new Vector(nodeStack);
	  sourcePaths[sourcePaths.length-max_depth].add(path);
	}
	if(max_depth > 1){
	  findAllPaths(nodeStack,max_depth-1,sourcePaths);
	}
	nodeStack.pop();
      }
    }
  }

  public Vector getResults(){
    return results;
  }

  protected Vector prune(Vector old){
    Vector results = new Vector();
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),"Pruning results",null,0,100);
    myMonitor.setMillisToPopup(50);
    int update_interval = (int)Math.ceil(old.size()/100.0);
    int count = 0;
    for(Iterator modelIt = old.iterator();modelIt.hasNext();){
      if(myMonitor.isCanceled()){
	throw new RuntimeException("Search cancelled");
      }
      if(count%update_interval == 0){
	myMonitor.setProgress(count/update_interval);
      }
      boolean overlap = false;
      NetworkModel current = (NetworkModel)modelIt.next();
      if(current.one.size() < 2 || current.two.size() < 2 || current.score < options.cutoff){
	overlap = true;
      }
      else{
	for(Iterator oldModelIt = results.iterator();oldModelIt.hasNext();){
	  NetworkModel oldModel = (NetworkModel)oldModelIt.next();
	  if(overlap(oldModel,current)){
	    overlap = true;
	    break;
	  }
	}
      }
      if(!overlap){
	results.add(current);
      }
    }
    myMonitor.close();
    return results;
  }

  public boolean overlap(NetworkModel one, NetworkModel two){
    return (intersection(one.one,two.one)>0.33 && intersection(one.two,two.two)>0.33) || (intersection(one.one,two.two) > 0.33 && intersection(one.two,two.one) > 0.33); 
  }


  public double intersection(Set one, Set two){
    int size = one.size() + two.size();
    int count = 0;
    for(Iterator nodeIt = one.iterator() ; nodeIt.hasNext() ;){
      if(two.contains(nodeIt.next())){
	count++;
      }
    }
    return count/(double)(size-count);
  }
      
  protected int crossPhysicalInteraction(List sourceCandidates, List targetCandidates, Set sourceMembers, Set targetMembers, int maximum){
    int cross_count = 0;
    for(Iterator sourceCandidateIt = sourceCandidates.iterator(); sourceCandidateIt.hasNext();){
      Node sourceCandidate = (Node)sourceCandidateIt.next();
      if(!sourceMembers.contains(sourceCandidate)){
	for(Iterator it = targetMembers.iterator();it.hasNext();){
	  if(physicalNetwork.isNeighbor(sourceCandidate,(Node)it.next())){
	    cross_count += 1;
	    if(cross_count >= maximum){
	      return cross_count;
	    }
	  }
	}
      }
      for(Iterator targetCandidateIt = targetCandidates.iterator(); targetCandidateIt.hasNext();){
	if(physicalNetwork.isNeighbor(sourceCandidate,(Node)targetCandidateIt.next())){
	  cross_count += 1;
	  if(cross_count >= maximum){
	    return cross_count;
	  }
	}
      }
    }
    for(Iterator targetCandidateIt = targetCandidates.iterator(); targetCandidateIt.hasNext();){
      Node targetCandidate = (Node)targetCandidateIt.next();
      if(!targetMembers.contains(targetCandidate)){
	for(Iterator it = sourceMembers.iterator();it.hasNext();){
	  if(physicalNetwork.isNeighbor(targetCandidate,(Node)it.next())){
	    cross_count += 1;
	    if(cross_count >= maximum){
	      return cross_count;
	    }
	  }
	}
      }
    }
    return cross_count;
  }

  protected double calculateGeneticIncrease(List sourceCandidates, List targetCandidates, Set sourceMembers, Set targetMembers){
    double result = 0;
    List newSources = new Vector(2);
    List newTargets = new Vector(2);
    for(Iterator sourceCandidateIt = sourceCandidates.iterator();sourceCandidateIt.hasNext();){
      Node sourceCandidate = (Node)sourceCandidateIt.next();
      boolean newSource = !sourceMembers.contains(sourceCandidate);
      if(newSource){
	newSources.add(sourceCandidate);
	result += calculatePartialGeneticIncrease(targetMembers,sourceCandidate);
      }
    }
    for(Iterator targetCandidateIt = targetCandidates.iterator();targetCandidateIt.hasNext();){
      Node targetCandidate = (Node)targetCandidateIt.next();
      boolean newTarget = !targetMembers.contains(targetCandidate);
      if(newTarget){
	newTargets.add(targetCandidate);
	result += calculatePartialGeneticIncrease(sourceMembers,targetCandidate);
      }
    }

    for(Iterator newSourceIt = newSources.iterator();newSourceIt.hasNext();){
      Node sourceCandidate = (Node)newSourceIt.next();
      for(Iterator newTargetIt = newTargets.iterator();newTargetIt.hasNext();){
	Node targetCandidate = (Node)newTargetIt.next();
	int one,two;
	one = Math.max(geneticNetwork.getIndex(sourceCandidate),geneticNetwork.getIndex(targetCandidate)) - 1;
	two = Math.min(geneticNetwork.getIndex(sourceCandidate),geneticNetwork.getIndex(targetCandidate)) - 1;
	result += logBeta;
	result -= Math.log(geneticScores[one][two]);
      }
    }
    return result;
  }

  protected double calculatePartialGeneticIncrease(Set memberSet, Node candidate){
    double result = 0;
   
    int candidate_index = geneticNetwork.getIndex(candidate);
    for(Iterator memberIt = memberSet.iterator();memberIt.hasNext();){
      Node member = (Node)memberIt.next();
      int member_index = geneticNetwork.getIndex(member);
      int one,two;
      if(candidate_index < member_index){
	one = member_index-1;
	two = candidate_index-1;
      }
      else{
	one = candidate_index-1;
	two = member_index-1;
      }
      if(geneticNetwork.isNeighbor(candidate,member)){
	result += logBeta;
	result -= Math.log(geneticScores[one][two]);
      }
      else{
	result += logOneMinusBeta;
	result -= Math.log(1-geneticScores[one][two]);
      }
      
    }
    
    return result;
	
  }

  protected double calculatePhysicalIncrease(List sourceCandidates, List targetCandidates, Set sourceMembers, Set targetMembers){
    double result = 0;
    List newSources = new Vector(2);
    for(Iterator sourceCandidateIt = sourceCandidates.iterator();sourceCandidateIt.hasNext();){
      Node sourceCandidate = (Node)sourceCandidateIt.next();
      if(!sourceMembers.contains(sourceCandidate)){
	result += calculatePartialPhysicalIncrease(sourceCandidate, sourceMembers, targetMembers);
	newSources.add(sourceCandidate);
      }
    }

    /*
     * If both edges are new,then we also have to consider the edge
     * (that must be present) between the two new additions to the sources
     */
    if(newSources.size() == 2){
      Node source = (Node)newSources.get(0);
      Node target = (Node)newSources.get(1);
      int one,two;
      one = Math.max(physicalNetwork.getIndex(source),physicalNetwork.getIndex(target)) - 1;
      two = Math.min(physicalNetwork.getIndex(source),physicalNetwork.getIndex(target)) - 1;
      result += logBeta;
      result -= Math.log(physicalScores[one][two]);
    }

    List newTargets = new Vector(2);
    for(Iterator targetCandidateIt = targetCandidates.iterator();targetCandidateIt.hasNext();){
      Node targetCandidate = (Node)targetCandidateIt.next();
      if(!targetMembers.contains(targetCandidate)){
	newTargets.add(targetCandidate);
	result += calculatePartialPhysicalIncrease(targetCandidate, targetMembers, sourceMembers);
      }
    }

    if(newTargets.size() == 2){
      Node source = (Node)newTargets.get(0);
      Node target = (Node)newTargets.get(1);
      int one,two;
      one = Math.max(physicalNetwork.getIndex(source),physicalNetwork.getIndex(target)) - 1;
      two = Math.min(physicalNetwork.getIndex(source),physicalNetwork.getIndex(target)) - 1;
      result += logBeta;
      result -= Math.log(physicalScores[one][two]);
    }
    
    return result;
  }
  
  protected double calculatePartialPhysicalIncrease(Node candidate, Set members, Set otherMembers){
    double result = 0;
    boolean candidatePresent = physicalNetwork.containsNode(candidate);
    if(candidatePresent){
      int candidateIndex = physicalNetwork.getIndex(candidate);
      for(Iterator memberIt = members.iterator();memberIt.hasNext();){
	Node member = (Node)memberIt.next();
	if(physicalNetwork.containsNode(member)){
	  int memberIndex = physicalNetwork.getIndex(member);
	  int one = Math.max(candidateIndex,memberIndex);
	  int two = Math.min(candidateIndex,memberIndex);
	  if(physicalNetwork.isNeighbor(candidate,member)){
	    result += logBeta;
	    result -= Math.log(physicalScores[one][two]);
	  }
	  else{
	    result += logOneMinusBeta;
	    result -= Math.log(1-physicalScores[one][two]);
	  }  
	}
	else{
	  result += logOneMinusBeta;
	  result -= Math.log(1-absent_score);
	}
      }
    }
    else{
      result += members.size()*(logOneMinusBeta - Math.log(1-absent_score));
    }

    return result;
    //get genetic interactios involving candidate
    //for each genetic interaction with a member of opposite set
    //    for each genetic interaction that member has with members
    //       check for a protein interaction, and update score
    /*
     * Update this function to ignore the distance restrictions
     */
    /*
      for(Iterator it = geneticNetwork.neighborsList(candidate).iterator();it.hasNext();){
      Node candidateNeighbor = (Node)it.next();
      if(otherMembers.contains(candidateNeighbor)){
      for(Iterator dist2It = geneticNetwork.neighborsList(candidateNeighbor).iterator();dist2It.hasNext();){
      Node dist2Neighbor = (Node)dist2It.next();
      if(members.contains(dist2Neighbor)){
      if(candidatePresent && physicalNetwork.containsNode(dist2Neighbor)){
      int one = Math.max(physicalNetwork.getIndex(dist2Neighbor),candidateIndex) -1;
      int two = Math.min(physicalNetwork.getIndex(dist2Neighbor),candidateIndex) -1;
      if(physicalNetwork.isNeighbor(candidate,dist2Neighbor)){
      result += logBeta;
      result -= Math.log(physicalScores[one][two]);
      }
      else{
      result += logOneMinusBeta;
      result -= Math.log(1-physicalScores[one][two]);
      }
      }
      else{
      result += logOneMinusBeta;
      result -= Math.log(1-absent_score);
      }
      }
      }
      }
      }
      return result;
    */
  }

  public double [][] getScores(File scoreFile, CyNetwork cyNetwork) throws IOException{
    double [][] result = new double[cyNetwork.getNodeCount()][];
    String [] names = new String [cyNetwork.getNodeCount()];
    for(int idx=result.length-1;idx>-1;idx--){
      result[idx] = new double[idx];
    }
    
    ProgressMonitor myMonitor = new ProgressMonitor(Cytoscape.getDesktop(),"Loading scores for "+cyNetwork.getTitle(),null,0,100);
    myMonitor.setMillisToDecideToPopup(50);
    int updateInterval = (int)Math.ceil(cyNetwork.getNodeCount()/100.0);
    
    BufferedReader reader = null;
    try{
      reader = new BufferedReader(new FileReader(scoreFile));
    }catch(Exception e){
      throw new RuntimeException("Error loading score file for "+cyNetwork.getTitle());
    }
    int line_number = 0;
    int progress = 0;
    String iterationString = reader.readLine();
    double iterations = (new Integer(iterationString)).doubleValue();
    while(reader.ready()){
      String line = reader.readLine();
      String [] splat = line.split("\t");
      if(line_number%updateInterval == 0){
	if(myMonitor.isCanceled()){
	  throw new RuntimeException("Score loading cancelled");
	}
	myMonitor.setProgress(progress++);
      }
      names[line_number++] = splat[0];
      if(splat.length != line_number){
	throw new RuntimeException("Score file in incorrect format");
      }
      int one;
      try{
	one = cyNetwork.getIndex(Cytoscape.getCyNode(splat[0]));
      }catch(Exception e){
	throw new RuntimeException("Score file contains protein ("+splat[0]+") not present in the network");
      }
      for(int idx=1;idx<splat.length;idx++){
	int two;
	try{
	  two = cyNetwork.getIndex(Cytoscape.getCyNode(names[idx-1]));
	}catch(Exception e){
	  throw new RuntimeException("Score file contains proteins ("+names[idx-1]+") not present in the network");
	}
	if(one < two){
	  result[two-1][one-1] = (new Integer(splat[idx])).intValue()/iterations;
	}
	else{
	  result[one-1][two-1] = (new Integer(splat[idx])).intValue()/iterations;
	}
	
      }
    }
    myMonitor.close();
    if(line_number != cyNetwork.getNodeCount()){
      throw new RuntimeException("The number of proteins in the network and score file do not match");
    }
    
    return result;
  }
}

class NeighborEdge{
  /**
   * Keeps track of the root graph indices
   * of the neighbors of a particular
   * node
   */
  public Edge edge;
  public List sourceCandidates;
  public List targetCandidates;
  /**
   * Whether we should reverse the source
   * and target to make the two edges
   * line up source to source
   */
  public boolean reverse;
  public NeighborEdge(Edge edge, List sourceCandidates, List targetCandidates, boolean reverse){
    this.edge = edge;
    this.reverse = reverse;
    this.sourceCandidates = sourceCandidates;
    this.targetCandidates = targetCandidates;
  }
  
  public boolean equals(Object o){
    NeighborEdge other = (NeighborEdge)o;
    if(other.edge != this.edge || other.reverse != reverse){
      return false;
    }
    if(sourceCandidates.size() != other.sourceCandidates.size()){
      return false;
    }
    if(targetCandidates.size() != other.targetCandidates.size()){
      return false;
    }
    for(int idx=0;idx<sourceCandidates.size();idx++){
      if(sourceCandidates.get(idx) != other.sourceCandidates.get(idx)){
	return false;
      }
    }
    for(int idx=0;idx<targetCandidates.size();idx++){
      if(targetCandidates.get(idx) != other.targetCandidates.get(idx)){
	return false;
      }
    }
    return true;
  }
  
  public int hashCode(){
	return edge.hashCode();
  }

  public NeighborEdge reverseEdge(){
    return new NeighborEdge(edge,sourceCandidates,targetCandidates,!reverse);
  }
}
