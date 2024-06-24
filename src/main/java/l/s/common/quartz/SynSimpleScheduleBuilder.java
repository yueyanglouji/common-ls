package l.s.common.quartz;

import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.quartz.spi.MutableTrigger;

public class SynSimpleScheduleBuilder extends ScheduleBuilder<SimpleTrigger> {

    private long interval = 0;

    private int repeatCount = 0;

    protected SynSimpleScheduleBuilder() {
    }

    public static SynSimpleScheduleBuilder simpleSchedule() {
        return new SynSimpleScheduleBuilder();
    }

    @Override
    public MutableTrigger build() {

        SynSimpleTrigger st = new SynSimpleTrigger();
        st.setRepeatInterval(interval);
        st.setRepeatCount(repeatCount);

        return st;
    }

    public SynSimpleScheduleBuilder withIntervalInMilliseconds(long intervalInMillis) {
        this.interval = intervalInMillis;
        return this;
    }

    public SynSimpleScheduleBuilder withRepeatCount(int triggerRepeatCount) {
        this.repeatCount = triggerRepeatCount;
        return this;
    }

    public SynSimpleScheduleBuilder repeatForever() {
        this.repeatCount = SimpleTrigger.REPEAT_INDEFINITELY;
        return this;
    }
}
