/*
 * Copyright (C) 2015 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.databinding.testapp;

import com.android.databinding.library.DataBinderTrojan;
import com.android.databinding.testapp.generated.NewApiLayoutBinder;

import android.content.Context;
import android.os.Build;
import android.test.UiThreadTest;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class NewApiTest extends BaseDataBinderTest<NewApiLayoutBinder> {
    public NewApiTest() {
        super(NewApiLayoutBinder.class, R.layout.new_api_layout);
    }

    @UiThreadTest
    public void testSetElevation() {
        mBinder.setElevation(3);
        mBinder.setName("foo");
        mBinder.setChildren(new ArrayList<View>());
        mBinder.rebindDirty();
        assertEquals("foo", mBinder.getTextView().getText().toString());
        assertEquals(3f, mBinder.getTextView().getElevation());
    }

    @UiThreadTest
    public void testSetElevationOlderAPI() {
        DataBinderTrojan.setBuildSdkInt(1);
        try {
            TextView textView = mBinder.getTextView();
            float originalElevation = textView.getElevation();
            mBinder.setElevation(3);
            mBinder.setName("foo2");
            mBinder.rebindDirty();
            assertEquals("foo2", textView.getText().toString());
            assertEquals(originalElevation, textView.getElevation());
        } finally {
            DataBinderTrojan.setBuildSdkInt(Build.VERSION.SDK_INT);
        }
    }

    @UiThreadTest
    public void testGeneric() {
        ArrayList<View> views = new ArrayList<>();
        mBinder.setChildren(views);
        mBinder.rebindDirty();
        assertEquals(1, views.size());
        assertSame(mBinder.getTextView(), views.get(0));
    }

    @UiThreadTest
    public void testGenericOlderApi() {
        DataBinderTrojan.setBuildSdkInt(1);
        try {
            ArrayList<View> views = new ArrayList<>();
            mBinder.setChildren(views);
            mBinder.rebindDirty();
            // we should not call the api on older platforms.
            assertEquals(0, views.size());
        } finally {
            DataBinderTrojan.setBuildSdkInt(Build.VERSION.SDK_INT);
        }
    }
}
