package com.example.androidlabs;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Test;

public class ExampleInstrumentTest {

    @Test
    public void test() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Assert.assertEquals("id.xxx.example", context.getPackageName());
    }
}
