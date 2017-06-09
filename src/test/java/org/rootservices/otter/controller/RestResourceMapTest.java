package org.rootservices.otter.controller;


import helper.entity.FakeRestResourceMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.rootservices.otter.translator.JsonTranslator;

public class RestResourceMapTest {
    @Mock
    private JsonTranslator mockJsonTranslator;
    private RestResource subject;

    @Before
    public void setUp() {
        subject = new FakeRestResourceMap(mockJsonTranslator);
    }

    @Test
    public void foo() {

    }
}
