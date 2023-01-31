package com.artwook.nft.nftshop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.artwook.nft.nftshop.db.mapper.DictionaryMapper;
import com.artwook.nft.nftshop.db.model.Dictionary;
import com.artwook.nft.nftshop.db.model.DictionaryExample;
import com.artwook.nft.nftshop.vo.dictionary.DictionaryWithOptionVO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class DicService {

    @Autowired
    private DictionaryMapper dictionaryMapper;

    public List<DictionaryWithOptionVO> appendOption(List<Dictionary> originalList) {
        Map<Integer, List<Dictionary>> map = originalList.stream()
                .collect(Collectors.groupingBy(Dictionary::getValueDicIndex));
        List<DictionaryWithOptionVO> rtn = new ArrayList<>();
        for (Dictionary dictionary : originalList) {
            DictionaryWithOptionVO vo = new DictionaryWithOptionVO();
            BeanUtils.copyProperties(dictionary, vo);
            rtn.add(vo);
        }
        if (map.isEmpty() || (map.size() == 1 && map.containsKey(Integer.valueOf(0)))) {
            return rtn;
        }
        List<Integer> indexs = new ArrayList<Integer>();
        indexs.addAll(map.keySet());
        DictionaryExample example = new DictionaryExample();
        example.createCriteria().andDicIndexIn(indexs);
        List<Dictionary> optionlist = dictionaryMapper.selectByExample(example);
        Map<Integer, List<Dictionary>> optionMap = optionlist.stream()
                .collect(Collectors.groupingBy(Dictionary::getDicIndex));
        for (DictionaryWithOptionVO vo : rtn) {
            vo.setOptions(optionMap.get(vo.getValueDicIndex()));
        }
        return rtn;
    }
}
