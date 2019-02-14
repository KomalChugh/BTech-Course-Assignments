import sys
import re

store_tags = []		# to store opening tags in a list which is used as a stack 
count = 0		# to keep count of tags

with open(sys.argv[1], 'r') as my_file:
    data=my_file.read().replace('\n', '')  # reads the input file and stores it in a string
    pattern = re.compile(r'(</?(.*?)/?>)')   # matches any substring of the form <...> and </...> 
    results = pattern.findall(data)	   # obtain all the matched results in a list 
    for result in results:
    	count += 1
    	tag = result[0]		           # result[0] contains <tag> or </tag>
    	if re.match('^[a-zA-Z]+$',result[1]): # result[1] contains tag without <> or </> Here we check whether tag contains only characters from a-z and A-Z or not
    		pass
    	else:
    		print "Not well-formed"     # if not, print the message and exit the program
    		print "Tags contain characters other than a-z A-Z"
    		exit()
    	if tag[1] == '/':                  # if it is a closing tag
    		if len(store_tags)==0:     # and store_tags is empty this means closing tag appears before an opening tag 
    			print "Not well-formed"
    			print "Closing tag appears before an opening tag."
    			exit()
    		opening_tag = store_tags.pop()  # otherwise pop the last element of the list store_tags and check if the closing tag matches with the opening tag
    		if opening_tag == result[1]:
    			pass
    		else:
    			print "Not well-formed"  # if it does not match, print the message and exit
    			print "Opening and closing tags do not match."
    			exit()
    	elif tag[len(tag)-2] == '/':      # if it is a self-closing tag
    		if len(store_tags)==0:      # and list is empty, this means self-closing tag appears after the root element
    			print "Not well-formed"
    			print "Self-closing tag does not appear within the root element."
    			exit()
    	else:
    		if len(store_tags)==0:         # if it is an opening tag
    			if count==1:	       # store_tags can be empty only before first opening tag is put into it
    				store_tags.append(result[1])
    			else:
    				print "Not well-formed" # else, there are more than one root elements 
    				print "There are more than one root elements."
    				exit()
    		else:
    			store_tags.append(result[1])   # add all the opening tags into the list store_tags
    				
    		
print "Well formed"     # if the program does not exit, it means the input file is well formed
    


    
