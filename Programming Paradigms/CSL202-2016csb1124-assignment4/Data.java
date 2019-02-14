import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  
import java.time.*;

public class Data{
     String PID;
     String USER;
     String CPU;
     String MEM;
     String TIME;
     String COMMAND;
     int cpu_violation_duration;  // time for which cpu usage has been violated
     int mem_violation_duration;  // time for which memory usage has been violated
     
     public void Data(){
        cpu_violation_duration = 0;
        mem_violation_duration = 0;
     }
     
     public void set(String PID,String USER,String CPU,String MEM,String TIME,String COMMAND){
        this.PID = PID;
        this.USER = USER;
        this.CPU = CPU;
        this.MEM = MEM;
        this.TIME = TIME;
        this.COMMAND = COMMAND;
     }
     
     public void set_cpu_duration(){
        this.cpu_violation_duration += 30;  // whenever %CPU exceeds cpu usage, we increment cpu_violation_duration by sampling rate
     }
     
     public void set_mem_duration(){
        this.mem_violation_duration += 30;  // whenever %MEM exceeds memory usage, we increment mem_violation_duration by sampling rate
     }
     
     public int check_cpu_violation(int max){
        if (this.cpu_violation_duration > max)
           return 1;
        else
           return 0; // returns 1 if cpu_violation_duration is greater than sustained_max_cpu_usage_duration_limit, otherwise 0
     }
     
     public int check_mem_violation(int max){
        if (this.mem_violation_duration > max)
           return 1;
        else
           return 0; // returns 1 if mem_violation_duration is greater than sustained_max_mem_usage_duration_limit, otherwise 0
     }
     
     public int checkCommand(String command)
     {
        if (COMMAND.equals(command))
           return 1;
        else
           return 0;   // checks if the command of this process is same as that of passed as an argument.
     }
     
     public void resetCpuDuration(){
        cpu_violation_duration = 0;   // resets cpu_violation_duration to 0 after adding the process to violators list
       
     }
     
     public void resetMemDuration(){
      mem_violation_duration = 0;      // resets mem_violation_duration to 0 after adding the process to violators list
     }
}
