package search_algos;

public class CreateObject{     // for creating objects 

    public Search getAlgo(String className){
      
      try {
      Class theClass = Class.forName(className);
      Search object = (Search)theClass.newInstance();
      return object;
      }
      
      catch (ClassNotFoundException ex){
         System.err.println(ex + " Search class must be in class path.");   // throws exception
      }
      
      catch(InstantiationException ex){
         System.err.println(ex + " Search class must be concrete.");
      }
      
      catch(IllegalAccessException ex){
         System.err.println(ex + " Search class must have a no-arg constructor.");
      }
    
      return null;
    }
}

