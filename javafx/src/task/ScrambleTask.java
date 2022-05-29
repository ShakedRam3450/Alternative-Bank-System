package task;

import javafx.concurrent.Task;

import java.io.IOException;

public class ScrambleTask extends Task<Boolean> {
    @Override
    protected Boolean call() throws Exception {
        try {
            updateMessage("Hello world");
        } catch (Exception e){
            System.out.println("");
        }

        return Boolean.TRUE;
    }
}
