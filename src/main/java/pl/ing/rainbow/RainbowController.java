package pl.ing.rainbow;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ExceptionDepthComparator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Created by B0621351 on 2016-08-31.
 */
@Component
public class RainbowController {

    @Autowired
    public RainbowPriceDAO rainbowPriceDAO;

    private static final Logger LOGGER = LogManager.getLogger(RainbowController.class);
    public static final int heartBitPeriod = 30 * 60 * 1000;

    @Scheduled(initialDelay = 0, fixedRate = heartBitPeriod)
    public void reportCurrentTime() {

        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws MalformedURLException {

        String link = "http://r.pl/wyspy-kanaryjskie-fuerteventura/labranda-bahia-de-lobos/20170803?liczbaDoroslych=2&liczbaDzieci=0&liczbaPokoi=1&wiekDzieci=&wybraneMiastoWyjazdu=Katowice&wybraneWyzywienie=All+inclusive";
        String pricesTag = "";
        Map<String, String> prices = new LinkedHashMap<>();

        URL url = new URL(link);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String inputLine;
            boolean skipLine = true;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("initDataKalkulatorCeny")) {
                    pricesTag = inputLine;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] elemnts = pricesTag.split(",");
        int elementID = 0;
        String tripDate = "";
        String tripPrice = "";

        for(String s : elemnts) {
            if(elementID == 0) {
                if (s.contains("DataPrzedzialHtml")) {
                    tripDate = cleanElement(s);
                    elementID=1;
                }
            } else {
                if (s.contains("CenaHtml")) {
                    tripPrice = cleanElement(s);
                    elementID=0;
                    addToList(prices, tripDate, tripPrice);
                }
            }
        }

        StringBuffer mail = new StringBuffer();
        LOGGER.log(Level.DEBUG, MarkerManager.getMarker("APP"), "Czytam...");

        LOGGER.log(Level.DEBUG, MarkerManager.getMarker("APP"), "Odczytane ceny.... ");
        for(String key: prices.keySet()) {
            String printElement = key + " " + prices.get(key);
            LOGGER.log(Level.DEBUG, MarkerManager.getMarker("APP"), printElement);
//            mail.append(printElement+"\n");
            rainbowPriceDAO.save(new RainbowPrice(key, Integer.valueOf(prices.get(key).replaceAll("[^0-9.]", ""))));
        }
        LOGGER.log(Level.DEBUG, MarkerManager.getMarker("APP"), "Koniec czytania...");
        LOGGER.log(Level.DEBUG, MarkerManager.getMarker("APP"), "-----------------------------------------------------------");

        System.out.println("---Best prices!!---");
        rainbowPriceDAO.findBest().forEach( record -> {
            mail.append(record[0] + ":" + record[1] + "\n");
        });
        LOGGER.log(Level.DEBUG, MarkerManager.getMarker("APP"), mail.toString());

//        sendEmail(mail.toString());

    }

    private void sendEmail(String text) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("phunt123@gmail.com","Kal2fior");
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("phunt123@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("phunt123@gmail.com,rychlikpatrycja@gmail.com"));
            message.setSubject("Rainbow");
            message.setText(text);

            Transport.send(message);

            LOGGER.log(Level.DEBUG, MarkerManager.getMarker("APP"), "Mail wysłany");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void addToList(Map<String, String> prices, String tripDate, String tripPrice) {
        if(prices.get(tripDate) == null) {
            prices.put(tripDate, tripPrice);
        }
    }

    private String cleanElement(String element) {
        element = element.replace("</span>", "");
        element = element.replace("<span class=&#39;ilosc-dni&#39;>", "");
        element = element.replace("<span class=\\\"cena-sufix\\\">", "");
        element = element.replace("/&nbsp", "");
        element = element.replace("&nbsp", "");
        element = element.replace("os.", "");
        element = element.replace(";", "");
        return element;
    }
}
