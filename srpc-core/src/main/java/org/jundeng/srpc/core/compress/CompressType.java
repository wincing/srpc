package org.jundeng.srpc.core.compress;

public enum CompressType {

    DEFAULT((byte) 0, "default"),
    GZIP((byte) 1, "gzip");

    private final byte value;
    private final String name;

    public static CompressType getCompressType(byte value) {
        for (CompressType compressType : CompressType.values()) {
            if (compressType.getValue() == value) {
                return compressType;
            }
        }
        return null;
    }

    public static CompressType getCompressType(String name) {
        for (CompressType compressType : CompressType.values()) {
            if (compressType.getName().equals(name)) {
                return compressType;
            }
        }
        return null;
    }


    CompressType(byte value, String name) {
        this.value = value;
        this.name = name;
    }

    public byte getValue() {
        return value;
    }

    public String getName() {
        return name;
    }
}
