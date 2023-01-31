package com.artwook.nft.nftshop.vo.patient;

import java.util.List;
import java.util.Map;

import com.artwook.nft.nftshop.db.model.Patient;

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
public class PatientDailyRecordVO {

    private String pid;
    private Patient patient;
    private String recordDate;
    private List<DictionaryDataVO> diagnosis;
    private Map<String, Object> mxList;
    private String prescription;
    private List<List<DictionaryDataVO>> pathologyList;
}
