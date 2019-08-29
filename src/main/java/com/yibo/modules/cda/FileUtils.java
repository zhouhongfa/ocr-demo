package com.yibo.modules.cda;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.List;

/**
 * 文件相关工具类
 *
 * @author zhouhongfa@gz-yibo.com
 * @ClassName FileUtils
 * @Version 1.0
 * @since 2019/7/19 14:08
 */
public class FileUtils {


    /**
     * 根据包路径返回对应的文件夹路径
     *
     * @param packagePath
     * @return
     */
    public static final String genPackageFilePath(String packagePath) {
        String res = "/".concat(packagePath.replaceAll("\\.", "/")).concat("/");
        return res;
    }

    /**
     * 创建目录
     *
     * @param dirPath
     */
    public static final void createDir(String dirPath) {
        createDir(getPathObj(dirPath));
    }

    /**
     * 创建目录
     *
     * @param path
     */
    public static final void createDir(Path path) {
        //判断是否有该目录
        boolean exists = Files.exists(path);
        if (!exists) {
            try {
                //创建出所有层级目录
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 创建文件
     *
     * @param path
     */
    public static final void createFile(Path path) {
        //判断是否有该文件
        boolean exists = Files.exists(path);
        if (!exists) {
            try {
                //创建父目录
                createDir(path.getParent());
                //创建文件
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据指定内容写入到文件中
     *
     * @param in
     * @param savePath
     * @return
     */
    public static String write(String in, Path savePath) {
        String res = "ok";
        try {
            Files.write(savePath, in.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            res = e.getCause().getMessage();
        }
        return res;
    }

    /**
     * 根据指定内容写入到指定文件中，如果不存在则创建
     *
     * @param in
     * @param filePath
     * @return
     */
    public static void write2File(String in, String filePath) {
        try {
            //判断是否已存在
            boolean exists = getPathObj(filePath).toFile().exists();
            if (!exists) {
                //创建上级目录
                Files.createDirectory(getPathObj(filePath).getParent());
            }
            Files.write(getPathObj(filePath), in.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public static Path getPathObj(String path) {
        return FileSystems.getDefault().getPath(path);
    }


    /**
     * 根据路径字符串获取所有的目录
     *
     * @param path
     * @return
     */
    public static List<String> getDirectoryFilesName(String path) {
        return getDirectoryFilesName(getPathObj(path));
    }

    /**
     * 获取目录下的所有文件名
     *
     * @author zhouhongfa@gz-yibo.com
     * @version 1.0
     * @since 2019/7/25
     */
    public static List<String> getDirectoryFilesName(Path pathObj) {

        List<String> res = Lists.newArrayList();
        //判断是否是目录
        if (pathObj.toFile().isDirectory()) {
            String[] sons = pathObj.toFile().list();
            Arrays.stream(sons).forEach(e -> res.add(e));
        } else {
            //如果是文件，则抛异常
            throw new RuntimeException(String.format("%s is not a Directory", pathObj.toFile()));
        }

        return res;
    }

    /**
     * 递归获取路径下的所有文件path对象
     *
     * @param pathObj
     * @return
     */
    public static List<Path> getDirectoryPaths(Path pathObj) {
        List<Path> res = Lists.newArrayList();
        //判断是否是目录
        if (pathObj.toFile().isDirectory()) {
            //取下级目录
            String[] sons = pathObj.toFile().list();
            //
            for (String son : sons) {
                //取 path对象，递归进行读取
                String sonPath = pathObj.toString().concat(pathObj.getFileSystem().getSeparator()).concat(son);
                res.addAll(getDirectoryPaths(getPathObj(sonPath)));
            }
        } else {
            //如果是文件，则返回path对象
            return ImmutableList.of(pathObj);
        }

        return res;
    }

    /**
     * 根据path，读取所有内容，并以字符串返回
     *
     * @param path
     * @return
     */
    public static String readFile2String(Path path) {
        StringBuffer sb = new StringBuffer();

        //读取文件内容
        try {
            //增加换行符
            Files.lines(path).forEach(e -> sb.append(e).append("\r\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 根据path，读取所有内容，并以字符串返回
     *
     * @param path
     * @return
     */
    public static String readFile2String(String path) {
        return readFile2String(getPathObj(path));
    }


    /**
     * 根据文件名过滤掉扩展名
     *
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName) {
        String dot = ".";
        // 找出最后一个.
        if (fileName.contains(dot)) {
            return fileName.substring(0, fileName.lastIndexOf(dot));
        } else {
            //抛异常?
            // throw new IllegalArgumentException("读取的文件名有误！");
            return fileName;
        }
    }

    /**
     * 获取文件最后修改时间
     */
    public static FileTime getLastModifiedTime(Path path) {
        try {
            FileTime lastModifiedTime = Files.getLastModifiedTime(path);
            return lastModifiedTime;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String s = genPackageFilePath("com.yibo.package");
        System.out.println(s);
    }


}
