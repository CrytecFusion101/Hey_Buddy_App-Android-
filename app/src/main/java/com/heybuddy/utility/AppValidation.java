package com.heybuddy.utility;

/**
 * Created by Dhara on 01/01/2019.
 */
public class AppValidation {
    static String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
//test
  /*  static String emailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";*/
    static String birthDatePattern = "^\\d{4}\\-(0?[1-9]|1[012])\\-(0?[1-9]|[12][0-9]|3[01])$";

    //EmptyFieldValidation
    public static boolean isEmptyFieldValidate(String strField) {
        boolean isEmptyField = false;
        if (strField.isEmpty())
            isEmptyField = true;
        return isEmptyField;
    }

    /*//BirthDateValidation
    public static boolean isBirthDateValidate(String birthdate) {
        boolean isValid = false;

        if (birthdate.matches(birthDatePattern)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }
*/

    //EmailValidation
    public static boolean isEmailValidate(String email) {
        boolean isValid = false;

        if (email.matches(emailPattern)) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    //PasswordValidation
    public static boolean isPasswordValidate(String password) {
        boolean isValid = false;
        if (password.length() >= 6  && password.length() <= 16 ) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

    //NumberValidation
    public static boolean isNumberValidate(String number) {
        boolean isValid = false;
        if (number.length() > 0 && number.length() == 10) {
            isValid = true;
        } else {
            isValid = false;
        }
        return isValid;
    }

}
