import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import rbr.RBR;
import sbr.SR;
import util.Graph;
import util.Path;

public class rsbr {

	public static void main(String[] args) throws IOException {
		double Count = 0;
		double count2 = 0;
		int min = 99999999;
		int max = -20;
		ArrayList<Integer> ValoresUnitarios = new ArrayList<>();
		double auxDesvio = 0;
		
	/*
	 * VALORES DAS CAMADAS.
	 * DESSA FORMA NÃO É PRECISO MODIFICAR UM A UM NO CÓDIGO
	 */
		 final int z = 2,
				 x = 8,
				 y = 8,
				 perc=50;
		 
		 
		 String top = "2X3X3";
		 String top1 = "3x3x2";
	/*	 
		 	File file = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/Segmentos Unitarios.txt");
			FileWriter esc = new FileWriter(file);
			BufferedWriter bf = new BufferedWriter(esc);
			
			File file2 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/Segmentos Totais.txt");
			FileWriter esc2 = new FileWriter(file2);
			BufferedWriter bf2 = new BufferedWriter(esc2);
			
			File file3 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/regioesMax.txt");
			FileWriter esc3 = new FileWriter(file3);
			BufferedWriter bf3 = new BufferedWriter(esc3);
			
			File file4 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/regioesMed.txt");
			FileWriter esc4 = new FileWriter(file4);
			BufferedWriter bf4 = new BufferedWriter(esc4);
			
			File file5 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/regioesMin.txt");
			FileWriter esc5 = new FileWriter(file5);
			BufferedWriter bf5 = new BufferedWriter(esc5);
			
			File file6 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/ARD.txt");
			FileWriter esc6 = new FileWriter(file6);
			BufferedWriter bf6 = new BufferedWriter(esc6);
			
			File file7 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/LW.txt");
			FileWriter esc7 = new FileWriter(file7);
			BufferedWriter bf7 = new BufferedWriter(esc7);

			File file8 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/STD LW.txt");
			FileWriter esc8 = new FileWriter(file8);
			BufferedWriter bf8 = new BufferedWriter(esc8);
			
			File file9 = new File ("C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+"/Diagonal/TSVunit.txt");
			FileWriter esc9 = new FileWriter(file9);
			BufferedWriter bf9 = new BufferedWriter(esc9);

	*/
   /*
    * COLOCANDO AS FALHAS DA DIMENSÃO Z NA DIAGONAL NÃO FORMAM-SE SEGMENTOS UNITARIOS.(2 Camadas)
	* NAS DE 3 CAMADAS,SEGUE-SE O PADRAO DA DIAGONAL E NA ULTIMA FILEIRA DA CAMADA SUPERIOR TIRA-SE O 0.(Sobra 1 Segmento unitário)
	* O POSICIONAMENTO DOS LINKS VERTICAIS INFLUENCIAM MUITO NA FORMA DE SEGMENTAÇÃO.
	*/	
		 
		String pasta;
		if(args.length > 0){
			pasta = String.valueOf(args[0]); //dimensao
			pasta = pasta + "_" + String.valueOf(args[1]); //percentual de falha
			System.out.println("Parametro lido: " + pasta);
		}else{
			pasta = "00";
		}
		
	for ( int a = 2; a < 3 ; a++){
	    String topologyFile = "C:\\Users\\Leo\\Desktop\\Topologias\\2X3X3\\25.0\\"+a+".txt", volumePath = "null";
//    	String topologyFile = "C:\\Users\\Rafael\\Documents\\RSBR 3D topologies\\3x3x3.txt", volumePath = "null";
		String merge = "merge";
		double reachability = 1.0;
		String tableFile = "Table_package_"+top1+"_"+"4o_"+a+".vhd";
		int dim = 2;
		
		
		System.out.println("Generating graph");
		
		Graph graph = (topologyFile != null) ? new Graph(new File(topologyFile)) :  new Graph(dim);
		System.out.println("Isolado? :"+graph.haveIsolatedCores());
		System.out.println(graph);
		System.out.println(" - SR Section");
		SR sbr = new SR(graph);

		System.out.println("Compute the segments");
		sbr.encontrar();
		sbr.OrganizaSegm();
		sbr.listSegments();

		System.out.println("Set the restrictions");
	    sbr.setrestrictions();
		sbr.printRestrictions();
		Count  += sbr.UnitSeg;
		count2 += sbr.getNumSegments();
		if(sbr.UnitSeg < min)
		{
			min = sbr.UnitSeg;
		}
		
		if(sbr.UnitSeg > max)
		{
			max = sbr.UnitSeg;
		}
		ValoresUnitarios.add(sbr.UnitSeg);
	/*
		bf.write(String.valueOf(sbr.UnitSeg));
		bf.newLine();
		
		bf2.write(String.valueOf(sbr.getNumSegments()));
		bf2.newLine();
	*/
		
		System.out.println(Count);
		System.out.println(count2);
		
		System.out.println("\n \nMEDIA DE SEGMENTOS UNITARIOS PARA ESTA TOPOLOGIA:" + Count/400);
		System.out.println("\n \nMEDIA DE SEGMENTOS PARA ESTA TOPOLOGIA:" + count2/400);
		System.out.println("\n \nMAXIMO DE SEGMENTOS UNITARIOS PARA ESTA TOPOLOGIA:" + max);
		System.out.println("\n \nMINIMO DE SEGMENTOS UNITARIOS PARA ESTA TOPOLOGIA:" + min);
//    	}
    	
    	for(Integer SegUni : ValoresUnitarios)
    	{
    		auxDesvio += Math.pow((SegUni - (Count/400)), 2);
    	}
    	System.out.println("\n \nDESVIO PADRAO PARA ESTA TOPOLOGIA:" + Math.sqrt(auxDesvio/399));
    	
//    	bf.close();	
//		esc.close();
//		bf2.close();	
//		esc2.close();
//	}
		
		System.out.println(" - RBR Section");
		RBR rbr = new RBR(graph);
		
		System.out.println("Paths Computation");
		
		ArrayList<ArrayList<Path>> paths = rbr.pathsComputation();
	
		
	if(volumePath != null) {
			File commvol = new File(volumePath);
			if(commvol.exists()) {
				System.out.println("Getting volumes from "+volumePath);
				rbr.setVolume(paths, commvol);
			}
		}
		System.out.println("Paths Selection");
		
		ArrayList<ArrayList<Path>> simplePaths = new ArrayList<ArrayList<Path>>();
			

	
	
		
			simplePaths = paths;

	
		rbr.printLengthofPaths(simplePaths);
		
		if(tableFile != null) {
			
		System.out.println("\nRegions Computation\n");
			rbr.addRoutingOptions(simplePaths);
			rbr.regionsComputationFor3D();
			
			if (merge.equals("merge")) {
			System.out.println("\nDoing Merge\n");
				//rbr.merge(reachability);
			}

			System.out.println("Making Tables");
			rbr.doRoutingTable(tableFile);
		}
		
		System.out.println("Doing Average Routing Distance and Link Weight\n");
		//rbr.makeStats(simplePaths);
/*	
		bf3.write(String.valueOf(rbr.getRegionsStats()[0])); //0-max
		bf3.newLine();
		bf4.write(String.valueOf(rbr.getRegionsStats()[1])); //1-med
		bf4.newLine();
		bf5.write(String.valueOf(rbr.getRegionsStats()[2])); //2-min
		bf5.newLine();
		//bf6.write(String.valueOf(writeMetrics(paths, graph)));
	//	bf6.newLine();
		bf7.write(String.valueOf(rbr.linkWeightStats()[0]));
		bf7.newLine();
		bf8.write(String.valueOf(rbr.linkWeightStats()[1]));
		bf8.newLine();
*/				
		System.out.println("Table_package_" + 01 + "_" + a + ".vhd done.");
//		System.out.println("\nAll done!");
		String segmentosunitarios = "C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+
				"\\" + top + "_" + "_" + a + ".txt";
		String unitSegTSV = "C:/Users/Lesc/Desktop/TopologiasDiagonal/"+top+
				"\\TSV " + top + "_" + "_" + a + ".txt";
		
	}
/*	
	bf.close();	
	esc.close();
	bf2.close();	
	esc2.close();
	bf3.close();	
	esc3.close();
	bf4.close();	
	esc4.close();
	bf5.close();	
	esc5.close();
	bf6.close();
	esc6.close();
	bf7.close();
	esc7.close();
	bf8.close();
	esc8.close();
	bf9.close();
	esc9.close();
*/	
	System.out.println("\nAll done!");
}	

	
	private static double writeMetrics(ArrayList<ArrayList<Path>> paths, Graph graph){
		
		double rd = 0.0;
		
		for(ArrayList<Path> path : paths){
			rd += path.size();
		}
		
		rd += graph.getVertices().size();
		
		return (rd / (paths.size() + graph.getVertices().size()));
		
	}

}
