package com.example.androidlabs.data.network;

import com.example.androidlabs.data.network.response.GuardianResponse;

import org.junit.Assert;
import org.junit.Test;

public class GuardianApiTest {

    @Test
    public void searchDefault() {
        try {
            GuardianResponse guardianResponse = GuardianApi.search("pillar-name=sport");
            Assert.assertNotNull(guardianResponse);
//            Assert.assertEquals(20, guardianResponse.getResults().size());
            System.out.println(guardianResponse.getResults());
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}