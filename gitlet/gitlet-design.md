# Gitlet Design Document

**Name**: Shantao Ru

## Classes and Data Structures

###DumpObj
A debugging class whose main program may be invoked as follows:
where each FILE is a file produced by Utils.writeObject
containing a serialized object).  This will simply read FILE,
deserialize it, and call the dump method on the resulting Object.

###Main 
This the Main class of the program, it takes no arugment and 
will reference DumpObj class of the program.

###Unit test
1.Place Order Test: test that putting files in inorder sequences
2.New Pending Test

## Algorithms

###DumpObj Class:
FileTree: Tree that use to hold the files' address, will add more to this 
in the future development.


###Main Class:
Will add more in the future development.


## Persistence

