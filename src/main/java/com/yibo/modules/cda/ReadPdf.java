package com.yibo.modules.cda;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReadPdf {

    public static void main(String[] args) throws IOException {

        try (PDDocument document = PDDocument.load(new File("D:\\zhf\\Documents\\CDA\\WST 483 健康档案共享文档规范\\WST 483.2-2016健康档案共享文档规范第2部分出生医学证明.pdf"))) {

            document.getClass();

            if (!document.isEncrypted()) {
                // 获取页码
                int pages = document.getNumberOfPages();

                // 读文本内容
                PDFTextStripper stripper = new PDFTextStripper();
                // 设置按顺序输出
                stripper.setSortByPosition(true);
                stripper.setStartPage(1);
                stripper.setEndPage(pages);
                String content = stripper.getText(document);
                System.out.println(content);
                //写到测试文件中
                String filePath = "./test.txt";
                Path path = FileSystems.getDefault().getPath(filePath);
                Files.write(path, content.getBytes());
            }

        }

    }
}