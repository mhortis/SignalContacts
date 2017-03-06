import java.io.File;
import java.util.List;

class AppData {
    private static String username;
    private static List<File> numbersFiles;
    private static String verificationNumber;
    private static SignalService signalService;

    static SignalService getSignalService() {
        return signalService;
    }

    private static AppData myObj;

    static AppData getInstance(){
        if(myObj == null){
            myObj = new AppData();
        }
        return myObj;
    }

    static String getUsername() {
        return username;
    }

    static void setUsername(String username) {
        AppData.username = username;
    }

    static List<File> getNumbersFile() {
        return numbersFiles;
    }

    static void setNumbersFile(List<File> numbersFiles) {
        AppData.numbersFiles = numbersFiles;
    }

    static String getVerificationNumber() {
        return verificationNumber;
    }

    static void setVerificationNumber(String verificationNumber) {
        AppData.verificationNumber = verificationNumber;
    }
}
