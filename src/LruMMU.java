import java.util.Vector;

/**
* MMU using least recently used replacement strategy
* Need to keep track of reference order 
*/

public class LruMMU implements MMU {
	
	private Vector<Page> pageQueue;
    private int frameWindowCapacity = 0;
    private Page lastVictim = null;
	
	//constructor
    public LruMMU(int frames) {
    	//make a vector of size "frames"
    	this.pageQueue = new Vector<Page>(frames);
    	this.frameWindowCapacity = frames;
    }
    
    //purpose: this method stores whenever a page is read from
    public void readMemory(int page_number) {
    	//record that the page has been read
    	int index = this.checkInMemory(page_number);
    	if(index == -1) {
    		System.out.println("failed to write to a page");
    		return;
    	}
    	Page readPage = pageQueue.get(index);
    	readPage.referenceBit = true;
    	MovePageToBottom(index);
    }
    
    //purpose: this method stores whenever a page is written to
    public void writeMemory(int page_number) {
    	//record that the page has been modified
    	int index = this.checkInMemory(page_number);
    	if(index == -1) {
    		System.out.println("failed to write to a page");
    		return;
    	}
    	Page writePage = pageQueue.get(index);
    	writePage.modifyBit = true;
    	writePage.referenceBit = true;
    	MovePageToBottom(index);
    }
    
    //purpose: moves the page to the bottom of the queue
    //input: the index of the page to move to the bottom
    private void MovePageToBottom(int pageIndex) {
    	Page tempPage = pageQueue.remove(pageIndex);
    	pageQueue.add(tempPage);
    }
    
    //purpose: check if the page being requested is already loaded in RAM
    //return: -1 if not currently in RAM. otherwise return the frame number
    public int checkInMemory(int page_number) {
    	//look in queue for page number
    	for(int i = 0; i < this.pageQueue.size(); ++i ) {
    		if(this.pageQueue.get(i).pageNo == page_number) {
    			return i;
    		}
    	}
    	//couldn't find page number
        return -1;
    }
    
    //purpose: write page to an avaliable frame
    //note: this method is only called if the simulation knows there is a free frame avaliable.
    public void allocateFrame(int page_number) {
    	//put at back of queue
    	Page newPage = new Page(page_number);
    	
    	//check size before inserting
    	if(this.pageQueue.size() >= frameWindowCapacity) {
    		//throw error
    		System.out.println("error: hit frame window capacity");
    		return;
    	}
    	pageQueue.add(newPage);
    }
    
    //purpose: despite the shit name. swaps a frame from the page queue with the page "page_number"
    //inputs: page_number = the new page that needs to be inserted into the frame window.
    //return: the page number that was replaced to make room for the new page.
    public int selectVictim(int page_number) {
    	//the victim page is the first page on the queue
    	lastVictim = pageQueue.remove(0);
    	
    	//insert the new page onto the frame queue
    	Page newPage = new Page(page_number);
    	pageQueue.add(newPage);
    	
        return lastVictim.pageNo;
    }
    
    //purpose: tells simulator if last page was modified
    //returns: true if page was modified and false otherwise.
    public boolean  lastVictimStatus( ) {
    	return lastVictim.modifyBit;
    }
}
