package cn.cloudwalk.smartframework.transport.support.disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;

public class ChannelEventHandler implements EventHandler<ChannelEvent> , SequenceReportingEventHandler<ChannelEvent> {

    private static final int NOTIFY_PROGRESS_THRESHOLD = 50;
    private Sequence sequenceCallback;
    private int counter;

    @Override
    public void setSequenceCallback(Sequence sequenceCallback) {
        this.sequenceCallback = sequenceCallback;
    }

    @Override
    public void onEvent(ChannelEvent event, long sequence, boolean endOfBatch) throws Exception {
        event.executeEvent();
        event.clear();
        if (++this.counter > NOTIFY_PROGRESS_THRESHOLD) {
            this.sequenceCallback.set(sequence);
            this.counter = 0;
        }
    }
}
