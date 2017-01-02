package in.andres.kandroid.kanboard;

import org.json.JSONObject;

public class KanboardUserInfo implements Comparable<KanboardUserInfo> {
    public final int ID;
    public final String Username;
    public final String Role;
    public final boolean IsLDAPUser;
    public final String Name;
    public final String Email;
    public final String GoogleID;
    public final String GithubID;
    public final String GitlabID;
    public final int NotificationsEnabled;
    public final String Timezone;
    public final String Language;
    public final int DisableLoginForm;
    public final boolean TwofactorActivated;
    public final String TwofactorSecret;
    public final String Token;
    public final int NotificationsFilter;
    public final int NumberFailedLohǵin;
    public final int LockExpirationDate;
    public final boolean IsActive;
    public final String AvatarPath;

    KanboardUserInfo(int id, String username, String role, boolean isLDAPUser, String name,
                     String email, String googleID, String githubID, String gitlabID,
                     int notificationsEnabled, String timezone, String language,
                     int disableLoginForm, boolean twofactorActivated, String twofactorSecret,
                     String token, int notificationsFilter, int numberFailedLohǵin,
                     int lockExpirationDate, boolean isActive, String avatarPath) {
        ID = id;
        Username = username;
        Role = role;
        IsLDAPUser = isLDAPUser;
        Name = name;
        Email = email;
        GoogleID = googleID;
        GithubID = githubID;
        GitlabID = gitlabID;
        NotificationsEnabled = notificationsEnabled;
        Timezone = timezone;
        Language = language;
        DisableLoginForm = disableLoginForm;
        TwofactorActivated = twofactorActivated;
        TwofactorSecret = twofactorSecret;
        Token = token;
        NotificationsFilter = notificationsFilter;
        NumberFailedLohǵin = numberFailedLohǵin;
        LockExpirationDate = lockExpirationDate;
        IsActive = isActive;
        AvatarPath = avatarPath;
    }

    KanboardUserInfo(JSONObject json) {
        ID = json.optInt("id", -1);
        Username = json.optString("username", "");
        Role = json.optString("role", "");
        IsLDAPUser = json.optBoolean("is_ldap_user", false);
        Name = json.optString("name", "");
        Email = json.optString("email", "");
        GoogleID = json.optString("google_id", "");
        GithubID = json.optString("github_id", "");
        GitlabID = json.optString("gitlab_id", "");
        NotificationsEnabled = json.optInt("notifications_enabled", -1);
        Timezone = json.optString("timezone", "");
        Language = json.optString("language", "");
        DisableLoginForm = json.optInt("disable_login_form", -1);
        TwofactorActivated = json.optBoolean("twofactor_activated", false);
        TwofactorSecret = json.optString("twofactor_secret", "");
        Token = json.optString("token", "");
        NotificationsFilter = json.optInt("notifications_filter", -1);
        NumberFailedLohǵin = json.optInt("nb_failed_login", -1);
        LockExpirationDate = json.optInt("lock_expiration_date", -1);
        IsActive = json.optBoolean("is_active", false);
        AvatarPath = json.optString("avatar_path", "");

    }

    @Override
    public int compareTo(KanboardUserInfo o) {
        return this.Username.compareTo(o.Username);
    }
}
