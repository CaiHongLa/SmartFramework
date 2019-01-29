package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

/**
 * @author LIYANHUI
 */
public class FileUtil {

    private static Logger logger = LogManager.getLogger(FileUtil.class);

    public static boolean delete(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    public static Properties loadProperties(String filePath) {
        InputStream is = null;
        try {
            Resource resource = new FileSystemResource(filePath);
            if (!resource.exists()) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("can not found " + filePath + "，please make sure file exist"));
            }
            is = resource.getInputStream();
            Properties properties = new Properties();
            String lowerCaseFilePath = filePath.toLowerCase();
            if (lowerCaseFilePath.endsWith(".properties")) {
                properties.load(is);
            } else if (lowerCaseFilePath.endsWith(".xml")) {
                properties.loadFromXML(is);
            }
            return properties;
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("error while load  " + filePath + "，please make sure file is properties or xml", e));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.error("close file stream error！" + e);
            }
        }
    }

    public static Properties loadClassPathProperties(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        InputStream is = null;
        try {
            is = resource.getInputStream();
            Properties properties = new Properties();
            String lowerCaseFileName = fileName.toLowerCase();
            if (lowerCaseFileName.endsWith(".properties")) {
                properties.load(is);
            } else if (lowerCaseFileName.endsWith(".xml")) {
                properties.loadFromXML(is);
            }
            return properties;
        } catch (IOException e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("error while load  " + fileName + "，please make sure file is properties or xml", e));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.error("close file stream error！" + e);
            }
        }
    }

    public static Properties loadPropertiesOnClassPathOrConfigDir(String fileName) {
        if (isClassPathFileExist(fileName)) {
            return loadClassPathProperties(fileName);
        } else {
            String path = "config" + File.separator + fileName;
            if (isFileExist(path)) {
                return loadProperties(path);
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("can not found file "  + fileName + "under classpath or config path ，please make sure file exist"));
            }
        }
    }

    public static InputStream loadClassPathResourceAsStream(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        if (!resource.exists()) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("error while load classpath file  " + fileName + " for file not found"));
        } else {
            try {
                return resource.getInputStream();
            } catch (IOException e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }
    }

    public static InputStream loadResourceAsStreamOnClassPathOrConfigDir(String fileName) {
        if (isClassPathFileExist(fileName)) {
            return loadClassPathResourceAsStream(fileName);
        } else {
            String path = "config" + File.separator + fileName;
            if (isFileExist(path)) {
                try {
                    return (new FileSystemResource(path)).getInputStream();
                } catch (Exception e) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                }
            } else {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("can not found file "  + fileName + "under classpath or config path ，please make sure file exist"));
            }
        }
    }

    public static String loadClassPathResourceAsText(String fileName) {
        return read(loadClassPathResourceAsStream(fileName));
    }

    public static String loadResourceAsTextOnClassPathOrConfigDir(String fileName) {
        return read(loadResourceAsStreamOnClassPathOrConfigDir(fileName));
    }

    public static boolean isClassPathFileExist(String fileName) {
        return (new ClassPathResource(fileName)).exists();
    }

    public static boolean isFileExist(String filePath) {
        return (new FileSystemResource(filePath)).exists();
    }

    public static boolean isFileExistOnClasspathOrConfigDir(String fileName) {
        return isClassPathFileExist(fileName) || isFileExist("config" + File.separator + fileName);
    }

    public static String read(String filePath) {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
            StringBuilder content = new StringBuilder();
            String data;

            while ((data = br.readLine()) != null) {
                content.append(data).append("\r\n");
            }

            if (content.length() <= "\r\n".length()) {
                return content.toString();
            }

            return content.substring(0, content.length() - "\r\n".length());
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                logger.error("close file stream error！" + e);
            }

        }
    }

    public static String read(String filePath, FileUtil.CHARSET charset) {
        StringBuilder content = new StringBuilder();
        File file = new File(filePath);
        FileInputStream is = null;

        try {
            is = new FileInputStream(file);
            byte[] tmp = new byte[512];

            int len;
            while ((len = is.read(tmp)) != -1) {
                content.append(new String(tmp, 0, len, charset.toString()));
            }

            is.close();
            return content.toString();
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.error("close file stream error ！" + e);
            }

        }
    }

    public static String read(InputStream is) {
        StringBuilder content = new StringBuilder();

        try {
            byte[] tmp = new byte[512];

            int len;
            while ((len = is.read(tmp)) != -1) {
                content.append(new String(tmp, 0, len));
            }

            is.close();
            return content.toString();
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                logger.error("close file stream error！" + e);
            }

        }
    }

    public static List<File> searchFiles(String path, List<String> types) {
        List<File> result = new ArrayList<>();
        List<String> typesCpy = new ArrayList<>();

        for (String type : types) {
            typesCpy.add(type.toUpperCase());
        }

        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                if (files.length == 0) {
                    return result;
                }
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        result.addAll(searchFiles(file2.getAbsolutePath(), typesCpy));
                    } else if (typesCpy.contains(TextUtil.getFileExtName(file2.getName()).toUpperCase())) {
                        result.add(file2);
                    }
                }
            }
        }

        return result;
    }

    public static File[] searchFiles(String basePath, final String keywords, final FileUtil.SEARCH_STRATEGY searchStrategy, final boolean ignoreCase) {
        File baseDir = new File(basePath);
        if (baseDir.isDirectory()) {
            return baseDir.listFiles(file -> {
                if (searchStrategy == SEARCH_STRATEGY.START_WIRH) {
                    return ignoreCase ? file.getName().toUpperCase().startsWith(keywords.toUpperCase()) : file.getName().startsWith(keywords);
                } else if (searchStrategy == SEARCH_STRATEGY.ENDS_WITH) {
                    return ignoreCase ? file.getName().toUpperCase().endsWith(keywords.toUpperCase()) : file.getName().endsWith(keywords);
                } else if (searchStrategy == SEARCH_STRATEGY.INDEXOF) {
                    if (ignoreCase) {
                        return file.getName().toUpperCase().contains(keywords.toUpperCase());
                    } else {
                        return file.getName().contains(keywords);
                    }
                } else {
                    return false;
                }
            });
        } else {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("path not exist"));
        }
    }

    public static void append(String content, String path) {
        write(content, path, true);
    }

    public static void write(String content, String path) {
        write(content, path, false);
    }

    private static void write(String content, String path, boolean append) {
        File dir = (new File(path)).getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            Writer writer = new FileWriter(path, append);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public static void save(MultipartFile file, String savePath) {
        if (file != null) {
            try {
                file.transferTo(new File(savePath));
            } catch (Exception e) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
            }
        }

    }

    public static boolean copy(String sourceFilePath, String newFilePath) {
        File srcFile = new File(sourceFilePath);
        if (!srcFile.exists()) {
            logger.error("sourceFilePath not exist", new FileNotFoundException("sourceFilePath not exist"));
            return false;
        } else {
            File newFile = new File(newFilePath);
            if (newFile.exists()) {
                logger.error("dest file exist", new FrameworkInternalSystemException(new SystemExceptionDesc("dest file exist")));
                return false;
            } else {
                InputStream is = null;
                FileOutputStream os = null;

                try {
                    is = new FileInputStream(srcFile);
                    os = new FileOutputStream(new File(newFilePath));
                    byte[] tmp = new byte[1024];

                    int len;
                    while ((len = is.read(tmp)) != -1) {
                        os.write(tmp, 0, len);
                    }

                    os.flush();
                    os.close();
                    is.close();
                    return true;
                } catch (Exception e) {
                    throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
                } finally {
                    if (os != null) {
                        try {
                            os.close();
                        } catch (IOException e) {
                            logger.error("close file write out stream error！" + e);
                        }
                    }

                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            logger.error("close file write in stream error！" + e);
                        }
                    }

                }
            }
        }
    }

    private static void pack(List<File> files, String outputFile, Map<String, String> nameMapping) {
        OutputStream fileOutputStream = null;
        CheckedOutputStream cos = null;
        ZipOutputStream out = null;

        try {
            fileOutputStream = new FileOutputStream(outputFile);
            cos = new CheckedOutputStream(fileOutputStream, new CRC32());
            out = new ZipOutputStream(cos);

            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    String nameWidthoutSuffix = name.substring(0, name.indexOf("."));
                    if (nameMapping != null && nameMapping.size() > 0 && nameMapping.containsKey(nameWidthoutSuffix)) {
                        String suffix = name.substring(name.indexOf("."));
                        name = nameMapping.get(nameWidthoutSuffix) + suffix;
                    }

                    ZipEntry entry = new ZipEntry(name);
                    out.putNextEntry(entry);
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    byte[] tmp = new byte[1024];

                    int len;
                    while ((len = bis.read(tmp)) != -1) {
                        out.write(tmp, 0, len);
                    }

                    bis.close();
                }
            }
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }

                if (cos != null) {
                    cos.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                logger.error("close file write out stream error！" + e);
            }

        }

    }

    public static void packFiles(List<String> files, String outputFile, Map<String, String> nameMapping) {
        List<File> _files = new ArrayList<>();
        File f;

        for (String file : files) {
            f = new File(file);
            if (!f.exists()) {
                throw new FrameworkInternalSystemException(new SystemExceptionDesc("file not found " + f));
            }

            _files.add(f);
        }

        pack(_files, outputFile, nameMapping);
    }

    public static void packFiles(List<String> files, String outputFile) {
        packFiles(files, outputFile, null);
    }

    public static void packFiles(String scanPath, String outputFile) {
        File dir = new File(scanPath);
        if (!dir.isDirectory()) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc("not directory"));
        } else {
            File[] files = dir.listFiles();
            if (files != null) {
                pack(Arrays.asList(files), outputFile, null);
            }
        }
    }

    public static void unpackFile(String srcFile, String outputPath) {
        try {
            ZipFile zFile = new ZipFile(srcFile);
            Enumeration entries = zFile.getEntries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                InputStream is = zFile.getInputStream(entry);
                OutputStream os = new FileOutputStream(outputPath + File.separator + entry.getName());
                byte[] tmp = new byte[1024];

                int len;
                while ((len = is.read(tmp)) != -1) {
                    os.write(tmp, 0, len);
                }

                os.flush();
                os.close();
                is.close();
            }

            zFile.close();
        } catch (Exception e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }

    public enum SEARCH_STRATEGY {
        START_WIRH,
        ENDS_WITH,
        INDEXOF;

        SEARCH_STRATEGY() {
        }
    }

    public enum CHARSET {
        UTF_8("UTF-8"),
        GBK("GBK"),
        GB2312("GB2312"),
        GB18030("GB18030");

        private String value;

        CHARSET(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
