import java.util.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.zip.*;
import java.util.zip.ZipEntry;
import java.io.*;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


class Check{
   
   public boolean spellCheck(String zipName, String word)
   {
      int flag =0;
      try{
      ZipFile zip = new ZipFile(zipName);
      Enumeration<? extends ZipEntry> entries = zip.entries();
      while(entries.hasMoreElements()){
         if(flag==1)
         {
            break;
         }
         ZipEntry entry = entries.nextElement();
         BufferedReader read = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
         String correctWord;
         while((correctWord=read.readLine()) != null)
         {
            if(word.equalsIgnoreCase(correctWord))
            {
               flag = 1;
               break;
            }
         }
         
         
      }
      
      zip.close();
      }
      
      
      catch (IOException e) {
            e.printStackTrace();
        } 
      if(flag ==0)
       return false;
      else
       return true;  
      
   }
   
}


public class test{
    public static void main(String[] args)
    {
        String line;
        int lineCount = 0;
        int wordCount = 0;
        int errCount = 0;
        try{
        File f = new File(args[1]);
        InputStream inputStream = new FileInputStream(f);
        InputStreamReader read = new InputStreamReader(inputStream);
        BufferedReader buffer = new BufferedReader(read);
        String zipName = args[0];
       Check c = new Check();
       boolean result;
       
        
           while((line = buffer.readLine()) != null)
           {
              int flag=0;
              lineCount++;
              String[] words = line.split("\\s+");
              for(int i=0;i<words.length;i++)
              {
               wordCount++;
               if((words[i].endsWith(".")) || (words[i].endsWith(",")) || (words[i].endsWith("!")) || (words[i].endsWith("?")) || (words[i].endsWith(";")))
               {
                 String s = words[i].substring(0,words[i].length()-1);
                 result = c.spellCheck(zipName,s);
                 if(result==false)
              {
                 errCount++;
                 if(flag==0)
                 {
                   flag=1;
                   System.out.print("\nLine #" + lineCount + ": " + s);
                 }
                 else
                   System.out.print(", " + s);
              }
               }
               else{
                result = c.spellCheck(zipName,words[i]);
                 if(result==false)
                {
                 errCount++;
                 if(flag==0)
                 {
                   flag=1;
                   System.out.print("\nLine #" + lineCount + ": " + words[i]);
                 }
                 else
                   System.out.print(", " + words[i]);
                }
               }
              }
              
              
           }
       if(errCount != 0){
       System.out.print("\nChecked "+ wordCount + " words in " + lineCount + " lines. Dictionary used: " + args[0] + "\n" + errCount + " spelling errors found.\n" ); }
       
       else
       System.out.print("\nNo spelling errors found in the file.\n");     
       
       
       }
       catch (IOException e) {
            e.printStackTrace();
        } 
        
    }
}
