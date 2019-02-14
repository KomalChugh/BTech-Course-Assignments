How to run the program:
lex 2016csb1124.lex
gcc lex.yy.c -lfl
./a.out input.txt > output.html


Description:
I have declared two definitions:
one [^:|]+":" this is for those fields which end with :     (rule 1)
other [^:|]+"|" this is for those fields which end with |   (rule 2)

For keeping track of background color I have declared an array which stores keys corresponding to serial number.
I have initialised color with #aaa and difference kept is #af
There is a flag for keeping track of start of record and whenever there is a new record, the corresponding key is matched with the elements of the array and if there is a match, the background color is kept same otherwise color is incremented by difference.

whenever rule 2 is matched, after printing the required field, it prints </tr> and flag is set to 0 so that a new entry can be started.
whenever rule 1 is matched, if flag==0 it prints <tr bgcolor=....> and if flag!=0 it prints <td> 
