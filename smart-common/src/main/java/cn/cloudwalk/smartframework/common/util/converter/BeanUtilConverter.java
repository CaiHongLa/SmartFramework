package cn.cloudwalk.smartframework.common.util.converter;

import cn.cloudwalk.smartframework.common.util.DateUtil;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author LIYANHUI
 */
public class BeanUtilConverter {

    public static void init() {
        BeanUtilsBean.getInstance().getConvertUtils().register(false, true, 0);
        ConvertUtils.register(new UtilDateConvert(), Date.class);
        ConvertUtils.register(new SqlDateConvert(), java.sql.Date.class);
        ConvertUtils.register(new TimestampConvert(), Timestamp.class);
    }

    static class TimestampConvert implements Converter {
        TimestampConvert() {
        }

        @Override
        public <T> T convert(Class<T> arg0, Object arg1) {
            if (arg1 != null) {
                return (T) (arg1.getClass() != Timestamp.class ? new Timestamp(DateUtil.parseDate(arg1 + "").getTime()) : arg1);
            } else {
                return null;
            }
        }
    }

    static class SqlDateConvert implements Converter {
        SqlDateConvert() {
        }

        @Override
        public <T> T convert(Class<T> arg0, Object arg1) {
            if (arg1 != null) {
                return (T) (arg1.getClass() != java.sql.Date.class ? new java.sql.Date(DateUtil.parseDate(arg1 + "").getTime()) : arg1);
            } else {
                return null;
            }
        }
    }

    static class UtilDateConvert implements Converter {
        UtilDateConvert() {
        }

        @Override
        public <T> T convert(Class<T> arg0, Object arg1) {
            if (arg1 != null) {
                return (T) (arg1.getClass() != Date.class ? DateUtil.parseDate(arg1 + "") : arg1);
            } else {
                return null;
            }
        }
    }
}
