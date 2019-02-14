import search_algos.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String s;
        Scanner in = new Scanner(System.in);
        System.out.println("This program helps in searching an element in a given array.");
        System.out.println("You have two algorithms for doing the same task.");
        System.out.println("Enter 1 for Linear Search and 2 for Binary Search");
        int option= in.nextInt();
        if(option==1)
           s = "search_algos.LinearSearch";
        else
           s = "search_algos.BinarySearch";
        
        CreateObject algos = new CreateObject();    // use CreateObject class to create objects of classes that implement Search
        Search algo= algos.getAlgo(s);
        System.out.println("Enter the number of elements in array");
        int n= in.nextInt();
        int Arr[] = new int[n];
        System.out.println("Enter elements");
        for(int i=0;i<n;i++)
           Arr[i]= in.nextInt();
        System.out.println("Enter the element you want to search");
        int key =  in.nextInt();
        int pos = algo.search(Arr, key);       // search is a method in the interface Search
        pos++;
        if(pos == -1)
          System.out.println("Element not found");
        else
        {
          if(option==1)
            System.out.println("The position of the given element is : "+ pos);
          else
            System.out.println("The position of the given element in the sorted array is : "+ pos);
        }
}

}
