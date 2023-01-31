package com.artwook.nft.nftshop.controller.user;

import java.io.File;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.artwook.nft.nftshop.common.BaseResponseCode;
import com.artwook.nft.nftshop.common.BizException;
import com.artwook.nft.nftshop.db.model.Attachments;
import com.artwook.nft.nftshop.service.AttachmentService;
import com.artwook.nft.nftshop.utils.FileUtils;
import com.artwook.nft.nftshop.utils.ShaUtils;
import com.artwook.nft.nftshop.vo.BaseResponseMessage;
import com.artwook.nft.nftshop.vo.attachment.OssFileVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/v1/attach")
public class AttachmentController {

    @Value("${app.upload.tmpPath}")
    private String tmpPath;

    @Autowired
    private AttachmentService attachmentService;

    @PostConstruct
    public void init() {
        System.setProperty("java.io.tmpdir", tmpPath);
        log.info("java.io.tmpdir :{}", System.getProperty("java.io.tmpdir"));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponseMessage<OssFileVO> upload(@RequestPart("file") MultipartFile mulFile) {
        log.info("upload {} {}", System.getProperty("java.io.tmpdir"), mulFile.getOriginalFilename());
        try {
            File file = FileUtils.multipartFileToFile(mulFile, true);

            String fileHash = ShaUtils.sha256File(file);
            Attachments aAttachments = attachmentService.getAttachmentsByUUID(fileHash);
            OssFileVO ssoFile = new OssFileVO();
            if (aAttachments != null) {
                ssoFile.setPath(aAttachments.getOssPath());
                ssoFile.setName(aAttachments.getAttachUuid());
                file.delete();
            } else {
                String path = file.getPath().replace(System.getProperty("java.io.tmpdir"), "");
                ssoFile.setPath(path);
                ssoFile.setName(fileHash);
                attachmentService.insert(fileHash, path,
                        FileUtils.getExtensionName(mulFile.getOriginalFilename()));
            }
            log.info("ssoFile:{}", JSONObject.toJSONString(ssoFile));
            return new BaseResponseMessage<OssFileVO>(ssoFile);
        } catch (Exception e) {
            log.error("upload", e);
            throw new BizException(BaseResponseCode.ERROR);
        }
    }
}
