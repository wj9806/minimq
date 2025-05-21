package io.github.wj9806.minimq.broker.core.data;

public class Message {

    /**
     * 消息大小，单位字节
     */
//    private int size;

    /**
     * 消息内同
     */
    private byte[] content;

//    public int getSize() {
//        return size;
//    }
//
//    public void setSize(int size) {
//        this.size = size;
//    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] toBytes() {
        //byte[] sizeByte = ByteUtils.intToBytes(this.getSize());
//        byte[] content = this.getContent();
//
//        byte[] msg = new byte[sizeByte.length + content.length];
//        System.arraycopy(sizeByte, 0, msg, 0, sizeByte.length);
//        System.arraycopy(content, 0, msg, sizeByte.length, content.length);
        return getContent();
    }
}
