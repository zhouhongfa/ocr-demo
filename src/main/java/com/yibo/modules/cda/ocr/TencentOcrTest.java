package com.yibo.modules.cda.ocr;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yibo.modules.cda.FileUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * TODO
 *
 * @author zhouhongfa@gz-yibo.com
 * @ClassName TencentOcrTest
 * @Version 1.0
 * @since 2019/8/27 11:31
 */
public class TencentOcrTest {
    public static final Integer APPID = 2121087126;
    public static final String APPKEY = "wzvdjlzWFtZ59UhE";
    public static final String OCR_API = "https://api.ai.qq.com/fcgi-bin/ocr/ocr_generalocr";

    public static void main(String[] args) {

        String testFileName = "D:\\zhf\\Documents\\CDA\\WST 483.1-2016健康档案共享文档规范第1部分个人基本健康信息登记\\1-1.png";
        OcrDto ocrDto = genRequestObj(testFileName);
        System.out.println(ocrDto);
        String sign = sign(ocrDto);
        System.out.println(sign);
        ocrDto.setSign(sign);
        //发请求
        String post = HttpUtil.post(OCR_API, BeanUtil.beanToMap(ocrDto));
        System.out.println(post);
        //转为jsonObject
        JSONObject res = JSON.parseObject(post);
        System.out.println(res);
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
//                cn.hutool.core.net.URLEncoder.createDefault().encode()
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
