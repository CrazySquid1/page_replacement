import java.io.BufferedReader;
import java.io.FileReader;

/*
run program like this:
javac *.java
java Memsim ../testData/trace1 4 lru debug
*/

public class Memsim {
	private static int page_offset = 12; // page is 2^12 = 4096 bytes
	private static int frames = 4; //frame window
	private static boolean debug = false;
	private static int numEvents = 0; //number of events
	private static int numDiskReads = 0;
	private static int numDiskWrites = 0;

    private static BufferedReader inputFile = null;
    private static MMU mmu = null;
	
	
	//entry point
    public static void main(String[] args) {
		ReadParameters(args);
		DoSimulate();
		DisplayResults();
    }
   
   
   	private static void ReadParameters(String[] args) {
		/* read parameters */
        //the file
        try {
           inputFile = new BufferedReader(new FileReader(args[0]));
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("file '" + args[0] + "' could not be found");
            System.out.println("Usage: java Memsim file numberframes replacementmode debugmode");
            System.exit(-1);
        }
        
        //number of frames
        frames = Integer.parseInt(args[1]);
		if (frames < 1) {
            System.out.println("Frame number must be at least 1");
            System.exit(-1);
        }
        
        //the replacement mode
        if (args[2].equals("rand"))
            mmu = new RandMMU(frames);
        else if (args[2].equals("lru"))
            mmu = new LruMMU(frames);
        else if (args[2].equals("esc"))
            mmu = new EscMMU(frames);
        else {
            System.out.println("Usage: java Memsim file numberframes replacementmode debugmode");
            System.out.println("replacementmodes are [ rand | lru | esc ]");
            System.exit(-1);
        }
        
        //debug mode?
        if (args[3].equals("debug"))
            debug = true;
        else if (args[3].equals("quiet"))
            debug = false;
        else {
            System.out.println("Usage: java Memsim file numberframes replacementmode debugmode");
            System.out.println("debugmode are [ debug | quiet ]");
            System.exit(-1);
        }
	}
   	
   	private static void DoSimulate() {
		int numAllocated = 0; //the number of pages currently on the frameWindow
		try {
			String traceLine = inputFile.readLine();
		
			//for every job
			while (traceLine != null) {
				//parse line
				String[] traceCmd = traceLine.split(" ");
                
				//convert from hexadecimal address from file, to appropriate page number
				long logical_address = Long.parseLong(traceCmd[0],16);
				//work out the reference number by deviding by the page size
				int page_number = (int) (logical_address >>> page_offset);
				//check if frame is in memory
				int frame_number = mmu.checkInMemory(page_number);
            
				//frame not in RAM
				if ( frame_number == -1) {
					// page fault 
					numDiskReads++;
					if (debug) { 
						System.out.println("Page fault   " + page_number);
					}
            	
					if (numAllocated < frames ) {
						//there is space on frame window
						mmu.allocateFrame(page_number);
						numAllocated++;
					} else { 
						//need to replace a frame
						int victim_page = mmu.selectVictim(page_number);
						boolean is_modified = mmu.lastVictimStatus( );
						if (is_modified){
							numDiskWrites++;
							if (debug) {
								System.out.println("Disk write  " + victim_page);
							}
            		
						} else {
							if (debug) {
								System.out.println("Discard      " + victim_page);
							}	
						}
					}
				}

				//process read or write
				if (traceCmd[1].equals("R")){
					mmu.readMemory(page_number);
					if (debug) {
						System.out.println("reading      " + page_number);
					}	
				} else if (traceCmd[1].equals("W")){
					mmu.writeMemory(page_number);
					if (debug) {
						System.out.println("writting     " + page_number);
					}	
				} else {
						System.out.println("Badly formatted file. Error on line " + (numEvents+1));
						System.exit(-1);
				}
            
				//move to the next page
				numEvents++;
				traceLine = inputFile.readLine();
			} //end of loop
        
		} catch (java.io.IOException e) {
			System.out.println("Error reading inputFile file");
			System.exit(-1);
		} catch (NumberFormatException e) {
			System.out.println("Memory address strange on line " + (numEvents+1));
			System.exit(-1);
		}
   	}
   	
   	private static void DisplayResults() {
   	    System.out.println("total memory frames:  " + frames);
   	    System.out.println("events in trace:      " + numEvents);
   	    System.out.println("total disk reads:     " + numDiskReads );
   	    System.out.println("total disk writes:    " + numDiskWrites);
   		java.text.DecimalFormat f = new java.text.DecimalFormat("0.0000");
   		System.out.println("page fault rate: " + f.format(((double)numDiskReads)/numEvents));

   	}

}
