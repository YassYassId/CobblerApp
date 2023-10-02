import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bson.Document;

public class EventListener extends Component implements ActionListener {
    private JFrame frame;
    private JTextField priceTag;
    private JTextField priceTag1;
    JTextField fname;JTextField mail;JTextField phonenumber;JTextField intakeDate;JTextField returnDate;JComboBox items;JCheckBox[] checkBox;

    public EventListener(JTextField priceTag) {
        this.priceTag = priceTag;
    }

    public EventListener(JFrame frame) {
        this.frame = frame;
    }

    public EventListener(JTextField fname, JTextField mail, JTextField phonenumber, JComboBox items, JTextField intakeDate, JTextField returnDate,JTextField priceTag,JCheckBox[] checkBox) {
        this.checkBox=checkBox;
        this.fname=fname;
        this.mail=mail;
        this.phonenumber=phonenumber;
        this.items=items;
        this.intakeDate=intakeDate;
        this.returnDate=returnDate;
        this.priceTag1=priceTag;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            String serviceName = checkBox.getText();

            // Load JSON data and calculate the total price based on selected checkboxes
            double totalPrice = 0.0;

            if (priceTag != null && priceTag.getText() != null && !priceTag.getText().isEmpty()) {
                String priceTagText = priceTag.getText();
                priceTagText = priceTagText.replace(",", ".");
                totalPrice = Double.parseDouble(priceTagText);
            }


            try {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader("D:\\Portfolio\\CobblerApp\\src\\CobblerServices.json"));

                JSONArray services = (JSONArray) ((JSONObject) obj).get("cobblerServices");

                for (Object serviceObj : services) {
                    JSONObject service = (JSONObject) serviceObj;
                    String serviceText = (String) service.get("service");

                    // Check if the service matches the selected checkbox
                    if (serviceText.equals(serviceName)) {
                        String priceStr = (String) service.get("priceRange");
                        // Replace commas with periods in the price string
                        priceStr = priceStr.replace(",", ".");
                        double servicePrice = Double.parseDouble(priceStr);

                        // Check if the checkbox is selected or unselected
                        if (checkBox.isSelected()) {
                            totalPrice += servicePrice; // Add the price if selected
                        } else {
                            totalPrice -= servicePrice; // Subtract the price if unselected
                        }
                    }
                }

                // Update the priceTag textfield with the calculated total price
                priceTag.setText(String.format("%.2f", totalPrice));
            } catch (IOException | ParseException ex) {
                ex.printStackTrace();
            }
        }else{
            String s = e.getActionCommand();
            //Buttons
            if (s.equals("Cancel")){
                frame.setVisible(false);
            }else{
                int cmpt=0;
                String Services1="";
                for (JCheckBox checkBox : checkBox) {
                    if(checkBox.isSelected()){
                        cmpt+=1;
                        Services1 += checkBox.getText() + ", ";
                    }
                }
                Object selectedElement = items.getSelectedItem();
                if(cmpt<=0 || fname.getText().isEmpty() || mail.getText().isEmpty() || phonenumber.getText().isEmpty() || intakeDate.getText().isEmpty() || returnDate.getText().isEmpty()){
                    JOptionPane.showMessageDialog(this.frame, "Please fill in all required fields.");
                }else{
                    int id= generateUniqueID();
                    String filePath = generatePDF(fname.getText(),mail.getText(),phonenumber.getText(),intakeDate.getText(),returnDate.getText(),priceTag1.getText(),Services1,selectedElement.toString(),id);
                    if (filePath != null) {
                        insertPDFIntoMongoDB(filePath, mail.getText(), id);
                    } else {
                        JOptionPane.showMessageDialog(this.frame, "PDF generation and local saving failed.");
                    }
                }

            }
        }
    }

    private int generateUniqueID() {
        // Generate a unique ID for the ticket (you can customize this logic)
        Random random = new Random();
        return random.nextInt(10000); // Generate a random ID for now
    }
    private String generatePDF(String fullname,String Mail,String phnumber,String indate, String redate,String price,String servicee,String item, int ID) {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        String filePath = null;

        try {
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            // Load the font (Helvetica-Bold)
            PDType0Font font = PDType0Font.load(document, new File("D:\\Portfolio\\CobblerApp\\Courier ITALIC.ttf"));
            // Set the font and font size
            contentStream.setFont(font, 12);
            // Define the lines of text to display
            List<String> linesOfText = new ArrayList<>();
            linesOfText.add("Client Name: " + fullname);
            linesOfText.add("Email: " + Mail);
            linesOfText.add("Phone Number: " + phnumber);
            linesOfText.add("Intake Date: " + indate);
            linesOfText.add("Return Date: " + redate);
            linesOfText.add("Item: " + item);
            linesOfText.add("Services Offered: " + servicee);
            linesOfText.add("Total price: " + price);
            linesOfText.add("ID: " + ID);
            int yOffset = 700; // Initial Y-coordinate
            int lineSpacing = 20; // Adjust this value for the desired vertical spacing

            for (String line : linesOfText) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yOffset);
                contentStream.showText(line);
                contentStream.endText();
                yOffset -= lineSpacing; // Move down for the next line
            }

            contentStream.close();

            File file = new File(fullname+"_"+ID + ".pdf");
            document.save(file);
            document.close();
            filePath = file.getAbsolutePath();

            JOptionPane.showMessageDialog(this.frame, "PDF generated and saved as '"+fullname+"_"+ID +".pdf'");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }
    private void insertPDFIntoMongoDB(String filePath, String Mail, int ID) {
        try {
            byte[] pdfBytes = Files.readAllBytes(Paths.get(filePath));

            // Store the PDF binary data in MongoDB
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("mydb");
            MongoCollection<org.bson.Document> collection = database.getCollection("CobblerTicket");

            Document pdfDocument = new Document()
                    .append("Email", Mail)
                    .append("ID", ID)
                    .append("pdfData", pdfBytes); // Store the PDF as binary data

            collection.insertOne(pdfDocument);

            JOptionPane.showMessageDialog(this.frame, "PDF generated and stored in MongoDB and saved locally as '" + filePath + "'.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
