package cn.ouctechnology.oucfly.http;

import okhttp3.FormBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 14:22
 * @description: Http请求的参数实体，所有的请求参数均需要以此种形式出现
 **/
public abstract class HttpParams {
    private Logger logger = LoggerFactory.getLogger(HttpParams.class);

    /**
     * 转化为FormBody，用于Post操作
     *
     * @return
     */
    FormBody toFormBody() {
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                Method readMethod = pd.getReadMethod();
                if (readMethod == null) continue;
                if (pd.getName().equals("class")) continue;
                Object value = pd.getReadMethod().invoke(this);
                if (value == null) value = "";
                if (pd.getClass().getClassLoader() != null) value = value.toString();
                bodyBuilder.add(pd.getName(), value.toString());
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            logger.error("error", e);
        }
        return bodyBuilder.build();
    }


    /**
     * 转化为String，用于get请求
     *
     * @return
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String separator = "";
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for (PropertyDescriptor pd : pds) {
                Method readMethod = pd.getReadMethod();
                if (readMethod == null) continue;
                if (pd.getName().equals("class")) continue;
                Object value = pd.getReadMethod().invoke(this);
                if (value == null) value = "";
                if (pd.getClass().getClassLoader() != null) value = value.toString();
                sb.append(separator).append(pd.getName()).append("=").append(value);
                separator = "&";
            }
        } catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            logger.error("error", e);
        }
        return sb.toString();
    }
}
