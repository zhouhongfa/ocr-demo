package com.yibo.modules.cda.ocr;

import com.google.common.collect.ImmutableMap;
import com.yibo.modules.cda.FileUtils;

import java.util.Map;

/**
 * 使用正则处理转换后的xml，这里处理过后就基本上可以用了
 * （java正则有些不一样吧…后面没用到，手动替换的~）
 *
 * @author zhouhongfa@gz-yibo.com
 * @ClassName XmlRegexCleaner
 * @Version 1.0
 * @since 2019/8/28 9:15
 */
public class XmlRegexCleaner {
    private static ImmutableMap.Builder<String, String> regexBuilder = ImmutableMap.builder();
    private static Map<String, String> regexes = regexBuilder
            .putAll(ImmutableMap.of("regex1", "^\\(|〈|<\\s+", "replace1", "<"))
            .putAll(ImmutableMap.of("regex2", "〉|\\)$", "replace2", ">"))
            .putAll(ImmutableMap.of("regex3", "(?<=<)([a-zA-Z ]+)\\)", "replace3", "$1>"))
            .putAll(ImmutableMap.of("regex4", "(?<=</)([a-zA-Z]+)\\s", "replace4", "$1"))
            .putAll(ImmutableMap.of("regex5", "\\s?(=)\\s?", "replace5", "$1"))
            .putAll(ImmutableMap.of("regex6", "(?<=</?)([a-zA-Z]+$)", "replace6", "$1>"))
            .putAll(ImmutableMap.of("regex7", "</\\s|<\\s/", "replace7", "</"))
            .putAll(ImmutableMap.of("regex8", "\\s/>|/\\s>", "replace8", "/>"))
            .putAll(ImmutableMap.of("regex9", "^<!\\s-+", "replace9", "<!--"))
            .putAll(ImmutableMap.of("regex10", "<!-(?!-)", "replace10", "<!--"))
            .putAll(ImmutableMap.of("regex11", "(?<!-)->$", "replace11", "-->"))
            .putAll(ImmutableMap.of("regex12", "xsi:\\s|xsi\\s:", "replace12", "xsi:"))
            .putAll(ImmutableMap.of("regex13", "\\b'|'\\s?|‘|”", "replace13", "\""))
            .putAll(ImmutableMap.of("regex14", "=\\s\"\\s", "replace14", "=\""))
            .putAll(ImmutableMap.of("regex15", "(<[a-zA-Z]+) (?!xsi:type)(?!/>)(?![a-zA-Z:]+=)", "replace15", "$1"))
            .putAll(ImmutableMap.of("regex16", "(\\d)\\s(\\.)", "replace16", "$1$2"))
            .putAll(ImmutableMap.of("regex17", "(\\d\\.)\\s", "replace17", "$1"))
            .putAll(ImmutableMap.of("regex18", "\\s(\")", "replace18", "$1"))
            .putAll(ImmutableMap.of("regex19", "(=\")\\s", "replace19", "$1"))
            .putAll(ImmutableMap.of("regex20", "(<[a-zA-z]+)\\)", "replace20", "$1>"))
            .putAll(ImmutableMap.of("regex21", "(?<=\")(?=[a-zA-Z]+=)", "replace21", " "))
            .putAll(ImmutableMap.of("regex22", "(=\")\\s", "replace22", "$1"))
            .putAll(ImmutableMap.of("regex23", "(\\s[a-zA-Z]+)\\s([a-zA-Z]+=)", "replace23", "$1$2"))
            .build();

    public static void main(String[] args) {
        String filePath = "D:\\zhf\\Documents\\CDA\\文档\\test\\18住院摘要文档.xml";
        clean(filePath);
    }

    private static void clean(String filePath) {
        //读取所有文本为一个字符串
        String text = FileUtils.readFile2String(filePath);
        //正则替换
        String flushText = dealText(text);

        //写回去
        FileUtils.write2File(flushText, filePath);
    }

    /**
     * 使用正则来替换文本
     *
     * @param text
     * @return
     */
    private static String dealText(String text) {
        String result = text;
        //遍历
        for (int i = 1; i <= regexes.size() / 2; i++) {
            String regexKey = "regex" + i;
            String replaceKey = "replace" + i;
            //取 正则和替换文本
            String regex = regexes.get(regexKey);
            String replace = regexes.get(replaceKey);
            //替换
            result = result.replaceAll(regex, replace);
        }
        return result;
    }
}
