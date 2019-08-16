package petshops.developerpet.com.petshops;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

/**
 * Created by guillaumeagis on 08/04/15.
 */
public class LanguageHelper {
    public static void changeLocale(Resources res, String locale) {
        Configuration config;
        config = new Configuration(res.getConfiguration());
        switch (locale) {
            case "en":
                config.locale = Locale.ENGLISH;
                break;
            case "hi":
                config.locale = new Locale("hi");
                break;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
        // reload files from assets directory
    }
}