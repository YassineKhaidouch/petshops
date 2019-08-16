package petshops.developerpet.com.petshops.DataBase;

/**
 * Created by root on 3/4/18.
 */

import android.content.Context;
import android.content.SharedPreferences;

public class SharedReferenceHelper {
    private static SharedReferenceHelper instance = null;
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;


    private static String SHERE_MSG = "check";

    public static final String MyKey = "verify";

    String UID = "uid";
    String Email = "email";
    String Name = "name";
    String Password = "password";
    String PhoneNumber = "phone";
    String keepMeLogin = "keepme";
    String StartScreen = "screen";
    String mylonguage = "language";
    String Position = "position";

    public SharedReferenceHelper() {}

    public static SharedReferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedReferenceHelper();
            preferences = context.getSharedPreferences(SHERE_MSG, Context.MODE_PRIVATE);
            editor = preferences.edit();
        }
        return instance;
    }

    public void setPhoneVerify(boolean checking){
        editor.putBoolean(MyKey, checking);
        //editor.apply();
        editor.commit();
    }


    public void setDataUser(String UID_,String Email_ ){
        editor.putString(UID, UID_);
        editor.putString(Email, Email_);
        editor.commit();
    }
    public  void  setPassword(String Password_){
        editor.putString(Password, Password_);
        editor.commit();
    }
    public void setPhoneNumber(String PhoneNumber_){
        editor.putString(PhoneNumber, PhoneNumber_);
        editor.commit();
    }
    public void setName(String Name_){
        editor.putString(Name, Name_);
        editor.commit();
    }

    public void setLanguage(String language){
        editor.putString(mylonguage, language);
        editor.commit();
    }

    public void setKeepMeLogin(boolean keepMeLogin_ ){
        editor.putBoolean(keepMeLogin, keepMeLogin_);
        editor.commit();
    }

   public void setGetstartScreen(boolean StartScreen_ ){
        editor.putBoolean(StartScreen, StartScreen_);
        editor.commit();
    }

    public void setPosition(int Position_ ){
        editor.putInt(Position ,Position_);
        editor.apply();
    }

    public boolean GetPhoneVerify() {
        return  preferences.getBoolean(MyKey, false);
    }

    public boolean getGetstartScreen() {
        return  preferences.getBoolean(StartScreen, false);
    }

    public  boolean getKeepMeLogin(){return preferences.getBoolean(keepMeLogin,false);}

    public String getLanguage() {return  preferences.getString(mylonguage, "en");}
    public int getPosition() {return  preferences.getInt(Position, 0);}
    public  String getUID(){
        return preferences.getString(UID,"");
    }
    public  String getEmail(){
        return preferences.getString(Email,"");
    }
    public  String getName(){return preferences.getString(Name,"");}
    public  String getPassword(){
        return preferences.getString(Password,"");
    }
    public  String getPhoneNumber(){
        return preferences.getString(PhoneNumber,"");
    }

}