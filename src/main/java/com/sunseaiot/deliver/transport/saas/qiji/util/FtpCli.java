/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 广播FTP客户端上传文件
 *
 * @author: xwb
 * @create: 2019/09/29 16:38
 */
public class FtpCli {

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final int DEFAULT_TIMEOUT = 60 * 1000;

    private static final String DAILY_FILE_PATH = "dailyFilePath";

    private final String host;

    private final int port;

    private final String username;

    private final String password;

    private FTPClient ftpClient;

    private volatile String ftpBasePath;

    private FtpCli(String host, String username, String password) {
        this(host, 21, username, password, DEFAULT_CHARSET);
        setTimeout(DEFAULT_TIMEOUT, DEFAULT_TIMEOUT, DEFAULT_TIMEOUT);
    }

    private FtpCli(String host, int port, String username, String password, String charset) {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding(charset);
        this.host = StringUtils.isEmpty(host) ? "localhost" : host;
        this.port = (port <= 0) ? 21 : port;
        this.username = StringUtils.isEmpty(username) ? "anonymous" : username;
        this.password = password;
    }

    /**
     * 创建默认的ftp客户端
     *
     * @param host     服务器地址
     * @param username 用户名
     * @param password 密码
     * @return ftp客户连接
     */
    public static FtpCli createFtpCli(String host, String username, String password) {
        return new FtpCli(host, username, password);
    }

    /**
     * 创建自定义属性的ftp客户端
     *
     * @param host     服务器地址
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @param charset  编码格式
     * @return ftp客户连接
     */
    public static FtpCli createFtpCli(String host, int port, String username, String password, String charset) {
        return new FtpCli(host, port, username, password, charset);
    }

    /**
     * 设置超时时间
     *
     * @param defaultTimeout 默认超时时间
     * @param connectTimeout 连接超时时间
     * @param dataTimeout    数据超时时间
     */
    public void setTimeout(int defaultTimeout, int connectTimeout, int dataTimeout) {
        ftpClient.setDefaultTimeout(defaultTimeout);
        ftpClient.setConnectTimeout(connectTimeout);
        ftpClient.setDataTimeout(dataTimeout);
    }

    /**
     * 连接到ftp
     *
     * @throws IOException
     */
    public void connect() throws IOException {
        try {
            ftpClient.connect(host, port);
        } catch (UnknownHostException e) {
            throw new IOException("Can't find FTP server :" + host);
        }
        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            disconnect();
            throw new IOException("Can't connect to server :" + host);
        }
        if (!ftpClient.login(username, password)) {
            disconnect();
            throw new IOException("Can't login to server :" + host);
        }
        // set data transfer mode.
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        // Use passive mode to pass firewalls.
        ftpClient.enterLocalPassiveMode();
        initFtpBasePath();
    }

    /**
     * 连接ftp时保存刚登陆ftp时的路径
     *
     * @throws IOException
     */
    private void initFtpBasePath() throws IOException {
        if (StringUtils.isEmpty(ftpBasePath)) {
            synchronized (this) {
                if (StringUtils.isEmpty(ftpBasePath)) {
                    ftpBasePath = ftpClient.printWorkingDirectory();
                }
            }
        }
    }

    /**
     * ftp是否处于连接状态
     *
     * @return 返回连接状态
     */
    public boolean isConnected() {
        return ftpClient.isConnected();
    }

    /**
     * 上传文件到对应日期文件下
     *
     * @param fileName    文件名
     * @param inputStream 输入流
     * @return 文件路径
     * @throws IOException
     */
    public String uploadFileToDailyDir(String fileName, InputStream inputStream) throws IOException {
        changeWorkingDirectory(ftpBasePath);
        SimpleDateFormat dateFormat = new SimpleDateFormat("/yyyy/MM/dd");
        String formatDatePath = dateFormat.format(new Date());
        String uploadDir = DAILY_FILE_PATH + formatDatePath;
        makeDirs(uploadDir);
        storeFile(fileName, inputStream);
        return formatDatePath + "/" + fileName;
    }

    /**
     * 根据uploadFileToDailyDir返回的路径，从ftp下载文件到指定输出流中
     *
     * @param dailyDirFilePath 文件夹路径
     * @param outputStream     输出流
     * @throws IOException
     */
    public void downloadFileFromDailyDir(String dailyDirFilePath, OutputStream outputStream) throws IOException {
        changeWorkingDirectory(ftpBasePath);
        String ftpRealFilePath = DAILY_FILE_PATH + dailyDirFilePath;
        ftpClient.retrieveFile(ftpRealFilePath, outputStream);
    }

    /**
     * 获取ftp上指定文件名到输出流中
     *
     * @param ftpFileName ftp文件名称
     * @param out         输出流
     * @throws IOException
     */
    public void retrieveFile(String ftpFileName, OutputStream out) throws IOException {
        try {
            FTPFile[] fileInfoArray = ftpClient.listFiles(ftpFileName);
            if (fileInfoArray == null || fileInfoArray.length == 0) {
                throw new FileNotFoundException("File '" + ftpFileName + "' was not found on FTP server.");
            }
            FTPFile fileInfo = fileInfoArray[0];
            if (fileInfo.getSize() > Integer.MAX_VALUE) {
                throw new IOException("File '" + ftpFileName + "' is too large.");
            }
            if (!ftpClient.retrieveFile(ftpFileName, out)) {
                throw new IOException("Error loading file '" + ftpFileName + "' from FTP server. Check FTP " + "permissions and path.");
            }
            out.flush();
        } finally {
            closeStream(out);
        }
    }

    /**
     * 将输入流存储到指定的ftp路径下
     *
     * @param ftpFileName ftp文件名称
     * @param in          输入流
     * @throws IOException
     */
    public void storeFile(String ftpFileName, InputStream in) throws IOException {
        try {
            if (!ftpClient.storeFile(ftpFileName, in)) {
                throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions " + "and path.");
            }
        } finally {
            closeStream(in);
        }
    }

    /**
     * 根据文件ftp路径名称删除文件
     *
     * @param ftpFileName 文件名
     * @throws IOException
     */
    public void deleteFile(String ftpFileName) throws IOException {
        if (!ftpClient.deleteFile(ftpFileName)) {
            throw new IOException("Can't remove file '" + ftpFileName + "' from FTP server.");
        }
    }

    /**
     * 上传文件到ftp
     *
     * @param ftpFileName 文件名
     * @param localFile   路径
     * @throws IOException
     */
    public void upload(String ftpFileName, File localFile) throws IOException {
        if (!localFile.exists()) {
            throw new IOException("Can't upload '" + localFile.getAbsolutePath() + "'. This file doesn't exist.");
        }
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(localFile));
            if (!ftpClient.storeFile(ftpFileName, in)) {
                throw new IOException("Can't upload file '" + ftpFileName + "' to FTP server. Check FTP permissions " + "and path.");
            }
        } finally {
            closeStream(in);
        }
    }

    /**
     * 上传文件夹到ftp上
     *
     * @param remotePath 远程路径
     * @param localPath  本地路径
     * @throws IOException
     */
    public void uploadDir(String remotePath, String localPath) throws IOException {
        localPath = localPath.replace("\\\\", "/");
        File file = new File(localPath);
        if (file.exists()) {
            if (!ftpClient.changeWorkingDirectory(remotePath)) {
                ftpClient.makeDirectory(remotePath);
                ftpClient.changeWorkingDirectory(remotePath);
            }
            File[] files = file.listFiles();
            if (null != files) {
                for (File f : files) {
                    if (f.isDirectory() && !f.getName().equals(".") && !f.getName().equals("..")) {
                        uploadDir(remotePath + "/" + f.getName(), f.getPath());
                    } else if (f.isFile()) {
                        upload(remotePath + "/" + f.getName(), f);
                    }
                }
            }
        }
    }

    /**
     * 下载ftp文件到本地上
     *
     * @param ftpFileName 文件名称
     * @param localFile   本地文件
     * @throws IOException
     */
    public void download(String ftpFileName, File localFile) throws IOException {
        OutputStream out = null;
        try {
            FTPFile[] fileInfoArray = ftpClient.listFiles(ftpFileName);
            if (fileInfoArray == null || fileInfoArray.length == 0) {
                throw new FileNotFoundException("File " + ftpFileName + " was not found on FTP server.");
            }
            FTPFile fileInfo = fileInfoArray[0];
            if (fileInfo.getSize() > Integer.MAX_VALUE) {
                throw new IOException("File " + ftpFileName + " is too large.");
            }
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            if (!ftpClient.retrieveFile(ftpFileName, out)) {
                throw new IOException("Error loading file " + ftpFileName + " from FTP server. Check FTP permissions " + "and path.");
            }
            out.flush();
        } finally {
            closeStream(out);
        }
    }

    /**
     * 改变工作目录
     *
     * @param dir 文件夹
     * @return 是否变更成功
     */
    public boolean changeWorkingDirectory(String dir) {
        if (!ftpClient.isConnected()) {
            return false;
        }
        try {
            return ftpClient.changeWorkingDirectory(dir);
        } catch (IOException e) {
        }
        return false;
    }

    /**
     * 下载ftp服务器下文件夹到本地
     *
     * @param remotePath 远程路径
     * @param localPath  本地路径
     * @throws IOException
     */
    public void downloadDir(String remotePath, String localPath) throws IOException {
        localPath = localPath.replace("\\\\", "/");
        File file = new File(localPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FTPFile[] ftpFiles = ftpClient.listFiles(remotePath);
        for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) {
            FTPFile ftpFile = ftpFiles[i];
            if (ftpFile.isDirectory() && !ftpFile.getName().equals(".") && !ftpFile.getName().equals("..")) {
                downloadDir(remotePath + "/" + ftpFile.getName(), localPath + "/" + ftpFile.getName());
            } else {
                download(remotePath + "/" + ftpFile.getName(), new File(localPath + "/" + ftpFile.getName()));
            }
        }
    }

    /**
     * 列出ftp上文件目录下的文件
     *
     * @param filePath 文件路径
     * @return 文件列表
     * @throws IOException
     */
    public List<String> listFileNames(String filePath) throws IOException {
        FTPFile[] ftpFiles = ftpClient.listFiles(filePath);
        List<String> fileList = new ArrayList<>();
        if (ftpFiles != null) {
            for (int i = 0; i < ftpFiles.length; i++) {
                FTPFile ftpFile = ftpFiles[i];
                if (ftpFile.isFile()) {
                    fileList.add(ftpFile.getName());
                }
            }
        }
        return fileList;
    }

    /**
     * 发送ftp命令到ftp服务器中
     *
     * @param args 参数
     * @throws IOException
     */
    public void sendSiteCommand(String args) throws IOException {
        if (!ftpClient.isConnected()) {
            ftpClient.sendSiteCommand(args);
        }
    }

    /**
     * 获取当前所处的工作目录
     *
     * @return 字符串空
     */
    public String printWorkingDirectory() {
        if (!ftpClient.isConnected()) {
            return "";
        }
        try {
            return ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            // do nothing
        }
        return "";
    }

    /**
     * 切换到当前工作目录的父目录下
     *
     * @return 切换是否成功
     */
    public boolean changeToParentDirectory() {
        if (!ftpClient.isConnected()) {
            return false;
        }
        try {
            return ftpClient.changeToParentDirectory();
        } catch (IOException e) {
            // do nothing
        }
        return false;
    }

    /**
     * 返回当前工作目录的上一级目录
     *
     * @return 文件夹名称
     */
    public String printParentDirectory() {
        if (!ftpClient.isConnected()) {
            return "";
        }
        String w = printWorkingDirectory();
        changeToParentDirectory();
        String p = printWorkingDirectory();
        changeWorkingDirectory(w);
        return p;
    }

    /**
     * 创建目录
     *
     * @param pathname 文件名称
     * @return 是否创建成功
     * @throws IOException
     */
    public boolean makeDirectory(String pathname) throws IOException {
        return ftpClient.makeDirectory(pathname);
    }

    /**
     * 创建多个目录
     *
     * @param pathname 文件名
     * @throws IOException
     */
    public void makeDirs(String pathname) throws IOException {
        pathname = pathname.replace("\\\\", "/");
        String[] pathnameArray = pathname.split("/");
        for (String each : pathnameArray) {
            if (StringUtils.isNotEmpty(each)) {
                ftpClient.makeDirectory(each);
                ftpClient.changeWorkingDirectory(each);
            }
        }
    }

    /**
     * 关闭流
     *
     * @param stream
     */
    private static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ex) {
                // do nothing
            }
        }
    }

    /**
     * 关闭ftp连接
     */
    public void disconnect() {
        if (null != ftpClient && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException ex) {
                // do nothing
            }
        }
    }
}
