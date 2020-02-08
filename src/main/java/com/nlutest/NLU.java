package com.nlutest;

import java.lang.reflect.Field;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.security.Authenticator;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions;
import com.ibm.watson.natural_language_understanding.v1.model.EntitiesOptions;
import com.ibm.watson.natural_language_understanding.v1.model.Features;
import com.ibm.watson.natural_language_understanding.v1.model.KeywordsOptions;

public class NLU {

    public void runNLU() {

        String text = Shared.s;

        //we must return with Shared.oL set to null for caller if a problem
        Shared.oL = null;

        //watson just hangs if transcript is too short
        String[] tstLen;
        tstLen = text.split("\\s+");
        if (tstLen.length < 5) {
            return;
        }

        System.out.println("doing nlu service");

        //must init for try/catch
        NaturalLanguageUnderstanding service = null;

        try {
            Authenticator authenticator = new IamAuthenticator(Credentials.NLUAPIkey);
            service = new NaturalLanguageUnderstanding("2019-07-12", authenticator);
        } catch (Exception e) {
            return;
        }
        //must init for try/catch
        Response<AnalysisResults> response = null;

        try {
            //Entities and keywords are parameters you get back from the service.
            EntitiesOptions entitiesOptions = new EntitiesOptions.Builder()
                    .emotion(true)
                    .sentiment(true)
                    .limit(1)
                    .build();

            KeywordsOptions keywordsOptions = new KeywordsOptions.Builder()
                    .emotion(true)
                    .sentiment(true)
                    .limit(1)
                    .build();

            Features features = new Features.Builder()
                    .entities(entitiesOptions)
                    .keywords(keywordsOptions)
                    .build();

            AnalyzeOptions parameters = new AnalyzeOptions.Builder()
                    .text(text)
                    .features(features)
                    .build();

            //Take the parameters and send them to service for resutls.
            response = service.analyze(parameters).execute();

        } catch (Exception e) {
            return;
        }
///////////////////////////////////////////////////////////////////////////////////////////////////      
        NLUResult result = null;

        for (Field field : response.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();

            if (name != "result") {
                continue;
            }
            try {
                Object value = field.get(response);
                //          System.out.println(value);

                result = new ObjectMapper()
                        .setVisibility(PropertyAccessor.FIELD, Visibility.ANY)
                        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                        .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                        .readValue(value.toString(), NLUResult.class);

            } catch (Exception e) {
                return;
            }

        }

        Float disgust = null, anger = null, fear = null, joy = null, sadness = null;

        if (result.keywords != null && result.keywords.length != 0) {
            disgust = result.keywords[0].emotion.disgust;
            anger = result.keywords[0].emotion.anger;
            fear = result.keywords[0].emotion.fear;
            joy = result.keywords[0].emotion.joy;
            sadness = result.keywords[0].emotion.sadness;
        } else if (result.entities != null && result.entities.length != 0) {
            disgust = result.entities[0].emotion.disgust;
            anger = result.entities[0].emotion.anger;
            fear = result.entities[0].emotion.fear;
            joy = result.entities[0].emotion.joy;
            sadness = result.entities[0].emotion.sadness;

        } else {
            return;
        }

        //scale to xx.x...
        disgust *= 100;
        anger *= 100;
        fear *= 100;
        joy *= 100;
        sadness *= 100;

        //scale to 100 percent
        float scale = disgust + anger + fear + joy + sadness;
        scale = 100 / scale;
        disgust *= scale;
        anger *= scale;
        fear *= scale;
        joy *= scale;
        sadness *= scale;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Disgust", disgust),
                new PieChart.Data("Anger", anger),
                new PieChart.Data("Fear", fear),
                new PieChart.Data("Joy", joy),
                new PieChart.Data("Sadness", sadness));

        Shared.oL = pieChartData;

    }
}
