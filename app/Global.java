import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import models.Activity;
import models.SecurityRole;
import models.SecurityRole.RoleType;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.db.jpa.JPA;
import play.libs.Akka;
import play.mvc.Call;
import play.mvc.Http.RequestHeader;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Results;
import redis.clients.jedis.JedisPool;
import babybox.events.handler.EventHandler;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import common.cache.CalcServer;
import common.cache.JedisCache;
import common.schedule.CommandChecker;
import common.schedule.JobScheduler;
import common.thread.ThreadLocalOverride;
import controllers.routes;
import scala.concurrent.duration.FiniteDuration;

public class Global extends GlobalSettings {
    private static final play.api.Logger logger = play.api.Logger.apply("application");

    // Configurations
    private static final String STARTUP_BOOTSTRAP_PROP = "startup.data.bootstrap";
    private static final String RUN_BACKGROUND_TASKS_PROP = "run.backgroundtasks";
    
    /**
     * @param app
     */
    public void onStart(Application app) {
		//jedisPool.getResource();
    	//EventHandler.getInstance();
		
        final boolean runBackgroundTasks = Play.application().configuration().getBoolean(RUN_BACKGROUND_TASKS_PROP, false);
        if (runBackgroundTasks) {
            // schedule background jobs
            scheduleJobs();
        }
        
    	PlayAuthenticate.setResolver(new Resolver() {

			@Override
			public Call login() {
				// Your login page
				return routes.Application.login();
			}

			@Override
			public Call afterAuth() {
				// The user will be redirected to this page after authentication
				// if no original URL was saved
            	// reset last login time
    		    
                //return routes.Application.mainHome();
                return routes.Application.mainHome();
			}

			@Override
			public Call afterLogout() {
				return routes.Application.index();
			}

			@Override
			public Call auth(final String provider) {
				// You can provide your own authentication implementation,
				// however the default should be sufficient for most cases
				return com.feth.play.module.pa.controllers.routes.Authenticate
						.authenticate(provider);
			}

			@Override
			public Call askMerge() {
				return routes.Account.askMerge();
			}

			@Override
			public Call askLink() {
				return routes.Account.askLink();
			}

			@Override
			public Call onException(final AuthException e) {
				if (e instanceof AccessDeniedException) {
					return routes.Signup
							.oAuthDenied(((AccessDeniedException) e)
									.getProviderKey());
				}

				// more custom problem handling here...
				return super.onException(e);
			}
		});


        final boolean doDataBootstrap = Play.application().configuration().getBoolean(STARTUP_BOOTSTRAP_PROP, false);

        if (doDataBootstrap) {
            logger.underlyingLogger().info("[Global.init()] Enabled");

            JPA.withTransaction(new play.libs.F.Callback0() {
                @Override
                public void invoke() throws Throwable {
                    //init();
                }
            });

            // bootstrap community feed Redis lists
            //FeedProcessor.bootstrapCommunityLevelFeed();
        } else {
            logger.underlyingLogger().info("[Global.init()] Disabled");
        }
	}

    /**
     * scheduleJobs
     */
    private void scheduleJobs() {
        // Note: (OFF as of 20150621) schedule Gamification EOD accounting daily at 3:00am HKT
    	/*
		JobScheduler.getInstance().schedule("gamificationEOD", "0 00 3 ? * *",
            new Runnable() {
                public void run() {
                    try {
                       JPA.withTransaction(new play.libs.F.Callback0() {
                            public void invoke() {
                                GameAccountTransaction.performEndOfDayTasks(1);
                            }
                        });
                    } catch (Exception e) {
                        logger.underlyingLogger().error("Error in gamificationEOD", e);
                    }
                }
            }
        );
        */
    	
        // schedule to purge sold posts daily at 5:00am HKT
        JobScheduler.getInstance().schedule("cleanupSoldPosts", "0 00 5 ? * *",
            new Runnable() {
                public void run() {
                    try {
                       JPA.withTransaction(new play.libs.F.Callback0() {
                            public void invoke() {
                            	CalcServer.getInstanceForDI().cleanupSoldPosts();
                            }
                        });
                    } catch (Exception e) {
                        logger.underlyingLogger().error("Error in cleanupSoldPosts", e);
                    }
                }
            }
        );
        
        // schedule to purge Activity daily at 4:00am HKT
        JobScheduler.getInstance().schedule("purgeActivity", "0 00 4 ? * *",
            new Runnable() {
                public void run() {
                    try {
                       JPA.withTransaction(new play.libs.F.Callback0() {
                            public void invoke() {
                            	Activity.purgeActivity();
                            }
                        });
                    } catch (Exception e) {
                        logger.underlyingLogger().error("Error in purgeActivity", e);
                    }
                }
            }
        );
		

        // schedule to check command every 2 min.
        JobScheduler.getInstance().schedule("commandCheck", 120000,
            new Runnable() {
                public void run() {
                    try {
                       JPA.withTransaction(new play.libs.F.Callback0() {
                            public void invoke() {
                                CommandChecker.checkCommandFiles();
                            }
                        });
                    } catch (Exception e) {
                        logger.underlyingLogger().error("Error in CommandChecker", e);
                    }
                }
            }
        );
    
    	Akka.system().scheduler().scheduleOnce(
    			new FiniteDuration(10, TimeUnit.SECONDS),
    			new Runnable(){
                    @Override
                    public void run() {
                    	try {
                    		Unirest.get("http://localhost:9000/warmUpActivity").asString();
                    	} catch (UnirestException e){
                    		e.printStackTrace();
                    	}
                    }
                },
    			Akka.system().dispatcher());
    
    }

	private void init() {
        if (SecurityRole.findRowCount() == 0L) {
            for (final RoleType roleType : Arrays.asList(SecurityRole.RoleType.values())) {
                final SecurityRole role = new SecurityRole();
                role.roleName = roleType.name();
                role.save();
            }
        }

        ThreadLocalOverride.setIsServerStartingUp(true);
        
        // data first time bootstrap
        DataBootstrap.bootstrap();
        
        // cache warm up
        //CalcServer.warmUpActivity();
        
        ThreadLocalOverride.setIsServerStartingUp(false);
	}

}