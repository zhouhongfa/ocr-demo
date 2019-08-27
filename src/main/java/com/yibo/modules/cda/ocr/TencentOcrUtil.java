package com.yibo.modules.cda.ocr;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yibo.modules.cda.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.*;

/**
 * 调用腾讯ocr接口示例
 *
 * @author zhouhongfa@gz-yibo.com
 * @ClassName TencentOcrTest
 * @Version 1.0
 * @since 2019/8/27 11:31
 */
public class TencentOcrUtil {
    public static final Integer APPID = 2121087126;
    public static final String APPKEY = "wzvdjlzWFtZ59UhE";
    public static final String OCR_API = "https://api.ai.qq.com/fcgi-bin/ocr/ocr_generalocr";

    public static void main(String[] args) {

        String testDirPath = "D:\\zhf\\Documents\\CDA\\WST 483.4-2016健康档案共享文档规范第4部分儿童健康体检";
        String saveFileName = "儿童健康体检文档.xml";
        extractAPdf(testDirPath, saveFileName);
    }

    /**
     * 给定一个目录，然后生成一个xml，目录下都是pdf截出来的图片
     *
     * @param dirPath
     * @param saveFileName 结果的保存文件名，会在 dirPath 下生成
     */
    private static void extractAPdf(String dirPath, String saveFileName) {
        //保存的文件路径拼接
        String saveFilePath = dirPath.concat("/").concat(saveFileName);

        //保存所有解析出来的文本
        List<String> allText = Lists.newLinkedList();
        //读取目录下的所有图片
        List<String> fileNames = FileUtils.getDirectoryFilesName(dirPath);
        //按照指定文件名进行排序，这里我比较时只保留数字，这样保证了读取的顺序是正确的，不过需要按指定的格式保存图片
        fileNames.stream().sorted(Comparator.comparing(o -> o.replaceAll("[-.png]", "")));
        for (String fileName : fileNames) {
            //排除.xml
            if (!fileName.contains(".xml")) {
                //拼出图片路径
                String pngPath = dirPath.concat("/").concat(fileName);
                System.out.println("读取的图片路径：" + pngPath);
                //生成dto
                OcrDto ocrDto = genRequestObj(pngPath);
                //调用api，不过要每1秒钟调一次~
                allText.addAll(callApi(ocrDto));
                System.out.println("调用api中……");
                //睡眠
                ThreadUtil.sleep(3_000);
            }
        }
        System.out.println("开始写入文件");
        //写入文件中
        try {
            Files.write(FileUtils.getPathObj(saveFilePath), allText);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用api，返回解析出来的字符串列表
     *
     * @return
     */
    private static List<String> callApi(OcrDto ocrDto) {
        List<String> strings = Lists.newLinkedList();
        Integer success = 0;
        //如果ret为 -30，则重新调用
        do {
            if (success != 0) {
                //睡一会
                ThreadUtil.sleep(2_000);
                System.out.println("调用失败，准备重新调用……");
            }
            //发请求
            try {
                String post = HttpUtil.post(OCR_API, BeanUtil.beanToMap(ocrDto));
                //转为jsonObject
                JSONObject res = JSON.parseObject(post);
                success = res.getInteger("ret");
                System.out.println(res);
                //获取item_list
                JSONArray itemListArray = res.getJSONObject("data").getJSONArray("item_list");
                for (Object o : itemListArray) {
                    //转为
                    JSONObject j = (JSONObject) o;
                    //取itemstring
                    String itemstring = j.getString("itemstring");
                    strings.add(itemstring);
                }
            } catch (Exception ex) {
                System.err.println("捕获到异常: " + ex.getCause().getMessage());
                success = -1;
//                ex.printStackTrace();
            }


        } while (success != 0);

        return strings;
    }

    //签名
    private static String sign(OcrDto ocrDto) {
        //先转 Map
        Map<String, Object> map = BeanUtil.beanToMap(ocrDto);
        //升序
        TreeMap<String, Object> sortedMap = MapUtil.sort(map);
        //joiner
        StringJoiner sj1 = new StringJoiner("&", "", "");
        //加密
        //去掉为空的value
        sortedMap.forEach((k, v) -> {
            if (ObjectUtil.isNotEmpty(v)) {
                //URL编码
                try {
                    sj1.add(k.concat("=").concat(URLEncoder.encode(StrUtil.toString(v), "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        //增加appkey
        try {
            sj1.add("app_key".concat("=").concat(URLEncoder.encode(StrUtil.toString(APPKEY), "UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //取md5

        return SecureUtil.md5(sj1.toString()).toUpperCase();
    }


    //生成请求实体
    public static OcrDto genRequestObj(String fileName) {
        OcrDto ocrDto = new OcrDto();
        ocrDto.setApp_id(APPID);
        ocrDto.setNonce_str(RandomUtil.randomString(16));
        ocrDto.setTime_stamp(Integer.valueOf(StrUtil.toString(DateUtil.date().getTime()).substring(0, 10)));
        ocrDto.setImage(base64(fileName));
        //签名
        ocrDto.setSign(sign(ocrDto));

        return ocrDto;
    }

    //转base64
    private static String base64(String fileName) {
        File file = FileUtils.getPathObj(fileName).toFile();

        BufferedImage read = ImgUtil.read(file);

        return ImgUtil.toBase64(read, FileUtil.getType(file));
//        return "test";
    }
}
