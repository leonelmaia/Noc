package util;

import java.util.ArrayList;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph {
	boolean debug = true;
//	static private String[] ports = { "N", "S", "E", "W", "UP", "DOWN" };
	static private String[] ports = { "N", "S", "E", "W", "U", "D" };
	public ArrayList<Vertice> vertices;
	ArrayList<Aresta> arestas;
	int dimX;
	int dimY;
	int dimZ;

	public Graph() {
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();
	}

	public Graph(File topology) {
		System.out.println("from file: "+topology.getName());
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();
		int count = 0;

		try {
			
			Scanner sc = new Scanner(new FileReader(topology));
			Scanner sd = new Scanner(new FileReader(topology));
			
			String[] lines = null, columns = null,alturas = null;
			do{
				if (sc.hasNextLine())
					lines = sc.nextLine().split("; ");
				if (sc.hasNextLine())
					columns = sc.nextLine().split("; ");
				if (sc.hasNextLine()){
					alturas = sc.nextLine().split("; ");
				    count++;
				}
			}while(sc.hasNextLine());
				
				dimZ = count + 1;
				dimX = lines[0].split(" ").length + 1;
				dimY = lines.length;
				lines=null; columns=null; alturas=null;

			for (int k = 0; k < dimZ; k++) {	
				for (int i = 0; i < dimY; i++) {
					for (int j = 0; j < dimX; j++) {
						String vertice = k+ "."+ i + "." + j;
				
						this.addVertice(vertice);

					}	}
				}
			
			
			for (int k = 0 ; k <= dimZ - 1   ; k++){
				
				if (sd.hasNextLine()){
					lines = sd.nextLine().split("; ");
				
				for (int i = 0; i < lines.length; i++) {
				
					String[] line = lines[i].split(" ");
					for (int j = 0; j < line.length; j++) {
						if (line[j].charAt(0) == '0') // there is a link
						{
							Vertice starting = this.getVertice(k + "." + j + "."
									+ (i));
							Vertice ending = this.getVertice(k + "." + (j + 1) + "."
									+ ( i));
							this.addAresta(starting, ending, ports[2]);
							this.addAresta(ending, starting, ports[3]);
							
						}
					}}}
				if (sd.hasNextLine()){
					columns = sd.nextLine().split("; ");
				for (int i = 0; i < columns.length; i++) {
					
					String[] column = columns[i].split(" ");
					for (int j = 0; j < column.length; j++) {
						if (column[j].charAt(0) == '0') // there is a link
						{
							Vertice starting = this.getVertice(k+"."+j + "."
									+ ( i));
							Vertice ending = this.getVertice(k+"."+j + "."
									+ ( i +1 ));
							this.addAresta(starting, ending, ports[0]);
							this.addAresta(ending, starting, ports[1]);
							
						}
					}}	}
				
				if (sd.hasNextLine()){
					alturas = sd.nextLine().split("; ");
							for (int i = 0; i < alturas.length; i++) {
							String[] altura = alturas[i].split(" ");
							System.out.println(alturas[i]);
							for (int j = 0; j < alturas.length; j++) {
								if (altura[j].charAt(0) == '0') // there is a link
								{
									Vertice starting = this.getVertice((k+1)+"."+j + "."
											+ (i) );
									Vertice ending = this.getVertice((k)+"."+j+ "."
											+  (i));
									this.addAresta(starting, ending, ports[5]);
									this.addAresta(ending, starting, ports[4]);
									starting.down = true;
									ending.top = true;
									
								}}
							}}
					}

				
				

						
			sd.close();
			sc.close();
		}
		catch (Exception ex) {
			Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
		}
		
	}

	
	public Graph(int dim, double perc) {
		this(dim, dim, dim, perc);
	}
	
	public Graph(int dim) {
		this(dim, dim, dim, 0);
	}
	
	public Graph(int dX,int dY,int Ncamadas, double perc)
	{
		if(Ncamadas <= 0){
			System.out.println("ERROR - NUMBER OF LAYERS ZERO OR NEGATIVE");
			System.exit(1);
		}

		vertices = new ArrayList<>();
		arestas = new ArrayList<>();

		dimX=dX;
		dimY=dY;
		dimZ = Ncamadas;

		int nArests = (dimZ*((dimX-1)*dimY + dimX*(dimY-1)) + (Ncamadas-1)*(dimX*dimY));
		int nFalts = (int)Math.ceil((double)nArests*perc);
		System.out.println(dimX + "--------"+dimY+"--------"+(dimZ));
		System.out.println("#Arestas: "+nArests);
		System.out.println("#Faults: "+nFalts);

		//Adiciona Vertices
		for(int k=0; k<dimZ; k++)
			for(int x=0; x<dimX; x++)
				for(int y=0; y<dimY; y++)
					addVertice(k+"."+x+"."+y);

		//Adiciona Arestas
		for(int k=0; k<dimZ; k++)
			for(int y=0; y<dimY; y++)
				for(int x=0; x<dimX; x++)
				{
					if(contem(k+"."+x+"."+(y+1)))
						addAresta(getVertice(k+"."+x+"."+y), getVertice(k+"."+x+"."+(y+1)), ports[0]);
					if(contem(k+"."+x+"."+(y-1)))
						addAresta(getVertice(k+"."+x+"."+y), getVertice(k+"."+x+"."+(y-1)), ports[1]);
					if(contem(k+"."+(x+1)+"."+y)) 
						addAresta(getVertice(k+"."+x+"."+y), getVertice(k+"."+(x+1)+"."+y), ports[2]);	
					if(contem(k+"."+(x-1)+"."+y)) 
						addAresta(getVertice(k+"."+x+"."+y), getVertice(k+"."+(x-1)+"."+y), ports[3]);	
					if(contem((k+1)+"."+x+"."+y)){
						
						addAresta(getVertice(k+"."+x+"."+y), getVertice((k+1)+"."+x+"."+y),ports[4]);
						getVertice(k+"."+x+"."+y).top = true;
						getVertice((k+1)+"."+x+"."+y).down = true;
					}
					if(contem((k-1)+"."+x+"."+y)){
						addAresta(getVertice(k+"."+x+"."+y), getVertice((k-1)+"."+x+"."+y),ports[5]);
						getVertice(k+"."+x+"."+y).down = true;
						getVertice((k-1)+"."+x+"."+y).top = true;
					}
				



				}				

		//Adiciona Falhas e checa isolamento
		for(int i=0;i<nFalts;i++)
		{
			while(true)
			{
				int idx = (int)(Math.random()*((double)arestas.size()));
				Aresta toRemoveIndo = arestas.get(idx);
				Aresta toRemoveVindo = toRemoveIndo.getDestino().getAresta(toRemoveIndo.getOrigem());

				if (debug) System.out.println("Removing: "+toRemoveIndo.getOrigem().getNome()
						+"->"+toRemoveIndo.getDestino().getNome());

				removeAresta(toRemoveIndo);
				removeAresta(toRemoveVindo);

				if(haveIsolatedCores())
				{
					AddAresta(toRemoveIndo);
					AddAresta(toRemoveVindo);
				}
				else break;
			}
		}
	}

	
	//Checa se existe cores isolados
	public boolean haveIsolatedCores() {
		ArrayList<Vertice> alc = new ArrayList<Vertice>();
		//Escolha do 0.0 para ser o core inicial. Garantido a existencia em todas as topologias
		getVertice("0.0.0").checkIsolation(alc);
		
		//Se lista de alcancaveis for igual ao total de cores nao existe isolamento
		if(!(alc.size()==vertices.size())) return true;
		
    	return false;
	}
	
	

	private boolean contem(String vertice) {

		for (int i = 0; i < vertices.size(); i++) {

			if (vertice.equals(vertices.get(i).getNome())) {
				return true;
			}
		}
		return false;
	}

	public  ArrayList<Vertice> getVertices() {

		return this.vertices;

	}

	public ArrayList<Aresta> getArestas() {
		return this.arestas;
	}

	public Vertice getVertice(String nomeVertice) {
		Vertice vertice = null;

		for (Vertice v : this.vertices) {
			if (v.getNome().equals(nomeVertice))
				vertice = v;
		}

		if (vertice == null) {
			System.out.println("Vertice: " + nomeVertice + " nao encontrado");
			return null;
		}

		return vertice;
	}
	
	public boolean hasAresta(Vertice origem , Vertice destino) {
		Aresta aresta = null;
		boolean b = false;
		for (Aresta v : this.arestas) {
			if (v.getDestino().getNome().equalsIgnoreCase(destino.getNome()) && v.getOrigem().getNome().equalsIgnoreCase(origem.getNome()) )
				b = true;
		}

		return b;
	}


	private void addVertice(String nome) {
		Vertice v = new Vertice(nome);
		vertices.add(v);
	}
	
	private void addAresta(Vertice origem, Vertice destino, String cor) 
	{
		Aresta e = new Aresta(origem, destino, cor);
		origem.addAdj(e);
		arestas.add(e);
	}
	
	private void AddAresta(Aresta toAdd)
	{
		toAdd.getOrigem().getAdj().add(toAdd);
		arestas.add(toAdd);
	}
	
	private void removeAresta(Aresta toRemove)
	{
		toRemove.getOrigem().getAdj().remove(toRemove);
		arestas.remove(toRemove);		
	}

	public String toString() {
		String r = "";
		System.out.println("Graph:");
		for (Vertice u : vertices) {
			r += u.getNome() + " -> ";
			for (Aresta e : u.getAdj()) {
				Vertice v = e.getDestino();
				r += v.getNome() + e.getCor() + ", ";
			}
			r += "\n";
		}
		return r;
	}

	public int dimX() {
		return dimX;
	}

	public int dimY() {
		return dimY;
	}
	
	public int dimZ() {
		return dimZ;
	}

	public int indexOf(Vertice v) {
		return indexOf(v.getNome());
	}
	
	public Vertice EscolherInicio() {
		return null;
		
	}
	
	
	
	private int indexOf(String xy) {
		int z = Integer.parseInt(xy.split("\\.")[0]);
		int x = Integer.parseInt(xy.split("\\.")[1]);
		int y = Integer.parseInt(xy.split("\\.")[2]);
		return z*(this.dimX() + y*this.dimX()) + (x + y*this.dimX());
	}
	
	
	public ArrayList<Vertice> reduçãoCaminho1 (Vertice fim , Vertice Inicio)
	{	int i = 0;
	ArrayList<Vertice>caminho = new ArrayList<Vertice>();
	int z1 = Integer.parseInt(Inicio.getNome().split("\\.")[0]);
	int x1 = Integer.parseInt(Inicio.getNome().split("\\.")[2]);
	int y1 = Integer.parseInt(Inicio.getNome().split("\\.")[2]);
	int z2 = Integer.parseInt(fim.getNome().split("\\.")[0]);
	int x2 = Integer.parseInt(fim.getNome().split("\\.")[1]);
	int y2 = Integer.parseInt(fim.getNome().split("\\.")[2]);

	
	if(z1==z2){
		if(x1 != 0){
		if((x1 == x2)){
			if(getAresta1(z1+"."+x1+"."+y1,z1+"."+(x1-1)+"."+(y1))){
				caminho.add(getVertice(z1+"."+(x1-1)+"."+(y1)));
				x1--;
			}
		}}
		if(x1 == 0){
			if((x1 == x2)){
				if(getAresta1(z1+"."+x1+"."+y1,z1+"."+(x1+1)+"."+(y1))){
					caminho.add(getVertice(z1+"."+(x1+1)+"."+(y1)));
					x1++;
					
				}
			}

		}
	while ( !((x1 == x2) && (y1 == y2))){

		i++;
		System.out.println(i);
		if(i == 5){
			ArrayList<Vertice>caminhoComp = new ArrayList<Vertice>();
			Vertice v = new Vertice(z1+"."+x1+"."+y1);
			System.out.println(z1+"."+x1+"."+y1);
			caminhoComp = reduçãoCaminho(fim,v);
			for(int k = 0 ; k < caminhoComp.size() ; k++)
			{
				caminho.add(caminhoComp.get(k));
			}
			return caminho;
		}

		while(y2 > y1)
		{

			//	 if( AreNeighboursTop(Inicio,fim) == true )
			//{
			//	caminho.add(fim);
			//	}
			if(getAresta1(z1+"."+x1+"."+y1,z1+"."+x1+"."+(y1+1))&& getVertice1((x1)+"."+(y1+1))){
				caminho.add(getVertice(z1+"."+(x1)+"."+(y1+1)));
				y1++;
			}else break;
		}
		while(y1 >  y2)
		{

			/* if( AreNeighboursTop(Inicio,fim) == true )
		{
			caminho.add(fim);
		}
			 */
			if(getAresta1(z1+"."+x1+"."+y1,z1+"."+x1+"."+(y1-1))&&getVertice1(z1+"."+(x1)+"."+(y1-1))){
				caminho.add(getVertice(z1+"."+(x1)+"."+(y1-1)));
				y1--;
			}else break;
		}
		while(x2 > x1)
		{


			if(getAresta1(z1+"."+x1+"."+y1,z1+"."+(x1+1)+"."+y1)&& getVertice1(z1+"."+(x1+1)+"."+(y1))){
				caminho.add(getVertice(z1+"."+(x1+1)+"."+(y1)));
				x1++;
			}else break;

		}
		while(x1 >  x2)
		{

			/* if( AreNeighboursTop(Inicio,fim) == true )
			{
				caminho.add(fim);
			}
			 */
			if(getAresta1(z1+"."+x1+"."+y1,z1+"."+(x1-1)+"."+y1)&&getVertice1(z1+"."+(x1-1)+"."+(y1))){
				caminho.add(getVertice(z1+"."+(x1-1)+"."+(y1)));
				x1--;
			}else break;

		}



	}
	return caminho;
	}
	return null;}
	
	public ArrayList<Vertice> reduçãoCaminho (Vertice fim , Vertice Inicio)
	{	
		int i = 0;
		ArrayList<Vertice>caminho = new ArrayList<Vertice>();
	
		int z1 = Integer.parseInt(Inicio.getNome().split("\\.")[0]);
		int x1 = Integer.parseInt(Inicio.getNome().split("\\.")[1]);
		int y1 = Integer.parseInt(Inicio.getNome().split("\\.")[2]);
		int z2 = Integer.parseInt(fim.getNome().split("\\.")[0]);
		int x2 = Integer.parseInt(fim.getNome().split("\\.")[1]);
		int y2 = Integer.parseInt(fim.getNome().split("\\.")[2]);
		
		
		if(z1 == z2){
		if(y2 != 0){
			if((y1 == y2)){
				if(getAresta1(z1+"."+x1+"."+y1    ,   z1+"."+(x1)+"."+(y1-1))){
					caminho.add(getVertice(z1+"."+x1+"."+(y1-1)));
					y1--;
				}
			}}else{
				if((y1 == y2)){
					if(getAresta1(z1+"."+x1+"."+y1         ,              z1+"."+(x1)+"."+(y1+1))){
						caminho.add(getVertice(z1+"."+x1+"."+(y1+1)));
						y1++;
					}

				}}



		while ( !((x1 == x2) && (y1 == y2))  ){
			i++;
			if(i == 5){
				ArrayList<Vertice>caminhoaux = new ArrayList<Vertice>();
				Vertice v = new Vertice(z1+"."+x1+"."+y1);
				caminhoaux = reduçãoCaminho1(fim,v);
				for(int k = 0 ; k < caminhoaux.size(); k++)
				{
					caminho.add(caminhoaux.get(k));
				}

				return caminho;
			}
			while(x2 > x1)
			{	

				if(getAresta1(z1+"."+x1+"."+y1,z1+"."+(x1+1)+"."+y1) && getVertice1(z1+"."+(x1+1)+"."+(y1))){
					caminho.add(getVertice(z1+"."+(x1+1)+"."+(y1)));
					x1++;
				}else break;

			}
			while(x1 >  x2)
			{

				/* if( AreNeighboursTop(Inicio,fim) == true )
		{
			caminho.add(fim);
		}
				 */
				if(getAresta1(z1+"."+x1+"."+y1,z1+"."+(x1-1)+"."+y1)&& getVertice1(z1+"."+(x1-1)+"."+(y1))){
					caminho.add(getVertice(z1+"."+(x1-1)+"."+(y1)));
					x1--;
				}else break;

			}


			while(y2 > y1)
			{	

				//	 if( AreNeighboursTop(Inicio,fim) == true )
				//{
				//	caminho.add(fim);
				//	}
				if(getAresta1(z1+"."+x1+"."+y1,z1+"."+x1+"."+(y1+1)) && getVertice1(z1+"."+(x1)+"."+(y1+1))){
					caminho.add(getVertice(z1+"."+(x1)+"."+(y1+1)));
					y1++;
				}else break;
			}
			while(y1 >  y2)
			{	

				/* if( AreNeighboursTop(Inicio,fim) == true )
		{
			caminho.add(fim);
		}
				 */
				if(getAresta1(z1+"."+x1+"."+y1,z1+"."+x1+"."+(y1-1))&& getVertice1(z1+"."+(x1)+"."+(y1-1))){
					caminho.add(getVertice(z1+"."+(x1)+"."+(y1-1)));
					y1--;
				}
				else break;

			}
	
		}

		return caminho;
	}
		return null;}
	
	public boolean getAresta1(String nomeVerticeOri , String nomeVerticeDest ) {
		Aresta aresta = null;
		boolean b = false;

		for (Aresta a : this.arestas) {
			if (a.starting.getNome().equals(nomeVerticeOri)&& a.ending.getNome().equals(nomeVerticeDest))
				aresta = a;
			b = true;
		}

		if (aresta == null) {
			//System.out.println("Aresta: " + nomeVerticeOri + "---to---"+ nomeVerticeDest + " nao encontrado");
			b = false;
		}
		return b;
	}
	
	public boolean getVertice1(String nomeVertice) {
		Vertice vertice = null;

		for (Vertice v : this.vertices) {
			if (v.getNome().equals(nomeVertice))
				vertice = v;
		}

		if (vertice == null) {
			System.out.println("Vertice: " + nomeVertice + " nao encontrado" );
			return false;
		}

		return true;
	}


	

}
