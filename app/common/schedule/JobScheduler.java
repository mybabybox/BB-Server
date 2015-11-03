package common.schedule;

import akka.actor.ActorSystem;
import play.libs.Akka;
import play.libs.Time;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * Date: 19/10/14
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class JobScheduler {
    private static play.api.Logger logger = play.api.Logger.apply(JobScheduler.class);

    // Singleton
    private static JobScheduler instance_ = new JobScheduler();

    public static JobScheduler getInstance() {
        return instance_;
    }

    /**
     * 
     * @param schedulerId
     * @param intervalMs
     * @param jobTask
     */
    public void schedule(String schedulerId, long intervalMs, Runnable jobTask) {
        schedule(schedulerId, 0L, intervalMs, TimeUnit.MILLISECONDS, jobTask);
    }

    /**
     * 
     * @param schedulerId
     * @param initial
     * @param interval
     * @param timeUnit
     * @param jobTask
     */
    public void schedule(String schedulerId, Long initial, long interval, TimeUnit timeUnit, Runnable jobTask) {
        ActorSystem actorSystem = Akka.system();
        try {
            FiniteDuration initialDelay = Duration.create(initial, timeUnit);
            FiniteDuration intervalDuration = Duration.create(interval, timeUnit);

            actorSystem.scheduler().schedule(
                    initialDelay, intervalDuration,
                    jobTask, actorSystem.dispatcher());
        } catch (Exception e) {
            logger.underlyingLogger().error("Error in schedule", e);
        }
    }

    /**
     * @param schedulerId
     * @param cronExpression
     * @param jobTask
     */
    public void schedule(String schedulerId, String cronExpression, Runnable jobTask) {
        ActorSystem actorSystem = Akka.system();
        try {
            Time.CronExpression e = new Time.CronExpression(cronExpression);
            Date nextValidTimeAfter = e.getNextValidTimeAfter(new Date());
            FiniteDuration d = Duration.create(
                    nextValidTimeAfter.getTime() - System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS);

            logger.underlyingLogger().info("Scheduling to run[" + schedulerId + "] at: " + nextValidTimeAfter);

            actorSystem.scheduler().scheduleOnce(
                    d,
                    new JobSchedulerRunnable(schedulerId, cronExpression, jobTask),
                    actorSystem.dispatcher());

        } catch (Exception e) {
            logger.underlyingLogger().error("Error in schedule", e);
        }
    }

    private class JobSchedulerRunnable implements Runnable {
        private String schedulerId;
        private String cronExpression;
        private Runnable jobTask;

        public JobSchedulerRunnable(String schedulerId, String cronExpression, Runnable jobTask) {
            this.schedulerId = schedulerId;
            this.cronExpression = cronExpression;
            this.jobTask = jobTask;
        }

        @Override
        public void run() {
            logger.underlyingLogger().info("JobScheduler - Running "+schedulerId+" ["+cronExpression+"]");

            try {
                jobTask.run();
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in "+schedulerId, e);
            }

            // Add a 5 sec delay before the next schedule.
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }

            //Schedule for next time
            schedule(schedulerId, cronExpression, jobTask);
        }
    }

}
