package com.artwook.nft.nftshop.controller.user;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.artwook.nft.nftshop.common.PassToken;
import com.artwook.nft.nftshop.db.mapper.CustomMapper;
import com.artwook.nft.nftshop.db.mapper.DictionaryMapper;
import com.artwook.nft.nftshop.db.model.Dictionary;
import com.artwook.nft.nftshop.db.model.DictionaryExample;
import com.artwook.nft.nftshop.service.DicService;
import com.artwook.nft.nftshop.vo.BaseResponseMessage;
import com.artwook.nft.nftshop.vo.dictionary.DictionaryWithOptionVO;
import com.artwook.nft.nftshop.vo.dictionary.UpdateDicRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/v1/dic")
public class DictionaryController {

    @Autowired
    private CustomMapper customMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private DicService dicService;

    @PostConstruct
    private void init() {
        dictionaryMapper.selectByExample(null);
    }

    @PassToken
    @PostMapping("/getDicList")
    public BaseResponseMessage<List<Dictionary>> getDicList() {
        return new BaseResponseMessage<List<Dictionary>>(customMapper.queryDicTypeList());
    }

    @PassToken
    @PostMapping("/getDicDataList")
    public BaseResponseMessage<List<?>> getDicDataList(@PathParam("dicIndex") Integer dicIndex) {
        DictionaryExample example = new DictionaryExample();
        example.createCriteria().andDicIndexEqualTo(dicIndex);
        List<Dictionary> list = dictionaryMapper.selectByExample(example);
        Map<Integer, List<Dictionary>> map = list.stream()
                .collect(Collectors.groupingBy(Dictionary::getValueDicIndex));
        if (map.isEmpty() || (map.size() == 1 && map.containsKey(Integer.valueOf(0)))) {
            return new BaseResponseMessage<List<?>>(list);
        }
        return new BaseResponseMessage<List<?>>(dicService.appendOption(list));
    }

    @PostMapping("/saveOrUpdate")
    public BaseResponseMessage<List<Dictionary>> saveOrUpdate(@RequestBody UpdateDicRequest request) {

        if (!CollectionUtils.isEmpty(request.getDelIds())) {
            for (Integer id : request.getDelIds()) {
                dictionaryMapper.deleteByPrimaryKey(id);
            }
        }

        List<Dictionary> rtn = null;
        if (!CollectionUtils.isEmpty(request.getDicList())) {
            if (null == request.getDicList().get(0).getDicIndex()) {
                DictionaryExample example = new DictionaryExample();
                example.setOrderByClause("id desc limit 1");
                List<Dictionary> list = dictionaryMapper.selectByExample(example);
                int newDicIndex = 0;
                if (!list.isEmpty()) {
                    newDicIndex = list.get(0).getId() + 1;
                }
                for (Dictionary dictionary : request.getDicList()) {
                    dictionary.setDicIndex(newDicIndex);
                }
            }
            for (int i = 0; i < request.getDicList().size(); i++) {
                if (StringUtils.isEmpty(request.getDicList().get(i).getDicKey())) {
                    // DicKey is varchar in db , so start with 10 is good for sort
                    request.getDicList().get(i).setDicKey(String.valueOf(i + 10));
                }
            }
            int i = customMapper.saveOrUpdateDictionary(request.getDicList());
            log.info("saveOrUpdate rows:{} size:{}", i, request.getDicList().size());
            // if (i != request.getDicList().size()) {
            // throw new BizException(BaseResponseCode.ERROR_INSERTFAIL);
            // }
            DictionaryExample example = new DictionaryExample();
            example.createCriteria().andDicIndexEqualTo(request.getDicList().get(0).getDicIndex());
            rtn = dictionaryMapper.selectByExample(example);
        }
        return new BaseResponseMessage<List<Dictionary>>(rtn);
    }

    @PostMapping("/getDicDataListByName")
    public BaseResponseMessage<List<DictionaryWithOptionVO>> getDicDataListByName(
            @PathParam("dicName") String dicName) {
        DictionaryExample example = new DictionaryExample();
        DictionaryExample.Criteria c = example.createCriteria();
        if (!StringUtils.isEmpty(dicName)) {
            c.andDicNameEqualTo(dicName);
        }
        example.setOrderByClause("dic_key");
        List<Dictionary> list = dictionaryMapper.selectByExample(example);
        return new BaseResponseMessage<List<DictionaryWithOptionVO>>(dicService.appendOption(list));
    }
}
