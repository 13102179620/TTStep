
package com.github.mikephil.charting.buffer;

public class LineBuffer extends AbstractBuffer<com.github.mikephil.charting.data.Entry> {

    public LineBuffer(int size) {
        super((size < 4) ? 4 : size);
    }

    public void moveTo(float x, float y) {

        if (index != 0)
            return;

        buffer[index++] = x;
        buffer[index++] = y;

        // in case just one entry, this is overwritten when lineTo is called
        buffer[index] = x;
        buffer[index + 1] = y;
    }

    public void lineTo(float x, float y) {

        if (index == 2) {
            buffer[index++] = x;
            buffer[index++] = y;
        } else {

            float prevX = buffer[index - 2];
            float prevY = buffer[index - 1];
            buffer[index++] = prevX;
            buffer[index++] = prevY;
            buffer[index++] = x;
            buffer[index++] = y;
        }
    }

    @Override
    public void feed(java.util.List<com.github.mikephil.charting.data.Entry> entries) {
        moveTo(entries.get(mFrom).getXIndex(), entries.get(mFrom).getVal() * phaseY);

        int size = (int) Math.ceil((mTo - mFrom) * phaseX + mFrom);
        int from = mFrom + 1;

        for (int i = from; i < size; i++) {

            com.github.mikephil.charting.data.Entry e = entries.get(i);
            lineTo(e.getXIndex(), e.getVal() * phaseY);
        }

        reset();
    }
}
