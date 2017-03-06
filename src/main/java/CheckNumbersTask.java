import javafx.concurrent.Task;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.push.ContactTokenDetails;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CheckNumbersTask extends Task<Void> {
    private SignalServiceAccountManager manager;
    private List<File> numbersFiles;
    private int usersFound = 0;
    private int totalFiles = 0;
    private int checkedFiles = 0;

    CheckNumbersTask(){
        manager = AppData.getSignalService().getInstance().getAccountManager();
        numbersFiles = AppData.getNumbersFile();
        totalFiles = numbersFiles.size();
    }

    @Override
    protected Void call() throws Exception {
        File outputFile1 = new File("Signal users.txt");
        FileWriter outputFileWriter1 = new FileWriter(outputFile1.getAbsolutePath(), true);
        BufferedWriter bw1 = new BufferedWriter(outputFileWriter1);

        File outputFile2 = new File("Stats.txt");
        FileWriter outputFileWriter2 = new FileWriter(outputFile2.getAbsolutePath(), true);
        BufferedWriter bw2 = new BufferedWriter(outputFileWriter2);

        for(File numbersFile : numbersFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(numbersFile))) {
                Set<String> numbers = new HashSet<>();
                String number;
                while ((number = br.readLine()) != null)
                    numbers.add(number);

                List<ContactTokenDetails> contacts = manager.getContacts(numbers);

                for (ContactTokenDetails contact : contacts) {
                    bw1.write(contact.getNumber());
                    bw1.write(System.lineSeparator());
                }

                checkedFiles++;
                usersFound += contacts.size();

                bw2.write("File: " + numbersFile.getName() + System.lineSeparator());
                bw2.write("Users found: " + contacts.size() + System.lineSeparator());
                bw2.write(System.lineSeparator());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        bw1.close();
        bw2.close();
        return null;
    }
}
