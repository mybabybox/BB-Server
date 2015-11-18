package module;
import com.google.inject.AbstractModule;
import common.cache.CalcServer;

public class CalcServerModule extends AbstractModule {
    protected void configure() {
    	bind(CalcServer.class);
    }
}