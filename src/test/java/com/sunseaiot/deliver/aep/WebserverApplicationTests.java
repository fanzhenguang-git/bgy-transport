/*
 * Copyright © 2019 SunseaIoT
 */
package com.sunseaiot.deliver.aep;

import com.ctg.ag.sdk.biz.AepDeviceCommandClient;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandRequest;
import com.ctg.ag.sdk.biz.aep_device_command.CreateCommandResponse;
import com.ctg.ag.sdk.biz.aep_device_command.QueryCommandRequest;
import com.google.gson.JsonObject;
import com.sunseaiot.deliver.DeliverTransportApplication;
import com.sunseaiot.deliver.util.RestUtils;
import com.sunseaiot.deliver.vo.RemoteInstructionCreateVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DeliverTransportApplication.class)
public class WebserverApplicationTests {

    @Test
    public void queryCommandDevice() throws Exception {
        AepDeviceCommandClient client = AepDeviceCommandClient.newClient()
                .appKey("9rKLWATcMD5").appSecret("mjJqHj4Fao")
                .build();

        CreateCommandRequest request = new CreateCommandRequest();
        request.addParamMasterKey("d2439e54e9a74328af3139cc5af69eb3");
        RemoteInstructionCreateVO remoteInstructionCreateVO = new RemoteInstructionCreateVO();
        remoteInstructionCreateVO.setDeviceId("f1241cc8c7164205868fcf00b76bb875");
        remoteInstructionCreateVO.setOperator("lu");
        remoteInstructionCreateVO.setProductId(10029901);
        remoteInstructionCreateVO.setTtl(10);

        JsonObject content = new JsonObject();
        JsonObject params = new JsonObject();
        params.addProperty("muffling", 0);
        content.add("params", params);
        content.addProperty("serviceIdentifier", "cmd");
        remoteInstructionCreateVO.setContent(content);
        byte[] bytes = RestUtils.convert2Bytes(remoteInstructionCreateVO);
        request.setBody(bytes);
        CreateCommandResponse response = client.CreateCommand(request);
        System.out.println("=================== \n" + response);
    }
}