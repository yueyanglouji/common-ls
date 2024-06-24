package l.s.common.quartz;

import org.quartz.Calendar;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.util.Date;

public class SynSimpleTrigger extends SimpleTriggerImpl {
    @Override
    public void updateAfterMisfire(Calendar cal) {
        Date newFireTime = new Date();
        if(getStartTime() != null && getRepeatInterval() > 0){
            long newTime = newFireTime.getTime();
            long yu = (newTime - getStartTime().getTime()) % getRepeatInterval();
            newFireTime = new Date(newTime - yu + getRepeatInterval());

        }
        int repeatCount = getRepeatCount();
        if (repeatCount != 0 && repeatCount != REPEAT_INDEFINITELY) {
            setRepeatCount(getRepeatCount() - getTimesTriggered());
            setTimesTriggered(0);
        }

        if (getEndTime() != null && getEndTime().before(newFireTime)) {
            setNextFireTime(null); // We are past the end time
        } else {
            setStartTime(newFireTime);
            setNextFireTime(newFireTime);
        }
    }
}
