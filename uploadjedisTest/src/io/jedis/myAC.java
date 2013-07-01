package io.jedis;
//java client to access redis
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.PipelineBlock;
/*
 * @author shin
 */
public class myAC {

	/**
	 * @param args
	 */
	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		Jedis jed = new Jedis("localhost", 6379);
		jed.connect();
		System.out.println("Connected");
		String temp = "";
		String tempIn = "";
		int count = 0;
		try {
         File file = new File ("/home/user/pys/female-names.txt");
         Scanner input = new Scanner(file);
         while(input.hasNext()){
        	 temp = input.next();
        	 //System.out.println(temp);//debug only
        	 for (int i = 0; i < temp.length(); i++){
        		 //System.out.println(temp.substring(0,i));//debug only
        		 tempIn = temp.substring(0,i);
        		 //jed.zadd(tempIn, 0, "zsetName");
        		 jed.zadd("zsetName",0,tempIn);//zsetName is name of the member set
        		 if(i == temp.length()-1){
        			 //System.out.println(temp+"*");//debug only
        			 tempIn = temp+"*";
        			 jed.zadd("zsetName",0,tempIn);
        			 count++;
        		 }//end if
        		 count++;
        	 }
         }//end while
         input.close();
         
         //query here
         System.out.println("auto completion word or sentence. Items in set " +count+ "");
         String strQ = "zuza";//find all name start with zuza 11821; mar = 7739
         printRed("zrank(\"zsetName\"," + strQ+ ")", jed.zrank("zsetName", strQ));
         
         long numR = jed.zrank("zsetName", strQ);//rank ret frm set
         System.out.println("rank in set numR is "+ numR);
         
         //hardcode: debug only
         //printRed("HC zrange(" + strQ + ", 11821, Integer.MAX_VALUE)", jed.zrange("zsetName", 11821, Integer.MAX_VALUE));//ret [] only
         
         //using num rank
         printRed("zrange(" + strQ + "," +numR + ", Integer.MAX_VALUE)",jed.zrange("zsetName", numR, Integer.MAX_VALUE));

         
         //debug only
         //printRed("zrange(\"zsetName\", 0, Integer.MAX_VALUE)",jed.zrange("zsetName", 0, Integer.MAX_VALUE));//works
         
        jed.expire("zsetName", 4);//retire key val
        
 		Thread.sleep(6500);
 		printRed("zrange(\"zsetName\", 0, Integer.MAX_VALUE)",jed.zrange("zsetName", 0, Integer.MAX_VALUE));
         
		}//end try
		catch(RuntimeException e){
			e.printStackTrace();
		}//end catch
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//end catch		
		
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			jed.disconnect();
			System.out.println("\nDisconnected");
		}//end finally
		
	}//end main
	
	static void printRed(String command, Object result) {
		System.out.printf("%-55s: %s%n", command, result);
	}

}
