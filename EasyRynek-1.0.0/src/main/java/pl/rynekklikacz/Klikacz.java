package pl.rynekklikacz;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.concurrent.locks.LockSupport;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2813;
import net.minecraft.class_310;
import net.minecraft.class_437;
import net.minecraft.class_465;
import net.minecraft.class_634;
import net.minecraft.class_746;

public final class Klikacz {
   private static final long SPOKOJ = 3L;
   private static volatile Thread watek;
   private static volatile int kliki;
   private static volatile int cykle;
   private static volatile int etap;
   private static volatile int lewe;
   private static volatile int prawe;
   private static volatile long czasCyklu;
   private static long startCyklu;
   private static volatile boolean wToku;
   private static volatile long wyslano;
   private static volatile long ostatnia;
   private static volatile int aktualizacje;
   private static volatile boolean zmiana;
   private static int syncPrzy;
   private static long podpisPrzy;
   private static int cykleOkno;
   private static long oknoOd;
   private static volatile int naSekunde;
   private static volatile long odstep = 15L;
   private static volatile long ping = 120L;
   private static boolean ostatniaNast;

   private Klikacz() {
   }

   public static boolean dziala() {
      return watek != null;
   }

   public static int kliki() {
      return kliki;
   }

   public static int cykle() {
      return cykle;
   }

   public static int naSekunde() {
      return naSekunde;
   }

   public static boolean rynek(class_437 s) {
      return Kalibracja.pasuje(s);
   }

   public static void aktualizacja(boolean zawartosc) {
      class_310 mc = class_310.method_1551();
      if (mc.method_18854()) {
         ostatnia = System.currentTimeMillis();
         aktualizacje++;
         Skanowanie.aktualizacja(mc, zawartosc);
         if (zawartosc && etap != 0 && mc.field_1724 != null) {
            class_1703 h = mc.field_1724.field_7512;
            if (h.field_7763 != syncPrzy || podpis(h) != podpisPrzy) {
               zmiana = true;
            }
         }
      }
   }

   public static int lewe() {
      return lewe;
   }

   public static int prawe() {
      return prawe;
   }

   public static long czasCyklu() {
      return czasCyklu;
   }

   static long limit() {
      return Math.max(120L, ping * 3L);
   }

   public static void start() {
      if (watek == null) {
         Skanowanie.stop();
         class_310 mc = class_310.method_1551();
         kliki = 0;
         cykle = 0;
         lewe = 0;
         prawe = 0;
         etap = 0;
         ostatniaNast = false;
         wToku = false;
         Thread t = new Thread(() -> {
            long bezRynku = 0L;

            while (watek == Thread.currentThread()) {
               long teraz = System.currentTimeMillis();
               if (!rynek(mc.field_1755)) {
                  if (bezRynku == 0L) {
                     bezRynku = teraz;
                  } else if (teraz - bezRynku > 500L) {
                     etap = 0;
                  }

                  LockSupport.parkNanos(5000000L);
               } else {
                  bezRynku = 0L;
                  boolean gotowe = teraz - wyslano >= odstep && (etap == 0 || zmiana && teraz - ostatnia >= 3L || teraz - wyslano >= limit());
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
         }, "RynekKlikacz");
         t.setDaemon(true);
         watek = t;
         t.start();
      }
   }

   private static long podpis(class_1703 h) {
      long p = 0L;
      int n = Math.min(h.field_7761.size(), 54);

      for (int i = 0; i < n; i++) {
         p = p * 31L + class_1799.method_57355(h.method_7611(i).method_7677());
      }

      return p;
   }

   private static void klik(class_634 net, class_1703 h, int przycisk) {
      klikSlot(net, h, 0, przycisk);
   }

   private static void klikSlot(class_634 net, class_1703 h, int slot, int przycisk) {
      aktualizacje = 0;
      zmiana = false;
      syncPrzy = h.field_7763;
      podpisPrzy = podpis(h);
      wyslano = System.currentTimeMillis();
      net.method_52787(new class_2813(h.field_7763, h.method_37421(), slot, przycisk, class_1713.field_7790, class_1799.field_8037, new Int2ObjectOpenHashMap()));
      kliki++;
   }

   private static void krok(class_310 mc) {
      if (watek != null) {
         class_746 gracz = mc.field_1724;
         class_634 net = mc.method_1562();
         if (gracz != null && net != null && rynek(mc.field_1755)) {
            if (!Snajper.zajety() && !Wystawiacz.wTrakcie() && !Sortowanie.naprawia(mc)) {
               class_1703 h = gracz.field_7512;
               if (h.field_7763 != 0) {
                  long teraz = System.currentTimeMillis();
                  if (etap != 0) {
                     if (zmiana) {
                        ping = (ping * 3L + ostatnia - wyslano) / 4L;
                        odstep = Math.max(0L, odstep - 2L);
                     } else {
                        odstep = Math.min(150L, odstep + 10L);
                     }
                  }

                  boolean nawigacja = Kalibracja.gotowa();
                  if (etap != 0 && (nawigacja || etap == 2)) {
                     Skaner.skanuj(mc, true);
                     Snajper.przeglad(mc);
                     if (Snajper.zajety()) {
                        etap = 0;
                        return;
                     }
                  }

                  if (nawigacja) {
                     int nast = Kalibracja.slotNast(h);
                     int poprz = Kalibracja.slotPoprz(h);
                     boolean wPrzod = nast >= 0 && (!ostatniaNast || poprz < 0);
                     int cel = wPrzod ? nast : poprz;
                     if (cel < 0) {
                        etap = 0;
                        return;
                     }

                     if (!wPrzod) {
                        cykle++;
                        cykleOkno++;
                        czasCyklu = teraz - startCyklu;
                        startCyklu = teraz;
                        if (teraz - oknoOd >= 1000L) {
                           naSekunde = cykleOkno;
                           cykleOkno = 0;
                           oknoOd = teraz;
                        }
                     }

                     ostatniaNast = wPrzod;
                     klikSlot(net, h, cel, 0);
                     Statystyki.zmianaStrony();
                     if (wPrzod) {
                        lewe++;
                     } else {
                        prawe++;
                     }

                     etap = 1;
                     return;
                  }

                  if (etap == 1) {
                     klik(net, h, 1);
                     prawe++;
                     etap = 2;
                  } else {
                     if (etap == 2) {
                        czasCyklu = teraz - startCyklu;
                        cykle++;
                        cykleOkno++;
                        if (teraz - oknoOd >= 1000L) {
                           naSekunde = cykleOkno;
                           cykleOkno = 0;
                           oknoOd = teraz;
                        }
                     }

                     startCyklu = teraz;
                     klik(net, h, 0);
                     lewe++;
                     etap = 1;
                  }
               }
            } else {
               etap = 0;
            }
         }
      }
   }

   public static void stop() {
      watek = null;
   }
}
