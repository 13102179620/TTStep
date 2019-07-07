
package com.github.mikephil.charting.buffer;

public class CandleBodyBuffer extends AbstractBuffer<com.github.mikephil.charting.data.CandleEntry> {
    
    private float mBodySpace = 0f;

    public CandleBodyBuffer(int size) {
        super(size);
    }
    
    public void setBodySpace(float bodySpace) {
        this.mBodySpace = bodySpace;
    }

    private void addBody(float left, float top, float right, float bottom) {

        buffer[index++] = left;
        buffer[index++] = top;
        buffer[index++] = right;
        buffer[index++] = bottom;
    }

    @Override
    public void feed(java.util.List<com.github.mikephil.charting.data.CandleEntry> entries) {

        int size = (int)Math.ceil((mTo - mFrom) * phaseX + mFrom);

        for (int i = mFrom; i < size; i++) {

            com.github.mikephil.charting.data.CandleEntry e = entries.get(i);
            addBody(e.getXIndex() - 0.5f + mBodySpace, e.getClose() * phaseY, e.getXIndex() + 0.5f - mBodySpace, e.getOpen() * phaseY);
        }

        reset();
    }
}
