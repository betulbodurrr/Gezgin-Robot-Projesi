package uygulama;

public class Engel{

    int x = 0, y = 0;
    int id = 0;
    int robot = 0;
    int goster = 0;
    int ziyaretSayisi = 0;
    int sira = 0;
    
    // id degerleri : 0 -> bos yol, 1-2-3 -> engel, 5 -> dis duvar, 6 -> start, 7 -> finish
    
    public Engel(){
    }

    public Engel(int x, int y){
        this.x = x;
        this.y = y;
    }
}
