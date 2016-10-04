package sbr;


import util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.*;

public class SR {

	private final boolean debug = true;
	private final static String[] RoundRobin = { "U", "N", "E", "D", "S", "W" };
	private static int RRIndex[];
	public  int UnitSeg,Index;

	private Graph graph;
	private int nUnitSeg;
	private int nRegSeg;

	private int subNet, maxSN;
	private ArrayList<Segment> segments;
	private ArrayList<Aresta> bridge;

	private List<Vertice> visitedVertices, unvisitedVertices, start, terminal;
	private List<Aresta> visitedArestas, unvisitedArestas;
	//private List<Vertice> visiteds;
	//private List<Vertice> nVisiteds;


	public SR(Graph graph) 
	{	 UnitSeg = 0;
		//graph = new Graph(fileName);
		this.graph = graph;
		if(debug) System.err.println(graph);
		segments = new ArrayList<>();
		visitedVertices = new ArrayList<>();
		unvisitedVertices = new ArrayList<>();
		start = new ArrayList<>();
		visitedArestas = new ArrayList<>();
		unvisitedArestas = new ArrayList<>();
		bridge = new ArrayList<>();
		//nVisiteds = new ArrayList<>();
		nUnitSeg = 0;
		nRegSeg = 0;
		RRIndex = new int[3];
		RRIndex[0] = -1;
		RRIndex[1] = -1;
		RRIndex[2] = -1;
		subNet = 0;
		maxSN = 0;
		new Bridge(graph);
		System.out.println(bridge.size()+" bridges.");
	}
	
	public void encontrar (){

		int dist1 = 0,dist2 = 0 , xMin,xMax , yMin,yMax ,aux = Integer.MAX_VALUE ,hops,auxiliar = Integer.MAX_VALUE;
		Vertice u = new Vertice();
		Vertice v = new Vertice();
		Collections.reverse(graph.getVertices());
		ArrayList<Vertice>Auxiliar = new ArrayList();
		Auxiliar = graph.getVertices();
		int x = 0,y=0;
		int count = 0;
		for (Vertice e : graph.vertices){
			if(e.getDown()){
				count ++;
			}
		}
		if (count > 1){
		
		
		for(Vertice e : Auxiliar){
			if ( Integer.valueOf(e.getNome().split("\\.")[0]) == 0){
				if(e.getTop()){
					xMax = Integer.valueOf(e.getNome().split("\\.")[1]);
					yMax = Integer.valueOf(e.getNome().split("\\.")[2]);
					dist1 = xMax + yMax;

					if(dist1 < auxiliar){
						auxiliar = dist1;

					}

				}}}
			auxiliar = Integer.MAX_VALUE;
			

			for(Vertice f : Auxiliar){

				if(f.getDown()){
					xMin = Integer.valueOf(f.getNome().split("\\.")[1]);
					yMin = Integer.valueOf(f.getNome().split("\\.")[2]);
					dist2 = xMin + yMin;
					if(dist2 < auxiliar){
					auxiliar = dist2;
				
						v = u;
						u = f;


					}
				}}

			
			System.out.println(v.getNome());
			Collections.reverse(graph.getVertices());
			x = Integer.valueOf(v.getNome().split("\\.")[1]);
			y = Integer.valueOf(v.getNome().split("\\.")[2]);
			computeSegments(x,y);
			}
	else
	{
		for (Vertice e : graph.vertices){
				e.setDown(false);
				e.setTop(false);
		}
			
		computeSegments(1,1);
	}}
			
		
	public void computeSegments(int x , int y) {
		// fill not visiteds' list
		unvisitedArestas.addAll(graph.getArestas());
		unvisitedVertices.addAll(graph.getVertices());
		int Nx = graph.dimX()-1;
		int Ny = graph.dimY()-1;
		int Nz = graph.dimZ()-1;
		boolean verificador = false;
		

		/*String min = 0 + "." + 0 + "." + 0;
		String max = Nz + "." + Nx + "." + Ny;
		computeSegments(min, max);
	}*/
		
		
		String min = 0 + "." + 0 + "." + 0;
			for (int k = 0; k <= Nz; k++) {
				
					for(int j = 0; j <= Nx; j++)
				{
						
					if( k==0 ){
					String max = k + "." + (j) + "." + (j);
					
					if(debug) System.err.println("#Min: " + min + " #Max: " + max);
					computeSegments(min, max);
					}
					if(y+j <= Ny && k>0 ){
						verificador = true;
						String max = k + "." + (x+j) + "." + (y+j);
						if(debug) System.err.println("#Min: " + min + " #Max: " + max);
						computeSegments(min, max);
					}
					/*if(y+j <= Ny && k>0 && verificador == true){
						
						String max = k + "." + (Nx) + "." + (y+j);
						if(debug) System.err.println("#Min: " + min + " #Max: " + max);
						computeSegments(min, max);
					}*/
				}
			}		}
		
		
		/*	int countCicles = 0;
			int auxX = Nx - 1 , auxY = Ny - 1 , auxZ = Nz;
			int auxX2 = Nx - 1 , auxY2 = Ny - 1 , auxZ2 = Nz;
			
			while((auxX2 >= 0) || (auxY2 >= 0) || (auxZ2 >= 0))
			{
				
				if(countCicles == 0)
				{
					String min = auxZ + "." + auxX + "." + auxY;
					if(debug) System.err.println("#Min: " + min + " #Max: " + max);
					computeSegments(min, max);
				}
				
				if(countCicles > 3)
				{
					countCicles = 0;
				}
					
					
				if(countCicles == 1)
					{
						if(auxZ > 0)
						{
							auxZ--;
							auxZ2--;
							String min = auxZ + "." + auxX + "." + auxY;
							if(debug) System.err.println("#Min: " + min + " #Max: " + max);
							computeSegments(min, max);
						}
						else
						{
							auxZ2--;;
						}
					}
				if(countCicles == 2)
				{
					if(auxX > 0)
					{
						auxX--;
						auxX2--;
						String min = auxZ + "." + auxX + "." + auxY;
						if(debug) System.err.println("#Min: " + min + " #Max: " + max);
						computeSegments(min, max);
					}
						
					else
					{
						auxX2--;
					}
				}
				if(countCicles == 3)
				{
					if(auxY > 0)
					{
						auxY--;
						auxY2--;
						String min = auxZ + "." + auxX + "." + auxY;
						if(debug) System.err.println("#Min: " + min + " #Max: " + max);
						computeSegments(min, max);
					}
						
					else
					{
						auxY2--;
					}
				}
				
				countCicles++;
		}
	
		
	} 
*/
	private void computeSegments(String min, String max) {

		terminal = new ArrayList<>();
		int zMin = Integer.valueOf(min.split("\\.")[0]);
		int xMin = Integer.valueOf(min.split("\\.")[1]);
		int yMin = Integer.valueOf(min.split("\\.")[2]);
		int zMax = Integer.valueOf(max.split("\\.")[0]);
		int xMax = Integer.valueOf(max.split("\\.")[1]);
		int yMax = Integer.valueOf(max.split("\\.")[2]);

		if (debug)
			System.err.println("Subnet now: " + subNet);

		// Choose the start switch
		boolean first = (xMin + 1  == xMax && yMin + 1  == yMax);
		boolean pair = ((yMin + 1) % 2 == 0);
		Vertice sw;
		Vertice left = graph.getVertice(zMin + "." + xMin + "." + (yMin + 1));
		Vertice right = graph.getVertice(zMax + "." + xMax + "." + (yMin + 1));
		Vertice Inicial = graph.getVertice(0 + "." + 0 + "." + 0);
	
		 
		if(first) {
			sw =  Inicial ;//(pair) ? left : right; 
			setStart(sw);
			sw.setSubNet(subNet);
		}
		
		else {
			sw = nextVisited(min, max);
			if (sw == null) {
				sw = nextNotVisited(min, max,graph);
				if(sw == null) return;
				setStart(sw);
				subNet = ++maxSN;
				sw.setSubNet(subNet);
			}
			subNet = sw.getSubNet();
		}

		Segment sg = new Segment();
		segments.add(sg);

		if (debug) System.err.println("#starting: " + sw.getNome());

		do {
			this.resetRRIndex();
			
			// try to form a segment
			if (find(sw, min, max)) {
				sg = new Segment();
				segments.add(sg);
				if (debug) System.err.println("New Segment.");
			} else if (isVisited(sw)) {
				setTerminal(sw);
				if (debug) System.err.println(sw.getNome() + " is Terminal.");

			} else if (zMin == graph.dimZ() && xMin == graph.dimX() && yMin == graph.dimY()) { // eh a ultima rodada
				visit(sw);
				setTerminal(sw);
				if (debug) System.err.println(sw.getNome() + " is Terminal.");
					
			} else {
			
				unsetStart(sw);
			}
			
			// look for a not visited switch to form the next segment
			sw = nextVisited(min, max);
			if (sw == null) { // if didn't find
				if ((zMin== graph.dimZ() && xMin == graph.dimX()  && yMin == graph.dimY() ) && (sw = nextNotVisited(min, max,graph)) != null) {
					subNet = ++maxSN;
					if (debug) System.err.println("Subnet now: " + subNet);
					segments.get(segments.size()-1).add(sw);// sg.add(sw);
					setStart(sw);
					if (debug)
						System.err.println(sw.getNome() + " is Start.");
					visit(sw);
					sw.setSubNet(subNet);
				} else {
					if (segments.get(segments.size()-1).getLinks().isEmpty()/* sg.getLinks().isEmpty()*/)
						segments.remove(sg);
					return;
				}
			}
		} while (sw != null);

	}

	protected boolean find(Vertice sw, String min, String max) {
	
	
		Segment segm = segments.get(segments.size()-1);
		if (!isVisited(sw)) {
			segm.add(sw);
			//sw.setSegment(segm);
			setTVisited(sw);
		} else if (!sw.belongsTo(subNet) && !(isStart(sw) && isTerminal(sw)))
			return false;
			
		if (debug) System.err.println("Switch now: " + sw.getNome());
		
		ArrayList<Aresta> links = suitableLinks(sw, min, max);
		
		if (links == null) {
			
			if (debug) System.err.println("No Suitable Links found.");
			if(isTVisited(sw)) unsetTVisited(sw);
			//sw.setSegment(null);
			segm.remove(sw);
			return false;
		}

		while (!links.isEmpty()) {
		
			
			if(GetLink()){
				ArrayList<Vertice> caminho = new ArrayList<>();
				int hops = Integer.MAX_VALUE ;
				int aux;
				Vertice Auxiliar = new Vertice();
				Vertice destino = new Vertice();
				for(Vertice e : graph.vertices){
					if(!(e.getNome().equalsIgnoreCase(sw.getNome()))){
						if(!(Integer.valueOf(e.getNome().split("\\.")[0]) == Integer.valueOf(sw.getNome().split("\\.")[0]))){
							if( e.getDown() == true){
								int Xe = Integer.valueOf(e.getNome().split("\\.")[1]);
								int Ye = Integer.valueOf(e.getNome().split("\\.")[2]);
								int Xvert = Integer.valueOf(sw.getNome().split("\\.")[1]);
								int Yvert = Integer.valueOf(sw.getNome().split("\\.")[2]);
								aux = (Xe - Xvert) + (Ye-Yvert);
				
								
								 if((Math.abs(aux) <= hops)){
									hops = (Math.abs(aux));
									destino = e;
									
								
								
								}
							}
						}}
				}

				if(destino.getNome() == null){
					RRIndex[2] = 1;
					if (find(sw, min, max)) {
					return true;
			      	}
					return false;
				}
				
				Vertice nsw = destino;
				
			   if(!isVisited(sw)) visit(sw);
				caminho = graph.reduçãoCaminho(destino, sw);
				
		
			
				if (nsw.isIn(min, max)) { 
				if (((isVisited(nsw) || isStart(nsw)) && nsw.belongsTo(subNet)) || find3D(nsw, min, max,3)) {
				for (Vertice v : caminho){
					System.out.println(v.getNome());
					if(!isVisited(v)) visit(v);
					if(v.getNome().equalsIgnoreCase(sw.getNome()) == true ){
						continue;
					}
			
					segm.add(v);

					if(graph.getAresta1(sw.getNome(), v.getNome()))	{
						segm.add(sw.getAresta(v));
						visit(sw.getAresta(v));
						visit(v.getAresta(sw));
						 System.out.println(graph.getAresta1(sw.getNome(), v.getNome())+">-->-->--->-->-->-->"+graph.getAresta1(v.getNome(), sw.getNome()));
						
					}
						
					
			
					
				}
				if(!isVisited(nsw)) visit(nsw);
				if (isTerminal(nsw) && isStart(nsw) && !nsw.belongsTo(subNet) && (nsw.getSegment() == null)) {
					unsetTerminal(nsw);
					unsetStart(nsw);
				}
				nsw.setSubNet(subNet);
				return true;
				}}
			
				return false;
				
			
			}
			
			
			Aresta ln  = getNextLink(links);
		
			
			links.remove(ln);
			Aresta nl = ln.getDestino().getAresta(ln.getOrigem());
			if (debug) System.err.println("Link now: "+ln.getOrigem().getNome()+" <-> "+ln.getDestino().getNome());
			setTVisited(ln);
			setTVisited(nl);
			segm.add(ln);
			Vertice nsw = ln.other(sw);
			
			if (nsw.isIn(min, max)) { 
				if (((isVisited(nsw) || isStart(nsw)) && nsw.belongsTo(subNet)) || find(nsw, min, max)) {
					visit(ln);
					visit(nl);
					if(!isVisited(sw)) visit(sw);
					if(!isVisited(nsw)) visit(nsw);
					if (isTerminal(nsw) && isStart(nsw) && !nsw.belongsTo(subNet) && (nsw.getSegment() == null)) {
						
						unsetTerminal(nsw);
						unsetStart(nsw);
						//nsw.setSegment(segm);
						segm.add(nsw);
					}

					nsw.setSubNet(subNet);
					return true;
				}
			}
			
			unsetTVisited(ln);
			unsetTVisited(nl);
			segm.remove(ln);
		}
		segm.remove(sw);
		sw.setSegment(null);
		if(isTVisited(sw)) unsetTVisited(sw);

		return false;
	}

	/*
	 * search for a switch marked as visited, belonging to the current subnet,
	 * and with at least one link not marked as visited.
	 */
	protected Vertice nextVisited(String min, String max) {
		ArrayList<Vertice> next = new ArrayList<>();
		// get switches from visiteds' list
		for (int i = visitedVertices.size() - 1; i >= 0; i--) {
			Vertice sw = visitedVertices.get(i);
			if (!isTerminal(sw) && sw.isIn(min, max) && suitableLinks(sw, min, max) != null) {
				
				// agora só retorna se existir mais de um visitado com links
				// favoráveis
				if(next.isEmpty())
					next.add(sw);
				else {
					for(Vertice n : next) {
						if(n.getSubNet() == sw.getSubNet()) {
							if (debug) System.err.println("nextVisited " + n.getNome());
							subNet = n.getSubNet();
							return n;							
						}
					}
					next.add(sw);
				}
			}
		}
		if (debug)
			System.err.println("nextVisited not found for subnet " + subNet);
		return null;
	}

	/*
	 * look for a switch that is not marked as visited not marked as terminal,
	 * and attached to a terminal switch.
	 */
	protected Vertice nextNotVisited(String min, String max , Graph graph) {
		for (Aresta b: graph.getArestas()) {
			Vertice sw = b.getDestino();
			if(!isVisited(sw) && sw.isIn(min, max) && (suitableLinks(sw, min, max) != null)) {
				if (debug) System.err.println("nextNotVisited " + sw.getNome());
				return sw;				
			}
			sw = b.getOrigem();
			if(!isVisited(sw) && sw.isIn(min, max) && (suitableLinks(sw, min, max) != null)) {
				if (debug) System.err.println("nextNotVisited " + sw.getNome());
				return sw;				
			}
		}
		/*
		for (Vertice sw : unvisitedVertices) {
			if (sw.isIn(min, max) && isTerminal(sw)) {
				List<Vertice> lS = sw.getNeighbors();
				for (Vertice s : lS) {
					if (!isVisited(s) && !isTerminal(s) && s.isIn(min, max)) {
						if (debug) System.err.println("nextNotVisited " + s.getNome());
						return s;
					}
				}
			}
		}
		*/
		if (debug)
			System.err.println("nextNotVisited not found");
		return null;
	}

	/*
	 * try to make a small segment by choosing a link, then applying a restriction in the segment,
	 * thus closing any possible cycle. RRIndex keeps track of the last turn
	 */
	
	public void OrganizaSegm(){
		for (Segment segm : segments){
			int count = 0;
			Vertice aux;
			if (segm.test == true){
				aux = segm.getLinks().get(0).getOrigem();
				int Zaux = Integer.valueOf(aux.getNome().split("\\.")[2]);
				for(Vertice e : segm.getSwitchs()){
					int Ze = Integer.valueOf(e.getNome().split("\\.")[0]);
					count++;
					if(Zaux != Ze){
						break;
					}
					
				}
				
				ArrayList<Aresta>auxiliar = new ArrayList<Aresta>();
				auxiliar = segm.getLinks();
				segm.add(auxiliar.get(count));
				segm.getLinks().remove(count);
				
				
				
			}
		}
		
		
		
	}
	protected boolean find3D(Vertice sw, String min, String max,int inteiro) {
		Segment segm = segments.get(segments.size()-1);
		if (!isVisited(sw)) {
			//segm.add(sw);
			//sw.setSegment(segm);
			setTVisited(sw);
		} else if (!sw.belongsTo(subNet) && !(isStart(sw) && isTerminal(sw)))
			return false;

		if (debug) System.err.println("Switch now: " + sw.getNome());

		ArrayList<Aresta> links = suitableLinks(sw, min, max);
		if (links == null) {
			if (debug) System.err.println("No Suitable Links found.");
			if(isTVisited(sw)) unsetTVisited(sw);
			//sw.setSegment(null);
			segm.remove(sw);
			return false;
		}

		while (!links.isEmpty()) {
			Aresta ln = getNextLink(links, inteiro);
			links.remove(ln);
			Aresta nl = ln.getDestino().getAresta(ln.getOrigem());
			if (debug) System.err.println("Link now: "+ln.getOrigem().getNome()+" <-> "+ln.getDestino().getNome());
			setTVisited(ln);
			setTVisited(nl);
			segm.add(ln);
			Vertice nsw = ln.other(sw);
			if (nsw.isIn(min, max)) {
				System.out.println((isVisited(nsw))+"FIND 3D");
				System.out.println(isStart(nsw)+"FIND 3D");
				System.out.println(nsw.belongsTo(subNet)+"FIND 3D");
				System.out.println(find3D(nsw, min, max,3)+"FIND 3D");
				if (((isVisited(nsw) || isStart(nsw)) && nsw.belongsTo(subNet)) || find(nsw, min, max)) {
					visit(ln);
					visit(nl);
					if(!isVisited(nsw)) visit(nsw);
					if (isTerminal(nsw) && isStart(nsw) && !nsw.belongsTo(subNet) && (nsw.getSegment() == null)) {
						unsetTerminal(nsw);
						unsetStart(nsw);
						//nsw.setSegment(segm);
						segm.add(nsw);
					}
					segm.test = true;
					nsw.setSubNet(subNet);
					return true;
				}
			}
			unsetTVisited(ln);
			unsetTVisited(nl);
			segm.remove(ln);
		}
		segm.remove(sw);
		sw.setSegment(null);
		if(isTVisited(sw)) unsetTVisited(sw);

		return false;
	}
	
	protected Aresta getNextLink(ArrayList<Aresta> links) {
		Aresta got = null;
		int index, count = 0;
	//	System.out.println(" \n 1->"+RRIndex[1] +" \n 2->"+ RRIndex [2]);
		
		
		if(RRIndex[1] == -1){
			if ((RRIndex[2] == -1)){ // first choice of this computation
					index = 2;
				}
			else{
				index = (RRIndex[2] + 1) %6;
			}
			
			}
		
					
		else { // other choices
			
			
			index = (RRIndex[1] + 3) % 6;
			if ((index - RRIndex[2]) % 3 == 0) {
				index = (index + 1) % 6;
			}
		}
		
		while (true) {
			for (Aresta ln : links) {
				if (ln.getCor() == RoundRobin[index]) {
					//System.out.println(RoundRobin[index]);
					got = ln;
					break;
				}
			}
			if (got != null)
				break;
			else {
			
					index = (index + 1) % 6;
			}
		}
		// updates the last turn
			RRIndex[0] = RRIndex[1];
			RRIndex[1] = RRIndex[2];
			RRIndex[2]= index;

		
		return got;
	}
	protected Aresta getNextLink(ArrayList<Aresta> links,int inteiro) {
		Aresta got = null;
		int index;
	//	System.out.println(" \n 1->"+RRIndex[1] +" \n 2->"+ RRIndex [2]);
		
		
		if(RRIndex[1] == -1){
			if ((RRIndex[2] == -1)){ // first choice of this computation
					index = inteiro % 6;
				}
			else{
				index =inteiro % 6;
			}
			
			}
		
					
		else { // other choices
			
			
			index = inteiro % 6;
			
		}
		
		while (true) {
			for (Aresta ln : links) {
				if (ln.getCor() == RoundRobin[index]) {
					//System.out.println(RoundRobin[index]);
					got = ln;
					break;
				}
			}
			if (got != null)
				break;
			else {
			
					index = (index + 1) % 6;
			}
		}
		// updates the last turn
			RRIndex[0] = RRIndex[1];
			RRIndex[1] = RRIndex[2];
			RRIndex[2]= index;

		
		return got;
	}
	
	protected boolean GetLink()
	{
		if(RRIndex[2] == 0 )
		return true;
		
		else return false;
	}

	private void resetRRIndex() {
		RRIndex[0] = -1;
		RRIndex[1] = -1;
		RRIndex[2] = -1;
	}

	public void listSegments() {
		int i = 1;

		for (Segment seg : segments) {
			System.err.println("Segment ns" + i++ + ": " + seg);
					}
		
		}
	
	public int getNumSegments() {
		int i = 0;

		for (Segment seg : segments) {
			i++;
					}
		return i;
		
		}
	
	

	public void printRestrictions() {
		File restrictions = new File("Restriction.txt");
		
		try {
			FileWriter wRestrictions = new FileWriter(restrictions);
			BufferedWriter bw = new BufferedWriter(wRestrictions);
			bw.newLine();
			String vetor[] = {"I" , "N" ,"S" , "E" , "W" , "U" , "D"};
			for (Vertice sw : graph.getVertices()) 
			{
				bw.write(sw.getNome()+": ");
				int count = 0;
				for(String rest : sw.getRestrictions()){
					bw.write(vetor[count]+"{"+rest+"} ");
					count ++; 
				}
				bw.newLine();
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void printUnitSeg() {

		try {
			FileWriter unitSeg = new FileWriter(new File("unitSeg"));
			unitSeg.write(Integer.toString(nUnitSeg));

			unitSeg.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public int getnUnitSeg()
	{
		return nUnitSeg;				
	}

	public void printRegSeg() {

		try {
			FileWriter regSeg = new FileWriter(new File("RegSeg"));
			regSeg.write(Integer.toString(nRegSeg));

			regSeg.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void setrestrictions() {
		for (Segment segment : segments) {
			if (segment.getLinks().isEmpty())
				continue;

			if (segment.isUnitary()) {
				nUnitSeg++;
				// No traffic allowed at link
				Vertice Starting = segment.getLinks().get(0).getOrigem();
				Vertice Ending = segment.getLinks().get(0).getDestino();
				//System.err.println("Start: " + Starting.getNome() + " Ending: "+ Ending.getNome());
				// Restricted link
				String opStarting = Starting.getAresta(Ending).getCor();
				String opEnding = Ending.getAresta(Starting).getCor();
				// Restrictions at Starting core
				for (Aresta link : Starting.getAdj())
					if (link.getCor() != opStarting)
						Starting.addRestriction(link.getCor(), opStarting);
				// Restrictions at Ending core
				for (Aresta link : Ending.getAdj())
					if (link.getCor() != opEnding)
						Ending.addRestriction(link.getCor(), opEnding);
				continue;
			}
			// Put it at first or second link
			//if (segment.getSwitchs().size() == 1) {
			if (segment.isRegular()) {
				nRegSeg++;
				segment.getSwitchs()
						.get(0)
						.addRestriction(
								segment.getLinks().get(0).getInvColor(),
								segment.getLinks().get(1).getCor());
				segment.getSwitchs()
						.get(0)
						.addRestriction(segment.getLinks().get(1).getCor(),
								segment.getLinks().get(0).getInvColor());
				continue;
			}
			// At this point we have or starting or regular segment
			/*if (segment.isRegular()) {
				nRegSeg++;
				Vertice restrict = segment.getSwitchs().get(1);
				restrict.addRestriction(
						segment.getLinks().get(1).getInvColor(), segment
								.getLinks().get(2).getCor());
				restrict.addRestriction(segment.getLinks().get(2).getCor(),
						segment.getLinks().get(1).getInvColor());
				continue;
			}*/
			if (segment.isStarting()) {
				Vertice restrict = segment.getSwitchs().get(1);
				restrict.addRestriction(
						segment.getLinks().get(0).getInvColor(), segment
								.getLinks().get(1).getCor());
				restrict.addRestriction(segment.getLinks().get(1).getCor(),
						segment.getLinks().get(0).getInvColor());
			}
		}
	}
	
	private boolean isVisited(Vertice v) {
		return visitedVertices.contains(v);
	}

	public void visit(Vertice v) {
		assert !visitedVertices.contains(v) : "Vertice jah visitado?";
		//assert unvisitedVertices.contains(v) : "Vertice (t)visitado?";
		
		visitedVertices.add(v);
		unvisitedVertices.remove(v);
	}

	private boolean isVisited(Aresta a) {
		return visitedArestas.contains(a);
	}

	private void visit(Aresta a) {
		assert !visitedArestas.contains(a) : "Aresta jah visitada?";
		//assert unvisitedArestas.contains(a) : "Aresta (t)visitada?";
		
		visitedArestas.add(a);
		unvisitedArestas.remove(a);
	}

	private boolean isTVisited(Vertice v) {
		return !visitedVertices.contains(v) && !unvisitedVertices.contains(v);
	}

	private void setTVisited(Vertice v) {
		assert !visitedVertices.contains(v) : "Vertice jah visitado?";
		assert unvisitedVertices.contains(v) : "Vertice jah tvisitado?";
		
		unvisitedVertices.remove(v);
	}

	private boolean isTVisited(Aresta a) {
		return !visitedArestas.contains(a) && !unvisitedArestas.contains(a);
	}

	private void setTVisited(Aresta a) {
		assert unvisitedArestas.contains(a) : "Aresta jah tvisitada?";
		
		unvisitedArestas.remove(a);
	}

	private void unsetTVisited(Aresta a) {
		assert !unvisitedArestas.contains(a) : "Aresta nao tvisitada?";
		
		unvisitedArestas.add(a);
	}

	private void unsetTVisited(Vertice v) {
		assert !unvisitedVertices.contains(v) : "Vertice nao tvisitado?";
		
		unvisitedVertices.add(v);
	}

	private boolean isStart(Vertice v) {
		return start.contains(v);
	}

	private void setStart(Vertice v) {
		assert !start.contains(v) : "Vertice jah start?";
		
		start.add(v);
	}

	private void unsetStart(Vertice v) {
		assert start.contains(v) : "Vertice nao start?";
		
		start.remove(v);
	}

	private boolean isTerminal(Vertice v) {
		return terminal.contains(v);
	}

	private void setTerminal(Vertice v) {
		assert !terminal.contains(v) : "Vertice jah terminal?";
		assert visitedVertices.contains(v) : "Vertice nao tvisitado?";

		terminal.add(v);
	}

	private void unsetTerminal(Vertice v) {
		assert terminal.contains(v) : "Vertice nao terminal?";
		
		terminal.remove(v);
	}
 
	public ArrayList<Aresta> suitableLinks(Vertice v, String min, String max) 
	{
		ArrayList<Aresta> adj = v.getAdj(); 
		if(adj.isEmpty())
			return null;
		
		ArrayList<Aresta> slinks = new ArrayList<>();
		for(Aresta ln : adj) {
			Vertice dst = ln.getDestino();
			boolean cruza = isTVisited(dst) && !isStart(dst);
			boolean bdg = bridge.contains(ln) || bridge.contains(dst.getAresta(ln.getOrigem()));
			if(!isVisited(ln) && !isTVisited(ln) && dst.isIn(min, max) && !cruza && !bdg)
				slinks.add(ln);
		}

		return (slinks.isEmpty())? null : slinks;
	}

	private class Bridge {
		private int cnt; // counter
		private int[] pre; // pre[v] = order in which dfs examines v
		private int[] low; // low[v] = lowest preorder of any vertex connected to v

		public Bridge(Graph G) {
			assert G != null : "Ponteiro nulo para grafo!";
			low = new int[G.getVertices().size()];
			pre = new int[G.getVertices().size()];
			cnt = 0;
			for (int v = 0; v < G.getVertices().size(); v++)
				low[v] = pre[v] = -1;

			for (Vertice v: G.getVertices())
				
				if (pre[G.indexOf(v)] == -1)
					dfs(G, v, v);
		}

		private void dfs(Graph g, Vertice u, Vertice v) {
			assert g != null && u != null && v != null : "Ponteiro(s) nulo(s) para vertice(s) ou grafo!";
			low[g.indexOf(v)] = pre[g.indexOf(v)] = cnt++;
			for(Aresta e : v.getAdj()) {
				Vertice w = e.getDestino();
				if (pre[g.indexOf(w)] == -1) {
					dfs(g, v, w);
					low[g.indexOf(v)] = Math.min(low[g.indexOf(v)], low[g.indexOf(w)]);
					if (low[g.indexOf(w)] == pre[g.indexOf(w)]) {
						bridge.add(e);
					}
				}
				else if (!w.equals(u))
					low[g.indexOf(v)] = Math.min(low[g.indexOf(v)], low[g.indexOf(w)]);
			}
		}
		
	}

}
