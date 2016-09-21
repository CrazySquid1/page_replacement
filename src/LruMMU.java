/**
* MMU using least recently used replacement strategy
* Need to keep track of reference order 
*/

public class LruMMU implements MMU {

    
    public LruMMU(int frames) {
	 // each page has a resident and a modified bit. 
        //to do
    }
    
    public void readMemory(int page_number) {
        //this method updates the reference status of a resident page
    }
    
    public void writeMemory(int page_number) {
        //this method updates the reference status of a resident page and
	// records it has been modified
    
    }

    public int checkInMemory(int page_number) {
        // check if a page is resident
	// returns its location (frame number) or -1 if not resident
        return -1;
    }
    
    public void allocateFrame(int page_number) {
        // it allocate a page into a free frame
	// to do
    }
    
    
    public int selectVictim(int page_number) {
        //it select the victim, allocates the new page into the selected frame 
	//and returns the number of the page replaced
        return 1;
    }
    
    public boolean  lastVictimStatus( ) {
	// it returns true if the last victim was a modified page
	// false otherwise
        //todo
        return false;
    }
}
