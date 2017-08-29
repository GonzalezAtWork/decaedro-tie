package net.decaedro.autentica;

public class AccountGeneral {
    public static final String ACCOUNT_TYPE = "net.decaedro.auth_example";
    public static final String ACCOUNT_NAME = "Decaedro";
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an Decaedro account";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an Decaedro account";
    public static final ServerAuthenticate sServerAuthenticate = new ParseAuthenticate();
}
