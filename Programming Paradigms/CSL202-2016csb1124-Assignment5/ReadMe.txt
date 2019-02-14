Submitter name: Komal

Roll No.: 2016csb1124

Course: CSL202 Assignment5

=================================

1. What does this program do

The program reads an input html file and then with the help of web scrapping tool BeautifulSoup, identifies all the anchor tags which have same value of their href attribute and displays them to the user. Then the user has options to select the anchor tags he/she wants to keep. There are special options available like the user can enter A to keep all the links as such, or user can enter F to keep only the first occurences of the duplicate anchor tags. The final output html is written to a file which is named as inputFileName.dedup and is created in the current directory.

=================================

Assumption : Anchor tag should be in one line ie the href attribute, text and closing and opening tags should be in a single line.
             The program does not take into account any anchor tag which is commented in the input html file.

================================= 

2. A description of how this program works (i.e. its logic)

The program opens the input file and then for each line of file, beautifulSoup is applied to it to get all the anchor tags which have href attribute. This is stored in a dictionary named url_list. url_list stores href as key and has a list as value. This list stores the corresponding text and line number of the anchor tag. Now we traverse through url_list and if its value list has more than two entries, this means that the href attribute is duplicate. We store this href attribute, its text and line number as a value list in another dictionary whose key is a unique serial number. All the duplicate anchor tags are displayed to the user. 

Then the user is asked to make a choice for the tags he/she wants to keep. We store those serial numbers in a list named keep_list according to the user's choice. Then all those serial numbers which are not a part of keep_list are put into another list named remove_list. Now we again open the input html file and apply beautifulSoup to it line by line. For each serial number in remove_list, we obtain its corresponding href, text and line number and then check whether these three values match for with the anchor tag obtained from beautifulSoup. If yes, we mark its occurence in a list named decompose_list. decompose_list basically stores the position of the tags to be removed according to the serial order in which they occur in input html file. Then soup for the whole input file is obtained and tags are decomposed according to the decompose_list and finally that soup is converted to string and written to the output html file.  

=================================

3. How to compile and run this program

Open terminal and enter the following command:
python scrapping.py chrome_bookmarks.html

Note: Instead of chrome_bookmarks.html, you can specify any other input html file
The program has been tested on python version -- Python 2.7.12

