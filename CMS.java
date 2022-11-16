
/**
 * @author Xiaoyun Fu and Gaurav Raj 
 * All rights reserved.
 * 
 * This class implements a Count-Min Sketch data structure which can be used to store a multi-set S.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class CMS
{
  /**
   * instance variables
   */
  int m; //the size of each hash table
  int k; //the number of hash functions used
  int p; // the least prime number that is >= m
  int[] a; //used to store the k random numbers(a \in [1, p-1]) 
  int[] b; //used to store the k random numbers(b \in [0, p-1]) 
  int[][] CMS; // a 2-D array to store the approximate frequencies of elements in data set S
  int n; //the size of the data set S
  HashSet<String> data;//double check if this is needed. used for heavy hitter
  
  /**
   * Build the data structure CMS(epsilon, delta) to store the multi-set S
   * @param epsilon an input used to calculate hash table size
   * @param delta  an input used to calculate number of hash functions
   * @param S a multi-set(ArrayList) of Strings
   */
  public CMS(float epsilon, float delta, ArrayList<String> S)
  {
	  m = (int) (2.0/epsilon + 1);
	  k = (int) (Math.log(1.0/delta)/Math.log(2)) ; 
	  p = leastPrime(m);
	  a = new int[k];
	  b = new int [k];
	  hashFunctions(k); // find the k pairs of <a, b>, fill up array a and b
	  n = S.size();
	  
	//store all distinct values in data, used for heavy hitter
	  data = new HashSet<String>();
	  for(int i = 0; i < S.size(); i++)
	  {
		  String x = S.get(i);
		  data.add(x);
	  }
	  
	  CMS = new int[k][p]; //use p >= m as the table size, all entries in CMS is by default the value 0
	  //Store S in CMS by incrementing CMS[i][hi(x)] for all x in S and all i in [0, k-1]	  
	  for(int j = 0; j < n; j++)  //loop through all elements x in s
	  {		  
		  for(int i = 0; i < k; i++) //compute all hi(x) for i = 0, 1, ..., k-1
		  {
			  String test = S.get(j);
			  int hashValue = hashValue(i, fi(i, test)); //calculate hi(x), 
			       //change input x to fi(i, x) according to the ith function in order to make all the k hash values for x independent.
			  CMS[i][hashValue] ++; 
		  }
	  }
  }
  
  /**
   * Gives an approximate value of the number of times x appears in S by consulting CMS
   * @param x a String whose frequency in S is to be found
   * @return the approximate value of the frequency of x in S
   */
  public int approximateFrequency(String x)
  {
	  int count = CMS[0][hashValue(0, fi(0, x))]; //count is used to find the min of CMS[i][hi(x)]
	  for(int i = 1; i < k; i++)
	  {
		  int cur = CMS[i][hashValue(i, fi(i, x))];
		  if( cur < count)
			  count = cur;
	  }
	  return count;
  }
  
  /**
   * Compute an approximate <q, r>-Heavy Hitter L with the property
   * that for all x in S with frequency >= q*n, x is in L; and for all x in S with frequency < rN, x is not in L.
   * n is the size of S
   * @param q a precision measure, assume that q >= r + epsilon
   * @param r a precision measure
   * @return an ArrayList of Strings in S with frequency >= rN
   */
  public ArrayList<String> approximateHeavyHitter(float q, float r)
  {
	  ArrayList<String> hitter = new ArrayList<String>();
	  //check every distinct element x in S, if approximateFrequency(x) >= q*n, add it to hitter
	 Iterator<String> it = data.iterator();  //loop through all distinct elements x in S
	 while(it.hasNext()) 
	 {
		  String x = it.next();
          if(approximateFrequency(x) >= q*n)
        	  hitter.add(x);
	  }
	  return hitter;
  }
  
  /**
   * Compute the hash value of x using the ith hash function. 
   * Convert x(String) to int using hashCode() method: called pre-hashing
   * @param i indicate the ith hash function is used. i \in {0, 1, ..., k-1}
   * @param str a string whose hash value is to be computed
   * @return hi(str) the hash value of str using the ith hash function
   */
  private int hashValue(int i, String str)
  {
	  int x = str.hashCode();	  
	  return Math.abs((a[i]*x + b[i])) % p;  //a[i] \in {1, 2, ..., p-1}, b[i] \in {0, 1, ..., p-1}, 
	                                                            // %p makes sure the returned hashValue < p
  }
  
  /**
   * Generate k pairs of random numbers <a, b> and store them in int[] a and int[] b
   * @param k the number of hash functions(num of <a, b> pairs)
   */
  private void hashFunctions(int k)
  {
	  Random rand = new Random();
	  for(int i = 0; i < k; i++)
	  {
		  int  x = rand.nextInt(p - 1) + 1; // x \in {1, 2, ..., p - 1}
		  a[i] = x;
		  int  y = rand.nextInt(p);    //y \in {0, 1, 2, ..., p - 1}
		  b[i] = y;  
	  }	   
  }
  
  /**
   * Change the input string x for the ith hash function by appending the ith character in x to x. Assume i is in range [0, k-1]	 
   * If the length of x is less than the number of hash functions, then those places are filled with $.
   * i denotes the ith hash function.
   * @param i the ith hash function, i starts with 0
   * @param x the input string
   * @return x appended by its ith character
   */
  private String fi(int i, String x)
  {
	   String test = x;
	   if(x.length() < k)
	       for(int j = 0; j< k - x.length(); j++)
	    	   test = test + "$";
	   return test + test.charAt(i);
  }
  
  /**
   * Find the least prime number p that is greater than hash table size m
   * @param m an int 
   * @return the least prime number p that is greater than m
   */
  private int leastPrime(int m)
  {
	  for(int i = m; m < 2*m; i++)
	      if(isPrime(i))
	    	  return i;
	  return 0;
  }
  
  /**
   * Check whether an input value n is a prime or not.
   * @param n an input integer value
   * @return true if the input value n is a prime; false otherwise.
   */
  private boolean isPrime(int n)
  {
	    // Corner cases
	    if (n <= 1)  return false;
	    if (n <= 3)  return true;

	    // This is checked so that we can skip middle five numbers in below loop
	    if (n%2 == 0 || n%3 == 0) return false;
        // It uses the fact that a prime (except 2 and 3) is of form 
	    // 6k - 1 or 6k + 1 and looks only at divisors of this form.
	    for (int i=5; i <= Math.sqrt(n); i=i+6)
	        if (n%i == 0 || n%(i+2) == 0)  //i is of the form 6k-1 and i+2 is of the form 6k+1
	           return false;

	    return true;
  }
}
