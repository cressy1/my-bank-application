package com.martins.martinsbank.util;

import java.time.Year;

public class AccountUtils {
    public static final String ACCOUNT_EXISTS_CODE="001";
    public static final String ACCOUNT_EXISTS_MESSAGE="This user already exists";
    public static final String ACCOUNT_CREATION_SUCCESS= "002";
    public static final String ACCOUNT_CREATION_SUCCESS_MESSAGE= "Account created successfully";
    public static final String ACCOUNT_NOT_EXISTS_CODE= "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE= "Account doesn't exist";
    public static final String ACCOUNT_FOUND_CODE= "004";
    public static final String ACCOUNT_FOUND_SUCCESS= "Account found";
    public static final String ACCOUNT_CREDITED_SUCCESS = "005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "Account credited Successfully";
    public static final String INSUFFICIENT_BALANCE_CODE = "006";
    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient balance";
    public static final String ACCOUNT_DEBITED_SUCCESS = "007";
    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account debited Successfully";
    public static final String TRANSFER_SUCCESSFUL_CODE = "008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer Successful";





    public static String generateAccountNumber() {
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        int randNumber = (int) Math.floor(Math.random() * (max-min + 1) + min);
        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();
        return accountNumber.append(year).append(randomNumber).toString();


    }
}
