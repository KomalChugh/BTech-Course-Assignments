import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  
import java.time.*;
import java.util.Properties;    
import javax.mail.*;    
import javax.mail.internet.*; 

public class Violation{

        static int quota_window_minutes = 0;
        static int sustained_max_cpu_usage_duration_limit = 0;
        static int sustained_max_cpu_usage_limit = 0;
        static int sustained_max_memory_usage_duration_limit =0;
        static int sustained_max_memory_usage_limit =0;
        static List <String> emailList = new ArrayList<String>();
        static Map<Integer,Data> procData = new HashMap<Integer,Data>();   // for storing information about different processes
        // Data is another class defined in Data.java
        static int top_gap = 30;      // sampling rate of top is fixed to 30 sec
        static ArrayList<ArrayList<String>> violators = new ArrayList<ArrayList<String>>();  // for storing information about violating processes

        public static void main(String[] args)throws Exception
        {
           FileRead(args[0]);  // reads the configuration file
           Top();    // for running top command periodically
           Delete();  // for cleaning file periodically to which top command is writing
        }
        
        public static void FileRead(String fileName){
            // reads the configuration file whose name is fileName
            try{ 
             File file = new File(fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null){
                 String new_line = line.trim();
                 String value;
                 if(new_line.equals(""))
                        continue;
                 if(new_line.charAt(0) == '#')
                     continue;
                 String[] parts = new_line.split("=");
                 if(parts[0].equalsIgnoreCase("quota.window.minutes ")){
                    value = parts[1].trim();
                    quota_window_minutes = Integer.parseInt(value);
                    }
                 else if(parts[0].equalsIgnoreCase("sustained.max.cpu.usage.duration.limit ")){
                    value = parts[1].trim();
                    sustained_max_cpu_usage_duration_limit = Integer.parseInt(value);
                    }
                 else if(parts[0].equalsIgnoreCase("sustained.max.cpu.usage.limit ")){
                    value = parts[1].trim();
                    sustained_max_cpu_usage_limit = Integer.parseInt(value);
                    }
                 else if(parts[0].equalsIgnoreCase("sustained.max.memory.usage.duration.limit ")){
                    value = parts[1].trim(); 
                    sustained_max_memory_usage_duration_limit = Integer.parseInt(value);
                    }
                 else if(parts[0].equalsIgnoreCase("sustained.max.memory.usage.limit ")){
                    value = parts[1].trim(); 
                    sustained_max_memory_usage_limit = Integer.parseInt(value);
                    }
                 else{
                    String[] emailIDs = parts[1].split(",");
                    for(int i=0;i<emailIDs.length;i++)
                       emailList.add(emailIDs[i]);
                 }
                    

            }
            br.close();
            
        
       
       }
       catch(FileNotFoundException e){
            e.printStackTrace();
          }
          catch(IOException e1){
            e1.printStackTrace();
          }
       }
       
       public static void Top() throws FileNotFoundException{
       
       // To run top command periodically, ScheduledExecutorService is used. It runs any method given in run() of Runnable() class at intervals given by the user. 
       ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
       
       File f = new File("topOutput.txt");   // file for storing output of top command
       if(f.exists())
       {
         System.setOut(new PrintStream(new FileOutputStream("topOutput.txt",true))); // if file exists, open it in append mode.
       }
       else
       {
          System.setOut(new PrintStream(new FileOutputStream("topOutput.txt"))); // otherwise open a new file
       }     
        Runnable task = new Runnable(){
       public void run() {
          
       try{
            Process P = Runtime.getRuntime().exec("top -bn 1");   // run top command in batch mode
          
            BufferedReader StdInput = new BufferedReader(new InputStreamReader(P.getInputStream()));
            String TopS ="";
            Map<Integer,String> map= new HashMap<Integer,String>();  // map for storing current output of top command
 
        while((TopS= StdInput.readLine())!=null)
        {
           String new_line = TopS.trim();
           if(new_line.startsWith("PID"))
           {
               System.out.println("Top Start");
               DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");   // date format
               LocalDateTime now = LocalDateTime.now();
               System.out.println(dtf.format(now));   // print date and time of top command in file 
              System.out.format("%-8s%-12s%-8s%-8s%-12s%-8s","PID","USER","%CPU","%MEM","TIME+","COMMAND");
              System.out.println();
              while((TopS= StdInput.readLine())!=null)
              {
                 if(TopS.equals(""))
                   break;
                 new_line = TopS.trim();
                 String[] parts = new_line.split("\\s+");
                 System.out.format("%-8s%-12s%-8s%-8s%-12s",parts[0],parts[1],parts[8],parts[9],parts[10]);  // extract PID, User, %CPU, %MEM, TIME+, COMMAND from output of top command
                 
                 StringBuilder sb= new StringBuilder();
                 for(int i=11;i<parts.length;i++)
                 {
                   System.out.print(parts[i]+" ");
                   sb.append(parts[i]).append(" ");   // if command has spaces in it
                 }
                 System.out.println();
                 int process_id = Integer.parseInt(parts[0]);  
                 map.put(process_id,sb.toString());   // this map stores process id and command of current top output
                 Data data1;
                 if(!procData.containsKey(process_id))   // if procData does not contain this process_id, then create a new object of Data Class to store it in procData
                 {
                     data1 = new Data();
                 }
                 
                 else
                 {
                     data1 = procData.get(process_id);  // process_id exists in procData
                     if(data1.checkCommand(sb.toString()) == 0)   // but command does not match
                       data1 = new Data();   // this means it will be treated as a new process
                 }
             data1.set(parts[0],parts[1],parts[8],parts[9],parts[10],sb.toString()); // if procdata has same process_id and command update the information in procData 
             int isInViolators =0;  // this process is not a violator
             if(Float.parseFloat(parts[8])>sustained_max_cpu_usage_limit){  // if cpu usage has been violated, increase the cpu violation duration
                data1.set_cpu_duration();
                int check = data1.check_cpu_violation(sustained_max_cpu_usage_duration_limit*60);  // check if violation duration has exceeded max cpu usage duration
                if(check==1)
                {
                   // add it to violators list
                    ArrayList<String> anotherList = new ArrayList<String>();
                    anotherList.add(parts[0]);
                    anotherList.add(parts[1]);
                    anotherList.add(parts[8]);
                    anotherList.add(parts[9]);
                    anotherList.add(parts[10]);
                    anotherList.add(sb.toString());
                    anotherList.add("CPU");
                    violators.add(anotherList);
                    
                    isInViolators =1;
                     data1.resetCpuDuration();  // reset cpu violation duration to 0 
                   
                }
             }
             if(Float.parseFloat(parts[9])>sustained_max_memory_usage_limit){  // if memory usage has been violated, increase the memory  violation duration
                data1.set_mem_duration();
                int check = data1.check_mem_violation(sustained_max_memory_usage_duration_limit*60); // check if violation duration has exceeded max memory usage duration
                if(check==1 && isInViolators ==0)
                {
                    // add it to violators list if isInViolators ==0 
                    ArrayList<String> anotherList = new ArrayList<String>();
                    anotherList.add(parts[0]);
                    anotherList.add(parts[1]);
                    anotherList.add(parts[8]);
                    anotherList.add(parts[9]);
                    anotherList.add(parts[10]);
                    anotherList.add(sb.toString());
                    anotherList.add("MEM");
                    violators.add(anotherList);
                    isInViolators =1;
                    data1.resetMemDuration();
                    
                }
             }
             
             procData.put(process_id,data1);  // add the current process to procdata
             
            }
             
            
            
           }
           
        }
        
        System.out.println("\n\n\n\n\n\n\n");
        ArrayList<Integer> AbortedProcesses = new ArrayList<Integer>();  // store the process_id of aborted processes
        for (Integer process_id : procData.keySet()){
              if(!map.containsKey(process_id))
                 AbortedProcesses.add(process_id);   // aborted processes are the ones which are in procdata but are not in map ie current output of top command
                 
        }
        for (Integer number : AbortedProcesses) {
	      procData.remove(number);  // remove all the aborted processes from procData
        }
        map.clear();   // clear the hashmap which contained output of currently running top command
        
        if(!violators.isEmpty()){   // if violators is not empty send mails 
        String message = "Following are the details of processes that violated either memory or cpu usage:\n\n";
        for(List<String> info : violators) {
          
           message += "Process ID "+Integer.parseInt(info.get(0))+" violated "+info.get(6)+" usage"+"\nUser: "+info.get(1)+"\n%CPU Usage: "+Float.parseFloat(info.get(2))+"\n%MEM Usage: "+Float.parseFloat(info.get(3))+"\nTIME+ : "+info.get(4)+"\nCOMMAND : "+info.get(5)+"\n\n";
        }
        
        
        for(String emailId : emailList) {
          
        
        Mailer.send("komalcse1024@gmail.com","chuhi@csl202",emailId,"Alert: Excess Resource Usage",message);}
        violators.clear();   // clear the violators list after sending mails
        }
       }
         
        catch(Exception e1){
            e1.printStackTrace();
        }
        }
        };
        
        int periodicDelay = top_gap;   // runs at period of 30 sec
 
        scheduler.scheduleAtFixedRate(task, 0, periodicDelay,TimeUnit.SECONDS);
        
        }
        
        
        public static void Delete() throws FileNotFoundException{
       // to clean topOutput.txt file after certain intervals
          ScheduledExecutorService scheduler2 = Executors.newSingleThreadScheduledExecutor();
          System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
          Runnable delete = new Runnable(){
          public void run() { 
             
		try
		{
		     
                        System.setOut(new PrintStream(new FileOutputStream("topOutput.txt")));
			BufferedReader file = new BufferedReader(new FileReader("topOutput.txt"));
			String line;
			StringBuilder sb= new StringBuilder();
			int flag=1;
			while ((line = file.readLine()) != null) 
			{
			   
			   
			    if(line.equals("Top Start"))
			    {
			         
			         
			         line = file.readLine();
			         line = line.trim();
			         String[] parts = line.split("\\s+");
			         String[] date_components = parts[0].split("/");
			         String[] time_components = parts[1].split(":");
			         DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                                 LocalDateTime now = LocalDateTime.now();   // current date and time
                                 String current_time = now.format(dtf);
                                 String[] current_parts = current_time.split("\\s+");
			         String[] current_date_components = current_parts[0].split("/");
			         String[] current_time_components = current_parts[1].split(":");
			         
			         int hours = Integer.parseInt(time_components[0]);
				 int minutes = Integer.parseInt(time_components[1]);
				 int seconds = Integer.parseInt(time_components[2]);
				 int duration_sec = 3600 * 24 * (Integer.parseInt(current_date_components[2]) - Integer.parseInt(date_components[2])) + 3600 * (Integer.parseInt(current_time_components[0])-hours) + 60 * (Integer.parseInt(current_time_components[1])-minutes) + (Integer.parseInt(current_time_components[2])-seconds);
				 double duration_min = (double)duration_sec / 60; // differnce of current date and time and that stored along with top output in minutes
				  
				 if(duration_min > quota_window_minutes)
				  { 
				     
				     flag=0;
				     
				     continue;
				  }
				  else
				  {
				      flag=2;
				      
				  }
			      } 
			    if(flag==0)
			       continue;
			    else if(flag==2){
			    
			     sb.append(line).append("\n");
			    
			   }
			   
			}
			String input = sb.toString();
			
			FileWriter fwriter = new FileWriter("topOutput.txt",false);  // open topOutput.txt in overwrite mode
			BufferedWriter br = new BufferedWriter(fwriter);
			PrintWriter out = new PrintWriter(br);
			out.print(input);
			out.close();
			file.close();
		}
		catch (Exception e)
		{
		        System.out.println("Problem reading file.");
		}
          }
          };
        
          int deleteDelay = quota_window_minutes;
 
          scheduler2.scheduleAtFixedRate(delete, 0, deleteDelay,TimeUnit.MINUTES);
        
        }
        
     
}

class Mailer{  // for sending mails
    public static void send(String from,String password,String to,String sub,String msg){  
          //Get properties object  
         
          Properties props = new Properties();    
          props.put("mail.smtp.host", "smtp.gmail.com");    
          props.put("mail.smtp.socketFactory.port", "465");    
          props.put("mail.smtp.socketFactory.class",    
                    "javax.net.ssl.SSLSocketFactory");    
          props.put("mail.smtp.auth", "true");    
          props.put("mail.smtp.port", "465");    
          //get Session   
          Session session = Session.getDefaultInstance(props,    
           new javax.mail.Authenticator() {    
           protected PasswordAuthentication getPasswordAuthentication() {    
           return new PasswordAuthentication(from,password);  
           }    
          });    
          //compose message    
          try {    
           MimeMessage message = new MimeMessage(session);    
           message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
           message.setSubject(sub);    
           message.setText(msg);    
           //send message  
           Transport.send(message);    
              
          } catch (MessagingException e) {throw new RuntimeException(e);}    
             
    }  
}  


