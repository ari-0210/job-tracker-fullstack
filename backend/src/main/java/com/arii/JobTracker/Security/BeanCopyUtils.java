package com.arii.JobTracker.Security;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

/**
 * 高性能反射对象属性拷贝高级拓展现基类.
 * <p>用于打破原生 Spring BeanUtils 会强行覆盖 null 值的缺陷，实现仅覆盖非空（Non-Null）字段的防洗数据流保护.</p>
 *
 * @author Ari
 */
public class BeanCopyUtils {

    /**
     * 精准提取并拷贝源对象中【非空(Non-Null)】的增量字段到目标对象.
     * <p>常用于 HTTP PUT/PATCH 局部修改业务场景，防止前端传过来的空值在合并时强行擦除数据库已有历史数据.</p>
     *
     * @param source 携带最新局部修改数据的源实例对象 (通常是 DTO)
     * @param target 从数据库提取出来的完整就绪存量目标对象 (通常是 Entity)
     */
    public static void copyNonNullProperties(Object source, Object target) {
        // 利用动态自省反射提取出所有的空属性名，作为 ignoreProperties 传递给底层拷贝引擎
        BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 深度内省扫描目标对象，动态萃取所有值为空(Null)的内部属性字段名称清单.
     *
     * @param source 待解析提取的源数据对象
     * @return 由所有值为 null 的属性名组成的扁平字符串数组，若全不为空则返回空数组
     */
    private static String[] getNullPropertyNames(Object source) {
        // 使用 Spring BeanWrapper 引擎包裹对象，提供低侵入性的属性描述符(PropertyDescriptor)探测
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            // 核心判定准则：若字段为底层映射 null，记录其字段键名，并在后续拷贝中下达免疫指令
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }
}
