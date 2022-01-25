package chatbot;
import java.util.Scanner;

import java.io.File;
import java.io.FileWriter;
// import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.time.LocalDate;
// import java.time.format.DateTimeFormatter;
// import java.time.temporal.ChronoUnit;
import java.time.LocalTime;

public class Storage {
    
    private String filePath;
    private String fileDirectory;

    public static final String LOAD_SUCCESS = "I have successfully loaded your saved agenda, Sir.";
    public static final String UNREADABLE_FILE = "Sorry Sir, I was unable to access the data file.";

    public Storage(String filePath, String fileDirectory) {
        this.filePath = filePath;
        this.fileDirectory = fileDirectory;
    }

    // saves task list into duke.txt
    public void saveData(TaskList taskList) throws DukeException {
        // create a directory if it doesn't exist
        File dataFile = new File(this.fileDirectory);
        if (!dataFile.exists()){
            dataFile.mkdirs();
        }

        try {
            FileWriter fileWriter = new FileWriter(this.filePath, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            
            for (Task task : taskList.getTaskList()) {
                String dataEntry = "";
                if (task instanceof Todo) {
                    dataEntry += "Todo";
                    dataEntry += task.getIsDone() ? "/;DONE" : "/;NOT_DONE";
                    dataEntry += "/;" + task.getTaskName();
                    dataEntry += "\n";

                } else if (task instanceof Deadline) {
                    Deadline deadline = (Deadline) task;
                    dataEntry += "Deadline";
                    dataEntry += deadline.getIsDone() ? "/;DONE" : "/;NOT_DONE";
                    dataEntry += "/;" + deadline.getTaskName();
                    dataEntry += "/;" + deadline.getDate();
                    if (deadline.getTime() == null) {
                        dataEntry += "/;" + "null";
                    } else {
                        dataEntry += "/;" + deadline.getTime();
                    }
                    dataEntry += "\n";

                } else if (task instanceof Event) {
                    Event event = (Event) task;
                    dataEntry += "Event";
                    dataEntry += event.getIsDone() ? "/;DONE" : "/;NOT_DONE";
                    dataEntry += "/;" + event.getTaskName();
                    dataEntry += "/;" + event.getDate();
                    if (event.getTime() == null) {
                        dataEntry += "/;" + "null";
                    } else {
                        dataEntry += "/;" + event.getTime();
                    }
                    dataEntry += "\n";
                }
                printWriter.printf(dataEntry);
            }
            printWriter.close();

        } catch (IOException exception) {
            throw new DukeException(UNREADABLE_FILE);
        }
    }

    // load the duke.txt into the array
    public void loadData(TaskList taskList) throws DukeException {
        try {
            // File directory = new File("./");
            // System.out.println(directory.getAbsolutePath()); // used to check directory java starts at

            // check if there is a file at all
            File dataFile = new File(this.filePath);
            if (!dataFile.exists()){
                return;
            }

            Scanner scanner = new Scanner(dataFile);
            String dataEntry;
            while (scanner.hasNext()) {
                dataEntry = scanner.nextLine();
                String[] dataEntryArray = dataEntry.split("/;"); // assumes data file is formatted correctly
                
                // check whether task is done
                boolean isDone = false;
                    if (dataEntryArray[1].equals("DONE")) {
                        isDone = true;
                    }
                
                Task newTask;
                LocalTime taskTime = null;
                switch (dataEntryArray[0]) {
                    case "Todo": // to-do task
                        newTask = new Todo(dataEntryArray[2], isDone);
                        break;
                    case "Deadline": // deadline task
                        taskTime = null;
                        if (!dataEntryArray[4].equals("null")) {
                            taskTime = LocalTime.parse(dataEntryArray[4]);
                        }
                        newTask = new Deadline(
                            dataEntryArray[2], 
                            LocalDate.parse(dataEntryArray[3]), 
                            taskTime,
                            isDone);
                        break;
                    case "Event": // event task
                        taskTime = null;
                        if (!dataEntryArray[4].equals("null")) {
                            taskTime = LocalTime.parse(dataEntryArray[4]);
                        }
                        newTask = new Event(
                            dataEntryArray[2], 
                            LocalDate.parse(dataEntryArray[3]), 
                            taskTime,
                            isDone);
                        break;
                    default: // unknown task - should not reach here
                        newTask = new Todo(dataEntryArray[2], isDone);
                        break;
                }
                taskList.addTask(newTask);
            }
            scanner.close();
            Ui.displayMessage(LOAD_SUCCESS);
        } catch (FileNotFoundException e) {
            throw new DukeException(UNREADABLE_FILE);
        }
    }
}
