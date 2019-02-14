from subprocess import call
import sys
import subprocess
import os


print "Enter 1 to know all the machines where an application can be executed"
print "Enter 2 to know whether an application can be executed on a given machine"
print "Enter 3 if yaml file has been modified"

answer= input()

if answer==1:
        print "Enter the application id : "
        app_id = input()
        process = subprocess.Popen(["swipl", "--quiet","-g","style_check(-singleton),[facts_and_rules],forall((Goal = checkmachine(" +str( app_id)+  ",X), call(Goal)), (nl)),halt"], stdout=subprocess.PIPE)
        count=1
        output = "\n"
        for line in iter(process.stdout.readline, ''):
                if line=="\n":
                        pass
                output += line
                
                if count==1:
                        count = count + 1;

        if count==1:
                print "Sorry there are no machines to execute this application."
        else:
                print output

       
elif answer==2:
        print "Enter the application id : "
        app_id = input()
        print "Enter the machine id : "
        mid = input()
        process = subprocess.Popen(["swipl", "--quiet","-g","style_check(-singleton),[facts_and_rules],forall((Goal = checkmachine(" +str( app_id)+  "," +str( mid)+  "), call(Goal)), (nl)),halt"], stdout=subprocess.PIPE)
        count=1
        for line in iter(process.stdout.readline, ''):
                if count==1:
                        print "Yes, the application can be executed on the given machine."
                        count = count + 1;

        if count==1:
                print "No, the application can not be executed on the given machine."

elif answer==3:
        os.system('python facts_gen.py '+sys.argv[1]+' -n')
        print "Prolog file has been modified"
