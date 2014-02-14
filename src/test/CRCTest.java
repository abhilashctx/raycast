package test;

import java.util.Random;

/*poly score
1000 rounds 100 poly
poly:3091 colis:7031
poly:6e39 colis:7139
poly:9f89 colis:7113
poly:edc9 colis:7109
*/
public class CRCTest {
	
	public static Random rand=new Random(System.currentTimeMillis());
	public static void main(String[] args) {
		
		int total_poly=100;
		int polys[]=new int[total_poly];
		int colis[]=new int[total_poly];
		genPoly(polys);
		polys[0]=0xedc9;
		
		CRC crc = new CRC();
		int total_rounds=1000;
		int crctest[]=new int[(1<<16)];
		for(int r=0;r<total_rounds;r++){
			
			//check colis
			//reset(colis);
			for(int c=0;c<total_poly;c++){
				reset(crctest);
				crc.setPoly(polys[c]);
				for(int i=0;i<2000;i++){
					int x=rand.nextInt((1<<20)+1);
					int xcrc = crc.crc(""+x);
					if(crctest[xcrc]!=0 && crctest[xcrc]!=x){
						//System.out.println("Collision: "+crctest[xcrc]+" : "+x+" crc("+crc.toHex(xcrc)+")");
						colis[c]++;
					}else{
						crctest[xcrc]=x;
					}
				}
			}
			
			//sort
			for(int s1=0;s1<(total_poly-1);s1++){
				int min=s1;
				for(int s2=s1+1;s2<total_poly;s2++){
					if(colis[s2]<colis[min]){
						min=s2;
					}
				}
				if(min!=s1){
					int tmp=polys[s1];
					polys[s1]=polys[min];
					polys[min]=tmp;
					tmp=colis[s1];
					colis[s1]=colis[min];
					colis[min]=tmp;
				}
			}
			System.out.println("round "+r+" poly:"+crc.toHex(polys[0])+" colis:"+colis[0]);
			
			//new polys
			/*int half_poly=total_poly/2;
			for(int s3=half_poly;s3<total_poly;s3++){
				int p1=rand.nextInt(half_poly);
				int p2=rand.nextInt(half_poly);
				polys[s3]=mut(combine(p1, p2));
			}*/
		}
	}
	
	public static int mut(int pr){
		if(rand.nextInt(100)<10){
			int bit = rand.nextInt(8);
			pr = ((pr^(1<<bit))&0xFFFF);
		}
		if(rand.nextInt(100)<10){
			int bit = rand.nextInt(8);
			pr = ((pr^(1<<bit))&0xFFFF);
		}
		if(rand.nextInt(100)<10){
			int bit = rand.nextInt(8);
			pr = ((pr^(1<<bit))&0xFFFF);
		}
		return pr;
	}
	public static int combine(int p1,int p2){
		int pr=0;
		int chance=rand.nextInt(100);
		if(chance<25){
			pr=(p1^p2);
		}else if(chance<50){
			pr=((p1*p2)&0xFFFF);
		}else if(chance<75){
			pr=(((p1<<(rand.nextInt(12)+2))|p2)&0xFFFF);
		}else{
			pr=(((p2<<(rand.nextInt(12)+2))|p1)&0xFFFF);
		}
		return pr;
	}
	public static void genPoly(int polys[]){
		for(int i=0;i<polys.length;i++) polys[i]=rand.nextInt((1<<16))+1;
	}
	public static void reset(int a[]){
		for(int i=0;i<a.length;i++) a[i]=0;
	}
}
