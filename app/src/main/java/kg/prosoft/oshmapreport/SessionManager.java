package kg.prosoft.oshmapreport;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by ProsoftPC on 2/2/2017.
 */

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "kg.prosoft.oshmapreport";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // make variable public to access from outside
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_USER_ID = "user_id";
    public static final String ACCESS_TOKEN = "access_token";

    //incident fill details if not logged in
    public static final String iname ="iname";
    public static final String iemail ="iemail";
    public static final String iphone ="iphone";
    public static final String itrue ="itrue";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createIncidentSession(String name, String email, String phone){
        editor.putBoolean(itrue, true);

        // Storing name in pref
        editor.putString(iname, name);
        editor.putString(iemail, email);
        editor.putString(iphone, phone);

        // commit changes
        editor.commit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String name, String email, int user_id, String access_token){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Storing name in pref
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putInt(KEY_USER_ID, user_id);
        editor.putString(ACCESS_TOKEN, access_token);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(ACCESS_TOKEN, pref.getString(ACCESS_TOKEN, null));
        user.put(KEY_USER_ID, Integer.toString(pref.getInt(KEY_USER_ID,0)));

        return user;
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL,"");
    }

    public String getName() {
        return pref.getString(KEY_NAME,"");
    }
    public String getAccessToken() {
        return pref.getString(ACCESS_TOKEN,"");
    }


    public String getIname() {
        return pref.getString(iname,"");
    }
    public String getIemail() {
        return pref.getString(iemail,"");
    }
    public String getIphone() {
        return pref.getString(iphone,"");
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID,0);
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Main Activity
        Intent i = new Intent(_context, MainActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isItrue(){
        return pref.getBoolean(itrue, false);
    }
}
