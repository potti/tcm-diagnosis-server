package com.artwook.nft.nftshop.vo.patient;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class PatientDailyRecordSaveVO {

    private String pid;
    private Date recordDate;
    private List<DictionaryDataVO> diagnosis;
    private List<DictionaryDataVO> mxList;
    private String prescription;
    private Map<Integer, List<DictionaryDataVO>> pathologyMap;
}
