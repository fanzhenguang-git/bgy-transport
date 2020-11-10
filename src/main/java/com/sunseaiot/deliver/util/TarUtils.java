/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.*;

/**
 * @description: 生成tar 文件工具类
 * @author: xwb
 * @param:
 * @return:
 * @date: 2019/8/29 10:03
 */
public class TarUtils {

    private static Log logger = LogFactory.getLog(TarUtils.class);

    private static final int BUFFER = 4096;

    public static void tarFile(String inputFileName, String targetFileName, boolean includeRootDir) {
        File inputFile = new File(inputFileName);
        if ((inputFile.isDirectory()) && (!(includeRootDir))) {
            List fileList = new ArrayList();
            File[] files = inputFile.listFiles();
            for (int i = 0; i < files.length; ++i)
                fileList.add(files[i].getAbsolutePath());

            tarFiles(fileList, targetFileName);
        } else {
            tarFile(inputFileName, targetFileName);
        }
    }

    private static void tarFile(String inputFileName, String targetFileName) {
        File inputFile = new File(inputFileName);
        String base = inputFileName.substring(inputFileName
                .lastIndexOf(File.separator) + 1);
        TarOutputStream out = getTarOutputStream(targetFileName);
        tarPack(out, inputFile, base);
        try {
            if (null != out) {
                out.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static void tarFiles(List inputFileNameList, String targetFileName) {
        TarOutputStream out = getTarOutputStream(targetFileName);

        for (Iterator iterator = inputFileNameList.iterator(); iterator
                .hasNext(); ) {
            String inputFileName = (String) iterator.next();
            File inputFile = new File(inputFileName);
            String base = inputFileName.substring(inputFileName
                    .lastIndexOf(File.separator) + 1);
            tarPack(out, inputFile, base);
        }
        try {
            if (null != out) {
                out.close();
            }
        } catch (IOException e) {
            logger.error(e);
        }
    }

    private static void tarPack(TarOutputStream out, File inputFile, String base) {
        if (inputFile.isDirectory()) {
            packFolder(out, inputFile, base);
        } else {
            packFile(out, inputFile, base);
        }
    }

    private static void packFolder(TarOutputStream out, File inputFile, String base) {
        File[] fileList = inputFile.listFiles();
        try {
            out.putNextEntry(new TarEntry(base + "/"));
        } catch (IOException e) {
            logger.error(e);
        }
        base = base + "/";
        int i = 0;
        for (int j = fileList.length; i < j; ++i) {
            File file = fileList[i];
            tarPack(out, file, base + file.getName());
        }
    }

    private static void packFile(TarOutputStream out, File inputFile, String base) {
        TarEntry tarEntry = new TarEntry(base);
        tarEntry.setSize(inputFile.length());
        try {
            out.putNextEntry(tarEntry);
        } catch (IOException e) {
            logger.error(e);
        }
        FileInputStream in = null;
        try {
            in = new FileInputStream(inputFile);
        } catch (FileNotFoundException e) {
            logger.error(e);
        }
        int b = 0;
        byte[] B_ARRAY = new byte[4096];
        try {
            while ((b = in.read(B_ARRAY, 0, 4096)) != -1)
                out.write(B_ARRAY, 0, b);
        } catch (IOException e) {
            logger.error(e);
        } catch (NullPointerException e) {
            logger.error(e);
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.closeEntry();
                }
            } catch (IOException e) {
            }
        }
    }

    private static void compress(File srcFile) {
        File target = new File(srcFile.getAbsolutePath() + ".gz");
        FileInputStream in = null;
        GZIPOutputStream out = null;
        try {
            in = new FileInputStream(srcFile);
            out = new GZIPOutputStream(new FileOutputStream(target));
            int number = 0;
            byte[] B_ARRAY = new byte[4096];
            while ((number = in.read(B_ARRAY, 0, 4096)) != -1) {
                out.write(B_ARRAY, 0, number);
            }
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static TarOutputStream getTarOutputStream(String targetFileName) {
        targetFileName = targetFileName + ".tar";
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(targetFileName);
        } catch (FileNotFoundException e) {
            logger.error(e);
        }
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        TarOutputStream out = new TarOutputStream(bufferedOutputStream);
        out.setLongFileMode(2);
        return out;
    }

    public static void unTgzFile(File file, String descDir) {
        GZIPInputStream gzi = null;
        try {
            gzi = new GZIPInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
        TarInputStream tin = new TarInputStream(gzi);
        try {
            TarEntry tarEntry = null;
            while ((tarEntry = tin.getNextEntry()) != null) {
                unTarPack(tin, tarEntry, descDir);
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                tin.close();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    public static void unTarFile(File file, String descDir) {
        TarInputStream tin = null;
        try {
            tin = new TarInputStream(new FileInputStream(file));
            TarEntry tarEntry;
            while ((tarEntry = tin.getNextEntry()) != null) {
                unTarPack(tin, tarEntry, descDir);
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            try {
                tin.close();
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private static void unTarPack(TarInputStream tin, TarEntry tarEntry, String descDir) {
        if (tarEntry.isDirectory()) {
            unPackFolder(tin, tarEntry, descDir);
        } else {
            unPackFile(tin, tarEntry, descDir);
        }
    }

    private static void unPackFolder(TarInputStream tin, TarEntry tarEntry, String descDir) {
        descDir = descDir + File.separator + tarEntry.getName();
        if (!(new File(descDir).exists())) {
            new File(descDir).mkdirs();
        }
    }

    private static void unPackFile(TarInputStream tin, TarEntry tarEntry, String descDir) {
        if (!(new File(descDir).exists())) {
            new File(descDir).mkdirs();
        }
        try {
            tin.copyEntryContents(new FileOutputStream(descDir + File.separator
                    + tarEntry.getName()));
        } catch (FileNotFoundException e) {
            logger.error(e);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static void zipFile(File srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(zipfile), new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            FileInputStream in = new FileInputStream(srcfile);
            out.putNextEntry(new ZipEntry(srcfile.getName()));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFile(String inputFileName, String targetFileName, boolean includeRootDir) {
        File inputFile = new File(inputFileName);
        File targetFile = new File(targetFileName);
        if ((inputFile.isDirectory()) && (!(includeRootDir))) {
            List fileList = new ArrayList();
            File[] files = inputFile.listFiles();
            for (int i = 0; i < files.length; ++i) {
                fileList.add(files[i].getAbsolutePath());
            }
            zipFiles(fileList, targetFile);
        } else {
            zipFile(inputFile, targetFile);
        }
    }

    public static void zipFiles(List srcfiles, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            CheckedOutputStream cos = new CheckedOutputStream(new FileOutputStream(zipfile), new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            for (Iterator iterator = srcfiles.iterator(); iterator.hasNext(); ) {
                String srcfilePath = (String) iterator.next();
                File srcfile = new File(srcfilePath);
                FileInputStream in = new FileInputStream(srcfile);

                out.putNextEntry(new ZipEntry(srcfile.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void unZipFile(File zipfile, String descDir) {
        ZipFile zf;
        try {
            zf = new ZipFile(zipfile);
            for (Enumeration entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zf.getInputStream(entry);
                OutputStream out = new FileOutputStream(descDir + zipEntryName);
                byte[] buf1 = new byte[1024];
                int len;
                while ((len = in.read(buf1)) > 0) {
                    out.write(buf1, 0, len);
                }
                in.close();
                out.close();
            }
            zf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] zipBytes(byte[] contentBytes) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        try {
            CheckedOutputStream cos = new CheckedOutputStream(output, new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            ByteArrayInputStream in = new ByteArrayInputStream(contentBytes);
            out.putNextEntry(new ZipEntry("proPboc"));
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output.toByteArray();
    }
}