package sbr;

import util.*;
import java.util.ArrayList;

public class Segment {
	private ArrayList<Aresta> links;
	private ArrayList<Vertice> switches;
	int cam;//Identifica a camada
	boolean test;

	public Segment() {
		links = new ArrayList<>();
		switches = new ArrayList<>();
		cam = 0;
		test = false;
	}
	

	public int getCam() {
		return cam;
	}
	public boolean getTest() {
		return test;
	}
	public void setTest(boolean test) {
	 this.test = test;
	}
	public void setCam(int cam) {
		this.cam = cam;
	}


	public boolean isStarting() {
		// Checa se o destino do ultimo link eh o primeiro switch

		// TESTE PROVISORIO, SERA APAGADO
		if (links.size() == 1)
			return false;

		if (links.get(links.size() - 1).getDestino().getNome()
				.equals(switches.get(0).getNome()))
			return true;

		return false;
	}

	public boolean isUnitary() {

		return (links.size() == 1 && switches.size() == 0);
	}

	public boolean isRegular() {

		return (!this.isUnitary() && !this.isStarting());
	}

	public void add(Aresta ln) {
		links.add(ln);
		// ln.setSegment(this);
	}

	public void add(Vertice sw) {
		switches.add(sw);
		sw.setSegment(this);
	}

	public void remove(Aresta ln) {
		// ln.setSegment(null);
		links.remove(ln);
	}

	public void remove(Vertice sw) {
		sw.setSegment(null);
		/* @RM */
		// Remove the last occurrence instead of the first
		if (switches.lastIndexOf(sw) != -1)
			switches.remove(switches.lastIndexOf(sw));
		/* @RM */
		// switches.remove(sw);

	}

	public String toString() {
		String r = "";
		int sw = 0, ln = 0;
		while (sw < switches.size()) {
			r += (switches.get(sw++).getNome() + " ");
		}
		r += '\n';
		while (ln < links.size()) {
			r += (links.get(ln).getOrigem().getNome() + " <=> "
					+ links.get(ln).getDestino().getNome() + " ");
			ln++;
		}
		return r;
	}

	public ArrayList<Vertice> getSwitchs() {
		return this.switches;
	}

	public ArrayList<Aresta> getLinks() {
		return this.links;
	}

}
