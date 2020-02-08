package com.nlutest;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;

class Shared {
    //contains transcript from s2t
    static String s;
    //piechart data to display
    static ObservableList oL;
    //test to stop recording
    static boolean stopRecording;
}

class NewMain {

    public static void main(String[] args) {
        NLUtest.main(args);
    }
}

public class NLUtest extends Application {

    private AnchorPane ovroot;
    OverviewController controller;
    //   Recorder recorder;
    static FXMLLoader load;
    private static Scene scene;
    ObservableList ch;
    private Stage stage;
    TranscribeService transcribeService;
    NLUService nluService;
    RecorderService recorderService;

    @Override
    public void start(Stage stage) throws IOException {

        this.stage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(NLUtest.class.getResource("overview.fxml"));
        ovroot = (AnchorPane) loader.load();
        // Show the scene containing the root layout.
        Scene scene = new Scene(ovroot);
        stage.setScene(scene);
        controller = loader.getController();
        // Give the controller access to the main app.
        controller.setMainApp(this);
        stage.show();
        
        controller.setLabel("");
        controller.drawLines(stage);
        controller.enableButton(true);

        transcribeService = new TranscribeService();
        nluService = new NLUService();
        recorderService = new RecorderService();

    }

    void startRecorderService() {

        controller.showPieChart(false);
        controller.showMessage("");

        recorderService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {

                //start transcribing Service
                controller.enableButton(false);
                controller.setLabel("Transcribing...");
                transcribeService.start();
                recorderService.reset();  //return to ready state

            }
        });

        transcribeService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                if (Shared.s == "") {
                    controller.setLabel("");
                    controller.setTextArea("Could Not Transcribe");
                    controller.setButtonText("Record");
                    controller.enableButton(true);
                } else {

                    controller.setTextArea(Shared.s);
                    controller.setLabel("Running NLU Service...");

                    nluService.start();
                }
                transcribeService.reset();  //return to ready state
            }
        });

        nluService.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {

                controller.setLabel("");
                controller.setButtonText("Record");
                controller.enableButton(true);
                if (Shared.oL != null) {
                    controller.changePieData(Shared.oL);
                } else {
                    controller.setTextArea("Could Not Create Pie Chart");

                }
                nluService.reset();  //return to ready state
            }
        });
////////////////////////////////////////////////////////////////////////////////

        controller.setLabel("Recording...");
        recorderService.start();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(NLUtest.class
                .getResource(fxml + ".fxml"));
        load = fxmlLoader;
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() {
        System.out.println("Stage is closing");
        System.exit(0);
    }
}

class RecorderService extends Service<Void> {

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                new Recorder().run();
                return null;
            }
        };
    }
}

class TranscribeService extends Service<Void> {

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                new Transcribe().runStoT();
                return null;
            }
        };
    }
}

class NLUService extends Service<Void> {

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                new NLU().runNLU();
                return null;
            }
        };
    }
}
