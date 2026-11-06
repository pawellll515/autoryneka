package pl.rynekklikacz.gui;

import net.minecraft.class_124;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_368;
import net.minecraft.class_374;
import net.minecraft.class_368.class_369;

public class ZakupToast implements class_368 {
   private static final int SZER = 220;
   private static final int WYS = 42;
   private final class_1799 ikona;
   private final String tytul;
   private final String linia1;
   private final String linia2;
   private final int kolor;
   private final int kolor2;
   private class_369 widocznosc = class_369.field_2210;
   private long czas;

   public ZakupToast(class_1799 ikona, String tytul, String linia1, String linia2, int kolor, int kolor2) {
      this.ikona = ikona;
      this.tytul = tytul;
      this.linia1 = linia1;
      this.linia2 = linia2;
      this.kolor = kolor;
      this.kolor2 = kolor2;
   }

   public int method_29049() {
      return 220;
   }

   public int method_29050() {
      return 42;
   }

   public class_369 method_61988() {
      return this.widocznosc;
   }

   public void method_61989(class_374 manager, long time) {
      this.czas = time;
      this.widocznosc = time >= 7000.0 * manager.method_48221() ? class_369.field_2209 : class_369.field_2210;
   }

   public void method_1986(class_332 c, class_327 tr, long startTime) {
      Draw.box(c, 0, 0, 220, 42, -15657959, Draw.mix(-14275269, this.kolor, 0.45F));
      c.method_25296(1, 1, 219, 21, Draw.alpha(this.kolor, 34), Draw.alpha(this.kolor, 0));
      c.method_25294(1, 4, 3, 38, this.kolor);
      float t = (float)(this.czas % 1600L) / 1600.0F;
      int bx = (int)(-40.0F + 300.0F * t);

      for (int i = 0; i < 40; i++) {
         int xx = bx + i;
         if (xx > 1 && xx < 219) {
            float k = 1.0F - Math.abs(i - 20) / 20.0F;
            c.method_25294(xx, 40, xx + 1, 41, Draw.alpha(this.kolor, (int)(192.0F * k)));
         }
      }

      Draw.box(c, 8, 9, 24, 24, -15986924, Draw.mix(-14275269, this.kolor, 0.6F));
      c.method_51448().method_22903();
      c.method_51448().method_46416(12.0F, 13.0F, 0.0F);
      c.method_51427(this.ikona, 0, 0);
      c.method_51431(tr, this.ikona, 0, 0);
      c.method_51448().method_22909();
      c.method_51439(tr, class_2561.method_43470(this.tytul).method_27692(class_124.field_1067), 38, 6, this.kolor, false);
      c.method_51433(tr, Draw.trim(tr, this.linia1, 176), 38, 17, -1512205, false);
      c.method_51433(tr, Draw.trim(tr, this.linia2, 176), 38, 28, this.kolor2, false);
   }
}
