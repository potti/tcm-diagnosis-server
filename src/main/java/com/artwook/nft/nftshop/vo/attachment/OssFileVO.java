package com.artwook.nft.nftshop.vo.attachment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
@Accessors(chain = true)
public class OssFileVO {

    private String path;

    private String url;

    private String name;

}
