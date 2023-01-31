package com.artwook.nft.nftshop.service;

import java.util.List;

import com.artwook.nft.nftshop.db.mapper.AttachmentsMapper;
import com.artwook.nft.nftshop.db.model.Attachments;
import com.artwook.nft.nftshop.db.model.AttachmentsExample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AttachmentService {

    @Autowired
    private AttachmentsMapper attachmentsMapper;

    public Attachments getAttachmentsByUUID(String fileHash) {

        AttachmentsExample example = new AttachmentsExample();
        example.createCriteria().andAttachUuidEqualTo(fileHash);
        List<Attachments> list = attachmentsMapper.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    public void insert(String hash, String path, String type) {
        Attachments record = new Attachments();
        record.setAttachUuid(hash);
        record.setOssPath(path);
        record.setType(type);
        attachmentsMapper.insertSelective(record);
    }
}
