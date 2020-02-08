package com.nlutest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ibm.cloud.sdk.core.http.HttpMediaType;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.cloud.sdk.core.service.exception.NotFoundException;
import com.ibm.cloud.sdk.core.service.exception.RequestTooLargeException;
import com.ibm.cloud.sdk.core.service.exception.ServiceResponseException;
import com.ibm.watson.speech_to_text.v1.SpeechToText;
import com.ibm.watson.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.speech_to_text.v1.model.SpeechRecognitionResults;
import java.io.File;
import java.lang.reflect.Field;

public class Transcribe {

    SpeechRecognitionResults transcript;

    public void runStoT() {

        System.out.println("doing transcribe service");

        try {

            IamAuthenticator authenticator = new IamAuthenticator(Credentials.StoTAPIkey);
            SpeechToText service = new SpeechToText(authenticator);

            File audio = new File("nlufile.wav");

            RecognizeOptions options = new RecognizeOptions.Builder()
                    .audio(audio)
                    .contentType(HttpMediaType.AUDIO_WAV)
                    .build();

            transcript = service.recognize(options).execute().getResult();
//            System.out.println(transcript);

        } catch (NotFoundException e) {
            // Handle Not Found (404) exception
        } catch (RequestTooLargeException e) {
            // Handle Request Too Large (413) exception
        } catch (ServiceResponseException e) {
            // Base class for all exceptions caused by error responses from the service
            System.out.println("Service returned status code "
                    + e.getStatusCode() + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("unknown error");
        }

        //parse the returned JSON in "transcript" for our desired field
        StoTResult[] result = null;

        for (Field field : transcript.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            String name = field.getName();

            if (name != "results") {
                continue;
            }
            try {
                Object value = field.get(transcript);
                System.out.println(value);

                result = new ObjectMapper()
                        .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                        .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                        .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                        .readValue(value.toString(), StoTResult[].class);

                //make sure result isn't null
                if (result == null) {
                    Shared.s = "";
                    return;
                }

//                Shared.s = "";
//                for (int i = 0; i < result.length; i++) {
//                    if (result[i].alternatives[0].transcript.length() > Shared.s.length()) {
//                        Shared.s = result[i].alternatives[0].transcript;
//                    }
//                }
                //for now just choose the longest of alternative transcripts returned
                Shared.s = "";
                for (StoTResult result1 : result) {
                    if (result1.alternatives[0].transcript.length() > Shared.s.length()) {
                        Shared.s = result1.alternatives[0].transcript;
                    }
                }

            } catch (Exception e) {
                Shared.s = "";
                return;
            }
        }
    }
}
