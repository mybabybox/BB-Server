import java.util.Arrays;
import java.util.Date;

import models.Activity;
import models.SecurityRole;
import models.SecurityRole.RoleType;
import models.User;
import play.Application;
import play.GlobalSettings;
import play.Play;
import play.db.jpa.JPA;
import play.mvc.Call;
import play.mvc.Http.RequestHeader;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Results;
import babybox.events.handler.EventHandler;

import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.PlayAuthenticate.Resolver;
import com.feth.play.module.pa.exceptions.AccessDeniedException;
import com.feth.play.module.pa.exceptions.AuthException;

import common.cache.CalcServer;
import common.schedule.CommandChecker;
import common.schedule.JobScheduler;
import common.thread.ThreadLocalOverride;
import controllers.routes;

/**
 *
 */
public class Global extends GlobalSettings {
    private static final play.api.Logger logger = play.api.Logger.apply("application");

    // Configurations
    private static final String STARTUP_BOOTSTRAP_PROP = "startup.data.bootstrap";
    private static final String RUN_BACKGROUND_TASKS_PROP = "run.backgroundtasks";

    /**
     * @param app
     */
    public void onStart(Application app) {
		
    	EventHandler.getInstance();
		
        final boolean runBackgroundTasks = Play.application().configuration().getBoolean(RUN_BACKGROUND_TASKS_PROP, false);
        if (runBackgroundTasks) {
            // schedule background jobs
            scheduleJobs();
        }
        
        PlayAuthenticate.setResolver(new Resolver() {
            @Override
            public Call login(final Session session) {
                // Your login page
                return routes.Application.login();
            }

            @Override
            public Call afterAuth(final Session session) {
                // The user will be redirected to this page after authentication
                // if no original URL was saved
            	
            	// reset last login time
            	final User user = controllers.Application.getLocalUser(session);
    		    user.setLastLogin(new Date());
    		    
                //return routes.Application.mainHome();
                return routes.Application.mainHome();
            }

            @Override
            public Call afterLogout(final Session session) {
                return routes.Application.mainHome();
            }

            @Override
            public Call auth(final String provider) {
                // You can provide your own authentication implementation,
                // however the default should be sufficient for most cases
                return com.feth.play.module.pa.controllers.routes.Authenticate
                        .authenticate(provider);
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

			@Override
			public Call askMerge() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Call askLink() {
				// TODO Auto-generated method stub
				return null;
			}
        });


        final boolean doDataBootstrap = Play.application().configuration().getBoolean(STARTUP_BOOTSTRAP_PROP, false);

        if (doDataBootstrap) {
            logger.underlyingLogger().info("[Global.init()] Enabled");

            JPA.withTransaction(new play.libs.F.Callback0() {
                @Override
                public void invoke() throws Throwable {
                    init();
                }
            });
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
                                CalcServer.cleanupSoldPosts();
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
        CalcServer.warmUpActivity();
        
        ThreadLocalOverride.setIsServerStartingUp(false);
	}

	@Override
	public Result onBadRequest(RequestHeader request, String error) {
	    return Results.badRequest(error);
	}
	
	@Override
    public Result onError(RequestHeader request, Throwable throwable) {
        return Results.internalServerError(throwable.getMessage());
    }
	
	@Override
    public Result onHandlerNotFound(RequestHeader request) {
        return Results.notFound(views.html.notFound404.render(request.path()));
    }
}