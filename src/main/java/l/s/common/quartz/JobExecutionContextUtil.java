package l.s.common.quartz;

import l.s.common.context.ThreadLocalContext;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.Date;
import java.util.List;

public class JobExecutionContextUtil {

    static void setContext(JobExecutionContext context){
        ThreadLocalContext.getContext().setAttribute("_quartz_JobExecutionContext", context);
    }

    public static JobExecutionContext getContext(){
        return (JobExecutionContext) ThreadLocalContext.getContext().getAttribute("_quartz_JobExecutionContext");
    }

    static void removeContext(){
        ThreadLocalContext.getContext().removeAttribute("_quartz_JobExecutionContext");
    }

    public static Date getNextFireTime() throws SchedulerException {
        JobExecutionContext context = getContext();
        Scheduler scheduler = context.getScheduler();
        JobDetail job = context.getJobDetail();
        List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(job.getKey());
        Date nextFireTime = null;
        for(Trigger trigger : triggersOfJob){
            if(nextFireTime == null){
                nextFireTime = trigger.getNextFireTime();
            }else {
                Date next = trigger.getNextFireTime();
                if(next.compareTo(nextFireTime) < 0){
                    nextFireTime = next;
                }
            }
        }
        return nextFireTime;
    }

}
