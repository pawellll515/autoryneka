package pl.rynekklikacz;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.concurrent.locks.LockSupport;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2813;
import net.minecraft.class_310;
import net.minecraft.class_634;

public final class Skanowanie {
   private static final int SLOT = 50;
   private static final long SPOKOJ = 3L;
   private static final Pattern STRONA = Pattern.compile("\\((\\d+)\\s*/\\s*(\\d+)\\)");
   private static volatile Thread watek;
   private static volatile boolean wToku;
   private static volatile boolean czeka;
   private static volatile boolean zmiana;
   private static volatile long wyslano;
   private static volatile long ostatnia;
   private static volatile long ping = 120L;
   private static volatile int strona;
   private static volatile int stron;
   private static volatile int odwiedzone;
   private static volatile int pominiete;
   private static volatile int kliki;
   private static volatile String wynik = "";
   private static int syncPrzy;
   private static String tytulPrzy;
   private static long start;

   private Skanowanie() {
   }

   public static boolean dziala() {
      return watek != null;
   }

   public static String status() {
      if (dziala()) {
         long s = Math.max(1L, System.currentTimeMillis() - start);
         return "§bSkanowanie §f" + strona + "/" + (stron > 0 ? stron : "?") + " §8· §7" + String.format("%.1f", odwiedzone * 1000.0 / s) + " str/s";
      } else {
         return wynik;
      }
   }

   public static void start() {
      if (watek == null) {
         Klikacz.stop();
         Snajper.stop();
         Skaner.wyczysc();
         class_310 mc = class_310.method_1551();
         Sortowanie.start(mc);
         strona = 0;
         stron = 0;
         odwiedzone = 0;
         pominiete = 0;
         kliki = 0;
         czeka = false;
         wToku = false;
         wynik = "";
         start = System.currentTimeMillis();
         Thread t = new Thread(() -> {
            long bezRynku = 0L;

            while (watek == Thread.currentThread()) {
               long teraz = System.currentTimeMillis();
               if (!Klikacz.rynek(mc.field_1755)) {
                  if (bezRynku == 0L) {
                     bezRynku = teraz;
                  } else if (teraz - bezRynku > 1500L) {
                     zakoncz("§cSkanowanie przerwane · zamknięty rynek");
                  }

                  LockSupport.parkNanos(5000000L);
               } else {
                  bezRynku = 0L;
                  boolean gotowe = !czeka || zmiana && teraz - ostatnia >= 3L || teraz - wyslano >= Math.max(150L, ping * 3L);
                  if (gotowe && !wToku) {
                     wToku = true;
                     mc.execute(() -> {
                        try {
                           krok(mc);
                        } finally {
                           wToku = false;
                        }
                     });
                  }

                  LockSupport.parkNanos(1000000L);
               }
            }
         }, "RynekKlikacz-skan");
         t.setDaemon(true);
         watek = t;
         t.start();
      }
   }

   static void aktualizacja(class_310 mc, boolean zawartosc) {
      if (czeka && zawartosc && mc.field_1724 != null && mc.field_1755 != null) {
         if (mc.field_1724.field_7512.field_7763 != syncPrzy || !mc.field_1755.method_25440().getString().equals(tytulPrzy)) {
            zmiana = true;
         }
      }
   }

   private static void krok(class_310 mc) {
      if (watek != null && mc.field_1724 != null && Klikacz.rynek(mc.field_1755)) {
         class_634 net = mc.method_1562();
         class_1703 h = mc.field_1724.field_7512;
         if (net != null && h.field_7763 != 0 && h.field_7761.size() > 50) {
            if (Sortowanie.naprawia(mc)) {
               czeka = false;
            } else {
               if (czeka && zmiana) {
                  ping = (ping * 3L + ostatnia - wyslano) / 4L;
               }

               String tytul = mc.field_1755.method_25440().getString();
               Skaner.skanuj(mc, true);
               Matcher m = STRONA.matcher(tytul);
               if (m.find()) {
                  int k = Integer.parseInt(m.group(1));
                  int n = Integer.parseInt(m.group(2));
                  if (k != strona) {
                     if (strona > 0 && k > strona + 1) {
                        pominiete = pominiete + (k - strona - 1);
                     }

                     odwiedzone++;
                  }

                  strona = k;
                  stron = n;
                  if (n > 0 && k >= n) {
                     long s = (System.currentTimeMillis() - start) / 1000L;
                     zakoncz("§aZeskanowano " + n + " stron w " + s + " s" + (pominiete > 0 ? " §8· §cpominięte " + pominiete : ""));
                     return;
                  }
               }

               czeka = true;
               zmiana = false;
               syncPrzy = h.field_7763;
               tytulPrzy = tytul;
               wyslano = System.currentTimeMillis();
               net.method_52787(
                  new class_2813(h.field_7763, h.method_37421(), 50, 0, class_1713.field_7790, class_1799.field_8037, new Int2ObjectOpenHashMap())
               );
               kliki++;
            }
         }
      }
   }

   private static void zakoncz(String w) {
      wynik = w;
      watek = null;
      Skaner.zapisz();
   }

   public static void stop() {
      if (watek != null) {
         zakoncz("§7Skanowanie zatrzymane na " + strona + "/" + (stron > 0 ? stron : "?"));
      }
   }
}
