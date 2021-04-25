import java.util.stream.*;
import java.nio.file.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Main {

  static FileWriter myWriter;


  public static void main(String... a) throws IOException {
    bmp2data();
  }

  static void bmp2data() throws IOException {
    // O stream basicamente procura por todos os arquivos que contem .java na pasta destino
    try (Stream<Path> paths = Files.walk(Paths.get("."))) {
      paths.filter(Files::isRegularFile).filter(x -> x.getFileName().toString().contains(".bmp")).forEach(x -> {   
        try {
          var text = new File(x.getFileName().toString().substring(0, x.getFileName().toString().length()-4) + ".data");      // Cria o .data 
          FileWriter myWriter = new FileWriter(text);

          BufferedImage img = ImageIO.read(x.toFile());
          int height = img.getHeight();
          int width = img.getWidth();

          myWriter.write(x.getFileName().toString().substring(0, x.getFileName().toString().length()-4) +": .word " + width + ", " + height + "\n.byte ");

          for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
              int rgb = img.getRGB(w, h);
              // regrinha de 3?
              // 255/7 = 37, arredondei para 37
              // entao n*17 eh o valor "normalizado" em 3 bits?
              // se for verdade eu quero o valor de: cor = n*37 => cor/37=n, valor truncado
              // para 2 bits fica: 255/3 = 85

              // entao a parte de baixo que faz sentido e a parte de cima, dos comentarios, que nao faz sentido
              // estavam funcionando marromenos entao oq eu fiz foi fazer a media dos 2 e acabou funcionando
              // se alguem souber me explicar por que eu aceito...

              int red = ((rgb >> 16) & 0x000000FF)/34;
              int green = ((rgb >> 8) & 0x000000FF)/34;
              int blue = ((rgb) & 0x000000FF)/74;

              // divide por 32 pq 8 bits dividido por 32 da 3 bits
              // excecao do azul q eh por 64 pq 8 bits div por 63 da 2 bits
              // ordem => red 7:5 green 4:2 blue 1:0
              //int resultado = blue + (green<<2) + (red<<4);
              
              int resultado = (blue<<6) + (green<<3) + red;
              myWriter.append("" +resultado + ", ");
            }
            myWriter.append("\n");
          }
          myWriter.close();   // fecha o arquivo muito importante!
        } catch (Exception f) {
          System.out.println("Erro!" + f);
        }
      });
    } catch (Exception e) {
      System.out.println("Erro!" + e);
    }
  }

}