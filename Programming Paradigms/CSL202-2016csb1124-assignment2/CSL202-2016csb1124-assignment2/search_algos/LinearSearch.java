package search_algos;

public class LinearSearch implements Search {
 
    public int search(int[] Arr, int key){     // does linear search
        
        System.out.println("Implementing Linear Search"); 
        int size = Arr.length;
        for(int i=0;i<size;i++){
            if(Arr[i] == key){
                return i;
            }
        }
        return -1;
    }
}

