# tpm
Total Productive Maintanence board

A GUI for tracking reoccurring tasks, originally written in Python for the console.

The database file "java-tpm-db.txt" follows the format below:

    name_of_item dateofnexttime dayinterval
    
The name of the item must not contain spaces, as a space is the default delimiter for `java.util.Scanner`.