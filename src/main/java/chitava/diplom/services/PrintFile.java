package chitava.diplom.services;


import chitava.diplom.models.Worker;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс вывода на бумажный носитель
 */
public class PrintFile {
    public static <PrintJobWatcher> void printTo(ArrayList<Worker> workers) throws IOException {
        StringBuilder toprint = new StringBuilder();
        for (Worker worker : workers) {
            toprint.append(worker.toString());
            toprint.append('\n');
            toprint.append("----------------------------------------------------");
            toprint.append('\n');
        }
        JTextPane jtp = new JTextPane();
        jtp.setBackground(Color.white);
        jtp.setText(String.valueOf(toprint));
        boolean show = true;
        try {
            jtp.print(null, null, show, null, null, show);
        } catch (java.awt.print.PrinterException e) {
            throw new RuntimeException();
        }
    }
}



