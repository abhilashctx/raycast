package test;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class RayTest extends Frame{
	
	public static boolean RUN=false;
	
	BufferedImage bi;
	Graphics2D g;
	
	//window width,height
	public static int WW=260;
	public static int WH=200;
	
	//view width, height
	public static int SW=260;
	public static int SH=200;
	public static int TS=32; //tile size
	
	//public static int a80=SW;
	//public static int a60=a80*3/4;
	public static int a60=SW;
	public static int a30=a60/2;
	public static int a90=a30*3;
	public static int a180=a60*3;
	public static int a270=a90*3;
	public static int a360=a180*2;
	
	public boolean kup=false;
	public boolean kdw=false;
	public boolean kle=false;
	public boolean kri=false;

	//public static int FOV=60;
	public static int pd=1; //projection distance
	public static int pa=a360-50; //current angle
	public static int px=150,py=150; //current position
	
	public float fsin[],fcos[],ftan[];
	
	
	public static final int map[][]={
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		{1,0,0,0,0,0,0,0,0,0,0,2,0,0,0,1},
		{1,0,0,0,1,0,0,0,0,0,0,2,0,0,0,1},
		{1,0,2,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,3,3,3,3,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,0,3,0,2,0,3,0,0,2,0,0,0,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,0,3,0,2,0,3,0,0,2,0,0,0,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,0,0,0,0,0,3,0,0,2,0,0,0,0,1},
		{1,0,0,1,1,1,0,0,0,0,0,0,0,0,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
		{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
	};
	
	public static float toRad(float angle){
		return ((float)(angle*Math.PI)/a180);
	}
	public static float dist(float dx,float dy){
		return (float)Math.sqrt(sqr(px-dx)+sqr(py-dy));
	}
	
	public RayTest() {
		
		setSize(WW, WH);
		setVisible(true);
		
		enableEvents(WindowEvent.WINDOW_CLOSING|KeyEvent.KEY_PRESSED);
		
		fsin=new float[a360+1];
		fcos=new float[a360+1];
		ftan=new float[a360+1];
		for(int i=0;i<=a360;i++){
			float rad=toRad(i)+(float)0.00001;
			fsin[i]=(float)Math.sin(rad);
			fcos[i]=(float)Math.cos(rad);
			ftan[i]=(float)Math.tan(rad);
		}
		
		//pd=256;
		pd = (int)((SW/2)/ftan[a30]);
		System.out.println(pd);
	}
	
	protected void processEvent(AWTEvent e) {
		KeyEvent ke;
		switch(e.getID()){
		case WindowEvent.WINDOW_CLOSING:
			RUN=false;
			break;
		case KeyEvent.KEY_PRESSED:
			ke=(KeyEvent)e;
			switch(ke.getKeyCode()){
				case KeyEvent.VK_DOWN:  kdw=true; break;
				case KeyEvent.VK_UP:    kup=true; break;
				case KeyEvent.VK_LEFT:  kle=true; break;
				case KeyEvent.VK_RIGHT: kri=true; break;
			}
			break;
		case KeyEvent.KEY_RELEASED:
			ke=(KeyEvent)e;
			switch(ke.getKeyCode()){
				case KeyEvent.VK_DOWN:  kdw=false; break;
				case KeyEvent.VK_UP:    kup=false; break;
				case KeyEvent.VK_LEFT:  kle=false; break;
				case KeyEvent.VK_RIGHT: kri=false; break;
			}
			break;
		}
	}
	
	public void init(){
		bi = new BufferedImage(SW, SH, BufferedImage.TYPE_INT_ARGB);
		g=bi.createGraphics();
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		RUN=true;
	}
	
	public void run(){
		
		g.setColor(Color.black);
		g.fillRect(0, 0, SW, SH);
		
		g.setColor(Color.white);
		
		if(kle){pa-=9; if(pa<0) pa+=a360;}
		if(kri){pa+=9; if(pa>a360) pa-=a360;}
		
		float xdir=fcos[pa]*3;
		float ydir=fsin[pa]*3;
		
		if(kup){px+=xdir;py+=ydir;}
		if(kdw){px-=xdir;py-=ydir;}
		
		
		rays(g);
		//g.drawRect(20, 20, 50, 30);
		
		g.setColor(Color.green);
		g.drawString("("+px+","+py+","+pa+")", 20, SH-20);
		
		//getGraphics().drawImage(bi, 0, 0, this);
		getGraphics().drawImage(bi, 0, 0, WW, WH, 0, 0, SW, SH, this);
	}
	
	public void rays(Graphics2D g){
		int ra=pa-a30;
		if(ra<0) ra+=a360;
		int hg=0,nhg=0;
		int vg=0,nvg=0;
		for(int ri=0;ri<SW;ri++){
			/*if(ra>0 && ra<a180){
				hg=(py/TS)*TS+TS;
				nhg=TS;
			}else{
				hg=(py/TS)*TS-1;
				nhg=-TS;
			}
			float xint=px+(hg-py)/ftan[ra];
			float xstep=TS/ftan[ra];
			if(ra<a90 || ra>a270){
				vg=(px/TS)*TS+TS;
				nvg=TS;
			}else{
				vg=(px/TS)*TS-1;
				nvg=-TS;
			}
			float yint=py+(vg-px)/ftan[ra];
			float ystep=TS*ftan[ra];*/
			
			float xdir=fcos[ra]*3;
			float ydir=fsin[ra]*3;
			float nx=px;//+xdir;
			float ny=py;//+ydir;
			while(true){
				int inx=(int)nx/TS;
				int iny=(int)ny/TS;
				if(inx<0 || iny<0 || inx>=16 || iny>=16){
					//System.out.println("no wall");
					break;
				}
				if(map[iny][inx]!=0){//wall
					//System.out.println("w["+inx+","+iny+"]");
					int fishang = ra-pa;
					if(fishang<0) fishang+=a360;
					float wdist = dist(nx,ny)*fcos[fishang];
					int dlen = (int)(((float)TS*pd)/wdist);
					drawWall(g, ri, dlen, map[iny][inx]);
					break;
				}
				nx+=xdir;ny+=ydir;
			}
			
			ra+=1;
			if(ra>=a360) ra-=a360;
		}
	}
	
	public void drawWall(Graphics2D g,int ri,int wlen,int wall){
		if(wall==1)	g.setColor(Color.white);
		else if(wall==2) g.setColor(Color.gray);
		else if(wall==3) g.setColor(Color.blue);
		int ys=(SH-wlen)/2;
		g.drawLine(ri, ys, ri, ys+wlen);
	}
	
	public static void main(String[] args) {
		RayTest rayTest = new RayTest();
		rayTest.init();
		while(rayTest.RUN){
			rayTest.run();sleep(10);
		}
		rayTest.dispose();
		System.exit(0);
	}
	public static void sleep(int t){
		try{Thread.sleep(t);}catch(Exception e){}
	}
	
	public static double sqr(double t){
		return t*t;
	}

}
