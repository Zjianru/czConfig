package com.cz.config.client.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.util.StringUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

/**
 * 占位符助手类，用于处理和解析Spring属性配置中的占位符。
 *
 * @author Zjianru
 */
public class PlaceholderHelper {


    /**
     * 占位符的前缀。
     */
    private static final String PLACEHOLDER_PREFIX = "${";
    /**
     * 占位符的后缀。
     */
    private static final String PLACEHOLDER_SUFFIX = "}";
    /**
     * 占位符中值的分隔符。
     */
    private static final String VALUE_SEPARATOR = ":";
    /**
     * 简单占位符的前缀。
     */
    private static final String SIMPLE_PLACEHOLDER_PREFIX = "{";
    /**
     * 表达式的前缀。
     */
    private static final String EXPRESSION_PREFIX = "#{";
    /**
     * 表达式的后缀。
     */
    private static final String EXPRESSION_SUFFIX = "}";

    /**
     * 占位符助手的私有构造方法，防止外部实例化。
     */
    private PlaceholderHelper() {
    }

    /**
     * 占位符助手的单例实例。
     */
    private static PlaceholderHelper INSTANCE = new PlaceholderHelper();

    /**
     * 获取占位符助手的单例实例。
     *
     * @return 占位符助手实例。
     */
    public static PlaceholderHelper getInstance() {
        return INSTANCE;
    }

    /**
     * 解析属性字符串中的占位符值。
     *
     * @param beanFactory Bean工厂，用于解析占位符。
     * @param beanName    Bean的名称，可能在解析过程中需要。
     * @param placeholder 带有占位符的属性字符串。
     * @return 解析后的值。
     */
    public Object resolvePropertyValue(ConfigurableBeanFactory beanFactory, String beanName, String placeholder) {
        // 解析包含占位符的字符串
        String strVal = beanFactory.resolveEmbeddedValue(placeholder);
        // 获取指定Bean的定义，可能在解析过程中需要
        BeanDefinition bd = (beanFactory.containsBean(beanName) ? beanFactory
                .getMergedBeanDefinition(beanName) : null);
        // 评估和解析Bean定义中的字符串表达式
        return evaluateBeanDefinitionString(beanFactory, strVal, bd);
    }

    /**
     * 评估和解析Bean定义中的字符串表达式。
     *
     * @param beanFactory    Bean工厂，用于解析表达式。
     * @param value          需要评估的字符串。
     * @param beanDefinition Bean定义，可能在解析过程中需要。
     * @return 解析后的值。
     */
    private Object evaluateBeanDefinitionString(ConfigurableBeanFactory beanFactory, String value,
                                                BeanDefinition beanDefinition) {
        // 如果Bean工厂没有Bean表达式解析器，则直接返回原始值
        if (beanFactory.getBeanExpressionResolver() == null) {
            return value;
        }
        // 根据Bean定义的范围获取相应的作用域
        Scope scope = (beanDefinition != null ? beanFactory
                .getRegisteredScope(beanDefinition.getScope()) : null);
        // 使用Bean表达式解析器评估字符串表达式
        return beanFactory.getBeanExpressionResolver()
                .evaluate(value, new BeanExpressionContext(beanFactory, scope));
    }

    /**
     * 从给定的属性字符串中提取占位符键。
     * e.g.
     * <ul>
     * <li>${some.key} => "some.key"</li>
     * <li>${some.key:${some.other.key:100}} => "some.key", "some.other.key"</li>
     * <li>${${some.key}} => "some.key"</li>
     * <li>${${some.key:other.key}} => "some.key"</li>
     * <li>${${some.key}:${another.key}} => "some.key", "another.key"</li>
     * <li>#{new java.text.SimpleDateFormat('${some.key}').parse('${another.key}')} => "some.key", "another.key"</li>
     * </ul>
     *
     * @param propertyString 包含占位符的属性字符串。
     * @return 一个包含所有占位符键的集合。
     */
    public Set<String> extractPlaceholderKeys(String propertyString) {
        // 使用LinkedHashSet保持插入顺序并避免重复
        Set<String> placeholderKeys = new LinkedHashSet<>();

        // 检查是否是规范的占位符或表达式，如果不是，则直接返回空集合
        if (!isNormalizedPlaceholder(propertyString) && !isExpressionWithPlaceholder(propertyString)) {
            return placeholderKeys;
        }

        // 使用栈来进行递归解析
        Stack<String> stack = new Stack<>();
        stack.push(propertyString);

        while (!stack.isEmpty()) {
            String strVal = stack.pop();
            int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
            if (startIndex == -1) {
                // 如果不包含占位符前缀，则作为占位符键添加
                placeholderKeys.add(strVal);
                continue;
            }
            int endIndex = findPlaceholderEndIndex(strVal, startIndex);
            if (endIndex == -1) {
                // 如果找不到占位符后缀，则跳过
                continue;
            }

            String placeholderCandidate = strVal.substring(startIndex + PLACEHOLDER_PREFIX.length(), endIndex);

            // 如果是嵌套的占位符，则递归解析
            if (placeholderCandidate.startsWith(PLACEHOLDER_PREFIX)) {
                stack.push(placeholderCandidate);
            } else {
                // 否则，处理占位符的默认值部分
                int separatorIndex = placeholderCandidate.indexOf(VALUE_SEPARATOR);
                if (separatorIndex == -1) {
                    stack.push(placeholderCandidate);
                } else {
                    stack.push(placeholderCandidate.substring(0, separatorIndex));
                    String defaultValuePart =
                            normalizeToPlaceholder(placeholderCandidate.substring(separatorIndex + VALUE_SEPARATOR.length()));
                    if (StringUtils.hasText(defaultValuePart)) {
                        stack.push(defaultValuePart);
                    }
                }
            }

            // 处理占位符后的剩余部分
            if (endIndex + PLACEHOLDER_SUFFIX.length() < strVal.length() - 1) {
                String remainingPart = normalizeToPlaceholder(strVal.substring(endIndex + PLACEHOLDER_SUFFIX.length()));
                if (!StringUtils.hasText(remainingPart)) {
                    stack.push(remainingPart);
                }
            }
        }

        return placeholderKeys;
    }

    /**
     * 检查字符串是否是规范的占位符，即以${开头并以}结尾。
     *
     * @param propertyString 要检查的字符串。
     * @return 如果是规范的占位符，则为true；否则为false。
     */
    private boolean isNormalizedPlaceholder(String propertyString) {
        return propertyString.startsWith(PLACEHOLDER_PREFIX) && propertyString.endsWith(PLACEHOLDER_SUFFIX);
    }

    /**
     * 检查字符串是否是包含占位符的表达式，即以#{开头，以}结尾，并且中间包含${。
     *
     * @param propertyString 要检查的字符串。
     * @return 如果是包含占位符的表达式，则为true；否则为false。
     */
    private boolean isExpressionWithPlaceholder(String propertyString) {
        return propertyString.startsWith(EXPRESSION_PREFIX) && propertyString.endsWith(EXPRESSION_SUFFIX)
                && propertyString.contains(PLACEHOLDER_PREFIX);
    }

    /**
     * 根据占位符前缀和后缀提取字符串中的占位符。
     * 如果字符串不包含占位符，则返回null。
     * 占位符定义为前缀和后缀之间的部分，支持嵌套占位符。
     *
     * @param strVal 待处理的字符串
     * @return 提取出的占位符，如果不存在占位符则返回null
     */
    private String normalizeToPlaceholder(String strVal) {
        // 查找占位符前缀的起始位置
        int startIndex = strVal.indexOf(PLACEHOLDER_PREFIX);
        // 如果没有找到占位符前缀，返回null
        if (startIndex == -1) {
            return null;
        }
        // 查找占位符后缀的结束位置
        int endIndex = strVal.lastIndexOf(PLACEHOLDER_SUFFIX);
        // 如果没有找到占位符后缀，返回null
        if (endIndex == -1) {
            return null;
        }
        // 提取占位符，包括前缀和后缀
        return strVal.substring(startIndex, endIndex + PLACEHOLDER_SUFFIX.length());
    }

    /**
     * 在给定的字符序列中找到占位符的结束位置。
     * 支持嵌套占位符，即一个占位符内部可以包含其他占位符。
     *
     * @param buf        待搜索的字符序列
     * @param startIndex 占位符前缀的起始位置
     * @return 占位符的结束位置，如果找不到则返回-1
     */
    private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
        // 从占位符前缀的结束位置开始搜索
        int index = startIndex + PLACEHOLDER_PREFIX.length();
        // 记录当前嵌套占位符的深度
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            // 检查当前位置是否为占位符后缀的开始
            if (StringUtils.substringMatch(buf, index, PLACEHOLDER_SUFFIX)) {
                // 如果在嵌套的占位符内部，则减少嵌套深度并继续搜索下一个位置
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + PLACEHOLDER_SUFFIX.length();
                } else {
                    // 如果不在嵌套的占位符内部，则找到了占位符的结束位置
                    return index;
                }
            } else if (StringUtils.substringMatch(buf, index, SIMPLE_PLACEHOLDER_PREFIX)) {
                // 如果当前位置为简单占位符的前缀，则增加嵌套深度并继续搜索下一个位置
                withinNestedPlaceholder++;
                index = index + SIMPLE_PLACEHOLDER_PREFIX.length();
            } else {
                // 如果当前位置不是占位符的一部分，则继续搜索下一个位置
                index++;
            }
        }
        // 如果搜索结束仍未找到占位符的结束位置，则返回-1
        return -1;
    }


    public static void main(String[] args) {
        String strVal = "${some.key:other.key}";
        System.out.println(new PlaceholderHelper().extractPlaceholderKeys(strVal));
        strVal = "${some.key:${some.other.key:100}}";
        System.out.println(new PlaceholderHelper().extractPlaceholderKeys(strVal));
        strVal = "${${some.key}}";
        System.out.println(new PlaceholderHelper().extractPlaceholderKeys(strVal));
        strVal = "${${some.key:other.key}}";
        System.out.println(new PlaceholderHelper().extractPlaceholderKeys(strVal));
        strVal = "${${some.key}:${another.key}}";
        System.out.println(new PlaceholderHelper().extractPlaceholderKeys(strVal));
    }

}

