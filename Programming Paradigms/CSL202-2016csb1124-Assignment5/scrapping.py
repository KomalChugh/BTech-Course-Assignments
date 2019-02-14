import urllib2
import sys
from bs4 import BeautifulSoup

line_no = 0                     # to get line number of <a> tag
url_list = {}                   # dictionary which stores href as key and a list of text and line number as value 
duplicate_url = {}              # dictionary which stores serial number as key and a list of href, text and line number as value
serial_no = 0                   # unique serial number for each duplicate 
first_occur_list = []           # list which stores serial numbers of first occurences of duplicates 

with open(sys.argv[1]) as inputfile:
    for line in inputfile:
        line_no += 1
        soup = BeautifulSoup(line, 'html.parser')       # apply beautifulSoup on each line of input file
        for a in soup.find_all('a', href=True):         # if it has href, store it in url_list
                url_list.setdefault(a['href'],[]).append(a.text)
                url_list.setdefault(a['href'],[]).append(line_no)
                
for key in url_list:                      # traverse url_list and if the value list has more than two entries, this means href is duplicate 
        list_of_values = url_list[key]
        if len(list_of_values)>2 :
                increment = 1
                for elem in list_of_values:
                        if increment%2 == 1 :
                                serial_no += 1
                                duplicate_url.setdefault(serial_no,[]).append(key)
                        if increment == 1:
                                first_occur_list.append(serial_no)
                        duplicate_url.setdefault(serial_no,[]).append(elem)  # store the duplicate hrefs in duplicate_url
                        increment += 1
 
print "Found",serial_no,"duplicates:"                       
for key in duplicate_url:                        # traverse duplicate_url and print all the duplicates
       list_of_values = duplicate_url[key] 
       print key, list_of_values[0],"\"",list_of_values[1],"\" at line",list_of_values[2]  

outputfile = sys.argv[1] + ".dedup" 
output = open(outputfile, "w")                  # open the output file in write mode

print ("\nSelect hyperlinks that you want to keep.\nEnter A to keep all, OR\nEnter F to keep the first one in a set of duplicates, OR\nEnter the serial numbers (separated by commas) of the links to keep.")

choice = raw_input("Your selection: ")
keep_list = []                          # stores serial numbers of duplicates to be kept
remove_list = []                        # stores serial numbers of duplicates to be removed
decompose_list = []                     # tags to be decomposed (removed)

if choice == "A" :
        for i in range(serial_no+1) :
             if i!=0 :
                keep_list.append(i)     # keep_list has all the serial numbers
          
elif choice == "F" :
        keep_list = list(first_occur_list)      # keep_list is same as first_occur_list
else :
        keep_list = [int(x) for x in choice.split(',')]         # keep_list as specified by user

for i in range(serial_no+1) :
             if i!=0 :
                if i in keep_list:
                      pass
                else:
                      remove_list.append(i)             # compliment of keep_list is remove_list 

tag_serial_number = 0           # to uniquely identify the tags to be removed
line_no_input = 0     
with open(sys.argv[1]) as inputfile:
        for line1 in inputfile:
                line_no_input += 1
                soup1 = BeautifulSoup(line1, 'html.parser')     # open input file and apply bs line by line
                for a in soup1.find_all('a', href=True):
                       tag_serial_number += 1
                       for i in remove_list :           # check for serial numbers to be removed
                                list_of_values = duplicate_url[i]
                                if a['href'] == list_of_values[0] and a.text == list_of_values[1] and line_no_input == list_of_values[2] :
                                    decompose_list.append(tag_serial_number)  # if the value of href, text and line number match for a given value list in duplicate_url, then add it to decompose_list
                        
tag_serial_number1 = 0   
with open(sys.argv[1]) as inputfile:
        soup2 = BeautifulSoup(inputfile, 'html.parser')
        for a in soup2.find_all('a', href=True):       # apply bs on the input file and decompose according to the entries in decompose_list
                                tag_serial_number1 += 1
                                if tag_serial_number1 in decompose_list:
                                        a.decompose()
        output.write(str(soup2))                # write formatted soup to output file

                                      
               
print "\nRemoved",len(remove_list),"hyperlinks. Output file written to",outputfile

                   

