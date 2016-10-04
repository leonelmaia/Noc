package rbr;

import java.util.ArrayList;

public class Region implements Comparable<Region> {
	private String ip;
	private String op;
	private String upRight;
	private String downLeft;
	private float size;
	private ArrayList<String> dst = new ArrayList<>();

	public Region(String ip, ArrayList<String> dsts, String op) {
		this.dst = dsts;
		this.ip = ip;
		this.op = op;
		this.setextrems();
		this.setSize();
	}

	public void setextrems() {
		int zMin = Integer.MAX_VALUE, xMin = Integer.MAX_VALUE, yMin = Integer.MAX_VALUE; 
		int zMax = 0,xMax = 0, yMax = 0;

		for (String s : this.dst) {
//			System.out.println(dst);
			String[] xy = s.split("\\.");
			int z = Integer.valueOf(xy[0]);
			int x = Integer.valueOf(xy[1]);
			int y = Integer.valueOf(xy[2]);
			
			zMin = (zMin < z) ? zMin : z;
			xMin = (xMin < x) ? xMin : x;
			yMin = (yMin < y) ? yMin : y;
			zMax = (zMax > z) ? zMax : z;
			xMax = (xMax > x) ? xMax : x;
			yMax = (yMax > y) ? yMax : y;
		}

		this.upRight =zMax + "." + xMax + "." + yMax;
		this.downLeft = zMin + "." + xMin + "." + yMin;

	}

	public void setSize() {
		String[] Min = this.downLeft.split("\\.");
		int Zmin = Integer.valueOf(Min[0]);
		int Xmin = Integer.valueOf(Min[1]);
		int Ymin = Integer.valueOf(Min[2]);

		String[] Max = this.upRight.split("\\.");
		int Zmax = Integer.valueOf(Max[0]);
		int Xmax = Integer.valueOf(Max[1]);
		int Ymax = Integer.valueOf(Max[2]);

		this.size = (((Zmax - Zmin) + 1) * (Xmax - Xmin) + 1) * ((Ymax - Ymin) + 1);
	}

	public String getIp() {
		return ip;
	}

	public String getOp() {
		return op;
	}

	public String getDownLeft() {
		return downLeft;
	}

	public void setDownLeft(String downLeft) {
		this.downLeft = downLeft;
	}

	public String getUpRight() {
		return upRight;
	}

	public void setUpRight(String upRight) {
		this.upRight = upRight;
	}

	public ArrayList<String> getDst() {
		return dst;
	}

	public ArrayList<String> getDst(int zmin, int xmin, int ymin, int zmax, int xmax, int ymax) {
		ArrayList<String> result = new ArrayList<String>();
		
		for(int z = zmin; z <= zmax; z++)
			for (int x = xmin; x <= xmax; x++)
				for (int y = ymin; y <= ymax; y++)
					if (this.dst.contains(z + "." + x + "." + y))
						result.add(z + "." + x + "." + y);
		if (result.size() == 0)
			result = null;
		return result;
	}

	@Override
	public int compareTo(Region otherRegion) {
		if (this.size < otherRegion.size) {
			return -1;
		}
		if (this.size > otherRegion.size) {
			return 1;
		}
		return 0;
	}

	public int getZmax() {
		return Integer.parseInt(this.getUpRight().split("\\.")[0]);
	}
	
	public int getXmax() {
		return Integer.parseInt(this.getUpRight().split("\\.")[1]);
	}

	public int getYmax() {
		return Integer.parseInt(this.getUpRight().split("\\.")[2]);
	}

	public int getZmin() {
		return Integer.parseInt(this.getDownLeft().split("\\.")[0]);
	}
	
	public int getXmin() {
		return Integer.parseInt(this.getDownLeft().split("\\.")[1]);
	}

	public int getYmin() {
		return Integer.parseInt(this.getDownLeft().split("\\.")[2]);
	}

	public String toString() {
		String out = this.downLeft + " " + this.upRight + " " + this.ip + " "
				+ this.op;
		return out;
	}

	public boolean contains(String router) {
		String[] xy = router.split("\\.");
		int z = Integer.parseInt(xy[0]);
		int x = Integer.parseInt(xy[1]);
		int y = Integer.parseInt(xy[2]);

		String[] Min = this.getDownLeft().split("\\.");
		int minZ = Integer.parseInt(Min[0]);
		int minX = Integer.valueOf(Min[1]);
		int minY = Integer.valueOf(Min[2]);
		String[] Max = this.getUpRight().split("\\.");
		int maxZ = Integer.parseInt(Max[0]);
		int maxX = Integer.valueOf(Max[1]);
		int maxY = Integer.valueOf(Max[2]);
		
		if (minZ <= z && z <= maxZ && minX <= x && x <= maxX && minY <= y && y <= maxY)
			return true;
		return false;
	}

}
