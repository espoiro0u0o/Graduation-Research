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
import java.util.Iterator;
import java.util.Random;

import javax.sound.sampled.Line;
import javax.xml.crypto.Data;

/*やること
 * 生データ差し替え
 * バス人数二項分布
 * 時刻表最初から最後まで探索型にする、そのうち
 * */
class readcsv{
	String line;
	String[] read1(String data[],File f){
	    try {
	        BufferedReader br = new BufferedReader(new FileReader(f));
	         // 1行ずつCSVファイルを読み込む
	        while ((line = br.readLine()) != null) {
	        	data = line.split(",", 0); // 行をカンマ区切りで配列に変換
	          /*for (String elem : data) {
	            System.out.println(elem);
	          }*/
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


public class Main {
	public static void main(String args[]){
		File train = new File("/Users/misaki/Documents/卒研/シミュレーション/traintime.csv");
		File buss = new File("/Users/misaki/Documents/卒研/シミュレーション/busstime.csv");
		FileWriter fw = null,fw2=null;
		try {
			fw = new FileWriter("/Users/misaki/Documents/卒研/シミュレーション/test.csv", false);
			fw2 = new FileWriter("/Users/misaki/Documents/卒研/シミュレーション/komaeda.csv", false);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}  
        PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
		int[] time = new int[3];
		time[0] = 8;
		time[1] = 52;
		time[2] = 0;
		int sec;
		int len = 0;
		int j=0,k=0,buff=0;
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
		//data = test1.read1(data, komaeda);
		//System.out.println(data[0]);
		System.out.println("時間		待ち人数		高坂住到着		電車到着		バス到着");
		//pw.println("時間		待ち人数		高坂住到着		電車到着		バス到着");
		for(int i=0;i<2000;i++){
			int trainflag=0,bussflag=0,takasakaflag=0;
			//時間処理
			sec=i;
			while(sec>60){
				sec = sec-60;
			}
			time[2]=sec;
			if (time[2]==60) {
				time[1]++;
				time[2]=0;
			}
			if (time[1]==60) {
				time[0]++;
				time[1]=0;
			}
			//電車到着時刻か？
			if (time[0]==TrainTimeTable[j][0]&&time[1]==TrainTimeTable[j][1]) {
				trainflag = 1;
				int ran=0;
				//乱数発生させて待ち列に追加
				Random rand = new Random();
				if (time[0]==8||time[1]<5) {
					//ran = rand.nextInt(10)+42;
					ran = rand.nextInt(20)+35;
				}else if (time[0]==9&&time[1]==10) {
					ran = rand.nextInt(10);
				}
				 else if (time[0]==9&&time[1]==6) {
					 //ran = rand.nextInt(10)+98;
					 ran = rand.nextInt(20)+50;
				}
				else if (time[0]==9&&time[1]==16) {
					ran = rand.nextInt(5)+9;
				}
				else {
					//ran = rand.nextInt(20) + 10;
					ran = rand.nextInt(5) + 25;
				}
				System.out.println(ran);
				//予備学科
				if (time[0]==9&&(time[1]==6||time[1]==13)) {
					buff = ran;
				}
				else{
					len+=ran;
				}
				j++;
				//応急処置
				if(j>(TrainTimeTable.length)-1){
					j=0;
				}
			}
			//高坂民到着か？
			//30秒に1回
			//Math.random()<0.047
			if (time[0]==8&&time[1]<59) {
				if(Math.random()<0.065){
					takasakaflag = 1;
					len++;
				}
			}else if(Math.random()<0.01){
				takasakaflag = 1;
				len++;
			}
			//バス到着か？
			if(time[0]==BussTimeTable[k][0]&&time[1]==BussTimeTable[k][1]){
				bussflag = 1;
				if(len<=55){
					len=0;
				} else {
					int ran;
					Random rand = new Random();
					ran = rand.nextInt(15) + 55;
					if (len>ran) {
						len-=ran;
					}else{
						len = 0;
					}
				}
				k++;
				//応急処置2
				if(k>(BussTimeTable.length)-1){
					k=0;
				}
			}
			//予備学科
			if (time[0]==9&&(time[1]==10||time[1]==15)) {
				len+=buff;
				buff = 0;
			}
			//time[2]%10==0
			if (time[2]==0) {
				System.out.println(time[0]+":"+time[1]+":"+time[2]+"		"+len+"		"+takasakaflag+"		"+trainflag+"		"+bussflag);
				pw.println(time[0]+":"+time[1]+":"+time[2]+","+len);
			}
		}
		pw.close();
	}		
	
}
