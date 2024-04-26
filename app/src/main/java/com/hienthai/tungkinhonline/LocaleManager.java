package com.hienthai.tungkinhonline;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class LocaleManager {

    private static final Locale defaultLocale = Locale.getDefault();

    private Locale systemLocale = defaultLocale;
    private static LocaleManager localeManager;

    AppPrefs prefs;

    public static LocaleManager getInstance() {
        return localeManager;
    }

    private LocaleManager(Application application, AppPrefs prefs) {
        this.prefs = prefs;
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                applyForActivity(activity);
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });

        application.registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {
                processConfigurationChange(application, newConfig);
            }

            @Override
            public void onLowMemory() {

            }
        });

        Locale locale;
        String lang = prefs.getLanguage();
        if (TextUtils.isEmpty(lang)) {
            locale = systemLocale;
        } else {
            locale = getLocaleFromLang(lang);
        }
        persistAndApply(application, locale);
    }

    public static void initialize(Application application, AppPrefs prefs) {
        localeManager = new LocaleManager(application, prefs);
    }

    public void setLocale(Context context, String lang) {
        if (TextUtils.isEmpty(lang)) {
            persistAndApply(context, systemLocale);
        } else {
            persistAndApply(context, getLocaleFromLang(lang));
        }
    }

    private void persistAndApply(Context context, Locale locale) {
        applyLocale(context, locale);
    }

    private void applyForActivity(Activity activity) {
        String lang = prefs.getLanguage();
        Locale locale;
        if (TextUtils.isEmpty(lang)) {
            locale = systemLocale;
        } else {
            locale = getLocaleFromLang(lang);
        }
        applyLocale(activity, locale);
    }

    private void processConfigurationChange(Context context, Configuration config) {
        systemLocale = getLocaleFromConfig(config);
        String lang = prefs.getLanguage();
        if (TextUtils.isEmpty(lang)) {
            persistAndApply(context, systemLocale);
        } else {
            applyLocale(context, getLocaleFromLang(lang));
        }
    }

    private void applyLocale(Context context, Locale locale) {
        updateResource(context, locale);
        Context appContext = context.getApplicationContext();
        if (appContext != context) {
            updateResource(appContext, locale);
        }
    }

    public Locale getLocaleFromConfig(Configuration config) {
        Locale current;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            current = config.getLocales().get(0);
        } else {
            current = config.locale;
        }
        return current;
    }

    private void updateResource(Context context, Locale locale) {
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Locale current = getLocaleFromConfig(res.getConfiguration());

        if (current == locale) return;
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setLocale24(config, locale);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setLocale24(Configuration config, Locale locale) {
        Set<Locale> set = new LinkedHashSet<>();
        set.add(locale);
        LocaleList defaultLocales = LocaleList.getDefault();

        for (int i = 0; i < defaultLocales.size(); i++) {
            set.add(defaultLocales.get(i));
        }
        Locale[] locales = new Locale[set.size()];
        set.toArray(locales);
        config.setLocales(new LocaleList(locales));
    }

    public static Locale getLocaleFromLang(String lang) {
        String[] sp = lang.split("[_-]");

        switch (sp.length) {
            case 2:
                return new Locale(sp[0], sp[1]);
            case 3:
                return new Locale(sp[0], sp[1], sp[2]);
            default:
                return new Locale(lang);
        }
    }
}
