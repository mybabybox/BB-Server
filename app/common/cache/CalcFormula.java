package common.cache;

import models.Post;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;

import play.Play;
import common.utils.NanoSecondStopWatch;

/**
 * http://stackoverflow.com/questions/11653545/hot-content-algorithm-score-with-time-decay
 * 
 * @author keithlei
 */
public class CalcFormula {
	private static play.api.Logger logger = play.api.Logger.apply(CalcFormula.class);
	
	public static final int FEED_SCORE_COMPUTE_BASE = Play.application().configuration().getInt("feed.score.compute.base");
	public static final int FEED_SCORE_COMPUTE_DECAY_START = Play.application().configuration().getInt("feed.score.compute.decay.start");
	public static final int FEED_SCORE_COMPUTE_DECAY_VELOCITY = Play.application().configuration().getInt("feed.score.compute.decay.velocity");
	
	public Long computeBaseScore(Post post) {
	    NanoSecondStopWatch sw = new NanoSecondStopWatch();
        logger.underlyingLogger().debug("calculateBaseScore for p="+post.id);
        
        post.baseScore = (long) (
                post.numComments
                + 2 * post.numViews
                + 3 * post.numLikes
                + 4 * post.numChats
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
        logger.underlyingLogger().debug("calculateTimeScore for p="+post.id+" with baseScore="+post.baseScore);
        
        Double timeScore = Math.log(Math.max(post.baseScore, 1));
        int hrs = Hours.hoursBetween(new DateTime(), new DateTime(post.getCreatedDate())).getHours();
        if (hrs > FEED_SCORE_COMPUTE_DECAY_START) {
            timeScore = timeScore * Math.exp(-FEED_SCORE_COMPUTE_DECAY_VELOCITY * hrs * hrs);
        }
        //timeScore = timeScore * FEED_SCORE_HIGH_BASE + post.id;
        
        sw.stop();
        logger.underlyingLogger().debug("computeTimeScore completed with timeScore="+timeScore+". Took "+sw.getElapsedSecs()+"s");
        
        return timeScore;
	}
}
