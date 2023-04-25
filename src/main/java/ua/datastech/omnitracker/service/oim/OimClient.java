package ua.datastech.omnitracker.service.oim;

import lombok.extern.slf4j.Slf4j;
import oracle.iam.platform.OIMClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import java.util.Hashtable;

@Component
@Slf4j
public class OimClient {

    @Value("${java.security.auth.login.config***REMOVED***")
    private String authConfig;

    @Value("${java.naming.provider.url***REMOVED***")
    private String javaNamingProvider;

    @Value("${oim.username***REMOVED***")
    private String oimUsername;

    @Value("${oim.password***REMOVED***")
    private String oimPassword;

    public OIMClient getOIMClient() {
        oracle.iam.platform.OIMClient client;
        System.setProperty("weblogic.Name", "oim_server1");
        System.setProperty("OIM.AppServerType", "wls");
        System.setProperty("APPSERVER_TYPE", "wls");
        System.setProperty("java.security.auth.login.config", authConfig);
        Hashtable<String, String> env = new Hashtable<>();
        env.put(OIMClient.JAVA_NAMING_FACTORY_INITIAL, "weblogic.jndi.WLInitialContextFactory");
        env.put(OIMClient.JAVA_NAMING_PROVIDER_URL, javaNamingProvider);
        client = new OIMClient(env);
        try {
            client.login(oimUsername, oimPassword.toCharArray());
            return client;
        ***REMOVED*** catch (LoginException e) {
            log.error(e.getMessage());
        ***REMOVED***
        return null;
    ***REMOVED***

***REMOVED***
