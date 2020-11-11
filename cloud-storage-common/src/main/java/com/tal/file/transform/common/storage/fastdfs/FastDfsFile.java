package com.tal.file.transform.common.storage.fastdfs;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.tal.file.transform.common.FilenameUtils;
import com.tal.file.transform.common.StringUtils;
import com.tal.file.transform.common.mime.Mime;
import com.tal.file.transform.common.mime.MimeUtils;
import com.tal.file.transform.common.storage.StorageConfig;
import com.tal.file.transform.common.storage.StorageFile;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Description
 * <p>
 * 工具类中只封装了部分方法,更多方法请自己封装
 * </p>
 * DATE 2020-11-10.
 *
 * @author 刘江涛.
 */
public class FastDfsFile implements StorageFile {

    private static final Logger log = LoggerFactory.getLogger(FastDfsFile.class);

    private  TrackerClient trackerClient = null;
    private  TrackerServer trackerServer = null;
    private  StorageServer storageServer = null;
    private  StorageClient1 storageClient = null;
    private  String path = "";
    private  StorageConfig conf = null;
    private  boolean lookup;

    public FastDfsFile(String path, StorageConfig conf, boolean lookup){
        this.lookup = lookup;

        try {
            ClientGlobal.init("fdfs-client.properties");
            trackerClient = new TrackerClient();
            trackerServer = trackerClient.getTrackerServer();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (MyException e) {
            log.error(e.getMessage(), e);
        }
        storageClient = new StorageClient1(trackerServer, storageServer);
        this.path = path;
        this.conf = conf;
    }


    /**
     * 静态工厂方法，简化调用关系
     * @param path
     * @param conf
     * @return
     */
    public static StorageFile create(String path, StorageConfig conf, boolean lookup) {
        return new FastDfsFile(path, conf, lookup);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     * @param fileName 文件全路径
     * @param extName 文件扩展名，不包含（.）
     * @param metas 文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(String fileName, String extName, NameValuePair[] metas) {
        String result=null;
        try {
            result = storageClient.upload_file1(fileName, extName, metas);
        } catch (IOException e) {
            log.error("fileName:{}.{}, 上传失败", fileName, extName);
        } catch (MyException e) {
            log.error("fileName:{}.{}, 上传失败", fileName, extName);
        }
        return result;
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     * @param inputStream 文件流
     * @param extName 文件扩展名，不包含（.）
     * @param metas 文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(InputStream inputStream, long length, String extName, NameValuePair[] metas) {
        String result=null;
        try {
            result = storageClient.upload_file(inputStream, length, extName, metas);
        } catch (IOException e) {
            log.error("fileName:{}.{}, 上传失败", length, extName, e);
        } catch (MyException e) {
            log.error("fileName:{}.{}, 上传失败", length, extName, e);
        }
        return result;
    }

    /**
     * 上传文件,传fileName
     * @param fileName 文件的磁盘路径名称 如：D:/image/aaa.jpg
     * @return null为失败
     */
    public String uploadFile(String fileName) {
        return uploadFile(fileName, null, null);
    }
    /**
     *
     * @param fileName 文件的磁盘路径名称 如：D:/image/aaa.jpg
     * @param extName 文件的扩展名 如 txt jpg等
     * @return null为失败
     */
    public  String uploadFile(String fileName, String extName) {
        return uploadFile(fileName, extName, null);
    }

    /**
     * 上传文件方法
     * <p>Title: uploadFile</p>
     * <p>Description: </p>
     * @param fileContent 文件的内容，字节数组
     * @param extName 文件扩展名
     * @param metas 文件扩展信息
     * @return
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent, String extName, NameValuePair[] metas) {
        String result=null;
        try {
            result = storageClient.upload_file1(fileContent, extName, metas);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (MyException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return result;
    }
    /**
     * 上传文件
     * @param fileContent 文件的字节数组
     * @return null为失败
     * @throws Exception
     */
    public String uploadFile(byte[] fileContent) throws Exception {
        return uploadFile(fileContent, null, null);
    }

    /**
     * 上传文件
     * @param fileContent  文件的字节数组
     * @param extName  文件的扩展名 如 txt  jpg png 等
     * @return null为失败
     */
    public String uploadFile(byte[] fileContent, String extName) {
        return uploadFile(fileContent, extName, null);
    }

    /**
     * 文件下载到磁盘T
     * @param path 图片路径
     * @param output 输出流 中包含要输出到磁盘的路径
     * @return -1失败,0成功
     */
    public int downloadFile(String path, BufferedOutputStream output) {
        int result=-1;
        try {
            byte[] b = storageClient.download_file1(path);
            try{
                if(b != null){
                    output.write(b);
                    result=0;
                }
            }catch (Exception e){} //用户可能取消了下载
            finally {
                if (output != null){
                    try {
                        output.close();
                    } catch (IOException e) {
                        log.error("path:{}, 关闭文件句柄失败", path);
                    }
                }
            }
        } catch (Exception e) {
            log.error("path:{}, 下载失败", path, e);
        }
        return result;
    }
    /**
     * 获取文件数组
     * @param path 文件的路径 如group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return
     */
    public byte[] downloadTytes(String path) {
        byte[] b=null;
        try {
            b = storageClient.download_file1(path);
        } catch (IOException e) {
            log.error("path:{}, 下载失败", path, e);
        } catch (MyException e) {
            log.error("path:{}, 下载失败", path, e);
        }
        return b;
    }

    /**
     * 删除文件
     * @param group 组名 如：group1
     * @param storagePath 不带组名的路径名称 如：M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return -1失败,0成功
     */
    public Integer deleteFile(String group ,String storagePath){
        int result=-1;
        try {
            result = storageClient.delete_file(group, storagePath);
        } catch (IOException e) {
            log.error("path:{}, 删除失败", storagePath, e);
        } catch (MyException e) {
            log.error("path:{}, 删除失败", storagePath, e);
        }
        return  result;
    }
    /**
     *
     * @param storagePath  文件的全部路径 如：group1/M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return -1失败,0成功
     * @throws IOException
     * @throws Exception
     */
    public Integer deleteFile(String storagePath){
        int result=-1;
        try {
            result = storageClient.delete_file1(storagePath);
        } catch (IOException e) {
            log.error("path:{}, 删除失败", storagePath, e);
        } catch (MyException e) {
            log.error("path:{}, 删除失败", storagePath, e);
        }
        return  result;
    }

    /**
     * 获取远程服务器文件资源信息
     * @param groupName   文件组名 如：group1
     * @param remoteFileName M00/00/00/wKgRsVjtwpSAXGwkAAAweEAzRjw471.jpg
     * @return
     */
    public FileInfo getFile(String groupName, String remoteFileName){
        try {
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            log.error("path:{}, 获取远程资源信息失败", remoteFileName, e);
        }
        return null;
    }

    @Override
    public String getUrl() {
        if(StringUtils.isUrl(path)) {
            return path;
        }

        return String.format("%s%s", conf.getPrefixUrl(), path);
    }

    @Override
    public String getPath() {
        if(lookup && StringUtils.isUrl(path)) {
            String prefixUrl = conf.getPrefixUrl();

            if(path.startsWith(prefixUrl)) {
                return path.substring(prefixUrl.length());
            }

            return "";
        }

        return path;
    }

    @Override
    public String getFullPath() {
        return getUrl();
    }

    @Override
    public long getSize() {
        String url = getUrl();

        if(StringUtils.isNotBlank(url) && StringUtils.isUrl(url)) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).method("head", null).build();

            try {
                com.squareup.okhttp.Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    return StringUtils.toLong(response.header("Content-Length"));
                }

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return 0;
    }

    @Override
    public InputStream openStream() {
        String url = getUrl();

        if(StringUtils.isNotBlank(url) && StringUtils.isUrl(url)) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            try {
                com.squareup.okhttp.Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    return response.body().byteStream();
                }

            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }

        return null;
    }

    @Override
    public void write(final InputStream stream) {

    }

    @Override
    public void write(final InputStream stream, final Mime mime) {

    }

    @Override
    public void write(final InputStream stream, final long writeLength) {
        
    }

    @Override
    public void write(final InputStream stream, final long writeLength, final Mime mime) {
        NameValuePair[] mimes = new NameValuePair[1];
        mimes[0] = new NameValuePair("mimeType", mime.getMimeType());
        String result = uploadFile(stream, writeLength, mime.getFileExt(), mimes);
        int tryCount = 0;

        while(org.apache.commons.lang3.StringUtils.isEmpty(result) && tryCount < 3) {
            result = uploadFile(stream, writeLength, mime.getFileExt(), mimes);

            tryCount ++;
        }
        this.path = result;
    }

    @Override
    public boolean exists(final String bucketName, final String key) {
        return false;
    }

    @Override
    public void delete() {
        deleteFile("");
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("/Users/liujiangtao/Downloads/5BD67081-8014-4ED0-84DF-78E4039FBEAD.jpg");
        FastDfsFile fastDfsFile = new FastDfsFile(file.getPath(), null, false);

        InputStream inputStream = new FileInputStream(file);
        Mime mime = MimeUtils.find(FilenameUtils.getExt(file.getPath()));
        fastDfsFile.write(inputStream, file.length(), mime);
    }
}
