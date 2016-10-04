package util;

import java.util.ArrayList;

public class Cubo {
	private ArrayList<Vertice> vertices;
	private ArrayList<Aresta> arestas;
	private int somaVertice,maior;
	private Vertice max,min,melhor;
	private String nome;


	public Cubo() {
		vertices = new ArrayList<>();
		arestas = new ArrayList<>();
		somaVertice = 0;
	}
	public Cubo(Vertice max , Vertice min, Graph topologia) {
		somaVertice = 0;
		maior=0;
		vertices = new ArrayList<>();
		
		String[] minParts = min.getNome().split("\\.");
		
		//System.out.println("teste");
		int zMin = Integer.parseInt(minParts[0]);
		int xMin = Integer.parseInt(minParts[1]);
		int yMin = Integer.parseInt(minParts[2]);
		
		String[] maxParts = max.getNome().split("\\.");
		
		int zMax = Integer.parseInt(maxParts[0]);
		int xMax = Integer.parseInt(maxParts[1]);
		int yMax = Integer.parseInt(maxParts[2]);
		nome = min.getNome()+"<-->"+max.getNome();
		for(int auxZ = zMin; auxZ <= zMax; auxZ++)
		{
			for(int auxX = xMin; auxX <= xMax; auxX++)
			{
				for(int auxY = yMin; auxY <= yMax; auxY++)
				{
					vertices.add(topologia.getVertice(Integer.toString(auxZ)+"."+Integer.toString(auxX)+"."+Integer.toString(auxY)));
				}
			}
		}
		for (Vertice u : vertices){
			for (Vertice v : vertices)
			{
				if(topologia.hasAresta(u, v)){
					u.setaddConnect();
					v.setConnect(0);

				}


			}
			somaVertice += u.getConnect();
			if(u.getConnect()>maior) {
				maior = u.getConnect();
				melhor = u;
			}

			//System.out.println(nome +"------>"+u.getNome()+"----->"+u.getConnect());
		}

	
		this.max = max;
		this.min = min;
		
	}

	public int getMaior() {
		return maior;
	}
	public void setMaior(int maior) {
		this.maior = maior;
	}
	public Vertice getMelhor() {
		return melhor;
	}
	public void setMelhor(Vertice melhor) {
		this.melhor = melhor;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public ArrayList<Vertice> getVertices() 
	{
		return vertices;
	}
	public void setVertices(ArrayList<Vertice> vertices) 
	{
		this.vertices = vertices;
	}
	public ArrayList<Aresta> getArestas() {
		return arestas;
	}
	public void setArestas(ArrayList<Aresta> arestas) 
	{
		this.arestas = arestas;
	}
	public int getSomaVertice() 
	{
		return somaVertice;
	}
	public void setSomaVertice(int somaVertice) 
	{
		this.somaVertice = somaVertice;
	}
	public Vertice getMax() 
	{
		return max;
	}
	public void setMax(Vertice max) 
	{
		this.max = max;
	}
	public Vertice getMin() 
	{
		return min;
	}
	public void setMin(Vertice min) 
	{
		this.min = min;
	}



}