How to run the program:
javac -cp ./activation.jar:./mail.jar:. Violation.java 
java -cp ./activation.jar:./mail.jar:. Violation settings.properties


Note: Enter Ctrl+C to terminate the program.

Assumption: Process is distinguished by both PID and command.
            Sampling rate of top command is 30 second 


Logic behind the program:

First, program reads the configuration file through a method FileRead of Violation.java and stores the extracted variables. Then top command is run periodically through ScheduledExecutorService Class. I have fixed the sampling rate of running top command at 30 sec. So after every 30 sec top command runs and stores the output in a file called topOutput.txt and also the PID and command of currently running top command are stored in a local hashmap called map. Now for every PID of map, we first search whether it exists in procData or not. procData stores all the required information about all the active processes in a hashmap<integer,data> where integer is the PID and data is an object of Data class. If the PID is not there in procData then we create a new instance of data class. And if it exists we check whether the command is same or not. 

If the command is not same this means it is a new process, then again we create a new instance of data class. Otherwise we update the corresponding data object. Now we check whether cpu usage has been violated or not. If it has been violated we increment the cpu_violation_duration (in data class) by sampling rate . Now we check whether this duration exceeds the sustained_max_cpu_usage_duration_limit or not. If yes, we add it to violators list. Same argument holds for memory usage. And after adding it to violators list we make cpu_violation_duration or mem_violation_duration as 0.

After every iteration of top command, we iterate over all the processes stored in procData and if there exists a process which is there in procData but not in local map, this means that process has been killed. Hence we remove it from procData. Also after every iteration of top command we check whether violators has any entry in it or not. If it has entries we send email to the ids listed in configuration file and remove those entries from procData.

Along with top command, ScheduledExecutorService Class is also cleaning topOutput.txt file simultaneously at an interval of quota_window_minutes. For cleaning we check the date and time of top output and take the difference of that time and current time. Then we convert it in minutes if it is more than quota_window_minutes then we do not add the output to a string, otherwise we do. That string is written to topOutput.txt opened in overwrite mode.

Hence the program keeps on storing output and cleaner keeps on cleaning the file. Here I have stored the output in a text file so that whenever this program  is terminated, we have the history in a file. Even on restarting the system we will have history. 

