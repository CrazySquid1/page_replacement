import java.util.Vector;
import java.util.Random;

/**
* MMU using random replacement strategy
* No need to keep reference record of pages
**/

public class RandMMU implements MMU {

	private Vector<Page> pageQueue;
    private int frameWindowCapacity = 0;
    private Page lastVictim = null;
	private Random rand = new Random();
    
    public RandMMU(int frames) {
	 // each page has a resident and a modified bit. 
        //to do
		this.pageQueue = new Vector<Page>(frames);
    	this.frameWindowCapacity = frames;
    }
    
    public void readMemory(int page_number) {
        //this method updates the reference status of a resident page
		int index = this.checkInMemory(page_number);
    	if(index == -1) {
    		System.out.println("failed to write to a page");
    		return;
    	}
    	Page readPage = pageQueue.get(index);
    	readPage.referenceBit = true;
    }
    
    public void writeMemory(int page_number) {
        //this method updates the reference status of a resident page and
		// records it has been modified
		int index = this.checkInMemory(page_number);
    	if(index == -1) {
    		System.out.println("failed to write to a page");
    		return;
    	}
    	Page writePage = pageQueue.get(index);
    	writePage.modifyBit = true;
    	writePage.referenceBit = true;
    }

    public int checkInMemory(int page_number) {
        // check if a page is resident
		// returns its location (frame number) or -1 if not resident
        for(int i = 0; i < this.pageQueue.size(); ++i ) {
    		if(this.pageQueue.get(i).pageNo == page_number) {
    			return i;
    		}
    	}
    	//couldn't find page number
        return -1;
    }
    
    public void allocateFrame(int page_number) {
        // it allocate a page into a free frame
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
    
    
    public int selectVictim(int page_number) {
		//chose a random page to remove
		int chosenPage = rand.nextInt(frameWindowCapacity)+1;
		lastVictim = pageQueue.remove(chosenPage);
    	
    	//insert the new page onto the frame queue
    	Page newPage = new Page(page_number);
    	pageQueue.add(newPage);
    	
        return lastVictim.pageNo;
    }
    
    public boolean  lastVictimStatus( ) {
	// it returns true if the last victim was a modified page
	// false otherwise
        return lastVictim.modifyBit;
    }
}
