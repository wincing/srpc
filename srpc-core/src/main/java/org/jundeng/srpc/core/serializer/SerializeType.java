package org.jundeng.srpc.core.serializer;

public enum SerializeType {

    PROTOSTUFF((byte) 1, "protostuff"),
    JSON((byte) 2, "json");

    private final byte value;
    private final String name;

    SerializeType(byte value, String name) {
        this.value = value;
        this.name = name;
    }

    public static SerializeType getSerializeType(byte value) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getValue() == value) {
                return serializeType;
            }
        }
        return null;
    }

    public static SerializeType getSerializeType(String name) {
        for (SerializeType serializeType : SerializeType.values()) {
            if (serializeType.getName().equals(name)) {
                return serializeType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public byte getValue() {
        return value;
    }
}
