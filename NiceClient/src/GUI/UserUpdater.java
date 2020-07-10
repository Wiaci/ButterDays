package GUI;

import java.net.SocketTimeoutException;

public class UserUpdater implements Runnable {

    UserMagicInteract magic;

    public UserUpdater(UserMagicInteract magic) {
        this.magic = magic;
    }


    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(5000);
                magic.getActualData();
            } catch (InterruptedException e) {
                break;
            } catch (SocketTimeoutException e) {
                continue;
            }
        }
    }
}
