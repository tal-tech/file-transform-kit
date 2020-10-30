package com.tal.file.transform.controller;

import com.tal.file.transform.controller.processor.UploadProcessor;
import com.tal.file.transform.controller.result.Result;
import com.tal.file.transform.controller.result.UploadResult;
import com.tal.file.transform.entity.Misc;
import com.tal.file.transform.command.Command;
import com.tal.file.transform.common.StringUtils;
import com.tal.file.transform.common.storage.StorageFileNamer;
import com.tal.file.transform.common.storage.StorageZone;
import com.tal.file.transform.customer.Customers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description
 * <p>
 * </p>
 * DATE 2018/8/10.
 *
 * @author 刘江涛.
 */
@RequestMapping("/multi-media/upload")
@RestController
@Configuration
public class UploadController {

    private Logger log = LoggerFactory.getLogger(UploadController.class);

    private Customers customers;
    private StorageZone zone;
    private StorageFileNamer fileNamer;
    private ApplicationContext app;

    private static final String CLIENT_ID = "clientId";

    private static final String DOMAIN = "domain";

    @Value("${file.upload.dir}")
    private String fileDir;

    @Autowired
    UploadController(Customers customers, StorageZone zone, StorageFileNamer fileNamer, ApplicationContext app) {
        this.customers = customers;
        this.zone = zone;
        this.fileNamer = fileNamer;
        this.app = app;
    }

    private File uploadDir = null;

    @PutMapping(value = "/{type}")
    public UploadResult putUpload(@PathVariable(value = "type", required = false) String type,
                                  @RequestParam(value = "clientId", required = false) String clientId,
                                  @RequestParam(value = "domain", required = false) String domain,
                                  @RequestParam(value = "storPath", required = false) String storPath,
                                  HttpServletRequest request) {
        return this.upload(type, clientId, domain, storPath, request);
    }

    @PostMapping(value = "/{type}")
    public UploadResult upload(@PathVariable(value = "type", required = false) String type,
                               @RequestParam(value = "clientId", required = false) String clientId,
                               @RequestParam(value = "domain", required = false) String domain,
                               @RequestParam(value = "storPath", required = false) String storPath,
                               HttpServletRequest request) {
        long startTime = System.currentTimeMillis();

        log.info(String.format("/* ---------- %s start at %d. ----------", type, startTime));

        UploadResult<?> ur = new UploadResult<Misc>();

        if(Command.TYPE_UNKNOWN.equals(type)) {
            ur.setCode(Result.ERR_NOT_SUPPORTED_TYPE);
            ur.setMessage(Result.MSG_NOT_SUPPORTED_TYPE);

        } else {
            List<MultipartFile> tempFiles = ((MultipartHttpServletRequest) request).getFiles("FILE");
            if (uploadDir == null) {
                uploadDir = getUploadDir(request);
            }

            try {

                if (Objects.nonNull(tempFiles) && tempFiles.size() >= 1) {
                    List<File> files = new ArrayList<>();
                    for (int i = 0; i < tempFiles.size(); i++) {
                        MultipartFile file = tempFiles.get(i);

                        String fileName = file.getOriginalFilename();

                        File dest = new File(uploadDir + fileName);
                        file.transferTo(dest);
                        files.add(dest);
                    }
                    log.info("   " + clientId + ", " + domain);

                    if (files.size() > 0) {
                        if (StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(domain)
                                    && customers.auth(clientId, domain)) {
                            UploadProcessor processor = getUploadProcessor(type);

                            if (processor != null) {
                                fileNamer.setFilePath(storPath);
                                ur = processor.process(clientId, files, zone, fileNamer);

                            } else {
                                ur.setCode(Result.ERR_NOT_SUPPORTED_TYPE);
                                ur.setMessage(Result.MSG_NOT_SUPPORTED_TYPE);
                            }

                        } else {
                            ur.setCode(Result.ERR_AUTHENTICATION_FAILED);
                            ur.setMessage(Result.MSG_AUTHENTICATION_FAILED);
                        }

                    } else {
                        ur.setCode(Result.ERR_NOT_ENOUGH_DATA);
                        ur.setMessage(Result.MSG_NOT_ENOUGH_DATA);
                    }

                } else {
                    ur.setCode(Result.ERR_NOT_ENOUGH_DATA);
                    ur.setMessage(Result.MSG_NOT_ENOUGH_DATA);
                }

            } catch (IOException e) {
                ur.setCode(Result.ERR_UPLOAD_FAILED);
                ur.setMessage(Result.MSG_UPLOAD_FAILED);
            }
        }
        log.info(String.format("---------- %s result: %d, cost %d ms. ---------- */", type, ur.getCode(), System.currentTimeMillis() - startTime));

        return ur;
    }

    private File getUploadDir(HttpServletRequest request) {
        File tmpDir = (File) request.getServletContext().getAttribute("javax.servlet.context.tempdir");
        String upload = tmpDir.getAbsolutePath() + File.separatorChar + "upload";
        File uploadDir = new File(upload);

        if(!uploadDir.exists() || !uploadDir.isDirectory()) {
            uploadDir.mkdirs();
        }

        return tmpDir;
    }


    private UploadProcessor getUploadProcessor(String type) {
        switch(type) {
            case Command.TYPE_IMAGE:
            case Command.TYPE_AUDIO:
            case Command.TYPE_VIDEO:
            case Command.TYPE_MISC:
                String beanName = String.format("%sProcessor", type);

                return (UploadProcessor)app.getBean(beanName);

            case Command.TYPE_UNKNOWN:
            default: break;
        }

        return null;
    }

}
