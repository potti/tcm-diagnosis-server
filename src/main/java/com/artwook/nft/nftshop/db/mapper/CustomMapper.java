package com.artwook.nft.nftshop.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.artwook.nft.nftshop.db.model.Dictionary;
import com.artwook.nft.nftshop.db.model.MxRecord;
import com.artwook.nft.nftshop.db.model.PathologyRecord;

public interface CustomMapper {

        @Select("select dic_index as dicIndex,dic_name as dicName from `tb_dictionary` group by dic_index,dic_name")
        List<Dictionary> queryDicTypeList();

        @Insert("<script>"
                        + " insert into tb_dictionary (dic_index, dic_name, dic_key, key_desc, value_type, value_dic_index) values "
                        + " <foreach collection = 'list' item = 'item' index = 'index' separator = ','> "
                        + " (#{item.dicIndex}, #{item.dicName}, #{item.dicKey}, #{item.keyDesc}, #{item.valueType},#{item.valueDicIndex}) "
                        + " </foreach>"
                        + " ON DUPLICATE KEY UPDATE"
                        + " dic_name=values(dic_name),"
                        + " key_desc=values(key_desc),"
                        + " value_type=values(value_type),"
                        + " value_dic_index=values(value_dic_index)"
                        + " </script>")
        int saveOrUpdateDictionary(@Param("list") List<Dictionary> list);

        @Insert("<script>"
                        + " insert into tb_mx_record (record_id, dic_index, dic_key, mx_dic_index, mx_dic_key, mx_value) values "
                        + " <foreach collection = 'list' item = 'item' index = 'index' separator = ','> "
                        + " (#{item.recordId,jdbcType=VARCHAR}, #{item.dicIndex,jdbcType=INTEGER}, #{item.dicKey,jdbcType=INTEGER}, #{item.mxDicIndex,jdbcType=INTEGER}, #{item.mxDicKey,jdbcType=INTEGER}, #{item.mxValue,jdbcType=INTEGER}) "
                        + " </foreach>"
                        + " </script>")
        int insertMxRecordBatch(@Param("list") List<MxRecord> list);

        @Select("select a.pathology_type as pathologyType,a.pathology_data as pathologyData from `tb_pathology_record` a "
                        + "inner join (select max(id) as id  from `tb_pathology_record` where pid = #{pid,jdbcType=VARCHAR} and date <= #{date,jdbcType=DATE} group by pathology_type) b on a.id = b.id order by a.pathology_type")
        List<PathologyRecord> queryPathologyByType(@Param("pid") String pid, @Param("date") Date date);

}
