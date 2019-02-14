import java.io.*;
import java.util.jar.*;
import java.util.*;
import java.util.zip.*;
import java.util.Map.Entry;
import java.net.HttpURLConnection;
import java.net.URL;

class Download{    // class which downloads jar file locally
        private static final int BUFFER_SIZE = 4096;
        
        public static String downloadFile(String fileURL, String path) throws IOException{
           URL url = new URL(fileURL);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           
           String fileName = fileURL.substring(fileURL.lastIndexOf("/")+1, fileURL.length());
           InputStream inputStream = conn.getInputStream();
           String saveFilePath = path + File.separator + fileName;
           FileOutputStream outputStream = new FileOutputStream(saveFilePath);
           
           int bytesRead = -1;
           byte[] buffer = new byte[BUFFER_SIZE];
           while((bytesRead = inputStream.read(buffer)) != -1){
             outputStream.write(buffer,0,bytesRead);
           }
           
           outputStream.close();
           inputStream.close();
           conn.disconnect();
           return fileName;
        }
}

public class JarFileInfo{    // Main class
        public static void main(String[] args){
           Scanner scan = new Scanner(System.in);
           
           String fileURL = scan.nextLine();
           String path = ".";
           try{
           String JarFileName = Download.downloadFile(fileURL, path);
           
           String des_jar_file = "./" + JarFileName; 
           GetClassesFromJar names = new GetClassesFromJar();
           List<String> classes = names.getClasses(des_jar_file);
           int[][] count = new int[classes.size()][2];
           ClassStructure cs = new ClassStructure();
           System.out.println("The given jar file contains the following classes in it:\n");
           for(int i=0;i<classes.size();i++)
           {
              System.out.println(classes.get(i));
              
           } 
           Map<String,Integer> map= new HashMap<String,Integer>();
           System.out.println("\n\n\n");
           System.out.println("The distribution of JVM instructions in the descending order (top 50) is as follows:\n");
           System.out.format("%-10s%-20s%-20s\n","S.No.","JVM Instruction","Frequency");
           for(int i=0;i<classes.size();i++)
           {
              count[i] = cs.javap(classes.get(i),classes.size(),i,map);
             
           } 
            System.out.println("\n\n\n\n\n\n");
           int[] poolCount = new int[classes.size()];
           int sumPool =0;
           int sumMethod =0;
           
           for(int i=0;i<classes.size();i++){
              poolCount[i] = count[i][0];
              sumPool += count[i][0];
           }
           
           int averagePool = sumPool / classes.size();
           
           int[] methodCount = new int[classes.size()];
           
           for(int i=0;i<classes.size();i++){
              methodCount[i] = count[i][1];
              sumMethod += count[i][1];
           }
           int averageMethod = sumMethod / classes.size();
           
           Arrays.sort(poolCount);
           System.out.println("Information about the constant pool section is as follows:\n");
           System.out.format("%-20s%-3s%-10d\n","Maximum Value",":",poolCount[poolCount.length-1]);
           System.out.format("%-20s%-3s%-10d\n","Minimum Value",":",poolCount[0]);
           System.out.format("%-20s%-3s%-10d\n","Average Value",":",averagePool);
           
           double variance = 0;
           for(int i=0;i<classes.size();i++){
              variance += (poolCount[i]-averagePool) * (poolCount[i]-averagePool);
           }
           
           variance = variance / classes.size();
           int standardDeviation = (int)Math.sqrt(variance);
           System.out.format("%-20s%-3s%-10d\n","Standard deviation",":",standardDeviation);
           System.out.println("\n\n\n\n");
           System.out.println("Average number of methods in a class is: "+averageMethod);
           scan.close();      
           }
           catch (IOException ex) {
              ex.printStackTrace();
           }
        }
}

class GetClassesFromJar {    // class which returns list of classes in jar file
    
    public static List<String> getClasses(String JarFileName){
       
         List<String> classNames = new ArrayList<String>();
         try{
         
         File file = new File(JarFileName);
         JarFile jar = new JarFile(file);
         for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
              JarEntry entry = (JarEntry) enums.nextElement();
 
              String fileName = "."+ File.separator + entry.getName();
	      File f = new File(fileName);
 
	      if (fileName.endsWith("/")) {
		f.mkdirs();
	      }
 
	  }
	  
	  for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
	      JarEntry entry = (JarEntry) enums.nextElement();
 
	      String fileName = "." + File.separator + entry.getName();
	      File f = new File(fileName);
 
	      if (!fileName.endsWith("/")) {
	         if(fileName.endsWith(".class"))
	         {
	              classNames.add(fileName);
          
	         }
		 InputStream is = jar.getInputStream(entry);
		 FileOutputStream fos = new FileOutputStream(f);
 
		 // write contents of 'is' to 'fos'
		 while (is.available() > 0) {
		     fos.write(is.read());
	         }
 
		 fos.close();
		 is.close();
	      }
	   }
         
         }
         catch(IOException e){
           e.printStackTrace();
         }
         return classNames;
    }
}

class ClassStructure{  // class which invokes javap

      public static int[] javap(String s, int size, int index, Map<String,Integer> map){
          
          int count[] = new int[2];
          try{
             
             List<String> cmdList = new ArrayList<String>();
             cmdList.add("/usr/bin/javap");
             cmdList.add("-p");
             cmdList.add("-verbose");
             cmdList.add(s);
             
             ProcessBuilder pb = new ProcessBuilder(cmdList);
             Process p = pb.start();
             
             InputStream fis = p.getInputStream();
             
             count = DisplayClassStructure(fis,size,index,map);
         
          }
          
         catch(IOException e1){
             e1.printStackTrace();
         }
         return count;
      }
      
      // Display disassembled class
      
      private static int[] DisplayClassStructure(InputStream is,int size, int index,Map<String,Integer> map){
          
          InputStream stream;
          int constantPoolCount =0;
          int methodCount =0;
          try{
       
             BufferedReader reader = new BufferedReader(new InputStreamReader (is));
             String line;
             while((line = reader.readLine())!=null){
                String new_line = line.trim();
                if(new_line.equals("Constant pool:"))
                {
                   while((line = reader.readLine())!=null){
                     new_line = line.trim();
                     if(new_line.charAt(0) != '#')
                        break;
                     constantPoolCount++;
                   }
                   
                }
                if(new_line.equals("Code:"))
                {
                   methodCount++;
                   line = reader.readLine(); // read one extra line
                   while((line = reader.readLine())!=null){
                     new_line = line.trim();
                     if(new_line.equals(""))
                        break;
                     else if( ! Character.isDigit(new_line.charAt(0)))
                        break;
                     else
                     {
                       int k=0;
                       while(Character.isDigit(new_line.charAt(k)))
                         k++;
                       if(new_line.charAt(k) == ':' && new_line.charAt(k+1) == ' '){
                       
                     String jvmInstruction = new_line.split(" ")[1];
                     if(Character.isDigit(jvmInstruction.charAt(0)))
                      continue;
                     if(!map.containsKey(jvmInstruction))
                        map.put(jvmInstruction,1);
                     else
                        map.put(jvmInstruction,map.get(jvmInstruction)+1);
                     }
                     }
                   }
                }

             }
             reader.close();
             if(index == size-1){
             Set<Entry<String,Integer>> set = map.entrySet();
             List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(set);
             Collections.sort(list, new Comparator<Map.Entry<String,Integer>>()
             {
                public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2)
                {
                   return (o2.getValue()).compareTo(o1.getValue());
                }
             }  );
             
             int limit =1;
             for(Map.Entry<String,Integer> entry:list){
                if(limit>50)
                break;
                String line_no = limit + ")";
                System.out.format("%-10s%-20s%-10d\n",line_no,entry.getKey(),entry.getValue());
                limit++;
             }
             }
             

          }
          catch(FileNotFoundException e){
            e.printStackTrace();
          }
          catch(IOException e1){
            e1.printStackTrace();
          }
          return new int[] {constantPoolCount,methodCount};
      }
}
