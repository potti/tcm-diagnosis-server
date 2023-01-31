package com.artwook.nft.nftshop.vo.patient;

import java.util.List;

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
public class PatientInfoVO {
    private Patient patient;

    private List<String> recordDates;

    private PatientDailyRecordVO lastestData;
}
