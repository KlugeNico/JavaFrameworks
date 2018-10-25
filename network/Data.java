package de.bastard.frameworks.network;

import com.badlogic.gdx.graphics.Color;
import de.bastard.frameworks.util.Position;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class Data {

    private byte[] data;
    private int pointer;
    private LinkedList<Entry> newData;
    private boolean reading;

    public Data() {
        this(new byte[0]);
    }

    public Data(byte[] data) {
        this.data = data;
        this.pointer = 0;
        this.reading = true;
        this.newData = new LinkedList<>();
    }

    public Data writingMode() {
        reading = false;
        return this;
    }

    public Data readingMode() {
        reading = true;
        makeReadable();
        return this;
    }

    public byte storeByte(byte value) {
        if (reading)
            return readByte();
        else
            writeByte(value);
        return value;
    }

    public short storeShort(short value) {
        if (reading)
            return readShort();
        else
            writeShort(value);
        return value;
    }

    public int storeInt(int value) {
        if (reading)
            return readInt();
        else
            writeInt(value);
        return value;
    }

    private float storeFloat(float value) {
        if (reading)
            return readFloat();
        else
            writeFloat(value);
        return value;
    }

    private float readFloat() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        byte[] byteArray = new byte[4];
        System.arraycopy(data, pointer, byteArray, 0, 4);
        pointer += byteArray.length;
        return ByteBuffer.wrap(byteArray).getFloat();
    }

    private void writeFloat(float value) {
        byte[] byteArray = new byte[4];
        ByteBuffer.wrap(byteArray).putFloat(value);
        newData.add(new Entry(pointer, byteArray));
        pointer += byteArray.length;
    }

    public boolean storeBoolean(boolean value) {
        if (reading)
            return readBoolean();
        else
            writeBoolean(value);
        return value;
    }

    public Data writeBoolean(boolean value) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeNumber(value ? 1 : 0, 1);
        return this;
    }

    private boolean readBoolean() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        return readNumber(1) != 0;
    }

    public Transferable storeTransferable(Transferable transferable, Context world) {
        if (reading)
            return readTransferable(world);
        else
            writeTransferable(transferable);
        return transferable;
    }

    public <T extends Transferable> Collection<T> storeCollection(Collection<T> list, Context context) {
        if (reading)
            return readCollection(context);
        else
            writeCollection(list);
        return list;
    }

    public Data writeByte(byte value) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeNumber(value, 1);
        return this;
    }

    public Data writeShort(short value) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeNumber(value, 2);
        return this;
    }

    public Data writeInt(int value) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeNumber(value, 4);
        return this;
    }

    public Data writeTransferable(Transferable transferable) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeInt(transferable.getTypeId());
        transferable.storeData(this);
        return this;
    }

    public <T extends Transferable> Data writeCollection(Collection<T> list) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeShort((short)list.size());
        for (Transferable transferable : list) {
            writeTransferable(transferable);
        }
        return this;
    }

    public byte readByte() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        return (byte) readNumber(1);
    }

    public short readShort() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        return (short) readNumber(2);
    }

    public int readInt() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        return (int) readNumber(4);
    }

    public Transferable readTransferable(Context context) {
        if (!reading)
            throw new IllegalStateException("Call readingMode before writing!");
        short modId = readShort();
        short typeId = readShort();
        Mod mod = context.getMod(modId);
        if (mod == null)
            throw new IllegalStateException("Mod " + modId + " with element " + typeId + " does not exist! Pointer at: " + pointer);
        Transferable transferable = mod.getInstanceById(typeId);
        if (transferable == null)
            throw new IllegalStateException("Serializing Error! Object with typeId " + typeId + " in Mod " + mod + " does not exist!");
        transferable.setContext(context);
        transferable.storeData(this);
        return transferable;
    }

    public <T extends Transferable> Collection<T> readCollection(Context context) {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        Collection<T> list = new LinkedList<>();
        short size = readShort();
        for (int i = 0; i < size; i++) {
            list.add((T)readTransferable(context));
        }
        return list;
    }

    public byte[] getData() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        return data;
    }

    public byte[] readRest() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        byte[] rest = Arrays.copyOfRange(data, pointer, data.length);
        pointer = data.length;
        return rest;
    }

    private long readNumber(int size) {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        long number = 0L;
        for (int i = 0; i < size; ++i) {
            number |= (data[pointer + ((size-1)-i)] & 0xff) << (i << 3);
        }
        pointer += size;
        return number;
    }

    private void writeNumber(long number, int size) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        byte[] byteArray = new byte[size];
        for (int i = 0; i < size; ++i) {
            int shift = i << 3; // i * 8
            byteArray[(size-1)-i] = (byte)((number & (0xff << shift)) >>> shift);
        }
        appendNewData(byteArray);
    }

    public Data appendDataRash(byte[] byteArray) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        appendNewData(byteArray);
        return this;
    }

    private void appendNewData(byte[] byteArray) {
        newData.add(new Entry(pointer, byteArray));
        pointer += byteArray.length;
    }

    private void makeReadable() {
        if (!newData.isEmpty()) {
            int newSize = data.length;
            for (Entry entry : newData) {
                if (entry.start + entry.data.length > newSize)
                    newSize = entry.start + entry.data.length;
            }
            if (newSize > Short.MAX_VALUE)
                throw new IllegalStateException("Data to long!");
            if (newSize > data.length)
                data = Arrays.copyOf(data, newSize);
            for (Entry entry : newData) {
                System.arraycopy(entry.data, 0, data, entry.start, entry.data.length);
            }
            newData.clear();
        }
        reading = true;
    }

    public boolean reachedEnd() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        return pointer >= data.length;
    }

    public boolean isReadMode() {
        return reading;
    }

    public Data readFromBeginning() {
        readingMode();
        pointer = 0;
        return this;
    }

    public Data setPointer(int position) {
        pointer = position;
        return this;
    }

    public int[] storeIntArray(int[] array) {
        if (reading)
            return readIntArray();
        else
            writeIntArray(array);
        return array;
    }

    public Data writeIntArray(int[] array) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeShort((short)array.length);
        ByteBuffer byteBuffer = ByteBuffer.allocate(array.length * 4);
        for (int i = 0; i < array.length; i++)
            byteBuffer.putInt(i*4, array[i]);
        appendNewData(byteBuffer.array());
        return this;
    }

    public int[] readIntArray() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        short size = readShort();
        int[] array = new int[size];
        for (int i = 0; i < array.length; i++)
            array[i] = ByteBuffer.wrap(Arrays.copyOfRange(data, pointer + i*4, pointer + i*4 + 4)).getInt();
        pointer += array.length * 4;
        return array;
    }

    public byte[] storeByteArray(byte[] array) {
        if (reading)
            return readByteArray();
        else
            writeByteArray(array);
        return array;
    }

    public Data writeByteArray(byte[] array) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeShort((short)array.length);
        appendNewData(Arrays.copyOf(array, array.length));
        return this;
    }

    public byte[] readByteArray() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        short size = readShort();
        byte[] array = Arrays.copyOfRange(data, pointer, pointer + size);
        pointer += array.length;
        return array;
    }

    public String storeString(String value) {
        if (reading)
            return readString();
        else
            writeString(value);
        return value;
    }

    public Data writeString(String value) {
        if (reading)
            throw new IllegalStateException("Call writingMode before writing!");
        writeShort((short)value.length());
        newData.add(new Entry(pointer, value.getBytes()));
        pointer += value.length();
        return this;
    }

    private String readString() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        short size = readShort();
        String string = new String(Arrays.copyOfRange(data, pointer, pointer + size));
        pointer += string.length();
        return string;
    }

    public int size() {
        if (!reading)
            throw new IllegalStateException("Call readingMode before reading!");
        return data.length;
    }

    public Position storePosition(Position pos) {
        pos.x = storeShort((short)pos.x);
        pos.y = storeShort((short)pos.y);
        return pos;
    }

    public Color storeColor(Color color) {
        color.r = storeFloat(color.r);
        color.g = storeFloat(color.g);
        color.b = storeFloat(color.b);
        color.a = storeFloat(color.a);
        return color;
    }

    private class Entry {
        int start;
        byte[] data;
        Entry(int start, byte[] data) {
            this.start = start;
            this.data = data;
        }
    }

}
