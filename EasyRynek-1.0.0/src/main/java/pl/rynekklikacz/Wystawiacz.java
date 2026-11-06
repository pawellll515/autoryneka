package pl.rynekklikacz;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1109;
import net.minecraft.class_1661;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2813;
import net.minecraft.class_2868;
import net.minecraft.class_310;
import net.minecraft.class_3417;
import net.minecraft.class_465;
import net.minecraft.class_634;
import pl.rynekklikacz.gui.BazaScreen;
import pl.rynekklikacz.gui.ZakupToast;

public final class Wystawiacz {
   private static final long WAZNOSC = 172800000L;
   private static final long TRZYMAJ = 500L;
   private static final long TRZYMAJ_AUTO = 250L;
   private static final int ZIELONY = -10755185;
   private static final int ZLOTY = -865972;
   private static long ostatniePrzeniesienie;
   private static class_1799 trzymany = class_1799.field_8037;
   private static int przywroc = -1;
   private static int powrotEtap;
   private static long powrotT;
   private static long powrotOkno;
   private static int powrotKliki;
   public static final int SLOT_POWROT = 53;
   private static long trzymanyOd;
   public static String status = "";

   private Wystawiacz() {
   }

   public static List<Zakupy.Zakup> czekajace() {
      long teraz = System.currentTimeMillis();
      List<Zakupy.Zakup> l = new ArrayList<>();

      for (Zakupy.Zakup z : Zakupy.wszystkie()) {
         if (!z.wystawiono && teraz - z.czas < 172800000L) {
            l.add(z);
         }
      }

      return l;
   }

   public static boolean wTrakcie() {
      return powrotEtap != 0;
   }

   private static void klikPowrot(class_310 mc) {
      class_1703 h = mc.field_1724.field_7512;
      class_634 net = mc.method_1562();
      if (net != null && h.field_7763 != 0 && 53 < h.field_7761.size()) {
         net.method_52787(new class_2813(h.field_7763, h.method_37421(), 53, 0, class_1713.field_7790, class_1799.field_8037, new Int2ObjectOpenHashMap()));
         powrotKliki++;
         powrotT = System.currentTimeMillis();
         status = "Wracam na rynek · klik " + powrotKliki + "/3 w slot 53";
      }
   }

   private static void powrot(class_310 mc) {
      if (powrotEtap != 0) {
         long t = System.currentTimeMillis();
         class_634 net = mc.method_1562();
         if (net == null) {
            powrotEtap = 0;
         } else if (powrotEtap == 1) {
            if (t - powrotT < 5000L) {
               status = "Wracam na rynek za " + (5L - (t - powrotT) / 1000L) + " s";
            } else {
               net.method_45730("ah");
               powrotEtap = 2;
               powrotT = t;
               powrotOkno = 0L;
               powrotKliki = 0;
               status = "Wracam na rynek: /ah";
            }
         } else if (powrotEtap != 2) {
            if (t - powrotT >= 500L) {
               if (powrotKliki < 3) {
                  klikPowrot(mc);
               } else {
                  powrotEtap = 0;
                  status = "Z powrotem na rynku · Start";
                  if (!Klikacz.dziala()) {
                     Klikacz.start();
                  }
               }
            }
         } else {
            boolean okno = mc.field_1755 instanceof class_465 && mc.field_1724.field_7512.field_7763 != 0;
            if (okno) {
               if (powrotOkno == 0L) {
                  powrotOkno = t;
               }

               if (t - powrotOkno >= 300L) {
                  klikPowrot(mc);
                  powrotEtap = 3;
               }
            } else if (t - powrotT > 6000L) {
               status = "/ah nie otworzyło okna";
               powrotEtap = 0;
            }
         }
      }
   }

   public static void tick(class_310 mc) {
      if (mc.field_1724 != null) {
         powrot(mc);
         if (Ustawienia.autoWystaw()) {
            List<Zakupy.Zakup> czekajace = czekajace();
            if (czekajace.isEmpty()) {
               trzymany = class_1799.field_8037;
            } else {
               przenies(mc, czekajace);
               wystaw(mc, czekajace);
            }
         }
      }
   }

   private static void przenies(class_310 mc, List<Zakupy.Zakup> czekajace) {
      class_1661 inv = mc.field_1724.method_31548();
      if (mc.field_1755 == null) {
         for (Zakupy.Zakup z : czekajace) {
            for (int i = 0; i < 9 && (!z.naPasku || z.auto); i++) {
               class_1799 s = (class_1799)inv.field_7547.get(i);
               if (!s.method_7960() && Skaner.grupa(s).equals(z.grupa)) {
                  if (!z.naPasku) {
                     z.naPasku = true;
                     Zakupy.zmieniono();
                     if (!z.auto) {
                        status = "Na pasku: weź do ręki, żeby wystawić";
                        mc.method_1566()
                           .method_1999(
                              new ZakupToast(s.method_7972(), "Gotowe do wystawienia", z.nazwa + " jest na pasku", "weź do ręki → /wystaw", -865972, -7629657)
                           );
                     }
                  }

                  if (z.auto && inv.field_7545 != i) {
                     if (przywroc < 0) {
                        przywroc = inv.field_7545;
                     }

                     inv.method_61496(i);
                     class_634 net = mc.method_1562();
                     if (net != null) {
                        net.method_52787(new class_2868(i));
                     }

                     status = "Biorę " + z.nazwa + " do ręki";
                  }
                  break;
               }
            }

            if (!z.naPasku) {
               long teraz = System.currentTimeMillis();
               if (teraz - ostatniePrzeniesienie < 500L) {
                  return;
               }

               for (int i = 9; i < 36; i++) {
                  class_1799 s = (class_1799)inv.field_7547.get(i);
                  if (!s.method_7960() && Skaner.grupa(s).equals(z.grupa)) {
                     int wolny = -1;

                     for (int h = 0; h < 9; h++) {
                        if (((class_1799)inv.field_7547.get(h)).method_7960()) {
                           wolny = h;
                           break;
                        }
                     }

                     if (wolny < 0) {
                        status = "Brak wolnego miejsca na pasku";
                        return;
                     }

                     class_1703 hd = mc.field_1724.field_7512;

                     for (class_1735 sl : hd.field_7761) {
                        if (sl.field_7871 == inv && sl.method_34266() == i) {
                           class_634 net = mc.method_1562();
                           if (net != null) {
                              net.method_52787(
                                 new class_2813(
                                    hd.field_7763,
                                    hd.method_37421(),
                                    sl.field_7874,
                                    wolny,
                                    class_1713.field_7791,
                                    class_1799.field_8037,
                                    new Int2ObjectOpenHashMap()
                                 )
                              );
                           }

                           ostatniePrzeniesienie = teraz;
                           status = "Przenoszę " + z.nazwa + " na pasek";
                           return;
                        }
                     }

                     return;
                  }
               }
            }
         }
      }
   }

   private static void wystaw(class_310 mc, List<Zakupy.Zakup> czekajace) {
      class_1799 reka = mc.field_1724.method_6047();
      if (mc.field_1755 == null && !reka.method_7960()) {
         String grupa = Skaner.grupa(reka);
         List<Zakupy.Zakup> pasujace = new ArrayList<>();

         for (Zakupy.Zakup z : czekajace) {
            if (z.grupa.equals(grupa)) {
               pasujace.add(z);
            }
         }

         if (pasujace.isEmpty()) {
            trzymany = class_1799.field_8037;
         } else {
            long teraz = System.currentTimeMillis();
            if (!class_1799.method_7973(trzymany, reka)) {
               trzymany = reka.method_7972();
               trzymanyOd = teraz;
            } else {
               boolean auto = pasujace.stream().anyMatch(zx -> zx.auto);
               if (teraz - trzymanyOd >= (auto ? 250L : 500L)) {
                  trzymany = class_1799.field_8037;
                  int ilosc = reka.method_7947();
                  List<Zakupy.Zakup> uzyte = new ArrayList<>();
                  int suma = 0;
                  double koszt = 0.0;

                  for (Zakupy.Zakup z : pasujace) {
                     if (!uzyte.isEmpty() && suma + z.ilosc > ilosc) {
                        break;
                     }

                     uzyte.add(z);
                     suma += z.ilosc;
                     koszt += z.cena;
                  }

                  double kosztSzt = koszt / Math.max(1, suma);
                  double kosztCalosci = kosztSzt * ilosc;
                  long bezStraty = (long)Math.ceil(kosztCalosci / 0.97) + 1L;
                  Analiza an = Snajper.analizaDowolna(grupa);
                  double rynek = an != null && an.rynkowa > 0.0 ? an.rynkowa : uzyte.get(0).rynkowa;
                  long cel;
                  if (rynek <= 0.0) {
                     cel = (long)Math.ceil(bezStraty * 1.2);
                  } else if (rynek >= 100.0) {
                     cel = (long)((Math.floor(rynek) - 1.0) * ilosc);
                  } else {
                     cel = (long)Math.floor(rynek * ilosc) - 1L;
                  }

                  boolean podniesiona = cel < bezStraty;
                  long cena = Math.max(cel, bezStraty);
                  class_634 net = mc.method_1562();
                  if (net != null) {
                     net.method_45730("wystaw " + ilosc + " " + cena);
                     if (auto && przywroc >= 0 && przywroc != mc.field_1724.method_31548().field_7545) {
                        mc.field_1724.method_31548().method_61496(przywroc);
                        net.method_52787(new class_2868(przywroc));
                     }

                     przywroc = -1;
                     if (auto && Ustawienia.autoPowrot()) {
                        powrotEtap = 1;
                        powrotT = teraz;
                     }

                     for (Zakupy.Zakup z : uzyte) {
                        z.wystawiono = true;
                        z.cenaWystawienia = cena;
                        z.iloscWystawienia = ilosc;
                        z.czasWystawienia = teraz;
                     }

                     Zakupy.zmieniono();
                     double zysk = cena * 0.97 - kosztCalosci;
                     status = "Wystawiono " + uzyte.get(0).nazwa + " za " + BazaScreen.cenaD(cena);
                     mc.method_1566()
                        .method_1999(
                           new ZakupToast(
                              reka.method_7972(),
                              "Wystawiono!",
                              uzyte.get(0).nazwa + " · " + BazaScreen.cenaD(cena) + (ilosc > 1 ? " (" + ilosc + " szt.)" : ""),
                              "zysk po 3%: +" + BazaScreen.cenaD(zysk) + (podniesiona ? " · cena bez straty" : " · rynek " + BazaScreen.krotkoD(rynek)),
                              -10755185,
                              podniesiona ? -865972 : -10755185
                           )
                        );
                     mc.method_1483().method_4873(class_1109.method_4758(class_3417.field_14627, 1.2F));
                  }
               }
            }
         }
      } else {
         trzymany = class_1799.field_8037;
      }
   }
}
