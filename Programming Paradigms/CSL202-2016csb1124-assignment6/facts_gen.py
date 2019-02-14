import sys
import yaml
import string
import re
import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler

def main_function():
        data_dict={}
        new_file="-\n"
        bool_hyphen=0
        f= open("new.yaml","w+")
        with open("yaml/"+sys.argv[1], 'r') as stream:
                old=stream.read()
                try:
                        old=re.sub(r'\t','    ',old)
                        lines = old.splitlines()
                        for line in lines:
                                if line=="":
                                        new_file = new_file + "\n" 
                                        bool_hyphen=1
                                else:
                                        if bool_hyphen==1:
                                                new_file = new_file + "-\n"+ "    "+line+"\n"
                                                bool_hyphen=0
                                        else:
                                                new_file = new_file + "    "+line+"\n"
                        f.write(new_file)
                        f.close()
                except yaml.YAMLError as exc:
                        print(exc)

        f= open("facts_and_rules.pl","w+")                
        with open("new.yaml", 'r') as stream:
                yaml_list = yaml.load(stream)
                for i in yaml_list:
                        key = next(iter(i))
                        if key in data_dict.keys():
                    		data_dict[key].append(i[key])
                        else:
                    		data_dict[key] = [i[key]]
                for i in range(len(data_dict["OS"])):
                        f.write("oid("+str(data_dict["OS"][i]["id"])+").\n")
                for i in range(len(data_dict["OS"])):        
                        f.write("oname("+str(data_dict["OS"][i]["id"])+",'"+str(data_dict["OS"][i]["name"])+"').\n")
                for i in range(len(data_dict["OS"])):                
                        f.write("olibs("+str(data_dict["OS"][i]["id"])+","+str(data_dict["OS"][i]["provides_libs"])+").\n")
                for i in range(len(data_dict["Machine"])):
                        f.write("mid("+str(data_dict["Machine"][i]["id"])+").\n")
                for i in range(len(data_dict["Machine"])):                
                        f.write("mtype("+str(data_dict["Machine"][i]["id"])+",'"+str(data_dict["Machine"][i]["type"])+"').\n")
                for i in range(len(data_dict["Machine"])):                
                        f.write("mos("+str(data_dict["Machine"][i]["id"])+","+str(data_dict["Machine"][i]["OS"])+").\n")
                for i in range(len(data_dict["Machine"])):                
                        ram = str(data_dict["Machine"][i]["RAM"]).split()
                        RAM = float(ram[0])
                        if ram[1]=="GB":
                                RAM = RAM*1024
                        elif ram[1]=="KB":
                                RAM = RAM/1024
                        elif ram[1]=="TB":
                                RAM = RAM*1024*1024
                        elif ram[1]=="B":
                                RAM = (RAM/1024)/1024
                        f.write("mram("+str(data_dict["Machine"][i]["id"])+","+str(RAM)+").\n")
                for i in range(len(data_dict["Machine"])):                
                        disk = str(data_dict["Machine"][i]["disk"]).split()
                        Disk = float(disk[0])
                        if disk[1]=="TB":
                                Disk = Disk*1024
                        elif disk[1]=="MB":
                                Disk = Disk/1024
                        elif disk[1]=="B":
                                Disk = ((Disk/1024)/1024)/1024
                        elif disk[1]=="KB":
                                Disk = (Disk/1024)/1024
                        f.write("mdisk("+str(data_dict["Machine"][i]["id"])+","+str(Disk)+").\n")
                for i in range(len(data_dict["Machine"])):                
                        f.write("mcpu("+str(data_dict["Machine"][i]["id"])+","+str((data_dict["Machine"][i]["CPU"]).split()[0])+").\n")
                for i in range(len(data_dict["SoftwareApp"])):
                        f.write("sname("+str(data_dict["SoftwareApp"][i]["id"])+",'"+str(data_dict["SoftwareApp"][i]["name"])+"').\n")
                for i in range(len(data_dict["SoftwareApp"])):                
                        ram = str(data_dict["SoftwareApp"][i]["requires_hardware"]["min_RAM"]).split()
                        RAM = float(ram[0])
                        if ram[1]=="GB":
                                RAM = RAM*1024
                        elif ram[1]=="KB":
                                RAM = RAM/1024
                        elif ram[1]=="TB":
                                RAM = RAM*1024*1024
                        elif ram[1]=="B":
                                RAM = (RAM/1024)/1024
                        f.write("sram("+str(data_dict["SoftwareApp"][i]["id"])+",'"+str(data_dict["SoftwareApp"][i]["name"])+"',"+str(RAM)+").\n")
                for i in range(len(data_dict["SoftwareApp"])):                
                        disk = str(data_dict["SoftwareApp"][i]["requires_hardware"]["min_disk"]).split()
                        Disk = float(disk[0])
                        if disk[1]=="TB":
                                Disk = Disk*1024
                        elif disk[1]=="MB":
                                Disk = Disk/1024
                        elif disk[1]=="B":
                                Disk = ((Disk/1024)/1024)/1024
                        elif disk[1]=="KB":
                                Disk = (Disk/1024)/1024
                        f.write("sdisk("+str(data_dict["SoftwareApp"][i]["id"])+",'"+str(data_dict["SoftwareApp"][i]["name"])+"',"+str(Disk)+").\n")
                for i in range(len(data_dict["SoftwareApp"])):                
                        f.write("scpu("+str(data_dict["SoftwareApp"][i]["id"])+",'"+str(data_dict["SoftwareApp"][i]["name"])+"',"+str((data_dict["SoftwareApp"][i]["requires_hardware"]["min_CPU"]).split()[0])+").\n")
                for i in range(len(data_dict["SoftwareApp"])):                
                        f.write("sos("+str(data_dict["SoftwareApp"][i]["id"])+",'"+str(data_dict["SoftwareApp"][i]["name"])+"',"+str(data_dict["SoftwareApp"][i]["requires_software"]["OS"])+").\n")
                for i in range(len(data_dict["SoftwareApp"])):                
                        f.write("slibs("+str(data_dict["SoftwareApp"][i]["id"])+",'"+str(data_dict["SoftwareApp"][i]["name"])+"',"+str(data_dict["SoftwareApp"][i]["requires_software"]["libs"])+").\n")
                f.write("memberchk(X, [Y|Ys]) :- X = Y ; memberchk(X, Ys).\nsubset([], _).\nsubset([X|Tail], Y):- memberchk(X, Y),subset(Tail, Y).\nisBiggerThan(X,Y) :-\n    (  X >= Y\n       -> true\n       ;  false\n    ).\ncheckmachine(Id,MId):- slibs(Id,Name,Slist),sos(Id,Name,OSId),memberchk(OS,OSId),olibs(OS,Olist),subset(Slist,Olist),mos(MId,OS),write('Machine Id : '),write(MId),write('\n'),mtype(MId,MType),write('Machine Type : '),write(MType),nl,write('OS : '),write(OS),nl,mram(MId,MRAM),write('RAM : '),write(MRAM),write(' MB'),nl,mdisk(MId,MDisk),write('DISK : '),write(MDisk),write(' GB'),nl,mcpu(MId,MCPU),write('CPU : '),write(MCPU),write(' cores'),nl,sram(Id,Name,SRAM),sdisk(Id,Name,SDisk),scpu(Id,Name,SCPU),isBiggerThan(MRAM,SRAM),isBiggerThan(MDisk,SDisk),isBiggerThan(MCPU,SCPU).")
                f.close()


class MyHandler(FileSystemEventHandler):
    def on_modified(self, event):
        print "Yaml file has changed!"
        main_function()

if __name__ == "__main__":
        if sys.argv[2]=="-n":
                main_function()
                
        elif sys.argv[2]=="-d":
                count = 1
                if count==1:
                        main_function()
                        count = count + 1
                event_handler = MyHandler()
                observer = Observer()
                observer.schedule(event_handler, path='./yaml', recursive=False)
                observer.start()

                try:
                        while True:
                            time.sleep(1)
                except KeyboardInterrupt:
                        observer.stop()
                observer.join()
