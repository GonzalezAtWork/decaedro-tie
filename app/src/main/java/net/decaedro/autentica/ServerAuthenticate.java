package net.decaedro.autentica;

public interface ServerAuthenticate {
    public String userSignUp(final String name, final String email, final String pass, String authType) throws Exception;
    public User userSignIn(final String device, final String user, final String pass, String authType) throws Exception;
}
