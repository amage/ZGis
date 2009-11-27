package model;

public class Rect {
	private double _x;
	private double _y;
	private double _w;
	private double _h;
	
	public Rect(double ax, double ay, double aw, double ah) {
		_x = ax; _y = ay; _w = aw; _h = ah;
	}
	
	public Rect() { }

	public double getX() {return _x;}
	public double getY() {return _y;}
	public void setX(double x) {_x=x;}
	public void setY(double y) {_y=y;}
	public double getWidth() {return _w;}
	public double getHeight() {return _h;}
	public void setWidth(double w) {_w=w;}
	public void setHeight(double h) {_h=h;}
	public void setGeometry(double x, double y, double w, double h) { 
		setX(x); setY(y); setWidth(w); setHeight(h);
	}
	public void setSameGeometry(Rect r) { 
		setX(r.getX());setY(r.getY()); 
		setWidth(r.getWidth()); setHeight(r.getHeight());
	}
	public boolean haveCross(Rect r) {
		double p1x = r.getX();
		double p1y = r.getY();
		double p2x = r.getX()+r.getWidth();
		double p2y = r.getY()+r.getHeight();
		
		if(
			 (p1x>getWidth()+getX()) || (p1y>getHeight()+getY()) ||
			 (p2x<getX()) || (p2y<getY())
			) {
			
			return false;
		}
		else 
			return true;
	}

	/*
	 * same as haveCross but without edges
	 */
	public boolean haveOverlap(Rect r) {
		double p1x = r.getX();
		double p1y = r.getY();
		double p2x = r.getX()+r.getWidth();
		double p2y = r.getY()+r.getHeight();
		
		if(
			 (p1x>=getWidth()+getX()) || (p1y>=getHeight()+getY()) ||
			 (p2x<=getX()) || (p2y<=getY())
			) {
			
			return false;
		}
		else 
			return true;
	}
	
	public double getArea() {return getWidth()*getHeight();}
	
	public Rect getBoundRectWith(Rect r) {
		return getBoundRect(this, r);
	}
	
	public Rect[] newFourBySplit() {
		Rect[] res = new Rect[4];
		res[0] = new Rect (getX(), getY(), getWidth()/2, getHeight()/2);
		res[1] = new Rect (getX()+getWidth()/2, getY(), getWidth()/2, getHeight()/2);
		res[2] = new Rect (getX(), getY()+getHeight()/2, getWidth()/2, getHeight()/2);
		res[3] = new Rect (getX()+getWidth()/2, getY()+getHeight()/2, getWidth()/2, getHeight()/2);
		
		return res;
	}
	
	private Rect getBoundRect(Rect r1, Rect r2) {
		double xmin,ymin,xmax,ymax;
		xmin = r1.getX() <= r2.getX()?r1.getX():r2.getX();
		ymin = r1.getY() <= r2.getY()?r1.getY():r2.getY();

		xmax = r1.getX()+r1.getWidth() >= r2.getX() + r2.getWidth()?r1.getX()+r1.getWidth():r2.getX()+r2.getWidth();
		ymax = r1.getY()+r1.getHeight() >= r2.getY() + r2.getHeight()?r1.getY()+r1.getHeight():r2.getY()+r2.getHeight();
		
		return new Rect(xmin,ymin,xmax-xmin,ymax-ymin);
	}

	public double getDist(Rect w) {
		return Math.sqrt(
				(getX()-w.getX())*(getX()-w.getX()) +
				(getY()-w.getY())*(getY()-w.getY())
				);
	}
}
