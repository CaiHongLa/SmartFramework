package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import cn.cloudwalk.smartframework.common.exception.exception.SystemException;
import cn.cloudwalk.smartframework.common.util.formatter.DDLFormatter;
import cn.cloudwalk.smartframework.common.util.formatter.DMLFormatter;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import io.netty.handler.codec.http.QueryStringDecoder;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Blob;
import java.util.*;

/**
 * @author LIYANHUI
 */
public class TextUtil {

    @SuppressWarnings("unchecked")
    public static <T> T format(Number number, TextUtil.NUMBER_PATTERN pattern, Class<T> returnType) {
        String rs = String.format(pattern.toString(), number);
        if (returnType == Float.class) {
            return (T) Float.valueOf(rs);
        } else if (returnType == Double.class) {
            return (T) Double.valueOf(rs);
        } else {
            throw new RuntimeException("not support returnType");
        }
    }

    public static String getFullPinyin(String text, String splitChar) {
        return PinyinHelper.convertToPinyinString(text, splitChar, PinyinFormat.WITHOUT_TONE).toUpperCase();
    }

    public static String getShortPinyin(String text) {
        return PinyinHelper.getShortPinyin(text).toUpperCase();
    }

    public static String substring(String src, int maxLength) {
        return src != null && src.length() > maxLength ? src.substring(0, maxLength) + "..." : src;
    }

    public static String deleteEndString(String src, String endStr) {
        String tmp = src.trim();
        return tmp.endsWith(endStr) ? tmp.substring(0, tmp.length() - endStr.length()) : tmp;
    }

    public static String resolve(String src, Map<String, String> varMapping) {
        String title = src;
        int count = 1000;

        while (count-- > 0) {
            int posi = title.indexOf("${");
            if (posi == -1) {
                break;
            }

            int endPosi = -1;

            for (int i = posi; i < title.length(); ++i) {
                if (title.charAt(i) == '}') {
                    endPosi = i;
                    break;
                }
            }

            String varName = title.substring(posi + 2, endPosi);
            if (varMapping.containsKey(varName)) {
                title = title.replaceFirst("\\$\\{" + varName + "\\}", varMapping.get(varName));
            }
        }

        return title;
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    public static boolean isEmpty(String text) {
        if (text == null) {
            return true;
        } else {
            text = text.trim().toLowerCase().replaceAll("'|\"", "");
            return "".equals(text) || "null".equals(text);
        }
    }

    public static String array2StringWithoutQuotes(List<String> arr) {
        if (arr != null && arr.size() > 0) {
            String rs = "";

            String ele;
            for (Iterator var2 = arr.iterator(); var2.hasNext(); rs = rs + ele + ",") {
                ele = (String) var2.next();
            }

            rs = rs.substring(0, rs.length() - 1);
            return rs;
        } else {
            return null;
        }
    }

    public static String array2StringWithoutQuotes(String[] arr) {
        return arr != null ? array2StringWithoutQuotes(Arrays.asList(arr)) : null;
    }

    public static String array2String(List<String> arr) {
        if (arr != null && arr.size() > 0) {
            String rs = "";

            String item;
            for (Iterator var2 = arr.iterator(); var2.hasNext(); rs = rs + "'" + item + "',") {
                item = (String) var2.next();
            }

            if (rs.length() >= 1) {
                rs = rs.substring(0, rs.length() - 1);
            }

            return rs;
        } else {
            return null;
        }
    }

    public static String array2String(String[] arr) {
        if (arr == null) {
            return null;
        } else {
            String rs;
            StringBuilder rsBuilder = new StringBuilder();
            for (String item : arr) {
                rsBuilder.append("'").append(item).append("',");
            }
            rs = rsBuilder.toString();

            if (rs.length() >= 1) {
                rs = rs.substring(0, rs.length() - 1);
            }

            return rs;
        }
    }

    public static String array2String(String[] arr, char splitChar) {
        if (arr == null) {
            return null;
        } else {
            StringBuilder rsBuilder = new StringBuilder();
            for (String item : arr) {
                rsBuilder.append(item).append(splitChar);
            }
            String rs = rsBuilder.toString();

            if (rs.length() >= 1) {
                rs = rs.substring(0, rs.length() - 1);
            }

            return rs;
        }
    }

    public static String formatVariableWithUpper(String varName) {
        if (varName == null) {
            return null;
        } else {
            varName = varName.toLowerCase();
            StringBuilder newName = new StringBuilder();
            String[] nameArray = varName.split("_");

            for (String part : nameArray) {
                newName.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
            }

            return newName.toString();
        }
    }

    public static String unFormatVariableWithUpper(String varName) {
        StringBuilder words = new StringBuilder();

        for (int i = 0; i < varName.length(); ++i) {
            char c = varName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    words.append("_");
                }

                words.append(Character.toLowerCase(c));
            } else {
                words.append(c);
            }
        }

        return words.toString();
    }

    public static String formatVariableWithLower(String varName) {
        if (varName == null) {
            return null;
        } else {
            varName = varName.toLowerCase();
            StringBuilder newName = new StringBuilder();
            String[] nameArray = varName.split("_");

            for (String part : nameArray) {
                newName.append(part.substring(0, 1).toUpperCase()).append(part.substring(1));
            }

            newName = new StringBuilder(newName.substring(0, 1).toLowerCase() + newName.substring(1));
            return newName.toString();
        }
    }

    public static String unFormatVariableWithLower(String varName) {
        StringBuilder words = new StringBuilder();

        for (int i = 0; i < varName.length(); ++i) {
            char c = varName.charAt(i);
            if (Character.isUpperCase(c)) {
                words.append("_").append(Character.toLowerCase(c));
            } else {
                words.append(c);
            }
        }

        return words.toString();
    }

    public static String formatSql4DMLOrDQL(String source) {
        return (new DMLFormatter()).format((source + "").trim().replaceAll("\n", ""));
    }

    public static String formatSql4DDL(String source) {
        return (new DDLFormatter()).format((source + "").trim().replaceAll("\n", ""));
    }

    public static String getExceptionDetail(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }

    public static String blob2String(Blob blob) {
        try {
            StringBuilder content = new StringBuilder();
            InputStream is = blob.getBinaryStream();
            byte[] tmp = new byte[512];

            int len;
            while ((len = is.read(tmp)) != -1) {
                content.append(new String(tmp, 0, len));
            }

            return content.toString();
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static String byte2Hex(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for (byte aData : data) {
            String hex = Integer.toHexString(aData & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    public static byte[] hex2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hexStr.length() / 2];

            for (int i = 0; i < hexStr.length() / 2; ++i) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }

            return result;
        }
    }

    public static String pad(String src, int length, char padChar, TextUtil.PAD_DIRECTION direction) {
        if (src != null && src.length() < length) {
            int lackLen = length - src.length();
            StringBuilder padStr = new StringBuilder();

            for (int i = 0; i < lackLen; ++i) {
                padStr.append(padChar);
            }

            if (direction == TextUtil.PAD_DIRECTION.LEFT) {
                return padStr + src;
            }

            if (direction == TextUtil.PAD_DIRECTION.RIGHT) {
                return src + padStr;
            }
        }

        return src;
    }

    public static String unpad(String src, char padChar, TextUtil.PAD_DIRECTION direction) {
        if (src != null && src.indexOf(padChar) != -1) {
            StringBuilder newSrc = new StringBuilder();
            boolean next;
            int i;
            char c;
            if (direction == TextUtil.PAD_DIRECTION.LEFT) {
                next = true;

                for (i = 0; i < src.length(); ++i) {
                    c = src.charAt(i);
                    if (next && c == padChar) {
                        if (i == src.length() - 1) {
                            newSrc.append(c);
                            break;
                        }
                    } else {
                        newSrc.append(c);
                        next = false;
                    }
                }
            } else if (direction == TextUtil.PAD_DIRECTION.RIGHT) {
                next = true;

                for (i = src.length() - 1; i >= 0; --i) {
                    c = src.charAt(i);
                    if (next && c == padChar) {
                        if (i == 0) {
                            newSrc.append(c);
                            break;
                        }
                    } else {
                        newSrc.append(c);
                        next = false;
                    }
                }
            }

            return newSrc.toString();
        } else {
            return src;
        }
    }

    public static String getFileExtName(String fileName) {
        if (fileName != null) {
            int posi = fileName.lastIndexOf(".");
            return fileName.substring(posi + 1);
        } else {
            return null;
        }
    }

    public static String getGetterPropertyName(String getterName) {
        String propertyName = getterName.replaceFirst("get", "");
        return propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
    }

    public static String getRequestParamsAsString(HttpServletRequest request) {
        StringBuilder tmp = new StringBuilder();
        if (request != null) {

            for (Map.Entry<String, String[]> item : request.getParameterMap().entrySet()) {
                tmp.append(item.getKey()).append("=").append(Arrays.toString(item.getValue())).append(",");
            }

            if (tmp.length() > 0) {
                tmp = tmp.deleteCharAt(tmp.length() - 1);
            }
        }

        return tmp.toString();
    }

    public static String getSimplifyStackTrace(Throwable throwable) {
        try {
            StackTraceElement[] traces = throwable.getStackTrace();
            int size = traces.length;
            if (size == 0) {
                return null;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(throwable.toString()).append("\n\tat ").append(traces[0]).append("\n");

                for (int index = 1; index < size; ++index) {
                    String trace = traces[index].toString();
                    if (trace.startsWith("cn.cloudwalk")) {
                        sb.append("\tat ").append(trace).append("\n");
                    }
                }

                return sb.toString();
            }
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static String getStackTrace(Throwable throwable) {
        try {
            if (throwable != null) {
                Writer writer = new StringWriter();
                PrintWriter printWriter = new PrintWriter(writer);
                throwable.printStackTrace(printWriter);
                printWriter.close();
                writer.close();
                return writer.toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static String getCurrentStackTrace() {
        return getCurrentStackTrace(null);
    }

    public static String getCurrentStackTrace(String[] startsWith) {
        StringBuilder content = new StringBuilder();
        StackTraceElement[] eles = Thread.currentThread().getStackTrace();
        if (eles != null && eles.length > 0) {
            for (StackTraceElement ele : eles) {
                String line = ele + "";
                if (startsWith != null) {
                    for (String st : startsWith) {
                        if (line.startsWith(st)) {
                            content.append(line).append("\n");
                        }
                    }
                } else {
                    content.append(line).append("\n");
                }
            }
        }

        return content.toString();
    }

    public static SystemExceptionDesc getDeepestSystemExceptionDesc(SystemException ex) {
        if (ex != null && ex.getDesc() != null) {
            Throwable thr = ex.getDesc().getThrowable();
            if (thr == null) {
                return ex.getDesc();
            } else {
                if (thr instanceof SystemException) {
                    SystemException se = (SystemException) thr;
                    if (se.getDesc() != null) {
                        return getDeepestSystemExceptionDesc(se);
                    }
                }

                return ex.getDesc();
            }
        } else {
            return null;
        }
    }

    public static Map<String, Object> parseUrlParams(String uri) {
        Map<String, Object> paramsMap = new HashMap<>();
        Map<String, List<String>> originalParams = (new QueryStringDecoder(uri)).parameters();
        if (originalParams != null && originalParams.size() > 0) {

            for (Map.Entry<String, List<String>> param : originalParams.entrySet()) {
                List<String> pv = param.getValue();
                if (pv != null && pv.size() > 0) {
                    if (pv.size() == 1) {
                        paramsMap.put(param.getKey(), pv.get(0));
                    } else {
                        paramsMap.put(param.getKey(), pv);
                    }
                }
            }
        }

        return paramsMap;
    }

    public static boolean matchRESTfulUrl(String srcUri, String uriPattern) {
        return matchRESTfulUrl(srcUri, uriPattern, TextUtil.MatchStrategy.STRICT);
    }

    public static boolean matchRESTfulUrl(String srcUri, String uriPattern, TextUtil.MatchStrategy strategy) {
        if (srcUri != null && uriPattern != null) {
            srcUri = srcUri.replaceAll("//", "/");
            uriPattern = uriPattern.replaceAll("//", "/");
            String[] srcUriEles = srcUri.split("/");
            String[] uriPatternEles = uriPattern.split("/");
            if (strategy == TextUtil.MatchStrategy.STRICT && srcUriEles.length != uriPatternEles.length) {
                return false;
            } else {
                for (int i = 0; i < uriPatternEles.length; ++i) {
                    String patternEle = uriPatternEles[i];
                    if (!patternEle.matches("\\{.*\\}") && i < srcUriEles.length && !patternEle.equals(srcUriEles[i])) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public static Map<String, String> parseRESTfulUrlParams(String srcUri, String uriPattern) {
        Map<String, String> pathParams = new HashMap<>();
        if (srcUri != null && uriPattern != null) {
            srcUri = srcUri.replaceAll("//", "/");
            uriPattern = uriPattern.replaceAll("//", "/");
            String[] srcUriEles = srcUri.split("/");
            String[] uriPatternEles = uriPattern.split("/");

            for (int i = 0; i < uriPatternEles.length; ++i) {
                String item = uriPatternEles[i];
                if (item.matches("\\{.*}") && srcUriEles.length >= i + 1) {
                    String paramName = item.replaceAll("[{}]", "");
                    pathParams.put(paramName, srcUriEles[i]);
                }
            }
        }

        return pathParams;
    }

    public enum MatchStrategy {
        SIMPLE,
        STRICT;

        MatchStrategy() {
        }
    }

    public enum NUMBER_PATTERN {
        ROUND_TO_3DECIMALS("%1$.3f"),
        ROUND_TO_2DECIMALS("%1$.2f");

        private String value;

        NUMBER_PATTERN(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum PAD_DIRECTION {
        LEFT,
        RIGHT;

        PAD_DIRECTION() {
        }
    }
}
