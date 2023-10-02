import javax.swing.*;
import java.awt.*;

public class Container extends JFrame {
    //-------------------------------------------Panels---------------------------------------------------//
    JPanel P=new JPanel() ;
    JPanel Panelwest=new JPanel() ;JPanel PanelwestNorth=new JPanel() ;JPanel PanelwestSouth=new JPanel() ;
    JPanel Paneleast=new JPanel() ; JPanel PaneleastNorth=new JPanel() ;JPanel PaneleastSouth=new JPanel() ;
    JPanel Panelnorth=new JPanel() ;
    JPanel Panelsouth=new JPanel() ;

    //----------------------------------------Labels + textfields + list + date-----------------------------//
    JLabel client = new JLabel("Client Informations:");
    JLabel fullname = new JLabel("Fullname");JTextField fname = new JTextField(15);
    JLabel email = new JLabel("Email");JTextField mail = new JTextField(15);
    JLabel phone = new JLabel("Phone");JTextField phonenumber = new JTextField(15);
    JLabel item = new JLabel("Items :");
    JLabel price = new JLabel("Total price :");JTextField priceTag = new JTextField(7);
    JLabel intake = new JLabel("Intake date :(DD-MM-YYYY)");JTextField intakeDate=new JTextField(15);
    JLabel returnD = new JLabel("Return date :(DD-MM-YYYY)");JTextField returnDate=new JTextField(15);
    String[] Elements = {"Men's shoes" , "Women's shoes" , "Child's shoes" , "Bag"};
    JComboBox items = new JComboBox<>(Elements);


    //---------------------------Buttons-------------------------------------------------------------//
    JButton ok = new JButton("Confirm");
    JButton annuler =  new JButton("Cancel");
    private JCheckBox[] checkBox = {new JCheckBox("Shrinking"),new JCheckBox("Patching"),new JCheckBox("Fixing zippers"),new JCheckBox("Stitching"),new JCheckBox("Polishing"),new JCheckBox("Repairing leather"),new JCheckBox("Dyeing")};

    public Container(){
        super("Cobbler App");
        //Panelwestnorth
        PanelwestNorth.setLayout(new GridLayout(1,1));
        PanelwestNorth.add(client);
        PanelwestSouth.setLayout(new GridLayout(6,1));
        PanelwestSouth.add(fullname);PanelwestSouth.add(fname);
        PanelwestSouth.add(email);PanelwestSouth.add(mail);
        PanelwestSouth.add(phone);PanelwestSouth.add(phonenumber);
        PanelwestSouth.add(item);PanelwestSouth.add(items);
        PanelwestSouth.add(intake);PanelwestSouth.add(intakeDate);
        PanelwestSouth.add(returnD);PanelwestSouth.add(returnDate);
        Panelwest.setLayout(new BorderLayout());
        Panelwest.add(PanelwestNorth,BorderLayout.NORTH);
        Panelwest.add(PanelwestSouth,BorderLayout.CENTER);

        //Paneleastnorth
        PaneleastNorth.setLayout(new GridLayout(7,0));
        for (JCheckBox box : checkBox) {
            PaneleastNorth.add(box);
        }
        // Add event listeners to the checkboxes
        for (JCheckBox checkBox : checkBox) {
            checkBox.addActionListener(new EventListener(priceTag));
        }
        PaneleastSouth.setLayout(new FlowLayout());
        PaneleastSouth.add(price);PaneleastSouth.add(priceTag);priceTag.setEditable(false);
        Paneleast.setLayout(new BorderLayout());
        Paneleast.add(PaneleastNorth,BorderLayout.CENTER);
        Paneleast.add(PaneleastSouth,BorderLayout.SOUTH);

        //PanelSouth
        Panelsouth.setLayout(new FlowLayout());
        Panelsouth.add(ok);ok.addActionListener(new EventListener(fname,mail,phonenumber,items,intakeDate,returnDate,priceTag,checkBox));
        Panelsouth.add(annuler);annuler.addActionListener(new EventListener(this));


        //PanelNorth
        Panelnorth.setLayout(new BorderLayout());
        Panelnorth.add(Panelwest,BorderLayout.CENTER);
        Panelnorth.add(Paneleast,BorderLayout.EAST);

        //Panel
        P.setLayout(new BorderLayout());
        P.add(Panelnorth,BorderLayout.CENTER);
        P.add(Panelsouth,BorderLayout.SOUTH);


        this.setContentPane(P);
        pack();
        this.setSize(700,350);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
