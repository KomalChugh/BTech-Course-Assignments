package search_algos;
import java.util.*;

public class BinarySearch implements Search {
 
    public int search(int[] Arr, int key) {    // sorts the array and returns the position of element in the sorted array
         
        System.out.println("Implementing Binary Search"); 
        Arrays.sort(Arr);
        int start = 0;
        int end = Arr.length - 1;
        while (start <= end) {
            int mid = (start + end) / 2;
            if (key == Arr[mid]) {
                return mid;
            }
            else if (key < Arr[mid]) {
                end = mid - 1;
            } 
            else {
                start = mid + 1;
            }
        }
        return -1;
    }
} 





