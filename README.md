# Recurring Task Board

A colored-coded GUI for tracking multiple recurring tasks.

On first run, the local database will be created using the example database. 

The local database `recurringtasks-db.txt` should follow this format:

    name of item, date of next occurrence, occurrence interval in days

**Warning**: Commas must not be used in the item name because they delimit a new task column.

## Tips

If you receive a Java's `ArrayIndexOutOfBoundsException`, then you forgot a comma somewhere in your local database.
