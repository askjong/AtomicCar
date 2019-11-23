package src.Tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Andreas
 * @version X.X
 * @since 16.11.2019, 11:35
 */
public class Terminal {

    public static void doScript(String script) {
        ProcessBuilder processBuilder = new ProcessBuilder();

        // -- Linux --
        processBuilder.command(script);

        try {
            processBuilder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean doCommand(String command) {
        boolean commandExecuted = false;
        ProcessBuilder processBuilder = new ProcessBuilder();

        // Run a shell command
        processBuilder.command("bash", "-c", command);
        try {

            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }


            int exitVal = process.waitFor();
            if (exitVal == 0) {
                commandExecuted = true;
                System.out.println("Success!");
                System.out.println(output);
               // System.exit(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return commandExecuted;
    }

}



