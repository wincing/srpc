package org.jundeng.sprc.common.extension;

import org.jundeng.sprc.common.extension.ext1.Ext1;
import org.jundeng.srpc.common.extension.ExtensionLoader;
import org.junit.Test;

public class ExtensionTest {
    @Test
    public void testDefaultExtension() {
        Ext1 extension = ExtensionLoader.getExtensionLoader(Ext1.class).getDefaultExtension();
        extension.print();
    }

    @Test
    public void testExtensionByValue() {
        Ext1 extension = ExtensionLoader.getExtensionLoader(Ext1.class).getExtension("impl2");
        extension.print();
    }
}
