import org.whispersystems.signalservice.api.push.TrustStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SignalServiceTrustStore implements TrustStore {

    @Override
    public InputStream getKeyStoreInputStream() {
        File initialFile = new File("whisper.store");
        try {
            return new FileInputStream(initialFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getKeyStorePassword() {
        return "whisper";
    }
}
