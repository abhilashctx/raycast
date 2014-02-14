/**
 * This source belongs to the author Abhilash
 * Contact me if you want to use it.
 */
package test;

import java.util.Random;

public class CRC {

	private static final int LENGTH=256;
	private int crc_tab[];
	
	public CRC() {
		crc_tab=new int[LENGTH];
		int poly = 0xedc9;//0xFABD;//0x1021;
		compute_table(poly);
	}
	
	private void compute_table(int poly){
		for(int i=0;i<LENGTH;i++){
			int r=(i<<8);
			for(int j=0;j<8;j++){
				r=( ((r&0x8000)>0) ? (r<<1)^poly : (r<<1) ) & 0xFFFF;
			}
			crc_tab[i]=r;
		}
	}
	
	public void setPoly(int poly){
		compute_table(poly);
	}
	
	public void print_crc_table(){
		for(int i=0;i<LENGTH;i++){
			System.out.println(i+" = "+Integer.toHexString(crc_tab[i]));
		}
	}
	
	public int crc(String msg){
		int r=0;
		for(int i=0;i<msg.length();i++){
			r=((crc_tab[(msg.charAt(i)^(r>>8))&0xFF] ^ (r<<8))&0xFFFF);
		}
		return r;
	}
	
	public String toHex(int msg){
		return Integer.toHexString(msg);
	}
	
	public static void main(String[] args) {
		CRC crc = new CRC();
		//crc.print_crc_table();
		System.out.println("crc="+crc.crc("Hello World!"));
		
		int collision_count=0;
		int total_rounds=1000;
		
		Random rand=new Random(System.currentTimeMillis());
		for(int c=0;c<total_rounds;c++){
			int crctest[]=new int[(1<<16)];
			for(int i=0;i<1000;i++){
				int x=rand.nextInt((1<<20)+1);
				int xcrc = crc.crc(""+x);
				if(crctest[xcrc]!=0 && crctest[xcrc]!=x){
					//System.out.println("Collision: "+crctest[xcrc]+" : "+x+" crc("+crc.toHex(xcrc)+")");
					collision_count++;
				}else{
					crctest[xcrc]=x;
				}
			}
		}
		int collision_per_1000=collision_count/total_rounds;
		System.out.println("avg collision per round is "+collision_per_1000);
		System.out.println(collision_count);
	}
}
