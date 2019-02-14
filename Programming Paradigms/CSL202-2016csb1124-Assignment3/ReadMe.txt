How to run the program:
javac JarFileInfo.java
java JarFileInfo > Report.txt
Http URL of jar file

Explanation:

The whole java program is divided into various classes. Main method is in a Class called JarFileInfo. First the program downloads the jar file locally using a Class called Download.

This class makes use of InputStream and FileOutputStream. InputStream reads bytes from the given jar file and Outputstream writes the bytes read into the jar file created locally. The name of the jar file is extracted from the given url. In basic terms it is just reading and writing bytes.

Then the program calls a method called getClasses which is declared in a class called GetClassesFromJar. This method iterates through the jar entries of the given jar file and if the entry is a directory (directories have a / at end of their names) it creates directories first. Then again it traverses through the jar entries and performs read write operation to extract the jar file in the given directory.
While doing so, if the file name ends with .class then it adds the file name to a list and then this method returns the list of classes.

Now we traverse the list of classes and print the names of all classes in the given jar file. Then the program traverses the list of classes and then for each class it calls javap method of a class called ClassStructure.

Javap method is used to invoke 'javap -p -verbose className.class' command by using ProcessBuilder object. InputStream of the process is sent to another method called DisplayClassStructure. This method scans the output of the command.

Now to count the number of entries under the constant pool section the program first searches for the string "Constant pool:" and then count all the entries under it staring with a '#'. This is beacuse all the entries of constant pool section start with a tag '#'.

For counting the number of methods in a class, the program searches for the string "Code:" and count the number of times this string is matched. Because every method has this string in the javap output.

Now to count the occurrences of JVM instructions , the program makes use of a hashmap. JVM instruction is recognised as an instruction which starts with a numeric constant and ':'. Whenever a string 'Code:' is matched it searches for a JVM instruction and extracts the second word from it.

JVM instruction has format like this
0: aload_0

But there are certain instructions like 0: 82
These instructions are ignored by the program.

The program first searches the JVM instruction in hashmap. If it exists it increments the value by 1 and if it doesn't exist it adds the JVM instruction as key and a value 1 to the hashmap.

When the javap method is called for the last class, hashmap is sorted in the DisplayClassStructure method and top 50 instructions are printed.
