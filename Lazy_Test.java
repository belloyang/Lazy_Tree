import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Lazy_Test{
	public static final int EXPONENT_OF_BASE2 = 10;
	public static final int MAX_RANGE = 1<<EXPONENT_OF_BASE2;
	
	public static int nThread = 1;
	public static int nSearch;
	public static int nInsert;
	public static int nDelete;
	public static int nTask = MAX_RANGE;
	public static int mode;
	
	public static Lazy_Tree tree;
	
	
	
	private static class UpdateRoutine implements Runnable
	{
		//public static Lazy_Tree lcrs_tree;
		
		public int fromTaskID;
		public int toTaskID;
		
		
		public UpdateRoutine(int from, int to)
		{
			
			fromTaskID = from;
			toTaskID = to;
			
		}

		
		public void run() {
			// TODO Auto-generated method stub
			Key key;
			boolean rc;
			int amount = toTaskID - fromTaskID +1;
			System.out.println("Thread:"+Thread.currentThread().getId()+" is assigned "+ amount+ " tasks from task:"+fromTaskID+" to task:"+ toTaskID);
			int succSearch, succInsert,succDelete;
			succSearch = succInsert = succDelete = 0;
			for(int i=fromTaskID; i<= toTaskID; i++)
			{
				key = new Key();
				
				if(i<nSearch)
				{
					rc = tree.Search(key);
					if(rc)succSearch++;
					//System.out.println("[TID:"+Thread.currentThread().getId()+"]{taskID:"+i+"}"+"Search(key:"+key.toString()+")returns "+rc);
				}
				else if(i>=nSearch&& i<(nSearch+nInsert))
				{
					rc = tree.Insert(key);
					if(rc)succInsert++;
					//System.out.println("[TID:"+Thread.currentThread().getId()+"]{taskID:"+i+"}"+"Insert(key:"+key.toString()+")returns "+rc);
				}
				else{
					rc = tree.Delete(key);
					if(rc)succDelete++;
					//System.out.println("[TID:"+Thread.currentThread().getId()+"]{taskID:"+i+"}"+"Delete(key:"+key.toString()+")returns "+rc);
				}
			}
			System.out.println("[TID:"+Thread.currentThread().getId()+"] successfully executes Search:"+succSearch+",Insert:"+succInsert+",Delete:"+succDelete +" times");
		}
		
		
	}
	
	public static void main(String[] args)
	{
		
		if(args.length>=1)
		{
			Lazy_Test.nThread = Integer.parseInt(args[0]);
		}
		
		if(args.length>=2)
		{
			Lazy_Test.mode = Integer.parseInt(args[1]);
		}
		
		System.out.println("Benchmark Setting for Lazy_Tree Algorithm:\n"+nThread+" Threads are runing in mode:"+
				mode);
		switch(mode)
		{
			case 0:{
				nSearch = nTask/2;
				nInsert = nTask/4;
				nDelete = nTask - nInsert - nSearch;
				System.out.println("Mode 0: nTask("+nTask+"). Operation Distribution:\nSearch("+nSearch+"):\t50%\nInsert("+nInsert+"):\t25%\nDelete("+nDelete+"):\t25%");
			}break;
			case 1:{
				nSearch = nTask*3/4;
				nInsert = nTask/8;
				nDelete = nTask - nInsert - nSearch;
				System.out.println("Mode 0: nTask("+nTask+"). Operation Distribution:\nSearch("+nSearch+"):\t75%\nInsert("+nInsert+"):\t12.5%\nDelete("+nDelete+"):\t12.5%");
			}break;
			
			case 2:{
				nSearch = nTask/4;
				nInsert = nTask/2;
				nDelete = nTask - nInsert - nSearch;
				System.out.println("Mode 0: nTask("+nTask+"). Operation Distribution:\nSearch("+nSearch+"):\t25%\nInsert("+nInsert+"):\t50%\nDelete("+nDelete+"):\t25%");
			}break;
			
			case 3:{
				nSearch = 0;
				nInsert = nTask/2;
				nDelete = nTask - nInsert - nSearch;
				System.out.println("Mode 0: nTask("+nTask+"). Operation Distribution:\nSearch("+nSearch+"):\t0%\nInsert("+nInsert+"):\t50%\nDelete("+nDelete+"):\t50%");
			}break;
			
			case 4:{//insert only
				nSearch = 0;
				nInsert = nTask;
				nDelete = 0;
				System.out.println("Mode 0: nTask("+nTask+"). Operation Distribution:\nSearch("+nSearch+"):\t0%\nInsert("+nInsert+"):\t100%\nDelete("+nDelete+"):\t0%");
			}break;
			
			case 5:{//delete only
				nSearch = 0;
				nInsert = 0;
				nDelete = nTask;
				System.out.println("Mode 0: nTask("+nTask+"). Operation Distribution:\nSearch("+nSearch+"):\t0%\nInsert("+nInsert+"):\t0%\nDelete("+nDelete+"):\t100%");
			}break;
			
			case 6:{//search only
				nSearch = nTask;
				nInsert = 0;
				nDelete = 0;
				System.out.println("Mode 0: nTask("+nTask+"). Operation Distribution:\nSearch("+nSearch+"):\t100%\nInsert("+nInsert+"):\t0%\nDelete("+nDelete+"):\t0%");
			}break;
			
			default:{
				System.out.println("Invalid Benchmark Mode:"+mode);
				
			}
		}//switch
		
		
		tree = new Lazy_Tree();
		/* INITILIZATION:
		 * Randomly select N pairs of value {a,b} from {0,MAX_RANGE} (where a<b) 
		 * to initialize the tree structure
		 */
		
		
		int count = 0;
		while (count <(MAX_RANGE/4))
		{
			Key k = new Key();
			if(tree.Insert(k))
			{
				count++;
			}
			else{
	
				//System.out.println("Initial insertion return failure!");
	
			}
			
			
			
		}
		
		System.out.println("Lazy_Tree is initilized! "+count+" nodes are inserted in the tree!");
		
		tree.display();
		
		int assignedTask = 0;
		int from, to;
		int avgTask = nTask/nThread;
		int leftTask = nTask - avgTask*nThread;
		Thread[] threads = new Thread[nThread];
		
		
		long startTime = System.nanoTime();
		for(int i=0;i<nThread;i++)
		{
			 from = assignedTask;
			 to = from + avgTask -1;
			 if(leftTask>0){
				 to += 1;				 
			 }
			 
			 assignedTask = to + 1;
			 
			 threads[i] = new Thread(new UpdateRoutine(from, to));
			 threads[i].start();
		}
		
		for(int j=0;j<nThread;j++)
		{
			try {
				threads[j].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long timeDiff = System.nanoTime() - startTime;
		
		double runtime = timeDiff/1000000.0;//ms
		double throughput = nTask/runtime;
		System.out.println("Experiment is done (nThread:"+nThread+",nTask:"+nTask+",mode:"+mode+")");
		System.out.println("Execution time(in millisecond): "+ runtime);
		System.out.println("Throughput(op per millisecond):"+ throughput);
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("Lazy_Test."+args[0]+".out", true)))) {
			//Print the average_time (in microsecond) and throughput(per millisecond)
			out.println(runtime + "\t"+throughput);
		}catch (IOException e) {
			System.out.println(e.toString());
		}
		
		
	}
}