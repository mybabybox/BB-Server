package common.cache;

import java.math.BigDecimal;

import models.Post;

import org.joda.time.DateTime;
import org.joda.time.Days;

import play.Play;
import common.thread.ThreadLocalOverride;
import common.utils.NanoSecondStopWatch;

/**
 * http://stackoverflow.com/questions/11653545/hot-content-algorithm-score-with-time-decay
 * 
 * @author keithlei
 */
public class CalcFormula {
	private static play.api.Logger logger = play.api.Logger.apply(CalcFormula.class);
	
	public static final Long FEED_SCORE_HIGH_BASE = Play.application().configuration().getLong("feed.score.high.base");
	public static final int FEED_SCORE_COMPUTE_BASE = Play.application().configuration().getInt("feed.score.compute.base");
	public static final int FEED_SCORE_COMPUTE_DECAY_START = Play.application().configuration().getInt("feed.score.compute.decay.start");
	public static final int FEED_SCORE_COMPUTE_DECAY_VELOCITY = Play.application().configuration().getInt("feed.score.compute.decay.velocity");
	
	public Long computeBaseScore(Post post) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("computeBaseScore for p="+post.id);
        if (!ThreadLocalOverride.isServerStartingUp()) {
            logger.underlyingLogger().debug("     numComments="+post.numComments);
            logger.underlyingLogger().debug("     numViews="+post.numViews);
            logger.underlyingLogger().debug("     numLikes="+post.numLikes);
            logger.underlyingLogger().debug("     numChats="+post.numChats);
            logger.underlyingLogger().debug("     numBuys="+post.numBuys);
            logger.underlyingLogger().debug("     baseScoreAdjust="+post.baseScoreAdjust);
        }
        
        post.baseScore = (long) (
                post.numComments 
                + 2 * post.numViews 
                + 2 * post.numLikes 
                + 5 * post.numChats 
                + 5 * post.numBuys 
                + FEED_SCORE_COMPUTE_BASE);
        
        if (post.baseScoreAdjust != null) {
            post.baseScore += post.baseScoreAdjust;     // can be negative
        }
        post.save();
        
        sw.stop();
        logger.underlyingLogger().debug("computeBaseScore completed with baseScore="+post.baseScore+". Took "+sw.getElapsedSecs()+"s");
        
        return post.baseScore;
	}
	
	public Double computeTimeScore(Post post) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("computeTimeScore for p="+post.id+" date=" + post.getCreatedDate() + " baseScore="+post.baseScore);
        
        Double timeScore = (double) Math.max(post.baseScore, 1);
        Double timeDiff = Math.abs(Days.daysBetween(new DateTime(post.getCreatedDate()), new DateTime()).getDays()) / 7D;
        timeDiff = (double) Math.ceil(timeDiff);
        if (timeDiff > FEED_SCORE_COMPUTE_DECAY_START) {
            timeDiff -= FEED_SCORE_COMPUTE_DECAY_START;
            timeScore = timeScore * getDiscountFactor(timeDiff);
        }
        
        BigDecimal bd = new BigDecimal(timeScore);
        bd = bd.setScale(5, BigDecimal.ROUND_HALF_UP);
        post.timeScore = bd.doubleValue();
        post.save();
        
        sw.stop();
        logger.underlyingLogger().debug("computeTimeScore completed with timeScore="+timeScore+". Took "+sw.getElapsedSecs()+"s");
        
        return timeScore;
	}
	
	private Double getDiscountFactor(Double timeDiff) {
	    return Math.exp(-FEED_SCORE_COMPUTE_DECAY_VELOCITY * timeDiff);
	}

}
