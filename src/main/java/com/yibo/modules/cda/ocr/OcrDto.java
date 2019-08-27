package com.yibo.modules.cda.ocr;

import lombok.Data;

/**
 * TODO
 *
 * @author zhouhongfa@gz-yibo.com
 * @ClassName OcrDto
 * @Version 1.0
 * @since 2019/8/27 11:36
 */
@Data
public class OcrDto {
    //应用标识（AppId）
    private Integer app_id;
    //请求时间戳（秒级）
    private Integer time_stamp;
    //随机字符串
    private String nonce_str;
    //非空且长度固定32字节
    private String sign;
    //原始图片的base64编码数据
    private String image;
}
