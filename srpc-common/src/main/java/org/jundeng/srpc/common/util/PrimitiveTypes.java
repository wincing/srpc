package org.jundeng.srpc.common.util;

public enum PrimitiveTypes {
    BOOLEAN(boolean.class),
    BYTE(byte.class),
    SHORT(short.class),
    INT(int.class),
    LONG(long.class),
    FLOAT(float.class),
    DOUBLE(double.class),
    CHAR(char.class),
    STRING(String.class);

    private final Class<?> primitiveClass;

    PrimitiveTypes(Class<?> primitiveClass) {
        this.primitiveClass = primitiveClass;
    }

    public static Class<?> getPrimitiveClass(String typeName) {
        for (PrimitiveTypes type : PrimitiveTypes.values()) {
            if (type.name().equalsIgnoreCase(typeName)) {
                return type.primitiveClass;
            }
        }
        return null;
    }
}
