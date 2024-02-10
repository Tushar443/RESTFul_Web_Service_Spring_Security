package com.example.demo.Security;

import com.example.demo.ws.SpringApplicationContext;
import org.springframework.core.env.Environment;


public class SecurityConstants
{
    public static final long EXPIRATION_TIME = 864000000;//10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String SIGN_UP_URL = "/users";
    public static final String EMAIL_VERIFICATION_URL = "/users/email-verification";
    
    public static final String TOKEN_SECRET = "cfiwefjecnsddhuseduo3842384723twyfmwecmwr4r8w9rw30r0w83rwjrwjrwe";


    public static String getTokenSecret(){
        Environment environment = (Environment) SpringApplicationContext.getBean("environment");
        return environment.getProperty("tokenSecret");
    }

    public static final String HEADER_STRING = "Authorization";
    public static final String USER_ID = "UserId";
}
