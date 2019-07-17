
package com.github.mikephil.charting.buffer;

public class ScatterBuffer extends AbstractBuffer<com.github.mikephil.charting.data.Entry> {
    
    public ScatterBuffer(int size) {
        super(size);
    }

    protected void addForm(float x, float y) {
        buffer[index++] = x;
        buffer[index++] = y;
    }

    @Override
    public void feed(java.util.List<com.github.mikephil.charting.data.Entry> entries) {
        
        float size = entries.size() * phaseX;
        
        for (int i = 0; i < size; i++) {

            com.github.mikephil.charting.data.Entry e = entries.get(i);
            addForm(e.getXIndex(), e.getVal() * phaseY);
        }
        
        reset();
    }
}
