package common.schedule;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import models.User;

import common.collection.Pair;

import email.EDMUtility;

/**
 * Created by IntelliJ IDEA.
 * Date: 24/10/14
 * Time: 11:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandChecker {
    private static play.api.Logger logger = play.api.Logger.apply(CommandChecker.class);

    public static void checkCommandFiles() {
        File f = new File("command.txt");
        if (f.exists()) {
            try {
                DataInputStream in = new DataInputStream(new FileInputStream(f));
                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                String strLine;
                while ((strLine = br.readLine()) != null)   {
                    performCommand(strLine);
                }

                in.close();
                // rename to prevent infinite loop.
                f.renameTo(new File("command_done.txt"));
            } catch (Exception e) {
                logger.underlyingLogger().error("Error in performCommand", e);
            }
        }
    }

    /**
     * 1) indexTagWords
     * 2) gamificationEOD [daysBefore]
     * 3) communityStatistics [daysBefore]
     * 4) bootstrapPNCommunity
     */
    private static void performCommand(String commandLine) {
        if (commandLine.endsWith("DONE")) {
            return;
        }

        String[] tokens = commandLine.split("\\s");

        // TagWords
        if (commandLine.startsWith("indexTagWords")) {
            //TaggingEngine.indexTagWords();
        }
        // EDM Emails
        else if (commandLine.startsWith("edmAppTargets")) {
            if (tokens.length > 1) {
                String email = tokens[1];
                Pair<Integer,String> csv = User.getAndroidTargetEdmUsers();
                logger.underlyingLogger().info("getAndroidTargetEdmUsers. Count="+csv.first);

                EDMUtility.getInstance().sendMail("Target Android EMD users", csv.second, email);
                logger.underlyingLogger().info("getAndroidTargetEdmUsers. Sent to "+email);
            } else {
                logger.underlyingLogger().error("Error. edmAppTargets missing email parameter");
            }
        }

        // GamificationEOD  (Not used)
//        else if (commandLine.startsWith("gamificationEOD")) {
//            if (tokens.length > 1) {
//                Integer daysBefore = Integer.valueOf(tokens[1]);
//                GameAccountTransaction.performEndOfDayTasks(daysBefore);
//            } else {
//                logger.underlyingLogger().error("gamificationEOD missing daysBefore parameter");
//            }
//        }

        // Community Stats
        else if (commandLine.startsWith("communityStatistics")) {
            if (tokens.length > 1) {
                Integer daysBefore = Integer.valueOf(tokens[1]);
                //CommunityStatistics.populatePastStats(daysBefore);
            } else {
                logger.underlyingLogger().error("communityStatistics missing daysBefore parameter");
            }
        }

        // PN communities (DONE)
       /* else if (commandLine.startsWith("bootstrapPNCommunity")) {
            DataBootstrap.bootstrapPNCommunity();
        }
        // KG communities (DONE)
        else if (commandLine.startsWith("bootstrapKGCommunity")) {
            DataBootstrap.bootstrapKGCommunity();
        }

        // PlayGroup
        else if (commandLine.startsWith("bootstrapPG")) {
            if (tokens.length > 1) {
                String filePath = tokens[1];
                logger.underlyingLogger().info("Running bootstrapPG with: "+filePath);
                DataBootstrap.bootstrapPlayGroups(filePath);
            } else {
                logger.underlyingLogger().error("bootstrapPG missing file path");
            }
        }
        else if (commandLine.startsWith("bootstrapPGCommunity")) {
            DataBootstrap.bootstrapPGCommunity();
        }*/

        // PN reviews
/*        else if (commandLine.startsWith("bootstrapPNReviews")) {
            if (tokens.length > 1) {
                String filePath = tokens[1];
                logger.underlyingLogger().info("Running bootstrapPNReviews with: "+filePath);
                ThreadLocalOverride.disableNotification(true);
                DataBootstrap.bootstrapPNReviews(filePath);
                ThreadLocalOverride.disableNotification(false);
            } else {
                logger.underlyingLogger().error("bootstrapPNReviews missing file path");
            }
        }
        // Community Posts
        else if (commandLine.startsWith("bootstrapCommunityPosts")) {
            if (tokens.length > 1) {
                String filePath = tokens[1];
                logger.underlyingLogger().info("Running bootstrapCommunityPosts with: "+filePath);
                ThreadLocalOverride.disableNotification(true);
                DataBootstrap.bootstrapCommunityPosts(filePath);
                ThreadLocalOverride.disableNotification(false);
            } else {
                logger.underlyingLogger().error("bootstrapCommunityPosts missing file path");
            }
        }
        // Check and assign comms for all users
        else if (commandLine.startsWith("assignCommunitiesToUsers")) {
        	if (tokens.length > 1) {
        		try {
	        		Long fromUserId = Long.parseLong(tokens[1]);
	        		Long toUserId = Long.parseLong(tokens[2]);
	        		logger.underlyingLogger().info("Running assignCommunitiesToUsers with: "+fromUserId+" "+toUserId);
	        		//CommunityTargetingEngine.assignSystemCommunitiesToUsers(fromUserId, toUserId);
	        		logger.underlyingLogger().info("Completed assignCommunitiesToUsers with: "+fromUserId+" "+toUserId);
        		} catch (NumberFormatException e) {
        			logger.underlyingLogger().error(
        					"assignCommunitiesToUsers with wrong param format - "+tokens[1]+" "+tokens[2], e);
        		} catch (Exception e) {
        			logger.underlyingLogger().error(
        					"assignCommunitiesToUsers with exception - "+e.getLocalizedMessage(), e);
        		}
        	} else {
                logger.underlyingLogger().error("assignCommunitiesToUsers missing fromUserId, toUserId");
            }
        }
*/    }
}
