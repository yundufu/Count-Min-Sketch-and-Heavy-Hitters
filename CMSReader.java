
/**
 * @author Xiaoyun Fu and Gaurav Raj 
 * All rights reserved.
 * 
 * This class stores an ArrayList of strings(obtained by reading the file shakepear.txt) in a CMS data  
 * structure, and uses it to construct a <q,r>-heavy hitter L.  
 * The performance of the CMS data structure in computing heavy hitters is tested.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class CMSReader
{
	public static void main(String[] args)
	{
		try
		{
			ArrayList<String> s = input("shakespear.txt");
			PrintWriter out = new PrintWriter(new File("originalS.txt"));
			for(int i = 0; i < s.size(); i++)
				out.print(s.get(i) + " ");
			out.close();
			
			//store s in a HashTable<String, Integer> where strings in s are used as keys
			//and the number of times x appears in s is used as Values
			Hashtable<String, Integer> t = new Hashtable<String, Integer>();
			for(int i = 0; i < s.size(); i++)
			{
				String x = s.get(i);
				if(t.containsKey(x))
				{
					t.put(x, t.get(x)+ 1);
				}
				else
					t.put(x, 1);
			}
			
			PrintWriter put = new PrintWriter(new File("distinctS.txt"));
			Set<String> keys = t.keySet();
			put.println(keys.size());
			Iterator<String> it = keys.iterator();
			while(it.hasNext())
			{
				String x = it.next();
				int count = t.get(x);
				put.println(x + " " + count);
			}
			put.close();
			
			//store s in CMS(0.01, 2^-20)
			CMS cms = new CMS((float) 0.01, (float) Math.pow(2, -20), s);
			ArrayList<String> L = cms.approximateHeavyHitter((float) 0.04, (float) 0.03);
			PrintWriter write = new PrintWriter(new File("HeavyHitterL.txt"));
			for(int i = 0; i < L.size(); i++)
				write.print(L.get(i) + " ");
			write.close();
			
			int n = s.size();
			int numGreaterThan04n = 0;  //the number of elements x in L whose frequency is >= 0.04 * |S| 
			int numGreaterThan025n = 0; //the number of elements x in L whose frequency is >= 0.025 * |S| 
			int numLessThan025n = 0; //the number of elements x in L whose frequency is < 0.025 * |S| 
			int numLessThan04n = 0; //the number of elements x in L whose frequency is < 0.04 * |S| 
			for(int i = 0; i < L.size(); i++)
			{
				String x = L.get(i);
				int count = (int) t.get(x);
				if(count >= 0.025 * n)
					numGreaterThan025n++;
				if(count >= 0.04 * n)
					numGreaterThan04n++;
				if(count < 0.04 * n)
					numLessThan04n++;
				if(count < 0.025 * n)
					numLessThan025n++;
			}
			
			System.out.println("Number of elements in L whose frequency is >= 0.04*|S|: " + numGreaterThan04n);
			System.out.println("Number of elements in L whose frequency is >= 0.025*|S|: " + numGreaterThan025n);
			System.out.println("Number of elements in L whose frequency is < 0.04*|S|: " + numLessThan04n);
			System.out.println("Number of elements in L whose frequency is < 0.025*|S|: " + numLessThan025n);
			System.out.println("Total number of strings added to CMS: " + s.size());
		    System.out.println("Total number of DISTINCT strings added to CMS:" + t.keySet().size());
		    System.out.println("estimate total memory to store CMS: " + 16000 + " bytes, calculates by 2/0.01 * log2(2^20) * 4 bytes.");
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Read a file of text and store each word (length >= 3) in an ArrayList of Strings (a multi-set). 
     * Do not store "The" and "the".
     * @param filename the file to read text from
	 * @return An ArrayList of strings
	 * @throws FileNotFoundException
	 */
	public static ArrayList<String> input(String filename) throws FileNotFoundException
	{
		Scanner s = new Scanner(new File(filename));
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNext()){		
			String x = s.next();
			if(x.length() >= 3 && !x.equals("the") && !x.equals("The"))
				list.add(x);			
		}
		s.close();	
		return list;
	}
}
