Submitter name: Komal

Roll No.: 2016csb1124

Course: CSL202 Assignment6

=================================

Requirements :

Import the following before running the application:

sudo apt-get install python-yaml
sudo pip install watchdog

=================================

Important note :

1) Input yaml file should be kept in the folder named yaml.

2) For keeping check on the yaml file, I have implemented the solution in two ways :

a) logic_app.py asks the user to enter 3 if the input yaml file has been changed. Upon entering 3, it calls the facts_gen.py and generates the prolog file again.

b) you can run facts_gen.py in two ways:
        python facts_gen.py machine_info.yaml -d  
        python facts_gen.py machine_info.yaml -n
        
   -n means in normal mode.
   -d means it will keep running and watchdog will generate the prolog file dynamically whenever yaml file will change.
   
==================================

What does this program do

There are two python files namely facts_gen.py and logic_app.py . Given a yaml file, facts_gen.py generates a prolog file facts_and_rules.pl which contains facts about machines, os and software applications and also contains some rules which are used to check whether an application can be executed on a given machine. After that logic_app.py is used to ask queries from the prolog file. There are two types of queries : 1) list all the machines where an application can be executed  2) whether an application can be executed on a given machine or not.   

================================= 

A description of how this program works (i.e. its logic)

There is a function named main_function() in facts_gen.py . This function opens the yaml file, converts all tabs to four spaces and inserts hyphen and generates a new file new.yaml. Then new.yaml is loaded into a list of dictionaries using yaml.load(). From this list of dictionaries, facts are written into the prolog file and also ram and disk are converted to a common unit for comparison. Then rules are also written to the prolog file.

If facts_gen.py is run with -n argument then only this main_function() is called and if it is run with -d argument then main_function() is called and after that watchdog keeps a check on yaml file and if a change is detected it prints the message on the terminal and also runs the main_function() again.

In logic_app.py user has 3 options : first and second for running queries and third if yaml file has been changed. I have used subprocess to run terminal command from python code and for printing all the outputs of a query in prolog, I have used forall command.  


=================================

How to compile and run this program

For running facts_gen.py normally :                     python facts_gen.py machine_info.yaml -n
For using watchdog to keep check on yaml file :         python facts_gen.py machine_info.yaml -d

python logic_app.py machine_info.yaml

If facts_gen.py is run with -d argument, then there is no need to enter 3 in the logic_app.py because it will automatically reload the prolog file. And if it is run with -n argument, then upon changing the yaml file enter 3. 

===================================

Sample run :   Screenshots have been attached.
