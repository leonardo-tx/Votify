package br.com.votify.core.domain.entities.users;

public final class PermissionFlags {
    public static final int NONE = 0;
    public static final int DETAILED_USER = 1;
    public static final int MODERATOR = DETAILED_USER;
    public static final int ALL = DETAILED_USER;
}
