
public class Page {
	public int pageNo = 0; //the page number
	//public int frameNo; //probably not needed
	public boolean modifyBit = false;
	public boolean referenceBit = true;
	
	
	//constructor
	public Page(int mPageNo ){
		this.pageNo = mPageNo;
    	this.modifyBit = false;
    	this.referenceBit = false;
	}
}
