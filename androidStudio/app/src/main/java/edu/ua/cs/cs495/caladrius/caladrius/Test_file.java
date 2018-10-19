package edu.ua.cs.cs495.caladrius.caladrius;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// This file just use for testing code

public class Test_file {

    public static void main(String[] args) {
        System.out.println("Hello, this is Java");
        System.out.println(getMonthForInt(10));
        String date = getBackupFolderName();
        System.out.println(date.substring(0, date.length()-5) + "th" +
                date.substring(date.length()-5, date.length()));
    }

    static String getMonthForInt(int m) {
        List<String> monthStr = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        return monthStr.get(m);
    }

    public static String getBackupFolderName() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
        return sdf.format(date);
    }
}
