package util;

import rbr.Region;
import sbr.Segment;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Vertice implements Comparable<Vertice> {
	private String[] restrictions = { "", "", "", "", "", "", ""}; // [0]-I [1]-N [2]-S
															// [3]-E [4]-W [5]-UP [6]-DOWN
	private Segment seg;
	private int snet;

	private int distancia;

	private ArrayList<rbr.RoutingPath> routingPaths = new ArrayList<>();
	private String nome;
	private ArrayList<Aresta> adj;
	private ArrayList<rbr.Region> Regions = new ArrayList<>();
	private int connect;
	boolean top , down;


	public Vertice(String name) {
		seg = null;
		snet = -1;
		nome = name;
		adj = new ArrayList<Aresta>();
		connect = 0;
		top = false;
		down = false;
		// restrictions = nome + ": I{} N{} S{} E{} W{}";
	}

	
	public Vertice() {
		// TODO Auto-generated constructor stub
		nome = null;
		seg = null;
		snet = -1;
		adj = new ArrayList<Aresta>();
		connect = 0;
		top = false;
		down = false;
			}


	public boolean getTop() {
		return top;
	}


	public boolean getDown() {
		return down;
	}


	public void setTop(boolean top) {
		this.top = top;
	}


	public void setDown(boolean down) {
		this.down = down;
	}


	public int getConnect() {
		return connect;
	}


	public void setaddConnect() {
		this.connect += 1;
	}


	public void setConnect(int connect) {
		this.connect = connect;
	}


	public void initRoutingOptions() {
		this.routingPaths = new ArrayList<>();
	}

	public void initRegions() {
		this.Regions = new ArrayList<>();
	}

	public void addRestriction(String op, String rest) {
		/*
		 * String op1 = op+"{"; this.restrictions =
		 * restrictions.substring(0,restrictions
		 * .indexOf(op1)+2)+rest+restrictions
		 * .substring(restrictions.indexOf(op1)+2);
		 */
		switch (op) {
		case "I":
			restrictions[0] = restrictions[0] + "" + rest;
			break;
		case "N":
			restrictions[1] = restrictions[1] + "" + rest;
			break;
		case "S":
			restrictions[2] = restrictions[2] + "" + rest;
			break;
		case "E":
			restrictions[3] = restrictions[3] + "" + rest;
			break;
		case "W":
			restrictions[4] = restrictions[4] + "" + rest;
			break;
//		case "UP":
		case "U":
			restrictions[5] = restrictions[5] + "" + rest;
			break;
//		case "DOWN":
		case "D":
			restrictions[6] = restrictions[6] + "" + rest;
			break;
		}
	}

	public String getRestriction(String op) {
		switch (op) {
		case "I":
			return restrictions[0];
		case "N":
			return restrictions[1];
		case "S":
			return restrictions[2];
		case "E":
			return restrictions[3];
		case "W":
			return restrictions[4];
//		case "UP":
		case "U":
			return restrictions[5];
//		case "DOWN":
		case "D":
			return restrictions[6];
		default:
			return null;
		}
	}

	public String[] getRestrictions() {
		return this.restrictions;
	}

	private String IntToBitsString(int a, int size) {
		String out = Integer.toBinaryString(a);
		while (out.length() < size)
			out = "0" + out;

		return out;
	}

	private String opToBinary(String ports) {
		char[] outOp = { '0', '0', '0', '0', '0', '0', '0' };
		char[] port = ports.toCharArray();

		for (char pt : port) {
			switch (pt) {
//			case 'D':
//				outOp[6] = '1';
//				break;
//			case 'U':
//				outOp[5] = '1';
//				break;
//			case 'E':
//				outOp[4] = '1';
//				break;
//			case 'W':
//				outOp[3] = '1';
//				break;
//			case 'N':
//				outOp[2] = '1';
//				break;
//			case 'S':
//				outOp[1] = '1';
//				break;
//			case 'I':
//				outOp[0] = '1';
//				break;
			case 'I':
				outOp[6] = '1';
				break;
			case 'D':
				outOp[5] = '1';
				break;
			case 'U':
				outOp[4] = '1';
				break;
			case 'S':
				outOp[3] = '1';
				break;
			case 'N':
				outOp[2] = '1';
				break;
			case 'W':
				outOp[1] = '1';
				break;
			case 'E':
				outOp[0] = '1';
				break;
			}
		}

		return String.valueOf(outOp);
	}

	public Aresta getAresta(Vertice destino) {
		for (Aresta v : adj)
			if (v.getDestino().getNome().equals(destino.getNome()))
				return v;

		return null;

	}

	public ArrayList<Aresta> getArestas() {
		return adj;
	}

	public void addAdj(Aresta e) {

		adj.add(e);
	}

	public ArrayList<Aresta> getAdj() {

		return this.adj;

	}

	public Aresta getAdj(String color) {
		for (Aresta a : this.adj)
			if (a.getCor().equals(color))
				return a;

		System.out.println("ERROR : There isn't a Op " + color + "?");
		return null;
	}

	public String getNome() {

		return this.nome;

	}

	public Segment getSegment() {
		return this.seg;
	}

	public void setSegment(Segment sg) {
		seg = sg;
	}

	public int getSubNet() {
		return this.snet;
	}

	public void setSubNet(int sn) {
		snet = sn;

	}

	public boolean belongsTo(int sn) {
		return (sn == snet);
	}

	public boolean isIn(String min, String max) {
		int zMin = Integer.valueOf(min.split("\\.")[0]);
		int xMin = Integer.valueOf(min.split("\\.")[1]);
		int yMin = Integer.valueOf(min.split("\\.")[2]);
		int zMax = Integer.valueOf(max.split("\\.")[0]);
		int xMax = Integer.valueOf(max.split("\\.")[1]);
		int yMax = Integer.valueOf(max.split("\\.")[2]);

		int z = Integer.valueOf(nome.split("\\.")[0]);
		int x = Integer.valueOf(nome.split("\\.")[1]);
		int y = Integer.valueOf(nome.split("\\.")[2]);

		return (x <= xMax && x >= xMin && y <= yMax && y >= yMin && z<=zMax && z>=zMin);
	}

	public ArrayList<Region> getRegions() {
		return Regions;
	}

	public void setRegions(ArrayList<Region> Regions) {
		this.Regions = Regions;
	}

	public void addRegion(String ip, ArrayList<String> dsts, String op) {
		rbr.Region region = new rbr.Region(ip, dsts, op);
		this.Regions.add(region);
	}

	public void addRP(String ip, String dst, String op) {
		ip = sortStrAlf(ip);
		op = sortStrAlf(op);
		if (!this.AlreadyExists(ip, dst, op)) {
			rbr.RoutingPath RP = new rbr.RoutingPath(ip, dst, op);
			this.routingPaths.add(RP);
//			System.out.println("(" + ip + "," + dst + "," + op + ")");
		}
	}

	private boolean AlreadyExists(String ip, String dst, String op) {
		for (int a = 0; a < this.routingPaths.size(); a++)
			if (this.routingPaths.get(a).getIp().equals(ip)
					&& this.routingPaths.get(a).getDst().equals(dst)
					&& this.routingPaths.get(a).getOp().equals(op))
				return true;

		return false;
	}

	public static String sortStrAlf(String input) {
		char[] ip1 = input.toCharArray();
		Arrays.sort(ip1);

		return String.valueOf(ip1);
	}

	public boolean reaches(Vertice dest) {
		return this.reaches(dest, "I");
	}

	private boolean reaches(Vertice dest, String ipColor) {
		if (dest == this)
			return true;
		String opColor = this.getOpColor(dest, ipColor);
		if (opColor == null)
			return false;
		return this.getAdj(opColor).getDestino()
				.reaches(dest, getAdj(opColor).getInvColor());
	}

	public void checkIsolation(ArrayList<Vertice> alc) {
		if (!alc.contains(this))
			alc.add(this); // Adiciona primeiro core analisado aos alcancaveis
		for (Aresta adj : adj) {
			// So adiciona aos alcancaveis cores que ainda nao foram adicionados
			if (alc.contains(adj.getDestino()))
				continue;
			Vertice neigh = adj.getDestino();
			alc.add(neigh);
			// checa para vizinhos
			neigh.checkIsolation(alc);
		}
	}

	private String getOpColor(Vertice dest, String ipColor) {
		String router = dest.getNome();
		for (rbr.Region reg : this.Regions)
			if (reg.contains(router) && reg.getIp().contains(ipColor))
				return (reg.getOp().substring(0, 1));

		System.err.println("ERROR : There isn't Op on " + this.getNome()
				+ " for " + dest.getNome() + " " + ipColor);
		return null;
	}

	public ArrayList<rbr.RoutingPath> getRoutingPaths() {
		return routingPaths;
	}

	public void setRoutingPaths(ArrayList<rbr.RoutingPath> routingPaths) {
		this.routingPaths = routingPaths;
	}

	public void PrintRegions(double[] stats, BufferedWriter bw, int nBits) {
		int maxRegion = (int) stats[0];
		try {
			bw.append("\n -- Router " + this.getNome() + "\n");
			bw.append("(");

			for (int a = 0; a < this.Regions.size(); a++) {
				int Xmin = Integer.parseInt(this.Regions.get(a).getDownLeft()
						.split("\\.")[1]);
				int Ymin = Integer.parseInt(this.Regions.get(a).getDownLeft()
						.split("\\.")[2]);
				int Zmin = Integer.parseInt(this.Regions.get(a).getDownLeft()
						.split("\\.")[0]);//09/11/2015
				int Xmax = Integer.parseInt(this.Regions.get(a).getUpRight()
						.split("\\.")[1]);
				int Ymax = Integer.parseInt(this.Regions.get(a).getUpRight()
						.split("\\.")[2]);
				int Zmax = Integer.parseInt(this.Regions.get(a).getUpRight()
						.split("\\.")[0]);//09/11/2015

				// Write on file
				String outLine = "(\""
						+ opToBinary(this.Regions.get(a).getIp())
						+ IntToBitsString(Xmin, nBits)
						+ IntToBitsString(Ymin, nBits)
						+ IntToBitsString(Zmin, nBits)//09/11/2015
						+ IntToBitsString(Xmax, nBits)
						+ IntToBitsString(Ymax, nBits)
						+ IntToBitsString(Zmax, nBits)//09/11/2015
						+ opToBinary(this.Regions.get(a).getOp()) + "\")";

				bw.append(outLine);
				System.out.println("(\""
						+ opToBinary(this.Regions.get(a).getIp()) + " "
						+ IntToBitsString(Xmin, nBits)
						+ IntToBitsString(Ymin, nBits)
						+ IntToBitsString(Zmin, nBits) + " "//09/11/2015
						+ IntToBitsString(Xmax, nBits)
						+ IntToBitsString(Ymax, nBits)
						+ IntToBitsString(Zmax, nBits) + " "//09/11/2015
						+ opToBinary(this.Regions.get(a).getOp()) + "\")" + sortStrAlf(this.Regions.get(a).getIp()) 
						+ " " + Xmin + Ymin + Zmin + " " + Xmax + Ymax + Zmax + " " + sortStrAlf(this.Regions.get(a).getOp()));

				if (a != this.Regions.size() - 1
						|| (a == this.Regions.size() - 1)
						&& (this.Regions.size() < maxRegion)) {
					bw.append(",");
				}
				bw.newLine();
			}

			// If less then Max Region
			if (this.Regions.size() < maxRegion) {
				int a = this.Regions.size();
				while (a < maxRegion) {
					a++;
//					String outLine = "(\"" + IntToBitsString(0, 4 * nBits + 10) + "\")";
					String outLine = "(\"" + IntToBitsString(0, 6 * nBits + 14) + "\")";//09/11/2015
					bw.append(outLine);

					if (a < maxRegion)
						bw.append(",");

					bw.newLine();
				}
			}

			bw.append(")");
			// bw.flush();
			// bw.close();
		} catch (IOException ex) {
			Logger.getLogger(Vertice.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public int compareTo(Vertice outroVertice) {
		if (this.distancia < outroVertice.distancia) {
			return -1;
		}
		if (this.distancia > outroVertice.distancia) {
			return 1;
		}
		return 0;
	}

}
