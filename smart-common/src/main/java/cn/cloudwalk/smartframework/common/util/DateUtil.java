package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author LIYANHUI
 */
public class DateUtil {

    public static int compareDate(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime()) {
            return 1;
        } else {
            return date1.getTime() == date2.getTime() ? 0 : -1;
        }
    }

    public static int compareDate(String date1, DateUtil.DATE_PATTERN date1Pattern, String date2, DateUtil.DATE_PATTERN date2Pattern) {
        Date _date1 = parseDate(date1, date1Pattern);
        Date _date2 = parseDate(date2, date2Pattern);
        return compareDate(_date1, _date2);
    }

    public static java.sql.Date toSqlDate(String date, DateUtil.DATE_PATTERN datePattern) {
        return new java.sql.Date(parseDate(date, datePattern).getTime());
    }

    public static Timestamp toTimestamp(String date, DateUtil.DATE_PATTERN datePattern) {
        return new Timestamp(parseDate(date, datePattern).getTime());
    }

    public static Date parseDate(String date) {
        if (date == null) {
            return null;
        } else {
            date = date.trim();
            return !date.contains("中国标准时间") && !date.contains("CST") ? parseDate(date, getPatternBySample(date)) : new Date(Date.parse(date));
        }
    }

    public static Date parseDate(String date, DateUtil.DATE_PATTERN pattern) {
        try {
            if ((pattern == DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss_SSS || pattern == DateUtil.DATE_PATTERN.yyyy_MM_dd_HH_mm_ss_SSS2) && date != null) {
                int index = date.lastIndexOf(".");
                if (index != -1) {
                    date = date.substring(0, index + 4);
                }
            }

            DateFormat df = new SimpleDateFormat(pattern.toString());
            return df.parse(date);
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static String formatDate(String srcDateStr, DateUtil.DATE_PATTERN srcDatePattern, DateUtil.DATE_PATTERN targetDatePattern) {
        try {
            Date srcDate = parseDate(srcDateStr, srcDatePattern);
            DateFormat df2 = new SimpleDateFormat(targetDatePattern.toString());
            return df2.format(srcDate);
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static String formatDate(Date date, DateUtil.DATE_PATTERN datePattern) {
        DateFormat df = new SimpleDateFormat(datePattern.toString());
        return df.format(date);
    }

    public static DateUtil.DATE_PATTERN getPatternBySample(String sample) {
        return DateUtil.DATE_PATTERN.getPatternBySample(sample);
    }

    public static boolean isOverlap(Date leftStartDate, Date leftEndDate, Date rightStartDate, Date rightEndDate) {
        return ((leftStartDate.getTime() >= rightStartDate.getTime())
                && leftStartDate.getTime() < rightEndDate.getTime())
                ||
                ((leftStartDate.getTime() > rightStartDate.getTime())
                        && leftStartDate.getTime() <= rightEndDate.getTime())
                ||
                ((rightStartDate.getTime() >= leftStartDate.getTime())
                        && rightStartDate.getTime() < leftEndDate.getTime())
                ||
                ((rightStartDate.getTime() > leftStartDate.getTime())
                        && rightStartDate.getTime() <= leftEndDate.getTime());
    }

    public enum DATE_PATTERN {
        yyyyMMdd("yyyyMMdd", "^\\d{2,4}\\d{1,2}\\d{1,2}$"),
        yyyy_MM("yyyy/MM", "^\\d{2,4}/\\d{1,2}$"),
        yyyy_MM2("yyyy-MM", "^\\d{2,4}-\\d{1,2}$"),
        yyyy_MM_dd("yyyy/MM/dd", "^\\d{2,4}/\\d{1,2}/\\d{1,2}$"),
        yyyy_MM_dd2("yyyy-MM-dd", "^\\d{2,4}-\\d{1,2}-\\d{1,2}$"),
        yyyy_MM_dd_HH_mm("yyyy/MM/dd HH:mm", "^\\d{2,4}/\\d{1,2}/\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}$"),
        yyyy_MM_dd_HH_mm2("yyyy-MM-dd HH:mm", "^\\d{2,4}-\\d{1,2}-\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}$"),
        yyyy_MM_dd_HH_mm_ss("yyyy/MM/dd HH:mm:ss", "^\\d{2,4}/\\d{1,2}/\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}$"),
        yyyy_MM_dd_HH_mm_ss2("yyyy-MM-dd HH:mm:ss", "^\\d{2,4}-\\d{1,2}-\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}$"),
        yyyy_MM_dd_HH_mm_ss_S("yyyy/MM/dd HH:mm:ss.S", "^\\d{2,4}/\\d{1,2}/\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1}$"),
        yyyy_MM_dd_HH_mm_ss_S2("yyyy-MM-dd HH:mm:ss.S", "^\\d{2,4}-\\d{1,2}-\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1}$"),
        yyyy_MM_dd_HH_mm_ss_SS("yyyy/MM/dd HH:mm:ss.SS", "^\\d{2,4}/\\d{1,2}/\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{2}$"),
        yyyy_MM_dd_HH_mm_ss_SS2("yyyy-MM-dd HH:mm:ss.SS", "^\\d{2,4}-\\d{1,2}-\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{2}$"),
        yyyy_MM_dd_HH_mm_ss_SSS("yyyy/MM/dd HH:mm:ss.SSS", "^\\d{2,4}/\\d{1,2}/\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3,}$"),
        yyyy_MM_dd_HH_mm_ss_SSS2("yyyy-MM-dd HH:mm:ss.SSS", "^\\d{2,4}-\\d{1,2}-\\d{1,2}\\s.{1,2}\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{3,}$");

        private String value;
        private String pattern;

        DATE_PATTERN(String value, String pattern) {
            this.value = value;
            this.pattern = pattern;
        }

        public static DateUtil.DATE_PATTERN getPatternBySample(String date) {
            if (date != null) {
                date = date.trim();
                DateUtil.DATE_PATTERN[] patterns = values();

                for (DATE_PATTERN value : patterns) {
                    if (date.matches(value.pattern)) {
                        return value;
                    }
                }
            }

            throw new FrameworkInternalSystemException(new SystemExceptionDesc("date is null or not support format：" + date));
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
