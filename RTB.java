/* 
 * Copyright (c) 2017, Clayton Wahlstrom
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
/* 
 * Recurring Task Board can help you keep track of your regularly occurring tasks
 * such as laundry, mowing the lawn, backing up your files, etc.
 *
 * Could implement
 *     - sort items by date
 * 
 */

package recurringtaskboard;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Calendar;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class RTB extends JFrame {

    // initialize variables
    public static final Font BASE_FONT = new Font("Verdana", Font.PLAIN, 16);
    public static final Font BOLD_FONT = new Font("Verdana", Font.BOLD, 16);
    public static final String DB_PATH = "java-recurringtask-db.txt";
    public static final String LINE_ENDING = "\r\n";
    public static final String PACK_NAME = RTB.class.getPackage().getName();
    public static final String PRGM_NAME = "Recurring Task Board";
    public static final String USERNAME = System.getProperty("user.name");
    public static final String[] HEADERS = {"Task", "Days until", "Date", "Days", "", ""};
    public static final String CLOUD_PATH = "C:/Users/" +  USERNAME + "/Google Drive";
    public static final int DB_COLUMNS = 3;
    public static final int MAX_BACKUP = 15;
    public static final int MAX_GAP = 20;
    
    // initialize JThings
    private JLabel[] headerLabels;
    private JLabel[] dayLabels;
    private JLabel[] taskLabels;
    private JLabel[] dates;
    private JTextArea[] inputAreas;
    private JButton[] submitButtons;
    private JButton[] setDefaultButtons;
    private JButton updateButton;
    
    private String[][] db;
    private List<String> lines = new ArrayList<String>();
    
    // main method: instantiates and creates the GUI
    public static void main(String[] args) {
        // create a thread for creating the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                RTB rtbController = new RTB();
                rtbController.deleteOld();
                rtbController.loadFile(new File(PACK_NAME + "/" + DB_PATH));
                rtbController.initializeVars();
                rtbController.render();
            }
        });
    }

    // creates a new task manager
    public RTB() {
        super(PRGM_NAME);
    }
    
    // renders and packs the GUI
    public void render() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addComponentsToPane(this.getContentPane());
        this.pack();
        this.setVisible(true);
    }
    
    public void deleteOld() {
        while (new File(PACK_NAME + "/backups").list().length > MAX_BACKUP) {
            new File(PACK_NAME + "/backups").listFiles()[0].delete();
        }
    }
        
    // initializes JPanel variables, checks if cloud service exists
    public void initializeVars() {

        headerLabels      = new JLabel[HEADERS.length];
        taskLabels        = new JLabel[db.length];
        dayLabels         = new JLabel[db.length];
        dates             = new JLabel[db.length];
        inputAreas        = new JTextArea[db.length];
        submitButtons     = new JButton[db.length];
        setDefaultButtons = new JButton[db.length];
        
        System.out.print("Cloud service 'Google Drive' ");
        if (cloudExists()){
            System.out.println("exists");
        } else {
            System.out.println("doesn't exist");
        }
        System.out.println();
    }
    
    // @override of super-class, accepts Container object
    public void addComponentsToPane(final Container pane) {
        
        GridLayout mainLayout = new GridLayout(db.length + 1, HEADERS.length); // +1 for header row
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setLayout(mainLayout);

        // Set up components preferred size
        Dimension buttonSize = new JButton("Just a fake button").getPreferredSize();
        Dimension preferredSize = new Dimension((int)(buttonSize.getWidth() * HEADERS.length * 1.02) + MAX_GAP * 2,
                                                (int)(buttonSize.getHeight() * (db.length + 1) * 1.5 + MAX_GAP * 2));
        mainPanel.setPreferredSize(preferredSize);

        for (int i = 0; i < HEADERS.length; i++) {
            headerLabels[i] = new JLabel(HEADERS[i]);
            headerLabels[i].setFont(BOLD_FONT);
            headerLabels[i].setHorizontalAlignment(JLabel.CENTER);
            mainPanel.add(headerLabels[i]);
        }

        // Add submitButtons to mainLayout with Grid Layout
        for (int i = 0; i < db.length; i++) {
            
            taskLabels[i] = new JLabel(db[i][0]);
            taskLabels[i].setFont(BASE_FONT);
            
            dayLabels[i] = new JLabel();
            dayLabels[i].setFont(BASE_FONT);
            dayLabels[i].setHorizontalAlignment(JLabel.CENTER);
            
            dates[i] = new JLabel(db[i][1]);
            dates[i].setFont(BASE_FONT);
            dates[i].setHorizontalAlignment(JLabel.CENTER);
            
            inputAreas[i] = new JTextArea(db[i][2]);
            inputAreas[i].setFont(BASE_FONT);
            
            submitButtons[i] = new JButton("Submit");
            submitButtons[i].setFont(BASE_FONT);
            submitButtons[i].addActionListener(new ButtonHandler());
            
            setDefaultButtons[i] = new JButton("Set Default");
            setDefaultButtons[i].setFont(BASE_FONT);
            setDefaultButtons[i].addActionListener(new ButtonHandler());
            
            // add components
            mainPanel.add(taskLabels[i]);
            mainPanel.add(dayLabels[i]);
            mainPanel.add(dates[i]);
            mainPanel.add(inputAreas[i]);
            mainPanel.add(submitButtons[i]);
            mainPanel.add(setDefaultButtons[i]);
        }
        
        JPanel updatePanel = new JPanel();
        updatePanel.setLayout(new FlowLayout());
        
        updateButton = new JButton();
        updateButton.setFont(BASE_FONT);
        updateButton.addActionListener(new ButtonHandler());
        updateButton.setText("Update days until");
        
        updatePanel.add(updateButton);
        
        mainLayout.setHgap(5);
        mainLayout.setVgap(5);
        mainLayout.layoutContainer(mainPanel);
        
        pane.add(mainPanel, BorderLayout.NORTH);
        pane.add(new JSeparator(), BorderLayout.CENTER);
        pane.add(updatePanel, BorderLayout.SOUTH);
        
        updateDaysUntil();
    }
        
    // backs up the db
    public void backup() {
        saveTasksToFile(PACK_NAME + "/backups/backup-" + System.currentTimeMillis() + ".txt");
    }
    
    // returns whether cloud storage exists
    public boolean cloudExists() {
        return new File(CLOUD_PATH).exists();
    }    
    
    // returns the number of days from today, + or -
    public int getDaysFromToday(String nextDate) {
        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
        
        // parse, add, and reformat
        int daysBetween = 0;
        try {
            Date parsedDate = parser.parse(nextDate);
            Calendar date = Calendar.getInstance();
            date.setTime(parsedDate);
            Calendar today = Calendar.getInstance();

            if (date.after(today)) {
                while (date.after(today)) {
                    date.add(Calendar.DATE, -1);
                    daysBetween++;
                }
            } else if (date.before(today)) {
                daysBetween = 1; // offset for correct calculations
                while (date.before(today)) {
                    date.add(Calendar.DATE, 1);
                    daysBetween--;
                }
            }
            // else, daysBetween stays 0
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return daysBetween;
    }
    
    public void loadFile(File file) {
        try {
            System.out.println("pwd = " + new File(".").getAbsoluteFile());
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter(LINE_ENDING);
            while (scanner.hasNext()) {
                lines.add(scanner.next());
            }
            db = new String[lines.size()][DB_COLUMNS];
            for (int i = 0; i < lines.size(); i++) {
                db[i] = lines.get(i).split(", ");
            }
            backup();

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(0);
        }
    }
    
    // saves the formatted date base to a file
    // assumes db exists and is filled
    // writes "string" to file using the writeText method
    public void saveTasksToFile(String path) {
        String string = "";
        for (int i = 0; i < db.length; i++) {
            string += db[i][0] + ", " + db[i][1] + ", " + db[i][2] + LINE_ENDING;
        }
        writeText(path, string.trim());
    }
    
    // set the default days left for the item clicked
    public boolean setDefaultDays(int i) {
        boolean set = false;
        if (!inputAreas[i].getText().equals("")) {
            System.out.println("Setting the default days for index " + i + 
                           " from " + db[i][2] + " to " + inputAreas[i].getText());
            db[i][2] = inputAreas[i].getText();
            set = true;
        } else {
            System.err.println("Cannot set default days to a blank string.");
        }
        inputAreas[i].setText(db[i][2]);
        return set;
    }
    
    // basic filewriter using filename and text to write
    public void writeText(String path, String text) {
        try {
            File file = new File(path);
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.close();
            System.out.println(path + " written");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }
    }
    
    // updates all of the days column for all of the tasks
    public void updateDaysUntil() {
        for (int i = 0; i < db.length; i++) {
            updateDaysUntil(i);
        }
    }
    
    // updates the "days until" field of the given index
    public void updateDaysUntil(int i) {
        int daysuntil = getDaysFromToday(db[i][1]);
        dayLabels[i].setText(Integer.toString(daysuntil));
        if (daysuntil > 0) {
            dayLabels[i].setForeground(Color.BLACK);
        } else if (daysuntil == 0) {
            dayLabels[i].setForeground(Color.GREEN);
        /* } else if (0 > daysuntil && daysuntil > -(Integer.parseInt(db[i][2])/2.0)) {
            dayLabels[i].setForeground(Color.ORANGE); */
        } else if (daysuntil < 0) {
            dayLabels[i].setForeground(Color.RED);
        }
    }
    
    // adds the number of days in the text box to the given index,
    // updates text area and date.
    public boolean setDays(int i) {
        boolean success = false;
        try {
            // set up parser
            int days = Integer.parseInt(inputAreas[i].getText().trim());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, days);
            SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy");
            String next = parser.format(cal.getTime());
            
            // change date
            db[i][1] = next;
            dates[i].setText(db[i][1]);
            System.out.println("Modifying the date at index " + i); // only prints if successful
            success = true;
        } catch (Exception e) {
            System.err.println("Cannot change the date of " + e.getMessage());
        } finally {
            // reset text area back to default days
            inputAreas[i].setText(db[i][2]);
        }
        return success;
    }
    
    // handles button events
    private class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent ae) {
            boolean flushOutput = false;
            for (int i = 0; i < db.length; i++) {
                boolean found = false;
                if (ae.getSource() == submitButtons[i]) {
                    // one of the submit buttons was pressed
                    found = setDays(i);
                } else if (ae.getSource() == setDefaultButtons[i]) {
                    // one of the set default buttons was pressed
                    found = setDefaultDays(i);
                }
                
                if (found) {
                    saveTasksToFile(PACK_NAME + "/" + DB_PATH);
                    backup();
                    if (cloudExists()) {
                        saveTasksToFile(CLOUD_PATH + "/" + DB_PATH);
                    }
                    flushOutput = true;
                }
            }
            // if (ae.getSource() == updateButton) {
                // updateDaysUntil();
            // }
            if (flushOutput) {
                System.out.println();
            }
            updateDaysUntil(); // behavior when any button is pressed
        }
    }
   
}
