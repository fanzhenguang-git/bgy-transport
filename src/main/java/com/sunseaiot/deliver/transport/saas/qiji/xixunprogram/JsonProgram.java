/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.transport.saas.qiji.xixunprogram;

import com.alibaba.fastjson.JSONObject;
import com.sunseaiot.deliver.transport.saas.qiji.util.CreateFileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设置节目json文件
 *
 * @author: xwb
 * @create: 2019/08/29 16:04
 */
@Slf4j
public class JsonProgram {

    public static boolean makeProgramConfigFile(MakeProgramFields makeProgramFields, List<ProgramMater> programMaterList, String targetFileUrl) {

        // 组装物管平台需要的数据
        Map<String, Object> programJson = new HashMap<>();
        List<Map<String, Object>> programs = new ArrayList<>();
        Map<String, Object> program = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        if (CollectionUtils.isEmpty(programMaterList)) {
            return false;
        }
        Map<Long, ProgramMater> programMap = new HashMap<>();
        for (ProgramMater p : programMaterList) {
            programMap.put(p.getId(), p);
        }
        Long[] ids = makeProgramFields.getMaterialIds();
        if (ids == null) {
            return false;
        }
        for (int i = 1; i <= ids.length; i++) {
            ProgramMater pm = programMap.get(ids[i - 1]);
            Map<String, Object> m = new HashMap<>();
            m.put("Index", i);
            // 对素材类型进行转换
            Integer materType = pm.getType();
            String materTypeName = materType == 1 ? "Image" : "Video";
            m.put("Type", materTypeName);
            Map<String, Object> fileTime = new HashMap<>();
            fileTime.put("File", pm.getUrl());
            if (materType == 1) {
                fileTime.put("Time", makeProgramFields.getPlayTime());
            } else {
                fileTime.put("Repeat", 1);
            }
            m.put(materTypeName, fileTime);
            items.add(m);
        }
        program.put("Name", "program 1");
        program.put("Index", 1);
        program.put("Items", items);
        programs.add(program);
        programJson.put("Version", 1);
        programJson.put("Programs", programs);
        // 将programJson转成json并存储在program.json文件中
        String jsonString = JSONObject.toJSONString(programJson);
        log.info("program json file content is={}", jsonString);
        boolean b = CreateFileUtil.createJsonFile(jsonString, targetFileUrl, "test");
        return b;
    }
}
