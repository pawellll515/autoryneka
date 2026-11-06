package pl.rynekklikacz;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2522;
import net.minecraft.class_310;

public class Wpis {
   public String item = "";
   public String id = "";
   public String nazwa = "";
   public List<String> enchanty = new ArrayList<>();
   public List<String> lore = new ArrayList<>();
   public int ilosc = 1;
   public long cena = -1L;
   public long pierwszy;
   public long ostatni;
   public int razy;
   public long wygasa;
   public Boolean kolorowa;
   transient String klucz;
   private transient class_1799 stack;
   private transient String tekst;

   public class_1799 stack() {
      if (this.stack == null) {
         class_310 mc = class_310.method_1551();
         if (mc.field_1687 == null) {
            return new class_1799(class_1802.field_8407);
         }

         this.stack = new class_1799(class_1802.field_8407);

         try {
            this.stack = class_1799.method_57360(mc.field_1687.method_30349(), class_2522.method_10718(this.item)).orElse(this.stack);
         } catch (Exception var3) {
         }
      }

      return this.stack;
   }

   public String grupa() {
      String k = this.id + "#" + this.nazwa;
      return this.kolorowa() ? k : k + "#" + String.join("|", this.enchanty);
   }

   public boolean aktywna(long teraz) {
      if (teraz - this.ostatni > 86400000L) {
         return false;
      } else {
         return this.wygasa == 0L ? teraz - this.ostatni < 21600000L : this.wygasa > teraz;
      }
   }

   public double zaSztukeD() {
      return this.cena < 0L ? -1.0 : (double)this.cena / Math.max(1, this.ilosc);
   }

   public long zaSztuke() {
      return this.cena < 0L ? -1L : Math.round((double)this.cena / Math.max(1, this.ilosc));
   }

   public boolean kolorowa() {
      if (this.kolorowa == null) {
         if (class_310.method_1551().field_1687 == null) {
            return false;
         }

         this.kolorowa = Skaner.kolorowa(this.stack());
      }

      return this.kolorowa;
   }

   void ustaw(class_1799 s, String snbt) {
      this.stack = s;
      this.item = snbt;
      this.tekst = null;
   }

   public String tekst() {
      if (this.tekst == null) {
         this.tekst = (this.nazwa + " " + this.id + " " + String.join(" ", this.enchanty) + " " + String.join(" ", this.lore)).toLowerCase(Locale.ROOT);
      }

      return this.tekst;
   }

   Wpis kopia() {
      Wpis w = new Wpis();
      w.item = this.item;
      w.id = this.id;
      w.nazwa = this.nazwa;
      w.enchanty = this.enchanty;
      w.lore = this.lore;
      w.ilosc = this.ilosc;
      w.cena = this.cena;
      w.pierwszy = this.pierwszy;
      w.ostatni = this.ostatni;
      w.razy = this.razy;
      w.kolorowa = this.kolorowa;
      w.wygasa = this.wygasa;
      return w;
   }
}
