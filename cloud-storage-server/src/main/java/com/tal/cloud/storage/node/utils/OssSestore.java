package com.tal.cloud.storage.node.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.StorageClass;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Description
 * <p>
 * </p>
 * DATE 2019/6/21.
 *
 * @author 刘江涛.
 */
@Component
public class OssSestore {

    @Value("${storage.accessKey}")
    private String accessKeyId;
    @Value("${storage.secretKey}")
    private String accessKeySecret;

    public void restore(String bucketName, String objectName) throws Exception {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
// 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。


// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

        ObjectMetadata objectMetadata = ossClient.getObjectMetadata(bucketName, objectName);

// 校验文件是否为归档文件。
        StorageClass storageClass = objectMetadata.getObjectStorageClass();
        if (storageClass == StorageClass.Archive) {
            // 解冻文件。
            ossClient.restoreObject(bucketName, objectName);

            // 等待解冻完成。
            do {
                Thread.sleep(1000);
                objectMetadata = ossClient.getObjectMetadata(bucketName, objectName);
            } while (!objectMetadata.isRestoreCompleted());
        }

// 获取解冻文件。
//        OSSObject ossObject = ossClient.getObject(bucketName, objectName);
//        ossObject.getObjectContent().close();

// 关闭OSSClient。
        ossClient.shutdown();
    }

}
