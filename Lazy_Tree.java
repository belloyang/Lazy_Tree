import java.util.concurrent.locks.ReentrantLock;
public class Lazy_Tree{
	public Tree_Entry root;
	public ReentrantLock lock;
	
	public boolean validate(Tree_Entry pred, Tree_Entry curr,boolean isLeftChild)
	{
		if(curr!=null && curr.isMarked())
		{
			return false;
		}
		else
		{
			if(pred==null)
			{

				return (this.root == curr);

			}
			else{//Traverse the childList or the siblingList(PMI->next)
				if(pred.isMarked())
				{
					return false;
				}
				
				if(isLeftChild)
				{
					return (pred.childList == curr);
				}
				else{
					return (pred.next == curr);
				}
			}
			
		}
	}
	
	public Lazy_Tree()
	{
		root = null;
		lock = new ReentrantLock();
	}
	public void display(){
		Tree_Entry p = this.root;
		
		System.out.println(">>>>>>>>>>>>>>>>Displaying BEGIN<<<<<<<<<<<<<");
		int nNodes = 0;
		while(p!=null)
		{
			System.out.print("[k:"+p.getKey().toString()+",M:"+p.isMarked()+"]->");
			nNodes++;
			p = p.childList;
		}
		System.out.println("null");
		
		System.out.println(">>>>>>>>>>>>>>>>Displaying End(nNodes:"+nNodes+"<<<<<<<<<<<<<");
	}
	
	public boolean Insert(Key k)
	{
		Tree_Entry pred,curr;
		
		
		boolean isLeftChild, insertParent;
		
		while(true){
			
		
			Tree_Entry rcSearchFrom = null;
			
			pred = null;
			curr = this.root;
			boolean searchDone = false;
			isLeftChild = insertParent = false;
			while(curr!=null &&!searchDone)
			{
				
				switch(k.compare2(curr.getKey()))
				{
					case Key.CMP_LESS_THAN:{//case1
						rcSearchFrom = null;
						searchDone = true;
					}break;
					case Key.CMP_EQUAL_TO:{//case5
						rcSearchFrom = curr;
						searchDone = true;
					}break;
					case Key.CMP_GREAT_THAN:{//case2
						pred = curr;
						curr = curr.next;
						isLeftChild = false;
						continue;
					}
					case Key.CMP_CONTAINED_BY:{//case3
						pred = curr;
						curr = curr.childList;
						isLeftChild = true;
						continue;
					}
					case Key.CMP_CONTAINS:{//case4
						insertParent = true;
						rcSearchFrom =  null;
						searchDone = true;
					}break;
					case Key.CMP_WILD:{
						System.out.println("INS/SearchFrom: Invalid Key Comparison:"+k.toString()+" cmp2 curr.key"+curr.getKey().toString());
						searchDone = true;
					}break;
					
				}
			}
			
			if(rcSearchFrom!=null&& !rcSearchFrom.isMarked())
			{
				return false;
			}
			
			
				if(pred == null)
				{
					this.lock.lock();
				}
				else{
					pred.lock.lock();
				}
				if(curr!=null)
				{
					curr.lock.lock();
				}
				
				if(!validate(pred,curr,isLeftChild))
				{
					if(curr!=null){
						curr.lock.unlock();
					}
					if(pred==null)
					{
						this.lock.unlock();
					}
					else{
						pred.lock.unlock();
					}
					continue;
				}
				
				
				
				if(insertParent)//complex insertion
				{
					if(curr == null || k.compare2(curr.getKey())!=Key.CMP_CONTAINS)
					{
						if(curr!=null){
							curr.lock.unlock();
						}
						if(pred==null)
						{
							this.lock.unlock();
						}
						else{
							pred.lock.unlock();
						}
						continue;
					}
					Tree_Entry target = new Tree_Entry(k);
					target.childList = curr;
					Tree_Entry lastChild = curr;
					
					//lastChild.lock.lock();// curr has been locked
					while(lastChild.next!=null && lastChild.next.getKey().compare2(k)==Key.CMP_CONTAINED_BY)
					{
						lastChild.parent = target;
						Tree_Entry next = lastChild.next;
						next.lock.lock();
						lastChild.lock.unlock();
						lastChild = next;					
					}
					lastChild.parent = target;
					target.next = lastChild.next;
					
					lastChild.next = null;
					lastChild.lock.unlock();
					
					if(pred == null)
					{
						this.root = target;
						this.lock.unlock();
					}
					else{
						if(isLeftChild)
						{
							target.parent = pred;
							pred.childList = target;
						}
						else{
							target.parent = pred.parent;
							pred.next = target;
						}
						pred.lock.unlock();
					}
				}else{//Simple Insertion
					
					if(curr == null)
					{
						if(pred !=null && pred.parent!=null&&k.compare2(pred.parent.getKey())==Key.CMP_GREAT_THAN)
						{
							
							pred.lock.unlock();
												
							continue;
						}
					}
					else{
						if(k.compare2(curr.getKey())!=Key.CMP_LESS_THAN)
						{
							if(pred==null)
							{
								this.lock.unlock();
							}
							else{
								pred.lock.unlock();
							}
							continue;
						}
					}
					
					Tree_Entry target = new Tree_Entry(k);
					if(pred!=null)
					{
						if(isLeftChild)
						{
							target.parent = pred;
						}
						else{
							target.parent = pred.parent;
						}
					}
					target.next = curr;
					
					if(curr!=null){
						curr.lock.unlock();
					}
					
					if(pred==null)
					{
						this.root = target;
						this.lock.unlock();
					}
					else{
						if(isLeftChild)
						{
							pred.childList = target;
						}
						else{
							pred.next = target;
						}
						pred.lock.unlock();
					}
				}
			
			
			return true;
		}//while
	}
	
	public boolean Delete(Key k)
	{
		Tree_Entry pred,curr;
		boolean isLeftChild, insertParent;
		
		while(true){
			
		
			Tree_Entry rcSearchFrom = null;
			
			pred = null;
			curr = this.root;
			
			boolean searchDone = false;
			isLeftChild = insertParent = false;
			while(curr!=null &&!searchDone)
			{
				
				switch(k.compare2(curr.getKey()))
				{
					case Key.CMP_LESS_THAN:{//case1
						rcSearchFrom = null;
						searchDone = true;
					}break;
					case Key.CMP_EQUAL_TO:{//case5
						rcSearchFrom = curr;
						searchDone = true;
					}break;
					case Key.CMP_GREAT_THAN:{//case2
						pred = curr;
						curr = curr.next;
						isLeftChild = false;
						continue;
					}
					case Key.CMP_CONTAINED_BY:{//case3
						pred = curr;
						curr = curr.childList;
						isLeftChild = true;
						continue;
					}
					case Key.CMP_CONTAINS:{//case4
						insertParent = true;
						rcSearchFrom =  null;
						searchDone = true;
					}break;
					case Key.CMP_WILD:{
						System.out.println("DEL/SearchFrom: Invalid Key Comparison:"+k.toString()+" cmp2 curr.key"+curr.getKey().toString());
						searchDone = true;
					}break;
					
				}
			}
			
			if(rcSearchFrom == null || rcSearchFrom.isMarked())
			{
				return false;
			}
			
			if(pred ==null)
			{
				this.lock.lock();
			}
			else{
				
				pred.lock.lock();
						
			}
			curr.lock.lock();
			
			if(!validate(pred,curr,isLeftChild))
			{
				curr.lock.unlock();
				if(pred==null)
				{
					this.lock.unlock();
					
				}
				else{
					pred.lock.unlock();
				}
				continue;
			}
			
			Tree_Entry toBeUpdated;
			
			if(pred == null)
			{
				toBeUpdated = this.root;
			}
			else{
				if(isLeftChild)
				{
					toBeUpdated =pred.childList;
				}
				else{
					toBeUpdated = pred.next;
				}
			}
			
			curr.mark();
			

			if(curr.childList == null)
			{
				if(pred == null)
				{
					this.root = curr.next;
				}
				else{
					if(isLeftChild)
					{
						pred.childList = curr.next;
					}
					else{
						pred.next=curr.next;
					}
				}
			}
			else{
				Tree_Entry lastChild = curr.childList;
				lastChild.lock.lock();
				while(lastChild.next!=null && lastChild.next.getKey().compare2(k)==Key.CMP_CONTAINED_BY)
				{
					lastChild.parent = curr.parent;
					Tree_Entry next = lastChild.next;
					next.lock.lock();
					lastChild.lock.unlock();
					lastChild = next;
				}
				lastChild.parent = curr.parent;
				lastChild.next = curr.next;
				
				lastChild.lock.unlock();
				
				if(pred == null)
				{
					this.root = curr.childList;
				}
				else{
					if(isLeftChild)
					{
						pred.childList = curr.childList;
					}
					else{
						pred.next=curr.childList;
					}
				}
			}
			curr.lock.unlock();
			if(pred == null)
			{
				this.lock.unlock();
			}
			else{
				pred.lock.unlock();
			}
			return true;
			
		}//while
					
		
	}
	
	public boolean Search(Key k)
	{
		Tree_Entry pred = null;
		Tree_Entry curr = this.root;
		Tree_Entry target;
		boolean isLeftChild=false, insertParent=false;
		if((target = SearchFrom(k,pred,curr,isLeftChild,insertParent))!=null && !target.isMarked())
		{
			return true;
		}
		else{
			return false;
		}
		
	}
	
	private Tree_Entry SearchFrom(Key k, Tree_Entry pred,Tree_Entry curr,boolean isLeftChild,boolean insertParent)
	{
		isLeftChild = insertParent = false;
		while(curr!=null)
		{
			
			switch(k.compare2(curr.getKey()))
			{
			case Key.CMP_LESS_THAN:{//case1
				return null;
			}
			case Key.CMP_EQUAL_TO:{//case5
				return curr;
			}
			case Key.CMP_GREAT_THAN:{//case2
				pred = curr;
				curr = curr.next;
				isLeftChild = false;
			}break;
			case Key.CMP_CONTAINED_BY:{//case3
				pred = curr;
				curr = curr.childList;
				isLeftChild = true;
				continue;
			}
			case Key.CMP_CONTAINS:{//case4
				insertParent = true;
				return null;
			}
			case Key.CMP_WILD:{
				System.out.println("SearchFrom: Invalid Key Comparison:"+k.toString()+" cmp2 curr.key"+curr.getKey().toString());
			}break;
				
			}
		}
		
		return null;
	}
}