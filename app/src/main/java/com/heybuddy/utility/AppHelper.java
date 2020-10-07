package com.heybuddy.utility;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.format.DateFormat;

import com.heybuddy.Controller;
import com.heybuddy.Model.UserData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class AppHelper {

    private static final AppHelper ourInstance = new AppHelper();

    public static AppHelper getInstance() {
        return ourInstance;
    }

    private AppHelper() {
    }


    public File frombitmapToFile(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        // String temp = null;
        File file = new File(extStorageDirectory, "blessing" + System.currentTimeMillis() + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "blessing" + System.currentTimeMillis() + ".png");
        }
        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    /**
     * For Recent chat date and Time,Display time instead of Today
     *
     * @param smsTimeInMilis
     * @return
     */
    public String getFormattedDateWithoutDay(long smsTimeInMilis) {
        getDeviceDateFormat();
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = ", MMMM  d";
        final String dateTimeFormatString = "dd/MM/yyyy";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "" + DateFormat.format("h:mm aa", smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            //return "Yesterday" + DateFormat.AppHelper.getInstancformat(timeFormatString, smsTime);
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("dd MMMM yyyy, h:mm aa", smsTime).toString();
        }
    }

    public String getDeviceDateFormat() {
        Calendar cal = new GregorianCalendar(2013, 11, 20);
        java.text.DateFormat df = DateFormat.getDateFormat(Controller.getInstance().getApplicationContext());
        return df.format(cal.getTime());
    }


    public UserData getUserData() {
        return PreferanceHelper.getInstance().getUserDetails();
    }

    public String getUid() {
        if (getUserData() == null) return "";
        return getUserData().getUid();
    }

    public String getLoginUserType() {
        if (getUserData() == null) return "";
        return getUserData().getUserType();
    }

    public String getUsername() {
        if (getUserData() == null) return "";
        return getUserData().getUsername();
    }

   /* public static void updateBadge() {
        RoomDB.getRecentChatCount(counter -> {
            if (counter <= 0) {
                ShortcutBadger.removeCount(Controller.getInstance().getApplicationContext()); //Remove launcher badge
            } else {
                ShortcutBadger.applyCount(Controller.getInstance().getApplicationContext(), counter); //for 1.1.4+
            }
        });
    }
*/

    public String compareUidWithCurrentUser(String receiverUserUid) {
        String currentUserUid = getUid();
        int result = currentUserUid.compareTo(receiverUserUid);
        if (result < 0)
            return currentUserUid + "-" + receiverUserUid;
        else
            return receiverUserUid + "-" + currentUserUid;
    }

    public String getFormattedDate(long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = ", MMMM d";
        final String dateTimeFormatString = "EEEE, MMMM d";

        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            return "Today" + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

}
