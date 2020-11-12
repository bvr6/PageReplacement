import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Arrays;
public class vmsim {
    static int totalMemoryAccesses = 0; // total memory accesses
    static int totalPageFaults = 0; // total page faults
    static int totalWritesToDisk = 0; // total writes to disk
    static int totalInstructions = 0; // total instructions in trace file
    static Hashtable<String, LinkedList<Integer>> hTable = new Hashtable<String, LinkedList<Integer>>(); // hashtable to store the locations of the instructions for each address
    static ArrayList<String> type = new ArrayList<String>(); // array to store the type of each instruction in the trace file
    static ArrayList<String> instructionListArray = new ArrayList<String>(); // array to store each instruction in the trace file
    public static void main(String[] args){
        int numframes = Integer.parseInt(args[1]);
        String algorithmName = args[3];
        String fileName = args[4];
        try {
            File f = new File (fileName);
            Scanner reader = new Scanner(f);
            while (reader.hasNextLine()){
                String s = reader.nextLine();
                type.add(s.substring(0, 1)); // type of instruction (add to array)

                s = s.substring(4, 9); // address
                if (hTable.containsKey(s)){ // if the hashtable contains the address, add the instruction number, if not create new key
                    LinkedList<Integer> l = hTable.get(s);
                    l.add(totalInstructions);
                }
                else {
                    LinkedList<Integer> l = new LinkedList<Integer>();
                    l.add(totalInstructions);
                    hTable.put(s, l);
                }
                instructionListArray.add(s); // instruction address (add to array)
                totalInstructions++;
            }
            reader.close();
            // System.out.println("Added table...");
        }
        catch (FileNotFoundException e){
            System.out.println("File not found!");
        }
        if (algorithmName.equals("opt")){
            opt(numframes, fileName);
        }
        else if (algorithmName.equals("lru")){
            lru(numframes, fileName);
        }
        else if (algorithmName.equals("second")){
            second(numframes, fileName);
        }
        System.out.println("Algorithm: " + algorithmName.toUpperCase());
        System.out.println("Number of frames: " + numframes);
        System.out.println("Total memory accesses: " + totalMemoryAccesses);
        System.out.println("Total page faults: " + totalPageFaults);
        System.out.println("Total writes to disk: " + totalWritesToDisk);
    }
    // OPTIMAL ALGORITHM
    public static void opt(int numframes, String fileName){
        Page[] table = new Page[numframes];
        for(int i = 0; i < totalInstructions; i++){
            String s = (String) instructionListArray.get(i);
            Page p = new Page(s);
            boolean found = false;
            if (type.get(i).equals("s")){
                p.dirtyBit = 1;
            }
            for (int j = 0; j < table.length && !found; j++){
                if (table[j] == null){
                    table[j] = p;
                    totalPageFaults++;
                    found = true;
                }
                else if (table[j].address.equals(p.address)){
                    if (table[j].dirtyBit == 1){
                        p.dirtyBit = 1;
                    }
                    table[j] = p;
                    found = true;
                }
            }
            if (!found){
                totalPageFaults++;
                int index = 0;
                int highestValue = -1;
                boolean[] LRUArray = new boolean[table.length];
                boolean subFound = false; // if there are two empty linked lists found, indicating that these addresses do not appear in the future
                Arrays.fill(LRUArray, false);
                for (int j = 0; j < table.length; j++){
                    String address = table[j].address;
                    if (hTable.containsKey(address)){
                        LinkedList l = hTable.get(address);
                        if (!l.isEmpty() && (int) l.getFirst() > highestValue){
                            index = j;
                            highestValue = (int) l.getFirst();
                        }
                        else if (l.isEmpty()){
                            LRUArray[j] = true;
                            subFound = true;
                        }
                    }
                }
                if (subFound){
                    int lowestValue = Integer.MAX_VALUE;
                    for (int j = 0; j < LRUArray.length; j++){
                        if (LRUArray[j] && table[j].age < lowestValue){
                            lowestValue = table[j].age;
                            index = j;
                        }
                    }
                }
                if (table[index].dirtyBit == 1){
                    totalWritesToDisk++;
                }
                table[index] = p;
            }
            totalMemoryAccesses++;
            LinkedList l = hTable.get(s);
            // System.out.println(l.getFirst());
            l.removeFirst();
            /*
            for (int j = 0; j < table.length; j++){
                if (table[j] != null)
                    System.out.println(table[j].address);
                else
                    System.out.println("null");
            }
            System.out.println("---");
            */
            
            for (int j = 0; j < table.length; j++){
                if (table[j] != null){
                    table[j].age++;
                }
            }
        }
    }
    // LRU ALGORITHM
    public static void lru(int numframes, String fileName){
        Page[] table = new Page[numframes];
        for(int i = 0; i < totalInstructions; i++){
            String s = (String) instructionListArray.get(i);
            Page p = new Page(s);
            boolean found = false;
            if (type.get(i).equals("s")){
                p.dirtyBit = 1;
            }
            for (int j = 0; j < table.length && !found; j++){
                if (table[j] == null){
                    table[j] = p;
                    totalPageFaults++;
                    found = true;
                }
                else if (table[j].address.equals(p.address)){
                    if (table[j].dirtyBit == 1){
                        p.dirtyBit = 1;
                    }
                    table[j] = p;
                    found = true;
                }
            }
            // Eviction starts here
            if (!found){
                totalPageFaults++;
                int index = 0;
                int highestValue = -1;
                for (int j = 0; j < table.length; j++){
                    if (table[j].age > highestValue){
                        highestValue = table[j].age;
                        index = j;
                    }
                }
                if (table[index].dirtyBit == 1){
                    totalWritesToDisk++;
                }
                table[index] = p;   
            }
            // Eviction stops here
            totalMemoryAccesses++;
            
            for (int j = 0; j < table.length; j++){
                if (table[j] != null){
                    table[j].age++;
                }
            }
        }
    }
    // SECOND CHANCE ALGORITHM
    public static void second(int numframes, String fileName) {
        LinkedList<Page> table = new LinkedList<Page>(); // Use linked list instead of array (easier to implement)
        for(int i = 0; i < totalInstructions; i++){
            String s = (String) instructionListArray.get(i);
            Page p = new Page(s);
            boolean found = false;
            if (type.get(i).equals("s")){
                p.dirtyBit = 1;
            }
            for (int j = 0; j < numframes && !found; j++){
                if (table.size() < numframes){
                    for (int k = 0; k < table.size() && !found; k++){
                        if (table.get(k).address.equals(p.address)){
                            p.RBit = 1;
                            if (table.get(k).dirtyBit == 1){
                                p.dirtyBit = 1;
                            }
                            table.remove(table.get(k));
                            table.add(k, p);
                            found = true;
                        }
                    }
                    if (!found) {
                        table.addLast(p);
                        totalPageFaults++;
                        found = true;
                    }
                }
                if (table.get(j).address.equals(p.address)){
                    p.RBit = 1;
                    if (table.get(j).dirtyBit == 1){
                        p.dirtyBit = 1;
                    }
                    table.remove(table.get(j));
                    table.add(j, p);
                    found = true;
                }
            }
            // Eviction starts here
            if (!found){
                totalPageFaults++;
                while (table.peek().RBit == 1){
                    Page q = table.pop();
                    q.RBit = 0;
                    table.addLast(q);
                }
                Page q = table.pop();
                if (q.dirtyBit == 1){
                    totalWritesToDisk++;
                }
                table.addLast(p);
            }

            // Eviction stops here
            totalMemoryAccesses++;
            /*
            if (totalInstructions == totalMemoryAccesses)
                System.out.println(table);
            */
        }
    }
}