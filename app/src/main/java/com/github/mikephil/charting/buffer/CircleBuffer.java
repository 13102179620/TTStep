
package com.github.mikephil.charting.buffer;

public class CircleBuffer extends AbstractBuffer<com.github.mikephil.charting.data.Entry> {

    public CircleBuffer(int size) {
        super(size);
    }

    protected void addCircle(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(java.util.List<com.github.mikephil.charting.data.Entry> entries) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseX + mFrom);

        for (int i = mFrom; i < size; i++) {

            com.github.mikephil.charting.data.Entry e = entries.get(i);
            addCircle(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
