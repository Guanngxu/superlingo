package com.guanngxu.superlingo.service;

import com.translated.lara.Credentials;
import com.translated.lara.errors.LaraException;
import com.translated.lara.translator.TextResult;
import com.translated.lara.translator.Translator;

import java.util.ArrayList;
import java.util.List;

public class TranslateUtil {

    // Access Key ID
    private static final String LARA_ACCESS_KEY_ID = "J8D8K06NG66ANM8A0GVCOJFEKO";
    // Access Key SECRET
    private static final String LARA_ACCESS_KEY_SECRET = "5IgF4B1h2LX2Fq-fRMPklg-1_Aas7km3PdP8LtRkZ6w";

    public static String translate(String text) {
        Credentials credentials = new Credentials(LARA_ACCESS_KEY_ID, LARA_ACCESS_KEY_SECRET);
        Translator lara = new Translator(credentials);
        String translateResult = text;
        try {
            TextResult res = lara.translate(text, "en-US", "zh-CN");
            translateResult = res.getTranslation();
        } catch (LaraException e) {

        }
        return translateResult;
    }

    public static String[] translateStringArray(String[] strings) {
        Credentials credentials = new Credentials(LARA_ACCESS_KEY_ID, LARA_ACCESS_KEY_SECRET);
        Translator lara = new Translator(credentials);
        String[] translateResult = new String[0];
        try {
            TextResult res = lara.translate(strings, "en-US", "zh-CN");
            translateResult = res.getTranslations().toArray(new String[0]);
        } catch (LaraException e) {

        }
        return translateResult;
    }


    public static void main(String[] args) throws LaraException {
        Credentials credentials = new Credentials(LARA_ACCESS_KEY_ID, LARA_ACCESS_KEY_SECRET);
        Translator lara = new Translator(credentials);

        // This translates your text from English ("en-US") to Italian ("it-IT").
        List<String> strings = new ArrayList<String>();
        strings.add("en:beverages-and-beverages-preparations");
        strings.add("pt:bebidas cafeína");
        strings.add("en:colas");
        strings.add("en:soft-drinks");
        strings.add("en:non-alcoholic-beverages");
        // 传入0会自动分配一个大小刚合适的String[]
        String[] transStrings = strings.toArray(new String[0]);
        TextResult res = lara.translate(transStrings, "en-US", "zh-CN");
//        TextResult res = lara.translate("strings", "en-US", "zh-CN");
        // Prints the translated text: "Ciao, come stai? Questo testo può essere molto lungo."
        System.out.println(res.getTranslations());
    }

}
