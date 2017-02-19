/*
 * Copyright 2017 Thomas Andres
 *
 * This file is part of Kandroid.
 *
 * Kandroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Kandroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package in.andres.kandroid.kanboard;

import android.support.annotation.NonNull;

import org.json.JSONObject;

import java.io.Serializable;

public class KanboardUserInfo implements Comparable<KanboardUserInfo>, Serializable {
    private int Id;
    private String Username;
    private String Role;
    private boolean IsLDAPUser;
    private String Name;
    private String Email;
    private String GoogleId;
    private String GithubId;
    private String GitlabId;
    private int NotificationsEnabled;
    private String Timezone;
    private String Language;
    private int DisableLoginForm;
    private boolean TwofactorActivated;
    private String TwofactorSecret;
    private String Token;
    private int NotificationsFilter;
    private int NumberFailedLogin;
    private int LockExpirationDate;
    private boolean IsActive;
    private String AvatarPath;

    KanboardUserInfo(int id, String username, String role, boolean isLDAPUser, String name,
                     String email, String googleId, String githubId, String gitlabId,
                     int notificationsEnabled, String timezone, String language,
                     int disableLoginForm, boolean twofactorActivated, String twofactorSecret,
                     String token, int notificationsFilter, int numberFailedLogin,
                     int lockExpirationDate, boolean isActive, String avatarPath) {
        Id = id;
        Username = username;
        Role = role;
        IsLDAPUser = isLDAPUser;
        Name = name;
        Email = email;
        GoogleId = googleId;
        GithubId = githubId;
        GitlabId = gitlabId;
        NotificationsEnabled = notificationsEnabled;
        Timezone = timezone;
        Language = language;
        DisableLoginForm = disableLoginForm;
        TwofactorActivated = twofactorActivated;
        TwofactorSecret = twofactorSecret;
        Token = token;
        NotificationsFilter = notificationsFilter;
        NumberFailedLogin = numberFailedLogin;
        LockExpirationDate = lockExpirationDate;
        IsActive = isActive;
        AvatarPath = avatarPath;
    }

    KanboardUserInfo(JSONObject json) {
        Id = json.optInt("id", -1);
        Username = json.optString("username", "");
        Role = json.optString("role", "");
        IsLDAPUser = json.optBoolean("is_ldap_user", false);
        Name = json.optString("name", "");
        Email = json.optString("email", "");
        GoogleId = json.optString("google_id", "");
        GithubId = json.optString("github_id", "");
        GitlabId = json.optString("gitlab_id", "");
        NotificationsEnabled = json.optInt("notifications_enabled", -1);
        Timezone = json.optString("timezone", "");
        Language = json.optString("language", "");
        DisableLoginForm = json.optInt("disable_login_form", -1);
        TwofactorActivated = json.optBoolean("twofactor_activated", false);
        TwofactorSecret = json.optString("twofactor_secret", "");
        Token = json.optString("token", "");
        NotificationsFilter = json.optInt("notifications_filter", -1);
        NumberFailedLogin = json.optInt("nb_failed_login", -1);
        LockExpirationDate = json.optInt("lock_expiration_date", -1);
        IsActive = json.optBoolean("is_active", false);
        AvatarPath = json.optString("avatar_path", "");

    }

    public int getId() {
        return Id;
    }

    public String getUsername() {
        return Username;
    }

    public String getRole() {
        return Role;
    }

    public boolean getIsLDAPUser() {
        return IsLDAPUser;
    }

    public String getName() {
        return Name;
    }

    public String getEmail() {
        return Email;
    }

    public String getGoogleId() {
        return GoogleId;
    }

    public String getGithubId() {
        return GithubId;
    }

    public String getGitlabId() {
        return GitlabId;
    }

    public int getNotificationsEnabled() {
        return NotificationsEnabled;
    }

    public String getTimezone() {
        return Timezone;
    }

    public String getLanguage() {
        return Language;
    }

    public int getDisableLoginForm() {
        return DisableLoginForm;
    }

    public boolean getTwofactorActivated() {
        return TwofactorActivated;
    }

    public String getTwofactorSecret() {
        return TwofactorSecret;
    }

    public String getToken() {
        return Token;
    }

    public int getNotificationsFilter() {
        return NotificationsFilter;
    }

    public int getNumberFailedLogin() {
        return NumberFailedLogin;
    }

    public int getLockExpirationDate() {
        return LockExpirationDate;
    }

    public boolean getIsActive() {
        return IsActive;
    }

    public String getAvatarPath() {
        return AvatarPath;
    }

    @Override
    public int compareTo(@NonNull KanboardUserInfo o) {
        return this.Username.compareTo(o.Username);
    }
}
