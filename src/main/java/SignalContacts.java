import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.push.ContactTokenDetails;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SignalContacts extends Application {
    final Alert statsAlert = new Alert(Alert.AlertType.INFORMATION);
    final Alert alertSucceeded = new Alert(Alert.AlertType.INFORMATION);
    AnimationTimer timer;

    @Override
    public void start(final Stage stage) throws InterruptedException {
        stage.setTitle("Signal Contacts Checker");

        /*
        First screen
         */
        final FileChooser fileChooser = new FileChooser();

        final Label usernameLabel = new Label("Your Signal mobile number:");
        final TextField usernameText = new TextField();

        final Button openButton = new Button("Select the file with the phone numbers");
        final Button nextButton = new Button("Next");
        nextButton.setDisable(true);

        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    List<File> list =
                            fileChooser.showOpenMultipleDialog(stage);
                    if (list != null) {
                        AppData.getInstance().setNumbersFile(list);
                        nextButton.setDisable(false);
                    }
                    }
            });

        final GridPane inputGridPane = new GridPane();
        final BorderPane selectFilePane = new BorderPane();
        final BorderPane goToVerifyPane = new BorderPane();

        final Pane firstPane = new VBox(12);
        final Pane verifyPane = new VBox(12);

        GridPane.setConstraints(usernameLabel, 0, 0);
        GridPane.setConstraints(usernameText, 1, 0);
        inputGridPane.setHgap(6);
        inputGridPane.setVgap(6);
        inputGridPane.getChildren().addAll(usernameLabel, usernameText);

        selectFilePane.setCenter(openButton);
        goToVerifyPane.setRight(nextButton);

        firstPane.getChildren().addAll(inputGridPane, selectFilePane, goToVerifyPane);
        firstPane.setPadding(new Insets(12, 12, 12, 12));

        stage.setScene(new Scene(firstPane));
        stage.show();

        /*
        Second screen (in case verification is needed)
         */
        final GridPane verificationGridPane = new GridPane();
        final BorderPane goToCheckContactsPane = new BorderPane();

        final Label shouldVerifyLabel = new Label("Your phone number needs verification");
        final Label verificationLabel = new Label("Your verification code (without \"-\"):");
        final TextField verificationText = new TextField();

        final Button nextButton2 = new Button("Next");

        GridPane.setConstraints(shouldVerifyLabel,0,0);
        GridPane.setConstraints(verificationLabel, 0, 1);
        GridPane.setConstraints(verificationText, 1, 1);
        verificationGridPane.setHgap(6);
        verificationGridPane.setVgap(6);
        verificationGridPane.getChildren().addAll(shouldVerifyLabel, verificationLabel, verificationText);

        goToCheckContactsPane.setRight(nextButton2);

        verifyPane.getChildren().addAll(verificationGridPane, goToCheckContactsPane);
        verifyPane.setPadding(new Insets(12, 12, 12, 12));

        /*
        Checking numbers popup screen
         */
        alertSucceeded.setTitle("Phone numbers have been checked");
        alertSucceeded.setHeaderText("Phone numbers have been checked");
        alertSucceeded.setContentText("The phone numbers have been checked and the users have been identified!");

        final Alert alertWrongVerification = new Alert(Alert.AlertType.ERROR);
        alertWrongVerification.setTitle("Number not verified");
        alertWrongVerification.setHeaderText("Number not verified");
        alertWrongVerification.setContentText("Your number could not be verified. Please try again.");

        statsAlert.setTitle("Checking phone numbers from files");
        statsAlert.setHeaderText("Checking phone numbers from files");
        statsAlert.getDialogPane().setPrefSize(400,300);
        statsAlert.setContentText("The phone numbers are being checked to identify Signal users");

        /*
        Button listeners
         */
        nextButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String username = usernameText.getText();
                    if(username != null && !username.isEmpty() && AppData.getInstance().getNumbersFile() != null){
                        AppData.getInstance().setUsername(username);
                        if(!AppData.getInstance().getSignalService().getInstance().isAccountVerified()) {
                            AppData.getInstance().getSignalService().getInstance().sendVerificationSMS();
                            stage.setScene(new Scene(verifyPane));
                            stage.show();
                        } else {
                            final long startTime = System.currentTimeMillis();
                            timer = new AnimationTimer() {
                                @Override
                                public void handle(long now) {
                                    long elapsedMillis = System.currentTimeMillis() - startTime ;
                                    statsAlert.setHeaderText("Checking phone numbers from files - Seconds elapsed: " + elapsedMillis/1000);
                                }
                            };
                            timer.start();

                            CheckNumbersTask checkNumbersTask = new CheckNumbersTask();
                            checkNumbersTask.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
                                @Override
                                public void handle(WorkerStateEvent e){
                                    alertSucceeded.showAndWait();
                                    nextButton.setText("Next");
                                    nextButton.setDisable(false);
                                    timer.stop();
                                }
                            });
                            checkNumbersTask.setOnRunning(new EventHandler<WorkerStateEvent>(){
                                @Override
                                public void handle(WorkerStateEvent e){
                                    statsAlert.showAndWait();
                                }
                            });

                            new Thread(checkNumbersTask).start();
                            nextButton.setText("Checking phone numbers...");
                            nextButton.setDisable(true);
                        }
                    }
                }
            }
        );

        nextButton2.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    String verificationCode = verificationText.getText();
                    if(!AppData.getInstance().getSignalService().getInstance().isAccountVerified()) {
                        if (verificationCode != null && !verificationCode.isEmpty()) {
                            AppData.getInstance().setVerificationNumber(verificationCode);
                            AppData.getInstance().getSignalService().getInstance().verifyAccount();
                        }
                        else {
                            alertWrongVerification.showAndWait();
                            return;
                        }
                    }
                    if(!AppData.getInstance().getSignalService().getInstance().isAccountVerified()) {
                        alertWrongVerification.showAndWait();
                        return;
                    }
                    else {
                        final long startTime = System.currentTimeMillis();
                        timer = new AnimationTimer() {
                            @Override
                            public void handle(long now) {
                                long elapsedMillis = System.currentTimeMillis() - startTime ;
                                statsAlert.setHeaderText("Checking phone numbers from files - Seconds elapsed: " + elapsedMillis/1000);
                            }
                        };
                        timer.start();

                        CheckNumbersTask checkNumbersTask = new CheckNumbersTask();
                        checkNumbersTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent e) {
                                alertSucceeded.showAndWait();
                                nextButton2.setText("Next");
                                nextButton2.setDisable(false);
                                timer.stop();
                            }
                        });
                        checkNumbersTask.setOnRunning(new EventHandler<WorkerStateEvent>(){
                            @Override
                            public void handle(WorkerStateEvent e){
                                statsAlert.showAndWait();
                            }
                        });

                        new Thread(checkNumbersTask).start();
                        nextButton2.setText("Checking phone numbers...");
                        nextButton2.setDisable(true);
                    }
                }
            }
        );
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public class CheckNumbersTask extends Task<Void> {
        private SignalServiceAccountManager manager;
        private List<File> numbersFiles;
        private int usersFound = 0;
        private int totalFiles = 0;
        private int checkedFiles = 0;
        private String currentFile;
        private String stats = "";

        CheckNumbersTask(){
            manager = AppData.getSignalService().getInstance().getAccountManager();
            numbersFiles = AppData.getNumbersFile();
            totalFiles = numbersFiles.size();
        }

        @Override
        protected Void call() throws Exception {

            for(File numbersFile : numbersFiles) {
                File outputFile1 = new File("Signal users.txt");
                FileWriter outputFileWriter1 = new FileWriter(outputFile1.getAbsolutePath(), true);
                BufferedWriter bw1 = new BufferedWriter(outputFileWriter1);

                File outputFile2 = new File("Stats.txt");
                FileWriter outputFileWriter2 = new FileWriter(outputFile2.getAbsolutePath(), true);
                BufferedWriter bw2 = new BufferedWriter(outputFileWriter2);

                File outputFile3 = new File("errorfiles.txt");
                FileWriter outputFileWriter3 = new FileWriter(outputFile3.getAbsolutePath(), true);
                BufferedWriter bw3 = new BufferedWriter(outputFileWriter3);

                currentFile = numbersFile.getName();
                updateStatus();

                try (BufferedReader br = new BufferedReader(new FileReader(numbersFile))) {
                    Set<String> numbers = new HashSet<>();
                    String number;
                    while ((number = br.readLine()) != null)
                        numbers.add(number);

                    List<ContactTokenDetails> contacts = manager.getContacts(numbers);

                    if(contacts.size() == 0)
                        contacts = manager.getContacts(numbers);

                    for (ContactTokenDetails contact : contacts) {
                        bw1.write(contact.getNumber());
                        bw1.write(System.lineSeparator());
                    }

                    checkedFiles++;
                    usersFound += contacts.size();
                    updateStatus();

                    bw2.write("File: " + numbersFile.getName() + System.lineSeparator());
                    bw2.write("Users found: " + contacts.size() + System.lineSeparator());
                    bw2.write(System.lineSeparator());
                    bw1.close();
                    bw2.close();
                    bw3.close();

                    if(checkedFiles < numbersFiles.size())
                        Thread.sleep(25000);
                } catch (IOException e) {
                    try (BufferedReader br = new BufferedReader(new FileReader(numbersFile))) {
                        Set<String> numbers = new HashSet<>();
                        String number;
                        while ((number = br.readLine()) != null)
                            numbers.add(number);
                        List<ContactTokenDetails> contacts = manager.getContacts(numbers);

                        if (contacts.size() == 0)
                            contacts = manager.getContacts(numbers);

                        for (ContactTokenDetails contact : contacts) {
                            bw1.write(contact.getNumber());
                            bw1.write(System.lineSeparator());
                        }

                        checkedFiles++;
                        usersFound += contacts.size();
                        updateStatus();

                        bw2.write("File: " + numbersFile.getName() + System.lineSeparator());
                        bw2.write("Users found: " + contacts.size() + System.lineSeparator());
                        bw2.write(System.lineSeparator());
                        bw1.close();
                        bw2.close();
                        bw3.close();

                        if(checkedFiles < numbersFiles.size())
                            Thread.sleep(25000);

                    } catch (IOException ex) {
                        ex.printStackTrace();
                        bw3.write("File: " + numbersFile.getName() + System.lineSeparator());
                        bw3.write("Exception thrown");
                        bw3.write(System.lineSeparator());
                        bw1.close();
                        bw2.close();
                        bw3.close();
                    }

                }
            }
            return null;
        }

        private void updateStatus(){
            stats = "";
            stats += "Files checked: " + checkedFiles + "/" + totalFiles + "\n";
            stats += "Current file being checked: " + currentFile + "\n";
            stats += "Signal users found so far: " + usersFound;
            Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    statsAlert.getDialogPane().setContent(new Label(stats));
                }
            });
        }
    }
}
