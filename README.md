# recurringtaskboard
Recurring Task Board

A colored-coded GUI for tracking multiple recurring tasks

The database file "java-recurringtask-db.txt" follows the format below:

    name of item, dateofnexttime, dayinterval
    
Items are delimited by commas and a space. Therefore, you must not use commas in any of the fields in the database.

If you receive an ArrayOutOfBoundsException than you forgot a comma somewhere.