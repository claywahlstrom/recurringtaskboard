# recurringtaskboard
Recurring Task Board

A colored-coded GUI for tracking multiple recurring tasks

The database file "java-recurringtask-db.txt" follows the format below:

    name_of_item dateofnexttime dayinterval
    
Items must be added when the program is not active and the name of the item must not contain spaces, as a space is the default delimiter for `Scanner` objects.