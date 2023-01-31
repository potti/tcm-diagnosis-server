package com.artwook.nft.nftshop.controller.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.websocket.server.PathParam;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.artwook.nft.nftshop.common.BaseResponseCode;
import com.artwook.nft.nftshop.common.BizException;
import com.artwook.nft.nftshop.common.LoginUserContext;
import com.artwook.nft.nftshop.db.mapper.CustomMapper;
import com.artwook.nft.nftshop.db.mapper.DailyRecordMapper;
import com.artwook.nft.nftshop.db.mapper.DictionaryMapper;
import com.artwook.nft.nftshop.db.mapper.MxRecordMapper;
import com.artwook.nft.nftshop.db.mapper.PathologyRecordMapper;
import com.artwook.nft.nftshop.db.mapper.PatientMapper;
import com.artwook.nft.nftshop.db.model.DailyRecord;
import com.artwook.nft.nftshop.db.model.DailyRecordExample;
import com.artwook.nft.nftshop.db.model.Dictionary;
import com.artwook.nft.nftshop.db.model.DictionaryExample;
import com.artwook.nft.nftshop.db.model.MxRecord;
import com.artwook.nft.nftshop.db.model.MxRecordExample;
import com.artwook.nft.nftshop.db.model.PathologyRecord;
import com.artwook.nft.nftshop.db.model.PathologyRecordExample;
import com.artwook.nft.nftshop.db.model.Patient;
import com.artwook.nft.nftshop.db.model.PatientExample;
import com.artwook.nft.nftshop.service.DicService;
import com.artwook.nft.nftshop.utils.RandomUtils;
import com.artwook.nft.nftshop.vo.BaseResponseMessage;
import com.artwook.nft.nftshop.vo.dictionary.DictionaryWithOptionVO;
import com.artwook.nft.nftshop.vo.patient.DictionaryDataVO;
import com.artwook.nft.nftshop.vo.patient.PatientDailyRecordSaveVO;
import com.artwook.nft.nftshop.vo.patient.PatientDailyRecordVO;
import com.artwook.nft.nftshop.vo.patient.PatientInfoVO;

import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("/v1/patient")
public class PatientController {

    @Autowired
    private PatientMapper patientMapper;
    @Autowired
    private DailyRecordMapper dailyRecordMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private CustomMapper customMapper;
    @Autowired
    private PathologyRecordMapper pathologyRecordMapper;
    @Autowired
    private MxRecordMapper mxRecordMapper;
    @Autowired
    private DicService dicService;

    @PostMapping("/queryPatientByPid")
    public BaseResponseMessage<PatientInfoVO> queryPatientByPid(@PathParam("pid") String pid) {
        PatientInfoVO rtn = new PatientInfoVO();
        Patient aPatient = queryPatient(pid);
        if (aPatient != null) {
            rtn.setPatient(aPatient);
            DailyRecordExample example = new DailyRecordExample();
            example.createCriteria().andPidEqualTo(pid);
            example.setOrderByClause("record_date desc");
            List<DailyRecord> list = dailyRecordMapper.selectByExample(example);
            if (!list.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                rtn.setRecordDates(
                        list.stream().map(item -> sdf.format(item.getRecordDate())).collect(Collectors.toList()));
                rtn.setLastestData(queryPatientByPidWithLastRecord(pid, null).getData());
            }
        }
        return new BaseResponseMessage<PatientInfoVO>(rtn);
    }

    @PostMapping("/queryPatientByName")
    public BaseResponseMessage<List<Patient>> queryPatientByName(@PathParam("name") String name) {
        return new BaseResponseMessage<List<Patient>>(queryPatientLike(name));
    }

    @PostMapping("/queryPatientByPidWithLastRecord")
    public BaseResponseMessage<PatientDailyRecordVO> queryPatientByPidWithLastRecord(@PathParam("pid") String pid,
            @PathParam("recordDate") String recordDate) {
        Patient aPatient = queryPatient(pid);

        PatientDailyRecordVO rtn = new PatientDailyRecordVO();
        rtn.setPatient(aPatient);

        DailyRecordExample example = new DailyRecordExample();
        DailyRecordExample.Criteria c = example.createCriteria();
        c.andPidEqualTo(pid);
        if (StringUtils.isEmpty(recordDate)) {
            example.setOrderByClause("record_date desc limit 1");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                c.andRecordDateEqualTo(sdf.parse(recordDate));
            } catch (ParseException e) {
                throw new BizException(BaseResponseCode.ERROR_PARAMILLEGAL);
            }
        }
        List<DailyRecord> list = dailyRecordMapper.selectByExampleWithBLOBs(example);
        if (!list.isEmpty()) {
            DailyRecord aDailyRecord = list.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            rtn.setRecordDate(sdf.format(aDailyRecord.getRecordDate()));
            rtn.setPrescription(aDailyRecord.getPrescription());

            Map<String, Object> mxMap = new HashMap<String, Object>();
            List<DictionaryDataVO> mxList = JSONArray.parseArray(aDailyRecord.getMxContent(), DictionaryDataVO.class);
            for (DictionaryDataVO item : mxList) {
                mxMap.put(String.valueOf(item.getDicIndex()) + '-' + String.valueOf(item.getDicKey()), item.getValue());
            }
            rtn.setMxList(mxMap);
            rtn.setDiagnosis(JSONArray.parseArray(aDailyRecord.getDiagnose(), DictionaryDataVO.class));

            List<List<DictionaryDataVO>> pathologys = new ArrayList<>();
            List<PathologyRecord> records = customMapper.queryPathologyByType(pid, aDailyRecord.getRecordDate());
            for (PathologyRecord record : records) {
                DictionaryExample de = new DictionaryExample();
                de.createCriteria().andDicIndexEqualTo(record.getPathologyType());
                List<Dictionary> dics = dictionaryMapper.selectByExample(de);
                List<DictionaryWithOptionVO> dicWithOpList = dicService.appendOption(dics);

                Map<String, Object> maps = JSONArray.parseArray(record.getPathologyData(), DictionaryDataVO.class)
                        .stream()
                        .collect(Collectors.toMap(DictionaryDataVO::getDicKey, DictionaryDataVO::getValue));
                List<DictionaryDataVO> pathologyList = new ArrayList<>();
                for (DictionaryWithOptionVO dic : dicWithOpList) {
                    DictionaryDataVO vo = new DictionaryDataVO();
                    BeanUtils.copyProperties(dic, vo);
                    if (maps.containsKey(vo.getDicKey())) {
                        if (maps.get(vo.getDicKey()) instanceof String) {
                            String value = (String) maps.get(vo.getDicKey());
                            // 兼容多选框
                            if (value.startsWith("[")) {
                                vo.setValue(JSONArray.parseArray(value, Object.class));
                            } else {
                                vo.setValue(value);
                            }
                        } else {
                            vo.setValue(maps.get(vo.getDicKey()));
                        }
                    }
                    pathologyList.add(vo);
                }
                pathologys.add(pathologyList);
            }
            rtn.setPathologyList(pathologys);
        }
        return new BaseResponseMessage<PatientDailyRecordVO>(rtn);
    }

    private Patient queryPatient(String pid) {
        PatientExample example = new PatientExample();
        example.createCriteria().andPidEqualTo(pid);
        List<Patient> list = patientMapper.selectByExample(example);
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Patient> queryPatientLike(String name) {
        PatientExample example = new PatientExample();
        example.createCriteria().andNameLike("%" + name + "%");
        return patientMapper.selectByExample(example);
    }

    @PostMapping("/createPatient")
    public BaseResponseMessage<String> createPatient(@RequestBody Patient patient) {
        patient.setPid(RandomUtils.nextHexString(8));

        int i = patientMapper.insertSelective(patient);
        if (i != 1) {
            throw new BizException(BaseResponseCode.ERROR_INSERTFAIL);
        }
        return new BaseResponseMessage<String>(patient.getPid());
    }

    @PostMapping("/saveDailyRecord")
    @Transactional
    public BaseResponseMessage<String> saveDailyRecord(@RequestBody PatientDailyRecordSaveVO request) {
        log.info("saveDailyRecord user:{} pid:{}", LoginUserContext.getCurrentUser().getId(), request.getPid());
        Patient aPatient = queryPatient(request.getPid());
        if (aPatient == null) {
            throw new BizException(BaseResponseCode.ERROR_PARAMILLEGAL);
        }

        DailyRecordExample aDailyRecordExample = new DailyRecordExample();
        aDailyRecordExample.createCriteria().andPidEqualTo(request.getPid())
                .andRecordDateEqualTo(request.getRecordDate());
        List<DailyRecord> existList = dailyRecordMapper.selectByExample(aDailyRecordExample);
        if (!existList.isEmpty()) {
            DailyRecord exist = existList.get(0);
            // delete all data first
            int id = exist.getId();
            dailyRecordMapper.deleteByPrimaryKey(id);

            MxRecordExample aMxRecordExample = new MxRecordExample();
            aMxRecordExample.createCriteria().andRecordIdEqualTo(exist.getMxId());
            mxRecordMapper.deleteByExample(aMxRecordExample);

            PathologyRecordExample aPathologyRecordExample = new PathologyRecordExample();
            aPathologyRecordExample.createCriteria().andPidEqualTo(request.getPid())
                    .andDateEqualTo(request.getRecordDate());
            pathologyRecordMapper.deleteByExample(aPathologyRecordExample);
        }

        Map<String, List<Dictionary>> dicTypeMap = null;

        DailyRecord record = new DailyRecord();
        record.setPid(request.getPid());
        record.setRecordDate(request.getRecordDate());
        record.setMxId(RandomUtils.nextHexString(10));
        record.setMxContent(JSONObject.toJSONString(request.getMxList()));
        record.setDiagnose(JSONObject.toJSONString(request.getDiagnosis()));
        record.setPrescription(request.getPrescription());
        record.setUserId(LoginUserContext.getCurrentUser().getId());
        dailyRecordMapper.insertSelective(record);

        List<MxRecord> insertList = new ArrayList<>();
        for (DictionaryDataVO vo : request.getMxList()) {
            MxRecord aMxRecord = new MxRecord();

            aMxRecord.setRecordId(record.getMxId());
            aMxRecord.setDicIndex(vo.getDicIndex());
            aMxRecord.setDicKey(vo.getDicKey());
            aMxRecord.setMxDicIndex(vo.getValueDicIndex());

            String value = (String) vo.getValue();
            try {
                Integer.parseInt(value);
                aMxRecord.setMxDicKey((String) value);
                insertList.add(aMxRecord);
            } catch (Exception e) {
                if (dicTypeMap == null) {
                    DictionaryExample example = new DictionaryExample();
                    example.createCriteria().andDicIndexEqualTo(vo.getValueDicIndex());
                    dicTypeMap = dictionaryMapper.selectByExample(example).stream()
                            .collect(Collectors.groupingBy(Dictionary::getKeyDesc));
                }
                Map<String, Integer> mxMap = new HashMap<>();
                String key = "";
                for (int i = 0; i < value.length(); i++) {
                    if (value.charAt(i) >= 48 && value.charAt(i) <= 57) {
                        mxMap.put(key, (int) (value.charAt(i) - '0'));
                        key = "";
                    } else {
                        key += String.valueOf(value.charAt(i));
                    }
                }

                for (Map.Entry<String, Integer> mxValue : mxMap.entrySet()) {
                    MxRecord copy = new MxRecord();
                    BeanUtils.copyProperties(aMxRecord, copy);
                    copy.setMxDicKey(dicTypeMap.get(mxValue.getKey()).get(0).getDicKey());
                    copy.setMxValue(mxValue.getValue());
                    insertList.add(copy);
                }
            }
        }
        int j = customMapper.insertMxRecordBatch(insertList);
        if (j != insertList.size()) {
            throw new BizException(BaseResponseCode.ERROR_INSERTFAIL);
        }

        for (Map.Entry<Integer, List<DictionaryDataVO>> entry : request.getPathologyMap().entrySet()) {
            PathologyRecord pr = new PathologyRecord();
            pr.setPid(request.getPid());
            pr.setDate(record.getRecordDate());
            pr.setPathologyType(entry.getKey());
            pr.setPathologyData(JSONObject.toJSONString(entry.getValue()));
            pathologyRecordMapper.insertSelective(pr);
        }

        return new BaseResponseMessage<String>(BaseResponseCode.SUCCESS_CODE);
    }

}
