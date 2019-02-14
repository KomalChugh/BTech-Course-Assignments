Name : Komal
Entry number : 2016csb1124
Submission : CSL202 Lab exam 3

==========================================

How to run the program :
python xml_checker.py test.xml

Here test.xml is the input xml file

==========================================

What does the program do:

Given an input xml file, the program checks if it is well-formed or not. An input file is well-formed if all the opening tags match with the closing tags and there is no overlapping between the opening and closing tags. It also checks if all the tags contain only a-z or A-Z characters. Also there should be only one root element that contains all the other elements for an input file to be well-formed. The program prints the message "Well formed" if it is well-formed. Otherwise it prints the message "Not well-formed" along with the error due to which it is considered as non well-formed.

==========================================

Assumptions: The given input file should not contain prologue. 
	   
==========================================

Logic behind the program :

The program first opens the input file and reads and stores it in a string. Now we match any pattern of the form <....> or </...>
in the file string. Pattern used for matching these expressions is </?(.*?)> where .*? means matching any character any number of times non-greedily. Non-greedily means to match the shortest substring possible. And we use findall to find all the tags contained in the string along with what is matched against (.*?)

The result obtained from matching patterns is a list of tuples whose first element contains <...> or </...> ie the full tag and second element contains what is matched against (.*?) for eg if a tag is of the form <strong>, then list contains a tuple of the form ('<strong>','strong'). Both of these elements are of interest to us. From the first element the program identifies whether it is an opening tag or closing tag or self-closing tag. And from the second element it checks whether the tag contains only a-z and A-Z characters or not.

If it is an opening tag, the program appends it to a list called store_tags. And if its a closing tag, then it pops the last element from the list and checks if the closing tag matches with the opening tag or not. Here the list is actually used as a stack.

To check whether the input file does not start with a closing tag, before poping an element from the list the program checks if the list is empty or not. In case the list is empty, this means input file starts with a closing tag.

To check whether there is a single root element or not : The list can be empty only when we are appending the first opening tag to it. Hence I have used a count variable to keep track of the opening tags.

For a self-closing tag, we do not need to add it to the list but we need to ensure that it does not occur after the end of root element or before the start of the root element. Otherwise it can occur anywhere in the input file.  

=======================================

Sample run:

There are three sample input files: test.xml , test1.xml and test2.xml
Output: 
test.xml: Not well-formed
	  Opening and closing tags do not match.
	  
test1.xml: Not well-formed
	   Self-closing tag does not appear within the root element.
	   
test2.xml: Well formed
