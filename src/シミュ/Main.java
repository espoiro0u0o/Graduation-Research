package シミュ;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.Line;
import javax.xml.crypto.Data;

class readcsv{
	String line;
	String[] read1(String data[],File f){
	    try {
	        BufferedReader br = new BufferedReader(new FileReader(f));
	         // 1行ずつCSVファイルを読み込む
	        while ((line = br.readLine()) != null) {
	        	data = line.split(",", 0); // 行をカンマ区切りで配列に変換
	        }
	        br.close();
	      } catch (IOException e) {
	        System.out.println(e);
	      }
		return data;	
	}
	
	int[][] timetable(String data[],int TrainTimeTable[][]){
		for (int i = 0; i < data.length; i++) {
			int buff;
			for (int k = 1; k < 5; k++) {
				int buff2;
				if (data[i].charAt(k)!=':'&&k==1) {
					buff2 = Character.getNumericValue(data[i].charAt(k-1))*10+Character.getNumericValue(data[i].charAt(k));
					TrainTimeTable[i][0] = buff2;
				}
				else {
					k++;
					buff = Character.getNumericValue(data[i].charAt(k))*10+Character.getNumericValue(data[i].charAt(k+1));
					TrainTimeTable[i][1] = buff;				
					k=k+2;
				}			
			}
		}
		return TrainTimeTable;
	}
	
}
//指数分布と秒計算
class index{
	int[] distribution(int lambda){
		double tau;
		int data[] = new int[2];
		int i=0;
		tau = -lambda * Math.log(1.0 -Math.random());
		if (tau<1000) {
			data[0]=0;
			data[1]=(int)tau;
		}else{
			data[1]=(int)tau;
			for(i=0;tau>1000;i++){
				tau-=1000;
			}
			data[0]=i;
			data[1]=(int)tau;
		}
		/*data[0] = (int)tau;
		tau = tau-(int)tau;
		tau = tau*100;
		data[1] = (int)tau;*/
		return data;
	}
}
//1000ms後で直す
class time{
	int[] timeProcessing(int i,int time[]){
		int msec = 0;
		int k=0;
		msec = i;
		while(msec>1000){
			msec = msec-1000;
			k++;
		}
		//System.out.println(k);
		time[3] = msec;
		if (time[3]>=1000) {
			time[2]++;
			time[3]-=1000;
		}
		if (time[2]>=60) {
			time[1]++;
			time[2]-=60;
		}
		if (time[1]>=60) {
			time[0]++;
			time[1]-=60;
		}
		//System.out.println(time[0]+""+time[1]+""+time[2]+time[3]);
		return time;
	}

	
}

public class Main {
	public static void main(String args[]){
		File train = new File("/Users/misaki/Documents/卒研/シミュレーション/traintime.csv");
		File buss = new File("/Users/misaki/Documents/卒研/シミュレーション/busstime.csv");
		FileWriter fw = null;
		try {
			fw = new FileWriter("/Users/misaki/Documents/卒研/シミュレーション/test.csv", false);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}  
        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		int[] time = new int[4];
		time[0] = 8;
		time[1] = 52;
		time[2] = 0;
		time[3] = 0;
		int[] timebuff = new int[4];
		Arrays.fill(timebuff, 0);
		int[] Traintimebuff = new int[4];
		Arrays.fill(Traintimebuff, 0);
		int[] BussExit = new int[3];
		int msec,kankaku=1500;
		int BussPassenger=0,IsBussExisting=0;
		int len = 0,buffflag=0;
		int j=0,k=0,buff=0,plus2=0,minus2=0;
		String data[] = null;
		//csv読み込み
		readcsv test1 = new readcsv();
		data = test1.read1(data,train);
		//data.length
		int[][] TrainTimeTable=new int[data.length][2];
		//とりあえず一限の電車時刻表
		TrainTimeTable = test1.timetable(data,TrainTimeTable);
		data = test1.read1(data,buss);
		int[][] BussTimeTable = new int [data.length][2];
		BussTimeTable = test1.timetable(data, BussTimeTable);
		int[] bussArrive = new int[3];
		Arrays.fill(timebuff, 0);
		int[] trainArrive = new int[3];
		Arrays.fill(trainArrive, 0);
		index sisu = new index();
		time test = new time();
		//System.out.println(test[0]+":"+test[1]);
		System.out.println("時間		待ち人数		高坂住到着		電車到着		バス到着");
		//i=1630,97000->9:18
		for(int i=0;i<1620000;i++){
			int trainflag=0,bussflag=0,takasakaflag=0;
			//時間処理
			msec = i;
			time = test.timeProcessing(msec,time);
			//電車到着時刻か？
			if (time[0]==TrainTimeTable[j][0]&&time[1]==TrainTimeTable[j][1]) {
				trainflag = 1;
				int ran=0;
				trainArrive = sisu.distribution(500);
				Traintimebuff[0]=time[0];
				Traintimebuff[1]=time[1];
				Traintimebuff[2]=time[2]+trainArrive[0];
				Traintimebuff[3]=time[3]+trainArrive[1];
				for(int i1=3;i1>0;i1--){
					int l=0;
					if (i1==3) {
						l=1000;
					}else{
						l=60;
					}
					if (Traintimebuff[i1]>=l) {
						Traintimebuff[i1-1] += Traintimebuff[i1]/l;
						Traintimebuff[i1] %=l;
					}	
				}
					plus2 = TrainTimeTable[j][1]+1%60;
				
				//乱数発生させて待ち列に追加
				Random rand = new Random();
				if (time[0]==8||time[1]<5) {
					ran = rand.nextInt(10)+42;
					//ran = 47;
				}else if (time[0]==9&&time[1]==10) {
					ran = rand.nextInt(10);
					//ran = 4;
				}
				 else if (time[0]==9&&time[1]==7) {
					 //ran = rand.nextInt(20)+78;
					 ran = 91;
				}
				else if (time[0]==9&&time[1]==16) {
					ran = rand.nextInt(5)+9;
					//ran = 22;
				}
				else {
					//ran = rand.nextInt(20) + 10;
					//ran = rand.nextInt(5) + 25;
					ran = 50;
				}
				j++;
				buff += ran;
				//応急処置
				if(j>(TrainTimeTable.length)-1){
					j=0;
				}
			}
			//電車民を列に加える処理
			//30秒で20人通過ー＞1秒0.66人->10秒7人くらい
			//1分半乗り換え->(plus2==time[1])||
			if ((plus2==time[1])||buffflag==1) {
				//System.out.println(plus2);
				buffflag=1;
				//buff!=0&&(time[2]%a==0&&time[3]==0)*/
				if (buff!=0&&(time[0]==Traintimebuff[0]&&time[2]==Traintimebuff[2]&&time[3]==Traintimebuff[3])) {
					buffflag=1;
					//System.out.println("if文");
					//System.out.println(Traintimebuff[0]+":"+Traintimebuff[1]+":"+Traintimebuff[2]+":"+Traintimebuff[3]);
					len++;
					buff--;	
					trainArrive = sisu.distribution(900);
					Traintimebuff[0]=time[0];
					Traintimebuff[1]=time[1];
					Traintimebuff[2]=time[2]+trainArrive[0];
					Traintimebuff[3]=time[3]+trainArrive[1];
					for(int i1=3;i1>0;i1--){
						int l=0;
						if (i1==3) {
							l=1000;
						}else{
							l=60;
						}
						if (Traintimebuff[i1]>=l) {
							Traintimebuff[i1-1] += Traintimebuff[i1]/l;
							Traintimebuff[i1] %=l;
						}	
					}
					}	
				if (buff==0) {
					buffflag = 0;
				}
				
			}
			//高坂民到着か？
			//30秒に1回
			//Math.random()<0.047
			/*if (time[0]==8&&time[1]<59) {
				if(Math.random()<0.065){
					System.out.println("aa");
					takasakaflag = 1;
					len++;
				}
			}else if(Math.random()<0.01){
				takasakaflag = 1;
				len++;
			}*/
			//バス到着か？
			//出発2分前に到着
			minus2 = BussTimeTable[k][1]-2;
			if (minus2<0) {
				minus2 = 60+minus2;
				//あとで汎用的に
				BussTimeTable[k][0]=8;
			
			}
			//バスに乗せる処理
			if(time[0]==BussTimeTable[k][0]&&time[1]==minus2){
				bussArrive = sisu.distribution(1200);
				timebuff[0]=time[0];
				timebuff[1]=time[1];
				timebuff[2]=time[2]+bussArrive[0];
				timebuff[3]=time[3]+bussArrive[1];
				//System.out.println("最初の指数分布");
				//System.out.println(timebuff[0]+":"+timebuff[1]+":"+timebuff[2]+":"+timebuff[3]);
				//バス滞在時間出す
				for (int l = 0; l <= 2; l++) {
					BussExit[l] = (minus2+l)%60;
				}
				bussflag = 1;
				//バス何人乗れるか
				if (len<=55) {
					BussPassenger = 55;
				} else {
					Random rand = new Random();
					BussPassenger = rand.nextInt(15) + 55;
				}
				k++;
				//応急処置2
				if(k>(BussTimeTable.length)-1){
					k=0;
				}
			}
			
			//バスが滞在してるか
			for (int l = 0; l < 2; l++) {
				if (BussExit[l]==time[1]) {
					IsBussExisting=1;
					break;
				}
				IsBussExisting=0;
			}
			//バスに乗り込む
			if (time[0]==timebuff[0]&&time[1]==timebuff[1]&&time[2]==timebuff[2]&&time[3]==timebuff[3]) {
				//System.out.println("if文の中");
				//System.out.println(time[0]+":"+time[1]+":"+time[2]+":"+time[3]);
				//System.out.println(len);
				//&&IsBussExisting==1
				if ((len>0&&BussPassenger>0)) {
					len--;
					BussPassenger--;
				}
				if (IsBussExisting==1) {
					if (BussPassenger<25) {
						kankaku=1500;
					}
					bussArrive = sisu.distribution(kankaku);
					timebuff[0]=time[0];
					timebuff[1]=time[1];
					timebuff[2]=time[2]+bussArrive[0];
					timebuff[3]=time[3]+bussArrive[1];
					for(int i1=3;i1>0;i1--){
						int l=0;
						if (i1==3) {
							l=1000;
						}else{
							l=60;
						}
						if (timebuff[i1]>=l) {
							timebuff[i1-1] += timebuff[i1]/l;
							timebuff[i1] %=l;
						}	
					}
				}
			}

			if (time[2]%10==0&&time[3]==0) {
				System.out.println(time[0]+":"+time[1]+":"+time[2]+"		"+len+"		"+takasakaflag+"		"+trainflag+"		"+bussflag);
				//System.out.println("収容可能人数"+BussPassenger);
				pw.println(time[0]+":"+time[1]+":"+time[2]+","+len);
			}
		}
		pw.close();
	}		
	
}
