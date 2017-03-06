import org.apache.commons.lang.RandomStringUtils;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.push.TrustStore;
import org.whispersystems.signalservice.internal.push.SignalServiceUrl;
import java.io.IOException;

class SignalService {
    private TrustStore trustStore = new SignalServiceTrustStore();
    private SignalServiceUrl[] urls = {new SignalServiceUrl("https://textsecure-service.whispersystems.org", trustStore)};
    private String password = generateRandomPassword();
    private SignalServiceAccountManager accountManager;

    private static SignalService myObj;

    private SignalService(){
        accountManager = new SignalServiceAccountManager(urls, AppData.getUsername(),password,"[FILL_IN]");
    }

    static SignalService getInstance(){
        if(myObj == null){
            myObj = new SignalService();
        }
        return myObj;
    }

    SignalServiceAccountManager getAccountManager(){
        return accountManager;
    }

    boolean isAccountVerified(){
        try {
            return accountManager.getAccountVerificationToken() != null;
        } catch (IOException e) {
            return false;
        }
    }

    void sendVerificationSMS(){
        try {
            accountManager.requestSmsVerificationCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void verifyAccount(){
        try {
            accountManager.verifyAccountWithCode(AppData.getVerificationNumber(), password,1,true,true,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateRandomPassword(){
        return RandomStringUtils.randomAlphanumeric(10);
    }
}
