package com.hxg.sofa.jraft.rhea.rpc;

import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.serialization.SerializerManager;

  
public final class ExtSerializerSupports {

    private static final InvokeContext INVOKE_CONTEXT = new InvokeContext();

    public static byte                 PROTO_STUFF    = 2;

    static {
        SerializerManager.addSerializer(PROTO_STUFF, ProtostuffSerializer.INSTANCE);
        INVOKE_CONTEXT.put(InvokeContext.BOLT_CUSTOM_SERIALIZER, PROTO_STUFF);
        INVOKE_CONTEXT.put(InvokeContext.BOLT_CRC_SWITCH, false);
    }

    public static void init() {
        // Will execute the code first of the static block
    }

    public static InvokeContext getInvokeContext() {
        return INVOKE_CONTEXT;
    }

    private ExtSerializerSupports() {
    }
}
