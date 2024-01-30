package uygulama;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GameFrame{
    
    Izgara izgara;
    JFrame f = new JFrame();
    
    private int key = 1;
    
    public GameFrame() throws IOException{      
        
        izgara = new Izgara("http://bilgisayar.kocaeli.edu.tr/prolab2/url2.txt");
        
        f.setTitle("maze game");
        
        JOptionPane.showMessageDialog(null, "Oyun Problem 1 ile başlamaktadır.\n"
                + "Haritalar arasında geçiş yapmak için \"Map Değiştir\" butonuna basınız.\n"
                , "Bilgilendirme", JOptionPane.INFORMATION_MESSAGE);
        
        JButton toggle = new JButton("Map Değiştir");
        toggle.setBounds(775,170,150,50);
        toggle.setBackground(Color.gray);
        toggle.setForeground(Color.orange);
        izgara.add(toggle);
        
        toggle.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                
                try {
                    f.remove(izgara);
                    
                    System.out.println("key: "+key);
                    
                    if(key == 1){ 
                        izgara = new Izgara("http://bilgisayar.kocaeli.edu.tr/prolab2/url1.txt");
                        key = 2;
                    }
                    
                    else if(key == 2){ 
                        izgara = new Izgara("http://bilgisayar.kocaeli.edu.tr/prolab2/url2.txt");
                        key = 1;
                    }
                    
                    izgara.add(toggle);
                    f.add(izgara);
                    
                } catch (IOException ex) {
                    Logger.getLogger(GameFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                f.revalidate();
                f.repaint();       
            }
        });
        
        f.add(izgara);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);
        
        /*this.add(izgara);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setResizable(false);*/
        
    }
  
}
