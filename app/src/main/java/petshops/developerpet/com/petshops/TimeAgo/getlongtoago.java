package petshops.developerpet.com.petshops.TimeAgo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by root on 3/22/18.
 */

public class getlongtoago {

    public  String getlongtoago(long createdAt){

        DateFormat userDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        DateFormat dateFormatNeeded = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS");
        Date date = null;
        date = new Date(createdAt);
        String crdate1 = dateFormatNeeded.format(date);

        /*
        import java.util.Calendar;

Calendar c = Calendar.getInstance();
//Set time in milliseconds
c.setTimeInMillis(milliseconds);
int mYear = c.get(Calendar.YEAR);
int mMonth = c.get(Calendar.MONTH);
int mDay = c.get(Calendar.DAY_OF_MONTH);
int hr = c.get(Calendar.HOUR);
int min = c.get(Calendar.MINUTE);
int sec = c.get(Calendar.SECOND);

         */

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        crdate1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(crdate1);
            current = dateFormat.parse(currenttime);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();
        long diffSeconds = diff / 1000;

        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);
        long diffWeeks = diff / (24 * 60 * 60 * 1000 * 7);
        long diffMonths = diff / (24 * 60 * 60 * 1000 * 30);

        String time = null;

        if (diffMonths > 0) {
            if (diffMonths == 1) {
                time = diffMonths + " month ago ";
            } else {
                time = diffMonths + " months ago ";
            }
        }else{
            if (diffWeeks > 0) {
                if (diffWeeks == 1) {
                    time = diffWeeks + " week ago ";
                } else {
                    time = diffWeeks + " weeks ago ";
                }
            }else{

                if (diffDays > 0) {
                    if (diffDays == 1) {
                        time = diffDays + " day ago ";
                    } else {
                        time = diffDays + " days ago ";
                    }
                } else {
                    if (diffHours > 0) {
                        if (diffHours == 1) {
                            time = diffHours + " hour ago";
                        } else {
                            time = diffHours + " hours ago";
                        }
                    } else {
                        if (diffMinutes > 0) {
                            if (diffMinutes == 1) {
                                time = diffMinutes + " minute ago";
                            } else {
                                time = diffMinutes + " minutes ago";
                            }
                        } else {
                            if (diffSeconds > 0) {
                                time = diffSeconds + " secs ago";
                            }
                        }
                    }
                }
            }
        }

        return time;
    }


    public  long getlongtoagoSecond(long createdAt){

        DateFormat userDateFormat = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        DateFormat dateFormatNeeded = new SimpleDateFormat("MM/dd/yyyy HH:MM:SS");
        Date date = null;
        date = new Date(createdAt);
        String crdate1 = dateFormatNeeded.format(date);

        // Date Calculation
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        crdate1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(date);
        // get current date time with Calendar()
        Calendar cal = Calendar.getInstance();
        String currenttime = dateFormat.format(cal.getTime());

        Date CreatedAt = null;
        Date current = null;
        try {
            CreatedAt = dateFormat.parse(crdate1);
            current = dateFormat.parse(currenttime);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Get msec from each, and subtract.
        long diff = current.getTime() - CreatedAt.getTime();

         long diffSeconds = diff / 1000;
      //  long diffMinutes = diff / (60 * 1000) % 60;

        /*
        String time = null;

        if (diffSeconds > 0) {
            time = String.valueOf(diffSeconds);
        }


        if (diffMinutes > 0) {
            if (diffMinutes == 1) {
                time = String.valueOf(diffMinutes);//+ " minute ago";
            } else {
                time = String.valueOf(diffMinutes);// + " minutes ago";
            }
        } else {
            if (diffSeconds > 0) {
                time = String.valueOf(diffSeconds);
            }
        }
        */

    //    return time;

        return diffSeconds;
    }

}