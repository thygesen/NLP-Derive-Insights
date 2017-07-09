package com.opennlp.insights;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import opennlp.tools.util.Span;

import java.io.Serializable;

/**
 * Created by thygesen on 09/07/2017.
 */
public class NameSampleSerializer extends Serializer<NameSample> implements Serializable {

    @Override
    public void write(Kryo kryo, Output output, NameSample nameSample) {
        output.writeString(nameSample.getId());
        if(nameSample.getSentence() != null) {
            output.writeInt(nameSample.getSentence().length);
            for (String sentence : nameSample.getSentence())
                output.writeString(sentence);
        } else {
            output.writeInt(0);
        }
        if(nameSample.getAdditionalContext() != null) {
            output.writeInt(nameSample.getAdditionalContext().length);
            for (String[] strings : nameSample.getAdditionalContext()) {
                output.writeInt(strings.length);
                for (String ctx : strings)
                    output.writeString(ctx);
            }
        } else
            output.writeInt(0);
        if(nameSample.getNames() != null) {
            output.writeInt(nameSample.getNames().length);
            for (Span span : nameSample.getNames()) {
                output.writeInt(span.getStart());
                output.writeInt(span.getEnd());
                output.writeString(span.getType());
                output.writeDouble(span.getProb());
            }
        } else {
            output.writeInt(0);
        }
        output.writeBoolean(nameSample.isClearAdaptiveDataSet());

    }

    @Override
    public NameSample read(Kryo kryo, Input input, Class<NameSample> aClass) {
        String id = input.readString();
        int sentenceLength = input.readInt();
        String[] sentences = new String[sentenceLength];
        for(int i=0; i<sentenceLength; i++) {
            sentences[i] = input.readString();
        }
        int ctxLength = input.readInt();
        String[][] ctx = null;
        if(ctxLength>0) {
            ctx = new String[ctxLength][];
            for(int i=0; i<ctxLength; i++) {
                int len = input.readInt();
                ctx[i] = new String[len];
                for(int j=0; j<len; j++)
                    ctx[i][j] = input.readString();
            }
        }
        int spanLen = input.readInt();
        Span[] names = new Span[spanLen];
        for(int i=0; i<spanLen; i++) {
            int begin = input.readInt();
            int end = input.readInt();
            String type = input.readString();
            double prop = input.readDouble();
            names[i] = new Span(begin, end, type, prop);
        }
        boolean clearAdaptiveData = input.readBoolean();
        return new NameSample(id, sentences, names, ctx, clearAdaptiveData);
    }
}

