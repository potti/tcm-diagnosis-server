package com.artwook.nft.nftshop.vo.dictionary;

import java.util.List;

import com.artwook.nft.nftshop.db.model.Dictionary;

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
public class UpdateDicRequest {

    private List<Integer> delIds;

    private List<Dictionary> dicList;
}
