import java.util.concurrent.locks.ReentrantLock;
public class Tree_Entry{
	private Key key;
	private boolean marked;
	public Tree_Entry next;
	public Tree_Entry childList;
	public Tree_Entry parent;
	
	public ReentrantLock lock;
	
	public Key getKey()
	{
		return this.key;
	}
	
	public void init(Key key)
	{
		this.key = key;
	}
	
	public boolean isMarked()
	{
		return marked;
	}
	public void mark()
	{
		this.marked = true;
	}
	public Tree_Entry(Key key)
	{
		this.key = key;
		this.marked = false;
		this.parent = null;
		this.childList = null;
		this.next = null;
		lock = new ReentrantLock();
	}
}