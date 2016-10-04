package rbr;

import util.*;
import util.Path.PropWeight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RBR {
	private Graph graph;



	public RBR(Graph g) {
		graph = g;

	}

	// Make log file for each input file
	public void makeLog() {
		// Print all regions of all routers
		for (Vertice router : graph.getVertices()) {
			System.out.println("");
			System.out.println("Router " + router.getNome() + ":\n");
			for (Region r : router.getRegions()) {
				System.out.println(r.getUpRight() + " " + r.getDownLeft()
				+ " Ip: " + r.getIp() + " Op: " + r.getOp());
			}
		}
	}

	// Make stats files
	public void makeStats(ArrayList<ArrayList<Path>> paths) {
		// double[] hopCount = getHopCountStats(paths);
		double[] Regions = getRegionsStats();
		double ard = getRoutingDistance(paths);
		double[] linkWeight = linkWeightStats();
		try {

			FileWriter ardfs = new FileWriter(new File("ard"));
			FileWriter lwMeanfs = new FileWriter(new File("lw-Mean"));
			FileWriter lwStdfs = new FileWriter(new File("lw-Std"));
			FileWriter regionMaxfs = new FileWriter(new File("region-max"));

			ardfs.write("" + ard);
			lwMeanfs.write("" + linkWeight[0]);
			lwStdfs.write("" + linkWeight[1]);
			regionMaxfs.write("" + Regions[0]);

			ardfs.close();
			lwMeanfs.close();
			lwStdfs.close();
			regionMaxfs.close();

		} catch (IOException ex) {
			Logger.getLogger(RBR.class.getName()).log(Level.SEVERE, null,
					ex);
		}

	}


	public void doRoutingTable(String routingTableFile) {
		double[] stats = getRegionsStats();
		File routingTable = new File(routingTableFile);
		int size = (graph.dimX() >= graph.dimY()) ? graph.dimX() : graph.dimY();

		//		int nBits = (int) Math.ceil(Math.log(size) / Math.log(2));
		int nBits = 4;//09/11/2015

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(routingTable));
			bw.append("library IEEE;\n"
					+ "use ieee.std_logic_1164.all;\n"
					+ "use ieee.numeric_std.all;\n"
					//					+ "use work.HermesPackage.all;\n\n"
					+ "use work.PhoenixPackage.all;\n\n"
					+ "package TablePackage is\n\n"
					+ "constant NREG : integer := "
					+ (int) stats[0]
							+ ";\n"
							+ "constant MEMORY_SIZE : integer := NREG;\n\n"
							+ "constant NBITS : integer := "
							+ nBits
							+ ";\n"
							+ "constant CELL_SIZE : integer := 2*NPORT+6*NBITS;\n"
							+ "subtype cell is std_logic_vector(CELL_SIZE-1 downto 0);\n"
							+ "subtype regAddr is std_logic_vector(2*NBITS-1 downto 0);\n"
							+ "type memory is array (0 to MEMORY_SIZE-1) of cell;\n"
							//					+ "type memory is array (0 to MEMORY_SIZE-1) of reg26;\n"
							+ "type tables is array (0 to NROT-1) of memory;\n\n"
							+ "constant TAB: tables :=(");


			for (Vertice router : graph.getVertices()) {
				System.out.println(router.getNome());
				router.PrintRegions(stats, bw, nBits);
				if (graph.getVertices().indexOf(router) != graph.getVertices()
						.size() - 1)
					bw.append(",");
			}

			bw.append("\n);\nend TablePackage;\n\npackage body TablePackage is\n"
					+ "end TablePackage;\n");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Calculate routing distance -> all paths lengths / #paths
	private double getRoutingDistance(ArrayList<ArrayList<Path>> paths) {
		double routingDistance = 0.0;

		for (ArrayList<Path> alp : paths)
			routingDistance += alp.get(0).size();

		// Cover paths with the same source and destination
		routingDistance += graph.getVertices().size();

		return routingDistance / (paths.size() + graph.getVertices().size());
	}

	// Link weight stats [0] - mean / [1] - standard deviation
	public double[] linkWeightStats() {
		double linksWeight = 0.0;
		double[] stats = new double[2];
		double mean = 0.0;
		double std = 0.0;

		for (Aresta link : graph.getArestas())
			linksWeight += (double) link.getWeight();

		mean = linksWeight / (double) graph.getArestas().size();
		stats[0] = mean;

		double temp = 0.0;
		for (Aresta link : graph.getArestas())
			temp += ((double) link.getWeight() - mean)
			* ((double) link.getWeight() - mean);

		double variance = (temp / (double) (graph.getArestas().size()));
		// size-1 for sample. We have population

		std = Math.sqrt(variance);
		stats[1] = std;

		return stats;
	}

	// Calculates the regions stats - [0] - Max / [1] - Min / [2] - Average
	public double[] getRegionsStats() {

		double[] stats = new double[3];
		double average;
		List<Integer> regSizes = new ArrayList<>();

		for (Vertice r : graph.getVertices()) {
			regSizes.add(r.getRegions().size());
		}
		Collections.sort(regSizes);

		int sum = 0;
		for (int size : regSizes) {
			sum += size;
		}
		average = sum / regSizes.size();

		stats[0] = (double) regSizes.get(regSizes.size() - 1);
		stats[1] = (double) regSizes.get(0);
		stats[2] = (double) average;
		return stats;
	}

	// Pack routing options if they have the same input port and the same
	// destination
	private static void packOutputPort(Vertice atual) {
		System.out.println("op group in: " + atual.getNome());
		ArrayList<RoutingPath> actRP = atual.getRoutingPaths();
		atual.setRoutingPaths(new ArrayList<RoutingPath>());
		for (RoutingPath a : actRP) {
			String op = a.getOp();
			String dst = a.getDst();
			String ip = a.getIp();

			for (RoutingPath b : actRP) {
				if (ip.equals(b.getIp()) && dst.equals(b.getDst())) {
					if (!op.contains(b.getOp()))
						op = op.concat(b.getOp());
				}
			}
			atual.addRP(ip, dst, op);
						System.out.println("(" + ip + "," + dst + "," + op + ")");
		}
	}

	// Pack routing options if they have the same output port and the same
	// destination
	public static void packInputPort(Vertice atual) {
		System.out.println("ip group in: " + atual.getNome());
		ArrayList<RoutingPath> actRP = atual.getRoutingPaths();
		atual.setRoutingPaths(new ArrayList<RoutingPath>());
		for (RoutingPath a : actRP) {
			String op = a.getOp();
			String dst = a.getDst();
			String ip = a.getIp();

			for (RoutingPath b : actRP) {
				if (op.equals(b.getOp()) && dst.equals(b.getDst())) {
					if (!ip.contains(b.getIp()))
						ip = ip.concat(b.getIp());
				}
			}
			atual.addRP(ip, dst, op);
			System.out.println("(" + ip + "," + dst + "," + op + ")");
		}
	}



	/*
	 * Nova versao da busca de pacotes. Retorna dividido por par de comunicação
	 * ordenado por tamanho dos caminhos.
	 */
	public ArrayList<ArrayList<Path>> pathsComputation() {
		ArrayList<Path> allPaths = new ArrayList<Path>();
		ArrayList<Path> lastPaths = new ArrayList<Path>();
		ArrayList<String> pairs = new ArrayList<String>();
		// N = 1 hop
		for (Vertice src : graph.getVertices()) {
			for (Aresta e : src.getAdj()) {
				Vertice dst = e.getDestino();
				if (src.getRestriction("I").contains(src.getAresta(dst).getCor())){ // nao eh permitido
					//					System.out.println("LOCAL");
					continue;
				}
				Path p = new Path();
				p.add(src);
				p.add(dst);
				lastPaths.add(p);
				String pair = src.getNome() + ":" + dst.getNome();
				pairs.add(pair);
				//System.out.println(pair + " #"+pairs.size());
			}
		}
		System.out.println("Tamanho: 1"+" - "+lastPaths.size()+" paths.");
		allPaths.addAll(lastPaths);
		//savePathInFile("paths 1 hop", lastPaths);

		int nPairs = graph.dimZ() * graph.dimX() * graph.dimY()
				* (graph.dimZ() *  graph.dimX() * graph.dimY() - 1);
		// N > 1 hop
		while (pairs.size() < nPairs) { // pares cadastrados menor que numero de
			// fluxos
			ArrayList<Path> valid = new ArrayList<Path>(); // actual mininal paths
			//System.out.println("Tamanho atual: " + lastPaths.get(0).size());
			for (Path p : lastPaths) {
				Vertice src = p.dst(); // fonte atual
				Vertice pre = p.get(p.size() - 2); // predecessor
				String inColor = src.getAresta(pre).getCor(); // porta de
				// entrada
				for (Aresta e : src.getAdj()) {
					Vertice dst = e.getDestino();
					if (dst == pre) // esta voltando
						continue;
					//if (p.contains(dst)) // esta cruzando
					//	continue;
					if (src.getRestriction("I").contains(src.getAresta(dst).getCor())){ // nao eh permitido
						//						System.out.println("LOCAL");
						continue;
					}
					if (src.getRestriction(inColor).contains(src.getAresta(dst).getCor())) // nao eh permitido
						continue;
					
					if (pairs.contains(p.src().getNome() + ":" + dst.getNome())) // nao minimo
						continue;

					Path q = new Path(p);
					q.add(dst);
					valid.add(q);
				}
			}
			System.out.println("Tamanho: "+lastPaths.get(0).size()+" - "+valid.size()+" paths. |" + pairs.size() + "/" + nPairs);
			allPaths.addAll(valid);
			//savePathInFile("paths"+lastPaths.get(0).size()+"hops", valid);
			//lastPaths = null;
			lastPaths = valid;
			for (Path p : valid) {
				String pair = p.src().getNome() + ":" + p.dst().getNome();
				if (!pairs.contains(pair)) {
					pairs.add(pair);
					//System.out.println(pair + " #"+pairs.size());					
				}
			}
			valid = null;
		}
		return divideByPair(allPaths);
	}





	public void addRoutingOptions(ArrayList<ArrayList<Path>> paths) {

		//inicializa opcoes de roteamento
		for(Vertice v : graph.getVertices())
			v.initRoutingOptions();

		for(ArrayList<Path> alp : paths) {			
			for (Path path : alp) {
//				System.out.println("path: " + path.toString());
				String dest = path.dst().getNome();
				for (Vertice sw : path) {
					if (path.indexOf(sw) != path.size() - 1) {
//						System.out.println("roteador " + sw.getNome());
						String op = sw.getAresta(path.get(path.indexOf(sw) + 1))
								.getCor();
						String ip = (path.indexOf(sw) == 0) ? "I" : sw.getAresta(
								path.get(path.indexOf(sw) - 1)).getCor();
//								System.out.println("(" + ip + "," + dest + "," + op + ")");
						sw.addRP(ip, dest, op);
					}
				}
			}
		}
		
		for (Vertice atual : graph.getVertices()) {
						
			packOutputPort(atual);
			 packInputPort(atual);

		}
	}

	// Do output combinations
	private static ArrayList<String> getOutputCombinations() {
		ArrayList<String> oPComb = new ArrayList<String>();
		char[] op = "DENSUW".toCharArray();//em ordem alf

		for (int m = 1; m != 1 << op.length; m++) {
			String a = "";
			for (int i = 0; i != op.length; i++) {
				if ((m & (1 << i)) != 0) {
					a = a.concat(Character.toString(op[i]));
				}
			}
			oPComb.add(a);
		}
		//		System.out.println(oPComb);
		return oPComb;
	}
	
	private static ArrayList<String> getInputCombinations() {
		ArrayList<String> ipComb = new ArrayList<String>();
		char[] ip = "DEINSUW".toCharArray();//em ordem alf

		for (int m = 1; m != 1 << ip.length; m++) {
			String a = "";
			for (int i = 0; i != ip.length; i++) {
				if ((m & (1 << i)) != 0) {
					a = a.concat(Character.toString(ip[i]));
				}
			}
			ipComb.add(a);
		}
		//		System.out.println(oPComb);
		return ipComb;
	}

	private int indexOf(String xyz) {
		//		int x = Integer.parseInt(xy.split("\\.")[0]);
		//		int y = Integer.parseInt(xy.split("\\.")[1]);
		//		return x + y*graph.dimX();
		int z = Integer.parseInt(xyz.split("\\.")[0]);
		int x = Integer.parseInt(xyz.split("\\.")[1]);
		int y = Integer.parseInt(xyz.split("\\.")[2]);
		return z*(graph.dimX() + y*graph.dimX()) + (x + y*graph.dimX());
	}

	public void printLengthofPaths(ArrayList<ArrayList<Path>> paths) {
		int dimX = graph.dimX();
		int dimY = graph.dimY();
		int dimZ = graph.dimZ(); //09/11/2015
		int[][] sizePath = new int[dimX * dimY * dimZ][dimX * dimY * dimZ]; //09/11/2015

		for(ArrayList<Path> alp : paths) {
			Path path = alp.get(0);
			int sourceN = indexOf(path.src().getNome()); //sourceX + sourceY * dimX;
			int sinkN = indexOf(path.dst().getNome()); //sinkX + sinkY * dimX;

			sizePath[sourceN][sinkN] = path.size();
		}

		try {
			Formatter output = new Formatter("sizeOfPaths.txt");

			for (int x = 0; x < dimX * dimY; x++) {
				for (int y = 0; y < dimX * dimY; y++) {
					output.format("%d \t", sizePath[x][y]);
				}
				output.format("\r\n");
			}

			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Verify all regions of vertice sw and split each 3D region into N new 2D regions.
	 * N is the number of layers of the topology.
	 * @param sw Vertice that will format the 3D regions.
	 */
//	private void splitInto2DRegions(Vertice sw){
//		ArrayList<Region> newRegions = new ArrayList<>();
//		ArrayList<ArrayList<String>> targets = new ArrayList<>();
//		boolean is3D = false, first = true;
//		int z = 0;
//
//		if(graph.dimZ() == 0){
//			return;
//		}
//
//		for(Region r : sw.getRegions()){
//			for(int i = 0; i < graph.dimZ(); i++){
//				targets.add(new ArrayList<>());
//			}
//
//			for(String zxy : r.getDst()){
//				if(first){
//					z = Integer.parseInt(zxy.split("\\.")[0]);
//					first = false;
//				}else if(z != Integer.parseInt(zxy.split("\\.")[0])){
//					is3D = true;
//					break;
//				}
//			}
//
//			if(!is3D){
//				//				System.out.println("this region is not 3D");
//				newRegions.add(new Region(r.getIp(), r.getDst(), r.getOp()));
//				first = true;
//				is3D = false;
//				targets.clear();
//				continue;
//			}else{
//				//				System.out.println("this region is 3D");
//				for(String zxy : r.getDst()){
//					targets.get(Integer.parseInt(zxy.split("\\.")[0])).add(zxy);
//				}
////								System.out.println(targets);
//				// codigo novo-------------------------------------------------
//				ArrayList<String> opComb = getOutputCombinations();
//				
//				for(ArrayList<String> targetsPerLayer : targets){
//					if(!targetsPerLayer.isEmpty()){
//						//ajeitar ip e op aqui
//						ArrayList<RoutingPath> new2DRegion = new ArrayList<>(); //regioes com ip e op corrigidos
//						for(String dst : targetsPerLayer){
//							for(RoutingPath opcoes : sw.getRoutingPaths()){
//								if(dst == opcoes.getDst()){
//									new2DRegion.add(opcoes);//cria uma lista com as opcoes de caminho para os destinos de targets
//								}
//							}	
//						}
//
//						for (String op : opComb) {
//							String ip = new String(); //ALTERACAO!!
//							ArrayList<String> destinations = new ArrayList<String>();
//							for (RoutingPath rp : new2DRegion) {
//								if (rp.getOp().equals(op)) {
//									if (!destinations.contains(rp.getDst())){
//										destinations.add(rp.getDst());
////										System.out.println("(" + rp.getIp() + "," + rp.getDst() + "," + rp.getOp() + ")");
//									}
//									ip = mergeString(ip, rp.getIp());
////									System.out.println("(" + rp.getIp() + "," + rp.getDst() + "," + rp.getOp() + ")");
//								}
//							}
//							if (destinations.size() != 0) {
//								newRegions.add(new Region(ip, targetsPerLayer, r.getOp()));//novo
//							}
//						}
////						newRegions.add(new Region(r.getIp(), targetsPerLayer, r.getOp()));//antigo
//						
//					}
//				}
//				//codigo novo--------------------------------------------------
//			}
//			is3D = false;
//			targets.clear();
//			first = true;
//		}
//
//		sw.setRegions(newRegions);
//	}
	
	
	
	private void splitInto2DRegions(Vertice sw){
		ArrayList<Region> newRegions = new ArrayList<>();
		ArrayList<ArrayList<String>> targets = new ArrayList<>();
		boolean is3D = false, first = true;
		int z = 0;
		
		if(graph.dimZ() == 0){
			return;
		}
		
		for(Region r : sw.getRegions()){
			for(int i = 0; i < graph.dimZ(); i++){
				targets.add(new ArrayList<>());
			}
			
			for(String zxy : r.getDst()){
				if(first){
					z = Integer.parseInt(zxy.split("\\.")[0]);
					first = false;
				}else if(z != Integer.parseInt(zxy.split("\\.")[0])){
					is3D = true;
					break;
				}
			}
			
			if(!is3D){
//				System.out.println("this region is not 3D");
				newRegions.add(new Region(r.getIp(), r.getDst(), r.getOp()));
				first = true;
				is3D = false;
				targets.clear();
				continue;
			}else{
//				System.out.println("this region is 3D");
				for(String zxy : r.getDst()){
					targets.get(Integer.parseInt(zxy.split("\\.")[0])).add(zxy);
				}
//				System.out.println(targets);
				for(ArrayList<String> targetsPerLayer : targets){
					if(!targetsPerLayer.isEmpty()){
						newRegions.add(new Region(r.getIp(), targetsPerLayer, r.getOp()));
					}
				}
			}
			is3D = false;
			targets.clear();
			first = true;
		}
		
		sw.setRegions(newRegions);
	}
	

	// Compute the regions
	public void regionsComputation() {
		ArrayList<String> opComb = getOutputCombinations();
		String ip;
		for (Vertice sw : graph.getVertices()) {			
			sw.initRegions();
//			ip = new String();
			System.out.println("sw " + sw.getNome());
			for (String op : opComb) {
				ip = new String(); //ALTERACAO!!
				ArrayList<String> destinations = new ArrayList<String>();
				for (RoutingPath rp : sw.getRoutingPaths()) {
					if (rp.getOp().equals(op)) {
						if (!destinations.contains(rp.getDst())){
							destinations.add(rp.getDst());
//							System.out.println("(" + rp.getIp() + "," + rp.getDst() + "," + rp.getOp() + ")");
						}
						ip = mergeString(ip, rp.getIp());
						System.out.println("(" + rp.getIp() + "," + rp.getDst() + "," + rp.getOp() + ") opcomb: " + op);
					}
				}
				if (destinations.size() != 0) {
					sw.addRegion(ip, destinations, op);
				}
			}
			//			System.out.println(sw.getNome());
			for (Region reg : sw.getRegions()) {
				reg.setextrems();
				System.out.println("(" + reg.getIp() + ",{" + reg.getDownLeft() + ","+ reg.getUpRight() + "}," + reg.getOp() + ")");
			}

			//System.out.println("doing split 3d in sw " + sw.getNome());
			//splitInto2DRegions(sw);
			for (Region reg : sw.getRegions()) {
				System.out.println("(" + reg.getIp() + ",{" + reg.getDownLeft() + ","+ reg.getUpRight() + "}," + reg.getOp() + ")");
			}

		}
		adjustsRegions();
//		for (Vertice sw : graph.getVertices()) {
//			System.out.println(sw.getNome());
//			for (Region reg : sw.getRegions()) {
////				System.out.println("(" + reg.getIp() + ",{" + reg.getDownLeft() + ","+ reg.getUpRight() + "}," + reg.getOp() + ")");
//			}
//		}
	}
	
	/**
	 * Compute the regions as well as the RBR theory specifies.
	 */
	public void regionsComputationFor3D(){
		ArrayList<String> opComb = getOutputCombinations();
		ArrayList<String> ipComb = getInputCombinations();
		for (Vertice sw : graph.getVertices()) {			
			sw.initRegions();
			System.out.println("roteador " + sw.getNome());
			for(String ip : ipComb){
				for (String op : opComb) {
					ArrayList<String> destinations = new ArrayList<String>();
//					System.out.println(ip + " " + op);
					for (RoutingPath rp : sw.getRoutingPaths()) {
						if((rp.getIp().equals(ip)) && (rp.getOp().equals(op))){
							if(!destinations.contains(rp.getDst())){ //same ip and same op
								destinations.add(rp.getDst());
								System.out.println("(" + rp.getIp() + "," + rp.getDst() + "," + rp.getOp() + ") ipcomb: " + ip + " opcomb: " + op);
								
							}
						}
//						System.out.println("contem? " + destinations.contains(rp.getDst()));					
					}
					
					if (destinations.size() != 0) {
						sw.addRegion(ip, destinations, op);
					}
				}
			}
			System.out.println("setando extremos");
			for (Region reg : sw.getRegions()) {
				reg.setextrems();
				System.out.println("(" + reg.getIp() + ",{" + reg.getDownLeft() + ","+ reg.getUpRight() + "}," + reg.getOp() + ")");
			}

			System.out.println("doing split 3d in sw " + sw.getNome());
			splitInto2DRegions(sw);
//			
//			for (Region reg : sw.getRegions()) {
//				System.out.println("(" + reg.getIp() + ",{" + reg.getDownLeft() + ","+ reg.getUpRight() + "}," + reg.getOp() + ")");
//			}

		}
		adjustsRegions();

	}

	// Adjust the regions to avoid overlap
	private void adjustsRegions() {
		for (Vertice sw : graph.getVertices()) {
			ArrayList<Region> regionsTemp = new ArrayList<>();
			ArrayList<Region> regionsRemov = new ArrayList<>();
			for (Region reg : sw.getRegions()) {
								System.out.println(sw.getNome());
				ArrayList<String> strgs = getStranges(reg);
								System.out.println("getStranges() OK");
				if (strgs != null) {
					String[] extrems = getExtrems(strgs);
										System.out.println("getExtrems() OK");
					String[] Min = extrems[0].split("\\.");
					int zmin = Integer.valueOf(Min[0]);
					int xmin = Integer.valueOf(Min[1]);
					int ymin = Integer.valueOf(Min[2]);
					String[] Max = extrems[1].split("\\.");
					int zmax = Integer.valueOf(Max[0]);
					int xmax = Integer.valueOf(Max[1]);
					int ymax = Integer.valueOf(Max[2]);
					//					System.out.println("{" + zmin + xmin + ymin + "," + zmax + xmax + ymax + "}");
					ArrayList<String> dests = reg.getDst(zmin, xmin, ymin, zmax, xmax, ymax);
					System.out.println("getDst() OK");
					
					if (nSides(reg, strgs) == 3 ) {
						deleteFromRegion(extrems, reg);
												System.out.println("deleteFromRegion() OK");
						reg.setextrems();
					} else {
						regionsRemov.add(reg);
						ArrayList<ArrayList<String>> dsts = getDestinations(zmin,zmax,
								xmin, xmax, ymin, ymax, reg);
												System.out.println("getDestinations() OK");
						if (dsts != null) {
							for (ArrayList<String> dst : dsts) {
								Region r = new Region(reg.getIp(), dst,
										reg.getOp());
								//r.setextrems();
								regionsTemp.add(r);
							}
						}

					}
					// use others routers to make others regions
					if (dests != null){
												//System.out.println("pronto");
						regionsTemp.addAll(makeRegions(dests, reg.getIp(),reg.getOp()));
												//System.out.println("pronto");
					}

				}
			}

			sw.getRegions().removeAll(regionsRemov);
			sw.getRegions().addAll(regionsTemp);
		}
				System.out.println("adjustRegions() OK");
	}

	
	
	private static boolean touchLeft(int xmin, Region reg) {
		return (xmin == reg.getXmin());
	}

	private static boolean touchRight(int xmax, Region reg) {
		return (xmax == reg.getXmax());
	}

	private static boolean touchUp(int ymax, Region reg) {
		return (ymax == reg.getYmax());
	}

	private static boolean touchDown(int ymin, Region reg) {
		return (ymin == reg.getYmin());
	}

	// [0] - DownLeft [1]- UpRight
	private static String[] getExtrems(ArrayList<String> dsts) {
		String[] xtrems = new String[2];
		int xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE , zMin = Integer.MAX_VALUE;
		int xMax = 0, yMax = 0 , zMax = 0 ;

		for (String s : dsts) {
			String[] zxy = s.split("\\.");
			int x = Integer.valueOf(zxy[1]);
			int y = Integer.valueOf(zxy[2]);
			int z = Integer.valueOf(zxy[0]);


			zMin = (zMin < z) ? zMin : z;
			zMax = (zMax > z) ? zMax : z;
			xMin = (xMin < x) ? xMin : x;
			yMin = (yMin < y) ? yMin : y;
			xMax = (xMax > x) ? xMax : x;
			yMax = (yMax > y) ? yMax : y;
		}

		xtrems[1] = zMax + "." + xMax + "." + yMax;
		xtrems[0] = zMin + "." + xMin + "." + yMin;
		//		System.out.println("min(" + zMin + "." + xMin + "." + yMin + ")");
		//		System.out.println("max(" + zMax + "." + xMax + "." + yMax + ")");
		return xtrems;

	}

	// Return number of common sides of the box formed by strangers and the
		// region
		private static int nSides(Region reg, ArrayList<String> strgs) {
			String[] strgsXtrems = getExtrems(strgs);
			int sides = 0;

			if (Integer.parseInt(strgsXtrems[0].split("\\.")[1]) == reg.getXmin())
				sides++;
			if (Integer.parseInt(strgsXtrems[0].split("\\.")[2]) == reg.getYmin())
				sides++;
			if (Integer.parseInt(strgsXtrems[1].split("\\.")[1]) == reg.getXmax())
				sides++;
			if (Integer.parseInt(strgsXtrems[1].split("\\.")[2]) == reg.getYmax())
				sides++;

			return sides;

		}

	// Delete routers inside of box defined by extremes
	private static void deleteFromRegion(String[] extrems, Region reg) {

		String[] Min = extrems[0].split("\\.");
		int zmin = Integer.valueOf(Min[0]);//19/11/2015
		int xmin = Integer.valueOf(Min[1]);
		int ymin = Integer.valueOf(Min[2]);
		String[] Max = extrems[1].split("\\.");
		int zmax = Integer.valueOf(Max[0]);//19/11/2015
		int xmax = Integer.valueOf(Max[1]);
		int ymax = Integer.valueOf(Max[2]);
		for(int k = zmin; k <= zmax; k++){//19/11/2015
			for (int i = xmin; i <= xmax; i++) {
				for (int j = ymin; j <= ymax; j++) {
					String dst = k + "." + i + "." + j;
					reg.getDst().remove(dst);
				}
			}
		}
	}

	// Return wrong destinations
	private static ArrayList<String> getStranges(Region reg) {
		ArrayList<String> strg = new ArrayList<String>();
		int xmin = reg.getXmin(), xmax = reg.getXmax();
		int ymin = reg.getYmin(), ymax = reg.getYmax();
		int zmin = reg.getZmin(), zmax = reg.getZmax();
		for (int z = zmin; z <= zmax; z++) 
			for (int x = xmin; x <= xmax; x++) {
				for (int y = ymin; y <= ymax; y++) {
					String dest = z + "." + x + "." + y;
					if (!reg.getDst().contains(dest)) {
						strg.add(dest);
					}
				}
			}
		if (strg.size() == 0)
			strg = null;
		return strg;

	}
	
	

	// Make regions only with correct destinations
	private static ArrayList<Region> makeRegions(ArrayList<String> dsts, String ip,
			String op) {
		System.err.println("------------------==============MAKEREGIONS================----------------");
		ArrayList<Region> result = new ArrayList<Region>();
		String[] extrems = getExtrems(dsts);
		String[] Min = extrems[0].split("\\.");
		int Zmin = Integer.valueOf(Min[0]);
		int Xmin = Integer.valueOf(Min[1]);
		int Ymin = Integer.valueOf(Min[2]);
		String[] Max = extrems[1].split("\\.");
		int Zmax = Integer.valueOf(Max[0]);
		int Xmax = Integer.valueOf(Max[1]);
		int Ymax = Integer.valueOf(Max[2]);
		
		
		int Amin = Zmin, Amax = Zmax;
		for (int altura = Amin; altura <= Amax; altura++){
		while (!(dsts.isEmpty())) {
		
			int Cmin = Xmin, Lmax = Ymax;
			int Lmin = Ymin, Cmax = Xmax; 
			boolean first = true;
			//System.out.println("ACABOU!");
			
				for (int line = Lmax; line >= Lmin; line--) {
					for (int col = Cmin; col <= Cmax; col++) {
						
				
						if (first) {
							
							if (dsts.contains(altura +"." + col + "." + line)) {
								Cmin = col;
								Lmax = line;
								Amax = altura;
								first = false;
								System.out.println("\n First ->"+altura +"." + col + "." + line);
							}
						} else {
							System.out.println("MAXIMOS"+ "----->"+Amax +"." + Cmax + "." + Lmax);
							System.out.println("MINIMOS"+ "----->"+Amin +"." + Cmin + "." + Lmin);
			
							System.out.println("Others ->"+altura +"." + col + "." + line);
							if (!(dsts.contains(altura +"."+col + "." + line))) { // if stranger
																System.out.println("STRANGER");
							System.out.println("Others ->"+altura +"." + col + "." + line);
																
								if (line == Lmax ) { // first line
									Cmax = col - 1;
								} else if (col > (Cmax - Cmin) / 2 && col > Cmin) {
									Cmax = col - 1;
								} else {
									Lmin = ++line;
								}

								if (line == Lmin) { // last line
									System.out.println("OthersNew ->"+altura +"." + col + "." + line);
									Region rg = montaRegiao(altura,Cmin, Lmin, Cmax, Lmax,
											ip, op);
									dsts.removeAll(rg.getDst());
									result.add(rg);
									System.out.println("Line = Lmin");
								}
								System.out.println("BREAK!");
								break;
							}
						}
						System.out.println("TESTE ->"+altura +"." + col + "." + line);
						System.out.println("TESTE2 ->"+Amax +"." + Cmax + "." + Lmin);
						if (line == Lmin && col == Cmax ) { // last line
							Region rg = montaRegiao(altura,Cmin, Lmin, Cmax, Lmax, ip, op);
							dsts.removeAll(rg.getDst());
							result.add(rg);
							System.out.println("line == Lmin && col == Cmax && altura == Amax");
						}
					
					}
				}}
		}
		return result;
	}

	private static Region montaRegiao(int z,int xmin, int ymin,  int xmax, int ymax,
			String ip, String op) {
		ArrayList<String> dst = new ArrayList<String>();
		//		System.out.println("zmin = " + zmin + "xmin = " + xmin + "ymin = " + ymin);
		//		System.out.println("zmax = " + zmax + "xmax = " + xmax + "ymax = " + ymax);
		
			for (int x = xmin; x <= xmax; x++)
				for (int y = ymin; y <= ymax; y++)
					dst.add(z + "."+ x + "." + y);
		//		System.out.println("(" + ip + ",{" + dst + "}," + op + ")");
		return (new Region(ip, dst, op));
	}

	// Check if regions r1 and r2 can be merged
	private static boolean CanBeMerged(Region r1, Region r2) {
		boolean canBeMerged = false;

		if (AreNeighbours(r1, r2) && FormBox(r1, r2) && OpIsSub(r1, r2)) {
			canBeMerged = true;
		}

		return canBeMerged;
	}

	// Calculates reachability
	private double reachability(Vertice orig) {
		double reaches = 0, total = graph.getVertices().size() - 1;
		for (Vertice dest : graph.getVertices()) {
			if (orig != dest) {
				if (orig.reaches(dest)) {
					reaches++;
				}
			}
		}
		return (reaches / total);
	}

	public void merge(double reachability) {
		for (Vertice vertice : graph.getVertices()){
			merge(vertice, reachability);
//			System.out.println(vertice.getNome());
			for(Region r : vertice.getRegions()){
//				System.out.println("(" + r.getIp() + ",{" + r.getDownLeft() + ","+ r.getUpRight() + "}," + r.getOp() + ")");
			}
		}

	}

	// Merge the regions of a router
	private void merge(Vertice router, double reachability) {
		ArrayList<Region> bkpListRegion = null;
		boolean wasPossible = true;

		while (reachability(router) >= reachability && wasPossible) {
			bkpListRegion = new ArrayList<Region>(router.getRegions());
			wasPossible = mergeUnitary(router);
		}
		if (bkpListRegion != null)
			router.setRegions(bkpListRegion);

	}

	/*
	 * Tries to make one (and only one) merge and returns true in case of
	 * success
	 */
	private static boolean mergeUnitary(Vertice router) {
		for (int a = 0; a < router.getRegions().size(); a++) {
			Region ra = router.getRegions().get(a);
			for (int b = a + 1; b < router.getRegions().size(); b++) {
				Region rb = router.getRegions().get(b);

				if (CanBeMerged(ra, rb)) {
					String upRight = getUpRightMerged(ra, rb);
					String downLeft = getDownLeftMerged(ra, rb);
					String op = getOpMerged(ra, rb);
					String ip = getIpMerged(ra, rb);

					Region reg = new Region(ip, ra.getDst(), op);
					reg.setUpRight(upRight);
					reg.setDownLeft(downLeft);
					reg.getDst().addAll(rb.getDst());
					reg.setSize();

					router.getRegions().add(reg);
					router.getRegions().remove(ra);
					router.getRegions().remove(rb);

					Collections.sort(router.getRegions());

					return true;
				}
			}
		}
		return false;
	}

	// Return UpRight identifier after merge
	private static String getUpRightMerged(Region r1, Region r2) {
		String upRight;

		upRight = Integer.toString(Math.max(
				Integer.parseInt(r1.getUpRight().split("\\.")[0]),
				Integer.parseInt(r2.getUpRight().split("\\.")[0])))
				+ "."
				+ Integer.toString(Math.max(
						Integer.parseInt(r1.getUpRight().split("\\.")[1]),
						Integer.parseInt(r2.getUpRight().split("\\.")[1])))
				+ "."
				+ Integer.toString(Math.max(
						Integer.parseInt(r1.getUpRight().split("\\.")[2]),
						Integer.parseInt(r2.getUpRight().split("\\.")[2])));

		return upRight;
	}

	// Return DownLeft identifier after merge
	private static String getDownLeftMerged(Region r1, Region r2) {
		String downLeft;

		downLeft = Integer.toString(Math.min(
				Integer.parseInt(r1.getDownLeft().split("\\.")[0]),
				Integer.parseInt(r2.getDownLeft().split("\\.")[0])))
				+ "."
				+ Integer.toString(Math.min(
						Integer.parseInt(r1.getDownLeft().split("\\.")[1]),
						Integer.parseInt(r2.getDownLeft().split("\\.")[1])))
				+ "."
				+ Integer.toString(Math.min(
						Integer.parseInt(r1.getDownLeft().split("\\.")[2]),
						Integer.parseInt(r2.getDownLeft().split("\\.")[2])));

		return downLeft;
	}

	// return the Output ports after merge
	private static String getOpMerged(Region r1, Region r2) {
		String op;

		if (r1.getOp().contains(r2.getOp())) {
			op = r2.getOp();
		} else {
			op = r1.getOp();
		}

		return op;
	}

	// return the Input ports after merge
	private static String getIpMerged(Region r1, Region r2) {
		String ip = new String(r2.getIp());

		for (int i = 0; i < r1.getIp().length(); i++) {
			if (!ip.contains(r1.getIp().substring(i, i + 1)))
				ip += r1.getIp().substring(i, i + 1);
		}
		return ip;
	}

	private static String mergeString(String s1, String s2) {
		String ip = new String(s2);

		for (int i = 0; i < s1.length(); i++) {
			if (!ip.contains(s1.substring(i, i + 1)))
				ip += s1.substring(i, i + 1);
		}
//		System.out.println("conc IP: " + ip);
		return ip;
	}

	// Check if regions r1 and r2 are neighbours
	private static boolean AreNeighbours(Region r1, Region r2) {
		boolean areNeighbours = false;

		int Zmax1 = Integer.parseInt(r1.getUpRight().split("\\.")[0]);
		int Zmax2 = Integer.parseInt(r2.getUpRight().split("\\.")[0]);
		int Xmax1 = Integer.parseInt(r1.getUpRight().split("\\.")[1]);
		int Xmax2 = Integer.parseInt(r2.getUpRight().split("\\.")[1]);
		int Ymax1 = Integer.parseInt(r1.getUpRight().split("\\.")[2]);
		int Ymax2 = Integer.parseInt(r2.getUpRight().split("\\.")[2]);

		int Zmin1 = Integer.parseInt(r1.getDownLeft().split("\\.")[0]);
		int Zmin2 = Integer.parseInt(r2.getDownLeft().split("\\.")[0]);
		int Xmin1 = Integer.parseInt(r1.getDownLeft().split("\\.")[1]);
		int Xmin2 = Integer.parseInt(r2.getDownLeft().split("\\.")[1]);
		int Ymin1 = Integer.parseInt(r1.getDownLeft().split("\\.")[2]);
		int Ymin2 = Integer.parseInt(r2.getDownLeft().split("\\.")[2]);

		if((Zmax1 != Zmax2) && (Zmin1 != Zmin2)){
			//			System.out.println("different layers");
			return areNeighbours;
		}

		//		System.out.println("same layer");
		if (Xmax1 > Xmax2) {
			if (Xmin1 == Xmax2 + 1)
				areNeighbours = true;
		}

		if (Xmax1 < Xmax2) {
			if (Xmin2 == Xmax1 + 1)
				areNeighbours = true;
		}

		if (Ymax1 > Ymax2) {
			if (Ymax2 == Ymin1 - 1)
				areNeighbours = true;
		}

		if (Ymax1 < Ymax2) {
			if (Ymax1 == Ymin2 - 1)
				areNeighbours = true;
		}
		return areNeighbours;
	}

	// Check if regions form a box
	private static boolean FormBox(Region r1, Region r2) {

		if ((Integer.parseInt(r1.getUpRight().split("\\.")[1]) == Integer
				.parseInt(r2.getUpRight().split("\\.")[1]) && Integer
				.parseInt(r1.getDownLeft().split("\\.")[1]) == Integer
				.parseInt(r2.getDownLeft().split("\\.")[1]))
				|| (Integer.parseInt(r1.getUpRight().split("\\.")[2]) == Integer
				.parseInt(r2.getUpRight().split("\\.")[2]) && Integer
				.parseInt(r1.getDownLeft().split("\\.")[2]) == Integer
				.parseInt(r2.getDownLeft().split("\\.")[2]))) {
			return true;
		}

		return false;
	}

	// Check if output port are subsets
	private static boolean OpIsSub(Region r1, Region r2) {

		String r1Op = Vertice.sortStrAlf(r1.getOp());
		String r2Op = Vertice.sortStrAlf(r2.getOp());
		if (r1Op.contains(r2Op) || r2Op.contains(r1Op)) {
			return true;
		}

		return false;
	}

	public ArrayList<ArrayList<Path>> divideByPair(ArrayList<Path> paths) {
		ArrayList<ArrayList<Path>> paths2 = new ArrayList<ArrayList<Path>>();
		ArrayList<Path> aux = new ArrayList<Path>();
		Collections.sort(paths, new Path.SrcDst()); // por par
		Collections.sort(paths); // soh pelo comprimento
		for(int i = 0; i < paths.size(); i++){
			Path act = paths.get(i);
			aux.add(act);
			if(i < paths.size()-1) {
				Path next = paths.get(i+1);
				if(!act.src().equals(next.src()) || !act.dst().equals(next.dst())) {
					paths2.add(aux);
					aux = new ArrayList<Path>();
				}
			}
			else
				paths2.add(aux);
		}
		return paths2;
	}

	public double linkWeightMean(ArrayList<ArrayList<Path>> paths) {
		double acc = 0;
		for(ArrayList<Path> alp : paths) {
			Path path = alp.get(0);
			acc += ((double)path.size()-1.0)*path.volume();
		}
		return acc/(double)graph.getArestas().size();
	}

	public double pathWeightMean(ArrayList<ArrayList<Path>> paths) {
		double acc = 0;
		for(ArrayList<Path> alp : paths)
			acc += (double) (alp.get(0).size()-1);
		return acc*linkWeightMean(paths)/(double)paths.size();
	}
	public ArrayList<Path> XYZpaths (){
		
		ArrayList<Path> caminhos = new ArrayList<Path>();
		int index,dist;
		index = 0;
		for(Vertice v : graph.vertices){
			int vx,vy,vz;
			vx = Integer.parseInt(v.getNome().split("\\.")[1]);
			vy = Integer.parseInt(v.getNome().split("\\.")[2]);
			vz = Integer.parseInt(v.getNome().split("\\.")[0]);
			 for(Vertice u : graph.vertices){
				 Path caminho = new Path();
				
				 int ux,uy,uz;
				 ux = Integer.parseInt(u.getNome().split("\\.")[1]);
				 uy = Integer.parseInt(u.getNome().split("\\.")[2]);
				 uz = Integer.parseInt(u.getNome().split("\\.")[0]);
				if(v.getNome() != u.getNome()){
					
				
				 if(vx != ux ){
					
					 if(vx > ux){
						 dist = vx - ux;
						do{
							String s = (vz +"."+(vx - index)+"."+vy);
							 caminho.add(new Vertice(s));
							 index ++;;
						 } while(index <= dist);
					 }
					 
					 if(vx < ux){
						 index = 0;
						 dist = ux - vx;
						 while(index < dist){
							 caminho.add(new Vertice(vz +"."+(vx + index)+"."+vy));
							 index ++;;
						 }
					 }
					 
				 }
				 
				 if(vy != uy ){
					 if(vy > uy){
						 index = 0;
						 dist = vy - uy;
						 while(index < dist){
							 caminho.add(new Vertice(vz+"."+ vx +"."+(vy - index)));
							 index ++;
						 }
					 }
					 
					 if(vy < uy){
						 index = 0;
						 dist = uy - vy;
						 while(dist - index > 0 ){
							 caminho.add(graph.getVertice(vz +"."+ vx +"."+(vy+index)));
							 index ++;
						 } 
					 }
					 
				 }
				 
				 
				 if(vz != uz ){
					 if(vz > uz){
						 index = 0;
						 dist = vz - uz;
						 while(index < dist){
							 caminho.add(new Vertice((vz-index)+"."+vx+"."+vy));
							 index ++;;
						 }
					 }
					 
					 if(vz < uz){
						 index = 0;
						 dist = uz - vz;
						 while(index < dist){
							 caminho.add(new Vertice((vz+index) +"."+vx+"."+vy));
							 index ++;;
						 }
					 }
					 
				 }
				if(caminho != null){
				 caminhos.add(caminho);
				}
			 }
				 }
		}
		return caminhos;
		
	}

	public void setVolume(ArrayList<ArrayList<Path>> paths, File commvol) {

		int N = graph.dimX()*graph.dimY();
		double[][] vol = new double[N][N];
		double maxVol = 0;

		try {
			Scanner sc = new Scanner(new FileReader(commvol));

			for(int i = 0; i < N; i++) {
				String[] lines = sc.nextLine().split(" \t");
				for(int j = 0; j < N; j++) {
					vol[i][j] = Double.valueOf(lines[j]);
					maxVol = (vol[i][j] > maxVol) ? vol[i][j] : maxVol;
				}
			}
			sc.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for(ArrayList<Path> alp : paths) {
			int i = indexOf(alp.get(0).src().getNome());
			int j = indexOf(alp.get(0).dst().getNome());
			double volume = vol[i][j];
			for(Path path : alp) {
				path.setVolume(volume/maxVol);
			}
		}
	}
	
	
	
	
	// Get destinations depending on the min and max from region and from
	// excluded box
	private static ArrayList<ArrayList<String>> getDestinations(int zmin , int zmax ,int xmin, int xmax,
			int ymin, int ymax, Region reg) {
		ArrayList<ArrayList<String>> dsts = new ArrayList<>();
		ArrayList<String> dstTemp1 = new ArrayList<>();
		ArrayList<String> dstTemp2 = new ArrayList<>();
		ArrayList<String> dstTemp3 = new ArrayList<>();
		ArrayList<String> dstTemp4 = new ArrayList<>();
		boolean left = touchLeft(xmin, reg);
		boolean right = touchRight(xmax, reg);
		boolean up = touchUp(ymax, reg);
		boolean down = touchDown(ymin, reg);

		if (left && down && !up && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (left && up && !right && !down) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y < ymin)
					dstTemp2.add(dst);
			}
		} else if (right && up && !left && !down) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y < ymin)
					dstTemp2.add(dst);
			}
		} else if (right && down && !left && !up) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (up && down && !right && !left) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (x > xmax)
					dstTemp2.add(dst);
			}
		} else if (left && right && !up && !down) {
			for (String dst : reg.getDst()) {
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (y < ymin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
			}
		} else if (left && !up && !down && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x > xmax)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
				else if (y < ymin)
					dstTemp3.add(dst);
			}
		} else if (right && !left && !down && !up) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (y > ymax)
					dstTemp2.add(dst);
				else if (y < ymin)
					dstTemp3.add(dst);
			}
		} else if (down && !up && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (y > ymax)
					dstTemp1.add(dst);
				else if (x < xmin)
					dstTemp2.add(dst);
				else if (x > xmax)
					dstTemp3.add(dst);
			}
		} else if (up && !down && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (y < ymin)
					dstTemp1.add(dst);
				else if (x < xmin)
					dstTemp2.add(dst);
				else if (x > xmax)
					dstTemp3.add(dst);
			}
		} else if (!up && !down && !left && !right) {
			for (String dst : reg.getDst()) {
				int x = Integer.parseInt(dst.split("\\.")[0]);
				int y = Integer.parseInt(dst.split("\\.")[1]);
				if (x < xmin)
					dstTemp1.add(dst);
				else if (x > xmax)
					dstTemp2.add(dst);
				else if (y > ymax)
					dstTemp3.add(dst);
				else
					dstTemp4.add(dst);
			}
		} else {
			;// System.err.println("Severe Error: total overlap!!");
		}
		if (dstTemp1.size() != 0)
			dsts.add(dstTemp1);
		if (dstTemp2.size() != 0)
			dsts.add(dstTemp2);
		if (dstTemp3.size() != 0)
			dsts.add(dstTemp3);
		if (dstTemp4.size() != 0)
			dsts.add(dstTemp4);
		if (dsts.size() == 0)
			dsts = null;
		return dsts;
	}

	
	
	

	
}
