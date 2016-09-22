import java.util.Vector;

/**
* MMU using enchanced second chance replacement strategy
* Page replacement based on the R and M bits
*/

public class EscMMU implements MMU {
	private Vector<Page> pageQueue;
    private int frameWindowCapacity = 0;
    private Page lastVictim = null;
    private int curserPos = 0;

    //constructor
    public EscMMU(int frames) {
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
    	
    	//add to back of queue
    	pageQueue.add(newPage);
    }
    
    //purpose: despite the shit name. swaps a frame from the page queue with the page "page_number"
    //inputs: page_number = the new page that needs to be inserted into the frame window.
    //return: the page number that was replaced to make room for the new page.
    public int selectVictim(int page_number) {
    	int victimIndex = VictimHunter();
    	
    	lastVictim = pageQueue.remove(victimIndex);
    	
    	//insert the new page onto the back of the frame queue
    	Page newPage = new Page(page_number);
    	pageQueue.add(newPage);
    	
        return lastVictim.pageNo;
    }
    
    //purpose: find the next victim when a page needs to be replaced
    //return: the index of the victim frame to swap
    private int VictimHunter() {
    	//the victim page is the first page from the lowest group
    	int victimPos;
    	victimPos = FindFirstVictim(0); //not recently used or modified
    	if(victimPos != -1) {
    		return victimPos;
    	}
    	victimPos = FindFirstVictim(0); //not recently used or modified
    	if(victimPos != -1) {
    		return victimPos;
    	}
    	//clear the reference frame on the second run
    	victimPos = FindFirstVictim(1); //not recently used but modified
    	if(victimPos != -1) {
    		return victimPos;
    	}
    	System.out.println("error: victim hunter failed.");
    	return curserPos;
    }
    
    //purpose: loops through the queue trying to find a victim that satisfies arguments
    //inputs: referenceBit = if false look for a victim that has not recently been modified
    //modifiedBit = if false look for a victim that has not be modified
    //note: if modified bit is high sets reference bit low for every page it passes.
    //starts search at cursor position
    private int FindFirstVictim(int modifiedBit) {
    	int startPos = this.curserPos;
    	//loop from current cursor position until back to starting cursor position
    	do {
    		Page potentialVictim = pageQueue.get(this.curserPos);
    		//the line below is very unreadable. I had to write it just for the lols.
    		if( Boolean.compare(potentialVictim.modifyBit, false) <= modifiedBit && potentialVictim.referenceBit == false ) {
    			//found the next victim;
    			return this.curserPos;
    		}
    		
    		//clear the reference bit
    		potentialVictim.referenceBit = false;
    		
    		//move to the next page
    		this.curserPos = (this.curserPos + 1) % this.frameWindowCapacity;
    	}while(this.curserPos != startPos);
    	//couldn't find a victim that meet the input criteria
    	return -1;
    }
    
    //purpose: tells simulator if last page was modified
    //returns: true if page was modified and false otherwise.
    public boolean  lastVictimStatus( ) {
    	return lastVictim.modifyBit;
    }
}
