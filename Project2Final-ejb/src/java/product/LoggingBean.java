/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package product;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@MessageDriven(mappedName = "jms/shoping", activationConfig = {
    @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class LoggingBean implements MessageListener {

    public LoggingBean() {
    }

    @Override
    public void onMessage(Message message) {
        // NOTE We are aware this method is inefficient but in the world
        //of a fake shopping cart and a project that is due it shows that
        // we can use message driven beans which is what this project is about really :)

        // Cast object to a type that plays nice
        TextMessage tm = (TextMessage) message;
        // Specify file with no absolute path so that we make sure path exists on diferent computers
        File outputFile = new File("shopingcart.log");
        FileWriter outputFileWriter;
        try {
            outputFileWriter = new FileWriter(outputFile, true);
            BufferedWriter outputBuffer = new BufferedWriter(outputFileWriter);
            try {
                // Write to logfile
                outputBuffer.write(tm.getText() + "\n");
                outputBuffer.close();
                // Print absolute path of log file for Reiner's benifit
                System.out.println("Output File found at " + outputFile.getAbsolutePath());
            } catch (JMSException ex) {
                Logger.getLogger(LoggingBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(LoggingBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
