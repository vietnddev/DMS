package com.flowiee.dms.common.utils;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoreUtils {
    public static final int NUMERIC = 1;
    public static final int STRING = 2;

    public static String trim(Object obj) {
        return obj == null ? "" : trim(obj.toString());
    }

    public static String trim(String str) {
        return str == null ? "" : trim(str, CoreUtils.STRING);
    }

    public static String trim(String pvValue, int pvType) {
        if (CoreUtils.isNullStr(pvValue))
        {
            if (pvType == CoreUtils.NUMERIC)
                return "0";
            else if (pvType == CoreUtils.STRING)
                return "";
        }
        return pvValue.trim();
    }

    public static boolean isNullStr(Object pStr)
    {
        return CoreUtils.isNullStr(pStr == null ? null : pStr.toString());
    }

    public static boolean isNullStr(String pStr) {
        if (pStr == null)
            return true;
        else
        {
            String pTrimStr = pStr.trim();
            return pTrimStr.length() == 0 || pTrimStr.equalsIgnoreCase("null");
        }
    }

    public static int coalesce(Integer value) {
        return coalesce(value, 0);
    }

    public static int coalesce(Integer value, Integer defaultValue) {
        if (value != null)
            return value;
        if (defaultValue != null)
            return defaultValue;

        return 0;
    }

    public static BigDecimal coalesce(BigDecimal value) {
        return coalesce(value, BigDecimal.ZERO);
    }

    public static BigDecimal coalesce(BigDecimal value, BigDecimal defaultValue) {
        if (value != null)
            return value;
        if (defaultValue != null)
            return defaultValue;

        return BigDecimal.ZERO;
    }

    public static boolean validateEmail(String pEmail) {
        if (!isNullStr(pEmail)) {
            int lvIndexOfAt = pEmail.indexOf("@");
            int lvIndexOfDot = pEmail.lastIndexOf(".");
            if (lvIndexOfAt == -1 || lvIndexOfDot == -1 || 	      // check if no "@" or "."
                    lvIndexOfAt == 0 || 						  // check if start with "@"
                    lvIndexOfDot == pEmail.length() - 1 ||        // check if end with "."
                    lvIndexOfAt != pEmail.lastIndexOf("@") || // check if more than one "@" exist
                    lvIndexOfDot - lvIndexOfAt < 2)
            {
                return false; // Invalid email
            }
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String strNum) {
        if (isNullStr(strNum)) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isAnySpecialCharacter(String pStr) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pStr);
        return m.find();
    }
}