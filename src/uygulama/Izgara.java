package uygulama;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Izgara extends JPanel implements ActionListener {

    Boolean secondStoper = false, paintKey = true, problem1 = false, problem2 = false;
    int basX = 0, basY = 0, sonX = 0, sonY = 0, tmp = 1, gecilenYol = 0;
    int basXp1 = 0, basYp1 = 0;
    int satir = 0, sutun = 0, miliSaniye = 500, second = 0, way = 0;
    static int[][] walls;
    String url;
    
    ArrayList <Integer> visited = new ArrayList <Integer>();
    ArrayList <Integer> emptyWays = new ArrayList <Integer>();
    ArrayList <Integer> emptyWays2 = new ArrayList <Integer>();
    ArrayList <Integer> emptyWays3 = new ArrayList <Integer>();
    ArrayList <Integer> shortestPath = new ArrayList <Integer>();
    ArrayList<Engel> wallList = new ArrayList<Engel>();
    
    Random r = new Random();
    JLabel blokSayisi = new JLabel();
    JLabel secondText = new JLabel();
    JLabel delayText = new JLabel();
    JButton shortPath = new JButton("Shortest Path");
    Timer timer;
    Engel duvar;
    
    public Izgara(String str) throws IOException {
        
        this.url = str;
        
        problem1 = true;
        problem2 = false;
        
        //gecilenYol = 0;
        
        //shortPath.setVisible(false);
        
        visited.clear();
        emptyWays.clear();
        emptyWays2.clear();
        emptyWays3.clear();
        wallList.clear();
        
        this.setLayout(null);
        this.setPreferredSize(new Dimension(1000, 700));
        this.setBackground(Color.darkGray);

        dosyaOku(url);
        setWalls();
        createWalls();
        setStartFinish();
        createPath();

        setButton();      
        
        timer = new Timer(miliSaniye, this);
        //timer.start();
    }

    public void paintComponent(Graphics g) { // her framede çizilecek elemanlar buraya fonk. olarak yazılabilir.
        super.paintComponent(g);
        
        if(paintKey)
            drawPath(); // bu fonksiyonda id değerleri sırayla değiştiriliyor. (timer sayesinde)
        try {
            drawMaze(g);
            
            //g.dispose(); // -> buton bunun yüzünden görünmüyor
        } catch (IOException ex) {
            Logger.getLogger(Izgara.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void showFinalScene(){
        
        paintKey = false;
        timer.stop();
        
        /*for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {
                wallList.get(i*satir+j).robot = 0;
                blokSayisi.setText("Visited "+gecilenYol+" block");               
            }
        }*/
        
        while(!secondStoper){
            blokSayisi.setText("Visited "+gecilenYol+" block");
            drawPath();
            repaint();
        }
    }
    
    private void setSideWinder(){
        
        wallList.clear();
        emptyWays.clear();
        
        paintKey = true;
        
        int index = 0, karar = 0, x = 0, y = 0, kontrol = 0;
        
        walls = new int[satir][sutun];
        
        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {
                if(i == 1 && j != 0 && j != sutun-1)
                    walls[i][j] = 0;
                else
                    walls[i][j] = 1;
                
                duvar = new Engel(j*(700/sutun), i*(700/satir));
                duvar.id = walls[i][j];
                
                if(i == 0 || i == satir - 1 || j == 0 || j == sutun - 1)
                    duvar.goster = 1;
                
                wallList.add(duvar);
            }
        }
        
        basX = basY = 1;
        sonX = sonY = sutun - 2;
        
        walls[sonY][sonX] = 0;
        wallList.get(sonY*satir+sonX).id = 0;
        
        for (int i = 3; i < satir - 1; i+=2) {
            for (int j = 1; j < sutun - 1; j++) {
                
                karar = r.nextInt(2);
                
                if(emptyWays.isEmpty())
                    karar = 1;
                
                else if(j == sutun - 2){
                    if(emptyWays.isEmpty())
                        emptyWays.add(i*satir+j);
                    
                    walls[i][j] = 0;
                    wallList.get(i*satir+j).id = 0;
                    karar = 0;   
                }
                
                else if(walls[i-2][j-1] == 1)
                    karar = 1;
                
                if(karar == 1){
                    walls[i][j] = 0;
                    emptyWays.add(i*satir+j);
                    wallList.get(i*satir+j).id = 0;
                }
                
                if(karar == 0){

                    index = emptyWays.get(r.nextInt(emptyWays.size()));
                    x = wallList.get(index).x/(700/sutun);
                    y = wallList.get(index).y/(700/satir);
                    
                    while(walls[y-2][x] != 0 && emptyWays.size() != 1){
                        
                        index = emptyWays.get(r.nextInt(emptyWays.size()));
                        x = wallList.get(index).x/(700/sutun);
                        y = wallList.get(index).y/(700/satir);
                    }               
                    
                    walls[y-1][x] = 0;
                    wallList.get((y-1)*satir+x).id = 0;
                                       
                    emptyWays.clear();
                }               
            }
        }
        
        if(satir % 2 == 0)
            for (int i = 1; i < sutun - 3; i++) {
                
                karar = r.nextInt(2);
                
                if(karar == 1 && walls[satir-2][i-1] != 0){
                    walls[satir-2][i] = 0;
                    wallList.get((satir-2)*satir+i).id = 0;
                }
            }
        
        sonKontrol();
        
        wallList.get(basY*satir+basX).id = 6;
        wallList.get(sonY*satir+sonX).id = 7;      
    }
    
    private void sonKontrol(){
        
        for (int i = 1; i < satir-1; i++) {
            for (int j = 1; j < sutun-1; j++) {
                
                int temp = r.nextInt(2);
                              
                if(walls[i][j] == 1 && temp == 1 && walls[i][j-1] == 0 && walls[i][j+1] == 0 && walls[i-1][j] == 1 && walls[i+1][j] == 1){
                    walls[i][j] = 0;
                    wallList.get(i*satir+j).id = 0;
                }
                
                //üstteki if kalkarsa finale sadece 1 yol ulaşır
                
                if(walls[i][j] == 0 && walls[i-1][j] == 1 && walls[i+1][j] == 1 && walls[i][j-1] == 1 && walls[i][j+1] == 1){
                    walls[i-1][j] = 0;
                    wallList.get((i-1)*satir+j).id = 0;
                }
                
                if(walls[i][j] == 0 && walls[i-1][j] == 1 && walls[i][j-1] == 1 && walls[i][j+1] == 1){
                    walls[i-1][j] = 0;
                    wallList.get((i-1)*satir+j).id = 0;
                } 
                
                if(i > 3 && i != satir-1 && j < satir - 10 && walls[i][j] == 0 && walls[i][j-1] == 1 && walls[i][j+1] == 1 && temp == 1){
                    walls[i][j] = 1;
                    wallList.get(i*satir+j).id = 1;
                }
                  
                // bu iki if sonradan eklendi, sonsuza sokan çıkarıldı.
                
                if(walls[i][j] == 1 && walls[i][j-1] == 1 && walls[i][j+1] == 1 && walls[i+1][j] == 0 && walls[i-1][j] == 0 && temp == 1){
                    walls[i][j] = 0;
                    wallList.get(i*satir+j).id = 0;
                }
                
                if(walls[i][j] == 1 && walls[i+1][j] == 1 && walls[i+1][j+1] == 1 && walls[i+1][j-1] == 1 && walls[i-1][j] == 0 && temp == 1){
                    walls[i][j] = 0;
                    wallList.get(i*satir+j).id = 0;
                }
            }
        }        
    }
    
    private void shortestPathProblem2(){
        
        visited.clear(); // ziyaret edilenlerin id değerini tutmak için.
        
        int sira = 2;             
        
        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {
                if(walls[i][j] == 2 || walls[i][j] == 3 || walls[i][j] == 5)
                    walls[i][j] = 1;
                
                if(walls[i][j] != 1)
                    walls[i][j] = 0;
                
                System.out.printf("%4d",walls[i][j]);
            }
            System.out.println("");
        }
        
        System.out.println("");        
        
        if(problem2) 
            basX = basY = 1;
        
        else if(problem1){
            basX = basXp1;
            basY = basYp1;

            for (int i = 0; i < wallList.size(); i++) { // bu sayede robotun öğrenmediği yerler duvar gibi oluyor.
                
                if(wallList.get(i).goster == 0){
                    walls[wallList.get(i).y/(700/satir)][wallList.get(i).x/(700/sutun)] = 1;
                }               
            }         
        }
                    
        wallList.get(basY*satir+basX).sira = 1;
        walls[basY][basX] = -2;
           
        while(!visited.contains(7)){          
            
            for (int i = 1; i < sutun-1; i++) {                
                for (int j = 1; j < satir-1; j++) {
                    
                   if(walls[i][j] != 0 && walls[i][j] != 1 && walls[i][j] != sira){
                       
                       if(walls[i][j-1] == 0){
                           walls[i][j-1] = sira;
                           wallList.get(i*satir+j-1).sira = sira;
                           visited.add(wallList.get(i*satir+j-1).id);
                       }
                       
                       if(walls[i-1][j] == 0){
                           walls[i-1][j] = sira;
                           wallList.get((i-1)*satir+j).sira = sira;
                           visited.add(wallList.get((i-1)*satir+j).id);
                       }
                       
                       if(walls[i][j+1] == 0){
                           walls[i][j+1] = sira;
                           wallList.get(i*satir+j+1).sira = sira;
                           visited.add(wallList.get(i*satir+j+1).id);
                       }
                       
                       if(walls[i+1][j] == 0){
                           walls[i+1][j] = sira;
                           wallList.get((i+1)*satir+j).sira = sira;
                           visited.add(wallList.get((i+1)*satir+j).id);
                       }                       
                   }
                }                              
            }  
            
            sira++;
        }
        
        wallList.get(basY*satir+basX).sira = 1;
        
        for (int i = 0; i < sutun; i++) {
            for (int j = 0; j < satir; j++) {
                System.out.printf("%4d", wallList.get(i*satir+j).sira);
            }
            
            System.out.println("");
        }
    }
    
    private void fillShortList(){
        
        shortestPath.clear();
        
        int sira = 0;
        int kisaYol = 0;
        
        if(problem2)
            basX = basY = 1;
        else if(problem1){
            basX = basXp1;
            basY = basYp1;
        }
        
        while(!shortestPath.contains(basY*satir+basX) && sonX > 0 && sonY > 0){
            
            kisaYol = 0;
            
            sira = wallList.get(sonY*satir+sonX).sira;
            
            if(wallList.get(sonY*satir+sonX-1).sira != 0 && wallList.get(sonY*satir+sonX-1).sira < sira){
                kisaYol = 1;
                sira = wallList.get(sonY*satir+sonX-1).sira;
            }
            
            if(wallList.get((sonY-1)*satir+sonX).sira != 0 && wallList.get((sonY-1)*satir+sonX).sira < sira){
                kisaYol = 2;
                sira = wallList.get((sonY-1)*satir+sonX).sira;
            }
            
            if(wallList.get(sonY*satir+sonX+1).sira != 0 && wallList.get(sonY*satir+sonX+1).sira < sira){
                kisaYol = 3;
                sira = wallList.get(sonY*satir+sonX+1).sira;
            }
            
            if(wallList.get((sonY+1)*satir+sonX).sira != 0 && wallList.get((sonY+1)*satir+sonX).sira < sira){
                kisaYol = 4;
                sira = wallList.get((sonY+1)*satir+sonX).sira;
            }
            
            if(kisaYol == 0){
                
                if(walls[sonY][sonX+1] != 1) {
                    wallList.get(sonY*satir+sonX).sira = sira + 1;
                    sonX++;
                }
                    
                else{                    
                    wallList.get(sonY*satir+sonX).sira = sira + 1;
                    sonY--;
                }
            }
            
            switch(kisaYol){
                case 1 -> sonX--;
                case 2 -> sonY--;
                case 3 -> sonX++;
                case 4 -> sonY++;
            }
            
            if(!shortestPath.contains(sonY*satir+sonX))
                shortestPath.add(sonY*satir+sonX);
        }
        
    }
    
    private void drawMaze(Graphics g) throws IOException{
        
        final BufferedImage wall = ImageIO.read(new URL("https://cdn-icons-png.flaticon.com/128/698/698684.png"));
        Image scaledWall = wall.getScaledInstance((700/sutun), (700/satir), Image.SCALE_DEFAULT);
        
        final BufferedImage fog = ImageIO.read(new URL("https://mw1.google.com/mw-planetary/sky/skytiles_v1/75_32_7.jpg"));
        Image scaledFog = fog.getScaledInstance((700/sutun), (700/satir), Image.SCALE_DEFAULT);
        
        //üstte ekrana çizilecek olan duvar ve yollar oranlanıyor.
        
        for (Engel engel : wallList) {

            switch (engel.id) {
                case 1:
                case 2:
                case 3: 
                    g.setColor(Color.darkGray);
                    break;
                case 5 : 
                    g.setColor(Color.black);
                    engel.goster = 1;
                    break;
                case 4 : 
                    g.setColor(Color.red);
                    break;
                case 6 :
                case 7 :
                    g.setColor(Color.magenta);
                    break;
                case 8 : 
                    g.setColor(Color.green);
                    break;
                case 9 :
                    g.setColor(Color.red);
                    break;
                case -3:
                    g.setColor(Color.magenta);
                    break;
                default :
                    g.setColor(Color.gray);
                    break;
            }
            
            if(engel.robot == 1){
                g.setColor(Color.white);
                engel.robot = 0;
            }
            
            g.fillRect(engel.x, engel.y, (700/sutun), (700/satir));
            
            if(engel.goster == 0 && engel.id != 5 && engel.id != 6 && engel.id != 7)
                g.drawImage(scaledFog, engel.x, engel.y, this);
                //g.setColor(Color.darkGray);

            //g.fillRect(engel.x, engel.y, (700/sutun), (700/satir));
            
            if((engel.id == 1 || engel.id == 2 || engel.id == 3 || engel.id == 5) && engel.goster == 1)
                g.drawImage(scaledWall, engel.x, engel.y, this);
            
            g.setFont(new Font("Calibri", Font.BOLD, 25));
            g.setColor(Color.black);
            
            if (engel.id == 6) {
                g.drawString("S", engel.x, engel.y+20);                
            }

            if (engel.id == 7) {
                g.drawString("F", engel.x, engel.y+20);               
            }
        }       
    }
    
    private void drawPath(){
        
        if(tmp < visited.size()-1){          
            
            duvar = wallList.get(visited.get(tmp));
            
            if(duvar.id == 0){
                duvar.id = 8;
                gecilenYol++;
            }
            else
                duvar.id = 9;
            
            wallList.get(visited.get(tmp)).goster = 1; // kendisi
            wallList.get(visited.get(tmp) - 1 ).goster = 1; // sol
            wallList.get(visited.get(tmp) + 1 ).goster = 1;  // sağ               
            wallList.get(visited.get(tmp) - sutun ).goster = 1; // üst
            wallList.get(visited.get(tmp) + sutun ).goster = 1; // alt     
            
            if(paintKey) duvar.robot = 1;
        }
        
        else if(tmp == visited.size()){
            /*for (Engel e : wallList) { // bu hedefe ulaştıktan sonra bulutu kaldırıyor.
                e.goster = 1;
            }*/
            
            secondStoper = true;
            shortPath.setEnabled(true);
        }        
        tmp++;
    }
    
    // timer içerisine yazılan sürede nelerin olacağı actionPerformed içerisine yazılacak.
    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(!secondStoper){
            second += 1000-miliSaniye;
            delayText.setText("Delay "+miliSaniye+" ms");
            secondText.setText(second/1000+" second");
            blokSayisi.setText("Visited "+gecilenYol+" block");
        }
        
        if(paintKey)
            repaint();
    }

    // buradan sonrası eklenecek
    private void createWalls() {

        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {

                duvar = new Engel(j * ((700/satir)), i * ((700/sutun))); // x ye y koordinat ayarlama.
                duvar.id = walls[i][j];
                wallList.add(duvar);
            }
        }
    }
    
    private void createPath(){
        
        System.out.println("Baslangic: "+basX+" and "+basY);
        System.out.println("Final: "+sonX+" and "+sonY+"\n");
        
        walls[sonY][sonX] = 0; // eğer 0 olmazsa oyun bitmez.
        int i = 0;
        
        visited.add(basY*satir+basX);
        wallList.get(sonY*satir+sonX).id = 7;
        
        while(visited.contains(sonY*satir+sonX) == false){
            
            // 0 olanlar (hiç gidilmeyenler)
            
            emptyWays.clear();
            
            if(walls[basY][basX-1] == 0)
                emptyWays.add(1);
            
            if(walls[basY-1][basX] == 0)
                emptyWays.add(2);
            
            if(walls[basY][basX+1] == 0)
                emptyWays.add(3);
            
            if(walls[basY+1][basX] == 0)
                emptyWays.add(4);
            
            // 8 olanlar (yeşil yollar)
            
            emptyWays2.clear();
            
            if(walls[basY][basX-1] == 8)
                emptyWays2.add(1);
            
            if(walls[basY-1][basX] == 8)
                emptyWays2.add(2);
            
            if(walls[basY][basX+1] == 8)
                emptyWays2.add(3);
            
            if(walls[basY+1][basX] == 8)
                emptyWays2.add(4);
            
            // -1 olanlar (kırmızı yollar)
            
            emptyWays3.clear();
            
            if(walls[basY][basX-1] == -1)
                emptyWays3.add(1);
            
            if(walls[basY-1][basX] == -1)
                emptyWays3.add(2);
            
            if(walls[basY][basX+1] == -1)
                emptyWays3.add(3);
            
            if(walls[basY+1][basX] == -1)
                emptyWays3.add(4);
            
            // etrafta 8 veya 0 var mı ? -> ikiside boş ise önceden gidilmiş yere git.
            
            if(emptyWays.isEmpty() && emptyWays2.isEmpty() && emptyWays3.isEmpty()){
                
                basX = wallList.get(visited.get(visited.size()-2-i)).x/((700/sutun));
                basY = wallList.get(visited.get(visited.size()-2-i)).y/((700/satir));
                
                visited.add(basY*satir+basX);
                i+=2;                   
            }
            
            else{
                
                // hiç gidilmeyen yol veya bir kere gidilen yol varsa ilk tercih o olacak.
                
                if (emptyWays.isEmpty() && emptyWays2.isEmpty())
                    way = emptyWays3.get(r.nextInt(emptyWays3.size()));
                else if(emptyWays.isEmpty()) 
                    way = emptyWays2.get(r.nextInt(emptyWays2.size()));
                else
                    way = emptyWays.get(r.nextInt(emptyWays.size()));
                
                switch(way){
                    case 1 -> basX--;
                    case 2 -> basY--;
                    case 3 -> basX++;
                    case 4 -> basY++;
                }
            
                // Bu ifler etkisiz hale gelirse kırmızılarda istediği kadar gezer.
                
                if(wallList.get(basY*sutun+basX).ziyaretSayisi == 0){
                    wallList.get(basY*sutun+basX).ziyaretSayisi++;
                    i = 0;
                    visited.add(basY*satir+basX);
                    walls[basY][basX] = 8; 
                }
                
                else if(wallList.get(basY*sutun+basX).ziyaretSayisi < 11){ // en fazla 10 kere ziyaret etsin yoksa uzun sürebiliyor
                    wallList.get(basY*sutun+basX).ziyaretSayisi++;
                    i = 0;
                    visited.add(basY*satir+basX);
                    walls[basY][basX] = -1;
                }
                    
               
                // Üstte yazılan üç if bloğu sayesinde yeşil alanlara öncelik verildi.
                // ikinci ve üçüncü if etkisiz kalırsa ışınlanıyor gibi oluyor.
                // Çünkü bir bloğu ne kadar ziyaret edersem edeyim id değeri 8 de kalıyor.
                // dolayısıyla emptyWay2 içerisine kayıt ediliyor.
                // Ama ziyaret sayısına göre visited listesine kayıt edilmiyor ve ışınlanıyor :D
                    
                if(wallList.get(basY*satir+basX-1).id == 7 || wallList.get((basY-1)*satir+basX).id == 7 || wallList.get(basY*satir+basX+1).id == 7 || wallList.get((basY+1)*satir+basX).id == 7)
                    visited.add(sonY*satir+sonX);
                
                // üstteki if eğer robot final olan bloğu tanırsa direkt oraya girmesi için.
                
            }      
        }
    }
    
    private void setStartFinish() {

        Random r = new Random();
        int bas = r.nextInt(satir*sutun);
        int son = r.nextInt(satir*sutun);

        while (wallList.get(bas).id != 0 && wallList.get(bas).id != 4) {
            bas = r.nextInt(satir*sutun);
        }

        while (wallList.get(son).id != 0 && wallList.get(son).id != 4) {
            son = r.nextInt(satir*sutun);
        }

        basX = wallList.get(bas).x/((700/sutun));
        basY = wallList.get(bas).y/((700/satir));
        
        sonX = wallList.get(son).x/((700/sutun));
        sonY = wallList.get(son).y/((700/satir));
        
        walls[wallList.get(bas).y / ((700/sutun))][wallList.get(bas).x / ((700/sutun))] = 6;
        walls[wallList.get(son).y / ((700/satir))][wallList.get(son).x / ((700/satir))] = 7;

        wallList.get(bas).id = 6; // id = 6 -> baslangic
        wallList.get(son).id = 7; // id = 7 -> son
        
        basXp1 = basX;
        basYp1 = basY;
        
        wallList.get(basY*satir+basX-1).goster = 1;
        wallList.get((basY-1)*satir+basX).goster = 1;
        wallList.get(basY*satir+basX+1).goster = 1;
        wallList.get((basY+1)*satir+basX).goster = 1;
    }

    private void setWalls() { // 3x3 lük alana engel koydum.

        int key3 = 1, key2 = 1; // random bir şekkilde duvar oluşturmak için key değişkeni var.

        for (int i = 0; i < satir; i++) {
            for (int j = 0; j < sutun; j++) {

                if (walls[i][j] == 3 && walls[i - 1][j] + walls[i + 1][j] + walls[i][j - 1] + walls[i][j + 1] == 12 && key3 == 1) {
                    walls[i][j - 1] = walls[i - 1][j] = walls[i - 1][j - 1] = 0;
                    key3 = 0;
                }

                if (walls[i][j] == 3 && walls[i - 1][j] + walls[i + 1][j] + walls[i][j - 1] + walls[i][j + 1] == 12 && key3 == 0) {
                    walls[i][j + 1] = walls[i + 1][j] = walls[i + 1][j + 1] = 0;
                    key3 = 1;
                }

                if (walls[i][j] == 2 && walls[i + 1][j] + walls[i + 1][j + 1] + walls[i][j + 1] == 6 && key2 == 1) {
                    walls[i + 1][j + 1] = walls[i][j + 1] = 0;
                    key2 = 0;
                }

                if (walls[i][j] == 2 && walls[i + 1][j] + walls[i + 1][j + 1] + walls[i][j + 1] == 6 && key2 == 0) {
                    walls[i + 1][j] = walls[i + 1][j + 1] = 0;
                    key2 = 1;
                }
            }
        }
    }

    
    public void dosyaOku(String url) throws FileNotFoundException, IOException {

        URL txt_url = new URL(url);
        
        BufferedReader br = new BufferedReader(new InputStreamReader(txt_url.openStream()));       

        String str;
        int row = 1;
        int key = 0;        

        while ((str = br.readLine()) != null) {

            if(key == 0){
                satir = sutun = str.length()+2;
                walls = new int[satir][sutun]; // matris burada oluşuyor.
                key++;
            }
            
            for (int i = 1; i < satir-1; i++) {
                walls[row][i] = Integer.parseInt(str.charAt(i-1) + "");
            }
            row++;
        }
        
        for (int i = 0; i < satir; i++) { // Dış duvarlar
            for (int j = 0; j < sutun; j++) {
                if (i == sutun-1 || j == satir-1 || i == 0 || j == 0) {
                    walls[i][j] = 5;
                }
            }
        }  
    }
    
    private void smileyFace(){
        
        wallList.clear();
        satir = sutun = 15;
        
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
             
                duvar = new Engel(j*(700/15), i*(700/15));
                duvar.goster = 1;
                duvar.id = 5;
                
                if((i == 3 || i == 4 || i == 5)&& (j == 3 || j == 4 || j == 5)){
                    if(i == 4 && j == 4)  duvar.id = 5;
                    else duvar.id = 8;                       
                }
                
                if((i == 3 || i == 4 || i == 5)&& (j == 9 || j == 10 || j == 11)){
                    if(i == 4 && j == 10)  duvar.id = 5;
                    else duvar.id = 8;                       
                }
                
                if((i == 8 || i == 9) && j == 4)
                    duvar.id = 8;
                
                if((i == 8 || i == 9) && j == 10)
                    duvar.id = 8;
                
                if(i == 10 && j > 4 && j < 10)
                    duvar.id = 8;
                
                wallList.add(duvar);
            }           
        }
    }
    
    private void setButton(){      
       
        JButton p2Onay = new JButton();
        JButton yavas = new JButton();
        JButton hizli = new JButton();
        JButton start = new JButton("Start");
        JButton stop = new JButton("Stop");
        JButton p2 = new JButton("Problem 2");
        JTextField dimension = new JTextField("Dimension");
        JButton bitir = new JButton("Bitir");
        
        shortPath.setBounds(775, 400, 150, 50);
        shortPath.setBackground(Color.gray);
        shortPath.setForeground(Color.orange);
        shortPath.setEnabled(false);
        this.add(shortPath);
        
        p2.setBounds(775, 280, 150,50);
        p2.setBackground(Color.gray);
        p2.setForeground(Color.white);
        this.add(p2);
        
        p2Onay.setBounds(880, 340, 45,50);
        p2Onay.setBackground(Color.yellow);
        p2Onay.setEnabled(false);
        this.add(p2Onay);
        
        dimension.setBounds(775, 340, 100,50);
        dimension.setBackground(Color.black);
        dimension.setForeground(Color.yellow);
        dimension.setHorizontalAlignment(JTextField.CENTER);
        dimension.setEditable(false);
        this.add(dimension);
        
        secondText.setBounds(700, 460, 300, 50);
        secondText.setForeground(Color.yellow);
        secondText.setText(second/1000+" second");
        secondText.setFont(new Font("Calibri", Font.BOLD, 25));
        secondText.setHorizontalAlignment(JLabel.CENTER);
        this.add(secondText);
        
        blokSayisi.setBounds(700, 510, 300, 50);
        blokSayisi.setForeground(Color.yellow);
        blokSayisi.setText("Visited "+gecilenYol+" block");
        blokSayisi.setFont(new Font("Calibri", Font.BOLD, 25));
        blokSayisi.setHorizontalAlignment(JLabel.CENTER);
        this.add(blokSayisi);
        
        delayText.setBounds(700,560,300,50);
        delayText.setForeground(Color.white);
        delayText.setText("Delay "+miliSaniye+" ms");
        delayText.setFont(new Font("Calibri", Font.BOLD, 25));
        delayText.setHorizontalAlignment(JLabel.CENTER);
        this.add(delayText);
        
        stop.setBounds(775,100,150,50);
        stop.setBackground(Color.gray);
        stop.setForeground(Color.black);
        this.add(stop);
        
        start.setBounds(775,30,150,50);
        start.setBackground(Color.gray);
        start.setForeground(Color.green);
        this.add(start);
        
        bitir.setBounds(775,620,150,50);
        bitir.setBackground(Color.gray);
        bitir.setForeground(Color.green);
        this.add(bitir);
        
        hizli.setBounds(930,100,38,50);
        hizli.setBackground(Color.green);
        hizli.setForeground(Color.orange);
        this.add(hizli);
        
        yavas.setBounds(730,100,38,50);
        yavas.setBackground(Color.red);
        yavas.setForeground(Color.orange);
        this.add(yavas);
        
        shortPath.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                
                paintKey = false;
                
                shortestPathProblem2();
                
                fillShortList(); // shortestPath burada doluyor
                
                for (int i = 0; i < shortestPath.size(); i++) {
                    wallList.get(shortestPath.get(i)).id = -3;
                }
                
                if(problem2)
                    for (Engel yol : wallList) 
                        yol.goster = 1;
                                
                wallList.get(basY*satir+basX).id = 6;
                
                repaint();
            }
        });
        
        p2Onay.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                tmp = 1;
                timer.setDelay(500);
                
                start.setBackground(Color.gray);
                stop.setBackground(Color.gray);
                
                //problem 2 onaylandıgında olacak fonksiyonları burada çağır.
                satir = sutun = Integer.parseInt(dimension.getText())+2;
                start.setEnabled(true);
                               
                visited.clear();
                secondStoper = false;
                
                setSideWinder();
                
                createPath();
                repaint();
                
                shortPath.setEnabled(true);
                p2Onay.setEnabled(false);
                dimension.setEditable(false);
            }
        });
        
        p2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {                
                                
                problem2 = true;
                problem1 = false;
                
                start.setEnabled(false);
                timer.stop();
                
                for(Engel eng : wallList){                                     
                    eng.id = 0;
                    eng.robot = 0;
                }
                
                paintKey = false;
                
                smileyFace();
                repaint();
                
                second = 0;
                gecilenYol = 0;
                miliSaniye = 500;
                delayText.setText("Delay "+miliSaniye+" ms");
                secondText.setText(second/1000+" second");               
                blokSayisi.setText("Visited "+gecilenYol+" block");
                
                bitir.setBackground(Color.gray);
                
                p2Onay.setEnabled(true);
                dimension.setEditable(true);
                
                JOptionPane.showMessageDialog(null, "1) Labirent boyutunu onaylamak için sarı tuşa basınız.\n"
                        + "2) Start butonu ile robotu harekete geçirebilirsiniz.", "Bilgilendirme", JOptionPane.INFORMATION_MESSAGE);
                
            }
        });
        
         yavas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                miliSaniye += (miliSaniye < 1000) ? 100 : 0;
                timer.setDelay(miliSaniye); // bitir butonuna basılırsa delay 0 olacak
                delayText.setText("Delay "+miliSaniye+" ms");
                secondText.setText(second/1000+" second");
            }
        });
        
        hizli.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                miliSaniye -= (miliSaniye != 0) ? 100 : 0;
                timer.setDelay(miliSaniye); // bitir butonuna basılırsa delay 0 olacak
                delayText.setText("Delay "+miliSaniye+" ms");
                secondText.setText(second/1000+" second");
            }
        });
        
        start.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.start();
                start.setBackground(Color.white);
                stop.setBackground(Color.gray);
            }
        });
        
        stop.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                timer.stop();
                start.setBackground(Color.gray);
                stop.setBackground(Color.white);
            }
        });
        
        bitir.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                showFinalScene();
                bitir.setBackground(Color.white);
                start.setBackground(Color.gray);
                stop.setBackground(Color.gray);
            }
        });
    }
}
