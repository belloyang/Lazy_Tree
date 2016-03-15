import java.util.Random;

public class Key{
	public static final int EXPONENT_OF_BASE2 = 10;
	public static final int MAX_RANGE = 1<<EXPONENT_OF_BASE2;
	
	private int begin;
	private int end;
	
	public static final int CMP_LESS_THAN= -1;
	public static final int CMP_EQUAL_TO= 0;
	public static final int CMP_GREAT_THAN = 1;
	public static final int CMP_CONTAINED_BY = 2;
	public static final int CMP_CONTAINS = 3;
	public static final int CMP_WILD = 4;
	
	
	/*
	 *	stepSize = 2^rand_base where rand_base = 0~10;
	 *  walk = 2^(max_exponent - rand_base);
	 */
	public Key()
	{
		Random rd = new Random();
		
		
		
		
		int stepSize, rand_base, walk;
		int randA, randB;
		
		rand_base = rd.nextInt(EXPONENT_OF_BASE2+1); //0~ EXPONENT_OF_BASE2
		
		stepSize = 1<<rand_base;
		walk = 1 <<(EXPONENT_OF_BASE2 - rand_base);//walk 1~ 1024 steps
		
		randA = (rd.nextInt(walk))*stepSize; //<=1024
		randB = randA + stepSize;
		
		
		
		this.begin = randA;
		this.end = randB;
		
		
	}
	
	public Key(int begin, int end)
	{
		this.begin = begin;
		this.end = end;
	}
	
	public void display()
	{
		System.out.println("Key.begin="+begin+",Key.end="+end);
	}
	
	public String toString()
	{
		String value = "("+begin+","+end+")";
		return value;
	}
	
	/*
	 * Any Two Key k1 and k2 must satisfy the following conditions:
	 * 1)	k1.begin < k1.end; k2.begin < k2.end;
	 * 2)	k1 != k2 if k1.begin != k2.begin or k1.end != k2.end;
	 * 3)	k1 < k2 if k1.end <= k2.begin ( e.g. (5,10) < (10,15));
	 * 4)	k1 ∩ k2 == k1 or k2 or NULL (e.g. (5,15) and (10,20) will never exist in the same multiway tree);
	 * 5)	k1 ⊂ k2 if k1 ∩ k2 == k1 && k1 != k2 (e.g. (5,10) ⊂ (5,20));
	 */
	
	public boolean isValid()
	{
		return this.begin<this.end;
	}
	
	public int compare2(Key k)
	{
		if(!this.isValid()||!k.isValid())
		{
			return CMP_WILD;
		}
		
		if( this.end<=k.begin )
		{
			return CMP_LESS_THAN;
		}
		else if(this.begin>=k.end)
		{
			return CMP_GREAT_THAN;
		}
		else if(this.begin==k.begin && this.end == k.end){
			
			return CMP_EQUAL_TO;
		}
		
		else if(this.begin>=k.begin && this.end <= k.end && !(this.begin==k.begin && this.end == k.end)){
			return CMP_CONTAINED_BY;
			
		}
		else if(this.begin<=k.begin && this.end >= k.end && !(this.begin==k.begin && this.end == k.end)){
			return CMP_CONTAINS;
		}
		else{
			return CMP_WILD;
		}
	}
}