package org.secured.jaspic.current;


import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import static javax.security.auth.message.AuthStatus.*;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class Provider implements ServerAuthModule {

    private CallbackHandler handler;
    private final Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

    @Override
    public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler, @SuppressWarnings("rawtypes") Map options) throws AuthException {
        this.handler = handler;
    }
    
    @Override
    public Class<?>[] getSupportedMessageTypes() {
        return supportedMessageTypes;
    }

    /**
     * This method will be called before the first Filter or Servlet in the request is invoked
     */
    @Override
    public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject) throws AuthException {
        try {
            // Communicate the details of the authenticated user to the container. In many
            // cases the handler will just store the details and the container will actually handle
            // the login after we return from this method.
            handler.handle( new Callback[] {
                    // The name of the authenticated user
	            new CallerPrincipalCallback(clientSubject, "snoopy"),
	            // the groups/roles of the authenticated user
	            new GroupPrincipalCallback(clientSubject, new String[]{"RedBaron", "JoeCool", "MansBestFriend"})}
            );
        } catch (IOException | UnsupportedCallbackException e) {
            throw (AuthException) new AuthException().initCause(e);
        }

        return SUCCESS;
    }

  
    /**
     * This method will be called after the last Filter or Servlet in the request has been invoked
     */
    @Override
    public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
        return SEND_SUCCESS;
    }

    /**
     * This method will be called when HttpServletRequest#logout is explicitly called
     */
    @Override
    public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {

    }
}
