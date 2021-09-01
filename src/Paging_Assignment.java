import java.util.*;

public class Paging_Assignment {
    // set up mainMemory, divided into 16 frames
    // each frame will contain if the frame is busy or free
    // 0 being free and 1 being busy
    // At the start all of the mainMemory is free
    private static int[] mainMemory = new int[16];

    //This is the queue that keeps track of what job has been "in use" the longest
    //    I am using this to track first in first out
    //    Jobs are added to queue once they are added to mainMemory frames
    private static Queue jobs = new PriorityQueue();

    //This is the PageMapTable to keep track of what job is in what mainMemory
    //   each spot will just have the job number that is assigned to the frame
    private static int[] pageMapTable = new int[16];


    public static void commandLine(){

        Scanner scanner = new Scanner(System.in);
        System.out.print("% ");
        String input = scanner.nextLine();

        while (!input.equals("exit")) {

            // this handles the input being print
            if(input.equals("print")){
                System.out.println("Main Memory      " + Arrays.toString(mainMemory));
                System.out.println("Page Map Table   " + Arrays.toString(pageMapTable));
                System.out.println("Jobs First In " + jobs.toString());
                commandLine();
                break;
            }

            // Try catch to get invalid commands. Otherwise an error will be thrown because
            // cannot split the input
            try{
                // getting each command by using split
                String[] command = input.split(" ");

                //make a switch statement here to catch the different command types
                // do what you need to to all parts and call separate functions

                //If the first command is a number then we are either adding
                // a new job using first in first out priority
                // or we are deleting a job from main memory.
                // else we need another command

                switch (Integer.parseInt(command[1])){
                    case 0:
                        //delete the job
                        removeJob(Integer.parseInt(command[0]));
                        break;
                    default:
                        //add the job
                        // if there is nowhere to add the job then use
                        // first in first out
                        addJob(command);
                        break;
                }

                //get next input
                // exit lets us leave the system
                System.out.print("% ");
                input = scanner.nextLine();
            }catch(Exception e){
                System.out.println("Invalid Command. Try Again.");
                System.out.print("% ");
                input = scanner.nextLine();
            }

        }
    }

    public static void addJob(String[] command){
        //Get the number of pages of the job that needs to be added to the memory frames
        int jobNumber = Integer.parseInt(command[0]);
        int jobSize = Integer.parseInt(command[1]);
        int jobPages = jobSize / 4096;
        if(jobSize % 4096 != 0){
            jobPages++;
        }

        //Now check the mainMemory to see if we have enough free frames to use
        int numberOfFreeFrames = 0;
        for(int i = 0; i < mainMemory.length; i++){
            if(mainMemory[i] == 0){
                numberOfFreeFrames++;
            }
        }
        //if there is space then add the pages to main memory and keep track of where they are placed
        // by using the pageMapTable
        if(numberOfFreeFrames >= jobPages){
            jobs.add(jobNumber);
            for(int i =0; i < jobPages; i++){
                for(int j = 0; j < mainMemory.length; j++){
                    if(mainMemory[j] == 0){
                        mainMemory[j] = 1;
                        pageMapTable[j] = jobNumber;
                        break;
                    }
                }
            }
        }else{
            //use the queue to figure out what job was added first
            Object dequeuedJob = jobs.remove();

            // I can't remove any added jobs from the queue early
            // It will just run through this with jobs that may not exist anymore.
            // So using array lists I check if the pageMapTable even contains the job we are
            // trying to remove with the first in first out replacement.

            // Need to convert the pageMapTable to IntegerList to use asList and contains methods
            int jobToRemove = Integer.parseInt(dequeuedJob.toString());
            Integer[] PMTInteger = new Integer[pageMapTable.length];
            for (int i = 0; i < pageMapTable.length; i++) {
                PMTInteger[i] = Integer.valueOf(pageMapTable[i]);
            }

            List pageMapTableList = Arrays.asList(PMTInteger);
            Boolean contains = pageMapTableList.contains(jobToRemove);
            // if that job still is in main memory then remove it otherwise try again
            //    -Note: Poor optimization b/c it tries to add the job again and not just dequeue
            //           the next item in the queue
            //           -Can be fixed by using a while loop and a boolean
            //              contains = false
            //              while contains is false
            //                  keep checking
            //              removeJob(jobToRemove)
            //              addJob(command)
            if(contains){
                removeJob(jobToRemove);
            }
            addJob(command);
        }
    }

    public static void removeJob(int jobToRemove){
        // goes through and sets mainMemory to free
        // changes the pageMapTable back to the default of 0
        for(int i =0; i < mainMemory.length; i++){
            if(pageMapTable[i] == jobToRemove){
                mainMemory[i] = 0;
                pageMapTable[i] = 0;
            }
        }
    }

    public static void main(String args[]){

        // Initializes the commandLine Menu
        commandLine();

    }
}
